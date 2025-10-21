package com.southaki.gamemarket.network;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.HashMap;
import java.util.Map;

/**
 * DNS 缓存管理器
 */
public class DnsCache {
    private static final String PREFS_NAME = "DnsCachePrefs";
    private static final String PREFIX_DNS = "dns_";
    private static final long CACHE_EXPIRY_TIME = 24 * 60 * 60 * 1000; // 24小时过期

    private static volatile DnsCache instance;
    private SharedPreferences sharedPreferences;
    private Map<String, DnsCacheEntry> memoryCache;

    private DnsCache(Context context) {
        this.sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        this.memoryCache = new HashMap<>();
    }

    public static DnsCache getInstance(Context context) {
        if (instance == null) {
            synchronized (DnsCache.class) {
                if (instance == null) {
                    instance = new DnsCache(context);
                }
            }
        }
        return instance;
    }

    /**
     * 获取缓存的IP地址
     */
    public String getIpAddress(String host) {
        // 先查内存缓存
        DnsCacheEntry cacheEntry = memoryCache.get(host);
        if (cacheEntry != null && !cacheEntry.isExpired()) {
            return cacheEntry.ipAddress;
        }

        // 再查持久化缓存
        String cachedData = sharedPreferences.getString(PREFIX_DNS + host, null);
        if (cachedData != null) {
            String[] parts = cachedData.split("\\|");
            if (parts.length == 2) {
                try {
                    long timestamp = Long.parseLong(parts[1]);
                    long age = System.currentTimeMillis() - timestamp;
                    if (age < CACHE_EXPIRY_TIME) {
                        String ipAddress = parts[0];
                        // 更新内存缓存
                        memoryCache.put(host, new DnsCacheEntry(ipAddress, timestamp));
                        return ipAddress;
                    } else {
                        // 缓存过期，删除
                        removeDnsCache(host);
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * 保存 DNS 缓存
     */
    public void cacheDnsResult(String host, String ipAddress) {
        long timestamp = System.currentTimeMillis();
        String cacheData = ipAddress + "|" + timestamp;

        // 保存到持久化存储
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PREFIX_DNS + host, cacheData);
        editor.apply();

        // 保存到内存缓存
        memoryCache.put(host, new DnsCacheEntry(ipAddress, timestamp));
    }

    /**
     * 删除缓存
     */
    public void removeDnsCache(String host) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(PREFIX_DNS + host);
        editor.apply();
        memoryCache.remove(host);
    }

    /**
     * 清空所有 DNS 缓存
     */
    public void clearAllCache() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Map<String, ?> allEntries = sharedPreferences.getAll();
        for (String key : allEntries.keySet()) {
            if (key.startsWith(PREFIX_DNS)) {
                editor.remove(key);
            }
        }
        editor.apply();
        memoryCache.clear();
    }

    /**
     * DNS 缓存条目
     */
    private static class DnsCacheEntry {
        String ipAddress;
        long timestamp;

        DnsCacheEntry(String ipAddress, long timestamp) {
            this.ipAddress = ipAddress;
            this.timestamp = timestamp;
        }

        boolean isExpired() {
            return System.currentTimeMillis() - timestamp > CACHE_EXPIRY_TIME;
        }
    }
}
