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
    private TextView instructionText;

    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        instructionText = findViewById(R.id.instructionText);

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

        circleManager = new CircleManager(circles, this, instructionText);

        voiceCommandRecognizer = new VoiceCommandRecognizer(this,
                command -> circleManager.fillCircleIfNotFilled(5, () -> circleManager.fill(5)),
                instructionText);

        wifiChecker = new WifiChecker(this, circleManager, instructionText);
        timeChecker = new TimeChecker(this, circleManager, instructionText);
        shakeDetector = new ShakeDetector(this, () -> circleManager.fill(2), instructionText);
        twoFactorAuthenticator = new TwoFactorAuthenticator(this, circleManager, instructionText);
        chargingStatusChecker = new ChargingStatusChecker(this, circleManager, instructionText);


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
            instructionText.setText("Checking your Wi-Fi connection...");
            circleManager.fillCircleIfNotFilled(0, () -> wifiChecker.check());
        } else if (id == R.id.circle2) {
            instructionText.setText("Checking if the current time matches the security condition...");
            circleManager.fillCircleIfNotFilled(1, () -> timeChecker.check());
        } else if (id == R.id.circle3) {
            shakeDetector.showInstruction();
            circleManager.fillCircleIfNotFilled(2, null);
        } else if (id == R.id.circle4) {
            instructionText.setText("Starting two-factor authentication...");
            circleManager.fillCircleIfNotFilled(3, () -> twoFactorAuthenticator.initiate());
        } else if (id == R.id.circle5) {
            instructionText.setText("Checking charging status...");
            circleManager.fillCircleIfNotFilled(4, () -> chargingStatusChecker.check());
        }
    }

    public void onVoiceClick(View view) {
        instructionText.setText("Say 'open' to complete the final step.");
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