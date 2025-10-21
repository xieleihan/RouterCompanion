package com.southaki.gamemarket.network;

import android.content.Context;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * DNS 解析器（带缓存）
 */
public class DnsResolver {
    private static final Executor executor = Executors.newSingleThreadExecutor();
    private final DnsCache dnsCache;

    public DnsResolver(Context context) {
        this.dnsCache = DnsCache.getInstance(context);
    }

    /**
     * 异步解析域名
     */
    public void resolveDns(String host, OnDnsResolveListener listener) {
        // 先检查缓存
        String cachedIp = dnsCache.getIpAddress(host);
        if (cachedIp != null) {
            if (listener != null) {
                listener.onSuccess(cachedIp);
            }
            return;
        }

        // 缓存未命中，进行 DNS 查询
        executor.execute(() -> {
            try {
                InetAddress inetAddress = InetAddress.getByName(host);
                String ipAddress = inetAddress.getHostAddress();

                // 缓存结果
                dnsCache.cacheDnsResult(host, ipAddress);

                if (listener != null) {
                    listener.onSuccess(ipAddress);
                }
            } catch (UnknownHostException e) {
                if (listener != null) {
                    listener.onFailed(e);
                }
            }
        });
    }

    /**
     * 同步解析域名（需要在子线程调用）
     */
    public String resolveDnsSync(String host) throws UnknownHostException {
        // 先检查缓存
        String cachedIp = dnsCache.getIpAddress(host);
        if (cachedIp != null) {
            return cachedIp;
        }

        // 缓存未命中，进行 DNS 查询
        InetAddress inetAddress = InetAddress.getByName(host);
        String ipAddress = inetAddress.getHostAddress();

        // 缓存结果
        dnsCache.cacheDnsResult(host, ipAddress);

        return ipAddress;
    }

    public interface OnDnsResolveListener {
        void onSuccess(String ipAddress);

        void onFailed(Exception e);
    }
}
