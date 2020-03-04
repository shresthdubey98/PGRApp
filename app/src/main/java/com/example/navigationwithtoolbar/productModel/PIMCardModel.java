package com.example.navigationwithtoolbar.productModel;


public class PIMCardModel {
    final String sno;
    final String title;


    public String getSno() {
        return sno;
    }

    public String getTitle() {
        return title;
    }

    public PIMCardModel(String sno, String title) {
        this.sno = sno;
        this.title = title;

    }
}
