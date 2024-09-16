package com.example.tradingpro.OverviewTabFragment;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
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
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.tradingpro.Interfaces.StockPrice;
import com.example.tradingpro.Model.StockPriceModel;
import com.example.tradingpro.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
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
    LineChart lineChart;
    ArrayList<Integer> timestamps = new ArrayList<>();
    ArrayList<Float> prices = new ArrayList<>();


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
        lineChart = view.findViewById(R.id.chart);

        setupChart();

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


        runnable = new Runnable() {
            @Override
            public void run() {
                fetchChartData(receivedSymbol);
                handler.postDelayed(this, 60000);
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


    public void setupChart() {
        lineChart.getDescription().setEnabled(false);
        lineChart.setDrawGridBackground(false);

        // Customize X-Axis
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(true);
//        xAxis.setAxisMaximum(345f);
//        xAxis.setAxisMinimum(0f);


        // Customize Y-Axis (left)
        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setDrawGridLines(true);
        leftAxis.setTextColor(Color.LTGRAY);  // Label color

        // Disable the right Y-Axis
        lineChart.getAxisRight().setEnabled(false);
    }

    public void fetchChartData(String symbol) {
        timestamps.clear();
        prices.clear();
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
                        prices.addAll(stockPriceModel.chart.result[0].indicators.quote[0].close);

                        // Create a dataset with the data
                        ArrayList<Entry> values = new ArrayList<>();
//                        Toast.makeText(getContext(), String.valueOf(prices.size()), Toast.LENGTH_SHORT).show();
                        for (int i = 0; i < timestamps.size(); i++) {
                            Float price = prices.get(i);
                            Integer time = timestamps.get(i);
                            if (price != null) {
                                values.add(new Entry(time, price));
                            }
                        }
                        LineDataSet lineDataSet = new LineDataSet(values, "Stock Prices");

                        // Customize dataset appearance
                        if ((current - previousClose) >= 0.0) {
                            lineDataSet.setColor(Color.GREEN);
                        } else {
                            lineDataSet.setColor(Color.RED);  // Fill color
                        }
                        lineDataSet.setLineWidth(1f);  // Line width
                        lineDataSet.setDrawCircles(false);  // No circles at data points
                        lineDataSet.setDrawValues(false);  // No values at data points
                        lineDataSet.setDrawFilled(true);  // Fill the area under the line
                        // Customize fill for gradient effect (optional)
                        lineDataSet.setFillAlpha(110);
                        if (current - previousClose >= 0) {
                            try {
                                Drawable drawable = ContextCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.green_gradiant_fill);
                                lineDataSet.setFillDrawable(drawable);
                            } catch (Exception ex) {

                            }
                        } else {
                            try {
                                Drawable drawable = ContextCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.red_gradiant_fill);
                                lineDataSet.setFillDrawable(drawable);
                            } catch (Exception ex) {

                            }
                        }

                        // Set the data to the chart
                        LineData lineData = new LineData(lineDataSet);
                        lineChart.setData(lineData);
                        // Refresh the chart
                        lineChart.invalidate();
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