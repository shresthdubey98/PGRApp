package com.example.navigationwithtoolbar.productModel;

public class VideoModel {
    private String title;
    private String url;

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public VideoModel(String title, String url) {
        this.title = title;
        this.url = url;
    }
}
