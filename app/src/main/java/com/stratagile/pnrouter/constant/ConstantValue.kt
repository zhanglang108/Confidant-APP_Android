package com.stratagile.pnrouter.constant

import java.util.HashMap

object ConstantValue {
        var localPath = "/RouterData6"
        var isInit = false;
        var isRefeshed = false
        var testValue = "testValue"
        var fingerprintUnLock = "fingerprintUnLock"
        var fingerPassWord = "fingerPassWord" //指纹密码
        var userId = "UserId"
        var username = "username"
        var routerId = "routerId"
        var selfImageName = "selfImageName"
        var msgId = ""
        var LOCALVERSIONCODE = "localversioncode"
        var selectFriend = "selectFriend"
        var realKeyboardHeight = "realKeyboardHeight"
        var port =":18006"
        var currentIp= ""//当前选择的ip
        var filePort =":18007"
        var status = "status"
        var msgIndex:Int = 1 //客户端的消息ID
        var privateRAS:String = ""
        var publicRAS:String = ""
        var updRouterData: HashMap<String,String> = HashMap()
        var scanRouterId:String =""//二维码扫出来的路由id
        var scanRouterSN:String =""//二维码扫出来的usersn
        var currentRouterIp:String = "192.168.1.103"//记录组播寻找到的路由器ip
        var currentRouterId:String = "B888FAC730AE4117A628E7743A3E7AA137896CFE79F03BE76DBFAD39DE450934C7E4D5952ED3"//记录组播寻找到的路由器id
        var currentRouterSN:String = "0200000DB827EBD089CB00005BFFDFD3"//记录组播寻找到的路由器SN  02000018B827EBD089CB00005BFB70B9     02000006B827EBD4703000005BFDF5E8 华为
        var isRegister:Boolean = false //记录当前选择路由器是否注册
        var isConnected:Boolean = false
        var shouBegin:Long = 0;
        var shouEnd:Long = 0;

}
