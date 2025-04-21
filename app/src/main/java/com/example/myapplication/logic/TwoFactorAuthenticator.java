package com.example.myapplication.logic;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.myapplication.ui.CircleManager;

import java.util.Random;

public class TwoFactorAuthenticator {
    private final Activity activity;
    private final CircleManager circleManager;

    private static final int PERMISSION_REQUEST_PHONE = 200;
    private static final int PERMISSION_REQUEST_SMS = 201;

    public TwoFactorAuthenticator(Activity activity, CircleManager circleManager) {
        this.activity = activity;
        this.circleManager = circleManager;
    }

    public void initiate() {
        circleManager.fillCircleIfNotFilled(3, () -> {
            String code = generateRandomCode();
            String number = getPhoneNumberFromSystem();

            if (number != null && number.length() >= 10) {
                sendSMSAndVerify(number, code);
            } else {
                askUserForPhoneNumber(code);
            }
        });
    }

    private String getPhoneNumberFromSystem() {
        TelephonyManager tm = (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{android.Manifest.permission.READ_PHONE_NUMBERS, android.Manifest.permission.READ_PHONE_STATE},
                    PERMISSION_REQUEST_PHONE);
            return null;
        }

        String number = tm.getLine1Number();
        if (number != null && number.startsWith("05")) {
            number = "+972" + number.substring(1);
        }
        return number;
    }

    private void askUserForPhoneNumber(String code) {
        EditText input = new EditText(activity);
        input.setHint("e.g. +972501234567");
        input.setInputType(InputType.TYPE_CLASS_PHONE);

        new AlertDialog.Builder(activity)
                .setTitle("Enter Phone Number")
                .setMessage("We couldn’t detect your number. Please enter it.")
                .setView(input)
                .setCancelable(false)
                .setPositiveButton("Send Code", (dialog, which) -> {
                    String userNumber = input.getText().toString().trim();
                    if (!userNumber.startsWith("+") || userNumber.length() < 11) {
                    } else {
                        sendSMSAndVerify(userNumber, code);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void sendSMSAndVerify(String phoneNumber, String code) {
        if (ContextCompat.checkSelfPermission(activity, android.Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{android.Manifest.permission.SEND_SMS}, PERMISSION_REQUEST_SMS);
            return;
        }

        SmsManager.getDefault().sendTextMessage(phoneNumber, null, "Your verification code is: " + code, null, null);
        showCodeDialog(code);
    }

    private void showCodeDialog(String expectedCode) {
        EditText input = new EditText(activity);
        input.setHint("4-digit code");
        input.setInputType(InputType.TYPE_CLASS_NUMBER);

        AlertDialog dialog = new AlertDialog.Builder(activity)
                .setTitle("Enter Verification Code")
                .setMessage("Enter the code we sent you")
                .setView(input)
                .setCancelable(false)
                .setPositiveButton("Verify", null)
                .setNegativeButton("Cancel", null)
                .create();

        dialog.setOnShowListener(d -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                String entered = input.getText().toString().trim();
                if (entered.equals(expectedCode)) {
                    circleManager.fill(3);
                    dialog.dismiss();
                } else {
                }
            });
        });

        dialog.show();
    }

    private String generateRandomCode() {
        return String.valueOf(1000 + new Random().nextInt(9000));
    }
}