package com.stratagile.pnrouter.ui.activity.main

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.CheckBox
import chat.tox.antox.tox.MessageHelper
import chat.tox.antox.wrapper.FriendKey
import com.hyphenate.chat.EMMessage
import com.pawegio.kandroid.runOnUiThread
import com.socks.library.KLog

import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseFragment
import com.stratagile.pnrouter.ui.activity.main.component.DaggerContactComponent
import com.stratagile.pnrouter.ui.activity.main.contract.ContactContract
import com.stratagile.pnrouter.ui.activity.main.module.ContactModule
import com.stratagile.pnrouter.ui.activity.main.presenter.ContactPresenter

import javax.inject.Inject;

import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.data.web.PNRouterServiceMessageReceiver
import com.stratagile.pnrouter.db.FriendEntity
import com.stratagile.pnrouter.db.UserEntity
import com.stratagile.pnrouter.db.UserEntityDao
import com.stratagile.pnrouter.entity.AddFriendReq
import com.stratagile.pnrouter.entity.BaseData
import com.stratagile.pnrouter.entity.JPullFriendRsp
import com.stratagile.pnrouter.entity.PullFriendReq
import com.stratagile.pnrouter.entity.events.EditNickName
import com.stratagile.pnrouter.entity.events.FriendChange
import com.stratagile.pnrouter.entity.events.SelectFriendChange
import com.stratagile.pnrouter.entity.events.UnReadContactCount
import com.stratagile.pnrouter.ui.activity.user.NewFriendActivity
import com.stratagile.pnrouter.ui.activity.user.UserInfoActivity
import com.stratagile.pnrouter.ui.adapter.user.ContactListAdapter
import com.stratagile.pnrouter.utils.LogUtil
import com.stratagile.pnrouter.utils.RxEncodeTool
import com.stratagile.pnrouter.utils.SpUtil
import com.stratagile.pnrouter.utils.baseDataToJson
import com.stratagile.tox.toxcore.ToxCoreJni
import im.tox.tox4j.core.enums.ToxMessageType
import kotlinx.android.synthetic.main.ease_search_bar.*
import kotlinx.android.synthetic.main.fragment_contact.*
import kotlinx.android.synthetic.main.fragment_my.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*
import kotlin.collections.ArrayList

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.main
 * @Description: $description
 * @date 2018/09/10 17:33:27
 */

class ContactFragment : BaseFragment(), ContactContract.View, PNRouterServiceMessageReceiver.PullFriendCallBack {
    override fun firendList(jPullFriendRsp: JPullFriendRsp) {
        var localFriendList = AppConfig.instance.mDaoMaster!!.newSession().userEntityDao.loadAll()
        var localFriendStatusList = AppConfig.instance.mDaoMaster!!.newSession().friendEntityDao.loadAll()
        var userId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
        if (jPullFriendRsp.params.payload == null || jPullFriendRsp.params.payload.size ==0) {
            /*for (i in localFriendList) {
                //是否为本地多余的好友
                if (i.friendStatus == 3 || i.friendStatus == 1) {
                    //等待验证的好友，不能处理
                    continue
                } else {
                    AppConfig.instance.mDaoMaster!!.newSession().userEntityDao.delete(i)
                }
            }*/
            var localFriendStatusList = AppConfig.instance.mDaoMaster!!.newSession().friendEntityDao.loadAll()
            for (i in localFriendStatusList) {
                if (i.userId.equals(userId)) {
                    if (i.friendLocalStatus == 3 || i.friendLocalStatus == 1) {
                        continue
                    }else{
                        i.friendLocalStatus = 7
                        AppConfig.instance.mDaoMaster!!.newSession().friendEntityDao.update(i)
                    }
                }

            }
            runOnUiThread {
                initData()
            }
            return
        }
        //添加新的好友
        for (i in jPullFriendRsp.params.payload) {
            //是否为本地好友
            var isLocalFriend = false
            for (j in localFriendList) {
                if (i.id.equals(j.userId)) {
                    isLocalFriend = true
                    //j.friendStatus = 0
                    j.nickName = i.name
                    j.remarks = i.remarks
                    j.publicKey = i.userKey
                    AppConfig.instance.mDaoMaster!!.newSession().userEntityDao.update(j)
                    break
                }
            }
            if (!isLocalFriend) {
                var userEntity = UserEntity()
                userEntity.nickName = i.name
                userEntity.userId = i.id
                userEntity.publicKey = i.userKey
                userEntity.remarks =i.remarks
                //userEntity.friendStatus = 0
                userEntity.timestamp = Calendar.getInstance().timeInMillis
                var selfUserId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
                userEntity.routerUserId = selfUserId
                AppConfig.instance.mDaoMaster!!.newSession().userEntityDao.insert(userEntity)
            }
            var isLocalFriendStatus = false
            for (j in localFriendStatusList) {
                if (i.id.equals(j.friendId) && j.userId.equals(userId)) {
                    isLocalFriendStatus = true
                    j.friendLocalStatus = 0
                    AppConfig.instance.mDaoMaster!!.newSession().friendEntityDao.update(j)
                    break
                }
            }
            if (!isLocalFriendStatus) {
                var friendEntity = FriendEntity()
                friendEntity.userId = userId
                friendEntity.friendId = i.id
                friendEntity.friendLocalStatus = 0
                friendEntity.timestamp = Calendar.getInstance().timeInMillis
                AppConfig.instance.mDaoMaster!!.newSession().friendEntityDao.insert(friendEntity)
            }
        }
        //把本地的多余好友清除
      /*  localFriendList = AppConfig.instance.mDaoMaster!!.newSession().userEntityDao.loadAll()
        for (i in localFriendList) {
            //是否为本地多余的好友
            if (i.friendStatus == 3 || i.friendStatus == 1) {
                //等待验证的S好友，不能处理
                continue
            }
            var isLocalDeletedFriend = true
            for (j in jPullFriendRsp.params.payload) {
                if (i.userId.equals(j.id)) {
                    isLocalDeletedFriend = false
                }
            }
            if (isLocalDeletedFriend) {
                i.friendStatus = 7
                AppConfig.instance.mDaoMaster!!.newSession().userEntityDao.update(i)
            }
        }*/
        //把本地的多余好友清除
        localFriendStatusList = AppConfig.instance.mDaoMaster!!.newSession().friendEntityDao.loadAll()
        LogUtil.addLog("localFriendStatusList:"+localFriendStatusList.size,"ContactFragment")
        for (i in localFriendStatusList) {
            if (i.userId.equals(userId)) {
                LogUtil.addLog("freindStatus:"+i.userId+"_"+ i.friendId+"_"+i.friendLocalStatus,"ContactFragment")
                //是否为本地多余的好友
                if (i.friendLocalStatus == 3 || i.friendLocalStatus == 1) {
                    //等待验证的S好友，不能处理
                    continue
                }

                var isLocalDeletedFriend = true
                for (j in jPullFriendRsp.params.payload) {
                    if (i.friendId.equals(j.id)) {
                        LogUtil.addLog("freindName:"+ j.name,"ContactFragment")
                        isLocalDeletedFriend = false
                    }
                }
                if (isLocalDeletedFriend) {
                    LogUtil.addLog("deletefreindName:"+ i.friendId,"ContactFragment")
                    i.friendLocalStatus = 7
                    AppConfig.instance.mDaoMaster!!.newSession().friendEntityDao.update(i)
                }
            }else{
                LogUtil.addLog("freindStatusOther:"+i.userId+"_"+ i.friendId+"_"+i.friendLocalStatus,"ContactFragment")
            }

        }
        runOnUiThread {
            initData()
        }
    }

    @Inject
    lateinit internal var mPresenter: ContactPresenter

    var contactAdapter : ContactListAdapter? = null

    lateinit var viewModel : MainViewModel

    var  fromId:String? = null
    var message: EMMessage? = null
    var onViewCreated = false;

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        fromId = null
        if(arguments != null && arguments!!.get("fromId") != null)
        {
            fromId = arguments!!.get("fromId") as String
            message = arguments!!.get("message") as EMMessage
        }

        var view = inflater.inflate(R.layout.fragment_contact, null);
        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        EventBus.getDefault().register(this)
        viewModel = ViewModelProviders.of(activity!!).get(MainViewModel::class.java)

        try {
            AppConfig.instance.messageReceiver!!.pullFriendCallBack = this
        }catch (e :Exception )
        {
            var aa = ""
        }
        viewModel.freindChange.observe(this, Observer<Long> { friendChange ->
            initData()
        })
        newFriend.setOnClickListener {
            startActivity(Intent(activity!!, NewFriendActivity::class.java))
        }
        initData()
        refreshLayout.setOnRefreshListener {
            pullFriendList()
            KLog.i("拉取好友列表")
        }
        pullFriendList()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun friendChange(friendChange: FriendChange) {
        initData()
    }
    fun pullFriendList() {
        if(refreshLayout != null)
            refreshLayout.isRefreshing = false
        var selfUserId = SpUtil.getString(activity!!, ConstantValue.userId, "")
        var pullFriend = PullFriendReq( selfUserId!!)
        if (ConstantValue.isWebsocketConnected) {
            Log.i("pullFriendList", "webosocket" + AppConfig.instance.getPNRouterServiceMessageSender())
            AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(pullFriend))
        }else if (ConstantValue.isToxConnected) {
            Log.i("pullFriendList", "tox")
            var baseData = BaseData(pullFriend)
            var baseDataJson = baseData.baseDataToJson().replace("\\", "")
            var friendKey: FriendKey = FriendKey(ConstantValue.currentRouterId.substring(0, 64))
            ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.currentRouterId.substring(0, 64))
//            MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL)
        }

    }
    fun updata()
    {
        pullFriendList()
    }
    fun updataUI()
    {
        var list = AppConfig.instance.mDaoMaster!!.newSession().userEntityDao.loadAll()
        var  contactList = arrayListOf<UserEntity>()
        var selfUserId = SpUtil.getString(activity!!, ConstantValue.userId, "")
        var newFriendCount = 0
        /*for (i in list) {

            if (i.userId.equals(selfUserId)) {
                continue
            }
            if(fromId != null && !fromId.equals(""))
            {
                if (i.userId.equals(fromId)) {
                    continue
                }
            }
            if (i.routerUserId !=null && i.routerUserId.equals(selfUserId)) {
                if (i.friendStatus == 0) {
                    contactList.add(i)
                }
                if (i.friendStatus == 3) {
                    newFriendCount++
                }
            }else{
                if (i.friendStatus == 0) {
                    contactList.add(i)
                }
            }

        }*/
        var localFriendStatusList = AppConfig.instance.mDaoMaster!!.newSession().friendEntityDao.loadAll()
        var userId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
        for (i in localFriendStatusList) {
            if (i.userId.equals(userId)) {
                if (i.friendLocalStatus == 0) {
                    var it = UserEntity()
                    var localFriendList = AppConfig.instance.mDaoMaster!!.newSession().userEntityDao.queryBuilder().where(UserEntityDao.Properties.UserId.eq(i.friendId)).list()
                    if (localFriendList.size > 0)
                        it = localFriendList.get(0)
                    contactList.add(it)
                }
                if (i.friendLocalStatus == 3) {
                    newFriendCount++
                }
            }

        }
        for (i in contactList) {
            if (i?.remarks != null && i?.remarks != "") {
                i.nickSouceName = String(RxEncodeTool.base64Decode(i.remarks)).toLowerCase()
            }else{
                i.nickSouceName = String(RxEncodeTool.base64Decode(i.nickName)).toLowerCase()
            }
        }
        contactList.sortBy {
            it.nickSouceName
        }
        contactAdapter!!.setNewData(contactList);
    }
    fun initData() {
        var bundle = getArguments();
        var hasNewFriendRequest = false
        var list = AppConfig.instance.mDaoMaster!!.newSession().userEntityDao.loadAll()
        var  contactList = arrayListOf<UserEntity>()
        var selfUserId = SpUtil.getString(activity!!, ConstantValue.userId, "")
        var newFriendCount = 0
       /* for (i in list) {

            if (i.userId.equals(selfUserId)) {
                continue
            }
            if(fromId != null && !fromId.equals(""))
            {
                if (i.userId.equals(fromId)) {
                    continue
                }
            }
            if (i.routerUserId !=null && i.routerUserId.equals(selfUserId)) {
                if (i.friendStatus == 0) {
                    contactList.add(i)
                }
                if (i.friendStatus == 3) {
                    hasNewFriendRequest = true
                    newFriendCount++
                }
            }else{
                if (i.friendStatus == 0) {
                    contactList.add(i)
                }
            }

        }*/
        var localFriendStatusList = AppConfig.instance.mDaoMaster!!.newSession().friendEntityDao.loadAll()
        var userId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
        for (i in localFriendStatusList) {
            if (i.userId.equals(userId)) {
                if (i.friendLocalStatus == 0) {
                    var it = UserEntity()
                    var localFriendList = AppConfig.instance.mDaoMaster!!.newSession().userEntityDao.queryBuilder().where(UserEntityDao.Properties.UserId.eq(i.friendId)).list()
                    if (localFriendList.size > 0)
                        it = localFriendList.get(0)
                    contactList.add(it)
                }
                if (i.friendLocalStatus == 3) {
                    hasNewFriendRequest = true
                    newFriendCount++
                }
            }

        }
        if (hasNewFriendRequest) {
            new_contact_dot.visibility = View.VISIBLE
            EventBus.getDefault().post(UnReadContactCount(newFriendCount))
        } else {
            new_contact_dot.visibility = View.GONE
            EventBus.getDefault().post(UnReadContactCount(0))
        }
        for (i in contactList) {
            if (i?.remarks != null && i?.remarks != "") {
                i.nickSouceName = String(RxEncodeTool.base64Decode(i.remarks)).toLowerCase()
            }else{
                i.nickSouceName = String(RxEncodeTool.base64Decode(i.nickName)).toLowerCase()
            }
        }
        contactList.sortBy {
            it.nickSouceName
        }
        if(bundle == null)
        {
            newFriend.visibility = View.VISIBLE
            contactAdapter = ContactListAdapter(contactList,false)
        }else{
            newFriend.visibility = View.GONE
            contactAdapter = ContactListAdapter(contactList,true)
        }
        recyclerView.adapter = contactAdapter
        contactAdapter!!.setOnItemClickListener { adapter, view, position ->
            if(bundle == null)
            {
                var intent = Intent(activity!!, UserInfoActivity::class.java)
                intent.putExtra("user", contactAdapter!!.getItem(position))
                startActivity(intent)
            }else{
                var checkBox =  contactAdapter!!.getViewByPosition(recyclerView,position,R.id.checkBox) as CheckBox
                checkBox.setChecked(!checkBox.isChecked)
                var itemCount =  contactAdapter!!.itemCount -1
                var count :Int = 0;
                for (i in 0..itemCount) {
                    var checkBox =  contactAdapter!!.getViewByPosition(recyclerView,i,R.id.checkBox) as CheckBox
                    if(checkBox.isChecked)
                    {
                        count ++
                    }
                }
                EventBus.getDefault().post(SelectFriendChange(count,0))
            }

        }


        query.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                fiter(s.toString(),contactList)
                if (s.length > 0) {
                    search_clear.setVisibility(View.VISIBLE)
                } else {
                    search_clear.setVisibility(View.INVISIBLE)
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun afterTextChanged(s: Editable) {

            }
        })
        search_clear.setOnClickListener(View.OnClickListener {
            query.getText().clear()
            //hideSoftKeyboard()
        })
    }
    fun fiter(key:String,contactList:ArrayList<UserEntity>)
    {
       var contactListTemp:ArrayList<UserEntity> = arrayListOf<UserEntity>()
        for (i in contactList) {
            if(i.nickSouceName.toLowerCase().contains(key))
            {
                contactListTemp.add(i)
            }
        }
        contactAdapter!!.setNewData(contactListTemp)
    }

    fun selectOrCancelAll()
    {
        var itemCount =  contactAdapter!!.itemCount -1

        for (i in 0..itemCount) {
            var checkBox =  contactAdapter!!.getViewByPosition(recyclerView,i,R.id.checkBox) as CheckBox
            checkBox.setChecked(!checkBox.isChecked)
        }
        var count :Int = 0;
        for (i in 0..itemCount) {
            var checkBox =  contactAdapter!!.getViewByPosition(recyclerView,i,R.id.checkBox) as CheckBox
            if(checkBox.isChecked)
            {
                count ++
            }
        }
        EventBus.getDefault().post(SelectFriendChange(count,0))
    }
    fun getAllSelectedFriend() : ArrayList<UserEntity>
    {
        var contactList = arrayListOf<UserEntity>()
        var itemCount =  contactAdapter!!.itemCount -1
        for (i in 0..itemCount) {
            var checkBox =  contactAdapter!!.getViewByPosition(recyclerView,i,R.id.checkBox) as CheckBox
            if(checkBox.isChecked)
            {
                contactList.add( contactAdapter!!.getItem(i) as UserEntity)
            }
        }
        return contactList!!
    }
    override fun setupFragmentComponent() {
        DaggerContactComponent
                .builder()
                .appComponent((activity!!.application as AppConfig).applicationComponent)
                .contactModule(ContactModule(this))
                .build()
                .inject(this)
    }

    override fun setPresenter(presenter: ContactContract.ContactContractPresenter) {
        mPresenter = presenter as ContactPresenter
    }

    override fun initDataFromLocal() {

    }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
        if(AppConfig.instance.messageReceiver != null)
            AppConfig.instance.messageReceiver!!.pullFriendCallBack = null
    }
}