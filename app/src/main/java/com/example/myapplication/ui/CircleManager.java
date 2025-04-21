package com.example.myapplication.ui;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Handler;
import android.os.Vibrator;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myapplication.R;

public class CircleManager {
    private final ImageView[] circles;
    private final boolean[] filled;
    private Context context;
    private ImageView lockIcon;
    private TextView successMessage;
    private TextView successSubMessage;
    private boolean animationInProgress = false;


    public CircleManager(ImageView[] circles, Context context) {
        this.circles = circles;
        this.filled = new boolean[circles.length];
        this.context = context;
        View rootView = circles[0].getRootView();
        this.lockIcon = rootView.findViewById(R.id.lockIcon);
        this.successMessage = rootView.findViewById(R.id.successMessage);
        this.successSubMessage = rootView.findViewById(R.id.successSubMessage);
    }

    public CircleManager(ImageView[] circles, TextView instructionText) {
        this.circles = circles;
        this.filled = new boolean[circles.length];
    }

    public void fill(int index) {
        if (index < 0 || index >= circles.length || filled[index]) return;
        circles[index].setBackgroundResource(R.drawable.circle_filled);
        AlphaAnimation anim = new AlphaAnimation(0f, 1f);
        anim.setDuration(500);
        anim.setFillAfter(true);
        circles[index].startAnimation(anim);
        filled[index] = true;


        checkCompletion();
    }

    public void fillCircleIfNotFilled(int index, Runnable onConditionValid) {
        if (index < 0 || index >= circles.length) return;

        if (isFilled(index)) {
            vibrate();
        } else {
            if (onConditionValid != null) {
                onConditionValid.run();
            }
        }
    }


    public boolean isFilled(int index) {
        return filled[index];
    }

    public void vibrate() {
        Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (v != null) v.vibrate(200);
    }

    private void checkCompletion() {
        //vibrate();
        if (animationInProgress) return;

        boolean allFilled = true;
        for (boolean isFilled : filled) {
            if (!isFilled) {
                allFilled = false;
                break;
            }
        }

        if (allFilled) {
            animationInProgress = true;
            new Handler().postDelayed(this::startCompletionAnimation, 900);

        }
    }

    private void startCompletionAnimation() {

        // First, hide all circles and texts immediately
        for (int i = 1; i <= 6; i++) {
            View container = circles[i-1].getRootView().findViewWithTag("circle" + i + "_container");
            if (container != null) {
                container.setVisibility(View.GONE);
            }
        }

        // Hide instruction text

        // Hide the entire column of circles
        View rootView = circles[0].getRootView();
        View circleColumn = rootView.findViewById(R.id.circle_column);
        if (circleColumn != null) {
            circleColumn.setVisibility(View.GONE);
        }

        // Now start the lock animation sequence
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            // Show and animate the lock
            lockIcon.setVisibility(View.VISIBLE);
            lockIcon.setScaleX(0f);
            lockIcon.setScaleY(0f);
            lockIcon.setAlpha(0f);
            lockIcon.setImageResource(R.drawable.lock_closed);

            ObjectAnimator lockScaleX = ObjectAnimator.ofFloat(lockIcon, "scaleX", 0f, 1.2f, 1f);
            ObjectAnimator lockScaleY = ObjectAnimator.ofFloat(lockIcon, "scaleY", 0f, 1.2f, 1f);
            ObjectAnimator lockAlpha = ObjectAnimator.ofFloat(lockIcon, "alpha", 0f, 1f);

            AnimatorSet lockAnimation = new AnimatorSet();
            lockAnimation.playTogether(lockScaleX, lockScaleY, lockAlpha);
            lockAnimation.setDuration(1000);
            lockAnimation.setInterpolator(new DecelerateInterpolator());

            // Lock opening animation
            lockAnimation.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {}

                @Override
                public void onAnimationEnd(Animator animation) {
                    // Fade out the closed lock
                    ObjectAnimator fadeOut = ObjectAnimator.ofFloat(lockIcon, "alpha", 1f, 0.3f);
                    fadeOut.setDuration(800);
                    fadeOut.setInterpolator(new AccelerateInterpolator());

                    fadeOut.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {}

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            // Change to open lock
                            lockIcon.setImageResource(R.drawable.lock_open);

                            // Fade in the open lock
                            ObjectAnimator fadeIn = ObjectAnimator.ofFloat(lockIcon, "alpha", 0.3f, 1f);
                            fadeIn.setDuration(800);
                            fadeIn.setInterpolator(new DecelerateInterpolator());

                            // Scale animation for the open lock
                            ObjectAnimator openScaleX = ObjectAnimator.ofFloat(lockIcon, "scaleX", 1f, 1.2f, 1f);
                            ObjectAnimator openScaleY = ObjectAnimator.ofFloat(lockIcon, "scaleY", 1f, 1.2f, 1f);

                            AnimatorSet openAnimation = new AnimatorSet();
                            openAnimation.playTogether(fadeIn, openScaleX, openScaleY);
                            openAnimation.setDuration(1000);
                            openAnimation.setInterpolator(new DecelerateInterpolator());
                            openAnimation.start();
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {}

                        @Override
                        public void onAnimationRepeat(Animator animation) {}
                    });

                    fadeOut.start();
                }

                @Override
                public void onAnimationCancel(Animator animation) {}

                @Override
                public void onAnimationRepeat(Animator animation) {}
            });

            lockAnimation.start();

            // Show success messages after lock animation
            handler.postDelayed(() -> {
                successMessage.setVisibility(View.VISIBLE);
                successMessage.setAlpha(0f);
                successSubMessage.setVisibility(View.VISIBLE);
                successSubMessage.setAlpha(0f);

                ObjectAnimator messageAlpha = ObjectAnimator.ofFloat(successMessage, "alpha", 0f, 1f);
                ObjectAnimator subMessageAlpha = ObjectAnimator.ofFloat(successSubMessage, "alpha", 0f, 1f);

                messageAlpha.setDuration(800);
                subMessageAlpha.setDuration(800);

                AnimatorSet messageAnimation = new AnimatorSet();
                messageAnimation.playTogether(messageAlpha, subMessageAlpha);
                messageAnimation.start();
            }, 2000);
        }, 100);

        animationInProgress = false;
    }
}
