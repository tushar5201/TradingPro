package com.example.tradingpro.Interfaces;

import com.example.tradingpro.Model.StockPriceModel;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface StockPrice {
    @GET("v8/finance/chart/{symbolName}")
    Call<StockPriceModel> getChartData(@Path("symbolName") String symbolName);
}
