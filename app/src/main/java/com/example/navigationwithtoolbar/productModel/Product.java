package com.example.navigationwithtoolbar.productModel;

public class Product {
    private String companyName;
    private String price;
    private String status;
    private String code;

    public String getCode() {
        return code;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getPrice() {
        return price;
    }

    public String getStatus() {
        return status;
    }

    public Product(String companyName, String price, String status,String code) {
        this.companyName = companyName;
        this.price = price;
        this.status = status;
        this.code = code;
    }
}
