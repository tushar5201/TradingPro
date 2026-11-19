package com.example.tradingpro.Model;

public class GainerLosersModel {
    private String symbol;
    private String symbolName;
    private String price;
    private String change;
    private String changesPercentage;
    private String exchange = "NSE";

    public GainerLosersModel() {
    }

    public GainerLosersModel(String symbol, String symbolName, String price, String change, String changesPercentage) {
        this.symbol = symbol;
        this.symbolName = symbolName;
        this.price = price;
        this.change = change;
        this.changesPercentage = changesPercentage;
    }

    public GainerLosersModel(String symbol, String symbolName, String price, String change, String changesPercentage, String exchange) {
        this.symbol = symbol;
        this.symbolName = symbolName;
        this.price = price;
        this.change = change;
        this.changesPercentage = changesPercentage;
        this.exchange = exchange;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbolName() {
        return symbolName != null ? symbolName : symbol;
    }

    public void setSymbolName(String symbolName) {
        this.symbolName = symbolName;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getChange() {
        return change;
    }

    public void setChange(String change) {
        this.change = change;
    }

    public String getChangesPercentage() {
        return changesPercentage;
    }

    public void setChangesPercentage(String changesPercentage) {
        this.changesPercentage = changesPercentage;
    }

    public String getExchange() {
        return exchange != null ? exchange : "NSE";
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }
}
