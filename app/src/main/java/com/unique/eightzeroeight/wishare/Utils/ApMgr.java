package com.unique.eightzeroeight.wishare.Utils;

import android.content.Context;
import android.net.wifi.WifiConfiguration;

import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import com.unique.eightzeroeight.wishare.Entities.WifiInfo;

import java.lang.reflect.Method;




/**
 * 1.Prepare
 * use WifiManager class need the permission
 * <uses-permission android:name="android.permission.CHANGE_NETWORK_STATE" />
 * <uses-permission android:name="android.permission.CHANGE_WIFI_STATE" />
 * <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
 *
 * 2.Usage
 * ...start
 * ApMgr.isApOn(Context context); // check Ap state :boolean
 * ApMgr.configApState(Context context); // change Ap state :boolean
 * ...end
 *
 *
 * Created by mayubao on 2016/11/2.
 * Contact me 345269374@qq.com
 */
public class ApMgr {
    public static String TAG = "ApMgr";
    //check whether wifi hotspot on or off
    public static boolean isApOn(Context context) {
        WifiManager wifimanager = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
        try {
            Method method = wifimanager.getClass().getDeclaredMethod("isWifiApEnabled");
            method.setAccessible(true);
            return (Boolean) method.invoke(wifimanager);
        }
        catch (Throwable ignored) {}
        return false;
    }

    //close wifi hotspot
    public static void disableAp(Context context) {
        WifiManager wifimanager = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
        try {
            Method method = wifimanager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
            method.invoke(wifimanager, null, false);
        } catch (Throwable ignored) {

        }
    }

    // toggle wifi hotspot on or off
    public static boolean configApState(Context context) {
        WifiManager wifimanager = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
        WifiConfiguration wificonfiguration = null;
        try {
            // if WiFi is on, turn it off
            if(isApOn(context)) {
                wifimanager.setWifiEnabled(false);
                // if ap is on and then disable ap
                disableAp(context);
            }
            Method method = wifimanager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
            method.invoke(wifimanager, wificonfiguration, !isApOn(context));
            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    public interface OpenApCallback {
        void callBack(WifiInfo info);
    }

    // toggle wifi hotspot on or off, and specify the hotspot name
    public static void openAp(Context context, final OpenApCallback callback) {

        final WifiInfo info = new WifiInfo();
        if (Build.VERSION.SDK_INT >= 26) {
            WifiManager manager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);

            manager.startLocalOnlyHotspot(new WifiManager.LocalOnlyHotspotCallback() {
                @Override
                public void onStarted(WifiManager.LocalOnlyHotspotReservation reservation) {
                    super.onStarted(reservation);
                    String pwd = reservation.getWifiConfiguration().preSharedKey;
                    String ssid = reservation.getWifiConfiguration().SSID;
                    Log.d(TAG, "Wifi Hotspot is on now");
                    info.setPwd(pwd);
                    info.setSsid(ssid);
                    Log.i(TAG, info.toString());
                    callback.callBack(info);
                }

                @Override
                public void onStopped() {
                    super.onStopped();
                    Log.d(TAG, "onStopped: ");
                }

                @Override
                public void onFailed(int reason) {
                    super.onFailed(reason);
                    Log.d(TAG, "onFailed: ");
                }
            }, new Handler());
            Log.i(TAG, "return");
        } else {
            WifiManager wifimanager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            WifiConfiguration wificonfiguration = null;
            try {
                wificonfiguration = new WifiConfiguration();
                wificonfiguration.SSID = "AndroidShare_4766";
                // if WiFi is on, turn it off
                if (isApOn(context)) {
                    wifimanager.setWifiEnabled(false);
                    // if ap is on and then disable ap
                    disableAp(context);
                }
                wificonfiguration.preSharedKey = "123456789";
                wifimanager.setWifiEnabled(true);
                Method method = wifimanager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
                method.invoke(wifimanager, wificonfiguration, true);
                Log.i("WebTransferActivity", "start ap success");
                info.setSsid("AndroidShare_4766");
                info.setPwd("123456789");
                callback.callBack(info);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.i("WebTransferActivity", "start ap false");
        }
    }
} // end of class
