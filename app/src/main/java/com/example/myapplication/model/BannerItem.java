package com.example.myapplication.model;

public class BannerItem {
    private int id;
    private String title;
    private String img; // 注意：JSON 中的字段名是 img
    private int jump_type;
    private String jump_value;
    private String img2;
    private String bg;

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getImg() { return img; } // Getter for img
    public void setImg(String img) { this.img = img; }

    public int getJump_type() { return jump_type; }
    public void setJump_type(int jump_type) { this.jump_type = jump_type; }

    public String getJump_value() { return jump_value; }
    public void setJump_value(String jump_value) { this.jump_value = jump_value; }

    public String getImg2() { return img2; }
    public void setImg2(String img2) { this.img2 = img2; }

    public String getBg() { return bg; }
    public void setBg(String bg) { this.bg = bg; }

    @Override
    public String toString() {
        return "BannerItem{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", img='" + img + '\'' +
                ", jump_type=" + jump_type +
                ", jump_value='" + jump_value + '\'' +
                ", img2='" + img2 + '\'' +
                ", bg='" + bg + '\'' +
                '}';
    }
}
