package com.example.tradingpro.Model;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import io.finnhub.api.models.Indicator;

public class StockPriceModel {
    public Chart chart;

    public static class Chart {
        public Result[] result;

        public static class Result {
            public Meta meta;
            public ArrayList<Integer> timestamp;
            public Indicators indicators;
        }
    }

    public static class Meta {
        public String regularMarketPrice, previousClose, fiftyTwoWeekHigh, fiftyTwoWeekLow, regularMarketDayHigh, regularMarketDayLow, regularMarketVolume;
    }

    public static class Indicators {
        public Quote[] quote;

        public static class Quote {
            public ArrayList<Float> close;
        }
    }
}
