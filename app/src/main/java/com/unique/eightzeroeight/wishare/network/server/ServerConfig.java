package com.unique.eightzeroeight.wishare.network.server;


import com.unique.eightzeroeight.wishare.network.base.DeviceData;

public class ServerConfig {
    private static int func;
    private static DeviceData deviceData;

    public static int getFunc() {
        return func;
    }

    public static void setFunc(int func) {
        ServerConfig.func = func;
    }

    public static DeviceData getDeviceData() {
        return deviceData;
    }

    public static void setDeviceData(DeviceData deviceData) {
        ServerConfig.deviceData = deviceData;
    }
}
