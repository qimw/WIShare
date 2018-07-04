package com.unique.eightzeroeight.wishare.Entities;

/**
 * Created by wqm on 18-7-3.
 */

public class WifiInfo {
    private String ssid;

    public WifiInfo() {
    }

    public WifiInfo(String ssid, String pwd) {

        this.ssid = ssid;
        this.pwd = pwd;
    }

    public String getSsid() {

        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }
    private String pwd;

    @Override
    public String toString() {
        return "ssid:" + ssid + " pwd:" + pwd;
    }
}
