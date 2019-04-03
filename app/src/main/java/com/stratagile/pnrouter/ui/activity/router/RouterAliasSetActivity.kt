package com.stratagile.pnrouter.ui.activity.router

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.TextView
import chat.tox.antox.tox.MessageHelper
import chat.tox.antox.wrapper.FriendKey
import com.pawegio.kandroid.startActivity
import com.pawegio.kandroid.toast
import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.data.web.PNRouterServiceMessageReceiver
import com.stratagile.pnrouter.entity.BaseData
import com.stratagile.pnrouter.entity.JResetRouterNameRsp
import com.stratagile.pnrouter.entity.ResetRouterNameReq
import com.stratagile.pnrouter.ui.activity.admin.AdminLoginSuccessActivity
import com.stratagile.pnrouter.ui.activity.login.LoginActivityActivity
import com.stratagile.pnrouter.ui.activity.router.component.DaggerRouterAliasSetComponent
import com.stratagile.pnrouter.ui.activity.router.contract.RouterAliasSetContract
import com.stratagile.pnrouter.ui.activity.router.module.RouterAliasSetModule
import com.stratagile.pnrouter.ui.activity.router.presenter.RouterAliasSetPresenter
import com.stratagile.pnrouter.utils.RxEncodeTool
import com.stratagile.pnrouter.utils.baseDataToJson
import com.stratagile.pnrouter.view.CommonDialog
import com.stratagile.pnrouter.view.SweetAlertDialog
import com.stratagile.tox.toxcore.KotlinToxService
import com.stratagile.tox.toxcore.ToxCoreJni
import im.tox.tox4j.core.enums.ToxMessageType
import kotlinx.android.synthetic.main.activity_routeraliasset.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import javax.inject.Inject

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.router
 * @Description: $description
 * @date 2019/02/21 11:00:31
 */

class RouterAliasSetActivity : BaseActivity(), RouterAliasSetContract.View, PNRouterServiceMessageReceiver.ResetRouterNameCallBack {
    override fun ResetRouterName(jResetRouterNameRsp: JResetRouterNameRsp) {

        runOnUiThread {
            closeProgressDialog()
        }
        isBack = true
        if(standaloneCoroutine != null)
            standaloneCoroutine.cancel()
        if(jResetRouterNameRsp.params.retCode == 0)
        {
            runOnUiThread {
                if(flag == 0)
                {
                    var intent = Intent(this, AdminLoginSuccessActivity::class.java)
                    intent.putExtra("adminRouterId",intent.getStringExtra("adminRouterId"))
                    intent.putExtra("adminUserSn",intent.getStringExtra("adminUserSn"))
                    intent.putExtra("adminIdentifyCode",intent.getStringExtra("adminIdentifyCode"))
                    intent.putExtra("adminQrcode",intent.getStringExtra("adminQrcode"))
                    intent.putExtra("routerName",routerName.text.toString())
                    startActivity(intent)
                }else{
                    var intent = Intent()
                    intent.putExtra("routerName", routerName.text.toString())
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }

            }
        }else  if(jResetRouterNameRsp.params.retCode == 1)
        {
            runOnUiThread {
                toast("No authority")
            }
        }else{
            runOnUiThread {
                toast("Other mistakes")
            }
        }

    }

    @Inject
    internal lateinit var mPresenter: RouterAliasSetPresenter
    private lateinit var standaloneCoroutine : Job
    private var isBack:Boolean = false
    var flag = 0;
    override fun onCreate(savedInstanceState: Bundle?) {
        needFront = true
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        setContentView(R.layout.activity_routeraliasset)
        showViewNeedFront()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE//设置状态栏黑色字体
        }
    }
    override fun initData() {
        flag = intent.getIntExtra("flag",0);
        var routerNameFrom = intent.getStringExtra("routerName")
        ivAvatar.withShape = true
        ivAvatar.setTextWithShape(routerNameFrom)
        LoginInBtn.setOnClickListener {
            if(routerName.text.toString().equals(""))
            {
                toast(getString(R.string.Cannot_be_empty))
                return@setOnClickListener
            }
            isBack = false
            standaloneCoroutine = launch(CommonPool) {
                delay(3000)
                if(!isBack) {
                    runOnUiThread {
                        showLeaveGroupDialog()
                    }

                    //showProgressDialog(getString(R.string.The_server_did_not_respond))
                }
            }
            AppConfig.instance.messageReceiver!!.resetRouterNameCallBack = this
            var routerName =  RxEncodeTool.base64Encode2String(routerName.text.toString().toByteArray())
            showProgressDialog("waiting...")
            var resetRouterNameReq = ResetRouterNameReq(intent.getStringExtra("adminRouterId"),routerName)
            if(ConstantValue.isWebsocketConnected)
            {
                AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(4,resetRouterNameReq))
            }else if (ConstantValue.isToxConnected) {
                var baseData = BaseData(4,resetRouterNameReq)
                var baseDataJson = baseData.baseDataToJson().replace("\\", "")
                if (ConstantValue.isAntox) {
                    var friendKey: FriendKey = FriendKey(ConstantValue.currentRouterId.substring(0, 64))
                    MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL)
                }else{
                    ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.currentRouterId.substring(0, 64))
                }
            }

        }
        routerName.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (!"".equals(p0?.toString())) {
                    llNext.background = resources.getDrawable(R.drawable.btn_maincolor)
                } else {
                    llNext.background = resources.getDrawable(R.drawable.btn_d5d5d5)
                }
            }

        })
    }

    var hasBeenFormatDialog : CommonDialog? = null
    fun showLeaveGroupDialog() {
        val view = View.inflate(this, R.layout.layout_two_check_notitle_with_content_ok, null)
        //取消或确定按钮监听事件处l
        hasBeenFormatDialog = CommonDialog(this)
        hasBeenFormatDialog?.setCancelable(true)
        val window = hasBeenFormatDialog?.window
        val tvContent = view.findViewById<TextView>(R.id.tvContent)
        val tvLeft = view.findViewById<TextView>(R.id.tvLeft)
        tvLeft.setOnClickListener {
            hasBeenFormatDialog?.dismissWithAnimation()
            var intent = Intent(this, LoginActivityActivity::class.java)
            startActivity(intent)
            finish()
        }
        tvContent.text = getString(R.string.The_server_did_not_respond)
        window?.setBackgroundDrawableResource(android.R.color.transparent)
        hasBeenFormatDialog?.setView(view)
        hasBeenFormatDialog?.show()
    }
    override fun setupActivityComponent() {
        DaggerRouterAliasSetComponent
                .builder()
                .appComponent((application as AppConfig).applicationComponent)
                .routerAliasSetModule(RouterAliasSetModule(this))
                .build()
                .inject(this)
    }
    override fun setPresenter(presenter: RouterAliasSetContract.RouterAliasSetContractPresenter) {
        mPresenter = presenter as RouterAliasSetPresenter
    }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }
    override fun onDestroy() {
        try {
            AppConfig.instance.messageReceiver!!.resetRouterNameCallBack = null
        } catch (e : Exception) {
            e.printStackTrace()
        }
        super.onDestroy()
    }
}