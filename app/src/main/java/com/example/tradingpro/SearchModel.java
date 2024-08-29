package com.example.tradingpro;

public class SearchModel {
    public String symbol, symbolName;

    public SearchModel(String symbol, String symbolName) {
        this.symbol = symbol;
        this.symbolName = symbolName;
    }

    public SearchModel() {
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
}
