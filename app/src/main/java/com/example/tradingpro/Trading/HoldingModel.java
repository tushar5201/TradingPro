package com.example.tradingpro.Trading;

import java.io.Serializable;

public class HoldingModel implements Serializable {
    private String symbol;
    private String companyName;
    private int quantity;
    private double avgBuyPrice;
    private double currentPrice;
    private String productType; // DELIVERY, INTRADAY
    private String exchange;

    public HoldingModel() {
    }

    public HoldingModel(String symbol, String companyName, int quantity, double avgBuyPrice, double currentPrice, String productType, String exchange) {
        this.symbol = symbol;
        this.companyName = companyName;
        this.quantity = quantity;
        this.avgBuyPrice = avgBuyPrice;
        this.currentPrice = currentPrice;
        this.productType = productType != null ? productType : "DELIVERY";
        this.exchange = exchange != null ? exchange : "NSE";
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getCompanyName() {
        return companyName != null ? companyName : symbol;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getAvgBuyPrice() {
        return avgBuyPrice;
    }

    public void setAvgBuyPrice(double avgBuyPrice) {
        this.avgBuyPrice = avgBuyPrice;
    }

    public double getCurrentPrice() {
        return currentPrice > 0 ? currentPrice : avgBuyPrice;
    }

    public void setCurrentPrice(double currentPrice) {
        this.currentPrice = currentPrice;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public double getTotalInvested() {
        return quantity * avgBuyPrice;
    }

    public double getCurrentValue() {
        return quantity * getCurrentPrice();
    }

    public double getProfitLoss() {
        return getCurrentValue() - getTotalInvested();
    }

    public double getProfitLossPercentage() {
        double invested = getTotalInvested();
        if (invested <= 0) return 0.0;
        return ((getCurrentValue() - invested) / invested) * 100.0;
    }
}
