package com.example.tradingpro.Interfaces;

import com.example.tradingpro.Model.ScreenerResponseModel;
import com.example.tradingpro.Model.YahooSearchResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface YahooFinanceApi {
    @GET("v1/finance/screener/predefined/saved")
    Call<ScreenerResponseModel> getPredefinedScreener(
            @Query("formatted") boolean formatted,
            @Query("scrIds") String scrIds,
            @Query("count") int count
    );

    @GET("v1/finance/search")
    Call<YahooSearchResponse> searchStocks(
            @Query("q") String query,
            @Query("quotesCount") int quotesCount,
            @Query("newsCount") int newsCount
    );
}
