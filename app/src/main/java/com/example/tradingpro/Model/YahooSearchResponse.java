package com.example.tradingpro.Model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class YahooSearchResponse {
    @SerializedName("count")
    public int count;

    @SerializedName("quotes")
    public List<SearchQuote> quotes;

    public static class SearchQuote {
        @SerializedName("symbol")
        public String symbol;

        @SerializedName("shortname")
        public String shortname;

        @SerializedName("longname")
        public String longname;

        @SerializedName("exchange")
        public String exchange;

        @SerializedName("exchDisp")
        public String exchDisp;

        @SerializedName("quoteType")
        public String quoteType;

        @SerializedName("sector")
        public String sector;

        @SerializedName("industry")
        public String industry;

        public String getDisplayName() {
            if (longname != null && !longname.isEmpty()) return longname;
            if (shortname != null && !shortname.isEmpty()) return shortname;
            return symbol != null ? symbol : "";
        }
    }
}
