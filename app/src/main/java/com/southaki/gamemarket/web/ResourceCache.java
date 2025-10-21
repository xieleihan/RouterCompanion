package com.southaki.gamemarket.web;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.concurrent.TimeUnit;

/**
 * 资源缓存管理器
 * 用于缓存 CSS、JS 等静态资源
 */
public class ResourceCache {
    private static final String TAG = "ResourceCache";
    private static final String CACHE_DIR = "web_resource_cache";
    private static final long CACHE_VALID_TIME = TimeUnit.DAYS.toMillis(7); // 缓存7天

    private final File cacheDir;

    public ResourceCache(Context context) {
        this.cacheDir = new File(context.getCacheDir(), CACHE_DIR);
        if (!cacheDir.exists()) {
            cacheDir.mkdirs();
        }
    }

    /**
     * 获取缓存文件
     *
     * @param url 资源 URL
     * @return 缓存文件，如果不存在或已过期则返回 null
     */
    public File getCacheFile(String url) {
        try {
            String fileName = generateFileName(url);
            File cacheFile = new File(cacheDir, fileName);

            if (cacheFile.exists()) {
                // 检查缓存是否过期
                long lastModified = cacheFile.lastModified();
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastModified < CACHE_VALID_TIME) {
                    Log.d(TAG, "缓存命中: " + url);
                    return cacheFile;
                } else {
                    // 缓存过期，删除
                    cacheFile.delete();
                    Log.d(TAG, "缓存已过期: " + url);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "获取缓存文件出错", e);
        }
        return null;
    }

    /**
     * 保存资源到缓存
     *
     * @param url  资源 URL
     * @param data 资源数据
     * @return 是否保存成功
     */
    public boolean saveCacheFile(String url, byte[] data) {
        try {
            String fileName = generateFileName(url);
            File cacheFile = new File(cacheDir, fileName);

            try (FileOutputStream fos = new FileOutputStream(cacheFile)) {
                fos.write(data);
                fos.flush();
                Log.d(TAG, "缓存保存: " + url);
                return true;
            }
        } catch (Exception e) {
            Log.e(TAG, "保存缓存文件出错", e);
        }
        return false;
    }

    /**
     * 清空所有缓存
     */
    public void clearAllCache() {
        try {
            if (cacheDir.exists() && cacheDir.isDirectory()) {
                File[] files = cacheDir.listFiles();
                if (files != null) {
                    for (File file : files) {
                        file.delete();
                    }
                }
                Log.d(TAG, "缓存已清空");
            }
        } catch (Exception e) {
            Log.e(TAG, "清空缓存出错", e);
        }
    }

    /**
     * 清空过期缓存
     */
    public void clearExpiredCache() {
        try {
            if (cacheDir.exists() && cacheDir.isDirectory()) {
                File[] files = cacheDir.listFiles();
                if (files != null) {
                    long currentTime = System.currentTimeMillis();
                    for (File file : files) {
                        long lastModified = file.lastModified();
                        if (currentTime - lastModified > CACHE_VALID_TIME) {
                            file.delete();
                            Log.d(TAG, "删除过期缓存: " + file.getName());
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "清空过期缓存出错", e);
        }
    }

    /**
     * 根据 URL 生成缓存文件名
     * 使用 MD5 哈希避免特殊字符问题
     *
     * @param url 资源 URL
     * @return 缓存文件名
     */
    private String generateFileName(String url) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(url.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : messageDigest) {
                sb.append(String.format("%02x", b));
            }
            // 添加扩展名
            String ext = getExtensionFromUrl(url);
            return sb.toString() + ext;
        } catch (Exception e) {
            Log.e(TAG, "生成文件名出错", e);
            return String.valueOf(url.hashCode());
        }
    }

    /**
     * 从 URL 获取文件扩展名
     *
     * @param url 资源 URL
     * @return 扩展名（包括点号）
     */
    private String getExtensionFromUrl(String url) {
        try {
            // 移除查询参数和锚点
            int questionMarkIndex = url.indexOf('?');
            if (questionMarkIndex > 0) {
                url = url.substring(0, questionMarkIndex);
            }

            int lastDotIndex = url.lastIndexOf('.');
            if (lastDotIndex > 0) {
                int lastSlashIndex = url.lastIndexOf('/');
                if (lastDotIndex > lastSlashIndex) {
                    return url.substring(lastDotIndex);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "获取扩展名出错", e);
        }
        return "";
    }

    /**
     * 读取缓存文件内容
     *
     * @param file 缓存文件
     * @return 文件字节数据
     */
    public byte[] readCacheFile(File file) {
        try {
            byte[] buffer = new byte[(int) file.length()];
            try (FileInputStream fis = new FileInputStream(file)) {
                fis.read(buffer);
                return buffer;
            }
        } catch (Exception e) {
            Log.e(TAG, "读取缓存文件出错", e);
        }
        return null;
    }

    /**
     * 获取缓存目录大小（字节）
     *
     * @return 缓存大小
     */
    public long getCacheSize() {
        long size = 0;
        try {
            if (cacheDir.exists() && cacheDir.isDirectory()) {
                File[] files = cacheDir.listFiles();
                if (files != null) {
                    for (File file : files) {
                        size += file.length();
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "计算缓存大小出错", e);
        }
        return size;
    }
}
