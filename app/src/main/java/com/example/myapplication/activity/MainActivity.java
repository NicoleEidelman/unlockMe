package com.example.myapplication.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.myapplication.R;
import com.example.myapplication.logic.*;
import com.example.myapplication.ui.CircleManager;

public class MainActivity extends AppCompatActivity {

    private CircleManager circleManager;
    private WifiChecker wifiChecker;
    private TimeChecker timeChecker;
    private ShakeDetector shakeDetector;
    private TwoFactorAuthenticator twoFactorAuthenticator;
    private ChargingStatusChecker chargingStatusChecker;
    private VoiceCommandRecognizer voiceCommandRecognizer;

    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        FrameLayout[] frameCircles = new FrameLayout[]{
                findViewById(R.id.circle1),
                findViewById(R.id.circle2),
                findViewById(R.id.circle3),
                findViewById(R.id.circle4),
                findViewById(R.id.circle5),
                findViewById(R.id.circle6)
        };

        ImageView[] circles = new ImageView[frameCircles.length];
        for (int i = 0; i < frameCircles.length; i++) {
            circles[i] = (ImageView) frameCircles[i].getChildAt(0); // assumes the first child is the icon
        }

        circleManager = new CircleManager(circles, this);

        voiceCommandRecognizer = new VoiceCommandRecognizer(this,
                command -> circleManager.fillCircleIfNotFilled(5, () -> circleManager.fill(5)));

        wifiChecker = new WifiChecker(this, circleManager);
        timeChecker = new TimeChecker(this, circleManager);
        shakeDetector = new ShakeDetector(this, () -> circleManager.fill(2));
        twoFactorAuthenticator = new TwoFactorAuthenticator(this, circleManager);
        chargingStatusChecker = new ChargingStatusChecker(this, circleManager);


        String[] permissions = new String[]{
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.SEND_SMS,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.READ_PHONE_NUMBERS
        };

        ActivityCompat.requestPermissions(this, permissions, 123);
    }



    public void onCircleClick(View view) {
        int id = view.getId();
        if (id == R.id.circle1) {
            circleManager.vibrate();
            circleManager.fillCircleIfNotFilled(0, () -> wifiChecker.check());
        } else if (id == R.id.circle2) {
            circleManager.vibrate();
            circleManager.fillCircleIfNotFilled(1, () -> timeChecker.check());
        } else if (id == R.id.circle3) {
            circleManager.vibrate();
            shakeDetector.showInstruction();
            circleManager.fillCircleIfNotFilled(2, null);
        } else if (id == R.id.circle4) {
            circleManager.vibrate();
            circleManager.fillCircleIfNotFilled(3, () -> twoFactorAuthenticator.initiate());
        } else if (id == R.id.circle5) {
            circleManager.vibrate();
            circleManager.fillCircleIfNotFilled(4, () -> chargingStatusChecker.check());
        }
    }

    public void onVoiceClick(View view) {
        circleManager.vibrate();
        voiceCommandRecognizer.startListening();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (shakeDetector != null) shakeDetector.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (shakeDetector != null) shakeDetector.stop();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 123) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission denied: " + permissions[i], Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

}