package com.example.myapplication.logic;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.widget.TextView;

import com.example.myapplication.ui.CircleManager;

public class WifiChecker {
    private final Context context;
    private final CircleManager circleManager;
    private final TextView instructionText;

    public WifiChecker(Context context, CircleManager circleManager, TextView instructionText) {
        this.context = context;
        this.circleManager = circleManager;
        this.instructionText = instructionText;
    }

    public void check() {
        circleManager.fillCircleIfNotFilled(0, () -> {
            WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            if (wifiManager == null || !wifiManager.isWifiEnabled()) {
                instructionText.setText("Wi-Fi is disabled.");
                return;
            }

            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            String ssid = wifiInfo.getSSID();
            if (ssid == null || ssid.equals("<unknown ssid>") || wifiInfo.getNetworkId() == -1) {
                instructionText.setText("Not connected to Wi-Fi.");
                return;
            }

            ssid = ssid.replace("\"", "").toLowerCase();
            if (ssid.contains("guest") || ssid.contains("free") || ssid.contains("public")) {
                instructionText.setText("Unsecure Wi-Fi detected.");
                return;
            }

            circleManager.fill(0);
            instructionText.setText("Wi-Fi is secure.\nConnected to: " + ssid);
        });
    }
}