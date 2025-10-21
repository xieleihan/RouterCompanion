package com.southaki.gamemarket.web;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * WebView 缓存配置
 * 支持为不同资源类型设置不同的缓存策略
 */
public class CacheConfig {
    private final Map<String, Long> extensionCacheTimeMap = new HashMap<>();
    private boolean enableCache = true;
    private boolean enableDebugLogging = true;

    public CacheConfig() {
        // 默认配置
        setExtensionCacheTime(".js", TimeUnit.DAYS.toMillis(30));
        setExtensionCacheTime(".css", TimeUnit.DAYS.toMillis(30));
        setExtensionCacheTime(".woff", TimeUnit.DAYS.toMillis(60));
        setExtensionCacheTime(".woff2", TimeUnit.DAYS.toMillis(60));
        setExtensionCacheTime(".ttf", TimeUnit.DAYS.toMillis(60));
        setExtensionCacheTime(".otf", TimeUnit.DAYS.toMillis(60));
        setExtensionCacheTime(".png", TimeUnit.DAYS.toMillis(30));
        setExtensionCacheTime(".jpg", TimeUnit.DAYS.toMillis(30));
        setExtensionCacheTime(".jpeg", TimeUnit.DAYS.toMillis(30));
        setExtensionCacheTime(".gif", TimeUnit.DAYS.toMillis(30));
        setExtensionCacheTime(".webp", TimeUnit.DAYS.toMillis(30));
        setExtensionCacheTime(".svg", TimeUnit.DAYS.toMillis(30));
    }

    /**
     * 为指定扩展名设置缓存时间
     *
     * @param extension 文件扩展名（包括点号）
     * @param cacheTime 缓存时间（毫秒）
     */
    public void setExtensionCacheTime(String extension, long cacheTime) {
        extensionCacheTimeMap.put(extension.toLowerCase(), cacheTime);
    }

    /**
     * 获取指定扩展名的缓存时间
     *
     * @param extension 文件扩展名（包括点号）
     * @return 缓存时间（毫秒）
     */
    public long getExtensionCacheTime(String extension) {
        return extensionCacheTimeMap.getOrDefault(extension.toLowerCase(), TimeUnit.DAYS.toMillis(7));
    }

    /**
     * 是否启用缓存
     *
     * @return true 表示启用
     */
    public boolean isEnableCache() {
        return enableCache;
    }

    /**
     * 设置是否启用缓存
     *
     * @param enableCache true 启用，false 禁用
     */
    public void setEnableCache(boolean enableCache) {
        this.enableCache = enableCache;
    }

    /**
     * 是否启用调试日志
     *
     * @return true 表示启用
     */
    public boolean isEnableDebugLogging() {
        return enableDebugLogging;
    }

    /**
     * 设置是否启用调试日志
     *
     * @param enableDebugLogging true 启用，false 禁用
     */
    public void setEnableDebugLogging(boolean enableDebugLogging) {
        this.enableDebugLogging = enableDebugLogging;
    }
}
