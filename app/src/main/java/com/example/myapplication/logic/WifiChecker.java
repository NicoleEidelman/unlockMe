package com.example.myapplication.logic;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.widget.TextView;

import com.example.myapplication.ui.CircleManager;

public class WifiChecker {
    private final Context context;
    private final CircleManager circleManager;

    public WifiChecker(Context context, CircleManager circleManager) {
        this.context = context;
        this.circleManager = circleManager;
    }

    public void check() {
        circleManager.fillCircleIfNotFilled(0, () -> {
            WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            if (wifiManager == null || !wifiManager.isWifiEnabled()) {
                return;
            }

            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            String ssid = wifiInfo.getSSID();
            if (ssid == null || ssid.equals("<unknown ssid>") || wifiInfo.getNetworkId() == -1) {
                return;
            }

            ssid = ssid.replace("\"", "").toLowerCase();
            if (ssid.contains("guest") || ssid.contains("free") || ssid.contains("public")) {
                return;
            }

            circleManager.fill(0);
        });
    }
}