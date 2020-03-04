package com.example.navigationwithtoolbar.productModel;

public class PortfolioProduct {
    private String companyName;
    private String price;
    private String status;
    private String code;
    private String availableStocks;
    private String worth;
    private String investment;
    private String position;


    public String getCode() {
        return code;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getAvailableStocks() {
        return availableStocks;
    }

    public String getWorth() {
        return worth;
    }

    public String getPrice() {
        return price;
    }

    public String getInvestment() {
        return investment;
    }

    public String getPosition() {
        return position;
    }

    public String getStatus() {
        return status;
    }

    public PortfolioProduct(String companyName, String price, String status, String code, String availableStocks, String worth, String investment/*, String position*/) {
        this.companyName = companyName;
        this.price = price;
        this.status = status;
        this.code = code;
        this.availableStocks = availableStocks;
        this.worth = worth;
        this.investment = investment;
      //  this.position = position;
    }
}
