package com.stratagile.pnrouter.ui.activity.login

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.hardware.fingerprint.FingerprintManager
import android.os.*
import android.provider.Settings
import android.support.v7.app.AlertDialog
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.pawegio.kandroid.toast
import com.socks.library.KLog
import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.constant.UserDataManger
import com.stratagile.pnrouter.data.web.PNRouterServiceMessageReceiver
import com.stratagile.pnrouter.db.RouterEntity
import com.stratagile.pnrouter.db.UserEntity
import com.stratagile.pnrouter.entity.BaseData
import com.stratagile.pnrouter.entity.JLoginRsp
import com.stratagile.pnrouter.entity.LoginReq
import com.stratagile.pnrouter.entity.MyRouter
import com.stratagile.pnrouter.entity.events.ConnectStatus
import com.stratagile.pnrouter.fingerprint.CryptoObjectHelper
import com.stratagile.pnrouter.fingerprint.MyAuthCallback
import com.stratagile.pnrouter.fingerprint.MyAuthCallback.*
import com.stratagile.pnrouter.ui.activity.login.component.DaggerLoginActivityComponent
import com.stratagile.pnrouter.ui.activity.login.contract.LoginActivityContract
import com.stratagile.pnrouter.ui.activity.login.module.LoginActivityModule
import com.stratagile.pnrouter.ui.activity.login.presenter.LoginActivityPresenter
import com.stratagile.pnrouter.ui.activity.main.LogActivity
import com.stratagile.pnrouter.ui.activity.main.MainActivity
import com.stratagile.pnrouter.ui.activity.scan.ScanQrCodeActivity
import com.stratagile.pnrouter.utils.FileUtil
import com.stratagile.pnrouter.utils.LocalRouterUtils
import com.stratagile.pnrouter.utils.PopWindowUtil
import com.stratagile.pnrouter.utils.SpUtil
import com.stratagile.pnrouter.view.CustomPopWindow
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject


/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.login
 * @Description: $description
 * @date 2018/09/10 15:05:29
 */

class LoginActivityActivity : BaseActivity(), LoginActivityContract.View, PNRouterServiceMessageReceiver.LoginMessageCallback {
    val REQUEST_SELECT_ROUTER = 2
    val REQUEST_SCAN_QRCODE = 1
    var loginBack = false
    override fun loginBack(loginRsp: JLoginRsp) {
        KLog.i(loginRsp.toString())
        standaloneCoroutine.cancel()
        if (loginRsp.params.retCode != 0) {
            if (loginRsp.params.retCode == 3) {
                runOnUiThread {
                    toast("The current service is not available.")
                    closeProgressDialog()
                }
            }
            if (loginRsp.params.retCode == 2) {
                runOnUiThread {
                    toast("Too many users")
                    closeProgressDialog()
                }
            }
            if (loginRsp.params.retCode == 1) {
                runOnUiThread {
                    toast("RouterId Error")
                    closeProgressDialog()
                }
            }
            if (loginRsp.params.retCode == 4) {
                runOnUiThread {
                    toast("System Error")
                    closeProgressDialog()
                }
            }
            return
        }
        if ("".equals(loginRsp.params.userId)) {
            runOnUiThread {
                toast("Too many users")
                closeProgressDialog()
            }
        } else {
            FileUtil.saveUserId2Local(loginRsp.params!!.userId)
            KLog.i("服务器返回的userId：${loginRsp.params!!.userId}")
            newRouterEntity.userId = ""
            SpUtil.putString(this, ConstantValue.userId, loginRsp.params!!.userId)
            SpUtil.putString(this, ConstantValue.username, userName.text.toString())
            SpUtil.putString(this, ConstantValue.routerId, routerId)
            var routerList = AppConfig.instance.mDaoMaster!!.newSession().routerEntityDao.loadAll()
            newRouterEntity.routerId = routerId
            newRouterEntity.routerName = "Router " + (routerList.size + 1)
            newRouterEntity.username = userName.text.toString()
            newRouterEntity.lastCheck = true
            var myUserData = UserEntity()
            myUserData.userId = loginRsp.params!!.userId
            myUserData.nickName = newRouterEntity.username;
            UserDataManger.myUserData = myUserData
            var contains = false
            for (i in routerList) {
                if (i.routerId.equals(routerId)) {
                    contains = true
                    break
                }
            }

            if (contains) {
                KLog.i("数据局中已经包含了这个routerId")
            } else {
                routerList.forEach {
                    it.lastCheck = false
                }
                AppConfig.instance.mDaoMaster!!.newSession().routerEntityDao.updateInTx(routerList)
                AppConfig.instance.mDaoMaster!!.newSession().routerEntityDao.insert(newRouterEntity)
                //更新sd卡路由器数据begin
                val myRouter = MyRouter()
                myRouter.setType(0)
                myRouter.setRouterEntity(newRouterEntity)
                LocalRouterUtils.insertLocalAssets(myRouter)
            }
            runOnUiThread {
                closeProgressDialog()
            }
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }


    @Inject
    internal lateinit var mPresenter: LoginActivityPresenter

    private var myAuthCallback: MyAuthCallback? = null
    private var cancellationSignal: CancellationSignal? = null

    private var handler: Handler? = null

    private var builderTips: AlertDialog? = null

    internal var finger: ImageView? = null

    var newRouterEntity = RouterEntity()
    private lateinit var standaloneCoroutine : Job

    var routerId = ""
    var userId = ""
    var username = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        needFront = true
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        setContentView(R.layout.activity_login)
//        CrashReport.testNativeCrash()
    }

    override fun onDestroy() {
        AppConfig.instance.messageReceiver!!.loginBackListener = null
        if (cancellationSignal != null) {
            cancellationSignal!!.cancel()
            cancellationSignal = null
        }
        if (builderTips != null) {
            builderTips?.dismiss()
            builderTips = null
        }
        if (myAuthCallback != null) {
            myAuthCallback?.removeHandle()
            myAuthCallback = null
        }
        if (handler != null) {
            handler?.removeCallbacksAndMessages(null)
        }
        if (handler != null) {
            handler = null
        }
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onWebSocketConnected(connectStatus: ConnectStatus) {
        if (connectStatus.status == 0) {
            loginBack = false
            closeProgressDialog()
            showProgressDialog("login...")
            standaloneCoroutine = launch(CommonPool) {
                delay(10000)
                if (!loginBack) {
                    runOnUiThread {
                        closeProgressDialog()
                        toast("login time out")
                    }
                }
            }
//            standaloneCoroutine.cancel()
            var login = LoginReq( routerId, userId, 0)
            AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(login))
        }
    }

    override fun initData() {
        userId = FileUtil.getLocalUserId()
        EventBus.getDefault().register(this)
        loginBtn.setOnClickListener {
            KLog.i("用来验证的routerId：${routerId}")
            if (userName.text.toString().equals("")) {
                toast(getString(R.string.please_type_your_username))
                return@setOnClickListener
            }
            if (routerId.equals("")) {
                return@setOnClickListener
            }
            if (intent.hasExtra("flag")) {
                AppConfig.instance.getPNRouterServiceMessageReceiver().reConnect()
                AppConfig.instance.messageReceiver!!.loginBackListener = this
                showProgressDialog("connecting...")
//                onWebSocketConnected(ConnectStatus(0))
            } else {
                AppConfig.instance.getPNRouterServiceMessageReceiver(true)
                AppConfig.instance.messageReceiver!!.loginBackListener = this
                showProgressDialog("connecting...")
            }
//            mThread = CustomThread(routerId, userId)
//            mThread!!.start()
        }
        scanIcon.setOnClickListener {
            mPresenter.getScanPermission()
        }
        miniScanIcon.setOnClickListener {
            mPresenter.getScanPermission()
        }

        viewLog.setOnClickListener {
            startActivity(Intent(this, LogActivity::class.java))
        }

        handler = object : Handler() {
            override fun handleMessage(msg: Message) {
                super.handleMessage(msg)
                when (msg.what) {
                    MSG_AUTH_SUCCESS -> {
                        setResultInfo(R.string.fingerprint_success)
                        cancellationSignal = null
                    }
                    MSG_AUTH_FAILED -> {
                        setResultInfo(R.string.fingerprint_not_recognized)
                        cancellationSignal = null
                    }
                    MSG_AUTH_ERROR -> handleErrorCode(msg.arg1)
                    MSG_AUTH_HELP -> handleHelpCode(msg.arg1)
                }
            }
        }
        var routerList = AppConfig.instance.mDaoMaster!!.newSession().routerEntityDao.loadAll()
        if (routerList.size != 0) {
            var hasCheckedRouter = false
            run breaking@ {
                routerList.forEach {
                    if (it.lastCheck) {
                        routerId = it.routerId
                        userId = FileUtil.getLocalUserId()
                        username = it.username
                        routerName.text = it.routerName
                        hasCheckedRouter = true
                        return@breaking
                    }
                }
            }
            if (!hasCheckedRouter) {
                routerId = routerList[0].routerId
                userId = FileUtil.getLocalUserId()
                username = routerList[0].username
                routerName.text = routerList[0].routerName
            }
            if (!username.equals("")) {
                userName.isEnabled = false
                userName.setText(username)
            }
            hasRouterParent.visibility = View.VISIBLE
            scanParent.visibility = View.INVISIBLE
            noRoutergroup.visibility = View.INVISIBLE
            miniScanParent.visibility = View.VISIBLE
        } else {
            scanParent.visibility = View.VISIBLE
            noRoutergroup.visibility = View.VISIBLE
            miniScanParent.visibility = View.INVISIBLE
            hasRouterParent.visibility = View.INVISIBLE
        }
        if (routerList.size > 0) {
            routerName.setOnClickListener { view1 ->
                PopWindowUtil.showSelectRouterPopWindow(this, routerName, object : PopWindowUtil.OnRouterSelectListener{
                    override fun onSelect(position: Int) {
                        routerList.forEach {
                            if(it.lastCheck) {
                                it.lastCheck = false
                                AppConfig.instance.mDaoMaster!!.newSession().routerEntityDao.update(it)
                            }
                        }
                        routerId = routerList[position].routerId
                        userId = FileUtil.getLocalUserId()
                        username = routerList[position].username
                        routerName.text = routerList[position].routerName
                        routerList[position].lastCheck = true
                        AppConfig.instance.mDaoMaster!!.newSession().routerEntityDao.update(routerList[position])
                        if (!username.equals("")) {
                            userName.isEnabled = false
                            userName.setText(username)
                        }
                    }
                })
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && SpUtil.getBoolean(this, ConstantValue.fingerprintUnLock, true)) {
            // init fingerprint.
            try {
                val fingerprintManager = AppConfig.instance.getSystemService(Context.FINGERPRINT_SERVICE) as FingerprintManager
                /*if(!SpUtil.getString(this, ConstantValue.fingerPassWord, "").equals(""))
                {*/
                if (fingerprintManager != null && fingerprintManager.isHardwareDetected && fingerprintManager.hasEnrolledFingerprints()) {
                    try {
                        myAuthCallback = MyAuthCallback(handler)
                        val cryptoObjectHelper = CryptoObjectHelper()
                        if (cancellationSignal == null) {
                            cancellationSignal = CancellationSignal()
                        }
                        fingerprintManager.authenticate(cryptoObjectHelper.buildCryptoObject(), cancellationSignal, 0,
                                myAuthCallback, null)
                        val builder = AlertDialog.Builder(this)
                        val view = View.inflate(this, R.layout.finger_dialog_layout, null)
                        builder.setView(view)
                        builder.setCancelable(false)
                        val tvContent = view.findViewById<View>(R.id.tv_content) as TextView//输入内容

                        val btn_cancel = view.findViewById<View>(R.id.btn_right) as Button//确定按钮

                        btn_cancel.visibility = View.VISIBLE
                        btn_cancel.setOnClickListener {
                            builderTips?.dismiss()
                            if (cancellationSignal != null) {
                                cancellationSignal?.cancel()
                                cancellationSignal = null
                            }
                            finish();
                            android.os.Process.killProcess(android.os.Process.myPid())
                            System.exit(0)
                        }
                        finger = view.findViewById<View>(R.id.finger) as ImageView
                        tvContent.setText(R.string.choose_finger_dialog_title)
                        val currentContext = this
                        builderTips = builder.create()
                        builderTips?.show()
                    } catch (e: Exception) {
                        try {
                            myAuthCallback = MyAuthCallback(handler)
                            val cryptoObjectHelper = CryptoObjectHelper()
                            if (cancellationSignal == null) {
                                cancellationSignal = CancellationSignal()
                            }
                            fingerprintManager.authenticate(cryptoObjectHelper.buildCryptoObject(), cancellationSignal, 0,
                                    myAuthCallback, null)
                            /* fingerprintManager.authenticate(cryptoObjectHelper.buildCryptoObject(), 0,
                                cancellationSignal, myAuthCallback, null);*/
                            val builder = AlertDialog.Builder(this)
                            val view = View.inflate(this, R.layout.finger_dialog_layout, null)
                            builder.setView(view)
                            builder.setCancelable(false)
                            val tvContent = view.findViewById<View>(R.id.tv_content) as TextView//输入内容
                            val btn_comfirm = view.findViewById<View>(R.id.btn_right) as Button//
                            btn_comfirm.setText(R.string.cancel_btn_dialog)
                            tvContent.setText(R.string.choose_finger_dialog_title)
                            val currentContext = this
                            builderTips = builder.create()
                            builderTips?.show()
                        } catch (er: Exception) {
                            er.printStackTrace()
                            builderTips?.dismiss()
                            toast(R.string.Fingerprint_init_failed_Try_again)
                        }

                    }

                } else {
                    SpUtil.putString(this, ConstantValue.fingerPassWord, "")
                    val dialog = AlertDialog.Builder(this)
                    dialog.setMessage(R.string.No_fingerprints_do_you_want_to_set_them_up)
                    dialog.setCancelable(false)

                    dialog.setPositiveButton(android.R.string.ok
                    ) { dialog, which ->
                        val intent = Intent(Settings.ACTION_SETTINGS)
                        startActivity(intent)
                        finish();
                        android.os.Process.killProcess(android.os.Process.myPid())
                        System.exit(0)
                    }
                    dialog.setNegativeButton(android.R.string.cancel
                    ) { dialog, which ->
                        finish();
                        android.os.Process.killProcess(android.os.Process.myPid())
                        System.exit(0)
                    }
                    dialog.create().show()
                }

            } catch (e: Exception) {
                SpUtil.putString(this, ConstantValue.fingerPassWord, "")
            }

        } else {
            SpUtil.putString(this, ConstantValue.fingerPassWord, "")
        }
    }

    override fun getScanPermissionSuccess() {
        val intent1 = Intent(this, ScanQrCodeActivity::class.java)
        startActivityForResult(intent1, REQUEST_SCAN_QRCODE)
    }

    private fun handleErrorCode(code: Int) {
        when (code) {
            //case FingerprintManager.FINGERPRINT_ERROR_CANCELED:
            FingerprintManager.FINGERPRINT_ERROR_HW_UNAVAILABLE, FingerprintManager.FINGERPRINT_ERROR_LOCKOUT, FingerprintManager.FINGERPRINT_ERROR_NO_SPACE, FingerprintManager.FINGERPRINT_ERROR_TIMEOUT, FingerprintManager.FINGERPRINT_ERROR_UNABLE_TO_PROCESS -> {
                setResultInfo(R.string.ErrorHwUnavailable_warning)
            }
        }
    }

    private fun handleHelpCode(code: Int) {
        when (code) {
            FingerprintManager.FINGERPRINT_ACQUIRED_GOOD, FingerprintManager.FINGERPRINT_ACQUIRED_IMAGER_DIRTY, FingerprintManager.FINGERPRINT_ACQUIRED_INSUFFICIENT, FingerprintManager.FINGERPRINT_ACQUIRED_PARTIAL, FingerprintManager.FINGERPRINT_ACQUIRED_TOO_FAST, FingerprintManager.FINGERPRINT_ACQUIRED_TOO_SLOW -> setResultInfo(R.string.AcquiredToSlow_warning)
        }
    }

    private fun setResultInfo(stringId: Int) {
        if (stringId == R.string.fingerprint_success) {
            finger?.setImageDrawable(resources.getDrawable(R.mipmap.icon_fingerprint_complete))
            setResult(RESULT_OK, intent)
            SpUtil.putString(this, ConstantValue.fingerPassWord, "888888")
            builderTips?.dismiss()
        } else {
            toast(stringId)
        }
    }

    override fun setupActivityComponent() {
        DaggerLoginActivityComponent
                .builder()
                .appComponent((application as AppConfig).applicationComponent)
                .loginActivityModule(LoginActivityModule(this))
                .build()
                .inject(this)
    }

    override fun setPresenter(presenter: LoginActivityContract.LoginActivityContractPresenter) {
        mPresenter = presenter as LoginActivityPresenter
    }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }

    override fun onBackPressed() {
        if (CustomPopWindow.onBackPressed()) {

        } else {
            super.onBackPressed()
        }
    }

   override  fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_SCAN_QRCODE && resultCode == Activity.RESULT_OK) {
            hasRouterParent.visibility = View.VISIBLE
            miniScanParent.visibility = View.VISIBLE
            scanParent.visibility = View.INVISIBLE
            noRoutergroup.visibility = View.INVISIBLE
            var routerList = AppConfig.instance.mDaoMaster!!.newSession().routerEntityDao.loadAll()
            if (routerList != null && routerList.size != 0) {
                routerList.forEach { itt ->
                    if (itt.routerId.equals(data!!.getStringExtra("result"))) {
                        routerName?.setText(itt.routerName)
                        routerId = data!!.getStringExtra("result")
                        routerList.forEach {
                            if (it.lastCheck) {
                                it.lastCheck = false
                                AppConfig.instance.mDaoMaster!!.newSession().routerEntityDao.update(it)
                            }
                        }
                        itt.lastCheck = true
                        AppConfig.instance.mDaoMaster!!.newSession().routerEntityDao.update(itt)
                        return
                    }
                }
                routerId = data!!.getStringExtra("result")
                routerName.text = "Router " + (routerList.size + 1)
                newRouterEntity.routerId = data!!.getStringExtra("result")
                return
            }
            return
        }
    }
}