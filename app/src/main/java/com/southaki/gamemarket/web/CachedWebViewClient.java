package com.southaki.gamemarket.web;

import android.content.Context;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * 自定义 WebViewClient
 * 实现资源请求拦截、缓存和本地处理
 */
public class CachedWebViewClient extends WebViewClient {
    private static final String TAG = "CachedWebViewClient";

    private final ResourceCache resourceCache;
    private final Context context;
    private OnPageFinishedListener onPageFinishedListener;

    // 需要缓存的资源类型
    private static final String[] CACHEABLE_EXTENSIONS = {
            ".js", ".css", ".woff", ".woff2", ".ttf", ".otf",
            ".png", ".jpg", ".jpeg", ".gif", ".webp", ".svg"
    };

    public interface OnPageFinishedListener {
        void onPageFinished(WebView view, String url);
    }

    public CachedWebViewClient(Context context, ResourceCache resourceCache) {
        this.context = context;
        this.resourceCache = resourceCache;
    }

    public void setOnPageFinishedListener(OnPageFinishedListener listener) {
        this.onPageFinishedListener = listener;
    }

    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
        String url = request.getUrl().toString();

        // 检查是否是可缓存的资源类型
        if (isCacheableResource(url)) {
            // 尝试从缓存加载
            File cachedFile = resourceCache.getCacheFile(url);
            if (cachedFile != null) {
                try {
                    String mimeType = getMimeType(url);
                    FileInputStream fis = new FileInputStream(cachedFile);
                    Log.d(TAG, "从缓存加载资源: " + url);
                    return new WebResourceResponse(mimeType, "utf-8", fis);
                } catch (Exception e) {
                    Log.e(TAG, "从缓存加载资源失败: " + url, e);
                }
            }
        }

        // 如果缓存不存在，继续加载原始资源
        return super.shouldInterceptRequest(view, request);
    }

    /**
     * 在页面完成加载后，缓存可缓存的资源
     * 注意：此方法仅用于后续访问的缓存优化
     */
    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        Log.d(TAG, "页面加载完成: " + url);

        // 触发页面完成监听器
        if (onPageFinishedListener != null) {
            onPageFinishedListener.onPageFinished(view, url);
        }

        // 可以在这里添加 JavaScript 注入来获取已加载的资源信息
        injectResourceCollector(view);
    }

    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        super.onReceivedError(view, errorCode, description, failingUrl);
        Log.e(TAG, "WebView 加载错误 - 代码: " + errorCode + ", 描述: " + description + ", URL: " + failingUrl);
        Toast.makeText(context, "加载失败: " + description, Toast.LENGTH_SHORT).show();
    }

    /**
     * 判断资源是否需要缓存
     *
     * @param url 资源 URL
     * @return 是否需要缓存
     */
    private boolean isCacheableResource(String url) {
        try {
            // 移除查询参数
            int questionMarkIndex = url.indexOf('?');
            String urlWithoutParams = (questionMarkIndex > 0) ? url.substring(0, questionMarkIndex) : url;

            for (String ext : CACHEABLE_EXTENSIONS) {
                if (urlWithoutParams.toLowerCase().endsWith(ext)) {
                    return true;
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "判断资源类型出错", e);
        }
        return false;
    }

    /**
     * 根据 URL 获取 MIME 类型
     *
     * @param url 资源 URL
     * @return MIME 类型
     */
    private String getMimeType(String url) {
        try {
            // 移除查询参数
            int questionMarkIndex = url.indexOf('?');
            String urlWithoutParams = (questionMarkIndex > 0) ? url.substring(0, questionMarkIndex) : url;

            String mimeType = URLConnection.guessContentTypeFromName(urlWithoutParams);
            if (mimeType != null) {
                return mimeType;
            }

            // if (urlWithoutParams.endsWith(".js")) {
            //     return "application/javascript";
            // } else if (urlWithoutParams.endsWith(".css")) {
            //     return "text/css";
            // } else if (urlWithoutParams.endsWith(".woff")) {
            //     return "font/woff";
            // } else if (urlWithoutParams.endsWith(".woff2")) {
            //     return "font/woff2";
            // } else if (urlWithoutParams.endsWith(".ttf")) {
            //     return "font/ttf";
            // } else if (urlWithoutParams.endsWith(".otf")) {
            //     return "font/otf";
            // } else if (urlWithoutParams.endsWith(".svg")) {
            //     return "image/svg+xml";
            // } else if (urlWithoutParams.endsWith(".webp")) {
            //     return "image/webp";
            // } else if (urlWithoutParams.endsWith(".png")) {
            //     return "image/png";
            // } else if (urlWithoutParams.endsWith(".jpg") || urlWithoutParams.endsWith(".jpeg")) {
            //     return "image/jpeg";
            // } else if (urlWithoutParams.endsWith(".gif")) {
            //     return "image/gif";
            // }
            switch (urlWithoutParams.substring(urlWithoutParams.lastIndexOf('.') + 1).toLowerCase()) {
                case "js":
                    return "application/javascript";
                case "css":
                    return "text/css";
                case "woff":
                    return "font/woff";
                case "woff2":
                    return "font/woff2";
                case "ttf":
                    return "font/ttf";
                case "otf":
                    return "font/otf";
                case "svg":
                    return "image/svg+xml";
                case "webp":
                    return "image/webp";
                case "png":
                    return "image/png";
                case "jpg":
                case "jpeg":
                    return "image/jpeg";
                case "gif":
                    return "image/gif";
                default:
                    break;
            }
        } catch (Exception e) {
            Log.e(TAG, "获取 MIME 类型出错", e);
        }
        return "text/plain";
    }

    /**
     * 注入 JavaScript 代码来收集页面资源信息
     * 这使得应用能够在后续访问时自动缓存这些资源
     *
     * @param view WebView 实例
     */
    private void injectResourceCollector(WebView view) {
        // 注入 JavaScript 代码来获取页面加载的资源
        String jsCode = "javascript:(function() {" +
                "  var links = document.querySelectorAll('link[rel=\"stylesheet\"], script, img, [data-src]');" +
                "  var resources = [];" +
                "  links.forEach(function(link) {" +
                "    var href = link.href || link.src || link.dataset.src;" +
                "    if(href) resources.push(href);" +
                "  });" +
                "  console.log('Resources: ' + JSON.stringify(resources));" +
                "})();";

        // 仅在 DEBUG 模式下执行
        if (android.util.Log.isLoggable(TAG, android.util.Log.DEBUG)) {
            view.evaluateJavascript(jsCode, null);
        }
    }
}
