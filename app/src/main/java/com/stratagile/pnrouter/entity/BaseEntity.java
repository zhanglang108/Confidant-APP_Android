package com.stratagile.pnrouter.entity;

public class BaseEntity {

    /**
     * appid : MIFI
     * timestamp : 1536839565
     * apiversion : 1
     */

    private String appid;
    private int timestamp;
    private int apiversion;

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    public int getApiversion() {
        return apiversion;
    }

    public void setApiversion(int apiversion) {
        this.apiversion = apiversion;
    }
}