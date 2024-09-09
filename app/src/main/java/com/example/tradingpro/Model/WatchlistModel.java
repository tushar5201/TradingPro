package com.example.tradingpro.Model;

public class WatchlistModel {
    public String symbol, symbolName, stockPlusMinusPercentage, stockPlusMinusPoints, stockPrice;

    public WatchlistModel(String symbol, String symbolName, String stockPrice, String stockPlusMinusPoints,String stockPlusMinusPercentage) {
        this.symbol = symbol;
        this.symbolName = symbolName;
        this.stockPlusMinusPercentage = stockPlusMinusPercentage;
        this.stockPlusMinusPoints = stockPlusMinusPoints;
        this.stockPrice = stockPrice;
    }

    public WatchlistModel(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbolName() {
        return symbolName;
    }

    public void setSymbolName(String symbolName) {
        this.symbolName = symbolName;
    }

    public String getStockPlusMinusPercentage() {
        return stockPlusMinusPercentage;
    }

    public void setStockPlusMinusPercentage(String stockPlusMinusPercentage) {
        this.stockPlusMinusPercentage = stockPlusMinusPercentage;
    }

    public String getStockPlusMinusPoints() {
        return stockPlusMinusPoints;
    }

    public void setStockPlusMinusPoints(String stockPlusMinusPoints) {
        this.stockPlusMinusPoints = stockPlusMinusPoints;
    }

    public String getStockPrice() {
        return stockPrice;
    }

    public void setStockPrice(String stockPrice) {
        this.stockPrice = stockPrice;
    }
}
