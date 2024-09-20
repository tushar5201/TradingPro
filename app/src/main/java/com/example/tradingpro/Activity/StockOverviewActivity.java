package com.example.tradingpro.Activity;

import static android.app.ProgressDialog.show;
import static java.security.AccessController.getContext;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager.widget.ViewPager;

import com.example.tradingpro.Adapter.OverviewTabAdapter;
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
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

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
    String previousClose, price, plusMinusPoints, plusMinusPercentage, symbol;
    CandleStickChart candleStickChart;
    MaterialButton btnBuy, btnSell;
    ArrayList<Integer> timestamps = new ArrayList<>();
    ArrayList<Float> highs = new ArrayList<>();
    ArrayList<Float> lows = new ArrayList<>();
    ArrayList<Float> opens = new ArrayList<>();
    ArrayList<Float> closes = new ArrayList<>();


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
        symbol = i1.getStringExtra("symbol");

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
        btnBuy.setOnClickListener(v -> {
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
            OverviewTabAdapter adapter = new OverviewTabAdapter(getSupportFragmentManager());
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
        timestamps.clear();
        opens.clear();
        closes.clear();
        highs.clear();
        lows.clear();

//        candleStickChart.setMaxVisibleValueCount(20);// for max candle visible
//        candleStickChart.setPinchZoom(true);//zoom
//        candleStickChart.setDragEnabled(true);//allow dragging
//        candleStickChart.setScaleEnabled(true);//scaling
//        candleStickChart.setHighlightPerDragEnabled(true);// highlight on drag

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
                    StockPriceModel stockPriceModel = response.body();
                    if (stockPriceModel != null) {
                        timestamps.addAll(stockPriceModel.chart.result[0].timestamp);
                        opens.addAll(stockPriceModel.chart.result[0].indicators.quote[0].open);
                        closes.addAll(stockPriceModel.chart.result[0].indicators.quote[0].close);
                        highs.addAll(stockPriceModel.chart.result[0].indicators.quote[0].high);
                        lows.addAll(stockPriceModel.chart.result[0].indicators.quote[0].low);

                        // Create a dataset with the data
                        ArrayList<CandleEntry> values = new ArrayList<>();
//                        Toast.makeText(getApplicationContext(), String.valueOf(opens.size()), Toast.LENGTH_SHORT).show();

                        for (int i = 0; i < timestamps.size(); i++) {
                            Float open = opens.get(i);
                            Float close = closes.get(i);
                            Float high = highs.get(i);
                            Float low = lows.get(i);

                            if (open != null && close != null && high != null && low != null) {
                                values.add(new CandleEntry(i, high, low, open, close));
                            }
                        }

                        // Add your data here
                        CandleDataSet set = new CandleDataSet(values, "Stock Prices");
                        set.setColor(Color.rgb(80, 80, 80));
                        set.setShadowColor(Color.DKGRAY);
                        set.setShadowWidth(0.5f);

                        set.setIncreasingColor(Color.GREEN);
//                        set.setShowCandleBar(true);
//                        set.setHighlightLineWidth(1f);
                        set.setIncreasingPaintStyle(Paint.Style.FILL);
                        set.setDecreasingColor(Color.RED);
                        set.setDecreasingPaintStyle(Paint.Style.FILL);

//                        set.setNeutralColor(Color.BLUE);
                        set.setValueTextColor(Color.WHITE);
                        set.setDrawValues(false);
//                        set.setBarSpace(1f);

                        CandleData data = new CandleData(set);

                        candleStickChart.setData(data);
                        candleStickChart.invalidate(); // Refresh the chart

                        XAxis xAxis = candleStickChart.getXAxis();
                        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                        xAxis.setDrawGridLines(true);
                        xAxis.setGridColor(Color.LTGRAY);
                        //

                        xAxis.setValueFormatter(new ValueFormatter() {
                            @Override
                            public String getFormattedValue(float value) {
                                // Convert timestamp to human-readable date
                                int index = (int) value;
//                                Toast.makeText(getApplicationContext(), String.valueOf(index), Toast.LENGTH_SHORT).show();
                                if (index >= 0 && index < timestamps.size()) {
                                    long timestampInMillis = timestamps.get(index) * 1000L;
//                                Toast.makeText(getApplicationContext(), String.valueOf(timestampInMillis), Toast.LENGTH_SHORT).show();
                                    Date date = new Date(timestampInMillis);
                                    SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault());
                                    return sdf.format(date);
                                } else {
                                    return "";
                                }

//                                long timestampInMillis = (long) value * 1000;
//                                Date date = new Date(timestampInMillis);
//                                SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault());
//                                return sdf.format(date);
                            }
                        });

                        YAxis leftAxis = candleStickChart.getAxisLeft();
                        leftAxis.setEnabled(false);

                        YAxis rightAxis = candleStickChart.getAxisRight();
                        rightAxis.setDrawGridLines(true);
                        rightAxis.setGridColor(Color.LTGRAY);

                    } else {
                        Log.e("Retrofit", "Request failed with status code: " + response.code());
                    }
                }

            }

            @Override
            public void onFailure(Call<StockPriceModel> call, Throwable t) {
                Log.e("Retrofit", "Network request failed", t);
            }
        });
    }

}