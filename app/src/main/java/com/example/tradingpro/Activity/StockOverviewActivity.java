package com.example.tradingpro.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager.widget.ViewPager;

import com.example.tradingpro.Adapter.TabAdapter;
import com.example.tradingpro.Interfaces.StockPrice;
import com.example.tradingpro.Model.StockPriceModel;
import com.example.tradingpro.R;
import com.google.android.material.tabs.TabLayout;

import java.text.DecimalFormat;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class StockOverviewActivity extends AppCompatActivity {

    TabLayout tabstock;
    ViewPager viewpagerstock;
    TextView stockPrice, stockPlusMinusPoints, stockPlusMinusPercentage;
    Handler handler = new Handler();
    Runnable runnable;
    private static final String BASE_YAHOO_URL = "https://query1.finance.yahoo.com/";
    String previousClose, price, plusMinusPoints, plusMinusPercentage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_stock_overview);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(40, 100, 40, 50);
            return insets;
        });

        tabstock = findViewById(R.id.tabstock);
        viewpagerstock = findViewById(R.id.viewpagerstock);
        stockPrice = findViewById(R.id.stockPrice);
        stockPlusMinusPoints = findViewById(R.id.stockPlusMinusPoints);
        stockPlusMinusPercentage = findViewById(R.id.stockPlusMinusPercentage);

        Intent i1 = getIntent();
        String symbolName = i1.getStringExtra("symbolName");
        String symbol = i1.getStringExtra("symbol");

        ((TextView) findViewById(R.id.symbolFullName)).setText(symbolName);
        ((TextView) findViewById(R.id.stockName)).setText(symbol);

//      tabview
        TabAdapter adapter = new TabAdapter(getSupportFragmentManager());
        viewpagerstock.setAdapter(adapter);

        tabstock.setupWithViewPager(viewpagerstock);

//        price
        runnable = new Runnable() {
            @Override
            public void run() {
                fetchStockPrice(symbol);
                handler.postDelayed(this, 10000);
            }
        };
        handler.post(runnable);
    }



    public void fetchStockPrice(String symbol) {
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
                        price = stockPriceModel.chart.result[0].meta.regularMarketPrice;
                        previousClose = stockPriceModel.chart.result[0].meta.previousClose;
                        plusMinusPoints = decim.format(Double.parseDouble(price) - Double.parseDouble(previousClose));
                        plusMinusPercentage = "  (" + decim.format(((Double.parseDouble(plusMinusPoints) * 100) / Double.parseDouble(price))) + "%)";
                        if (Double.parseDouble(plusMinusPoints) > 0) {
                            stockPlusMinusPoints.setTextColor(Color.parseColor("#3FC33F"));
                            stockPlusMinusPercentage.setTextColor(Color.parseColor("#3FC33F"));
                            plusMinusPoints = "+" + plusMinusPoints;
                            plusMinusPercentage = " (+" + decim.format(((Double.parseDouble(plusMinusPoints) * 100) / Double.parseDouble(price))) + "%)";
                        } else {
                            stockPlusMinusPoints.setTextColor(Color.parseColor("#D53030"));
                            stockPlusMinusPercentage.setTextColor(Color.parseColor("#D53030"));
                        }
                        stockPrice.setText(price);
                        stockPlusMinusPoints.setText(plusMinusPoints);
                        stockPlusMinusPercentage.setText(plusMinusPercentage);
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