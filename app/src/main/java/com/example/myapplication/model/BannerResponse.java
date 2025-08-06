package com.example.myapplication.model;

import java.util.List;

public class BannerResponse {
    private int code;
    private String msg;
    private List<BannerItem> data;

    // Getters and Setters
    public int getCode() { return code; }
    public void setCode(int code) { this.code = code; }

    public String getMsg() { return msg; }
    public void setMsg(String msg) { this.msg = msg; }

    public List<BannerItem> getData() { return data; }
    public void setData(List<BannerItem> data) { this.data = data; }

    @Override
    public String toString() {
        return "BannerResponse{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }
}
