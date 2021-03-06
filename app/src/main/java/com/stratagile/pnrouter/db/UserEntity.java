package com.stratagile.pnrouter.db;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

import java.util.Objects;

@Entity
public class UserEntity implements Parcelable{
    @Id(autoincrement = true)
    private Long id;
    //用户id
    private String userId;
    //用户hashid，14个字节，不能为空
    private String index;

    //路由器用户id
    private String routerUserId;

    private String signPublicKey;

    private String RouteId;

    private String RouteName;

    private String routerAlias;

    private String miPublicKey;

    //昵称
    private String nickName;

    //用户备注
    private String remarks;
    //昵称
    private String nickSouceName;
    //头像
    private String avatar;
    //备注名字
    private String noteName;
    //好友状态， 0 好友， 1 等待对方同意，2 对方决绝， 3 等待我同意， 4 对方删除我， 5 我拒绝， 6 我删除对方 7 什么都不是，等待发起加好友
    private int friendStatus;
    //是否为我加对方
    private boolean addFromMe;
    //第一次通信的时间戳
    private long timestamp;

    private String validationInfo;//添加好友附言

    protected UserEntity(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readLong();
        }
        userId = in.readString();
        index = in.readString();
        routerUserId = in.readString();
        signPublicKey = in.readString();
        RouteId = in.readString();
        RouteName = in.readString();
        routerAlias = in.readString();
        miPublicKey = in.readString();
        nickName = in.readString();
        remarks = in.readString();
        nickSouceName = in.readString();
        avatar = in.readString();
        noteName = in.readString();
        friendStatus = in.readInt();
        addFromMe = in.readByte() != 0;
        timestamp = in.readLong();
        validationInfo = in.readString();
    }

    public static final Creator<UserEntity> CREATOR = new Creator<UserEntity>() {
        @Override
        public UserEntity createFromParcel(Parcel in) {
            return new UserEntity(in);
        }

        @Override
        public UserEntity[] newArray(int size) {
            return new UserEntity[size];
        }
    };

    @Override
    public String toString() {
        return "UserEntity{" +
                "id=" + id +
                ", userId='" + userId + '\'' +
                ", index='" + index + '\'' +
                ", routerUserId='" + routerUserId + '\'' +
                ", signPublicKey='" + signPublicKey + '\'' +
                ", RouteId='" + RouteId + '\'' +
                ", RouteName='" + RouteName + '\'' +
                ", miPublicKey='" + miPublicKey + '\'' +
                ", nickName='" + nickName + '\'' +
                ", remarks='" + remarks + '\'' +
                ", nickSouceName='" + nickSouceName + '\'' +
                ", avatar='" + avatar + '\'' +
                ", noteName='" + noteName + '\'' +
                ", friendStatus=" + friendStatus +
                ", addFromMe=" + addFromMe +
                ", timestamp=" + timestamp +
                ", validationInfo='" + validationInfo + '\'' +
                '}';
    }

    @Generated(hash = 1512865517)
    public UserEntity(Long id, String userId, String index, String routerUserId,
            String signPublicKey, String RouteId, String RouteName, String routerAlias,
            String miPublicKey, String nickName, String remarks, String nickSouceName,
            String avatar, String noteName, int friendStatus, boolean addFromMe,
            long timestamp, String validationInfo) {
        this.id = id;
        this.userId = userId;
        this.index = index;
        this.routerUserId = routerUserId;
        this.signPublicKey = signPublicKey;
        this.RouteId = RouteId;
        this.RouteName = RouteName;
        this.routerAlias = routerAlias;
        this.miPublicKey = miPublicKey;
        this.nickName = nickName;
        this.remarks = remarks;
        this.nickSouceName = nickSouceName;
        this.avatar = avatar;
        this.noteName = noteName;
        this.friendStatus = friendStatus;
        this.addFromMe = addFromMe;
        this.timestamp = timestamp;
        this.validationInfo = validationInfo;
    }

    @Generated(hash = 1433178141)
    public UserEntity() {
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserEntity)) return false;
        UserEntity that = (UserEntity) o;
        return getFriendStatus() == that.getFriendStatus() &&
                isAddFromMe() == that.isAddFromMe() &&
                getTimestamp() == that.getTimestamp() &&
                Objects.equals(getUserId(), that.getUserId()) &&
                Objects.equals(getRouterUserId(), that.getRouterUserId()) &&
                Objects.equals(getRouteId(), that.getRouteId()) &&
                Objects.equals(getRouteName(), that.getRouteName()) &&
                Objects.equals(getNickName(), that.getNickName()) &&
                Objects.equals(getNoteName(), that.getNoteName()) &&
                Objects.equals(getValidationInfo(), that.getValidationInfo());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getRouterUserId(), getRouteId(), getRouteName(), getNickName());
    }


    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getUserId() {
        return this.userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getNickName() {
        return this.nickName;
    }
    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
    public String getAvatar() {
        return this.avatar;
    }
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }


    public String getNoteName() {
        return this.noteName;
    }
    public void setNoteName(String noteName) {
        this.noteName = noteName;
    }
    public int getFriendStatus() {
        return this.friendStatus;
    }
    public void setFriendStatus(int friendStatus) {
        this.friendStatus = friendStatus;
    }
    public boolean getAddFromMe() {
        return this.addFromMe;
    }
    public void setAddFromMe(boolean addFromMe) {
        this.addFromMe = addFromMe;
    }
    public long getTimestamp() {
        return this.timestamp;
    }
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    public String getSignPublicKey() {
        return signPublicKey;
    }

    public void setSignPublicKey(String signPublicKey) {
        this.signPublicKey = signPublicKey;
    }
    public String getRouterUserId() {
        return this.routerUserId;
    }
    public void setRouterUserId(String routerUserId) {
        this.routerUserId = routerUserId;
    }
    public String getNickSouceName() {
        return this.nickSouceName;
    }
    public void setNickSouceName(String nickSouceName) {
        this.nickSouceName = nickSouceName;
    }
    public String getValidationInfo() {
        return this.validationInfo;
    }
    public void setValidationInfo(String validationInfo) {
        this.validationInfo = validationInfo;
    }
    public String getIndex() {
        return this.index;
    }
    public void setIndex(String index) {
        this.index = index;
    }

    public String getRouteId() {
        return RouteId;
    }

    public void setRouteId(String routeId) {
        RouteId = routeId;
    }

    public String getRouteName() {
        return RouteName;
    }

    public void setRouteName(String routeName) {
        RouteName = routeName;
    }

    public boolean isAddFromMe() {
        return addFromMe;
    }

    public String getMiPublicKey() {
        return this.miPublicKey;
    }
    public void setMiPublicKey(String miPublicKey) {
        this.miPublicKey = miPublicKey;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        if (id == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeLong(id);
        }
        parcel.writeString(userId);
        parcel.writeString(index);
        parcel.writeString(routerUserId);
        parcel.writeString(signPublicKey);
        parcel.writeString(RouteId);
        parcel.writeString(RouteName);
        parcel.writeString(routerAlias);
        parcel.writeString(miPublicKey);
        parcel.writeString(nickName);
        parcel.writeString(remarks);
        parcel.writeString(nickSouceName);
        parcel.writeString(avatar);
        parcel.writeString(noteName);
        parcel.writeInt(friendStatus);
        parcel.writeByte((byte) (addFromMe ? 1 : 0));
        parcel.writeLong(timestamp);
        parcel.writeString(validationInfo);
    }

    public String getRouterAlias() {
        return this.routerAlias;
    }

    public void setRouterAlias(String routerAlias) {
        this.routerAlias = routerAlias;
    }
}
