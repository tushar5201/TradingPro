package com.example.tradingpro.Model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ScreenerResponseModel {
    @SerializedName("finance")
    public Finance finance;

    public static class Finance {
        @SerializedName("result")
        public List<Result> result;
    }

    public static class Result {
        @SerializedName("id")
        public String id;

        @SerializedName("title")
        public String title;

        @SerializedName("quotes")
        public List<Quote> quotes;
    }

    public static class Quote {
        @SerializedName("symbol")
        public String symbol;

        @SerializedName("shortName")
        public String shortName;

        @SerializedName("longName")
        public String longName;

        @SerializedName("regularMarketPrice")
        public Double regularMarketPrice;

        @SerializedName("regularMarketChange")
        public Double regularMarketChange;

        @SerializedName("regularMarketChangePercent")
        public Double regularMarketChangePercent;

        @SerializedName("currency")
        public String currency;

        @SerializedName("fullExchangeName")
        public String fullExchangeName;
    }
}
