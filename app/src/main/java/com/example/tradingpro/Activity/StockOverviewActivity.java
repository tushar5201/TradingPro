package com.example.tradingpro.Activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.example.tradingpro.Adapter.TabAdapter;
import com.example.tradingpro.Interfaces.StockPrice;
import com.example.tradingpro.Model.StockPriceModel;
import com.example.tradingpro.OverviewTabFragment.CompanyProfileFragment;
import com.example.tradingpro.OverviewTabFragment.OverviewFragment;
import com.example.tradingpro.R;
import com.github.mikephil.charting.charts.CandleStickChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CandleEntry;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import java.text.DecimalFormat;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class StockOverviewActivity extends AppCompatActivity {

    TabLayout tabstock;
    ViewPager viewpagerstock;
    ImageView cancelButton;
    TextView stockPrice, stockPlusMinusPoints, stockPlusMinusPercentage;
    Handler handler = new Handler();
    Runnable runnable;
    private static final String BASE_YAHOO_URL = "https://query1.finance.yahoo.com/";
    String previousClose, price, plusMinusPoints, plusMinusPercentage;
    CandleStickChart candleStickChart;
    MaterialButton btnBuy, btnSell;


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
        cancelButton = findViewById(R.id.cancelButton);
        candleStickChart = findViewById(R.id.candleChart);
        btnSell = findViewById(R.id.btnSell);
        btnBuy = findViewById(R.id.btnBuy);


//        getting stock and display
        Intent i1 = getIntent();
        String symbolName = i1.getStringExtra("symbolName");
        String symbol = i1.getStringExtra("symbol");

        OverviewFragment overviewFragment = new OverviewFragment();
        CompanyProfileFragment companyProfileFragment = new CompanyProfileFragment();
        Bundle bundle = new Bundle();
        bundle.putString("symbol", symbol);
        companyProfileFragment.setArguments(bundle);
        overviewFragment.setArguments(bundle);

        ((TextView) findViewById(R.id.symbolFullName)).setText(symbolName);
        ((TextView) findViewById(R.id.stockName)).setText(symbol);

//        cancel button
        cancelButton.setOnClickListener(v -> {
            finish();
        });

//        buy and sell btn
        btnBuy.setOnClickListener(v-> {
            DialogPlus dialogPlus = DialogPlus.newDialog(this)
                    .setExpanded(true)
                    .setContentHeight(ViewGroup.LayoutParams.MATCH_PARENT)
                    .setContentHolder(new ViewHolder(R.layout.dialog_buy))
//                    .setHeader(R.layout.dialog_buy)
                    .create();
            dialogPlus.show();

//            View view = dialogPlus.getHolderView();
//            TextView tvText = view.findViewById(R.id.tvText);

//            tvText.setOnClickListener(v1-> {
//                dialogPlus.dismiss();
//            });

        });

//        for orientation change
        int orientation = getResources().getConfiguration().orientation;

        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // The screen is in landscape mode
            candlechart();

        } else if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            // The screen is in portrait mode

//      tabview
            TabAdapter adapter = new TabAdapter(getSupportFragmentManager());
            adapter.addFragment(overviewFragment);
            adapter.addFragment(companyProfileFragment);
            viewpagerstock.setAdapter(adapter);
            tabstock.setupWithViewPager(viewpagerstock);

        }

//        price
        runnable = new Runnable() {
            @Override
            public void run() {
                fetchStockPrice(symbol);
                handler.postDelayed(this, 3000);
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

    public void candlechart() {
        ArrayList<CandleEntry> candleEntries = new ArrayList<>();
        // Add your data here
        // Example data:
        candleEntries.add(new CandleEntry(0, 270f, 250f, 260f, 255f));
        candleEntries.add(new CandleEntry(1, 280f, 240f, 270f, 250f));
        candleEntries.add(new CandleEntry(2, 290f, 260f, 280f, 275f));
        candleEntries.add(new CandleEntry(3, 300f, 270f, 290f, 285f));
        candleEntries.add(new CandleEntry(4, 270f, 250f, 260f, 255f));
        candleEntries.add(new CandleEntry(5, 280f, 240f, 270f, 250f));
        candleEntries.add(new CandleEntry(6, 290f, 260f, 280f, 275f));
        candleEntries.add(new CandleEntry(7, 300f, 270f, 290f, 285f));
        candleEntries.add(new CandleEntry(8, 320f, 290f, 310f, 305f));
        candleEntries.add(new CandleEntry(9, 340f, 310f, 330f, 325f));
        candleEntries.add(new CandleEntry(10, 280f, 240f, 340f, 320f));

        CandleDataSet set = new CandleDataSet(candleEntries, "Data Set");
        set.setColor(Color.rgb(80, 80, 80));
        set.setShadowColor(Color.DKGRAY);
        set.setShadowWidth(0.7f);
        set.setIncreasingColor(Color.GREEN);
        set.setIncreasingPaintStyle(Paint.Style.FILL);
        set.setDecreasingColor(Color.RED);
        set.setDecreasingPaintStyle(Paint.Style.FILL);
        set.setNeutralColor(Color.BLUE);
        set.setValueTextColor(Color.WHITE);

        CandleData data = new CandleData(set);

        candleStickChart.setData(data);
        candleStickChart.invalidate(); // Refresh the chart

        XAxis xAxis = candleStickChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);

        YAxis leftAxis = candleStickChart.getAxisLeft();
        leftAxis.setEnabled(false);

        YAxis rightAxis = candleStickChart.getAxisRight();
        rightAxis.setDrawGridLines(false);
    }

}