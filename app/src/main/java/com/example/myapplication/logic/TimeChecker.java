package com.example.myapplication.logic;

import android.content.Context;
import android.widget.TextView;

import com.example.myapplication.ui.CircleManager;

import java.util.Calendar;

public class TimeChecker {
    private final Context context;
    private final CircleManager circleManager;
    private final TextView instructionText;

    public TimeChecker(Context context, CircleManager circleManager, TextView instructionText) {
        this.context = context;
        this.circleManager = circleManager;
        this.instructionText = instructionText;
    }

    public void check() {
        circleManager.fillCircleIfNotFilled(1, () -> {
            Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);
            int sum = hour / 10 + hour % 10 + minute / 10 + minute % 10;

            if (minute % 2 != 0 || sum % 2 != 0) {
                circleManager.fill(1);
                instructionText.setText("Time condition met. Sum of digits: " + sum);
            } else {
                instructionText.setText("Time condition not met.");
            }
        });
    }
}
