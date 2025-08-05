package com.example.myapplication.utils;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

public class NetworkUtils {
    public static String getGatewayIP(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (wifiManager == null) return null;

        DhcpInfo dhcpInfo = wifiManager.getDhcpInfo();
        if (dhcpInfo == null) return null;

        int gateway = dhcpInfo.gateway;
        return intToIp(gateway);
    }

    private static String intToIp(int ip) {
        return (ip & 0xFF) + "." +
                ((ip >> 8) & 0xFF) + "." +
                ((ip >> 16) & 0xFF) + "." +
                ((ip >> 24) & 0xFF);
    }
}
