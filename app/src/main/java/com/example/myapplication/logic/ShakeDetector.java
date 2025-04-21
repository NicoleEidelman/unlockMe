package com.example.myapplication.logic;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.widget.TextView;

public class ShakeDetector implements SensorEventListener {
    private static final float SHAKE_THRESHOLD = 12.0f;
    private static final int SHAKE_WAIT_TIME_MS = 500;

    private final SensorManager sensorManager;
    private final Sensor accelerometer;
    private final OnShakeListener listener;
    private long lastShakeTime;

    public interface OnShakeListener {
        void onShake();
    }

    public ShakeDetector(Context context, OnShakeListener listener) {
        this.listener = listener;
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager != null ? sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) : null;
    }

    public void start() {
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        }
    }

    public void stop() {
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        double acceleration = Math.sqrt(x * x + y * y + z * z) - SensorManager.GRAVITY_EARTH;

        long now = System.currentTimeMillis();
        if (acceleration > SHAKE_THRESHOLD && now - lastShakeTime > SHAKE_WAIT_TIME_MS) {
            lastShakeTime = now;
            listener.onShake();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    public void showInstruction() {
    }
}
