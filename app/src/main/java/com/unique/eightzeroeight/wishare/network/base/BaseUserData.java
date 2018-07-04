package com.unique.eightzeroeight.wishare.network.base;

public class BaseUserData {
    public static final byte FIELD_TYPE_IP = 0x11;
    public static final byte FIELD_TYPE_PORT = 0x12;

    String ip;      // IP地址
    int port;       // 端口

    public BaseUserData() {
    }

    public BaseUserData(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    @Override
    public int hashCode() {
        return ip.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof BaseUserData) {
            return this.ip.equals(((BaseUserData) o).getIp());
        }
        return super.equals(o);
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
