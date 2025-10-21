package com.southaki.gamemarket;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.southaki.gamemarket.network.DnsCache;
import com.southaki.gamemarket.network.DnsResolver;

public class MainActivity extends AppCompatActivity {

    private WebView webView;
    private long lastBackPressTime = 0;
    private static final int BACK_PRESS_INTERVAL = 3000; // 3秒内双击退出
    private static final String PREFS_NAME = "WebViewPrefs";
    private static final String KEY_LAST_URL = "lastUrl";
    private SharedPreferences sharedPreferences;
    private DnsCache dnsCache;
    private DnsResolver dnsResolver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // 初始化 SharedPreferences
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // 初始化 DNS 缓存
        dnsCache = DnsCache.getInstance(this);
        dnsResolver = new DnsResolver(this);

        // 预加载常用域名 DNS
        preloadDns();

        // 初始化 WebView
        webView = findViewById(R.id.webview);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                // 保存当前页面URL
                saveCurrentUrl(url);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                Toast.makeText(MainActivity.this, "加载失败: " + description, Toast.LENGTH_SHORT).show();
            }
        });

        // 配置 WebView 设置
        configureWebViewSettings();

        // 恢复上次访问的页面或加载默认页面
        String lastUrl = getLastUrl();
        if (lastUrl != null && !lastUrl.isEmpty()) {
            webView.loadUrl(lastUrl);
        } else {
            webView.loadUrl("http://game.localtest.echoing.cc:61007/");
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    /**
     * 预加载常用域名的 DNS 缓存
     */
    private void preloadDns() {
        String[] commonHosts = {
                "game.localtest.echoing.cc", // 测试域名
        };

        for (String host : commonHosts) {
            dnsResolver.resolveDns(host, new DnsResolver.OnDnsResolveListener() {
                @Override
                public void onSuccess(String ipAddress) {
                    // DNS 已缓存
                }

                @Override
                public void onFailed(Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

    /**
     * 配置 WebView 设置
     */
    private void configureWebViewSettings() {
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setDatabaseEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        webSettings.setAllowFileAccess(false);
        webSettings.setAllowContentAccess(false);
        webSettings.setAllowUniversalAccessFromFileURLs(false);

        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.setAcceptThirdPartyCookies(webView, false);
    }

    @Override
    public void onBackPressed() {
        // 检查 WebView 是否可以返回上一页
        if (webView.canGoBack()) {
            webView.goBack(); // WebView 返回上一页
            return;
        }

        // WebView 无法返回时，处理双击退出逻辑
        long currentTime = System.currentTimeMillis();

        if (currentTime - lastBackPressTime < BACK_PRESS_INTERVAL) {
            // 3秒内第二次按返回键，移到后台
            moveTaskToBack(true);
        } else {
            // 第一次按返回键，显示提示并记录时间
            lastBackPressTime = currentTime;
            Toast.makeText(this, "再次返回将退出应用", Toast.LENGTH_SHORT).show();
        }
    }

    // 保存当前URL
    private void saveCurrentUrl(String url) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_LAST_URL, url);
        editor.apply();
    }

    // 获取上次保存的URL
    private String getLastUrl() {
        return sharedPreferences.getString(KEY_LAST_URL, null);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // 保存WebView状态
        webView.saveState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // 恢复WebView状态
        if (savedInstanceState != null) {
            webView.restoreState(savedInstanceState);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 暂停WebView
        webView.onPause();
        // 保存Cookie
        CookieManager.getInstance().flush();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 恢复WebView
        webView.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 保存当前URL
        if (webView != null) {
            String currentUrl = webView.getUrl();
            if (currentUrl != null) {
                saveCurrentUrl(currentUrl);
            }
            webView.stopLoading();
            webView.removeAllViews();
            webView.destroy();
            webView = null;
        }
    }
}