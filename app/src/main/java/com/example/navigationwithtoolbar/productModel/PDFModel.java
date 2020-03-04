package com.example.navigationwithtoolbar.productModel;

public class PDFModel {
    private String title;
    private String url;

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public PDFModel(String title, String url) {
        this.title = title;
        this.url = url;
    }
}
