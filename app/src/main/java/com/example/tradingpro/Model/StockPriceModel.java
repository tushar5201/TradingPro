package com.example.tradingpro.Model;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;

public class StockPriceModel {
    @SerializedName("chart")
    public Chart chart;

    public static class Chart {
        @SerializedName("result")
        public Result[] result;

        public static class Result {
            @SerializedName("meta")
            public Meta meta;

            @SerializedName("timestamp")
            public ArrayList<Integer> timestamp;

            @SerializedName("indicators")
            public Indicators indicators;
        }
    }

    public static class Meta {
        @SerializedName("regularMarketPrice")
        public String regularMarketPrice;

        @SerializedName("previousClose")
        public String previousClose;

        @SerializedName("chartPreviousClose")
        public String chartPreviousClose;

        @SerializedName("fiftyTwoWeekHigh")
        public String fiftyTwoWeekHigh;

        @SerializedName("fiftyTwoWeekLow")
        public String fiftyTwoWeekLow;

        @SerializedName("regularMarketDayHigh")
        public String regularMarketDayHigh;

        @SerializedName("regularMarketDayLow")
        public String regularMarketDayLow;

        @SerializedName("regularMarketVolume")
        public String regularMarketVolume;

        @SerializedName("symbol")
        public String symbol;

        @SerializedName("longName")
        public String longName;

        @SerializedName("shortName")
        public String shortName;

        @SerializedName("currency")
        public String currency;

        @SerializedName("fullExchangeName")
        public String fullExchangeName;

        @SerializedName("exchangeName")
        public String exchangeName;

        @SerializedName("regularMarketChangePercent")
        public String regularMarketChangePercent;
    }

    public static class Indicators {
        @SerializedName("quote")
        public Quote[] quote;

        public static class Quote {
            @SerializedName("close")
            public ArrayList<Float> close;

            @SerializedName("open")
            public ArrayList<Float> open;

            @SerializedName("high")
            public ArrayList<Float> high;

            @SerializedName("low")
            public ArrayList<Float> low;
        }
    }
}
