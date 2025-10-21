package com.southaki.gamemarket;

import android.app.Application;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.southaki.gamemarket.web.WebViewCacheManager;

/**
 * 自定义 Application 类
 * 用于应用级别的初始化和配置
 */
public class MyApplication extends Application {

    private static WebView webViewWarmup;

    @Override
    public void onCreate() {
        super.onCreate();

        // 初始化 WebView 缓存管理器
        WebViewCacheManager.init(this);

        // 异步预热 WebView
        warmupWebView();
    }

    /**
     * 异步预热 WebView 以加速首屏加载
     * 在后台线程中创建 WebView，进行基础初始化
     */
    private void warmupWebView() {
        new Thread(() -> {
            try {
                // 在后台线程中创建 WebView 实例
                webViewWarmup = new WebView(this);

                // 配置基础设置以加速后续使用
                WebSettings settings = webViewWarmup.getSettings();
                settings.setJavaScriptEnabled(true);
                settings.setDomStorageEnabled(true);
                settings.setLoadWithOverviewMode(true);
                settings.setUseWideViewPort(true);
                settings.setDatabaseEnabled(true);
                settings.setCacheMode(WebSettings.LOAD_DEFAULT);
                settings.setAllowFileAccess(false);
                settings.setAllowContentAccess(false);
                settings.setAllowUniversalAccessFromFileURLs(false);

                // 启用 WebView 调试（开发版本）
                if (BuildConfig.DEBUG) {
                    WebView.setWebContentsDebuggingEnabled(true);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * 获取预热的 WebView 缓存
     */
    public static WebView getWarmupWebView() {
        return webViewWarmup;
    }

    /**
     * 释放预热的 WebView
     */
    public static void releaseWarmupWebView() {
        if (webViewWarmup != null) {
            webViewWarmup.stopLoading();
            webViewWarmup.removeAllViews();
            webViewWarmup.destroy();
            webViewWarmup = null;
        }
    }
}
