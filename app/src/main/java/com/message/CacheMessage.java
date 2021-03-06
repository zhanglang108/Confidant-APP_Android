package com.message;

import com.stratagile.pnrouter.application.AppConfig;
import com.stratagile.pnrouter.constant.ConstantValue;
import com.stratagile.pnrouter.utils.Base58;
import com.stratagile.pnrouter.utils.SpUtil;

import java.util.Calendar;

public class CacheMessage {

    /**
     * MsgId : 1537262116
     * MsgType : 1
     * TimeStatmp : 1537262116
     * From : EEA02E58D797E4C2D34AA5727A5547FD415A21AFD255CE4825F05836FC1D0267ACF17C109788
     * To : 14EB061F2A983B966B79030AF773AE74BE703315A4E56EA9D801DAC5DC840522C62EA32ECBC5
     * Msg : 。。。
     */

    private int MsgId;
    private int MsgType;
    private int DbId;
    private long TimeStatmp;
    private String From;
    private String To;
    private String Msg;
    private Type type;
    private int Status;
    private int Sender;
    private String FileName;
    private String  FilePath;
    private Long FileSize;
    private String UserKey;

    @Override
    public String toString() {
        return "Message{" +
                "MsgId=" + MsgId +
                "DbId=" + DbId +
                ", MsgType=" + MsgType +
                ", TimeStatmp=" + TimeStatmp +
                ", From='" + From + '\'' +
                ", To='" + To + '\'' +
                ", Msg='" + Msg + '\'' +
                ", type=" + type +
                ", Status=" + Status +
                ", unRead=" + unRead +
                '}';
    }
    public String getFileName() {
        //bas58解码
        //String FileNameOld = new String(RxEncodeTool.base64Decode(FileName.getBytes()));
        String FileNameOld = new String(Base58.decode(FileName));
        return FileNameOld;
    }

    public void setFileName(String fileName) {
        FileName = fileName;
    }

    public String getFilePath() {
        return FilePath;
    }

    public void setFilePath(String filePath) {
        FilePath = filePath;
    }
    public Long getFileSize() {
        return FileSize;
    }

    public void setFileSize(Long fileSize) {
        FileSize = fileSize;
    }

    public boolean isUnRead() {
        return unRead;
    }

    public void setUnRead(boolean unRead) {
        this.unRead = unRead;
    }

    private boolean unRead;

    public int getStatus() {
        return Status;
    }

    public void setStatus(int status) {
        this.Status = status;
    }
    public int getSender() {
        return Sender;
    }

    public void setSender(int sender) {
        Sender = sender;
    }
    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public int getMsgId() {
        return MsgId;
    }

    public void setMsgId(int MsgId) {
        this.MsgId = MsgId;
    }

    public int getMsgType() {
        return MsgType;
    }

    public void setMsgType(int MsgType) {
        this.MsgType = MsgType;
    }
    public int getDbId() {
        return DbId;
    }

    public void setDbId(int dbId) {
        DbId = dbId;
    }

    public long getTimeStatmp() {
        return TimeStatmp;
    }

    public void setTimeStatmp(long TimeStatmp) {
        this.TimeStatmp = TimeStatmp;
    }

    public String getFrom() {
        return From;
    }

    public void setFrom(String From) {
        this.From = From;
    }

    public String getTo() {
        return To;
    }

    public void setTo(String To) {
        this.To = To;
    }

    public String getMsg() {
       /* try{
            String encryptedBytes = new String(RxEncodeTool.base64Decode(Msg));
            return encryptedBytes;
        }catch (IllegalArgumentException e)
        {
            return Msg;
        }*/
        return Msg;
    }

    public void setMsg(String Msg) {
        this.Msg = Msg;
    }

    //1为发送，0为接受
    public int getItemType() {
        if (From.equals(SpUtil.INSTANCE.getString(AppConfig.instance, ConstantValue.INSTANCE.getUserId(), ""))) {
            return 1;
        } else {
            return 0;
        }
    }
    public String getUserKey() {
        return UserKey;
    }

    public void setUserKey(String userKey) {
        UserKey = userKey;
    }
    public static enum ChatType {
        Chat,
        GroupChat,
        ChatRoom;

        private ChatType() {
        }
    }

    public static enum Status {
        SUCCESS,//发送成功
        SENDED,//表示消息已推送到对端
        LOOKED,//表示消息对端已阅
        FAIL,//发送失败
        CREATE;//本地创建成功，为了显示

        private Status() {
        }
    }

    public static enum Direct {
        SEND,
        RECEIVE;

        private Direct() {
        }
    }

    public static enum Type {
        TXT,
        IMAGE,
        VIDEO,
        LOCATION,
        VOICE,
        FILE,
        CMD;

        private Type() {
        }
    }

    public void setType() {
        switch (MsgType) {
            case 0:
                type = Type.TXT;
                break;
            default:
                break;
        }
    }

}
