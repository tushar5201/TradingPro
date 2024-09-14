package com.example.tradingpro.Model;

public class GainerLosersModel {
    public String symbol, symbolName, price, change, changesPercentage;

    public GainerLosersModel(String symbol, String symbolName, String price, String change, String changesPercentage) {
        this.symbol = symbol;
        this.price = price;
        this.change = change;
        this.changesPercentage = changesPercentage;
        this.symbolName = symbolName;
    }

    public GainerLosersModel() {
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

}
