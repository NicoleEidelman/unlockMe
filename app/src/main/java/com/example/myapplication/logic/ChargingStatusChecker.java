package com.example.myapplication.logic;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.widget.TextView;

import com.example.myapplication.ui.CircleManager;

public class ChargingStatusChecker {
    private final Context context;
    private final CircleManager circleManager;


    public ChargingStatusChecker(Context context, CircleManager circleManager) {
        this.context = context;
        this.circleManager = circleManager;
    }


    public void check() {
        circleManager.fillCircleIfNotFilled(4, () -> {
            Intent batteryStatus = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
            int status = batteryStatus != null ? batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1) : -1;

            boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL;
            if (isCharging) {
                circleManager.fill(4);
            } else {

            }
        });
    }
}
