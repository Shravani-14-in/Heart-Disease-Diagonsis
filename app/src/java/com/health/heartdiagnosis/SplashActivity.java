package com.health.heartdiagnosis;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        View ivHeart  = findViewById(R.id.ivHeart);
        View tvName   = findViewById(R.id.tvAppName);
        View tvTag    = findViewById(R.id.tvTagline);
        View llLoader = findViewById(R.id.llLoader);

        // Heart pulse + fade in
        AnimationSet heartAnim = new AnimationSet(true);
        ScaleAnimation scale = new ScaleAnimation(0.6f, 1f, 0.6f, 1f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scale.setDuration(700);
        AlphaAnimation fade = new AlphaAnimation(0f, 1f);
        fade.setDuration(700);
        heartAnim.addAnimation(scale);
        heartAnim.addAnimation(fade);
        heartAnim.setFillAfter(true);
        ivHeart.startAnimation(heartAnim);

        // Text fade in after 400ms
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            AlphaAnimation fadeIn = new AlphaAnimation(0f, 1f);
            fadeIn.setDuration(500);
            fadeIn.setFillAfter(true);
            tvName.startAnimation(fadeIn);
            tvTag.startAnimation(fadeIn);
        }, 400);

        // Loader after 800ms
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            AlphaAnimation fadeIn = new AlphaAnimation(0f, 1f);
            fadeIn.setDuration(400);
            fadeIn.setFillAfter(true);
            llLoader.startAnimation(fadeIn);
        }, 800);

        // Navigate to Main after 2.4s
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }, 2400);
    }
}
