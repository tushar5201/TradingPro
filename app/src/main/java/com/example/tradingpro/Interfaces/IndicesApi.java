package com.example.tradingpro.Interfaces;

import com.example.tradingpro.Model.IndicesResponseModel;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface IndicesApi {
    @GET("v8/finance/chart/{symbolNames}")
    Call<IndicesResponseModel> getIndicesPrice(@Path("symbolNames") String symbolNames);
}
