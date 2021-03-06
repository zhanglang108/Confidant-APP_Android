package com.stratagile.pnrouter.ui.activity.chat

import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.util.DisplayMetrics
import android.view.KeyEvent
import android.view.View
import android.view.ViewTreeObserver
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import chat.tox.antox.tox.MessageHelper
import chat.tox.antox.tox.ToxService
import chat.tox.antox.wrapper.FriendKey
import cn.bingoogolapple.qrcode.zxing.QRCodeDecoder
import com.alibaba.fastjson.JSONObject
import com.google.gson.Gson
import com.hyphenate.chat.EMMessage
import com.hyphenate.easeui.EaseConstant
import com.hyphenate.easeui.ui.EaseChatFragment
import com.hyphenate.easeui.utils.PathUtils
import com.luck.picture.lib.PicturePreviewActivity
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.adapter.SimpleFragmentAdapter
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.photoview.PhotoView
import com.message.Message
import com.pawegio.kandroid.toast
import com.socks.library.KLog
import com.stratagile.pnrouter.BuildConfig
import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.constant.ConstantValue.port
import com.stratagile.pnrouter.constant.UserDataManger
import com.stratagile.pnrouter.data.web.PNRouterServiceMessageReceiver
import com.stratagile.pnrouter.db.*
import com.stratagile.pnrouter.entity.*
import com.stratagile.pnrouter.entity.events.*
import com.stratagile.pnrouter.fingerprint.MyAuthCallback
import com.stratagile.pnrouter.ui.activity.admin.AdminLoginActivity
import com.stratagile.pnrouter.ui.activity.chat.component.DaggerChatComponent
import com.stratagile.pnrouter.ui.activity.chat.contract.ChatContract
import com.stratagile.pnrouter.ui.activity.chat.module.ChatModule
import com.stratagile.pnrouter.ui.activity.chat.presenter.ChatPresenter
import com.stratagile.pnrouter.ui.activity.login.LoginActivityActivity
import com.stratagile.pnrouter.ui.activity.main.MainActivity
import com.stratagile.pnrouter.ui.activity.user.SendAddFriendActivity
import com.stratagile.pnrouter.utils.*
import com.stratagile.pnrouter.view.CustomPopWindow
import com.stratagile.pnrouter.view.RxDialogLoading
import com.stratagile.pnrouter.view.SweetAlertDialog
import com.stratagile.tox.toxcore.KotlinToxService
import com.stratagile.tox.toxcore.ToxCoreJni
import events.*
import im.tox.tox4j.core.enums.ToxMessageType
import interfaceScala.InterfaceScaleUtil
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.libsodium.jni.Sodium
import java.io.File
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import kotlin.collections.ArrayList

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.chat
 * @Description: $description
 * @date 2018/09/13 13:18:46
 */

class ChatActivity : BaseActivity(), ChatContract.View, PNRouterServiceMessageReceiver.ChatCallBack, ViewTreeObserver.OnGlobalLayoutListener, SimpleFragmentAdapter.OnPictureLongClick {



    override fun onLongClick(path: String?,mContext:Activity,viewShow: View) {
        isAddMenu = 0;
        hasQRCode = "";
        image = viewShow;
        imageActivity = mContext;
        var localFilePath = path;
        val list = java.util.ArrayList<String>()
        list.add("Save Image")
        if (hasQRCode != null && hasQRCode != "") {
            list.add("Scan QR Code in Image")
        }else{
            Thread(Runnable {
                if(hasQRCode == null || hasQRCode.equals(""))
                {
                    EventBus.getDefault().post(AddMenu())
                }
            }).start()
        }
        PopWindowUtil.showSelecMenuPopWindow(mContext, image!!, list, object : PopWindowUtil.OnSelectListener
        {
            override fun onSelect(position: Int, obj: Any) {

                val choose = obj.toString()
                when (choose) {
                    "Save Image" -> {
                        var galleryPath = (Environment.getExternalStorageDirectory().toString()
                                + File.separator + Environment.DIRECTORY_DCIM
                                + File.separator + "Confidant" + File.separator)
                        val galleryPathFile = File(galleryPath)
                        if (!galleryPathFile.exists()) {
                            galleryPathFile.mkdir()
                        }
                        var imagePath = localFilePath
                        if (fileUrl != null) {
                            imagePath = fileUrl
                        }
                        galleryPath += System.currentTimeMillis().toString() + imagePath!!.substring(imagePath.lastIndexOf("."), imagePath.length)
                        val result = FileUtil.copyAppFileToSdcard(imagePath, galleryPath)
                        if (result == 1) {

                            AlbumNotifyHelper.insertImageToMediaStore(AppConfig.instance, galleryPath, System.currentTimeMillis())
                        }
                        EventBus.getDefault().post(SaveMsgEvent("", result))
                    }
                    "Scan QR Code in Image" ->
                    {
                        var result = hasQRCode
                        var type = result!!.substring(0,6);
                        var data = result!!.substring(7,result!!.length);
                        var soureData:ByteArray =  ByteArray(0)
                        if(!type.equals("type_0"))
                        {
                            soureData =  AESCipher.aesDecryptByte(data,"welcometoqlc0101")
                        }
                        if (hasQRCode!!.indexOf("http://") > -1 || hasQRCode!!.indexOf("https://") > -1) {
                            /*val intent = Intent(AppConfig.instance, WebViewActivity::class.java)
                            intent.putExtra("url", hasQRCode)
                            intent.putExtra("title", "Other websites")
                            startActivity(intent)*/
                            val intent = Intent()
                            intent.action = "android.intent.action.VIEW"
                            val url = Uri.parse(hasQRCode)
                            intent.data = url
                            startActivity(intent)
                        } else if (!result!!.contains("type_")){
                            if (NetUtils.isMacAddress(result)) {
                                runOnUiThread {
                                    SweetAlertDialog(imageActivity, SweetAlertDialog.BUTTON_NEUTRAL)
                                            .setContentText(getString(R.string.Are_you_sure_you_want_to_leave_the_circle))
                                            .setConfirmClickListener {
                                                imageActivity!!.finish()
                                                var selfUserId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
                                                var msgData = LogOutReq(ConstantValue.currentRouterId,selfUserId!!,ConstantValue.currentRouterSN)
                                                if (ConstantValue.isWebsocketConnected) {
                                                    AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(2,msgData))
                                                } else if (ConstantValue.isToxConnected) {
                                                    val baseData = BaseData(2,msgData)
                                                    val baseDataJson = JSONObject.toJSON(baseData).toString().replace("\\", "")
                                                    ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.currentRouterId.substring(0, 64))
                                                }
                                                ConstantValue.loginReq = null
                                                isScanSwitch = true
                                                scanType = 0;
                                                RouterMacStr = result
                                                if(RouterMacStr != null && !RouterMacStr.equals(""))
                                                {
                                                    if(AppConfig.instance.messageReceiver != null)
                                                        AppConfig.instance.messageReceiver!!.close()
                                                    if(WiFiUtil.isWifiConnect())
                                                    {
                                                        showProgressDialog("wait...")
                                                        ConstantValue.currentRouterMac  = ""
                                                        isFromScanAdmim = true
                                                        var count =0;
                                                        KLog.i("测试计时器Mac" + count)
                                                        Thread(Runnable() {
                                                            run() {
                                                                Thread.sleep(1500)
                                                                while (true)
                                                                {
                                                                    if(count >=3)
                                                                    {
                                                                       /* if(ConstantValue.currentRouterMac.equals(""))
                                                                        {
                                                                            runOnUiThread {
                                                                                closeProgressDialog()
                                                                                RouterMacStr = ""
                                                                                isFromScanAdmim = false
                                                                                gotoLogin()
                                                                                toast(R.string.Unable_to_connect_to_router)
                                                                            }
                                                                        }
                                                                        Thread.currentThread().interrupt(); //方法调用终止线程
                                                                        break;*/
                                                                        if(!ConstantValue.currentRouterMac.equals(""))
                                                                        {
                                                                            Thread.currentThread().interrupt(); //方法调用终止线程
                                                                            break;
                                                                        }else{
                                                                            getMacFromRemote()
                                                                            break;
                                                                        }

                                                                    }/*else if(!ConstantValue.currentRouterMac.equals(""))
                                                                    {
                                                                        Thread.currentThread().interrupt(); //方法调用终止线程
                                                                        break;
                                                                    }*/
                                                                    count ++;
                                                                    MobileSocketClient.getInstance().init(handler,AppConfig.instance)
                                                                    var macAddress  = ""
                                                                    for (i in 0..5) {
                                                                        macAddress = macAddress + RouterMacStr.substring(i * 2, (i + 1) * 2) + ":"
                                                                    }
                                                                    macAddress = macAddress.subSequence(0, macAddress.length - 1).toString()
                                                                    var toMacMi = AESCipher.aesEncryptString(macAddress,"slph\$%*&^@-78231")
                                                                    MobileSocketClient.getInstance().destroy()
                                                                    MobileSocketClient.getInstance().send("MAC"+toMacMi)
                                                                    MobileSocketClient.getInstance().receive()
                                                                    KLog.i("测试计时器Mac" + count)
                                                                    Thread.sleep(1000)
                                                                }

                                                            }
                                                        }).start()

                                                    }else{
                                                        /*runOnUiThread {
                                                            closeProgressDialog()
                                                            gotoLogin()
                                                            toast(R.string.Please_connect_to_WiFi)
                                                        }*/
                                                        getMacFromRemote()
                                                    }
                                                }else{
                                                    runOnUiThread {
                                                        closeProgressDialog()
                                                        toast(R.string.code_error)
                                                    }
                                                }
                                            }
                                            .show()
                                }
                                return;

                            } else {
                                runOnUiThread {
                                    toast(R.string.code_error)
                                }
                                return
                            }
                            runOnUiThread {
                                toast(R.string.code_error)
                            }

                            return;
                        }
                        else if (hasQRCode!!.contains("type_0")) {
                            val toAddUserId = hasQRCode!!.substring(7, hasQRCode!!.length)
                            val selfUserId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
                            if (toAddUserId.contains(selfUserId!!)) {
                                runOnUiThread {
                                    runOnUiThread {
                                        toast(R.string.The_same_user)
                                    }

                                }
                                return
                            }
                            if ("" != toAddUserId) {
                                val toAddUserIdTemp = toAddUserId.substring(0, toAddUserId.indexOf(","))
                                var intent = Intent(AppConfig.instance, SendAddFriendActivity::class.java)
                                val useEntityList = AppConfig.instance.mDaoMaster!!.newSession().userEntityDao.loadAll()
                                for (i in useEntityList) {
                                    if (i.userId == toAddUserIdTemp) {
                                        var nickName = toAddUserId!!.substring(toAddUserId!!.indexOf(",") + 1, toAddUserId.lastIndexOf(","))
                                        i.nickName = nickName
                                        AppConfig.instance.mDaoMaster!!.newSession().userEntityDao.update(i)
                                        var freindStatusData = FriendEntity()
                                        freindStatusData.friendLocalStatus = 7
                                        val localFriendStatusList = AppConfig.instance.mDaoMaster!!.newSession().friendEntityDao.queryBuilder().where(FriendEntityDao.Properties.UserId.eq(selfUserId), FriendEntityDao.Properties.FriendId.eq(toAddUserIdTemp)).list()
                                        if (localFriendStatusList.size > 0)
                                            freindStatusData = localFriendStatusList[0]

                                        if (freindStatusData.friendLocalStatus == 0) {
                                            intent.putExtra("user", i)
                                            startActivity(intent)
                                        } else {
                                            intent = Intent(AppConfig.instance, SendAddFriendActivity::class.java)
                                            intent.putExtra("user", i)
                                            startActivity(intent)
                                        }

                                        return
                                    }
                                }
                                intent = Intent(AppConfig.instance, SendAddFriendActivity::class.java)
                                val userEntity = UserEntity()
                                //userEntity.friendStatus = 7
                                userEntity.userId = toAddUserId.substring(0, toAddUserId.indexOf(","))
                                userEntity.nickName = toAddUserId.substring(toAddUserId.indexOf(",") + 1, toAddUserId.lastIndexOf(","))
                                userEntity.signPublicKey = toAddUserId.substring(toAddUserId.lastIndexOf(",") + 1, toAddUserId.length)
                                userEntity.timestamp = Calendar.getInstance().timeInMillis

                                userEntity.routerUserId = selfUserId
                                AppConfig.instance.mDaoMaster!!.newSession().userEntityDao.insert(userEntity)


                                val userId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
                                val newFriendStatus = FriendEntity()
                                newFriendStatus.userId = userId
                                newFriendStatus.friendId = toAddUserId
                                newFriendStatus.friendLocalStatus = 7
                                newFriendStatus.timestamp = Calendar.getInstance().timeInMillis
                                AppConfig.instance.mDaoMaster!!.newSession().friendEntityDao.insert(newFriendStatus)
                                intent.putExtra("user", userEntity)
                                startActivity(intent)
                            }
                        } else if (hasQRCode!!.contains("type_1")) {

                            scanType = 1
                            val keyId:ByteArray = ByteArray(6) //密钥ID
                            val RouterId:ByteArray = ByteArray(76) //路由器id
                            val UserSn:ByteArray = ByteArray(32)  //用户SN
                            System.arraycopy(soureData, 0, keyId, 0, 6)
                            System.arraycopy(soureData, 6, RouterId, 0, 76)
                            System.arraycopy(soureData, 82, UserSn, 0, 32)
                            var keyIdStr = String(keyId)
                            var RouterIdStr = String(RouterId)
                            var UserSnStr = String(UserSn)

                            ConstantValue.scanRouterId = RouterIdStr
                            ConstantValue.scanRouterSN = UserSnStr
                            if(RouterIdStr != null && !RouterIdStr.equals("")&& UserSnStr != null && !UserSnStr.equals(""))
                            {
                                if(ConstantValue.currentRouterId.equals(RouterIdStr))
                                {
                                    runOnUiThread {
                                        toast(R.string.The_same_circle_without_switching)
                                    }

                                    return
                                }
                                runOnUiThread {
                                    SweetAlertDialog(imageActivity, SweetAlertDialog.BUTTON_NEUTRAL)
                                            .setContentText(getString(R.string.Are_you_sure_you_want_to_leave_the_circle))
                                            .setConfirmClickListener {
                                                imageActivity!!.finish()
                                                showProgressDialog("wait...")
                                                var selfUserId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
                                                var msgData = LogOutReq(ConstantValue.currentRouterId,selfUserId!!,ConstantValue.currentRouterSN)
                                                if (ConstantValue.isWebsocketConnected) {
                                                    AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(2,msgData))
                                                } else if (ConstantValue.isToxConnected) {
                                                    val baseData = BaseData(2,msgData)
                                                    val baseDataJson = JSONObject.toJSON(baseData).toString().replace("\\", "")
                                                    ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.currentRouterId.substring(0, 64))
                                                }
                                                ConstantValue.loginReq = null
                                                isScanSwitch = true
                                                if(AppConfig.instance.messageReceiver != null)
                                                    AppConfig.instance.messageReceiver!!.close()
                                                ConstantValue.lastNetworkType = ConstantValue.curreantNetworkType

                                                ConstantValue.lastRouterIp =  ConstantValue.currentRouterIp
                                                ConstantValue.lastPort=  ConstantValue.port
                                                ConstantValue.lastFilePort= ConstantValue.filePort
                                                ConstantValue.lastRouterId =   ConstantValue.currentRouterId
                                                ConstantValue.lastRouterSN =  ConstantValue.currentRouterSN

                                                isFromScan = true
                                                ConstantValue.currentRouterIp = ""
                                                if(WiFiUtil.isWifiConnect())
                                                {
                                                    showProgressDialog("wait...")
                                                    var count =0;
                                                    KLog.i("测试计时器" + count)
                                                    Thread(Runnable() {
                                                        run() {
                                                            Thread.sleep(1500)
                                                            while (true)
                                                            {
                                                                if(count >=3)
                                                                {
                                                                    if(!ConstantValue.currentRouterIp.equals(""))
                                                                    {
                                                                        Thread.currentThread().interrupt(); //方法调用终止线程
                                                                        break;
                                                                    }else{

                                                                        OkHttpUtils.getInstance().doGet(ConstantValue.httpUrl + RouterIdStr,  object : OkHttpUtils.OkCallback {
                                                                            override fun onFailure( e :Exception) {
                                                                                startToxAndRecovery()
                                                                                Thread.currentThread().interrupt(); //方法调用终止线程
                                                                            }
                                                                            override fun  onResponse(json:String ) {

                                                                                val gson = GsonUtil.getIntGson()
                                                                                var httpData: HttpData? = null
                                                                                try {
                                                                                    if (json != null) {
                                                                                        httpData = gson.fromJson<HttpData>(json, HttpData::class.java)
                                                                                        if(httpData != null  && httpData.retCode == 0 && httpData.connStatus == 1)
                                                                                        {
                                                                                            ConstantValue.curreantNetworkType = "WIFI"
                                                                                            ConstantValue.currentRouterIp = httpData.serverHost
                                                                                            ConstantValue.port = ":"+httpData.serverPort.toString()
                                                                                            ConstantValue.filePort = ":"+(httpData.serverPort +1).toString()
                                                                                            ConstantValue.currentRouterId = ConstantValue.scanRouterId
                                                                                            ConstantValue.currentRouterSN =  ConstantValue.scanRouterSN
                                                                                            if(ConstantValue.isHasWebsocketInit)
                                                                                            {
                                                                                                AppConfig.instance.getPNRouterServiceMessageReceiver().reConnect()
                                                                                            }else{
                                                                                                ConstantValue.isHasWebsocketInit = true
                                                                                                AppConfig.instance.getPNRouterServiceMessageReceiver(true)
                                                                                            }
                                                                                            //KLog.i("没有初始化。。设置loginBackListener"+_this)
                                                                                            //AppConfig.instance.messageReceiver!!.loginBackListener = _this
                                                                                            Thread.currentThread().interrupt() //方法调用终止线程
                                                                                        }else{
                                                                                            startToxAndRecovery()
                                                                                            Thread.currentThread().interrupt(); //方法调用终止线程
                                                                                        }

                                                                                    }
                                                                                } catch (e: Exception) {
                                                                                    startToxAndRecovery()
                                                                                    Thread.currentThread().interrupt(); //方法调用终止线程
                                                                                }
                                                                            }
                                                                        })
                                                                        break;
                                                                    }

                                                                }
                                                                count ++;
                                                                MobileSocketClient.getInstance().init(handler,AppConfig.instance)
                                                                var toxIdMi = AESCipher.aesEncryptString(RouterIdStr,"slph\$%*&^@-78231")
                                                                MobileSocketClient.getInstance().destroy()
                                                                MobileSocketClient.getInstance().send("QLC"+toxIdMi)
                                                                MobileSocketClient.getInstance().receive()
                                                                KLog.i("测试计时器" + count)
                                                                Thread.sleep(1000)
                                                            }

                                                        }
                                                    }).start()
                                                }else{
                                                    showProgressDialog("wait...")
                                                    Thread(Runnable() {
                                                        run() {
                                                            OkHttpUtils.getInstance().doGet(ConstantValue.httpUrl + RouterIdStr,  object : OkHttpUtils.OkCallback {
                                                                override fun onFailure( e :Exception) {
                                                                    startToxAndRecovery()
                                                                }

                                                                override fun  onResponse(json:String ) {

                                                                    val gson = GsonUtil.getIntGson()
                                                                    var httpData: HttpData? = null
                                                                    try {
                                                                        if (json != null) {
                                                                            var  httpData = gson.fromJson<HttpData>(json, HttpData::class.java)
                                                                            if(httpData != null  && httpData.retCode == 0 && httpData.connStatus == 1)
                                                                            {
                                                                                ConstantValue.curreantNetworkType = "WIFI"
                                                                                ConstantValue.currentRouterIp = httpData.serverHost
                                                                                ConstantValue.port = ":"+httpData.serverPort.toString()
                                                                                ConstantValue.filePort = ":"+(httpData.serverPort +1).toString()
                                                                                ConstantValue.currentRouterId = ConstantValue.scanRouterId
                                                                                ConstantValue.currentRouterSN =  ConstantValue.scanRouterSN
                                                                                if(ConstantValue.isHasWebsocketInit)
                                                                                {
                                                                                    AppConfig.instance.getPNRouterServiceMessageReceiver().reConnect()
                                                                                }else{
                                                                                    ConstantValue.isHasWebsocketInit = true
                                                                                    AppConfig.instance.getPNRouterServiceMessageReceiver(true)
                                                                                }
                                                                                //KLog.i("没有初始化。。设置loginBackListener"+_this)
                                                                                //AppConfig.instance.messageReceiver!!.loginBackListener = _this
                                                                            }else{
                                                                                startToxAndRecovery()
                                                                            }

                                                                        }
                                                                    } catch (e: Exception) {
                                                                        startToxAndRecovery()
                                                                    }
                                                                }
                                                            })
                                                        }
                                                    }).start()
                                                }
                                            }
                                            .show()
                                }


                            }else{
                                runOnUiThread {
                                    toast(R.string.code_error)
                                }
                            }
                        }else if (hasQRCode!!.contains("type_2")) {
                            scanType = 0;
                            RouterMacStr = String(soureData)
                            if(RouterMacStr != null && !RouterMacStr.equals(""))
                            {
                                runOnUiThread {
                                    SweetAlertDialog(imageActivity, SweetAlertDialog.BUTTON_NEUTRAL)
                                            .setContentText(getString(R.string.Are_you_sure_you_want_to_leave_the_circle))
                                            .setConfirmClickListener {
                                                imageActivity!!.finish()
                                                showProgressDialog("wait...")
                                                var selfUserId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
                                                var msgData = LogOutReq(ConstantValue.currentRouterId,selfUserId!!,ConstantValue.currentRouterSN)
                                                if (ConstantValue.isWebsocketConnected) {
                                                    AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(2,msgData))
                                                } else if (ConstantValue.isToxConnected) {
                                                    val baseData = BaseData(2,msgData)
                                                    val baseDataJson = JSONObject.toJSON(baseData).toString().replace("\\", "")
                                                    ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.currentRouterId.substring(0, 64))
                                                }
                                                ConstantValue.loginReq = null
                                                isScanSwitch = true
                                                if(AppConfig.instance.messageReceiver != null)
                                                    AppConfig.instance.messageReceiver!!.close()
                                                if(WiFiUtil.isWifiConnect())
                                                {
                                                    showProgressDialog("wait...")
                                                    ConstantValue.currentRouterMac  = ""
                                                    isFromScanAdmim = true
                                                    var count =0;
                                                    KLog.i("测试计时器Mac" + count)
                                                    Thread(Runnable() {
                                                        run() {
                                                            Thread.sleep(1500)
                                                            while (true)
                                                            {
                                                                if(count >=3)
                                                                {
                                                                    if(ConstantValue.currentRouterMac.equals(""))
                                                                    {
                                                                        runOnUiThread {
                                                                            closeProgressDialog()
                                                                            RouterMacStr = ""
                                                                            isFromScanAdmim = false
                                                                            toast(R.string.Unable_to_connect_to_router)
                                                                        }
                                                                    }
                                                                    Thread.currentThread().interrupt(); //方法调用终止线程
                                                                    break;
                                                                }else if(!ConstantValue.currentRouterMac.equals(""))
                                                                {
                                                                    Thread.currentThread().interrupt(); //方法调用终止线程
                                                                    break;
                                                                }
                                                                count ++;
                                                                MobileSocketClient.getInstance().init(handler,AppConfig.instance)
                                                                var toMacMi = AESCipher.aesEncryptString(RouterMacStr,"slph\$%*&^@-78231")
                                                                MobileSocketClient.getInstance().destroy()
                                                                MobileSocketClient.getInstance().send("MAC"+toMacMi)
                                                                MobileSocketClient.getInstance().receive()
                                                                KLog.i("测试计时器Mac" + count)
                                                                Thread.sleep(1000)
                                                            }

                                                        }
                                                    }).start()

                                                }else{
                                                    runOnUiThread {
                                                        closeProgressDialog()
                                                        toast(R.string.Please_connect_to_WiFi)
                                                    }
                                                }
                                            }
                                            .show()
                                }


                            }else{
                                runOnUiThread {
                                    closeProgressDialog()
                                    toast(R.string.code_error)
                                }
                            }
                        }else if (hasQRCode!!.contains("type_3")) {
                            try
                            {
                                var left = result.substring(7,result.length)
                                var signprivatek = left.substring(0,left.indexOf(","))
                                left = left.substring(signprivatek.length+1,left.length)
                                var usersn = left.substring(0,left.indexOf(","))
                                left = left.substring(usersn.length+1,left.length)
                                var username = left.substring(0,left.length)
                                username = String(RxEncodeTool.base64Decode(username))
                                SpUtil.putString(AppConfig.instance, ConstantValue.username, username)
                                var routerList = AppConfig.instance.mDaoMaster!!.newSession().routerEntityDao.loadAll()

                                if(signprivatek.equals(ConstantValue.libsodiumprivateSignKey))
                                {
                                    toast(R.string.Same_account_no_need_to_import)
                                    return;
                                }else{
                                    runOnUiThread {
                                        SweetAlertDialog(imageActivity, SweetAlertDialog.BUTTON_NEUTRAL)
                                                .setContentText(getString(R.string.Do_you_leave_the_circle_to_import_new_accounts))
                                                .setConfirmClickListener {
                                                    imageActivity!!.finish()
                                                    var type = result.substring(0,6);
                                                    var left = result.substring(7,result.length)
                                                    var signprivatek = left.substring(0,left.indexOf(","))
                                                    left = left.substring(signprivatek.length+1,left.length)
                                                    var usersn = left.substring(0,left.indexOf(","))
                                                    left = left.substring(usersn.length+1,left.length)
                                                    var username = left.substring(0,left.length)
                                                    username = String(RxEncodeTool.base64Decode(username))
                                                    val localSignArrayList: java.util.ArrayList<CryptoBoxKeypair>
                                                    val localMiArrayList: java.util.ArrayList<CryptoBoxKeypair>
                                                    val gson = Gson()

                                                    var strSignPublicSouce = ByteArray(32)
                                                    var  signprivatekByteArray = RxEncodeTool.base64Decode(signprivatek)
                                                    System.arraycopy(signprivatekByteArray, 32, strSignPublicSouce, 0, 32)


                                                    var dst_public_SignKey = strSignPublicSouce
                                                    var dst_private_Signkey = signprivatekByteArray
                                                    val strSignPrivate:String =  signprivatek
                                                    val strSignPublic =  RxEncodeTool.base64Encode2String(strSignPublicSouce)
                                                    ConstantValue.libsodiumprivateSignKey = strSignPrivate
                                                    ConstantValue.libsodiumpublicSignKey = strSignPublic
                                                    ConstantValue.localUserName = username
                                                    SpUtil.putString(AppConfig.instance, ConstantValue.libsodiumprivateSignKeySp, ConstantValue.libsodiumprivateSignKey!!)
                                                    SpUtil.putString(AppConfig.instance, ConstantValue.libsodiumpublicSignKeySp, ConstantValue.libsodiumpublicSignKey!!)
                                                    SpUtil.putString(AppConfig.instance, ConstantValue.localUserNameSp, ConstantValue.localUserName!!)
                                                    SpUtil.putString(AppConfig.instance, ConstantValue.username, ConstantValue.localUserName!!)
                                                    localSignArrayList = java.util.ArrayList()
                                                    var SignData: CryptoBoxKeypair = CryptoBoxKeypair()
                                                    SignData.privateKey = strSignPrivate
                                                    SignData.publicKey = strSignPublic
                                                    SignData.userName = username
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
                                                    ConstantValue.localUserName = username
                                                    SpUtil.putString(AppConfig.instance, ConstantValue.libsodiumprivateMiKeySp, ConstantValue.libsodiumprivateMiKey!!)
                                                    SpUtil.putString(AppConfig.instance, ConstantValue.libsodiumpublicMiKeySp, ConstantValue.libsodiumpublicMiKey!!)
                                                    SpUtil.putString(AppConfig.instance, ConstantValue.localUserNameSp, ConstantValue.localUserName!!)
                                                    SpUtil.putString(AppConfig.instance, ConstantValue.username, ConstantValue.localUserName!!)
                                                    localMiArrayList = java.util.ArrayList()
                                                    var RSAData: CryptoBoxKeypair = CryptoBoxKeypair()
                                                    RSAData.privateKey = strMiPrivate
                                                    RSAData.publicKey = strMiPublic
                                                    RSAData.userName = username
                                                    localMiArrayList.add(RSAData)
                                                    FileUtil.saveKeyData(gson.toJson(localMiArrayList),"libsodiumdata_mi")
                                                    FileUtil.deleteFile(Environment.getExternalStorageDirectory().getPath()+ConstantValue.localPath + "/RouterList/routerData.json")
                                                    AppConfig.instance.mDaoMaster!!.newSession().routerEntityDao.deleteAll()
                                                    ConstantValue.loginReq = null
                                                    AppConfig.instance.deleteEmailData()
                                                    runOnUiThread {
                                                        toast("Import success")
                                                        startActivity(Intent(_this, LoginActivityActivity::class.java))
                                                        finish()
                                                    }
                                                }
                                                .show()
                                    }
                                }
                            }catch (e:Exception)
                            {
                                runOnUiThread {
                                    closeProgressDialog()
                                    toast(R.string.code_error)
                                }
                                e.printStackTrace();
                            }

                        }else{
                            runOnUiThread {
                                closeProgressDialog()
                                toast(R.string.code_error)
                            }
                        }
                    }
                    else -> {

                    }
                }
            }
        })
    }

    var isAddMenu = 0;
    private var fileUrl: String? = null
    private var localFilePath: String? = null
    private var bitmap: Bitmap? = null
    private var isDownloaded: Boolean = false
    private var hasQRCode: String? = ""
    private lateinit var standaloneCoroutine : Job
    var image:View? = null
    var routerId = ""
    var userSn = ""
    var userId = ""
    var username = ""
    var dataFileVersion = 0
    var isScanSwitch = false

    var SELECT_PHOTO = 2
    var SELECT_VIDEO = 3
    var SELECT_DEOCUMENT = 4
    var create_group = 5
    var add_activity = 6
    var isSendRegId = true
    var newRouterEntity = RouterEntity()
    private var loginGoMain:Boolean = false
    var loginBack = false
    var isFromScan = false
    var isFromScanAdmim = false
    //是否点击了登陆按钮
    //是否点击了登陆按钮
    var isClickLogin = false
    //是否正在登陆
    var isStartLogin = false
    var stopTox = false;
    var loginOk = false
    var isToxLoginOverTime = false;
    var maxLogin = 0
    var threadInit = false
    var RouterMacStr = ""
    var islogining = false
    var isloginOutTime = false
    var scanType = 0 // 0 admin   1 其他
    var adminUserSn:String?  = null
    var hasFinger = false
    var name:Long  = 0;
    var activityFlag = ""
    var imageActivity:Activity? = null;
    var _this = this
    var isStartWebsocket = false
    override fun registerBack(registerRsp: JRegisterRsp) {
        if(image == null)
        {
            return;
        }
        if(!isScanSwitch)
        {
            runOnUiThread {
                closeProgressDialog()
            }
            return;
        }
        if (registerRsp.params.retCode != 0) {
            if (registerRsp.params.retCode == 1) {
                runOnUiThread {
                    toast("RouterId Error")
                    closeProgressDialog()
                    gotoLogin()
                }
            }
            if (registerRsp.params.retCode == 2) {
                runOnUiThread {
                    toast("QR code has been activated by other users.")
                    closeProgressDialog()
                    gotoLogin()
                }
            }
            if (registerRsp.params.retCode == 3) {
                runOnUiThread {
                    toast("Error Verification Code")
                    gotoLogin()
                    closeProgressDialog()
                }
            }
            if (registerRsp.params.retCode == 4) {
                runOnUiThread {
                    toast("Other Error")
                    gotoLogin()
                    closeProgressDialog()
                }
            }
            return
        }
        if ("".equals(registerRsp.params.userId)) {
            runOnUiThread {
                toast("Too many users")
                gotoLogin()
                closeProgressDialog()
            }
        } else {
            runOnUiThread {
                closeProgressDialog()
                KLog.i("1111")
            }
            var newRouterEntity = RouterEntity()
            newRouterEntity.routerId = registerRsp.params.routeId
            newRouterEntity.userSn = registerRsp.params.userSn
            newRouterEntity.username = ConstantValue.localUserName
            newRouterEntity.userId = registerRsp.params.userId
            newRouterEntity.loginKey = "";
            newRouterEntity.dataFileVersion = registerRsp.params.dataFileVersion
            newRouterEntity.dataFilePay = registerRsp.params.dataFilePay
            newRouterEntity.adminId = registerRsp.params!!.adminId
            newRouterEntity.adminName = registerRsp.params!!.adminName
            newRouterEntity.adminKey = registerRsp.params!!.adminKey
            var localData: java.util.ArrayList<MyRouter> =  LocalRouterUtils.localAssetsList
            newRouterEntity.routerName = String(RxEncodeTool.base64Decode(registerRsp.params!!.routerName))
            val myRouter = MyRouter()
            myRouter.setType(0)
            myRouter.setRouterEntity(newRouterEntity)
            LocalRouterUtils.insertLocalAssets(myRouter)
            AppConfig.instance.mDaoMaster!!.newSession().routerEntityDao.insert(newRouterEntity)
            var sign = ByteArray(32)
            var time = (System.currentTimeMillis() /1000).toString().toByteArray()
            System.arraycopy(time, 0, sign, 0, time.size)
            var dst_signed_msg = ByteArray(96)
            var signed_msg_len = IntArray(1)
            var mySignPrivate  = RxEncodeTool.base64Decode(ConstantValue.libsodiumprivateSignKey)
            var crypto_sign = Sodium.crypto_sign(dst_signed_msg,signed_msg_len,sign,sign.size,mySignPrivate)
            var signBase64 = RxEncodeTool.base64Encode2String(dst_signed_msg)
            val NickName = RxEncodeTool.base64Encode2String( ConstantValue.localUserName!!.toByteArray())
            //var LoginKeySha = RxEncryptTool.encryptSHA256ToString(userName3.text.toString())
            //var login = LoginReq(  registerRsp.params.routeId,registerRsp.params.userSn, registerRsp.params.userId,LoginKeySha, registerRsp.params.dataFileVersion)
            var login = LoginReq_V4(  registerRsp.params.routeId,registerRsp.params.userSn, registerRsp.params.userId,signBase64, registerRsp.params.dataFileVersion,NickName)
            islogining = true
            ConstantValue.loginReq = login
            if(ConstantValue.isWebsocketConnected)
            {

                AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(4,login))
            }
            else if(ConstantValue.isToxConnected)
            {
                var baseData = BaseData(4,login)
                var baseDataJson = baseData.baseDataToJson().replace("\\", "")
                if (ConstantValue.isAntox) {
                    var friendKey: FriendKey = FriendKey(registerRsp.params.routeId.substring(0, 64))
                    MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL)
                }else{
                    ToxCoreJni.getInstance().senToxMessage(baseDataJson, registerRsp.params.routeId.substring(0, 64))
                }
            }
        }
    }

    override fun loginBack(loginRsp: JLoginRsp) {
//        if (!loginRsp.params.userId.equals(userId)) {
//            KLog.i("过滤掉userid错误的请求")
//            return
//        }
        if(image == null)
        {
            return;
        }
        if(!isScanSwitch)
        {
            runOnUiThread {
                closeProgressDialog()
            }
            return;
        }
        islogining = false
        ConstantValue.unSendMessage.remove("login")
        ConstantValue.unSendMessageFriendId.remove("login")
        ConstantValue.unSendMessageSendCount.remove("login")
        KLog.i(loginRsp.toString())
        LogUtil.addLog("loginBack:"+loginRsp.params.retCode,"LoginActivityActivity")
        if(standaloneCoroutine != null)
            standaloneCoroutine.cancel()
        if (loginRsp.params.retCode != 0) {
            if (loginRsp.params.retCode == 1) {
                runOnUiThread {
                    toast(R.string.need_Verification)
                    closeProgressDialog()
                }
            }
            else if (loginRsp.params.retCode == 2) {
                runOnUiThread {
                    toast(R.string.rid_error)
                    closeProgressDialog()
                }
            }
            else if (loginRsp.params.retCode == 3) {
                runOnUiThread {
                    toast(R.string.uid_error)
                    closeProgressDialog()
                }
            }
            else if (loginRsp.params.retCode == 4) {
                runOnUiThread {
                    toast(R.string.Validation_failed)
                    closeProgressDialog()
                }
            }
            else if (loginRsp.params.retCode == 5) {
                runOnUiThread {
                    toast(R.string.Verification_code_error)
                    closeProgressDialog()
                }
            }
            else if (loginRsp.params.retCode == 7) {
                runOnUiThread {
                    toast(R.string.The_account_has_expired)
                    closeProgressDialog()
                }
            }
            else{
                runOnUiThread {
                    toast(R.string.other_error)
                    closeProgressDialog()
                }
            }
            gotoLogin()
            return
        }
        if ("".equals(loginRsp.params.userId)) {
            runOnUiThread {
                toast(R.string.userId_is_empty)
                closeProgressDialog()
                gotoLogin()
            }
        } else {
            islogining = false
            ConstantValue.loginOut = false
            ConstantValue.logining = true
            LogUtil.addLog("loginBack:"+"begin","LoginActivityActivity")
            FileUtil.saveUserData2Local(loginRsp.params!!.userId,"userid")
            //FileUtil.saveUserData2Local(loginRsp.params!!.index,"userIndex")
            LogUtil.addLog("loginBack:"+"a","LoginActivityActivity")
            FileUtil.saveUserData2Local(loginRsp.params!!.userSn,"usersn")
            LogUtil.addLog("loginBack:"+"b","LoginActivityActivity")
            FileUtil.saveUserData2Local(loginRsp.params!!.routerid,"routerid")
            LogUtil.addLog("loginBack:"+"c","LoginActivityActivity")
            KLog.i("服务器返回的userId：${loginRsp.params!!.userId}")
            ConstantValue.currentRouterId = loginRsp.params!!.routerid
            newRouterEntity.userId = loginRsp.params!!.userId
            newRouterEntity.index = ""
            SpUtil.putString(this, ConstantValue.userId, loginRsp.params!!.userId)
            //SpUtil.putString(this, ConstantValue.userIndex, loginRsp.params!!.index)
            //SpUtil.putString(this, ConstantValue.username,ConstantValue.localUserName!!)
            SpUtil.putString(this, ConstantValue.routerId, loginRsp.params!!.routerid)
            var routerList = AppConfig.instance.mDaoMaster!!.newSession().routerEntityDao.loadAll()
            newRouterEntity.routerId = loginRsp.params!!.routerid
            newRouterEntity.routerName = String(RxEncodeTool.base64Decode(loginRsp.params!!.routerName))
            if(loginRsp.params.nickName != null)
                newRouterEntity.username = String(RxEncodeTool.base64Decode(loginRsp.params.nickName))
            newRouterEntity.lastCheck = true
            newRouterEntity.userSn = loginRsp.params!!.userSn
            newRouterEntity.loginKey = ""
            var myUserData = UserEntity()
            myUserData.userId = loginRsp.params!!.userId
            myUserData.nickName = loginRsp.params!!.nickName
            UserDataManger.myUserData = myUserData
            var contains = false
            for (i in routerList) {
                if (i.userSn.equals(loginRsp.params!!.userSn)) {
                    contains = true
                    newRouterEntity = i

                    break
                }
            }
            LogUtil.addLog("loginBack:"+"d","LoginActivityActivity")
            var needUpdate : java.util.ArrayList<MyRouter> = java.util.ArrayList();
            routerList.forEach {
                it.lastCheck = false
                var myRouter:MyRouter = MyRouter();
                myRouter.setType(0)
                myRouter.setRouterEntity(it)
                needUpdate.add(myRouter);
            }
            AppConfig.instance.mDaoMaster!!.newSession().routerEntityDao.updateInTx(routerList)
            LocalRouterUtils.updateList(needUpdate)
            newRouterEntity.lastCheck = true
            newRouterEntity.loginKey = ""
            newRouterEntity.routerName = String(RxEncodeTool.base64Decode(loginRsp.params!!.routerName))
            newRouterEntity.dataFileVersion = 0
            newRouterEntity.dataFilePay =  ""
            newRouterEntity.adminId = loginRsp.params!!.adminId
            newRouterEntity.adminName = loginRsp.params!!.adminName
            newRouterEntity.adminKey = loginRsp.params!!.adminKey
            ConstantValue.currentRouterSN = loginRsp.params!!.userSn
            ConstantValue.isCurrentRouterAdmin =  loginRsp.params!!.userSn.indexOf("01") == 0
            if (contains) {
                KLog.i("数据局中已经包含了这个userSn")
                AppConfig.instance.mDaoMaster!!.newSession().routerEntityDao.update(newRouterEntity)
            } else {

                AppConfig.instance.mDaoMaster!!.newSession().routerEntityDao.insert(newRouterEntity)
            }
            LogUtil.addLog("loginBack:"+"e","LoginActivityActivity")
            //更新sd卡路由器数据begin
            val myRouter = MyRouter()
            myRouter.setType(0)
            myRouter.setRouterEntity(newRouterEntity)
            LocalRouterUtils.insertLocalAssets(myRouter)
            runOnUiThread {
                closeProgressDialog()
                KLog.i("333")
            }
            LogUtil.addLog("loginBack:"+"f","LoginActivityActivity")
            loginOk = true
            isToxLoginOverTime = false
            ConstantValue.hasLogin = true
            ConstantValue.isHeart = true
            resetUnCompleteFileRecode()
            if(loginGoMain)
                return
            isScanSwitch = false
            ConstantValue.isInit = false
            AppConfig.instance.mAppActivityManager.finishAllActivityWithoutThis()
            startActivity(Intent(this, MainActivity::class.java))
            loginGoMain  = true
            LogUtil.addLog("loginBack:"+"g","LoginActivityActivity")
            finish()
        }
    }

    override fun recoveryBack(recoveryRsp: JRecoveryRsp) {
        if(image == null)
        {
            return;
        }
        if(!isScanSwitch)
        {
            runOnUiThread {
                closeProgressDialog()
            }
            return;
        }
        closeProgressDialog()
        KLog.i("222")
        ConstantValue.unSendMessage.remove("recovery")
        ConstantValue.unSendMessageFriendId.remove("recovery")
        ConstantValue.unSendMessageSendCount.remove("recovery")
        when (recoveryRsp.params.retCode) {
            0 ->{
                ConstantValue.lastNetworkType = "";
                /*val routerEntityList = AppConfig.instance.mDaoMaster!!.newSession().routerEntityDao.queryBuilder().where(RouterEntityDao.Properties.UserSn.eq(recoveryRsp.params.userSn)).list()
                if (routerEntityList != null && routerEntityList!!.size != 0) {
                    var routerEntity:RouterEntity = routerEntityList[0]
                    routerId = routerEntity.routerId
                    userSn = routerEntity.userSn
                    userId = routerEntity.userId
                    username = routerEntity.username
                    dataFileVersion = routerEntity.dataFileVersion
                    *//*runOnUiThread {
                        routerNameTips.text = newRouterEntity.routerName
                    }*//*
                    EventBus.getDefault().post(NameChange(routerEntity.routerName,routerEntity.loginKey))
                }else{
                    var newRouterEntity = RouterEntity()
                    newRouterEntity.routerId = recoveryRsp.params.routeId
                    newRouterEntity.userSn = recoveryRsp.params.userSn
                    newRouterEntity.username = String(RxEncodeTool.base64Decode(recoveryRsp.params.nickName))
                    newRouterEntity.userId = recoveryRsp.params.userId
                    newRouterEntity.dataFileVersion = recoveryRsp.params.dataFileVersion
                    newRouterEntity.loginKey = ""
                    newRouterEntity.dataFilePay = ""
                    var localData: java.util.ArrayList<MyRouter> =  LocalRouterUtils.localAssetsList
                    newRouterEntity.routerName = String(RxEncodeTool.base64Decode(recoveryRsp.params!!.routerName))
                    routerId = recoveryRsp.params.routeId
                    userSn = recoveryRsp.params.userSn
                    userId = recoveryRsp.params.userId
                    username =String(RxEncodeTool.base64Decode(recoveryRsp.params.nickName))
                    dataFileVersion =recoveryRsp.params.dataFileVersion
                    //routerNameTips.text = newRouterEntity.routerName
                    EventBus.getDefault().post(NameChange(newRouterEntity.routerName))
                    val myRouter = MyRouter()
                    myRouter.setType(0)
                    myRouter.setRouterEntity(newRouterEntity)
                    LocalRouterUtils.insertLocalAssets(myRouter)
                    AppConfig.instance.mDaoMaster!!.newSession().routerEntityDao.insert(newRouterEntity)
                }*/
                var sign = ByteArray(32)
                var time = (System.currentTimeMillis() /1000).toString().toByteArray()
                System.arraycopy(time, 0, sign, 0, time.size)
                var dst_signed_msg = ByteArray(96)
                var signed_msg_len = IntArray(1)
                var mySignPrivate  = RxEncodeTool.base64Decode(ConstantValue.libsodiumprivateSignKey)
                var crypto_sign = Sodium.crypto_sign(dst_signed_msg,signed_msg_len,sign,sign.size,mySignPrivate)
                var signBase64 = RxEncodeTool.base64Encode2String(dst_signed_msg)
                val NickName = RxEncodeTool.base64Encode2String( ConstantValue.localUserName!!.toByteArray())
                //var LoginKeySha = RxEncryptTool.encryptSHA256ToString(userName3.text.toString())
                //var login = LoginReq(  registerRsp.params.routeId,registerRsp.params.userSn, registerRsp.params.userId,LoginKeySha, registerRsp.params.dataFileVersion)
                var login = LoginReq_V4(  recoveryRsp.params.routeId,recoveryRsp.params.userSn, recoveryRsp.params.userId,signBase64, recoveryRsp.params.dataFileVersion,NickName)
                islogining = true
                ConstantValue.loginReq = login
                if(ConstantValue.isWebsocketConnected)
                {

                    AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(4,login))
                }
                else if(ConstantValue.isToxConnected)
                {
                    var baseData = BaseData(4,login)
                    var baseDataJson = baseData.baseDataToJson().replace("\\", "")
                    ToxCoreJni.getInstance().senToxMessage(baseDataJson, recoveryRsp.params.routeId.substring(0, 64))
                }
            }
            1 -> {
                /*  AppConfig.instance.messageReceiver!!.loginBackListener = null
                  startActivity(Intent(this, RegisterActivity::class.java))*/

                runOnUiThread {
                    showProgressDialog("waiting...")
                }

                val NickName = RxEncodeTool.base64Encode2String( ConstantValue.localUserName!!.toByteArray())
                var sign = ByteArray(32)
                var time = (System.currentTimeMillis() /1000).toString().toByteArray()
                System.arraycopy(time, 0, sign, 0, time.size)
                var dst_signed_msg = ByteArray(96)
                var signed_msg_len = IntArray(1)
                var mySignPrivate  = RxEncodeTool.base64Decode(ConstantValue.libsodiumprivateSignKey)
                var crypto_sign = Sodium.crypto_sign(dst_signed_msg,signed_msg_len,sign,sign.size,mySignPrivate)
                var signBase64 = RxEncodeTool.base64Encode2String(dst_signed_msg)
                var pulicMiKey = ConstantValue.libsodiumpublicSignKey!!
                //var LoginKey = RxEncryptTool.encryptSHA256ToString(userName3.text.toString())
                //var regeister = RegeisterReq( ConstantValue.scanRouterId, ConstantValue.scanRouterSN, IdentifyCode.text.toString(),LoginKey,NickName)
                var regeister = RegeisterReq_V4( recoveryRsp.params.routeId,recoveryRsp.params.userSn, signBase64,pulicMiKey,NickName)
                if(ConstantValue.isWebsocketConnected)
                {
                    AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(4,regeister))
                }
                else if(ConstantValue.isToxConnected)
                {
                    var baseData = BaseData(4,regeister)
                    var baseDataJson = baseData.baseDataToJson().replace("\\", "")
                    if (ConstantValue.isAntox) {
                        var friendKey: FriendKey = FriendKey(recoveryRsp.params.routeId.substring(0, 64))
                        MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL)
                    }else{
                        ToxCoreJni.getInstance().senToxMessage(baseDataJson, recoveryRsp.params.routeId.substring(0, 64))
                    }
                }
            }
            2 -> {
                runOnUiThread {
                    toast(R.string.rid_error)
                    gotoLogin()
                }

            }
            3-> {
                ConstantValue.lastNetworkType = "";
                /*val routerEntityList = AppConfig.instance.mDaoMaster!!.newSession().routerEntityDao.queryBuilder().where(RouterEntityDao.Properties.UserSn.eq(recoveryRsp.params.userSn)).list()
                if (routerEntityList != null && routerEntityList!!.size != 0) {
                    for( i in routerEntityList)
                    {

                        if(i!= null && !i.userId.equals(""))
                        {
                            routerId = i.routerId
                            userSn = i.userSn
                            userId = i.userId
                            username = i.username
                            if(i.dataFileVersion == null)
                            {
                                dataFileVersion = 0
                            }else{
                                dataFileVersion = i.dataFileVersion
                            }

                            runOnUiThread()
                            {
                                routerNameTips.text = i.routerName
                                ivAvatar.setText(i.routerName)
                                loginKey.setText(i.loginKey)
                            }
                            ConstantValue.currentRouterIp = ""
                            //ConstantValue.scanRouterId = routerId;
                            isClickLogin = false
                            isStartLogin = true
                            getServer(routerId,userSn,true,true)
                        }
                    }
                }else{*/
                    /*  AppConfig.instance.messageReceiver!!.loginBackListener = null
                      var intent = Intent(this, RegisterActivity::class.java)
                      intent.putExtra("flag", 1)
                      startActivity(intent)*/
                    runOnUiThread {
                        showProgressDialog("waiting...")
                    }

                    val NickName = RxEncodeTool.base64Encode2String( ConstantValue.localUserName!!.toByteArray())
                    var sign = ByteArray(32)
                    var time = (System.currentTimeMillis() /1000).toString().toByteArray()
                    System.arraycopy(time, 0, sign, 0, time.size)
                    var dst_signed_msg = ByteArray(96)
                    var signed_msg_len = IntArray(1)
                    var mySignPrivate  = RxEncodeTool.base64Decode(ConstantValue.libsodiumprivateSignKey)
                    var crypto_sign = Sodium.crypto_sign(dst_signed_msg,signed_msg_len,sign,sign.size,mySignPrivate)
                    var signBase64 = RxEncodeTool.base64Encode2String(dst_signed_msg)
                    var pulicMiKey = ConstantValue.libsodiumpublicSignKey!!
                    //var LoginKey = RxEncryptTool.encryptSHA256ToString(userName3.text.toString())
                    //var regeister = RegeisterReq( ConstantValue.scanRouterId, ConstantValue.scanRouterSN, IdentifyCode.text.toString(),LoginKey,NickName)
                    var regeister = RegeisterReq_V4( recoveryRsp.params.routeId,recoveryRsp.params.userSn, signBase64,pulicMiKey,NickName)
                    if(ConstantValue.isWebsocketConnected)
                    {
                        AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(4,regeister))
                    }
                    else if(ConstantValue.isToxConnected)
                    {
                        var baseData = BaseData(4,regeister)
                        var baseDataJson = baseData.baseDataToJson().replace("\\", "")
                        if (ConstantValue.isAntox) {
                            var friendKey: FriendKey = FriendKey( recoveryRsp.params.routeId.substring(0, 64))
                            MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL)
                        }else{
                            ToxCoreJni.getInstance().senToxMessage(baseDataJson,  recoveryRsp.params.routeId.substring(0, 64))
                        }
                    }
                //}

            }
            4 -> {
                runOnUiThread {
                    toast(R.string.other_error)
                }
            }
            5 -> {
                runOnUiThread {
                    toast(R.string.The_QR_code_has_been_occupied_by_others)
                }
            }
            6 -> {
                runOnUiThread {
                    toast(R.string.The_account_has_expired)
                }
            }
            else -> {
                runOnUiThread {
                    toast(R.string.other_error)
                }
            }
        }
    }

    override fun fileForwardReq(jFileForwardRsp: JFileForwardRsp) {
        chatFragment?.upateForwardMessage(jFileForwardRsp)
    }

    override fun updateAvatarReq(jUpdateAvatarRsp: JUpdateAvatarRsp) {
        if(jUpdateAvatarRsp.params.retCode == 0)
        {

            var filePath = jUpdateAvatarRsp.params.fileName
            var fileBase58Name = filePath.substring(8,filePath.length)
            var fileName = String(Base58.decode(fileBase58Name));
            val filledUri = "https://" + ConstantValue.currentRouterIp + ConstantValue.port + filePath
            var fileSavePath  = PathUtils.getInstance().filePath.toString() + "/"
            var msgId = Calendar.getInstance().timeInMillis /1000
            FileDownloadUtils.doDownLoadWork(filledUri,"", fileSavePath, this, msgId.toInt(), handlerDown, "","0")
        }
    }
    internal var handlerDown: Handler = object : Handler() {
        override fun handleMessage(msg: android.os.Message) {
            when (msg.what) {
                0x404 -> {

                }
                0x55 -> {
                }
            }//goMain();
            //goMain();
        }
    }
    override fun QueryFriendRep(jQueryFriendRsp: JQueryFriendRsp) {
        chatFragment?.setFriendStatus(jQueryFriendRsp.params.retCode)
    }

    override fun userInfoPushRsp(jUserInfoPushRsp: JUserInfoPushRsp) {
        chatFragment?.updatFriendName(jUserInfoPushRsp)
    }

    override fun pullFileMsgRsp(jJToxPullFileRsp: JToxPullFileRsp) {
        if(jJToxPullFileRsp.params.retCode != 0)
        {
            toast(R.string.acceptanceerror)
        }
    }

    override fun sendToxFileRsp(jSendToxFileRsp: JSendToxFileRsp) {
        chatFragment?.onToxFileSendRsp(jSendToxFileRsp)

    }

    override fun readMsgPushRsp(jReadMsgPushRsp: JReadMsgPushRsp) {

        var userId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
        var msgData = ReadMsgPushReq(0, "", userId!!)
        var msgId:String = ""
        if (ConstantValue.isWebsocketConnected) {
            AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(2,msgData,jReadMsgPushRsp.msgid))
        }else if (ConstantValue.isToxConnected) {
            var baseData = BaseData(2,msgData,jReadMsgPushRsp.msgid)
            var baseDataJson = baseData.baseDataToJson().replace("\\", "")
            if (ConstantValue.isAntox) {
                var friendKey: FriendKey = FriendKey(ConstantValue.currentRouterId.substring(0, 64))
                MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL)
            }else{
                ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.currentRouterId.substring(0, 64))
            }
        }
        chatFragment?.refreshReadData(jReadMsgPushRsp.params.readMsgs)
    }

    @Inject
    internal lateinit var mPresenter: ChatPresenter
    var activityInstance: ChatActivity? = null
    private var chatFragment: EaseChatFragment? = null
    internal var toChatUserID: String? = null
    var statusBarHeight: Int = 0
    var receiveFileDataMap = ConcurrentHashMap<String, JPushFileMsgRsp>()
    var receiveToxFileDataMap = ConcurrentHashMap<String, JPushFileMsgRsp>()
    override fun onGlobalLayout() {
        var myLayout = getWindow().getDecorView();
        val r = Rect()
        // 使用最外层布局填充，进行测算计算
        parentLayout.getWindowVisibleDisplayFrame(r)
        val screenHeight = myLayout.getRootView().getHeight()
        val heightDiff = screenHeight - (r.bottom - r.top)
        if (heightDiff > 100) {
            // 如果超过100个像素，它可能是一个键盘。获取状态栏的高度
            statusBarHeight = 0
        }
        getSupportSoftInputHeight()
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onToxFileSendFinished(toxSendFileFinishedEvent: ToxSendFileFinishedEvent) {
        var fileNumber=  toxSendFileFinishedEvent.fileNumber
        var key = toxSendFileFinishedEvent.key
        chatFragment?.onToxFileSendFinished(fileNumber,key)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun OnToxChatReceiveFileNoticeEvent(toxReceiveFileNoticeEvent: ToxChatReceiveFileNoticeEvent) {
        var fileNumber=  toxReceiveFileNoticeEvent.fileNumber
        var key = toxReceiveFileNoticeEvent.key
        var fileName = toxReceiveFileNoticeEvent.filename
        chatFragment?.onAgreeReceivwFileStart(fileNumber,key,fileName)

    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onToxChatReceiveFileFinishedEvent(toxReceiveFileFinishedEvent: ToxChatReceiveFileFinishedEvent) {
        var fileNumber=  toxReceiveFileFinishedEvent.fileNumber
        var key = toxReceiveFileFinishedEvent.key
        var fileNameSouce =  chatFragment?.getToxReceiveFileName(fileNumber,key)
        var fileNameList = fileNameSouce!!.split(":")
        var fileMiName = fileNameList[1]
        var jPushFileMsgRsp = receiveToxFileDataMap.get(fileMiName)
        if(jPushFileMsgRsp != null)
        {
            var fileName:String = jPushFileMsgRsp!!.params.fileName;
            val base58files_dir = PathUtils.getInstance().tempPath.toString() + "/" + fileName
            val files_dir = PathUtils.getInstance().filePath.toString() + "/" + fileName
            var aesKey = ""
            if(ConstantValue.encryptionType.equals("1"))
            {
                aesKey = LibsodiumUtil.DecryptShareKey(jPushFileMsgRsp!!.params.dstKey)
            }else{
                aesKey = RxEncodeTool.getAESKey(jPushFileMsgRsp!!.params.dstKey)
            }

            var code = FileUtil.copySdcardToxFileAndDecrypt(base58files_dir,files_dir,aesKey)
            if(code == 1)
            {
                var fromId = jPushFileMsgRsp!!.params.fromId;
                var toId = jPushFileMsgRsp!!.params.toId
                var FileType = jPushFileMsgRsp!!.params.fileType
                var timeStamp = (System.currentTimeMillis() / 1000).toInt()
                chatFragment?.receiveFileMessage(fileName,jPushFileMsgRsp.params.msgId.toString(),fromId,toId,FileType, "",timeStamp)
                receiveFileDataMap.remove(fileMiName)
            }
        }else{
            chatFragment?.onToxReceiveFileFinished(fileMiName)
        }

    }
    override fun queryFriend(FriendId :String) {

        var userId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
        var msgData = QueryFriendReq(userId!!, FriendId)
        if (ConstantValue.isWebsocketConnected) {
            AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(2,msgData))
        }else if (ConstantValue.isToxConnected) {
            var baseData = BaseData(2,msgData)
            var baseDataJson = baseData.baseDataToJson().replace("\\", "")
            if (ConstantValue.isAntox) {
                var friendKey: FriendKey = FriendKey(ConstantValue.currentRouterId.substring(0, 64))
                MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL)
            }else{
                ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.currentRouterId.substring(0, 64))
            }
        }
    }
    /**
     * 获取软件盘的高度
     * @return
     */
    private fun getSupportSoftInputHeight(): Int {
        val r = Rect()
        /**
         * decorView是window中的最顶层view，可以从window中通过getDecorView获取到decorView。
         * 通过decorView获取到程序显示的区域，包括标题栏，但不包括状态栏。
         */
        getWindow().getDecorView().getWindowVisibleDisplayFrame(r)
        //获取屏幕的高度
        val screenHeight = getWindow().getDecorView().getRootView().getHeight()
        //计算软件盘的高度
        var softInputHeight = screenHeight - r.bottom

        /**
         * 某些Android版本下，没有显示软键盘时减出来的高度总是144，而不是零，
         * 这是因为高度是包括了虚拟按键栏的(例如华为系列)，所以在API Level高于20时，
         * 我们需要减去底部虚拟按键栏的高度（如果有的话）
         */
        if (Build.VERSION.SDK_INT >= 20) {
            // When SDK Level >= 20 (Android L), the softInputHeight will contain the height of softButtonsBar (if has)
            softInputHeight = softInputHeight - getSoftButtonsBarHeight()
        }

        if (softInputHeight < 0) {
            KLog.w("EmotionKeyboard--Warning: value of softInputHeight is below zero!")
        }
        //存一份到本地
        if (softInputHeight > 0) {
            KLog.i("获取到的键盘的高度为: " + softInputHeight)
            SpUtil.putInt(this@ChatActivity, ConstantValue.realKeyboardHeight, softInputHeight)
        }
        return softInputHeight
    }


    /**
     * 底部虚拟按键栏的高度
     * @return
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private fun getSoftButtonsBarHeight(): Int {
        val metrics = DisplayMetrics()
        //这个方法获取可能不是真实屏幕的高度
        getWindowManager().getDefaultDisplay().getMetrics(metrics)
        val usableHeight = metrics.heightPixels
        //获取当前屏幕的真实高度
        getWindowManager().getDefaultDisplay().getRealMetrics(metrics)
        val realHeight = metrics.heightPixels
        return if (realHeight > usableHeight) {
            realHeight - usableHeight
        } else {
            0
        }
    }

    override fun pushDelMsgRsp(delMsgPushRsp: JDelMsgPushRsp) {

        var msgData = DelMsgRsp(0,"", delMsgPushRsp.params.friendId)
        if (ConstantValue.isWebsocketConnected) {
            AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(msgData,delMsgPushRsp.msgid))
        } else if (ConstantValue.isToxConnected) {
            var baseData = BaseData(msgData,delMsgPushRsp.msgid)
            var baseDataJson = baseData.baseDataToJson().replace("\\", "")
            if (ConstantValue.isAntox) {
                var friendKey: FriendKey = FriendKey(ConstantValue.currentRouterId.substring(0, 64))
                MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL)
            }else{
                ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.currentRouterId.substring(0, 64))
            }
        }
        if (delMsgPushRsp.params.userId.equals(toChatUserID)) {//正好在聊天窗口聊天
            chatFragment?.delFreindMsg(delMsgPushRsp)
        }
    }
    override fun pushFileMsgRsp(jPushFileMsgRsp: JPushFileMsgRsp) {
        KLog.i("abcdefshouTime:" + (System.currentTimeMillis() - ConstantValue.shouBegin) / 1000)
        val userId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
        val gson = Gson()
        val Message = Message()
        Message.msgType = jPushFileMsgRsp.params.fileType
        Message.fileName = jPushFileMsgRsp.params.fileName
        Message.filePath = jPushFileMsgRsp.params.filePath
        Message.msg = ""
        Message.from = userId
        Message.to = jPushFileMsgRsp.params.fromId
        Message.timeStamp = System.currentTimeMillis() / 1000
        Message.chatType = EMMessage.ChatType.Chat
        Message.unReadCount = 0
        val baseDataJson = gson.toJson(Message)
        if (Message.sender == 0) {
            SpUtil.putString(AppConfig.instance, ConstantValue.message + userId + "_" + jPushFileMsgRsp.params.fromId, baseDataJson)
        } else {
            SpUtil.putString(AppConfig.instance, ConstantValue.message + userId + "_" + jPushFileMsgRsp.params.fromId, baseDataJson)
        }
        var msgDataPushFileRsp = PushFileRespone(0,jPushFileMsgRsp.params.fromId, jPushFileMsgRsp.params.toId,jPushFileMsgRsp.params.msgId)
        var msgId:String = jPushFileMsgRsp?.params.msgId.toString()
        var readMsgReq  =  ReadMsgReq(userId!!,jPushFileMsgRsp.params.fromId,msgId)
        if (ConstantValue.isWebsocketConnected) {
            AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(5,msgDataPushFileRsp,jPushFileMsgRsp.msgid))
            if(!msgId.equals(""))
            {
                if (jPushFileMsgRsp.params.fromId.equals(toChatUserID)) {//正好在聊天窗口聊天
                    AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(2,readMsgReq))
                }
            }
        } else if (ConstantValue.isToxConnected) {
            var baseData = BaseData(5,msgDataPushFileRsp,jPushFileMsgRsp.msgid)
            var baseDataJson = baseData.baseDataToJson().replace("\\", "")
            if (ConstantValue.isAntox) {
                var friendKey: FriendKey = FriendKey(ConstantValue.currentRouterId.substring(0, 64))
                MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL)
            }else{
                ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.currentRouterId.substring(0, 64))
            }
            if (jPushFileMsgRsp.params.fromId.equals(toChatUserID)) {//正好在聊天窗口聊天
                var baseData2 = BaseData(2,readMsgReq)
                var baseDataJson2 = baseData2.baseDataToJson().replace("\\", "")
                if(!msgId.equals(""))
                {
                    if (ConstantValue.isAntox) {
                        var friendKey: FriendKey = FriendKey(ConstantValue.currentRouterId.substring(0, 64))
                        MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson2, ToxMessageType.NORMAL)
                    }else{
                        ToxCoreJni.getInstance().senToxMessage(baseDataJson2, ConstantValue.currentRouterId.substring(0, 64))
                    }
                }
            }

        }
        if (jPushFileMsgRsp.params.fromId.equals(toChatUserID)) {//正好在聊天窗口聊天
            var filledUri = "https://" + ConstantValue.currentRouterIp + port+jPushFileMsgRsp.params.filePath
            var fileName = jPushFileMsgRsp.params.fileName;
            var files_dir = PathUtils.getInstance().filePath.toString()+"/"
            if (ConstantValue.isWebsocketConnected) {
                receiveFileDataMap.put(jPushFileMsgRsp.params.msgId.toString(),jPushFileMsgRsp)
                FileDownloadUtils.doDownLoadWork(filledUri,Base58.encode(fileName.toByteArray()), files_dir, this,jPushFileMsgRsp.params.msgId, handler,jPushFileMsgRsp.params.dstKey,"0")
            }else{

                var base58Name =  Base58.encode(jPushFileMsgRsp.params.fileName.toByteArray())
                receiveToxFileDataMap.put(base58Name,jPushFileMsgRsp)
                var msgData = PullFileReq(jPushFileMsgRsp.params.fromId, jPushFileMsgRsp.params.toId,base58Name,jPushFileMsgRsp.params.msgId,2,1)
                var baseData = BaseData(msgData)
                var baseDataJson = baseData.baseDataToJson().replace("\\", "")
                if (ConstantValue.isAntox) {
                    var friendKey: FriendKey = FriendKey(ConstantValue.currentRouterId.substring(0, 64))
                    MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL)
                }else{
                    ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.currentRouterId.substring(0, 64))
                }
            }
        }
    }

    override fun delMsgRsp(delMsgRsp: JDelMsgRsp) {
        if (delMsgRsp.params.retCode == 0) {
            chatFragment?.delMyMsgOnSuccess(delMsgRsp.params.msgId.toString())
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun deleteMsgEvent(deleteMsgEvent: DeleteMsgEvent) {
        chatFragment?.delMyMsgOnSending(deleteMsgEvent.msgId)
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun saveMsgEvent(saveMsgEvent: SaveMsgEvent) {
        if(saveMsgEvent.result == 1)
        {
            runOnUiThread {
                toast(R.string.success)
            }

        }
    }
    override fun pullMsgRsp(pushMsgRsp: JPullMsgRsp) {


        var messageList: List<Message> = ArrayList<Message>()
        if(pushMsgRsp.params.payload != null)
        {
            messageList = pushMsgRsp.params.payload
        }
        KLog.i("insertMessage:ChatActivity"+chatFragment)
        val size = messageList.size
        var msgIdStr:String = "";
        for (mesage in messageList)
        {
            if(mesage.sender == 1)//对方发的消息
            {
                if(mesage.status == 0 || mesage.status == 1)//未读
                {
                    msgIdStr += (mesage.msgId.toString() + ",")
                }
            }

        }
        if(!msgIdStr.equals(""))
        {
            val userId = SpUtil.getString(this, ConstantValue.userId, "")
            var readMsgReq  =  ReadMsgReq(userId!!,pushMsgRsp.params.friendId,msgIdStr)
            if (ConstantValue.isWebsocketConnected) {
                AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(2,readMsgReq))
            }else if (ConstantValue.isToxConnected) {
                var baseData = BaseData(2,readMsgReq)
                var baseDataJson = baseData.baseDataToJson().replace("\\", "")
                if (ConstantValue.isAntox) {
                    var friendKey: FriendKey = FriendKey(ConstantValue.currentRouterId.substring(0, 64))
                    MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL)
                }else{
                    ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.currentRouterId.substring(0, 64))
                }
            }
        }
        chatFragment?.refreshData(messageList,pushMsgRsp.params.userId,pushMsgRsp.params.friendId)
    }

    override fun pushMsgRsp(pushMsgRsp: JPushMsgRsp) {

        var userId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
        var msgData = PushMsgReq(Integer.valueOf(pushMsgRsp?.params.msgId),userId!!, 0, "")
        var msgId:String = pushMsgRsp?.params.msgId.toString()
        var readMsgReq  =  ReadMsgReq(userId,pushMsgRsp.params.fromId,msgId)
        var sendData = BaseData(3,msgData,pushMsgRsp?.msgid)
        if(ConstantValue.encryptionType.equals("1"))
        {
            sendData = BaseData(3,msgData,pushMsgRsp?.msgid)
        }
        if (ConstantValue.isWebsocketConnected) {
            AppConfig.instance.getPNRouterServiceMessageSender().send(sendData)
            if(!msgId.equals(""))
            {
                if (pushMsgRsp.params.fromId.equals(toChatUserID)) {//正好在聊天窗口聊天
                    AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(2,readMsgReq))
                }
            }

        }else if (ConstantValue.isToxConnected) {
            var baseData = sendData
            var baseDataJson = baseData.baseDataToJson().replace("\\", "")
            if (ConstantValue.isAntox) {
                var friendKey: FriendKey = FriendKey(ConstantValue.currentRouterId.substring(0, 64))
                MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL)
            }else{
                ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.currentRouterId.substring(0, 64))
            }
            if (pushMsgRsp.params.fromId.equals(toChatUserID)) {//正好在聊天窗口聊天
                var baseData2 = BaseData(2,readMsgReq)
                var baseDataJson2 = baseData2.baseDataToJson().replace("\\", "")
                //var friendKey2: FriendKey = FriendKey(ConstantValue.currentRouterId.substring(0, 64))
                if(!msgId.equals(""))
                {
                    if (ConstantValue.isAntox) {
                        var friendKey: FriendKey = FriendKey(ConstantValue.currentRouterId.substring(0, 64))
                        MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL)
                    }else{
                        ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.currentRouterId.substring(0, 64))
                    }
                }
            }

        }
        if (pushMsgRsp.params.fromId.equals(toChatUserID)) {//正好在聊天窗口聊天
            if(ConstantValue.encryptionType.equals("1"))
            {
                chatFragment?.receiveTxtMessageV3(pushMsgRsp)
            }else{
                chatFragment?.receiveTxtMessage(pushMsgRsp)
            }

        }
    }

    override fun sendMsg(FromId: String, ToId: String, FriendPublicKey:String, Msg: String) {
        try {
            var len = Msg.toCharArray()
            if(len.size >ConstantValue.sendMaxSize)
            {
                toast(R.string.nomorecharacters)
                return
            }
            var aesKey =  RxEncryptTool.generateAESKey()
            LogUtil.addLog("sendMsg aesKey:",aesKey)
            var my = RxEncodeTool.base64Decode(ConstantValue.publicRAS)
            LogUtil.addLog("sendMsg myKey:",ConstantValue.publicRAS)
            var friend = RxEncodeTool.base64Decode(FriendPublicKey)
            LogUtil.addLog("sendMsg friendKey:",FriendPublicKey)
            var SrcKey = RxEncodeTool.base64Encode( RxEncryptTool.encryptByPublicKey(aesKey.toByteArray(),my))
            LogUtil.addLog("sendMsg SrcKey:",SrcKey.toString())
            var DstKey = RxEncodeTool.base64Encode(RxEncryptTool.encryptByPublicKey(aesKey.toByteArray(),friend))
            LogUtil.addLog("sendMsg DstKey:",SrcKey.toString())
            var miMsg = AESCipher.aesEncryptString(Msg,aesKey)
            LogUtil.addLog("sendMsg miMsg:",miMsg)
            var msgData = SendMsgReq(FromId!!, ToId!!, miMsg,String(SrcKey),String(DstKey))
            if (ConstantValue.isWebsocketConnected) {
                AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(msgData))
            }else if (ConstantValue.isToxConnected) {
                var baseData = BaseData(msgData)
                var baseDataJson = baseData.baseDataToJson().replace("\\", "")
                if (ConstantValue.isAntox) {
                    var friendKey: FriendKey = FriendKey(ConstantValue.currentRouterId.substring(0, 64))
                    MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL)
                }else{
                    ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.currentRouterId.substring(0, 64))
                }
            }
        }catch (e:Exception)
        {
            chatFragment?.deleteMessage()
            LogUtil.addLog("sendMsg 错误:",e.toString())
            toast(R.string.Encryptionerror)
        }
    }
    override fun sendMsgV3(FromIndex: String, ToIndex: String, FriendMiPublicKey :String, Msg: String):String {
        var msgId = 0
        try {
            if(FromIndex.equals("") || ToIndex.equals("") || FriendMiPublicKey.equals(""))
            {
                toast(R.string.Empty_with_parameters)
                return msgId.toString()
            }
            var len = Msg.toCharArray()
            if(len.size >ConstantValue.sendMaxSize)
            {
                toast(R.string.nomorecharacters)
                return msgId.toString()
            }

            var friendMiPublic = RxEncodeTool.base64Decode(FriendMiPublicKey)
            LogUtil.addLog("sendMsg2 friendKey:",FriendMiPublicKey)
            var msgMap = LibsodiumUtil.EncryptSendMsg(Msg,friendMiPublic)
            var msgData = SendMsgReqV3(FromIndex!!, ToIndex!!, msgMap.get("encryptedBase64")!!,msgMap.get("signBase64")!!,msgMap.get("NonceBase64")!!,msgMap.get("dst_shared_key_Mi_My64")!!)

            var baseData = BaseData(3,msgData)
            msgId = baseData.msgid!!
            if (ConstantValue.curreantNetworkType.equals("WIFI")) {
                AppConfig.instance.getPNRouterServiceMessageSender().sendChatMsg(baseData)

            }else if (ConstantValue.isToxConnected) {
                var baseDataJson = baseData.baseDataToJson().replace("\\", "")
                if (ConstantValue.isAntox) {
                    var friendKey: FriendKey = FriendKey(ConstantValue.currentRouterId.substring(0, 64))
                    MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL)
                }else{
                    ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.currentRouterId.substring(0, 64))
                }
            }
            return msgId.toString()
        }catch (e:Exception)
        {
            LogUtil.addLog("sendMsg2 错误:",e.toString())
            toast(R.string.Encryptionerror)
            chatFragment?.removeLastMessage()
        }
        return msgId.toString()
    }
    override fun sendMsgRsp(sendMsgRsp: JSendMsgRsp) {
        chatFragment?.upateMessage(sendMsgRsp)
        //todo
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        needFront = true
        KLog.i("insertMessage:ChatActivity_onCreate"+chatFragment)
        toChatUserID = intent.extras!!.getString(EaseConstant.EXTRA_USER_ID)
        receiveFileDataMap = ConcurrentHashMap<String, JPushFileMsgRsp>()
        receiveToxFileDataMap = ConcurrentHashMap<String, JPushFileMsgRsp>()
        super.onCreate(savedInstanceState)

    }

    override fun initView() {
        setContentView(R.layout.activity_chat)
        //禁止截屏
        var screenshotsSettingFlag = SpUtil.getString(AppConfig.instance, ConstantValue.screenshotsSetting, "1")
        if(screenshotsSettingFlag.equals("1"))
        {
            window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
        }else{
            window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
        }
        activityInstance = this
        //user or group id
        AppConfig.instance.isChatWithFirend = toChatUserID
        chatFragment = EaseChatFragment()
        //set arguments
        chatFragment?.setArguments(intent.extras)
        chatFragment?.setChatUserId(toChatUserID)
        supportFragmentManager.beginTransaction().add(R.id.container, chatFragment!!).commit()
        val llp = LinearLayout.LayoutParams(UIUtils.getDisplayWidth(this), UIUtils.getStatusBarHeight(this))
        view1.setLayoutParams(llp)
        parentLayout.getViewTreeObserver().addOnGlobalLayoutListener(this@ChatActivity)
        queryFriend(toChatUserID!!)
    }

    override fun initData() {
        try {
            if(AppConfig.instance.messageReceiver != null)
                AppConfig.instance.messageReceiver!!.chatCallBack = this
        }catch (e:Exception)
        {

        }
        standaloneCoroutine = launch(CommonPool) {
            delay(10000)
        }
        val userId = SpUtil.getString(this, ConstantValue.userId, "")
        var pullMsgList = PullMsgReq(userId!!, toChatUserID!!, 0, 0, 10)
        var sendData = BaseData(5,pullMsgList)
        if(ConstantValue.encryptionType.equals("1"))
        {
            sendData = BaseData(5,pullMsgList)
        }
        if (ConstantValue.isWebsocketConnected) {
            AppConfig.instance.getPNRouterServiceMessageSender().send(sendData)
        }else if (ConstantValue.isToxConnected) {
            var baseData = sendData
            var baseDataJson = baseData.baseDataToJson().replace("\\", "")
            if (ConstantValue.isAntox) {
                var friendKey: FriendKey = FriendKey(ConstantValue.currentRouterId.substring(0, 64))
                MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL)
            }else{
                ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.currentRouterId.substring(0, 64))
            }
        }
        EventBus.getDefault().register(this)

        var fileBase58Name = Base58.encode( RxEncodeTool.base64Decode(UserDataManger.curreantfriendUserData!!.signPublicKey))
        var filePath  = PathUtils.getInstance().filePath.toString() + "/" + fileBase58Name + ".jpg"
        var fileMD5 = FileUtil.getFileMD5(File(filePath))
        if(fileMD5 == null)
        {
            fileMD5 = ""
        }
        val updateAvatarReq = UpdateAvatarReq(userId!!, UserDataManger.curreantfriendUserData!!.userId, fileMD5)
        if (ConstantValue.isWebsocketConnected) {
            AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(4, updateAvatarReq))
        } else if (ConstantValue.isToxConnected) {
            val baseData = BaseData(4, updateAvatarReq)
            val baseDataJson = JSONObject.toJSON(baseData).toString().replace("\\", "")
            if (ConstantValue.isAntox) {
                val friendKey = FriendKey(ConstantValue.currentRouterId.substring(0, 64))
                MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL)
            } else {
                ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.currentRouterId.substring(0, 64))
            }
        }
        view1.postDelayed({
            if (SpUtil.getInt(this@ChatActivity, ConstantValue.realKeyboardHeight, 0) == 0) {
                chatFragment?.inputMenu?.chatPrimaryMenu?.showKeyBorad()
            }
        }, 300)
        initPictureSelector()
    }

    private fun initPictureSelector() {
        PictureSelector.create(this)
                .openGallery(PictureMimeType.ofAll())
                .maxSelectNum(9)
                .minSelectNum(1)
                .imageSpanCount(3)
                .selectionMode(PictureConfig.MULTIPLE)
                .previewImage(true)
                .previewVideo(true)
                .enablePreviewAudio(false)
                .isCamera(false)
                .imageFormat(PictureMimeType.PNG)
                .isZoomAnim(true)
                .sizeMultiplier(0.5f)
                .setOutputCameraPath("/CustomPath")
                .enableCrop(false)
                .compress(false)
                .glideOverride(160, 160)
                .hideBottomControls(false)
                .isGif(false)
                .openClickSound(false)
                .minimumCompressSize(100)
                .synOrAsy(true)
                .rotateEnabled(true)
                .scaleEnabled(true)
                .videoMaxSecond(60 * 60 * 3)
                .videoMinSecond(1)
                .isDragFrame(false)
                .setPictureLongClick(this)
    }
    private var isCanShotNetCoonect = true
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun connectNetWorkStatusChange(statusChange: ConnectStatus) {
        when (statusChange.status) {
            0 -> {
                progressDialog.hide()
                isCanShotNetCoonect = true
            }
            1 -> {

            }
            2 -> {
                if(isCanShotNetCoonect)
                {
                    //showProgressDialog(getString(R.string.network_reconnecting))
                    isCanShotNetCoonect = false
                }
            }
            3 -> {
                if(isCanShotNetCoonect)
                {
                    //showProgressDialog(getString(R.string.network_reconnecting))
                    isCanShotNetCoonect = false
                }
            }
        }
    }

    override fun onDestroy() {
        try {
            var PicAndVideoTempPath = Environment.getExternalStorageDirectory().toString() + ConstantValue.localPath + "/PicAndVideoTemp"
            DeleteUtils.deleteDirectorySubs(PicAndVideoTempPath)
            AppConfig.instance.messageReceiver!!.chatCallBack = null
            AppConfig.instance.isChatWithFirend = null
            activityInstance = null
            EventBus.getDefault().unregister(this)
            super.onDestroy()
        }catch (e :Exception)
        {

        }
    }

    override fun onNewIntent(intent: Intent) {
        // enter to chat activity when click notification bar, here make sure only one chat activiy
        val username = intent.getStringExtra("userId")
        if (toChatUserID == username)
            super.onNewIntent(intent)
        else {
            finish()
            startActivity(intent)
        }

    }

    override fun onBackPressed() {
        chatFragment?.onBackPressed()
    }

    fun getToChatUsername(): String {
        return toChatUserID!!
    }

    override fun setupActivityComponent() {
        DaggerChatComponent
                .builder()
                .appComponent((application as AppConfig).applicationComponent)
                .chatModule(ChatModule(this))
                .build()
                .inject(this)
    }

    override fun setPresenter(presenter: ChatContract.ChatContractPresenter) {
        mPresenter = presenter as ChatPresenter
    }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }

    internal var handler: Handler = object : Handler() {
        override fun handleMessage(msg: android.os.Message) {
            when (msg.what) {
                0x404 -> {

                }
                0x55 -> {
                    try {
                        var data:Bundle = msg.data;
                        var msgId = data.getInt("msgID")
                        var jPushFileMsgRsp:JPushFileMsgRsp = receiveFileDataMap.get(msgId.toString())!!
                        var fileName:String = jPushFileMsgRsp.params.fileName;
                        var fromId = jPushFileMsgRsp.params.fromId;
                        var toId = jPushFileMsgRsp.params.toId
                        var FileType = jPushFileMsgRsp.params.fileType
                        var timeStamp = (System.currentTimeMillis() / 1000).toInt()
                        if (jPushFileMsgRsp.params.fileInfo != null) {
                            chatFragment?.receiveFileMessage(fileName,msgId.toString(),fromId,toId,FileType, jPushFileMsgRsp.params.fileInfo,timeStamp)
                        } else {
                            chatFragment?.receiveFileMessage(fileName,msgId.toString(),fromId,toId,FileType, "",timeStamp)
                        }
                        receiveFileDataMap.remove(msgId.toString())
                    }catch (e:Exception)
                    {
                        e.printStackTrace()
                    }

                }
                MyAuthCallback.MSG_UPD_DATA -> {
                    KLog.i("收到了组播的回复了 ")
                    var obj:String = msg.obj.toString()
                    if(!obj.equals(""))
                    {
                        var objArray = obj.split("##")
                        var index = 0;
                        for(item in objArray)
                        {
                            if(!item.equals(""))
                            {
                                try {
                                    var udpData = AESCipher.aesDecryptString(objArray[index],"slph\$%*&^@-78231")
                                    var udpRouterArray = udpData.split(";")

                                    if(udpRouterArray.size > 1)
                                    {
                                        println("ipdizhi:"+udpRouterArray[1] +" ip: "+udpRouterArray[0])
                                        //ConstantValue.updRouterData.put(udpRouterArray[1],udpRouterArray[0])
                                        if(scanType == 1)//不是admin二维码
                                        {
                                            if(!ConstantValue.scanRouterId.equals("") && ConstantValue.scanRouterId.equals(udpRouterArray[1]))
                                            {
                                                ConstantValue.currentRouterIp = udpRouterArray[0]
                                                ConstantValue.localCurrentRouterIp = ConstantValue.currentRouterIp
                                                ConstantValue.port= ":18006"
                                                ConstantValue.filePort = ":18007"
                                                ConstantValue.currentRouterId = ConstantValue.scanRouterId
                                                ConstantValue.currentRouterSN =  ConstantValue.scanRouterSN
                                                break;
                                            }else if(!routerId.equals("") && routerId.equals(udpRouterArray[1]))
                                            {
                                                ConstantValue.currentRouterIp = udpRouterArray[0]
                                                ConstantValue.localCurrentRouterIp = ConstantValue.currentRouterIp
                                                ConstantValue.port= ":18006"
                                                ConstantValue.filePort = ":18007"
                                                ConstantValue.currentRouterId = routerId
                                                ConstantValue.currentRouterSN =  userSn
                                                break;
                                            }
                                        }else{
                                            ConstantValue.curreantNetworkType = "WIFI"
                                            ConstantValue.currentRouterIp = udpRouterArray[0]
                                            ConstantValue.localCurrentRouterIp = ConstantValue.currentRouterIp
                                            ConstantValue.currentRouterId = ConstantValue.scanRouterId
                                            ConstantValue.currentRouterSN =  ConstantValue.scanRouterSN
                                            ConstantValue.port= ":18006"
                                            ConstantValue.filePort = ":18007"
                                            ConstantValue.currentRouterMac = RouterMacStr
                                            break;
                                        }


                                    }
                                }catch (e:Exception)
                                {
                                    e.printStackTrace()
                                }

                            }
                            index ++

                        }
                        if(ConstantValue.currentRouterIp != null  && !ConstantValue.currentRouterIp.equals(""))
                        {
                            ConstantValue.curreantNetworkType = "WIFI"
                            if(isFromScan || isFromScanAdmim)
                            {
                                if(ConstantValue.isHasWebsocketInit)
                                {
                                    AppConfig.instance.getPNRouterServiceMessageReceiver().reConnect()
                                }else{
                                    ConstantValue.isHasWebsocketInit = true
                                    AppConfig.instance.getPNRouterServiceMessageReceiver(true)
                                }
                                isStartWebsocket = true
                                KLog.i("没有初始化。。设置loginBackListener"+_this)
                                //AppConfig.instance.messageReceiver!!.loginBackListener = _this
                            }

                        }
                    }

                }
            }//goMain();
            //goMain();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun addMenu(addMenu: AddMenu) {
        Thread(Runnable {
            if(image != null)
            {
                try {
                    val obmp = ((image!! as ImageView).getDrawable() as BitmapDrawable).bitmap
                    val list = java.util.ArrayList<String>()
                    list.add("Save Image")
                    hasQRCode = QRCodeDecoder.syncDecodeQRCode(obmp)
                    if (hasQRCode != null && hasQRCode != "") {
                        list.add("Scan QR Code in Image")
                        runOnUiThread {
                            PopWindowUtil.showSelecMenuPopWindowNotice(list);
                        }
                        isAddMenu = 0;
                    }else{
                        if(isAddMenu < 1)
                        {
                            EventBus.getDefault().post(AddMenu())
                            isAddMenu ++;
                        }
                    }
                }catch (e:Exception)
                {
                  e.printStackTrace()
                    if(isAddMenu < 1)
                    {
                        EventBus.getDefault().post(AddMenu())
                        isAddMenu ++;
                    }
                }
            }


        }).start()
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onWebSocketConnected(connectStatus: ConnectStatus) {
        KLog.i("websocket状态MainActivity:" + connectStatus.status)
        if (connectStatus.status != 0) {
            resetUnCompleteFileRecode()
            EventBus.getDefault().post(AllFileStatus())
        }
        when (connectStatus.status) {
            0 -> {
                if(standaloneCoroutine != null)
                    standaloneCoroutine.cancel()
                ConstantValue.isHasWebsocketInit = true
                if(isFromScanAdmim)
                {
                    runOnUiThread {
                        closeProgressDialog()
                    }
                    gotoActivity(1)
                    /* var intent = Intent(this, AdminLoginActivity::class.java)
                     startActivity(intent)*/
                    /*closeProgressDialog()
                    showProgressDialog("wait...")
                    var recovery = RecoveryReq( ConstantValue.currentRouterId, ConstantValue.currentRouterSN)
                    AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(2,recovery))*/
                    isFromScanAdmim = false
//                    finish()
                }
                else if(isFromScan)
                {
                    runOnUiThread {
                        closeProgressDialog()
                        showProgressDialog("wait...")
                    }
                    var pulicMiKey = ConstantValue.libsodiumpublicSignKey!!
                    var recovery = RecoveryReq( ConstantValue.currentRouterId, ConstantValue.currentRouterSN,pulicMiKey)
                    AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(4,recovery))
                    isFromScan = false
                }else{
                    if(isClickLogin)
                    {
                        KLog.i("开始用websocket登录路由器")
                        loginBack = false
                        runOnUiThread {
                            showProgressDialog(getString(R.string.login_))
                        }
                        standaloneCoroutine = launch(CommonPool) {
                            delay(10000)
                            if (!loginBack) {
                                runOnUiThread {
                                    closeProgressDialog()
                                    isloginOutTime = true
                                    toast("login time out")
                                    gotoLogin()
                                }
                            }
                        }
//            standaloneCoroutine.cancel()

                        //var login = LoginReq( routerId,userSn, userId,LoginKeySha, dataFileVersion)
                        var sign = ByteArray(32)
                        var time = (System.currentTimeMillis() /1000).toString().toByteArray()
                        System.arraycopy(time, 0, sign, 0, time.size)
                        var dst_signed_msg = ByteArray(96)
                        var signed_msg_len = IntArray(1)
                        var mySignPrivate  = RxEncodeTool.base64Decode(ConstantValue.libsodiumprivateSignKey)
                        var crypto_sign = Sodium.crypto_sign(dst_signed_msg,signed_msg_len,sign,sign.size,mySignPrivate)
                        var signBase64 = RxEncodeTool.base64Encode2String(dst_signed_msg)
                        val NickName = RxEncodeTool.base64Encode2String(username.toByteArray())
                        //var login = LoginReq( routerId,userSn, userId,LoginKeySha, dataFileVersion)
                        KLog.i("没有初始化。。登录接口设置loginBackListener"+"##" +AppConfig.instance.name +"##"+this.name+"##"+AppConfig.instance.messageReceiver)
                        //AppConfig.instance.messageReceiver!!.loginBackListener = this
                        var login = LoginReq_V4(routerId,userSn, userId,signBase64, dataFileVersion,NickName)
                        ConstantValue.loginReq = login
                        AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(4,login))
                    }

                }

            }
            1 -> {

            }
            2 -> {

            }
            3 -> {
                runOnUiThread {
                    closeProgressDialog()
                    toast(R.string.Network_error)
                }

            }
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onToxSendInfoEvent(toxSendInfoEvent: ToxSendInfoEvent) {
        LogUtil.addLog("Tox发送消息："+toxSendInfoEvent.info)
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onToxFriendStatusEvent(toxFriendStatusEvent: ToxFriendStatusEvent) {

        if(toxFriendStatusEvent.status == 1)
        {

            ConstantValue.freindStatus = 1
            if(!threadInit)
            {
                Thread(Runnable() {
                    run() {

                        while (true)
                        {
                            if(ConstantValue.unSendMessage.size >0)
                            {
                                for (key in ConstantValue.unSendMessage.keys)
                                {
                                    var sendData = ConstantValue.unSendMessage.get(key)
                                    var friendId = ConstantValue.unSendMessageFriendId.get(key)
                                    var sendCount:Int = ConstantValue.unSendMessageSendCount.get(key) as Int
                                    if(sendCount < 5)
                                    {
                                        if (ConstantValue.isAntox) {
                                            var friendKey: FriendKey = FriendKey(routerId.substring(0, 64))
                                            MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, sendData, ToxMessageType.NORMAL)
                                        }else{
                                            ToxCoreJni.getInstance().senToxMessage(sendData, friendId)
                                        }
                                        ConstantValue.unSendMessageSendCount.put(key,sendCount++)
                                    }else{
                                        closeProgressDialog()
                                        break
                                    }
                                }

                            }else{
                                closeProgressDialog()
                                break
                            }
                            Thread.sleep(2000)
                        }

                    }
                }).start()
                threadInit = true
            }


            LogUtil.addLog("P2P检测路由好友上线，可以发消息:","LoginActivityActivity")
        }else{
            ConstantValue.freindStatus = 0;
            LogUtil.addLog("P2P检测路由好友未上线，不可以发消息:","LoginActivityActivity")
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onStopTox(stopTox: StopTox) {
        try {
            MessageHelper.clearAllMessage()
        }catch (e:Exception)
        {
            e.printStackTrace()
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onToxConnected(toxStatusEvent: ToxStatusEvent) {
        KLog.i("tox状态MainActivity:" + toxStatusEvent.status)
        if (toxStatusEvent.status != 0) {
            resetUnCompleteFileRecode()
            EventBus.getDefault().post(AllFileStatus())
        }
        when (toxStatusEvent.status) {
            0 -> {
                KLog.i("P2P连接成功")
                LogUtil.addLog("P2P连接成功:","LoginActivityActivity")
                runOnUiThread {
                    KLog.i("444")
                    closeProgressDialog()
                    //toast("login time out")
                }
                ConstantValue.isToxConnected = true

                if(ConstantValue.curreantNetworkType.equals("TOX"))
                {
                    ConstantValue.isHasWebsocketInit = true
                    AppConfig.instance.getPNRouterServiceMessageReceiver()
                    KLog.i("没有初始化。。设置loginBackListener")
                    //AppConfig.instance.messageReceiver!!.loginBackListener = this
                }
                if( stopTox ||  ConstantValue.curreantNetworkType.equals("WIFI"))
                    return
                if(isFromScan)
                {

                    if (ConstantValue.isAntox) {
                        InterfaceScaleUtil.addFriend( ConstantValue.scanRouterId,this)
                    }else{
                        ToxCoreJni.getInstance().addFriend( ConstantValue.scanRouterId)
                    }
                    standaloneCoroutine = launch(CommonPool) {
                        delay(60000)
                        if (!loginBack) {
                            runOnUiThread {
                                closeProgressDialog()
                                toast("time out")
                                gotoLogin()
                            }
                        }
                    }

                    runOnUiThread {
                        var tips = getString(R.string.login_)
                        if(ConstantValue.freindStatus == 1)
                        {
                            tips = "wait..."
                        }else{
                            tips = "circle connecting..."
                        }
                        showProgressDialog(tips, DialogInterface.OnKeyListener { dialog, keyCode, event ->
                            if (keyCode == KeyEvent.KEYCODE_BACK) {
                                if(standaloneCoroutine != null)
                                    standaloneCoroutine.cancel()
                                EventBus.getDefault().post(StopTox())
                                gotoLogin()
                                false
                            } else false
                        })
                    }
                    var pulicMiKey = ConstantValue.libsodiumpublicSignKey!!
                    var recovery = RecoveryReq( ConstantValue.scanRouterId, ConstantValue.scanRouterSN,pulicMiKey)
                    var baseData = BaseData(4,recovery)
                    var baseDataJson = baseData.baseDataToJson().replace("\\", "")

                    ConstantValue.unSendMessage.put("recovery",baseDataJson)
                    ConstantValue.unSendMessageFriendId.put("recovery",ConstantValue.scanRouterId.substring(0, 64))
                    ConstantValue.unSendMessageSendCount.put("recovery",0)
                    //ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.scanRouterId.substring(0, 64))
                    isFromScan = false
                }else{
                    if (ConstantValue.isAntox) {
                        InterfaceScaleUtil.addFriend(routerId,this)
                    }else{
                        ToxCoreJni.getInstance().addFriend(routerId)
                    }
                    if(isClickLogin)
                    {
                        //var friendKey:FriendKey = FriendKey(routerId.substring(0, 64))
                        loginBack = false

                        standaloneCoroutine = launch(CommonPool) {
                            delay(60000)
                            if (!loginBack) {
                                runOnUiThread {
                                    closeProgressDialog()
                                    isloginOutTime = true
                                    toast("login time out")
                                    gotoLogin()
                                }
                            }
                        }
                        runOnUiThread {
                            var tips = getString(R.string.login_)
                            if(ConstantValue.freindStatus == 1)
                            {
                                tips = getString(R.string.login_)
                            }else{
                                tips = "Circle connecting..."
                            }
                            showProgressDialog(tips, DialogInterface.OnKeyListener { dialog, keyCode, event ->
                                if (keyCode == KeyEvent.KEYCODE_BACK) {
                                    if(standaloneCoroutine != null)
                                        standaloneCoroutine.cancel()
                                    EventBus.getDefault().post(StopTox())
                                    gotoLogin()
                                    false
                                } else false
                            })
                        }
                        var sign = ByteArray(32)
                        var time = (System.currentTimeMillis() /1000).toString().toByteArray()
                        System.arraycopy(time, 0, sign, 0, time.size)
                        var dst_signed_msg = ByteArray(96)
                        var signed_msg_len = IntArray(1)
                        var mySignPrivate  = RxEncodeTool.base64Decode(ConstantValue.libsodiumprivateSignKey)
                        var crypto_sign = Sodium.crypto_sign(dst_signed_msg,signed_msg_len,sign,sign.size,mySignPrivate)
                        var signBase64 = RxEncodeTool.base64Encode2String(dst_signed_msg)
                        val NickName = RxEncodeTool.base64Encode2String(username.toByteArray())
                        //var login = LoginReq( routerId,userSn, userId,LoginKeySha, dataFileVersion)
                        var login = LoginReq_V4(routerId,userSn, userId,signBase64, dataFileVersion,NickName)
                        ConstantValue.loginReq = login
                        var baseData = BaseData(4,login)
                        var baseDataJson = baseData.baseDataToJson().replace("\\", "")
                        ConstantValue.unSendMessage.put("login",baseDataJson)
                        ConstantValue.unSendMessageFriendId.put("login",routerId.substring(0, 64))
                        ConstantValue.unSendMessageSendCount.put("login",0)
                        //ToxCoreJni.getInstance().senToxMessage(baseDataJson, routerId.substring(0, 64))
                        //MessageHelper.sendMessageFromKotlin(this, friendKey, baseDataJson, ToxMessageType.NORMAL)
                        isClickLogin = false;
                    }

                }
            }
            1 -> {
                LogUtil.addLog("P2P连接中Reconnecting:","LoginActivityActivity")
            }
        }
    }
    fun initSwitchData() {


    }
    fun gotoLogin()
    {
        closeProgressDialog()
        ConstantValue.unSendMessage.remove("login")
        ConstantValue.unSendMessageFriendId.remove("login")
        ConstantValue.unSendMessageSendCount.remove("login")
        ConstantValue.isHasWebsocketInit = true
        if(AppConfig.instance.messageReceiver != null)
            AppConfig.instance.messageReceiver!!.close()

        ConstantValue.loginOut = true
        ConstantValue.logining = false
        ConstantValue.currentRouterIp = ""
        if (ConstantValue.isWebsocketConnected) {
            FileMangerDownloadUtils.init()
            ConstantValue.webSockeFileMangertList.forEach {
                it.disconnect(true)
                //ConstantValue.webSockeFileMangertList.remove(it)
            }
            ConstantValue.webSocketFileList.forEach {
                it.disconnect(true)
                //ConstantValue.webSocketFileList.remove(it)
            }
        }else{
            val intentTox = Intent(AppConfig.instance, KotlinToxService::class.java)
            AppConfig.instance.stopService(intentTox)
        }
        ConstantValue.loginReq = null
        ConstantValue.isWebsocketReConnect = false
        ConstantValue.hasLogin = false
        ConstantValue.isHeart = false
        resetUnCompleteFileRecode()
        AppConfig.instance.mAppActivityManager.finishAllActivityWithoutThis()
        var intent = Intent(AppConfig.instance, LoginActivityActivity::class.java)
        intent.putExtra("flag", "logout")
        startActivity(intent)
        finish()
    }
    private fun startTox(startToxFlag:Boolean)
    {
        ConstantValue.curreantNetworkType = "TOX"
        stopTox = false
        if(!ConstantValue.isToxConnected && startToxFlag)
        {
            runOnUiThread {
                showProgressDialog("p2p connecting...", DialogInterface.OnKeyListener { dialog, keyCode, event ->
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        stopTox = true
                        gotoLogin()
                        false
                    } else false
                })
            }
            LogUtil.addLog("P2P启动连接:","LoginActivityActivity")
            var intent = Intent(AppConfig.instance, KotlinToxService::class.java)
            if(ConstantValue.isAntox)
            {
                intent = Intent(AppConfig.instance, ToxService::class.java)
            }
            startService(intent)
        }else{
            runOnUiThread {
                KLog.i("666")
                closeProgressDialog()
            }
        }
    }
    private fun startLogin()
    {

        isloginOutTime = false
        isStartLogin = true
        if(!ConstantValue.lastNetworkType.equals(""))
        {
            isFromScan = false
            ConstantValue.curreantNetworkType = ConstantValue.lastNetworkType
            ConstantValue.currentRouterIp = ConstantValue.lastRouterIp
            ConstantValue.port = ConstantValue.lastPort
            ConstantValue.filePort = ConstantValue.lastFilePort
            ConstantValue.currentRouterId = ConstantValue.lastRouterId
            ConstantValue.currentRouterSN =  ConstantValue.lastRouterSN
            ConstantValue.lastRouterId=""
            ConstantValue.lastPort=""
            ConstantValue.lastFilePort=""
            ConstantValue.lastRouterId=""
            ConstantValue.lastRouterSN=""
            ConstantValue.lastNetworkType =""
        }
        /* if (loginKey.text.toString().equals("")) {
             toast(getString(R.string.please_type_your_password))
             return
         }*/
        if( ConstantValue.curreantNetworkType.equals("TOX"))
        {

            if(ConstantValue.isToxConnected)
            {
                isToxLoginOverTime = true
                //var friendKey:FriendKey = FriendKey(routerId.substring(0, 64))

                //var login = LoginReq( routerId,userSn, userId,LoginKeySha, dataFileVersion)
                var sign = ByteArray(32)
                var time = (System.currentTimeMillis() /1000).toString().toByteArray()
                System.arraycopy(time, 0, sign, 0, time.size)
                var dst_signed_msg = ByteArray(96)
                var signed_msg_len = IntArray(1)
                var mySignPrivate  = RxEncodeTool.base64Decode(ConstantValue.libsodiumprivateSignKey)
                var crypto_sign = Sodium.crypto_sign(dst_signed_msg,signed_msg_len,sign,sign.size,mySignPrivate)
                var signBase64 = RxEncodeTool.base64Encode2String(dst_signed_msg)
                val NickName = RxEncodeTool.base64Encode2String(username.toByteArray())
                //var login = LoginReq( routerId,userSn, userId,LoginKeySha, dataFileVersion)
                var login = LoginReq_V4(routerId,userSn, userId,signBase64, dataFileVersion,NickName)
                ConstantValue.loginReq = login
                var baseData = BaseData(4,login)
                var baseDataJson = baseData.baseDataToJson().replace("\\", "")
                KLog.i("没有初始化。。设置loginBackListener")
                //AppConfig.instance.messageReceiver!!.loginBackListener = this
                standaloneCoroutine = launch(CommonPool) {
                    delay(60000)
                    if (!loginBack) {
                        runOnUiThread {
                            closeProgressDialog()
                            isloginOutTime = true
                            toast("login time out")
                            gotoLogin()
                        }
                    }
                }
                runOnUiThread {
                    var tips = getString(R.string.login_)
                    if(ConstantValue.freindStatus == 1)
                    {
                        tips = getString(R.string.login_)
                    }else{
                        tips = "Circle connecting..."
                    }
                    showProgressDialog(tips, DialogInterface.OnKeyListener { dialog, keyCode, event ->
                        if (keyCode == KeyEvent.KEYCODE_BACK) {
                            if(standaloneCoroutine != null)
                                standaloneCoroutine.cancel()
                            EventBus.getDefault().post(StopTox())
                            gotoLogin()
                            false
                        } else false
                    })
                }
                if (ConstantValue.isAntox) {
                    var friendKey: FriendKey = FriendKey(routerId.substring(0, 64))
                    MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL)
                }else{
                    ToxCoreJni.getInstance().senToxMessage(baseDataJson, routerId.substring(0, 64))
                }
            }else{
//                    if (!ConstantValue.isToxConnected) {
//                        loadLibrary()
//                    }
                isToxLoginOverTime = true
                isClickLogin = true
                stopTox = false
                ConstantValue.curreantNetworkType = "TOX"
                runOnUiThread {
                    showProgressDialog("p2p connecting...", DialogInterface.OnKeyListener { dialog, keyCode, event ->
                        if (keyCode == KeyEvent.KEYCODE_BACK) {
                            stopTox = true
                            gotoLogin()
                            false
                        } else false
                    })
                }
                LogUtil.addLog("P2P启动连接:","LoginActivityActivity")

                if(ConstantValue.isAntox)
                {
                    var intent = Intent(AppConfig.instance, ToxService::class.java)
                    startService(intent)
                }else{
                    var intent = Intent(AppConfig.instance, KotlinToxService::class.java)
                    startService(intent)
                }

            }

        }else{
            isClickLogin = true
            if(!ConstantValue.isWebsocketConnected)
            {
                if (intent.hasExtra("flag")) {
                    if(ConstantValue.isHasWebsocketInit)
                    {
                        KLog.i("已经初始化了，走重连逻辑")
                        AppConfig.instance.getPNRouterServiceMessageReceiver().reConnect()
                    }else{
                        KLog.i("没有初始化。。")
                        ConstantValue.isHasWebsocketInit = true
                        AppConfig.instance.getPNRouterServiceMessageReceiver(true)
                    }
                    KLog.i("没有初始化。。设置loginBackListener")
                    //AppConfig.instance.getPNRouterServiceMessageReceiver().loginBackListener = this
                    standaloneCoroutine = launch(CommonPool) {
                        delay(6000)
                        runOnUiThread {
                            closeProgressDialog()
                            if (!ConstantValue.isWebsocketConnected) {
                                if(AppConfig.instance.messageReceiver != null)
                                    AppConfig.instance.messageReceiver!!.close()
                                toast("Server connection timeout")
                                gotoLogin()
                            }
                        }
                    }
                } else {
                    KLog.i("走不带flag")
                    if(ConstantValue.isHasWebsocketInit)
                    {
                        KLog.i("已经初始化了，走重连逻辑")
                        KLog.i("已经初始化了。。走重连逻辑" +this+ "##" +AppConfig.instance.messageReceiver)
                        AppConfig.instance.getPNRouterServiceMessageReceiver(true).reConnect()
                    }else{
                        KLog.i("没有初始化。。")
                        ConstantValue.isHasWebsocketInit = true
                        KLog.i("没有初始化。。设置loginBackListener前" +this+ "##" +AppConfig.instance.messageReceiver)
                        AppConfig.instance.getPNRouterServiceMessageReceiver(true)
                    }
                    KLog.i("没有初始化。。设置loginBackListener前" +this+ "##" +AppConfig.instance.name)
                    //AppConfig.instance.messageReceiver!!.loginBackListener = this
                    KLog.i("没有初始化。。设置loginBackListener 后" + AppConfig.instance.messageReceiver!!.loginBackListener +"##" +AppConfig.instance.name)
                    KLog.i("没有初始化。。设置loginBackListener 后" + AppConfig.instance.messageReceiver!! + "##" +AppConfig.instance.name)
                    standaloneCoroutine = launch(CommonPool) {
                        delay(6000)
                        runOnUiThread {
                            closeProgressDialog()
                            if (!ConstantValue.isWebsocketConnected) {
                                if(AppConfig.instance.messageReceiver != null)
                                    AppConfig.instance.messageReceiver!!.close()
                                toast("Server connection timeout")
                                gotoLogin()
                            }
                        }
                    }
                }
            }else{
                //var login = LoginReq( routerId,userSn, userId,LoginKeySha, dataFileVersion)
                var sign = ByteArray(32)
                var time = (System.currentTimeMillis() /1000).toString().toByteArray()
                System.arraycopy(time, 0, sign, 0, time.size)
                var dst_signed_msg = ByteArray(96)
                var signed_msg_len = IntArray(1)
                var mySignPrivate  = RxEncodeTool.base64Decode(ConstantValue.libsodiumprivateSignKey)
                var crypto_sign = Sodium.crypto_sign(dst_signed_msg,signed_msg_len,sign,sign.size,mySignPrivate)
                var signBase64 = RxEncodeTool.base64Encode2String(dst_signed_msg)
                val NickName = RxEncodeTool.base64Encode2String(username.toByteArray())
                //var login = LoginReq( routerId,userSn, userId,LoginKeySha, dataFileVersion)
                var login = LoginReq_V4(routerId,userSn, userId,signBase64, dataFileVersion,NickName)
                ConstantValue.loginReq = login
                standaloneCoroutine = launch(CommonPool) {
                    delay(10000)
                    if (!loginBack) {
                        runOnUiThread {
                            closeProgressDialog()
                            isloginOutTime = true
                            toast("login time out")
                            gotoLogin()
                        }
                    }
                }
                runOnUiThread {
                    showProgressDialog(getString(R.string.login_))
                }
                KLog.i("没有初始化。。设置loginBackListener")
                //AppConfig.instance.messageReceiver!!.loginBackListener = this
                AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(4,login))
            }
        }

    }
    fun gotoActivity(index:Int)
    {
        ConstantValue.unSendMessage.remove("login")
        ConstantValue.unSendMessageFriendId.remove("login")
        ConstantValue.unSendMessageSendCount.remove("login")
        ConstantValue.isHasWebsocketInit = true

        if (ConstantValue.isWebsocketConnected) {
            FileMangerDownloadUtils.init()
            ConstantValue.webSockeFileMangertList.forEach {
                it.disconnect(true)
                //ConstantValue.webSockeFileMangertList.remove(it)
            }
            ConstantValue.webSocketFileList.forEach {
                it.disconnect(true)
                //ConstantValue.webSocketFileList.remove(it)
            }
        }else{
            val intentTox = Intent(AppConfig.instance, KotlinToxService::class.java)
            AppConfig.instance.stopService(intentTox)
        }
        resetUnCompleteFileRecode()
        AppConfig.instance.mAppActivityManager.finishAllActivityWithoutThis()
        when(index)
        {
            0->{
                var intent = Intent(AppConfig.instance, LoginActivityActivity::class.java)
                intent.putExtra("flag", "logout")
                startActivity(intent)
            }
            1->
            {
                ConstantValue.loginOut = true
                ConstantValue.logining = false
                ConstantValue.isHeart = false
                var intent = Intent(this, AdminLoginActivity::class.java)
                startActivity(intent)
            }

        }

        finish()
    }
    fun resetUnCompleteFileRecode() {
        var localFilesList = LocalFileUtils.localFilesList
        for (myFie in localFilesList) {
            if (myFie.upLoadFile.isComplete == false) {
                myFie.upLoadFile.SendGgain = true
                myFie.upLoadFile.isStop = "1"
                myFie.upLoadFile.segSeqResult = 0
                val myRouter = MyFile()
                myRouter.type = 0
                myRouter.userSn = ConstantValue.currentRouterSN
                myRouter.upLoadFile = myFie.upLoadFile
                LocalFileUtils.updateLocalAssets(myRouter)
            }
        }
    }
    private fun startToxAndRecovery() {

        ConstantValue.curreantNetworkType = "TOX"
        stopTox = false
        if (!ConstantValue.isToxConnected) {
            runOnUiThread {
                showProgressDialog("p2p connecting...", DialogInterface.OnKeyListener { dialog, keyCode, event ->
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        stopTox = true
                        gotoLogin()
                        false
                    } else false
                })
            }
            LogUtil.addLog("P2P启动连接:", "LoginActivityActivity")
            var intent = Intent(AppConfig.instance, KotlinToxService::class.java)
            if(ConstantValue.isAntox)
            {
                intent = Intent(AppConfig.instance, ToxService::class.java)
            }
            startService(intent)
        } else {
            //var friendKey: FriendKey = FriendKey(ConstantValue.scanRouterId.substring(0, 64))
            runOnUiThread {
                showProgressDialog("wait...", DialogInterface.OnKeyListener { dialog, keyCode, event ->
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        EventBus.getDefault().post(StopTox())
                        gotoLogin()
                        false
                    } else false
                })
            }
            KLog.i("没有初始化。。设置loginBackListener")
            //AppConfig.instance.messageReceiver!!.loginBackListener = this
            if (ConstantValue.isAntox) {
                InterfaceScaleUtil.addFriend( ConstantValue.scanRouterId,this)
            }else{
                ToxCoreJni.getInstance().addFriend(ConstantValue.scanRouterId)
            }
            var pulicMiKey = ConstantValue.libsodiumpublicSignKey!!
            var recovery = RecoveryReq(ConstantValue.scanRouterId, ConstantValue.scanRouterSN,pulicMiKey)
            var baseData = BaseData(4, recovery)
            var baseDataJson = baseData.baseDataToJson().replace("\\", "")
            if (ConstantValue.isAntox) {
                var friendKey: FriendKey = FriendKey(ConstantValue.scanRouterId.substring(0, 64))
                MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL)
            }else{
                ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.scanRouterId.substring(0, 64))
            }
        }
    }
    fun getMacFromRemote()
    {
        var RouterMacData = RouterMacStr.replace(":","")
        var httpUrlData = ConstantValue.httpMacUrl +"CheckByMac?mac="
        if(!BuildConfig.DEBUG)
        {
            httpUrlData = ConstantValue.httpMacUrl +"CheckByMac?mac="
        }
        OkHttpUtils.getInstance().doGet(httpUrlData + RouterMacData,  object : OkHttpUtils.OkCallback {
            override fun onFailure( e :Exception) {
                runOnUiThread {
                    closeProgressDialog()
                    RouterMacStr = ""
                    isFromScanAdmim = false
                    gotoLogin()
                    toast(R.string.Unable_to_connect_to_router)
                }
                Thread.currentThread().interrupt(); //方法调用终止线程
            }
            override fun  onResponse(json:String ) {

                val gson = GsonUtil.getIntGson()
                var httpDataMac: HttpData? = null
                try {
                    if (json != null) {
                        httpDataMac = gson.fromJson<HttpData>(json, HttpData::class.java)
                        if(httpDataMac != null  && httpDataMac.retCode == 0 && httpDataMac.connStatus == 1)
                        {
                            ConstantValue.curreantNetworkType = "WIFI"
                            ConstantValue.currentRouterIp = httpDataMac.serverHost
                            ConstantValue.port = ":"+httpDataMac.serverPort.toString()
                            ConstantValue.filePort = ":"+(httpDataMac.serverPort +1).toString()
                            ConstantValue.currentRouterMac = RouterMacStr
                            /*ConstantValue.currentRouterId = ConstantValue.scanRouterId
                            ConstantValue.currentRouterSN =  ConstantValue.scanRouterSN*/
                            if(ConstantValue.isHasWebsocketInit)
                            {
                                AppConfig.instance.getPNRouterServiceMessageReceiver().reConnect()
                            }else{
                                ConstantValue.isHasWebsocketInit = true
                                AppConfig.instance.getPNRouterServiceMessageReceiver(true)
                            }
                            //KLog.i("没有初始化。。设置loginBackListener"+this_)
                            //AppConfig.instance.messageReceiver!!.loginBackListener = this_
                            Thread.currentThread().interrupt() //方法调用终止线程
                        }else{
                            runOnUiThread {
                                closeProgressDialog()
                                RouterMacStr = ""
                                isFromScanAdmim = false
                                gotoLogin()
                                toast(R.string.Unable_to_connect_to_router)
                            }
                            Thread.currentThread().interrupt(); //方法调用终止线程
                        }

                    }
                } catch (e: Exception) {
                    runOnUiThread {
                        closeProgressDialog()
                        RouterMacStr = ""
                        isFromScanAdmim = false
                        gotoLogin()
                        toast(R.string.Unable_to_connect_to_router)
                    }
                    Thread.currentThread().interrupt(); //方法调用终止线程
                }
            }
        })
    }
}