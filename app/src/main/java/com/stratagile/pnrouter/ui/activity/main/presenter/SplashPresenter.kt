package com.stratagile.pnrouter.ui.activity.main.presenter

import android.Manifest
import android.app.Activity
import android.os.Environment
import android.util.Log
import chat.tox.antox.toxme.ToxData
import chat.tox.antox.utils.CreateUserUtils
import chat.tox.antox.wrapper.ToxAddress
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hyphenate.easeui.utils.PathUtils
import com.pawegio.kandroid.toast
import com.socks.library.KLog
import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.entity.CryptoBoxKeypair
import com.stratagile.pnrouter.entity.MyFile
import com.stratagile.pnrouter.entity.RSAData
import com.stratagile.pnrouter.ui.activity.main.contract.SplashContract
import com.stratagile.pnrouter.utils.*
import com.yanzhenjie.permission.AndPermission
import com.yanzhenjie.permission.PermissionListener
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_edit_nick_name.*
import org.libsodium.jni.Sodium
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.main
 * @Description: presenter of SplashActivity
 * @date 2018/09/10 22:25:34
 */
class SplashPresenter @Inject
constructor(internal var httpAPIWrapper: HttpAPIWrapper, private val mView: SplashContract.View) : SplashContract.SplashContractPresenter {
    private val mCompositeDisposable: CompositeDisposable
    private val JUMPTOLOGIN = 1
    private val HASPUDATE = 3
    private val getLastVersionBack = false
    private var permissionState = -1    //-1表示原始状态,0表示允许,1表示拒绝.
    private var hasUpdate = false
    private var timeOver = false
    private val jump = JUMPTOLOGIN
    private var jumpToGuest = false

    override fun doAutoLogin() {
        Log.i("splash", "2")
    }

    override fun getLastVersion() {
        Log.i("splash", "1")
       /* if (SpUtil.getInt(AppConfig.instance, ConstantValue.LOCALVERSIONCODE, 0) !== VersionUtil.getAppVersionCode(AppConfig.instance)) {
            KLog.i("需要跳转到guest.........................")
            KLog.i(SpUtil.getInt(AppConfig.instance, ConstantValue.LOCALVERSIONCODE, 0))
            KLog.i(VersionUtil.getAppVersionCode(AppConfig.instance))
            jumpToGuest = true
        }*/
        /*var routerList = AppConfig.instance.mDaoMaster!!.newSession().routerEntityDao.loadAll()
        if(routerList.size == 0)
        {
            jumpToGuest = true
        }*/
        if( ConstantValue.libsodiumprivateSignKey.equals(""))
        {
            jumpToGuest = true
        }
    }

    override fun getPermission() {
        AndPermission.with(mView as Activity)
                .requestCode(101)
                .permission(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_PHONE_STATE
                )
                .callback(permission)
                .start()
    }

    override fun observeJump() {
        Observable.interval(0, 1, TimeUnit.SECONDS).take(4)
                .map { aLong -> 2 - aLong }
                .observeOn(AndroidSchedulers.mainThread())//发射用的是observeOn
                .doOnSubscribe {
                    KLog.i("1")
                }
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Long> {
                    override fun onSubscribe(d: Disposable) {
                        KLog.i("2")
                    }

                    override fun onNext(remainTime: Long) {
                        KLog.i("剩余时间$remainTime")
                    }

                    override fun onError(e: Throwable) {
                        KLog.i("4")
                    }

                    override fun onComplete() {
                        AppConfig.instance.messageReceiver = null
                        //                        jump = JUMPTOGUEST;
                        timeOver = true
                        KLog.i("时间到，开始跳转")
                        if (permissionState != 0) {
                            return
                        }
                        if (jumpToGuest) {
                            mView.jumpToGuest()
                            return
                        }
                        if (jump == JUMPTOLOGIN) {
                            mView.jumpToLogin()
                        }
                    }
                })
    }

    init {
        mCompositeDisposable = CompositeDisposable()
    }

    override fun subscribe() {

    }

    override fun unsubscribe() {
        if (!mCompositeDisposable.isDisposed) {
            mCompositeDisposable.dispose()
        }
    }

    private val permission = object : PermissionListener {
        override fun onSucceed(requestCode: Int, grantedPermissions: List<String>) {
            DeleteUtils.deleteDirectory(Environment.getExternalStorageDirectory().toString()+ConstantValue.localPath+"/temp/")//删除外部查看文件的临时路径
            FileUtil.init()
            PathUtils.getInstance().initDirs("", "", AppConfig.instance)

            if(ConstantValue.encryptionType.equals("0"))
            {
                ConstantValue.privateRAS = SpUtil.getString(AppConfig.instance, ConstantValue.privateRASSp, "")
                ConstantValue.publicRAS = SpUtil.getString(AppConfig.instance, ConstantValue.publicRASSp, "")
                if(ConstantValue.privateRAS.equals("") && ConstantValue.publicRAS.equals(""))
                {
                    val gson = Gson()
                    var rsaData = FileUtil.readKeyData("data");
                    val localRSAArrayList: ArrayList<RSAData>
                    if(rsaData.equals("")&& false)
                    {
                        val KeyPair = RxEncryptTool.generateRSAKeyPair(1024)
                        val aahh = KeyPair!!.private.format
                        val strBase64Private:String = RxEncodeTool.base64Encode2String(KeyPair.private.encoded)
                        val strBase64Public = RxEncodeTool.base64Encode2String(KeyPair.public.encoded)
                        ConstantValue.privateRAS = strBase64Private
                        ConstantValue.publicRAS = strBase64Public
                        SpUtil.putString(AppConfig.instance, ConstantValue.privateRASSp, ConstantValue.privateRAS!!)
                        SpUtil.putString(AppConfig.instance, ConstantValue.publicRASSp, ConstantValue.publicRAS!!)
                        localRSAArrayList = ArrayList()
                        var RSAData: RSAData = RSAData()
                        RSAData.privateKey = strBase64Private
                        RSAData.publicKey = strBase64Public
                        localRSAArrayList.add(RSAData)
                        FileUtil.saveKeyData(gson.toJson(localRSAArrayList),"data")
                    }else{
                        var rsaStr = rsaData
                        if (rsaStr != "") {
                            localRSAArrayList = gson.fromJson<ArrayList<RSAData>>(rsaStr, object : TypeToken<ArrayList<RSAData>>() {

                            }.type)
                            if(localRSAArrayList.size > 0)
                            {
                                ConstantValue.privateRAS = localRSAArrayList.get(0).privateKey
                                ConstantValue.publicRAS =  localRSAArrayList.get(0).publicKey
                                SpUtil.putString(AppConfig.instance, ConstantValue.privateRASSp, ConstantValue.privateRAS!!)
                                SpUtil.putString(AppConfig.instance, ConstantValue.publicRASSp, ConstantValue.publicRAS!!)
                            }
                        }
                    }
                }
            }else{
                ConstantValue.libsodiumprivateSignKey = ""
                ConstantValue.libsodiumpublicSignKey = ""
                ConstantValue.libsodiumprivateMiKey = ""
                ConstantValue.libsodiumpublicMiKey = ""
                ConstantValue.localUserName = ""
                if(ConstantValue.libsodiumprivateSignKey.equals("") && ConstantValue.libsodiumpublicSignKey.equals(""))
                {
                    val gson = Gson()
                    var signData = FileUtil.readKeyData("libsodiumdata_sign")
                    var miData = FileUtil.readKeyData("libsodiumdata_mi")
                    val localSignArrayList: ArrayList<CryptoBoxKeypair>
                    val localMiArrayList: ArrayList<CryptoBoxKeypair>
                    if(signData.equals("")&& false)//不用在这里创建
                    {
                        var dst_public_SignKey = ByteArray(32)
                        var dst_private_Signkey = ByteArray(64)
                        var crypto_box_keypair_result = Sodium.crypto_sign_keypair(dst_public_SignKey,dst_private_Signkey)

                        val strSignPrivate:String =  RxEncodeTool.base64Encode2String(dst_private_Signkey)
                        val strSignPublic =  RxEncodeTool.base64Encode2String(dst_public_SignKey)
                        ConstantValue.libsodiumprivateSignKey = strSignPrivate
                        ConstantValue.libsodiumpublicSignKey = strSignPublic
                        SpUtil.putString(AppConfig.instance, ConstantValue.libsodiumprivateSignKeySp, ConstantValue.libsodiumprivateSignKey!!)
                        SpUtil.putString(AppConfig.instance, ConstantValue.libsodiumpublicSignKeySp, ConstantValue.libsodiumpublicSignKey!!)
                        localSignArrayList = ArrayList()
                        var SignData: CryptoBoxKeypair = CryptoBoxKeypair()
                        SignData.privateKey = strSignPrivate
                        SignData.publicKey = strSignPublic
                        localSignArrayList.add(SignData)
                        FileUtil.saveKeyData(gson.toJson(localSignArrayList),"libsodiumdata_sign")


                        var dst_public_MiKey = ByteArray(32)
                        var dst_private_Mikey = ByteArray(32)
                        var crypto_sign_ed25519_pk_to_curve25519_result = Sodium.crypto_sign_ed25519_pk_to_curve25519(dst_public_MiKey,dst_public_SignKey)
                        var crypto_sign_ed25519_sk_to_curve25519_result = Sodium.crypto_sign_ed25519_sk_to_curve25519(dst_private_Mikey,dst_private_Signkey)

                        val strMiPrivate:String =  RxEncodeTool.base64Encode2String(dst_private_Mikey)
                        val strMiPublic =  RxEncodeTool.base64Encode2String(dst_public_MiKey)
                        ConstantValue.libsodiumprivateMiKey = strMiPrivate
                        ConstantValue.libsodiumpublicMiKey = strMiPublic
                        SpUtil.putString(AppConfig.instance, ConstantValue.libsodiumprivateMiKeySp, ConstantValue.libsodiumprivateMiKey!!)
                        SpUtil.putString(AppConfig.instance, ConstantValue.libsodiumpublicMiKeySp, ConstantValue.libsodiumpublicMiKey!!)
                        localMiArrayList = ArrayList()
                        var RSAData: CryptoBoxKeypair = CryptoBoxKeypair()
                        RSAData.privateKey = strMiPrivate
                        RSAData.publicKey = strMiPublic
                        localMiArrayList.add(RSAData)
                        FileUtil.saveKeyData(gson.toJson(localMiArrayList),"libsodiumdata_mi")


                    }else{
                        var signStr = signData
                        if (signStr != "") {
                            localSignArrayList = gson.fromJson<ArrayList<CryptoBoxKeypair>>(signStr, object : TypeToken<ArrayList<CryptoBoxKeypair>>() {

                            }.type)
                            if(localSignArrayList.size > 0)
                            {
                                ConstantValue.libsodiumprivateSignKey = localSignArrayList.get(0).privateKey
                                ConstantValue.libsodiumpublicSignKey =  localSignArrayList.get(0).publicKey
                                ConstantValue.localUserName =  localSignArrayList.get(0).userName
                                SpUtil.putString(AppConfig.instance, ConstantValue.libsodiumprivateSignKeySp, ConstantValue.libsodiumprivateSignKey!!)
                                SpUtil.putString(AppConfig.instance, ConstantValue.libsodiumpublicSignKeySp, ConstantValue.libsodiumpublicSignKey!!)
                                SpUtil.putString(AppConfig.instance, ConstantValue.localUserNameSp, ConstantValue.localUserName!!)
                                SpUtil.putString(AppConfig.instance, ConstantValue.username, ConstantValue.localUserName!!)
                            }
                        }

                        var miStr = miData
                        if (miStr != "") {
                            localMiArrayList = gson.fromJson<ArrayList<CryptoBoxKeypair>>(miStr, object : TypeToken<ArrayList<CryptoBoxKeypair>>() {

                            }.type)
                            if(localMiArrayList.size > 0)
                            {
                                ConstantValue.libsodiumprivateMiKey = localMiArrayList.get(0).privateKey
                                ConstantValue.libsodiumpublicMiKey =  localMiArrayList.get(0).publicKey
                                ConstantValue.localUserName =  localMiArrayList.get(0).userName
                                SpUtil.putString(AppConfig.instance, ConstantValue.libsodiumprivateMiKeySp, ConstantValue.libsodiumprivateMiKey!!)
                                SpUtil.putString(AppConfig.instance, ConstantValue.libsodiumpublicMiKeySp, ConstantValue.libsodiumpublicMiKey!!)
                                SpUtil.putString(AppConfig.instance, ConstantValue.localUserNameSp, ConstantValue.localUserName!!)
                                SpUtil.putString(AppConfig.instance, ConstantValue.username, ConstantValue.localUserName!!)
                            }
                        }
                    }
                }
            }
            AppConfig.instance.mDaoMaster!!.newSession().routerEntityDao.deleteAll()
            LocalRouterUtils.inspectionLocalData();
            LocalRouterUtils.updateGreanDaoFromLocal()
            var tempFile = AppConfig.instance.getFilesDir().getAbsolutePath() +"/temp/"//删除聊天的临时加密文件
            val savedNodeFile = Environment.getExternalStorageDirectory().toString()+ConstantValue.localPath+"/Nodefile.json"
            RxFileTool.deleteFilesInDir(tempFile)
            RxFileTool.deleteNodefile(savedNodeFile)
            val userId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
            if(userId == null || userId.equals(""))
            {
                DeleteUtils.deleteFile(Environment.getExternalStorageDirectory().toString()+ConstantValue.localPath+"/RouterList/fileData3.json")
            }
            FileUtil.drawableToFile(AppConfig.instance,R.drawable.image_defalut_bg,"image_defalut_bg.xml",1)
            FileUtil.drawableToFile(AppConfig.instance,R.drawable.image_defalut_bg,"image_defalut_fileForward_bg.xml",1)
            FileUtil.drawableToFile(AppConfig.instance,R.mipmap.ic_upload_photo,"image_defalut_bg.png",1)
            FileUtil.drawableToFile(AppConfig.instance,R.mipmap.doc_img_default,"image_defalut_fileForward_bg.png",1)
            FileUtil.drawableToFile(AppConfig.instance,R.drawable.image_defalut_bg,"ease_default_amr.amr",2)
            FileUtil.drawableToFile(AppConfig.instance,R.drawable.image_defalut_bg,"ease_default_vedio.mp4",3)
            FileUtil.drawableToFile(AppConfig.instance,R.drawable.image_defalut_bg,"ease_default_fileForward_vedio.mp4",3)
            FileUtil.drawableToFile(AppConfig.instance,R.drawable.image_defalut_bg,"file_downloading.*",5)
            FileUtil.drawableToFile(AppConfig.instance,R.drawable.image_defalut_bg,"file_fileForward.*",5)
            /*FileUtil.getKongFile("image_defalut_bg.xml")
            FileUtil.getKongFile("image_defalut_bg.png")
            FileUtil.getKongFile("image_defalut_fileForward_bg.png")
            FileUtil.getKongFile("ease_default_amr.amr")
            FileUtil.getKongFile("ease_default_vedio.mp4")
            FileUtil.getKongFile("ease_default_fileForward_vedio.mp4")
            FileUtil.getKongFile("file_downloading.*")
            FileUtil.getKongFile("file_fileForward.*")*/
            var routerList = AppConfig.instance.mDaoMaster!!.newSession().routerEntityDao.loadAll()
            var abvc = ""
            routerList.forEach {
                if (it.routerId == null || it.routerId.equals("") || it.userSn == null || it.userSn.equals("") || it.userId == null || it.userId.equals("")) {
                    AppConfig.instance.mDaoMaster!!.newSession().routerEntityDao.delete(it)
                }
            }



            if(ConstantValue.isAntox)
            {
                var toxData: ToxData = CreateUserUtils.createToxData("myRouter",AppConfig.instance)
                var toxId:String =  FileUtil.getLocalUserData("toxId")
                if(toxId == null || toxId.equals(""))            {

                    if(toxData != null && toxData.address()!= null)
                    {
                        var toxAddress: ToxAddress = toxData.address()
                        var toxId = toxAddress.toString()
                        FileUtil.saveUserData2Local(toxId,"toxId")
                    }
                }
            }

            getLastVersion()
            var lastLoginRouterId = FileUtil.getLocalUserData("routerid")
            var lastLoginUserSn = FileUtil.getLocalUserData("usersn")
            ConstantValue.currentRouterId = lastLoginRouterId;

            //System.out.println(ByteOrder.nativeOrder());
            // 权限申请成功回调。
            if (requestCode == 101) {
                permissionState = 0
                if (timeOver) {
                    if (jumpToGuest) {
                        mView.jumpToGuest()
                        return
                    }
                    else if (jump == JUMPTOLOGIN) {
                        mView.jumpToLogin()
                    }
                }
            }
        }

        override fun onFailed(requestCode: Int, deniedPermissions: List<String>) {
            // 权限申请失败回调。
            if (requestCode == 101) {
                KLog.i("权限申请失败")
                permissionState = 0
                AppConfig.instance.toast(R.string.permission_denied)
                mView.exitApp()
            }
        }
    }
    private fun startTox()
    {
        ConstantValue.curreantNetworkType = "TOX"
       /* LogUtil.addLog("P2P启动连接:","SplashActivity")
        var intent = Intent(AppConfig.instance, KotlinToxService::class.java)
        AppConfig.instance.startService(intent)*/
    }

}