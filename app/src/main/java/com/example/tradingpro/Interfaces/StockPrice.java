package com.example.tradingpro.Interfaces;

import com.example.tradingpro.Model.StockPriceModel;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface StockPrice {
    @GET("v8/finance/chart/{symbolName}")
    Call<StockPriceModel> getChartData(@Path("symbolName") String symbolName);

    @GET("v8/finance/chart/{symbolName}")
    Call<StockPriceModel> getChartDataWithRange(
            @Path("symbolName") String symbolName,
            @Query("range") String range,
            @Query("interval") String interval
    );
}
