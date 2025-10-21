package com.southaki.gamemarket.web;

import android.content.Context;

/**
 * WebView 缓存管理工具
 * 提供公共接口来管理 WebView 资源缓存
 */
public class WebViewCacheManager {
    private static ResourceCache instance;

    /**
     * 初始化缓存管理器（应在 Application 中调用）
     *
     * @param context 应用上下文
     */
    public static void init(Context context) {
        if (instance == null) {
            instance = new ResourceCache(context);
        }
    }

    /**
     * 获取缓存管理器实例
     *
     * @return ResourceCache 实例
     */
    public static ResourceCache getInstance() {
        if (instance == null) {
            throw new IllegalStateException("WebViewCacheManager 尚未初始化，请先调用 init()");
        }
        return instance;
    }

    /**
     * 清空所有缓存
     */
    public static void clearAllCache() {
        if (instance != null) {
            instance.clearAllCache();
        }
    }

    /**
     * 清空过期缓存
     */
    public static void clearExpiredCache() {
        if (instance != null) {
            instance.clearExpiredCache();
        }
    }

    /**
     * 获取缓存大小（字节）
     *
     * @return 缓存大小
     */
    public static long getCacheSize() {
        if (instance != null) {
            return instance.getCacheSize();
        }
        return 0;
    }

    /**
     * 获取缓存大小（MB）
     *
     * @return 缓存大小（MB）
     */
    public static double getCacheSizeInMB() {
        return getCacheSize() / (1024.0 * 1024.0);
    }
}
