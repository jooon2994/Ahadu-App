package com.ahadumarket.app;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Full screen dark
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                             WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setStatusBarColor(Color.parseColor("#0a0a0a"));
        getWindow().setNavigationBarColor(Color.parseColor("#0a0a0a"));

        // Root layout
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER);
        root.setBackgroundColor(Color.parseColor("#0a0a0a"));

        // Gold top accent line
        View topLine = new View(this);
        topLine.setBackgroundColor(Color.parseColor("#f0a500"));
        LinearLayout.LayoutParams lineParams = new LinearLayout.LayoutParams(80, 4);
        lineParams.gravity = Gravity.CENTER_HORIZONTAL;
        lineParams.bottomMargin = dpToPx(40);
        root.addView(topLine, lineParams);

        // Logo text  ✈ (plane icon as unicode)
        TextView logo = new TextView(this);
        logo.setText("✈");
        logo.setTextSize(56);
        logo.setTextColor(Color.parseColor("#f0a500"));
        logo.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams logoParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT);
        logoParams.gravity = Gravity.CENTER_HORIZONTAL;
        logoParams.bottomMargin = dpToPx(24);
        root.addView(logo, logoParams);

        // Company name
        TextView name = new TextView(this);
        name.setText("AHADU MARKET AND EXPRESS");
        name.setTextSize(26);
        name.setTextColor(Color.parseColor("#f0f0f0"));
        name.setLetterSpacing(0.18f);
        name.setTypeface(Typeface.DEFAULT_BOLD);
        name.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams nameParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT);
        nameParams.gravity = Gravity.CENTER_HORIZONTAL;
        root.addView(name, nameParams);

        // & Express
        TextView express = new TextView(this);
        express.setText("& EXPRESS");
        express.setTextSize(13);
        express.setTextColor(Color.parseColor("#f0a500"));
        express.setLetterSpacing(0.25f);
        express.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams expParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT);
        expParams.gravity = Gravity.CENTER_HORIZONTAL;
        expParams.bottomMargin = dpToPx(32);
        root.addView(express, expParams);

        // Motto
        TextView motto = new TextView(this);
        motto.setText("We don't sell — we deliver.");
        motto.setTextSize(14);
        motto.setTextColor(Color.parseColor("#888888"));
        motto.setGravity(Gravity.CENTER);
        motto.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.ITALIC));
        LinearLayout.LayoutParams mottoParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT);
        mottoParams.gravity = Gravity.CENTER_HORIZONTAL;
        mottoParams.bottomMargin = dpToPx(60);
        root.addView(motto, mottoParams);

        // Bottom tagline
        TextView tagline = new TextView(this);
        tagline.setText("USA & China → Addis Ababa");
        tagline.setTextSize(11);
        tagline.setTextColor(Color.parseColor("#555555"));
        tagline.setLetterSpacing(0.1f);
        tagline.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams tagParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT);
        tagParams.gravity = Gravity.CENTER_HORIZONTAL;
        root.addView(tagline, tagParams);

        setContentView(root);

        // Animate in
        AnimationSet anim = new AnimationSet(true);
        AlphaAnimation fade = new AlphaAnimation(0f, 1f);
        fade.setDuration(900);
        TranslateAnimation slide = new TranslateAnimation(0, 0, 40, 0);
        slide.setDuration(900);
        anim.addAnimation(fade);
        anim.addAnimation(slide);
        root.startAnimation(anim);

        // Go to main after 2.5 seconds
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }, 2500);
    }

    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }
}
