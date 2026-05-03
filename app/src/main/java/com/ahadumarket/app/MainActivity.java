package com.ahadumarket.app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SwipeRefreshLayout;
import android.widget.TextView;

public class MainActivity extends Activity {

    private WebView webView;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefresh;
    private LinearLayout noInternetView;
    private FrameLayout webContainer;

    // ── Bottom nav tabs ──────────────────────────────────────────
    // Each entry: { icon, label, full URL with hash }
    private static final String[] TAB_ICONS   = { "🏠", "📦", "💰", "⚖️", "📸", "📞" };
    private static final String[] TAB_LABELS  = { "Home", "Track", "Estimate", "Ship KG", "Gallery", "Contact" };
    private static final String[] TAB_ANCHORS = { "", "#track-section", "#estimator", "#shipping-calc", "#gallery", "#contact" };

    private static final String BASE_URL = "https://ahadu-erp.vercel.app/customer";

    private int activeTab = 0;
    private final TextView[] tabIconViews  = new TextView[TAB_ICONS.length];
    private final TextView[] tabLabelViews = new TextView[TAB_LABELS.length];
    private final View[]     tabDots       = new View[TAB_ICONS.length];

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setStatusBarColor(Color.parseColor("#0a0a0a"));
        getWindow().setNavigationBarColor(Color.parseColor("#0f0f0f"));

        // ── Root layout ──────────────────────────────────────────
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.parseColor("#0a0a0a"));

        // ── Web container (fills remaining space) ────────────────
        FrameLayout outerFrame = new FrameLayout(this);
        LinearLayout.LayoutParams outerParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f);
        outerFrame.setLayoutParams(outerParams);

        // Progress bar
        progressBar = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
        progressBar.setMax(100);
        progressBar.setProgressTintList(
            android.content.res.ColorStateList.valueOf(Color.parseColor("#f0a500")));
        FrameLayout.LayoutParams pbLp = new FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT, dpToPx(3));
        pbLp.gravity = Gravity.TOP;
        outerFrame.addView(progressBar, pbLp);

        // SwipeRefreshLayout wraps the WebView
        swipeRefresh = new SwipeRefreshLayout(this);
        swipeRefresh.setColorSchemeColors(Color.parseColor("#f0a500"));
        swipeRefresh.setProgressBackgroundColorSchemeColor(Color.parseColor("#181818"));
        swipeRefresh.setOnRefreshListener(() -> {
            if (isOnline()) {
                webView.reload();
            } else {
                swipeRefresh.setRefreshing(false);
                showNoInternet();
            }
        });

        // WebView
        webView = new WebView(this);
        webView.setVerticalScrollBarEnabled(false);   // ← hide scrollbar
        webView.setHorizontalScrollBarEnabled(false);
        webView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
        swipeRefresh.addView(webView, new FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT));

        outerFrame.addView(swipeRefresh, new FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT));

        // ── No Internet view ─────────────────────────────────────
        noInternetView = buildNoInternetView();
        noInternetView.setVisibility(View.GONE);
        outerFrame.addView(noInternetView, new FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT));

        // ── Bottom nav bar ───────────────────────────────────────
        View divider = new View(this);
        divider.setBackgroundColor(Color.parseColor("#1e1e1e"));

        LinearLayout bottomBar = buildBottomBar();

        root.addView(outerFrame);
        root.addView(divider, new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, 1));
        root.addView(bottomBar, new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, dpToPx(60)));

        setContentView(root);

        setupWebView();

        if (isOnline()) {
            webView.loadUrl(BASE_URL);
        } else {
            showNoInternet();
        }
    }

    // ── Build bottom navigation bar ──────────────────────────────
    private LinearLayout buildBottomBar() {
        LinearLayout bar = new LinearLayout(this);
        bar.setOrientation(LinearLayout.HORIZONTAL);
        bar.setBackgroundColor(Color.parseColor("#0f0f0f"));
        bar.setGravity(Gravity.CENTER_VERTICAL);

        for (int i = 0; i < TAB_ICONS.length; i++) {
            final int idx = i;

            LinearLayout tab = new LinearLayout(this);
            tab.setOrientation(LinearLayout.VERTICAL);
            tab.setGravity(Gravity.CENTER);
            tab.setPadding(0, dpToPx(6), 0, dpToPx(6));
            tab.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.MATCH_PARENT, 1f));
            tab.setClickable(true);
            tab.setFocusable(true);

            // Active dot indicator above icon
            View dot = new View(this);
            LinearLayout.LayoutParams dotLp = new LinearLayout.LayoutParams(
                dpToPx(18), dpToPx(3));
            dotLp.gravity = Gravity.CENTER_HORIZONTAL;
            dotLp.bottomMargin = dpToPx(3);
            dot.setBackgroundColor(Color.TRANSPARENT);
            // Rounded dot via background shape — use color directly
            tabDots[i] = dot;
            tab.addView(dot, dotLp);

            // Icon
            TextView icon = new TextView(this);
            icon.setText(TAB_ICONS[i]);
            icon.setTextSize(18);
            icon.setGravity(Gravity.CENTER);
            tabIconViews[i] = icon;
            tab.addView(icon, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

            // Label
            TextView label = new TextView(this);
            label.setText(TAB_LABELS[i]);
            label.setTextSize(9);
            label.setGravity(Gravity.CENTER);
            label.setLetterSpacing(0.04f);
            LinearLayout.LayoutParams lblLp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
            lblLp.topMargin = dpToPx(2);
            tabLabelViews[i] = label;
            tab.addView(label, lblLp);

            tab.setOnClickListener(v -> switchTab(idx));
            bar.addView(tab);
        }

        updateTabColors();
        return bar;
    }

    private void switchTab(int idx) {
        if (idx == activeTab) return;
        activeTab = idx;
        updateTabColors();
        haptic();

        // Navigate: load full URL then scroll to anchor via JS
        String anchor = TAB_ANCHORS[idx];
        if (anchor.isEmpty()) {
            // Home — scroll to very top
            if (webView.getUrl() != null && webView.getUrl().startsWith(BASE_URL)) {
                webView.evaluateJavascript("window.scrollTo({top:0,behavior:'smooth'});", null);
            } else {
                webView.loadUrl(BASE_URL);
            }
        } else {
            // Use JS to scroll to section if already on the page, else load fresh
            String currentUrl = webView.getUrl();
            if (currentUrl != null && currentUrl.startsWith(BASE_URL)) {
                // Already on page — just scroll to section
                String sectionId = anchor.replace("#", "");
                webView.evaluateJavascript(
                    "var el=document.getElementById('" + sectionId + "');" +
                    "if(el){el.scrollIntoView({behavior:'smooth',block:'start'});}",
                    null);
            } else {
                webView.loadUrl(BASE_URL + anchor);
            }
        }
    }

    private void updateTabColors() {
        for (int i = 0; i < TAB_ICONS.length; i++) {
            boolean active = (i == activeTab);
            int color = active ? Color.parseColor("#f0a500") : Color.parseColor("#555555");
            if (tabIconViews[i]  != null) tabIconViews[i].setTextColor(color);
            if (tabLabelViews[i] != null) tabLabelViews[i].setTextColor(color);
            if (tabDots[i] != null) {
                tabDots[i].setBackgroundColor(
                    active ? Color.parseColor("#f0a500") : Color.TRANSPARENT);
            }
        }
    }

    // ── No internet view ─────────────────────────────────────────
    private LinearLayout buildNoInternetView() {
        LinearLayout v = new LinearLayout(this);
        v.setOrientation(LinearLayout.VERTICAL);
        v.setGravity(Gravity.CENTER);
        v.setBackgroundColor(Color.parseColor("#0a0a0a"));
        v.setPadding(dpToPx(32), 0, dpToPx(32), dpToPx(60));

        // Icon
        TextView icon = new TextView(this);
        icon.setText("📡");
        icon.setTextSize(52);
        icon.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams iconLp = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT);
        iconLp.gravity = Gravity.CENTER_HORIZONTAL;
        iconLp.bottomMargin = dpToPx(20);
        v.addView(icon, iconLp);

        // Title
        TextView title = new TextView(this);
        title.setText("No Internet Connection");
        title.setTextSize(20);
        title.setTextColor(Color.parseColor("#f0f0f0"));
        title.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);
        title.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams titleLp = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT);
        titleLp.gravity = Gravity.CENTER_HORIZONTAL;
        titleLp.bottomMargin = dpToPx(10);
        v.addView(title, titleLp);

        // Subtitle
        TextView sub = new TextView(this);
        sub.setText("Please check your connection\nand try again.");
        sub.setTextSize(14);
        sub.setTextColor(Color.parseColor("#888888"));
        sub.setGravity(Gravity.CENTER);
        sub.setLineSpacing(4, 1);
        LinearLayout.LayoutParams subLp = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT);
        subLp.gravity = Gravity.CENTER_HORIZONTAL;
        subLp.bottomMargin = dpToPx(36);
        v.addView(sub, subLp);

        // Retry button
        TextView retry = new TextView(this);
        retry.setText("  Try Again  ");
        retry.setTextSize(14);
        retry.setTextColor(Color.parseColor("#000000"));
        retry.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);
        retry.setBackgroundColor(Color.parseColor("#f0a500"));
        retry.setGravity(Gravity.CENTER);
        retry.setPadding(dpToPx(32), dpToPx(14), dpToPx(32), dpToPx(14));
        LinearLayout.LayoutParams retryLp = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT);
        retryLp.gravity = Gravity.CENTER_HORIZONTAL;
        v.addView(retry, retryLp);

        retry.setOnClickListener(view -> {
            if (isOnline()) {
                hideNoInternet();
                webView.loadUrl(BASE_URL);
            }
        });

        // Brand tagline at bottom
        TextView brand = new TextView(this);
        brand.setText("Ahadu Market and Express");
        brand.setTextSize(11);
        brand.setTextColor(Color.parseColor("#333333"));
        brand.setGravity(Gravity.CENTER);
        brand.setLetterSpacing(0.08f);
        LinearLayout.LayoutParams brandLp = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT);
        brandLp.gravity = Gravity.CENTER_HORIZONTAL;
        brandLp.topMargin = dpToPx(48);
        v.addView(brand, brandLp);

        return v;
    }

    private void showNoInternet() {
        noInternetView.setVisibility(View.VISIBLE);
        swipeRefresh.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
    }

    private void hideNoInternet() {
        noInternetView.setVisibility(View.GONE);
        swipeRefresh.setVisibility(View.VISIBLE);
    }

    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) return false;
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null && ni.isConnected();
    }

    // ── WebView setup ─────────────────────────────────────────────
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
                if (url.startsWith("https://wa.me")    ||
                    url.startsWith("whatsapp://")       ||
                    url.startsWith("https://t.me")      ||
                    url.startsWith("tg://")             ||
                    url.startsWith("tel:")              ||
                    url.startsWith("https://tiktok.com")) {
                    try { startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url))); }
                    catch (Exception ignored) {}
                    return true;
                }
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                swipeRefresh.setRefreshing(false);
                // Hide website's own scrollbar via injected CSS
                view.evaluateJavascript(
                    "(function(){" +
                    "var s=document.createElement('style');" +
                    "s.textContent='::-webkit-scrollbar{display:none!important;}" +
                    "body{-ms-overflow-style:none!important;scrollbar-width:none!important;}';" +
                    "document.head.appendChild(s);" +
                    "})()", null);
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

    // ── Haptic feedback ───────────────────────────────────────────
    private void haptic() {
        try {
            Vibrator vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            if (vib != null && vib.hasVibrator()) {
                vib.vibrate(VibrationEffect.createOneShot(28,
                    VibrationEffect.DEFAULT_AMPLITUDE));
            }
        } catch (Exception ignored) {}
    }

    @Override
    public void onBackPressed() {
        if (noInternetView.getVisibility() == View.VISIBLE) {
            finish();
        } else if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override protected void onResume() { super.onResume(); webView.onResume(); }
    @Override protected void onPause()  { super.onPause();  webView.onPause();  }

    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }
}
