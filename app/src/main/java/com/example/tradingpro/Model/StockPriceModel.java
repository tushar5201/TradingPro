package com.example.tradingpro.Model;

public class StockPriceModel {
    public Chart chart;

    public static class Chart {
        public Result[] result;

        public static class Result {
            public Meta meta;
        }
    }

    public static class Meta {
        public String regularMarketPrice, previousClose;
    }

}
