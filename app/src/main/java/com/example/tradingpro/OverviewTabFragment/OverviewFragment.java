package com.example.tradingpro.OverviewTabFragment;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.tradingpro.Interfaces.StockPrice;
import com.example.tradingpro.Model.StockPriceModel;
import com.example.tradingpro.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class OverviewFragment extends Fragment {

    View view;
    Handler handler = new Handler();
    Runnable runnable;
    ProgressBar progressBarToday, progressBar52;
    ImageView indicatorToday, indicator52;
    TextView tvTodayLow, tvTodayHigh, tv52Low, tv52High, tvVolume, tvPrevClose;
    Double todayLow = 0.0;
    Double todayHigh = 0.0;
    Double fiftytwoLow = 0.0;
    Double fiftytwoHigh = 0.0;
    Double current = 0.0;
    Double previousClose = 0.0;
    String receivedSymbol;
    String volume;
    private static final String BASE_YAHOO_URL = "https://query1.finance.yahoo.com/";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_overview, container, false);
        if (getArguments() != null) {
            receivedSymbol = getArguments().getString("symbol");
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        progressBarToday = view.findViewById(R.id.progress_bar_today);
        progressBar52 = view.findViewById(R.id.progress_bar_52);
        indicatorToday = view.findViewById(R.id.indicator_today);
        indicator52 = view.findViewById(R.id.indicator_52);
        tvTodayHigh = view.findViewById(R.id.tvTodayHigh);
        tvTodayLow = view.findViewById(R.id.tvTodayLow);
        tv52High = view.findViewById(R.id.tv52High);
        tv52Low = view.findViewById(R.id.tv52Low);
        tvVolume = view.findViewById(R.id.tvVolume);
        tvPrevClose = view.findViewById(R.id.tvPrevClose);

        runnable = new Runnable() {
            @Override
            public void run() {
                fetchStockData(receivedSymbol);
                handler.postDelayed(this, 3000);

                int progressToday = (int) (((float) (current - todayLow) / (todayHigh - todayLow)) * 100);
                int progress52 = (int) (((float) (current - fiftytwoLow) / (fiftytwoHigh - fiftytwoLow)) * 100);

                progressBarToday.post(new Runnable() {
                    @Override
                    public void run() {
                        // Ensure the ProgressBar width is ready before calculating
                        int progressBarWidth = progressBarToday.getWidth();

                        // Calculate the new marginStart for the ImageView (indicator)
                        int newMarginStart = (int) (((float) progressToday / 100) * progressBarWidth);

                        // Get the current layout params of the ImageView
                        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) indicatorToday.getLayoutParams();

                        // Set the calculated marginStart
                        params.setMarginStart(newMarginStart);

                        // Apply the updated layout params back to the ImageView
                        indicatorToday.setLayoutParams(params);
                    }
                });
                progressBar52.post(new Runnable() {
                    @Override
                    public void run() {
                        // Ensure the ProgressBar width is ready before calculating
                        int progressBarWidth = progressBar52.getWidth();

                        // Calculate the new marginStart for the ImageView (indicator)
                        int newMarginStart = (int) (((float) progress52 / 100) * progressBarWidth);

                        // Get the current layout params of the ImageView
                        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) indicator52.getLayoutParams();

                        // Set the calculated marginStart
                        params.setMarginStart(newMarginStart);

                        // Apply the updated layout params back to the ImageView
                        indicator52.setLayoutParams(params);
                    }
                });
            }
        };
        handler.post(runnable);
    }

    public void fetchStockData(String symbol) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_YAHOO_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        StockPrice apiService = retrofit.create(StockPrice.class);
        Call<StockPriceModel> call = apiService.getChartData(symbol);

        call.enqueue(new Callback<StockPriceModel>() {
            @Override
            public void onResponse(Call<StockPriceModel> call, Response<StockPriceModel> response) {
                if (response.isSuccessful()) {
                    DecimalFormat decim = new DecimalFormat("###.##");
                    StockPriceModel stockPriceModel = response.body();
                    if (stockPriceModel != null) {
                        current = Double.parseDouble(stockPriceModel.chart.result[0].meta.regularMarketPrice);
                        previousClose = Double.parseDouble(stockPriceModel.chart.result[0].meta.previousClose);
                        fiftytwoHigh = Double.parseDouble(stockPriceModel.chart.result[0].meta.fiftyTwoWeekHigh);
                        fiftytwoLow = Double.parseDouble(stockPriceModel.chart.result[0].meta.fiftyTwoWeekLow);
                        todayHigh = Double.parseDouble(stockPriceModel.chart.result[0].meta.regularMarketDayHigh);
                        todayLow = Double.parseDouble(stockPriceModel.chart.result[0].meta.regularMarketDayLow);
                        volume = stockPriceModel.chart.result[0].meta.regularMarketVolume;

                        tv52High.setText(fiftytwoHigh.toString());
                        tv52Low.setText(fiftytwoLow.toString());
                        tvTodayHigh.setText(todayHigh.toString());
                        tvTodayLow.setText(todayLow.toString());
                        tvVolume.setText(volume.toString());
                        tvPrevClose.setText(previousClose.toString());
                    }
                } else {
                    Log.e("Retrofit", "Request failed with status code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<StockPriceModel> call, Throwable t) {
                Log.e("Retrofit", "Network request failed", t);
            }
        });

    }
}