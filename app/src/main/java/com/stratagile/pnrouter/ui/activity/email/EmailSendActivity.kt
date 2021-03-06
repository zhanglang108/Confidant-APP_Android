package com.stratagile.pnrouter.ui.activity.email

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.net.http.SslError
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v7.widget.GridLayoutManager
import android.text.Editable
import android.text.InputType
import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.hyphenate.easeui.model.EaseCompat
import com.hyphenate.easeui.utils.EaseCommonUtils
import com.hyphenate.easeui.utils.PathUtils
import com.hyphenate.easeui.widget.ATEmailEditText
import com.hyphenate.easeui.widget.TInputConnection
import com.hyphenate.util.EMLog
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.pawegio.kandroid.toast
import com.smailnet.eamil.Callback.GetSendCallback
import com.smailnet.eamil.EmailSendClient
import com.smailnet.islands.Islands
import com.socks.library.KLog
import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.ui.activity.email.component.DaggerEmailSendComponent
import com.stratagile.pnrouter.ui.activity.email.contract.EmailSendContract
import com.stratagile.pnrouter.ui.activity.email.module.EmailSendModule
import com.stratagile.pnrouter.ui.activity.email.presenter.EmailSendPresenter
import com.stratagile.pnrouter.ui.activity.email.view.ColorPickerView
import com.stratagile.pnrouter.ui.activity.email.view.RichEditor
import com.stratagile.pnrouter.ui.activity.file.SelectFileActivity
import com.stratagile.pnrouter.ui.adapter.conversation.EmaiAttachAdapter
import com.yanzhenjie.permission.AndPermission
import com.yanzhenjie.permission.PermissionListener
import kotlinx.android.synthetic.main.email_picture_image_grid_item.*
import kotlinx.android.synthetic.main.email_send_edit.*
import java.io.File
import javax.inject.Inject
import com.stratagile.pnrouter.method.*
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.webkit.*
import android.widget.EditText
import com.hyphenate.easeui.ui.EaseShowFileVideoActivity
import com.hyphenate.easeui.utils.OpenFileUtil
import com.luck.picture.lib.PicturePreviewActivity
import com.luck.picture.lib.observable.ImagesObservable
import com.message.Message
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog
import com.smailnet.eamil.Callback.GetAttachCallback
import com.smailnet.eamil.EmailReceiveClient
import com.smailnet.eamil.MailAttachment
import com.stratagile.pnrouter.data.web.PNRouterServiceMessageReceiver
import com.stratagile.pnrouter.db.*
import com.stratagile.pnrouter.entity.*
import com.stratagile.pnrouter.entity.events.ChangFragmentMenu
import com.stratagile.pnrouter.entity.events.SendEmailSuccess
import com.stratagile.pnrouter.ui.activity.file.FileChooseActivity
import com.stratagile.pnrouter.utils.*
import com.stratagile.pnrouter.view.CustomPopWindow
import com.stratagile.pnrouter.view.SweetAlertDialog
import kotlinx.android.synthetic.main.ease_chat_menu_item.view.*
import org.greenrobot.eventbus.EventBus
import org.libsodium.jni.Sodium
import java.io.ByteArrayInputStream
import java.io.Serializable


/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.email
 * @Description: $description
 * @date 2019/07/25 11:21:29
 */

class EmailSendActivity : BaseActivity(), EmailSendContract.View,View.OnClickListener, PNRouterServiceMessageReceiver.CheckmailUkeyCallback{

    @Inject
    internal lateinit var mPresenter: EmailSendPresenter
    /********************boolean开关 */
    //是否加粗
    internal var isClickBold = false
    //是否正在执行动画
    internal var isAnimating = false
    //是否按ol排序
    internal var isListOl = false
    //是否按ul排序
    internal var isListUL = false
    //是否下划线字体
    internal var isTextLean = false
    //是否下倾斜字体
    internal var isItalic = false
    //是否左对齐
    internal var isAlignLeft = false
    //是否右对齐
    internal var isAlignRight = false
    //是否中对齐
    internal var isAlignCenter = false
    //是否缩进
    internal var isIndent = false
    //是否较少缩进
    internal var isOutdent = false
    //是否索引
    internal var isBlockquote = false
    //字体中划线
    internal var isStrikethrough = false
    //字体上标
    internal var isSuperscript = false
    //字体下标
    internal var isSubscript = false

    private var onKeyDel = false

    private var ctrlPress = false
    /********************变量 */
    //折叠视图的宽高
    private var mFoldedViewMeasureHeight: Int = 0
    var emaiAttachAdapter : EmaiAttachAdapter? = null
    protected var cameraFile: File? = null
    protected var videoFile: File? = null
    protected val REQUEST_CODE_MAP = 1
    protected val REQUEST_CODE_CAMERA = 2
    protected val REQUEST_CODE_LOCAL = 3
    protected val REQUEST_CODE_DING_MSG = 4
    protected val REQUEST_CODE_FILE = 5
    protected val REQUEST_CODE_VIDEO = 6

    protected val REQUEST_CODE_TO = 101
    protected val REQUEST_CODE_CC = 102
    protected val REQUEST_CODE_BCC = 103
    protected val CHOOSE_PIC = 88 //选择原图还是压缩图
    private var imputOld: String? = null
    private val methods = arrayOf(Weibo)//arrayOf(Weibo,WeChat, QQ)
    private var iterator: Iterator<Method> = methods.iterator()
    private val methodContext = MethodContext()
    private val methodContextCc = MethodContext()
    private val methodContextBcc = MethodContext()
    var contactMapList = HashMap<String, String>()
    internal var previewImages: MutableList<LocalMedia> = java.util.ArrayList()
    private val users = arrayListOf(
            User("1", "激浊扬清"),
            User("2", "清风引佩下瑶台"),
            User("3", "浊泾清渭"),
            User("4", "刀光掩映孔雀屏"),
            User("5", "清风徐来"),
            User("6", "英雄无双风流婿"),
            User("7", "源清流洁"),
            User("8", "占断人间天上福"),
            User("9", "清音幽韵"),
            User("10", "碧箫声里双鸣凤"),
            User("11", "风清弊绝"),
            User("12", "天教艳质为眷属"),
            User("13", "独清独醒"),
            User("14", "千金一刻庆良宵"),
            User("15", "必须要\\n\n，不然不够长"))
    var flag = 0;
    var foward = 0;
    var emailMeaasgeInfoData: EmailMessageEntity? = null
    var oldAdress = ""
    var attach = 0;
    var menu:String= "INBOX"
    var attachListEntity =  arrayListOf<EmailAttachEntity>()
    var isSendCheck = false
    var toAdressEditLastContent = ""
    var ccAdressEditLastContent = ""
    var bccAdressEditLastContent = ""

    override fun checkmailUkey(jCheckmailUkeyRsp: JCheckmailUkeyRsp) {
        if(isSendCheck)
        {
            runOnUiThread {
                closeProgressDialog()
            }
        }
        if(jCheckmailUkeyRsp.params.retCode == 0)
        {
            var data = jCheckmailUkeyRsp.params.payload
            for (item in data)
            {
                var value = item.pubKey;
                val dst_public_MiKey_Friend = ByteArray(32)
                val crypto_sign_ed25519_pk_to_curve25519_result = Sodium.crypto_sign_ed25519_pk_to_curve25519(dst_public_MiKey_Friend, RxEncodeTool.base64Decode(value))
                if (crypto_sign_ed25519_pk_to_curve25519_result == 0) {
                    contactMapList.put(item.user,RxEncodeTool.base64Encode2String(dst_public_MiKey_Friend))
                }
            }
            if(isSendCheck)
            {
                sendEmail(true);
            }
           runOnUiThread {
               if(contactMapList.size > 0)
               {
                   lockTips.visibility = View.VISIBLE
               }
           }
        }else{
            contactMapList = HashMap<String, String>()
            if(isSendCheck)
            {
                sendEmail(true);
            }
            runOnUiThread {
                lockTips.visibility = View.GONE
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        needFront = true
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        setContentView(R.layout.email_send_edit)
    }
    override fun initData() {
        AppConfig.instance.messageReceiver?.checkmailUkeyCallback = this
        emailMeaasgeInfoData = intent.getParcelableExtra("emailMeaasgeInfoData")
        flag = intent.getIntExtra("flag",0)
        foward = intent.getIntExtra("foward",0)
        attach = intent.getIntExtra("attach",0)
        menu = intent.getStringExtra("menu")
        initUI()
        initClickListener()
        sendCheck(false)
    }

    /**
     * 初始化View
     */
    private fun initUI() {
        if (methodContext.method == null) {
            switch()
        }
        val selectionEnd = toAdressEdit.length()
        val selectionStart = 5
        var aa = toAdressEdit!!.getText()
        val spans = toAdressEdit!!.getText()!!.getSpans(selectionStart, selectionEnd, User::class.java)
        initEditor()
        initMenu()
        initColorPicker()
        oldtitle.visibility = View.GONE
        list_itease_layout_info.visibility = View.GONE
        sentTitle.visibility = View.VISIBLE
        if(flag == 1)
        {
            initBaseUI(emailMeaasgeInfoData!!)
            oldtitle.visibility = View.VISIBLE
            sentTitle.visibility = View.GONE
            list_itease_layout_info.visibility = View.VISIBLE
            if(foward == 1)
            {
                list_itease_layout_info.visibility = View.GONE
                sentTitle.visibility = View.VISIBLE
            }

        }
        toAdressEdit.setOnFocusChangeListener(object : View.OnFocusChangeListener {
            override fun onFocusChange( v:View,  hasFocus:Boolean) {
                if(hasFocus)
                {

                }else{
                    allSpan(toAdressEdit)
                    sendCheck(false)
                }
            }
        });
        ccAdressEdit.setOnFocusChangeListener(object : View.OnFocusChangeListener {
            override fun onFocusChange( v:View,  hasFocus:Boolean) {
                if(hasFocus)
                {

                }else{
                    allSpan(ccAdressEdit)
                    sendCheck(false)
                }
            }
        });
        bccAdressEdit.setOnFocusChangeListener(object : View.OnFocusChangeListener {
            override fun onFocusChange( v:View,  hasFocus:Boolean) {
                if(hasFocus)
                {

                }else{
                    allSpan(bccAdressEdit)
                    sendCheck(false)
                    bccAdressEditLastContent = bccAdressEdit.text.toString()
                }
            }
        });
        //根据输入框输入值的改变来过滤搜索
        toAdressEdit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable) {
                var lineCount = toAdressEdit.lineCount
                toAdressEdit.minHeight = resources.getDimension(R.dimen.x50).toInt() * lineCount
            }
        })
        //根据输入框输入值的改变来过滤搜索
        ccAdressEdit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable) {
                var lineCount = ccAdressEdit.lineCount
                ccAdressEdit.minHeight = resources.getDimension(R.dimen.x50).toInt() * lineCount
            }
        })
        //根据输入框输入值的改变来过滤搜索
        bccAdressEdit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable) {
                var lineCount = bccAdressEdit.lineCount
                bccAdressEdit.minHeight = resources.getDimension(R.dimen.x50).toInt() * lineCount
            }
        })
        /*toAddress.setOnClickListener(this)
        toAddress.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
             KLog.i("key" + "keyCode:" + keyCode + " action:" + event.action)

             // test on Mac virtual machine: ctrl map to KEYCODE_UNKNOWN
             if (keyCode == KeyEvent.KEYCODE_UNKNOWN) {
                 if (event.action == KeyEvent.ACTION_DOWN) {
                     ctrlPress = true
                 } else if (event.action == KeyEvent.ACTION_UP) {
                     ctrlPress = false
                 }
             }
             onKeyDel = true
             if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_DOWN) {
                      ATEmailEditText.KeyDownHelper(toAddress.getText())
                  } else false
         })
        toAddress.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            EMLog.d("key", "keyCode:" + event.keyCode + " action" + event.action + " ctrl:" + ctrlPress)
            if (actionId == EditorInfo.IME_ACTION_SEND || event.keyCode == KeyEvent.KEYCODE_ENTER &&
                    event.action == KeyEvent.ACTION_DOWN &&
                    ctrlPress == true) {
                val s = toAddress.getText().toString().trim({ it <= ' ' })
                toAddress.setText("")
                //listener.onSendBtnClicked(s, "")
                true
            } else {
                false
            }
        })*/
        initAttachUI()
    }
    private fun allSpan(editText: EditText)
    {

        var textStrList = editText.text.split(",")
        var i = 0
        for(str in textStrList)
        {
            if(str != "")
            {
                var beginIndex = i
                var endIndex = i+str.length
                val spans = editText!!.getText()!!.getSpans(beginIndex, endIndex, User::class.java)
                if(spans.size == 0)
                {
                    var addUser = User(str, str)
                    /* editText.text.replace(beginIndex,endIndex,methodContext.newSpannable(addUser))
                     (editText.text as SpannableStringBuilder).append(",")*/
                    /*(editText.text as SpannableStringBuilder)
                            .append(methodContext.newSpannable(addUser))
                            .append(" ")*/
                    editText.text.replace(beginIndex,endIndex,methodContext.newSpannable(addUser))
                    /*editText.text.replace(beginIndex,endIndex,"")
                    (editText.text as SpannableStringBuilder)
                            .append(methodContext.newSpannable(addUser))
                            .append(" ")*/
                }
            }
            i += str.length+1
        }
    }
    fun initBaseUI(emailMessageEntity:EmailMessageEntity)
    {
        var fromName = ""
        var fromAdress = ""
        if(emailMessageEntity!!.from.indexOf("<") >=0)
        {
            fromName = emailMessageEntity!!.from.substring(0,emailMessageEntity!!.from.indexOf("<"))
            fromAdress = emailMessageEntity!!.from.substring(emailMessageEntity!!.from.indexOf("<")+1,emailMessageEntity!!.from.length-1)
        }else{
            fromName = emailMessageEntity!!.from.substring(0,emailMessageEntity!!.from.indexOf("@"))
            fromAdress = emailMessageEntity!!.from.substring(0,emailMessageEntity!!.from.length)
        }
        var subjectText = emailMessageEntity.subject
        if(foward == 0)
        {
            if(emailMessageEntity!!.from.indexOf("<") >=0)
            {
                fromName = emailMessageEntity!!.from.substring(0,emailMessageEntity!!.from.indexOf("<"))
                fromAdress = emailMessageEntity!!.from.substring(emailMessageEntity!!.from.indexOf("<")+1,emailMessageEntity!!.from.length-1)
            }else{
                fromName = emailMessageEntity!!.from.substring(0,emailMessageEntity!!.from.indexOf("@"))
                fromAdress = emailMessageEntity!!.from.substring(0,emailMessageEntity!!.from.length)
            }
        }else if(foward == 3)
        {
            if(emailMessageEntity!!.to.indexOf("<") >=0)
            {
                fromName = emailMessageEntity!!.to.substring(0,emailMessageEntity!!.to.indexOf("<"))
                fromAdress = emailMessageEntity!!.to.substring(emailMessageEntity!!.to.indexOf("<")+1,emailMessageEntity!!.to.length-1)
            }else{
                fromName = emailMessageEntity!!.to.substring(0,emailMessageEntity!!.to.indexOf("@"))
                fromAdress = emailMessageEntity!!.to.substring(0,emailMessageEntity!!.to.length)
            }
        }
        var localEmailContacts = AppConfig.instance.mDaoMaster!!.newSession().emailContactsEntityDao.queryBuilder().where(EmailContactsEntityDao.Properties.Account.eq(fromAdress)).list()
        if(localEmailContacts.size != 0)
        {
            var localEmailContactsItem = localEmailContacts.get(0)
            fromName = localEmailContactsItem.name
        }
        avatar_info.setText(fromName.trim())
        title_info.setText(fromName.trim())
        draft_info.setText(fromAdress.trim())
        //val result = toAddress.addSpan(fromName, fromAdress)
        var aa = "";
        if(foward == 0 || foward == 3)
        {
            var user = User(fromAdress,fromName)
            (toAdressEdit.text as SpannableStringBuilder)
                    .append(methodContext.newSpannable(user))
                    .append(",")
        }
        if(subjectText != null)
        {
            if(foward == 0)
            {
                subject.setText(getString(R.string.Reply)+":"+subjectText)
            }else{
                subject.setText(subjectText)
            }

        }
        val selectionEnd = toAdressEdit.length()
        val selectionStart = 0
        val spans = toAdressEdit!!.getText()!!.getSpans(selectionStart, selectionEnd, User::class.java)
        var dd = ""

        /*var backspaceListener: TInputConnection.BackspaceListener = TInputConnection.BackspaceListener {
            val editable = toAddress.getText()

            if (editable!!.length == 0) {
                return@BackspaceListener false
            }
            if (!onKeyDel) {
                ATEmailEditText.KeyDownHelper(toAddress!!.getText())
            }
            onKeyDel = false
            false
        }
        toAddress.setBackSpaceLisetener(backspaceListener)*/
        var URLText = "<html><body style ='font-size:16px;'>"+emailMessageEntity!!.content+"</body></html>";
        if(emailMessageEntity!!.originalText != null && emailMessageEntity!!.originalText != "")
        {
            URLText = "<html><body style ='font-size:16px;'>"+emailMessageEntity!!.originalText+"</body></html>";
        }
        var needOp = false
        if( emailMessageEntity !!.content != null && emailMessageEntity!!.content.contains("<img"))
        {
            needOp = true
        }
        if(emailMessageEntity!!.originalText!= null && emailMessageEntity!!.originalText.contains("<img"))
        {
            needOp = true;
        }
        if(needOp)
        {
            val webSettings = webView.getSettings()
            if (Build.VERSION.SDK_INT >= 19) {
                webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK)//加载缓存否则网络
            }
            if (Build.VERSION.SDK_INT >= 19) {
                webSettings.setLoadsImagesAutomatically(true)//图片自动缩放 打开
            } else {
                webSettings.setLoadsImagesAutomatically(false)//图片自动缩放 关闭
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null)//软件解码
            }
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null)//硬件解码
            webView.setLayerType(View.LAYER_TYPE_NONE, null);
            webSettings.javaScriptEnabled = true // 设置支持javascript脚本
            //webSettings.setTextSize(WebSettings.TextSize.LARGEST)
//        webSettings.setPluginState(WebSettings.PluginState.ON);
            webSettings.setSupportZoom(true)// 设置可以支持缩放
            webSettings.builtInZoomControls = true// 设置出现缩放工具 是否使用WebView内置的缩放组件，由浮动在窗口上的缩放控制和手势缩放控制组成，默认false

            webSettings.displayZoomControls = false//隐藏缩放工具
            webSettings.useWideViewPort = true// 扩大比例的缩放

            webSettings.layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN//自适应屏幕
            webSettings.loadWithOverviewMode = true

            /* webSettings.databaseEnabled = true//
             webSettings.savePassword = true//保存密码
             webSettings.domStorageEnabled = true//是否开启本地DOM存储  鉴于它的安全特性（任何人都能读取到它，尽管有相应的限制，将敏感数据存储在这里依然不是明智之举），Android 默认是关闭该功能的。

             webView.setSaveEnabled(true)
             webView.setKeepScreenOn(true)*/
        }


        webView.webChromeClient = object : WebChromeClient() {
            override fun onReceivedTitle(view: WebView, title1: String?) {
                super.onReceivedTitle(view, title1)
                if (title1 != null) {
                    //title.text = title1
                }
            }

            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                if (newProgress == 100) {
                    progressBar.visibility = View.GONE
                } else {
                    KLog.i("进度：" + newProgress)
                    progressBar.visibility = View.VISIBLE
                    progressBar.progress = newProgress
                }
                super.onProgressChanged(view, newProgress)
            }

        }
        webView.webViewClient = object  : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                //view.loadUrl(url)
                val intent = Intent()
                intent.action = "android.intent.action.VIEW"
                val url = Uri.parse(url)
                intent.data = url
                startActivity(intent)
                return true
            }

            override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler, error: SslError) {
                if (error.getPrimaryError() == SslError.SSL_DATE_INVALID
                        || error.getPrimaryError() == SslError.SSL_EXPIRED
                        || error.getPrimaryError() == SslError.SSL_INVALID
                        || error.getPrimaryError() == SslError.SSL_UNTRUSTED) {
                    handler.proceed();
                } else {
                    handler.cancel();
                }
                super.onReceivedSslError(view, handler, error)
            }

            override fun onReceivedHttpError(view: WebView?, request: WebResourceRequest?, errorResponse: WebResourceResponse?) {
                KLog.i("ddddddd")
                super.onReceivedHttpError(view, request, errorResponse)
            }

            override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                KLog.i("ddddddd")
                super.onReceivedError(view, request, error)
            }
        }

        if(foward == 3)
        {
            re_main_editor.setHtml(emailMessageEntity!!.content)
        }else{
            try {
                webView.loadDataWithBaseURL(null,URLText,"text/html","utf-8",null);
            }catch (e:Exception)
            {
                e.printStackTrace()
            }

        }

    }

    private fun initAttachUI()
    {
        attachListEntity =  arrayListOf<EmailAttachEntity>()
        var attachCount = 0
        if(emailMeaasgeInfoData != null)
        {
            attachCount = emailMeaasgeInfoData!!.attachmentCount
        }
        if(attach > 0 && attachCount > 0)
        {
            val save_dir = PathUtils.getInstance().filePath.toString() + "/"
            var  attachListData =  AppConfig.instance.mDaoMaster!!.newSession().emailAttachEntityDao.queryBuilder().where(EmailAttachEntityDao.Properties.MsgId.eq(emailMeaasgeInfoData!!.msgId)).list()

            var isDownload = true
            var listAccath:ArrayList<MailAttachment>  = ArrayList<MailAttachment>()
            var i = 0;
            for (attach in attachListData)
            {
                var file = File(save_dir+attach.account+"_"+attach.name)
                if(!file.exists())
                {
                    isDownload = false
                }
                attach.localPath = save_dir+attach.account+"_"+attach.name
                AppConfig.instance.mDaoMaster!!.newSession().emailAttachEntityDao.update(attach)

                var fileName =  attach.name
                if (fileName.contains("jpg") || fileName.contains("JPG")  || fileName.contains("png")) {
                    val localMedia = LocalMedia()
                    localMedia.isCompressed = false
                    localMedia.duration = 0
                    localMedia.height = 100
                    localMedia.width = 100
                    localMedia.isChecked = false
                    localMedia.isCut = false
                    localMedia.mimeType = 0
                    localMedia.num = 0
                    localMedia.path = attach.localPath
                    localMedia.pictureType = "image/jpeg"
                    localMedia.setPosition(i)
                    localMedia.sortIndex = i
                    previewImages.add(localMedia)
                    ImagesObservable.getInstance().saveLocalMedia(previewImages, "chat")
                }

                i++

            }
            if(!isDownload)
            {
                showProgressDialog(getString(R.string.Attachmentdownloading))
                val emailReceiveClient = EmailReceiveClient(AppConfig.instance.emailConfig())

                emailReceiveClient
                        .imapDownloadEmailAttach(this@EmailSendActivity, object : GetAttachCallback {
                            override fun gainSuccess(messageList: List<MailAttachment>, count: Int) {
                                //tipDialog.dismiss()
                                closeProgressDialog()
                                runOnUiThread {
                                    attachListData =  AppConfig.instance.mDaoMaster!!.newSession().emailAttachEntityDao.queryBuilder().where(EmailAttachEntityDao.Properties.MsgId.eq(emailMeaasgeInfoData!!.msgId)).list()
                                    for (item in attachListData)
                                    {
                                        item.isHasData = true
                                        item.isCanDelete = true
                                    }
                                    attachListEntity.addAll(attachListData)
                                    updataAttachUI()
                                }
                            }
                            override fun gainFailure(errorMsg: String) {
                                //tipDialog.dismiss()
                                closeProgressDialog()
                                Toast.makeText(this@EmailSendActivity, getString(R.string.Attachment_download_failed), Toast.LENGTH_SHORT).show()
                            }
                        },menu,emailMeaasgeInfoData!!.msgId,save_dir,emailMeaasgeInfoData!!.aesKey)
            }else{
                attachListData =  AppConfig.instance.mDaoMaster!!.newSession().emailAttachEntityDao.queryBuilder().where(EmailAttachEntityDao.Properties.MsgId.eq(emailMeaasgeInfoData!!.msgId)).list()
                for (item in attachListData)
                {
                    item.isHasData = true
                    item.isCanDelete = true
                }
                attachListEntity.addAll(attachListData)
                updataAttachUI()
            }


        }else{
            updataAttachUI()
        }
    }
    fun updataAttachUI()
    {

        var emailAttachEntity = EmailAttachEntity()
        emailAttachEntity.isHasData = false
        emailAttachEntity.isCanDelete = false
        attachListEntity.add(emailAttachEntity)
        emaiAttachAdapter = EmaiAttachAdapter(attachListEntity)
        emaiAttachAdapter!!.setOnItemLongClickListener { adapter, view, position ->

            true
        }
        recyclerViewAttach.setLayoutManager(GridLayoutManager(AppConfig.instance, 2));
        recyclerViewAttach.adapter = emaiAttachAdapter

        emaiAttachAdapter!!.setOnItemClickListener { adapter, view, position ->
            /* var intent = Intent(activity!!, ConversationActivity::class.java)
             intent.putExtra("user", coversationListAdapter!!.getItem(position)!!.userEntity)
             startActivity(intent)*/
        }
        emaiAttachAdapter!!.setOnItemChildClickListener { adapter, view, position ->
            when (view.id) {
                R.id.deleteBtn -> {
                    emaiAttachAdapter!!.remove(position)
                    emaiAttachAdapter!!.notifyDataSetChanged();
                    if( emaiAttachAdapter!!.itemCount > 1)
                    {
                        addSubject.text = (emaiAttachAdapter!!.itemCount -1).toString()
                    }else{
                        addSubject.text = ""
                    }
                }
                R.id.iv_add -> {
                    hideSoftKeyboard()
                    var menuArray = arrayListOf<String>(getString(R.string.attach_picture),getString(R.string.attach_take_pic),getString(R.string.attach_video),getString(R.string.attach_file))
                    var iconArray = arrayListOf<String>("sheet_album","sheet_camera","sheet_video","sheet_file")
                    PopWindowUtil.showPopAttachMenuWindow(this@EmailSendActivity, itemParent,menuArray,iconArray, object : PopWindowUtil.OnSelectListener {
                        override fun onSelect(position: Int, obj: Any) {
                            KLog.i("" + position)
                            when (position) {
                                0 -> {
                                    selectPicFromLocal()
                                }
                                1 -> {
                                    AndPermission.with(AppConfig.instance)
                                            .requestCode(101)
                                            .permission(
                                                    Manifest.permission.CAMERA
                                            )
                                            .callback(permission)
                                            .start()
                                }
                                2 -> {
                                    AndPermission.with(AppConfig.instance)
                                            .requestCode(101)
                                            .permission(
                                                    Manifest.permission.CAMERA
                                            )
                                            .callback(permissionVideo)
                                            .start()
                                }
                                3 -> {
                                    startActivityForResult(Intent(this@EmailSendActivity, FileChooseActivity::class.java).putExtra("fileType", 2), REQUEST_CODE_FILE)
                                    //startActivityForResult(Intent(this@EmailSendActivity, SelectFileActivity::class.java).putExtra("fileType", 2), REQUEST_CODE_FILE)
                                }

                            }
                        }

                    })
                }
            }
        }
    }
    fun showImagList(showIndex:Int)
    {
        val selectedImages = java.util.ArrayList<LocalMedia>()
        val previewImages = ImagesObservable.getInstance().readLocalMedias("chat")
        if (previewImages != null && previewImages.size > 0) {

            val intentPicturePreviewActivity = Intent(this, PicturePreviewActivity::class.java)
            val bundle = Bundle()
            //ImagesObservable.getInstance().saveLocalMedia(previewImages);
            bundle.putSerializable(PictureConfig.EXTRA_SELECT_LIST, selectedImages as Serializable)
            bundle.putInt(PictureConfig.EXTRA_POSITION, showIndex)
            bundle.putString("from", "chat")
            intentPicturePreviewActivity.putExtras(bundle)
            startActivity(intentPicturePreviewActivity)
        }
    }
    private fun sendCheck(flag:Boolean)
    {
        contactMapList = HashMap<String, String>()
        isSendCheck = flag
        var toAdress = getEditText(toAdressEdit)
        toAdress = toAdress.replace(",,","")
        var toAdressArr = toAdress.split(",");
        if(flag== true && toAdress== "")
        {
            toast(R.string.The_recipient_cant_be_empty)
            return
        }
        var subjectStr = subject.getText().toString()
        if(flag== true && subjectStr== "")
        {
            toast(R.string.The_subject_cant_be_empty)
            return
        }
        var toAdressBase64 = ""
        for (item in toAdressArr)
        {
            if(item != "")
            {
                var temp = item.trim()
                temp = temp.toLowerCase()
                var isEmail = StringUitl.isEmail(temp)
                if(!isEmail)
                {
                    toast(temp +" "+ getString(R.string.Some_addresses_are_illegal))
                    return;
                }
                toAdressBase64 += RxEncodeTool.base64Encode2String(temp.toByteArray()) +","
            }
        }
        if(toAdressBase64.length >0)
        {
            toAdressBase64 = toAdressBase64.substring(0,toAdressBase64.length -1)
        }

        var toAdressBase64CC = ""
        var ccAdress = getEditText(ccAdressEdit)
        ccAdress = ccAdress.replace(",,","")
        var ccAdressArr = ccAdress.split(",");
        for (item in ccAdressArr)
        {
            if(item != "")
            {
                var temp = item.trim()
                temp = temp.toLowerCase()
                var isEmail = StringUitl.isEmail(temp)
                if(!isEmail)
                {
                    toast(temp +" " + getString(R.string.Some_addresses_are_illegal))
                    return;
                }
                toAdressBase64CC += RxEncodeTool.base64Encode2String(temp.toByteArray()) +","
            }
        }
        if(toAdressBase64CC.length >0)
        {
            toAdressBase64CC = toAdressBase64CC.substring(0,toAdressBase64CC.length -1)
        }
        var toAdressBase64BCC = ""
        var bccAdress = getEditText(bccAdressEdit)
        bccAdress.replace(",,","")
        var bccAdressArr = bccAdress.split(",");
        for (item in bccAdressArr)
        {
            if(item != "")
            {
                var temp = item.trim()
                temp = temp.toLowerCase()
                var isEmail = StringUitl.isEmail(temp)
                if(!isEmail)
                {
                    toast(temp + getString(R.string.Some_addresses_are_illegal))
                    return;
                }
                toAdressBase64BCC += RxEncodeTool.base64Encode2String(temp.toByteArray()) +","
            }
        }
        if(toAdressBase64BCC.length >0)
        {
            toAdressBase64BCC = toAdressBase64BCC.substring(0,toAdressBase64BCC.length -1)
        }
        var addressBase64 = toAdressBase64 +toAdressBase64CC +toAdressBase64BCC
        if(addressBase64 == "")
        {
            lockTips.visibility = View.GONE
            return
        }
        if(isSendCheck)
        {
            runOnUiThread {
                showProgressDialog(getString(R.string.waiting))
            }
        }
        var checkmailUkey = CheckmailUkey(toAdressArr.size,addressBase64)
        AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(6,checkmailUkey))
    }
    /**
     * 发送邮件
     */
    private fun sendEmail(send: Boolean) {
        var fileKey = RxEncryptTool.generateAESKey()
        var contentHtml = re_main_editor.html
        if(flag == 1 && emailMeaasgeInfoData != null && emailMeaasgeInfoData!!.content != null)
        {
            var from = emailMeaasgeInfoData!!.from
            var toStr  = emailMeaasgeInfoData!!.to
            var centerStr =  " <br />"+
                    " <br />"+
                    " <br />"+
                    "<div style=\"background: #f2f2f2;\">"+
                    getString(R.string.Original_mail)+
                    "   <br />"+getString(R.string.From)+"：&quot;"+from+"：&quot;"+
                    "   <br />"+getString(R.string.To)+"：&quot;"+toStr+"：&quot;"+
                    "   <br />"+getString(R.string.Subject)+"：&quot;"+emailMeaasgeInfoData!!.subject+"：&quot;"+
                    "   <br />"+getString(R.string.Date)+"："+emailMeaasgeInfoData!!.date+
                    "  </div>"+
                    "   <br />"+
                    "   <br />";
            contentHtml +=  centerStr
            if(emailMeaasgeInfoData!!.originalText != "")
            {
                contentHtml +=emailMeaasgeInfoData!!.originalText
            }else{
                contentHtml +=emailMeaasgeInfoData!!.content
            }


        }
        var needTipsShow = true;
        if(contentHtml.contains("myconfidantbegin"))
        {
            var endIndex = contentHtml.indexOf("<div myconfidantbegin=")
            if(endIndex > 0)
            {
                contentHtml = contentHtml.substring(0,endIndex)
            }
        }
        if(contactMapList.size >0)
        {
            val contentBuffer = contentHtml.toByteArray()
            var fileKey16 = fileKey.substring(0,16)
            Log.i("fileKey16",fileKey16)
            var contentBufferMiStr = RxEncodeTool.base64Encode2String(AESCipher.aesEncryptBytes(contentBuffer, fileKey16!!.toByteArray(charset("UTF-8"))))
            contentHtml = contentBufferMiStr;
        }
        var confidantKey = "";
        for(item in contactMapList)
        {
            var account = item.key
            var friendMiPublicKey = item.value
            var dstKey = String(RxEncodeTool.base64Encode(LibsodiumUtil.EncryptShareKey(fileKey, friendMiPublicKey)))
            confidantKey += account+"&&"+dstKey+"##";
        }
        if(confidantKey != "")
        {
            var myAccountBase64 = String(RxEncodeTool.base64Encode(AppConfig.instance.emailConfig().account))
            var dstKey = String(RxEncodeTool.base64Encode(LibsodiumUtil.EncryptShareKey(fileKey, ConstantValue.libsodiumpublicMiKey!!)))
            confidantKey += myAccountBase64+"&&"+dstKey;
        }
        if(contactMapList.size >0)
        {
            contentHtml += "<span style=\'display:none\' confidantkey=\'"+confidantKey+"\'></span>";
        }
        var endStr =  "<div myconfidantbegin=''>"+
                "<br />"+
                " <br />"+
                " <br />"+
                "<span>"+
                getString(R.string.sendfromconfidant)+
                "</span>"+
                "</div>"
        if(needTipsShow)
        {
            contentHtml += endStr
        }
        var toAdress = getEditText(toAdressEdit)
        var ccAdress = getEditText(ccAdressEdit)
        var bccAdress = getEditText(bccAdressEdit)


        var toAdressArr = toAdress.split(",")
        for(item in toAdressArr)
        {
            if(item != "")
            {
                if(!StringUitl.checkEmail(item))
                {
                    toast(R.string.Illegal_address)
                    return
                }
                var name  = item.substring(0,item.indexOf("@"))
                var account=item
                account = account.toLowerCase()
                var localEmailContacts = AppConfig.instance.mDaoMaster!!.newSession().emailContactsEntityDao.queryBuilder().where(EmailContactsEntityDao.Properties.Account.eq(account)).list()
                if(localEmailContacts.size == 0)
                {
                    var emailContactsEntity= EmailContactsEntity();
                    emailContactsEntity.name = name
                    emailContactsEntity.account = account
                    emailContactsEntity.createTime = System.currentTimeMillis()
                    AppConfig.instance.mDaoMaster!!.newSession().emailContactsEntityDao.insert(emailContactsEntity)
                }else{
                    var emailContactsEntity = localEmailContacts.get(0)
                    emailContactsEntity.createTime = System.currentTimeMillis()
                    AppConfig.instance.mDaoMaster!!.newSession().emailContactsEntityDao.update(emailContactsEntity)
                }
            }

        }
        var ccAdressArr = ccAdress.split(",")
        for(item in ccAdressArr)
        {
            if(item != "")
            {
                var name  = item.substring(0,item.indexOf("@"))
                var account=item
                account = account.toLowerCase()
                var localEmailContacts = AppConfig.instance.mDaoMaster!!.newSession().emailContactsEntityDao.queryBuilder().where(EmailContactsEntityDao.Properties.Account.eq(account)).list()
                if(localEmailContacts.size == 0)
                {
                    var emailContactsEntity= EmailContactsEntity();
                    emailContactsEntity.name = name
                    emailContactsEntity.account = account
                    emailContactsEntity.createTime = System.currentTimeMillis()
                    AppConfig.instance.mDaoMaster!!.newSession().emailContactsEntityDao.insert(emailContactsEntity)
                }else{
                    var emailContactsEntity = localEmailContacts.get(0)
                    emailContactsEntity.createTime = System.currentTimeMillis()
                    AppConfig.instance.mDaoMaster!!.newSession().emailContactsEntityDao.update(emailContactsEntity)
                }
            }

        }
        var bccAdressArr = bccAdress.split(",")
        for(item in bccAdressArr)
        {
            if(item != "")
            {
                var name  = item.substring(0,item.indexOf("@"))
                var account=item
                account = account.toLowerCase()
                var localEmailContacts = AppConfig.instance.mDaoMaster!!.newSession().emailContactsEntityDao.queryBuilder().where(EmailContactsEntityDao.Properties.Account.eq(account)).list()
                if(localEmailContacts.size == 0)
                {
                    var emailContactsEntity= EmailContactsEntity();
                    emailContactsEntity.name = name
                    emailContactsEntity.account = account
                    emailContactsEntity.createTime = System.currentTimeMillis()
                    AppConfig.instance.mDaoMaster!!.newSession().emailContactsEntityDao.insert(emailContactsEntity)
                }else{
                    var emailContactsEntity = localEmailContacts.get(0)
                    emailContactsEntity.createTime = System.currentTimeMillis()
                    AppConfig.instance.mDaoMaster!!.newSession().emailContactsEntityDao.update(emailContactsEntity)
                }
            }

        }
        /*val toSelectionEnd = toAdressEdit.length()
        val toSelectionStart = 0
        val toSpans = toAdressEdit!!.getText()!!.getSpans(toSelectionStart, toSelectionEnd, User::class.java)

        var toIndex = 0
        for (span in toSpans) {
            if (span != null && span!!.id != null && span!!.id != "") {
                if (toIndex > 0) {
                    toAdress += "," + span!!.id
                } else {
                    toAdress += span!!.id
                }
                toIndex++
            }
        }
        val ccSelectionEnd = ccAdressEdit.length()
        val ccSelectionStart = 0
        val ccSpans = ccAdressEdit!!.getText()!!.getSpans(ccSelectionStart, ccSelectionEnd, User::class.java)
        var ccIndex = 0
        for (span in ccSpans) {
            if (span != null && span!!.id != null && span!!.id != "") {
                if (ccIndex > 0) {
                    ccAdress += "," + span!!.id
                } else {
                    ccAdress += span!!.id
                }
                ccIndex++
            }
        }
        val bccSelectionEnd = bccAdressEdit.length()
        val bccSelectionStart = 0
        val bccSpans = bccAdressEdit!!.getText()!!.getSpans(bccSelectionStart, bccSelectionEnd, User::class.java)
        var bccIndex = 0
        for (span in bccSpans) {
            if (span != null && span!!.id != null && span!!.id != "") {
                if (bccIndex > 0) {
                    bccAdress += "," + span!!.id
                } else {
                    bccAdress += span!!.id
                }
                bccIndex++
            }
        }*/
        if(toAdress== "")
        {
            toast(R.string.The_recipient_cant_be_empty)
            return
        }

        var attachList = ""
        var emaiAttachAdapterList = emaiAttachAdapter!!.data
        for(item in emaiAttachAdapterList)
        {
            if(item.localPath != null)
            {
                if(contactMapList.size >0)
                {
                    val base58files_dir = PathUtils.getInstance().tempPath.toString() + "/" + item.name
                    val code = FileUtil.copySdcardToxFileAndEncrypt(item.localPath, base58files_dir, fileKey.substring(0, 16))
                    if(code ==1)
                    {
                        attachList +=base58files_dir+","
                    }
                }else{
                    attachList +=item.localPath+","
                }


            }

        }
        if(attachList.length >0)
        {
            attachList = attachList.substring(0,attachList.length -1)
        }
        val emailSendClient = EmailSendClient(AppConfig.instance.emailConfig())
        var name = toAdress.substring(1,toAdress.indexOf("@"))
        if(AppConfig.instance.emailConfig().name != null && AppConfig.instance.emailConfig().name != "")
        {
            name = AppConfig.instance.emailConfig().name;
        }
        var subjectStr = emailSendClient.getUTFStr(subject.getText().toString())
        if(send)
        {
            runOnUiThread {
                showProgressDialog(getString(R.string.Sending))
            }
            emailSendClient
                    .setTo(toAdress)                //收件人的邮箱地址
                    .setCc(ccAdress)
                    .setBcc(bccAdress)
                    .setNickname(name)                                    //发件人昵称
                    .setSubject(subject.getText().toString())             //邮件标题
                    .setContent(contentHtml)              //邮件正文
                    .setAttach(attachList)
                    .sendAsyn(this, object : GetSendCallback {
                        override fun sendSuccess() {
                            runOnUiThread {
                                closeProgressDialog()
                                var emailConfigEntityChooseList = AppConfig.instance.mDaoMaster!!.newSession().emailConfigEntityDao.queryBuilder().where(EmailConfigEntityDao.Properties.IsChoose.eq(true)).list()
                                if(emailConfigEntityChooseList.size > 0) {
                                    var emailConfigEntityChoose = emailConfigEntityChooseList.get(0)
                                    emailConfigEntityChoose.sendMenuRefresh = true
                                    AppConfig.instance.mDaoMaster!!.newSession().emailConfigEntityDao.update(emailConfigEntityChoose)
                                }
                                EventBus.getDefault().post(SendEmailSuccess())
                                Toast.makeText(this@EmailSendActivity, R.string.success, Toast.LENGTH_SHORT).show()
                                finish()
                            }
                        }

                        override fun sendFailure(errorMsg: String) {
                            runOnUiThread {
                                closeProgressDialog()
                            }
                            Islands.ordinaryDialog(this@EmailSendActivity)
                                    .setText(null, getString(R.string.error))
                                    .setButton(getString(R.string.close), null, null)
                                    .click().show()
                        }
                    },ConstantValue.currentEmailConfigEntity!!.sendMenu)

        }else{
            runOnUiThread {
                showProgressDialog(getString(R.string.Saving))
            }
            emailSendClient
                    .setTo(toAdress)                //收件人的邮箱地址
                    .setCc(ccAdress)
                    .setBcc(bccAdress)
                    .setNickname(name)                                    //发件人昵称
                    .setSubject(subject.getText().toString())             //邮件标题
                    .setContent(contentHtml)              //邮件正文
                    .setAttach(attachList)
                    .saveDraftsAsyn(this, object : GetSendCallback {
                        override fun sendSuccess() {
                            runOnUiThread {
                                closeProgressDialog()
                                Toast.makeText(this@EmailSendActivity, R.string.success, Toast.LENGTH_SHORT).show()
                                finish()
                            }
                        }

                        override fun sendFailure(errorMsg: String) {
                            runOnUiThread {
                                closeProgressDialog()
                            }
                            Islands.ordinaryDialog(this@EmailSendActivity)
                                    .setText(null, getString(R.string.error))
                                    .setButton(getString(R.string.close), null, null)
                                    .click().show()
                        }
                    },ConstantValue.currentEmailConfigEntity!!.drafMenu,"draf")
        }

    }
    private val permission = object : PermissionListener {
        override fun onSucceed(requestCode: Int, grantedPermissions: List<String>) {

            // 权限申请成功回调。
            if (requestCode == 101) {
                selectPicFromCamera()
            }
        }

        override fun onFailed(requestCode: Int, deniedPermissions: List<String>) {
            // 权限申请失败回调。
            if (requestCode == 101) {
                KLog.i("权限申请失败")

            }
        }
    }
    private val permissionVideo = object : PermissionListener {
        override fun onSucceed(requestCode: Int, grantedPermissions: List<String>) {

            // 权限申请成功回调。
            if (requestCode == 101) {
                selectVideoFromCamera()
            }
        }

        override fun onFailed(requestCode: Int, deniedPermissions: List<String>) {
            // 权限申请失败回调。
            if (requestCode == 101) {
                KLog.i("权限申请失败")

            }
        }
    }
    fun getEditText(edit:EditText):String
    {
        val toSelectionEnd = edit.length()
        val toSelectionStart = 0
        val toSpans = edit!!.getText()!!.getSpans(toSelectionStart, toSelectionEnd, User::class.java)
        var toAdress = ""
        var toIndex = 0
        for (span in toSpans) {
            var id = span!!.id
            if(id != null)
            {
                id = id.trim()
            }
            if (span != null && id != null && id!= "") {
                if (toIndex > 0) {
                    toAdress += "," + id
                } else {
                    toAdress += id
                }
                toIndex++
            }
        }
        return toAdress
    }
    /**
     * capture new image
     */
    protected fun selectPicFromCamera() {
        if (!EaseCommonUtils.isSdcardExist()) {
            Toast.makeText(this, R.string.sd_card_does_not_exist, Toast.LENGTH_SHORT).show()
            return
        }
        cameraFile = File(PathUtils.getInstance().tempPath, (System.currentTimeMillis() / 1000).toString() + ".jpg")
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            cameraFile = File(Environment.getExternalStorageDirectory().toString() + ConstantValue.localPath + "/PicAndVideoTemp", (System.currentTimeMillis() / 1000).toString() + ".jpg")
        }

        try {
            cameraFile!!.getParentFile().mkdirs()
            val uri = EaseCompat.getUriForFile(this, cameraFile)
            startActivityForResult(
                    Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(MediaStore.EXTRA_OUTPUT, uri),
                    REQUEST_CODE_CAMERA)
        } catch (e: Exception) {
            Toast.makeText(this, R.string.Permissionerror, Toast.LENGTH_SHORT).show()
        }

    }

    /**
     * capture new video
     */
    protected fun selectVideoFromCamera() {
        if (!EaseCommonUtils.isSdcardExist()) {
            Toast.makeText(this, R.string.sd_card_does_not_exist, Toast.LENGTH_SHORT).show()
            return
        }
        videoFile = File(PathUtils.getInstance().videoPath, (System.currentTimeMillis() / 1000).toString() + ".mp4")
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            videoFile = File(Environment.getExternalStorageDirectory().toString() + ConstantValue.localPath + "/PicAndVideoTemp", (System.currentTimeMillis() / 1000).toString() + ".mp4")
        }
        KLog.i(videoFile!!.getPath())

        videoFile!!.getParentFile().mkdirs()
        startActivityForResult(
                Intent(MediaStore.ACTION_VIDEO_CAPTURE).putExtra(MediaStore.EXTRA_OUTPUT, EaseCompat.getUriForFile(this, videoFile)).putExtra(MediaStore.EXTRA_DURATION_LIMIT, 30).putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0),
                REQUEST_CODE_VIDEO)
    }
    /**
     * select local image
     * //todo
     */
    protected fun selectPicFromLocal() {
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
                .forResult(REQUEST_CODE_LOCAL)
    }
    /**
     * 初始化文本编辑器
     */
    private fun initEditor() {
        re_main_editor.setEditorHeight(120);
        //输入框显示字体的大小
        re_main_editor.setEditorFontSize(16)
        //输入框显示字体的颜色
        re_main_editor.setEditorFontColor(Color.GRAY)
        //输入框背景设置
        re_main_editor.setEditorBackgroundColor(Color.WHITE)
        //re_main_editor.setBackgroundColor(Color.BLUE);
        //re_main_editor.setBackgroundResource(R.drawable.bg);
        //re_main_editor.setBackground("https://raw.githubusercontent.com/wasabeef/art/master/chip.jpg");
        //输入框文本padding
        //re_main_editor.setPadding(10, 10, 10, 10)
        //输入提示文本
        re_main_editor.setPlaceholder(getString(R.string.Compose_email))
        //是否允许输入
        //re_main_editor.setInputEnabled(false);
        //文本输入框监听事件
        re_main_editor.setOnTextChangeListener(object : RichEditor.OnTextChangeListener {
            override fun onTextChange(text: String) {
                Log.d("re_main_editor", "html文本：$text")
            }
        })
        re_main_editor.setOnFocusChangeListener(object : View.OnFocusChangeListener {
            override fun onFocusChange( v:View,  hasFocus:Boolean) {
                if(hasFocus)
                {
                    EditorIconParent.visibility = View.VISIBLE
                }else{
                    EditorIconParent.visibility = View.GONE
                }
            }
        });
    }

    /**
     * 初始化颜色选择器
     */
    private fun initColorPicker() {
        cpv_main_color.setOnColorPickerChangeListener(object : ColorPickerView.OnColorPickerChangeListener {
            override fun onColorChanged(picker: ColorPickerView, color: Int) {
                button_text_color.setBackgroundColor(color)
                re_main_editor.setTextColor(color)
            }

            override  fun onStartTrackingTouch(picker: ColorPickerView) {

            }

            override fun onStopTrackingTouch(picker: ColorPickerView) {

            }
        })
    }

    /**
     * 初始化菜单按钮
     */
    private fun initMenu() {
        getViewMeasureHeight()
    }

    /**
     * 获取控件的高度
     */
    private fun getViewMeasureHeight() {
        //获取像素密度
        val mDensity = resources.displayMetrics.density
        //获取布局的高度
        val w = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED)
        val h = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED)
        ll_main_color.measure(w, h)
        val height = ll_main_color.getMeasuredHeight()
        mFoldedViewMeasureHeight = (mDensity * height + 0.5).toInt()
    }

    private fun initClickListener() {
        button_bold.setOnClickListener(this)
        button_text_color!!.setOnClickListener(this)
        tv_main_preview.setOnClickListener(this)
        button_image.setOnClickListener(this)
        button_list_ol.setOnClickListener(this)
        button_list_ul.setOnClickListener(this)
        button_underline.setOnClickListener(this)
        button_italic.setOnClickListener(this)
        button_align_left.setOnClickListener(this)
        button_align_right.setOnClickListener(this)
        button_align_center.setOnClickListener(this)
        button_indent.setOnClickListener(this)
        button_outdent.setOnClickListener(this)
        action_blockquote.setOnClickListener(this)
        action_strikethrough.setOnClickListener(this)
        action_superscript.setOnClickListener(this)
        action_subscript.setOnClickListener(this)

        backBtn.setOnClickListener(this)
        addTo.setOnClickListener(this)
        showCcAndBcc.setOnClickListener(this)
        addCc.setOnClickListener(this)
        addBcc.setOnClickListener(this)

        sendBtn.setOnClickListener(this)

    }

    override fun onClick(v: View) {
        val id = v.id
        if (id == R.id.button_bold) {//字体加粗
            if (isClickBold) {
                button_bold.setImageResource(R.mipmap.bold)
            } else {  //加粗
                button_bold.setImageResource(R.mipmap.bold_)
            }
            isClickBold = !isClickBold
            re_main_editor.setBold()
        } else if (id == R.id.button_text_color) {//设置字体颜色
            //如果动画正在执行,直接return,相当于点击无效了,不会出现当快速点击时,
            // 动画的执行和ImageButton的图标不一致的情况
            if (isAnimating) return
            //如果动画没在执行,走到这一步就将isAnimating制为true , 防止这次动画还没有执行完毕的
            //情况下,又要执行一次动画,当动画执行完毕后会将isAnimating制为false,这样下次动画又能执行
            isAnimating = true

            if (ll_main_color.getVisibility() == View.GONE) {
                //打开动画
                animateOpen(ll_main_color)
            } else {
                //关闭动画
                animateClose(ll_main_color)
            }
        } else if (id == R.id.button_image) {//插入图片
            //这里的功能需要根据需求实现，通过insertImage传入一个URL或者本地图片路径都可以，这里用户可以自己调用本地相
            //或者拍照获取图片，传图本地图片路径，也可以将本地图片路径上传到服务器（自己的服务器或者免费的七牛服务器），
            //返回在服务端的URL地址，将地址传如即可（我这里传了一张写死的图片URL，如果你插入的图片不现实，请检查你是否添加
            // 网络请求权限<uses-permission android:name="android.permission.INTERNET" />）
            re_main_editor.insertImage("http://www.1honeywan.com/dachshund/image/7.21/7.21_3_thumb.JPG",
                    "dachshund")
        } else if (id == R.id.button_list_ol) {
            if (isListOl) {
                button_list_ol.setImageResource(R.mipmap.list_ol)
            } else {
                button_list_ol.setImageResource(R.mipmap.list_ol_)
            }
            isListOl = !isListOl
            re_main_editor.setNumbers()
        } else if (id == R.id.button_list_ul) {
            if (isListUL) {
                button_list_ul.setImageResource(R.mipmap.list_ul)
            } else {
                button_list_ul.setImageResource(R.mipmap.list_ul_)
            }
            isListUL = !isListUL
            re_main_editor.setBullets()
        } else if (id == R.id.button_underline) {
            if (isTextLean) {
                button_underline.setImageResource(R.mipmap.underline)
            } else {
                button_underline.setImageResource(R.mipmap.underline_)
            }
            isTextLean = !isTextLean
            re_main_editor.setUnderline()
        } else if (id == R.id.button_italic) {
            if (isItalic) {
                button_italic.setImageResource(R.mipmap.lean)
            } else {
                button_italic.setImageResource(R.mipmap.lean_)
            }
            isItalic = !isItalic
            re_main_editor.setItalic()
        } else if (id == R.id.button_align_left) {
            if (isAlignLeft) {
                button_align_left.setImageResource(R.mipmap.align_left)
            } else {
                button_align_left.setImageResource(R.mipmap.align_left_)
            }
            isAlignLeft = !isAlignLeft
            re_main_editor.setAlignLeft()
        } else if (id == R.id.button_align_right) {
            if (isAlignRight) {
                button_align_right.setImageResource(R.mipmap.align_right)
            } else {
                button_align_right.setImageResource(R.mipmap.align_right_)
            }
            isAlignRight = !isAlignRight
            re_main_editor.setAlignRight()
        } else if (id == R.id.button_align_center) {
            if (isAlignCenter) {
                button_align_center.setImageResource(R.mipmap.align_center)
            } else {
                button_align_center.setImageResource(R.mipmap.align_center_)
            }
            isAlignCenter = !isAlignCenter
            re_main_editor.setAlignCenter()
        } else if (id == R.id.button_indent) {
            if (isIndent) {
                button_indent.setImageResource(R.mipmap.indent)
            } else {
                button_indent.setImageResource(R.mipmap.indent_)
            }
            isIndent = !isIndent
            re_main_editor.setIndent()
        } else if (id == R.id.button_outdent) {
            if (isOutdent) {
                button_outdent.setImageResource(R.mipmap.outdent)
            } else {
                button_outdent.setImageResource(R.mipmap.outdent_)
            }
            isOutdent = !isOutdent
            re_main_editor.setOutdent()
        } else if (id == R.id.action_blockquote) {
            if (isBlockquote) {
                action_blockquote.setImageResource(R.mipmap.blockquote)
            } else {
                action_blockquote.setImageResource(R.mipmap.blockquote_)
            }
            isBlockquote = !isBlockquote
            re_main_editor.setBlockquote()
        } else if (id == R.id.action_strikethrough) {
            if (isStrikethrough) {
                action_strikethrough.setImageResource(R.mipmap.strikethrough)
            } else {
                action_strikethrough.setImageResource(R.mipmap.strikethrough_)
            }
            isStrikethrough = !isStrikethrough
            re_main_editor.setStrikeThrough()
        } else if (id == R.id.action_superscript) {
            if (isSuperscript) {
                action_superscript.setImageResource(R.mipmap.superscript)
            } else {
                action_superscript.setImageResource(R.mipmap.superscript_)
            }
            isSuperscript = !isSuperscript
            re_main_editor.setSuperscript()
        } else if (id == R.id.action_subscript) {
            if (isSubscript) {
                action_subscript.setImageResource(R.mipmap.subscript)
            } else {
                action_subscript.setImageResource(R.mipmap.subscript_)
            }
            isSubscript = !isSubscript
            re_main_editor.setSubscript()
        } else if (id == R.id.tv_main_preview) {//预览
            /* val intent = Intent(this@EmailSendActivity, WebDataActivity::class.java)
             intent.putExtra("diarys", re_main_editor.getHtml())
             startActivity(intent)*/
        }//H1--H6省略，需要的自己添加
        else if (id == R.id.showCcAndBcc) {//预览
            if(ccParent.visibility == View.VISIBLE)
            {
                var drawable = getResources().getDrawable(R.mipmap.tabbar_arrow_lower)
                drawable.setBounds(0, 0, 48, 48);
                showCcAndBcc.setCompoundDrawables(drawable, null, null, null);
                ccParent.visibility = View.GONE
                bccParent.visibility = View.GONE
            }else{
                var drawable = getResources().getDrawable(R.mipmap.tabbar_arrow_upper)
                drawable.setBounds(0, 0, 48, 48);
                showCcAndBcc.setCompoundDrawables(drawable, null, null, null);
                ccParent.visibility = View.VISIBLE
                bccParent.visibility = View.VISIBLE
            }

        }
        else if (id == R.id.addTo) {
            val intent = Intent(this@EmailSendActivity, SelectEmailFriendActivity::class.java)
            oldAdress = getEditText(toAdressEdit)
            intent.putExtra("oldAdress", oldAdress)
            startActivityForResult(intent,REQUEST_CODE_TO)
        }else if (id == R.id.addCc) {
            val intent = Intent(this@EmailSendActivity, SelectEmailFriendActivity::class.java)
            oldAdress = getEditText(ccAdressEdit)
            intent.putExtra("oldAdress", oldAdress)
            startActivityForResult(intent,REQUEST_CODE_CC)
        }else if (id == R.id.addBcc) {
            val intent = Intent(this@EmailSendActivity, SelectEmailFriendActivity::class.java)
            oldAdress = getEditText(bccAdressEdit)
            intent.putExtra("oldAdress", oldAdress)
            startActivityForResult(intent,REQUEST_CODE_BCC)
        }else if (id == R.id.backBtn ) {
            var toAdress = toAdressEdit.text.toString()
            var subject = subject.text.toString()
            var re_main_editorStr = re_main_editor.html
            if(toAdress!= "" || subject!= "" || re_main_editorStr!= "")
            {
                showDialog()
            }else{
                finish()
            }
            //onBackPressed()
        }else if (id == R.id.sendBtn ) {
            sendCheck(true);
            //sendEmail()
        }

    }

    /**
     * 开启动画
     *
     * @param view 开启动画的view
     */
    private fun animateOpen(view: LinearLayout) {
        view.visibility = View.VISIBLE
        val animator = createDropAnimator(view, 0, mFoldedViewMeasureHeight)
        animator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                isAnimating = false
            }
        })
        animator.start()
    }

    /**
     * 关闭动画
     *
     * @param view 关闭动画的view
     */
    private fun animateClose(view: LinearLayout) {
        val origHeight = view.height
        val animator = createDropAnimator(view, origHeight, 0)
        animator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                view.visibility = View.GONE
                isAnimating = false
            }
        })
        animator.start()
    }


    /**
     * 创建动画
     *
     * @param view  开启和关闭动画的view
     * @param start view的高度
     * @param end   view的高度
     * @return ValueAnimator对象
     */
    private fun createDropAnimator(view: View, start: Int, end: Int): ValueAnimator {
        val animator = ValueAnimator.ofInt(start, end)
        animator.addUpdateListener { animation ->
            val value = animation.animatedValue as Int
            val layoutParams = view.layoutParams
            layoutParams.height = value
            view.layoutParams = layoutParams
        }
        return animator
    }
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.getAction() === MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (isShouldHideInput(v, ev)) {

                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm?.hideSoftInputFromWindow(v!!.windowToken, 0)
            }
            return super.dispatchTouchEvent(ev)
        }
        // 必不可少，否则所有的组件都不会有TouchEvent了
        return if (window.superDispatchTouchEvent(ev)) {
            true
        } else onTouchEvent(ev)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_CAMERA) { // capture new image
                if (cameraFile != null && cameraFile!!.exists()) {
                    var videoFilePath = cameraFile!!.getAbsolutePath()
                    var emailAttachEntity = EmailAttachEntity()
                    emailAttachEntity.isHasData = true
                    emailAttachEntity.localPath = videoFilePath
                    emailAttachEntity.name = videoFilePath.substring(videoFilePath.lastIndexOf("/")+1,videoFilePath.length)
                    emailAttachEntity.isCanDelete = true
                    emaiAttachAdapter!!.addData(0,emailAttachEntity)
                    emaiAttachAdapter!!.notifyDataSetChanged();
                }
            } else if (requestCode == REQUEST_CODE_VIDEO) {
                if (videoFile != null && videoFile!!.exists()) {
                    var videoFilePath = videoFile!!.getAbsolutePath()
                    var emailAttachEntity = EmailAttachEntity()
                    emailAttachEntity.isHasData = true
                    emailAttachEntity.localPath = videoFilePath
                    emailAttachEntity.name = videoFilePath.substring(videoFilePath.lastIndexOf("/")+1,videoFilePath.length)
                    emailAttachEntity.isCanDelete = true
                    emaiAttachAdapter!!.addData(0,emailAttachEntity)
                    emaiAttachAdapter!!.notifyDataSetChanged();
                    if( emaiAttachAdapter!!.itemCount > 1)
                    {
                        addSubject.text = (emaiAttachAdapter!!.itemCount -1).toString()
                    }else{
                        addSubject.text = ""
                    }
                }
            } else if (requestCode == REQUEST_CODE_LOCAL) { // send local image
                KLog.i("选照片或者视频返回。。。")
                val list = data!!.getParcelableArrayListExtra<LocalMedia>(PictureConfig.EXTRA_RESULT_SELECTION)
                KLog.i(list)
                if (list != null && list.size > 0) {
                    var len = list.size
                    //emaiAttachAdapter!!.remove(emaiAttachAdapter!!.itemCount)
                    var itemCount = emaiAttachAdapter!!.itemCount
                    for (i in 0 until len) {
                        var emailAttachEntity = EmailAttachEntity()
                        emailAttachEntity.isHasData = true
                        emailAttachEntity.localPath = list.get(i).path
                        emailAttachEntity.name = list.get(i).path.substring(list.get(0).path.lastIndexOf("/")+1,list.get(0).path.length)
                        emailAttachEntity.isCanDelete = true
                        emaiAttachAdapter!!.addData(0,emailAttachEntity)

                    }
                    emaiAttachAdapter!!.notifyDataSetChanged();
                    if( emaiAttachAdapter!!.itemCount > 1)
                    {
                        addSubject.text = (emaiAttachAdapter!!.itemCount -1).toString()
                    }else{
                        addSubject.text = ""
                    }
                } else {
                    Toast.makeText(this, getString(R.string.select_resource_error), Toast.LENGTH_SHORT).show()
                }
            } else if (requestCode == REQUEST_CODE_FILE) {

                if (data!!.hasExtra("path")) {
                    val filePath = data.getStringExtra("path")
                    if (filePath != null) {
                        val file = File(filePath)
                        val md5Data = ""
                        if (file.exists()) {
                            var emailAttachEntity = EmailAttachEntity()
                            emailAttachEntity.isHasData = true
                            emailAttachEntity.localPath = file.path
                            emailAttachEntity.name = file.path.substring(file.path.lastIndexOf("/")+1,file.path.length)
                            emailAttachEntity.isCanDelete = true
                            emaiAttachAdapter!!.addData(0,emailAttachEntity)
                            emaiAttachAdapter!!.notifyDataSetChanged();
                            if( emaiAttachAdapter!!.itemCount > 1)
                            {
                                addSubject.text = (emaiAttachAdapter!!.itemCount -1).toString()
                            }else{
                                addSubject.text = ""
                            }
                        }
                    }
                } else {
                    val fileData = data.getParcelableExtra<JPullFileListRsp.ParamsBean.PayloadBean>("fileData")
                    //sendFileFileForward(fileData)
                }
            } else if (requestCode == REQUEST_CODE_TO) {
                toAdressEdit.requestFocus()
                if (data!!.hasExtra("selectAdressStr")) {
                    var selectAdressStr = data!!.getStringExtra("selectAdressStr")
                    var nameAdressStr = data!!.getStringExtra("nameAdressStr")
                    var selectAdressStrArray = selectAdressStr.split(",")
                    var nameAdressStrArray = nameAdressStr.split(",")
                    var i = 0;
                    for (item in selectAdressStrArray)
                    {
                        if(!oldAdress.contains(item))
                        {
                            var adress = item
                            var name =  nameAdressStrArray.get(i)
                            var user = User(adress,name)
                            (toAdressEdit.text as SpannableStringBuilder)
                                    .append(methodContext.newSpannable(user))
                                    .append(",")
                        }
                        i++;
                    }
                    sendCheck(false)
                }
            } else if (requestCode == REQUEST_CODE_CC) {
                ccAdressEdit.requestFocus()
                if (data!!.hasExtra("selectAdressStr")) {
                    var selectAdressStr = data!!.getStringExtra("selectAdressStr")
                    var nameAdressStr = data!!.getStringExtra("nameAdressStr")
                    var selectAdressStrArray = selectAdressStr.split(",")
                    var nameAdressStrArray = nameAdressStr.split(",")
                    var i = 0;
                    for (item in selectAdressStrArray)
                    {
                        if(!oldAdress.contains(item))
                        {
                            var adress = item
                            var name =  nameAdressStrArray.get(i)
                            var user = User(adress,name)
                            (ccAdressEdit.text as SpannableStringBuilder)
                                    .append(methodContext.newSpannable(user))
                                    .append(",")
                        }
                        i++;
                    }
                    sendCheck(false)
                }
            } else if (requestCode == REQUEST_CODE_BCC) {
                bccAdressEdit.requestFocus()
                if (data!!.hasExtra("selectAdressStr")) {
                    var selectAdressStr = data!!.getStringExtra("selectAdressStr")
                    var nameAdressStr = data!!.getStringExtra("nameAdressStr")
                    var selectAdressStrArray = selectAdressStr.split(",")
                    var nameAdressStrArray = nameAdressStr.split(",")
                    var i = 0;
                    for (item in selectAdressStrArray)
                    {
                        if(!oldAdress.contains(item))
                        {
                            var adress = item
                            var name =  nameAdressStrArray.get(i)
                            var user = User(adress,name)
                            (bccAdressEdit.text as SpannableStringBuilder)
                                    .append(methodContext.newSpannable(user))
                                    .append(",")
                        }
                        i++;
                    }
                    sendCheck(false)
                }
            }

        }
    }
    override fun setupActivityComponent() {
        DaggerEmailSendComponent
                .builder()
                .appComponent((application as AppConfig).applicationComponent)
                .emailSendModule(EmailSendModule(this))
                .build()
                .inject(this)
    }
    override fun setPresenter(presenter: EmailSendContract.EmailSendContractPresenter) {
        mPresenter = presenter as EmailSendPresenter
    }
    override fun onBackPressed() {
        super.onBackPressed()
    }
    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }
    private fun switch() {
        val method = circularMethod()
        methodContext.method = method
        methodContext.init(toAdressEdit)
        methodContextCc.method = method
        methodContextCc.init(ccAdressEdit)
        methodContextBcc.method = method
        methodContextBcc.init(bccAdressEdit)
    }
    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            var toAdress = toAdressEdit.text.toString()
            var subject = subject.text.toString()
            var re_main_editorStr = re_main_editor.html
            if(toAdress!= "" || subject!= "" || re_main_editorStr!= "")
            {
                showDialog()
            }else{
                finish()
            }

        }
        return true
    }
    fun showDialog() {
        SweetAlertDialog(this, SweetAlertDialog.BUTTON_NEUTRAL)
                .setCancelText(getString(R.string.no))
                .setConfirmText(getString(R.string.yes))
                .setContentText(getString(R.string.Save_it_in_the_draft_box))
                .setConfirmClickListener {
                    sendEmail(false)
                }.setCancelClickListener {
                    finish()
                }
                .show()

    }
    private tailrec fun circularMethod(): Method {
        return if (iterator.hasNext()) {
            iterator.next()
        } else {
            iterator = methods.iterator()
            circularMethod()
        }
    }
    override fun onDestroy() {
        AppConfig.instance.messageReceiver?.checkmailUkeyCallback = null
        super.onDestroy()
    }
}