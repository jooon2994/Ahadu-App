package com.ahadumarket.app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends Activity {

    private WebView webView;
    private ProgressBar progressBar;
    private static final String APP_URL = "https://ahadu-erp.vercel.app/customer";

    // Bottom nav tab URLs
    private static final String[] TAB_URLS = {
        "https://ahadu-erp.vercel.app/customer#track-section",
        "https://ahadu-erp.vercel.app/customer",
        "https://ahadu-erp.vercel.app/customer#estimator",
        "https://ahadu-erp.vercel.app/customer#contact",
    };
    private static final String[] TAB_ICONS = {"📦", "🏠", "💰", "📞"};
    private static final String[] TAB_LABELS = {"Track", "Home", "Estimate", "Contact"};
    private int activeTab = 1;
    private TextView[] tabLabels = new TextView[4];
    private TextView[] tabIcons  = new TextView[4];

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setStatusBarColor(Color.parseColor("#0a0a0a"));
        getWindow().setNavigationBarColor(Color.parseColor("#111111"));

        // Root: vertical layout (webview + bottom bar)
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.parseColor("#0a0a0a"));

        // ── WebView container (fills remaining space) ──
        FrameLayout webContainer = new FrameLayout(this);
        LinearLayout.LayoutParams wcParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f);
        webContainer.setLayoutParams(wcParams);

        // Progress bar
        progressBar = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
        progressBar.setMax(100);
        progressBar.setProgressTintList(
            android.content.res.ColorStateList.valueOf(Color.parseColor("#f0a500")));
        FrameLayout.LayoutParams pbParams = new FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT, 6);
        pbParams.gravity = Gravity.TOP;
        webContainer.addView(progressBar, pbParams);

        // WebView
        webView = new WebView(this);
        FrameLayout.LayoutParams wvParams = new FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT);
        webContainer.addView(webView, wvParams);

        // ── Bottom Navigation Bar ──
        LinearLayout bottomBar = new LinearLayout(this);
        bottomBar.setOrientation(LinearLayout.HORIZONTAL);
        bottomBar.setBackgroundColor(Color.parseColor("#111111"));
        bottomBar.setGravity(Gravity.CENTER_VERTICAL);

        // Top border line
        View topBorder = new View(this);
        topBorder.setBackgroundColor(Color.parseColor("#222222"));

        int barHeight = dpToPx(62);
        LinearLayout.LayoutParams barParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, barHeight);
        bottomBar.setLayoutParams(barParams);

        // Create 4 tabs
        for (int i = 0; i < 4; i++) {
            final int idx = i;
            LinearLayout tab = new LinearLayout(this);
            tab.setOrientation(LinearLayout.VERTICAL);
            tab.setGravity(Gravity.CENTER);
            tab.setPadding(0, dpToPx(8), 0, dpToPx(8));
            tab.setLayoutParams(new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.MATCH_PARENT, 1f));
            tab.setClickable(true);
            tab.setFocusable(true);

            TextView icon = new TextView(this);
            icon.setText(TAB_ICONS[i]);
            icon.setTextSize(20);
            icon.setGravity(Gravity.CENTER);
            tabIcons[i] = icon;

            TextView label = new TextView(this);
            label.setText(TAB_LABELS[i]);
            label.setTextSize(9);
            label.setGravity(Gravity.CENTER);
            label.setLetterSpacing(0.05f);
            tabLabels[i] = label;

            tab.addView(icon);
            tab.addView(label);
            tab.setOnClickListener(v -> switchTab(idx));
            bottomBar.addView(tab);
        }

        root.addView(webContainer);
        root.addView(topBorder, new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, 1));
        root.addView(bottomBar);
        setContentView(root);

        updateTabColors();
        setupWebView();
        webView.loadUrl(APP_URL);
    }

    private void switchTab(int idx) {
        activeTab = idx;
        updateTabColors();
        webView.loadUrl(TAB_URLS[idx]);
    }

    private void updateTabColors() {
        for (int i = 0; i < 4; i++) {
            int color = (i == activeTab)
                ? Color.parseColor("#f0a500")
                : Color.parseColor("#555555");
            if (tabIcons[i]  != null) tabIcons[i].setTextColor(color);
            if (tabLabels[i] != null) tabLabels[i].setTextColor(color);
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void setupWebView() {
        WebSettings s = webView.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setLoadWithOverviewMode(true);
        s.setUseWideViewPort(true);
        s.setBuiltInZoomControls(false);
        s.setDisplayZoomControls(false);
        s.setCacheMode(WebSettings.LOAD_DEFAULT);
        s.setMediaPlaybackRequiresUserGesture(false);
        s.setUserAgentString(s.getUserAgentString() + " AhaduApp/1.0");

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest req) {
                String url = req.getUrl().toString();
                if (url.startsWith("https://wa.me") || url.startsWith("whatsapp://") ||
                    url.startsWith("https://t.me")  || url.startsWith("tg://") ||
                    url.startsWith("tel:")           ||
                    url.startsWith("https://tiktok.com")) {
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                    } catch (Exception e) { /* ignore */ }
                    return true;
                }
                return false;
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int p) {
                progressBar.setProgress(p);
                progressBar.setVisibility(p < 100 ? View.VISIBLE : View.GONE);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) webView.goBack();
        else super.onBackPressed();
    }

    @Override protected void onResume() { super.onResume(); webView.onResume(); }
    @Override protected void onPause()  { super.onPause();  webView.onPause();  }

    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }
}
