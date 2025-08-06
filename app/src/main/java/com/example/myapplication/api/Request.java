package com.example.myapplication.api;
import java.net.URLEncoder;

public class Request {
    // 获取封面背景
    public static String getPicBackground(){
        String url = "https://picsum.photos/2160/3840";
        return url;
    }

    // 获取首页Banner
    public static String getHomeBanner(){
        String url = "https://apis.netstart.cn/bcomic/Banner";
        return url;
    }
}
