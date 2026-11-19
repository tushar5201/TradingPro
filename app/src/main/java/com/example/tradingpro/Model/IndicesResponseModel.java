package com.example.tradingpro.Model;

import com.google.gson.annotations.SerializedName;

public class IndicesResponseModel {
    @SerializedName("chart")
    public Chart chart;

    public static class Chart {
        @SerializedName("result")
        public Result[] result;

        public static class Result {
            @SerializedName("meta")
            public Meta meta;
        }
    }

    public static class Meta {
        @SerializedName("previousClose")
        public String previousClose;

        @SerializedName("chartPreviousClose")
        public String chartPreviousClose;

        @SerializedName("regularMarketPrice")
        public String regularMarketPrice;

        @SerializedName("symbol")
        public String symbol;

        @SerializedName("longName")
        public String symbolFullname;

        @SerializedName("shortName")
        public String shortName;

        public String getPrevClose() {
            if (previousClose != null && !previousClose.isEmpty()) return previousClose;
            return chartPreviousClose != null ? chartPreviousClose : "0";
        }

        public String getFullName() {
            if (symbolFullname != null && !symbolFullname.isEmpty()) return symbolFullname;
            return shortName != null ? shortName : symbol;
        }
    }
}
