package com.example.tradingpro.Trading;

import java.io.Serializable;

public class OrderModel implements Serializable {
    private String orderId;
    private String symbol;
    private String companyName;
    private String orderType;   // BUY, SELL
    private String productType; // DELIVERY, INTRADAY
    private int quantity;
    private double price;
    private double totalAmount;
    private String status;      // COMPLETED, CANCELLED
    private long timestamp;
    private String formattedDate;

    public OrderModel() {
    }

    public OrderModel(String orderId, String symbol, String companyName, String orderType, String productType, int quantity, double price, double totalAmount, String status, long timestamp, String formattedDate) {
        this.orderId = orderId;
        this.symbol = symbol;
        this.companyName = companyName;
        this.orderType = orderType;
        this.productType = productType;
        this.quantity = quantity;
        this.price = price;
        this.totalAmount = totalAmount;
        this.status = status;
        this.timestamp = timestamp;
        this.formattedDate = formattedDate;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
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

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getFormattedDate() {
        return formattedDate;
    }

    public void setFormattedDate(String formattedDate) {
        this.formattedDate = formattedDate;
    }
}
