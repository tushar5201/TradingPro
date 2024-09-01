package com.example.tradingpro.Model;

public class IndicesModel {
    public String symbol, symbolFullName, stockPrice, stockPlusMinusPoints, stockPlusMinusPercentage, symbolIcon;

    public IndicesModel() {
    }

    public IndicesModel(String symbol, String symbolFullName, String stockPrice, String stockPlusMinusPoints, String stockPlusMinusPercentage, String symbolIcon) {
        this.symbol = symbol;
        this.symbolFullName = symbolFullName;
        this.stockPrice = stockPrice;
        this.stockPlusMinusPoints = stockPlusMinusPoints;
        this.stockPlusMinusPercentage = stockPlusMinusPercentage;
        this.symbolIcon = symbolIcon;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbolFullName() {
        return symbolFullName;
    }

    public void setSymbolFullName(String symbolFullName) {
        this.symbolFullName = symbolFullName;
    }

    public String getStockPrice() {
        return stockPrice;
    }

    public void setStockPrice(String stockPrice) {
        this.stockPrice = stockPrice;
    }

    public String getStockPlusMinusPoints() {
        return stockPlusMinusPoints;
    }

    public void setStockPlusMinusPoints(String stockPlusMinusPoints) {
        this.stockPlusMinusPoints = stockPlusMinusPoints;
    }

    public String getStockPlusMinusPercentage() {
        return stockPlusMinusPercentage;
    }

    public void setStockPlusMinusPercentage(String stockPlusMinusPercentage) {
        this.stockPlusMinusPercentage = stockPlusMinusPercentage;
    }

    public String getSymbolIcon() {
        return symbolIcon;
    }

    public void setSymbolIcon(String symbolIcon) {
        this.symbolIcon = symbolIcon;
    }
}
