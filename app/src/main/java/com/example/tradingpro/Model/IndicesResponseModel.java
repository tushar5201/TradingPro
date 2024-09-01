package com.example.tradingpro.Model;

import com.google.gson.annotations.SerializedName;

public class IndicesResponseModel {
    @SerializedName("chart")
    public Chart chart;

    public class Chart {
        @SerializedName("result")
        public Result[] result;

        public class Result {
            @SerializedName("meta")
            public Meta meta;
        }
    }

    public class Meta {
        @SerializedName("previousClose")
        public String previousClose;

        @SerializedName("regularMarketPrice")
        public String regularMarketPrice;

        @SerializedName("symbol")
        public String symbol;

        @SerializedName("longName")
        public String symbolFullname;
    }
}
