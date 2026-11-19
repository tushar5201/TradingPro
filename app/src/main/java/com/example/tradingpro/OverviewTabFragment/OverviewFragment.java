package com.example.tradingpro.OverviewTabFragment;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.tradingpro.Constant.StockUtils;
import com.example.tradingpro.Interfaces.StockPrice;
import com.example.tradingpro.Model.StockPriceModel;
import com.example.tradingpro.Network.ApiClient;
import com.example.tradingpro.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OverviewFragment extends Fragment {

    private final Handler handler = new Handler();
    private Runnable priceRunnable;
    private Runnable chartRunnable;

    private ProgressBar progressBarToday, progressBar52;
    private ImageView indicatorToday, indicator52;
    private TextView tvTodayLow, tvTodayHigh, tv52Low, tv52High, tvVolume, tvPrevClose;

    private double todayLow = 0.0;
    private double todayHigh = 0.0;
    private double fiftytwoLow = 0.0;
    private double fiftytwoHigh = 0.0;
    private double current = 0.0;
    private double previousClose = 0.0;
    private String receivedSymbol = "RELIANCE.NS";
    private String volume = "0";

    private LineChart lineChart;
    private final ArrayList<Integer> timestamps = new ArrayList<>();
    private final ArrayList<Float> prices = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_overview, container, false);
        if (getArguments() != null) {
            String sym = getArguments().getString("symbol");
            if (sym != null && !sym.isEmpty()) {
                receivedSymbol = sym;
            }
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

        priceRunnable = new Runnable() {
            @Override
            public void run() {
                fetchStockData(receivedSymbol);
                handler.postDelayed(this, 5000);
            }
        };
        handler.post(priceRunnable);

        chartRunnable = new Runnable() {
            @Override
            public void run() {
                fetchChartData(receivedSymbol);
                handler.postDelayed(this, 60000);
            }
        };
        handler.post(chartRunnable);
    }

    public void fetchStockData(String symbol) {
        StockPrice apiService = ApiClient.createService(StockPrice.class);
        Call<StockPriceModel> call = apiService.getChartData(symbol);

        call.enqueue(new Callback<StockPriceModel>() {
            @Override
            public void onResponse(@NonNull Call<StockPriceModel> call, @NonNull Response<StockPriceModel> response) {
                if (response.isSuccessful() && response.body() != null && isAdded()) {
                    StockPriceModel stockPriceModel = response.body();
                    if (stockPriceModel.chart != null && stockPriceModel.chart.result != null && stockPriceModel.chart.result.length > 0) {
                        StockPriceModel.Meta meta = stockPriceModel.chart.result[0].meta;
                        if (meta != null) {
                            current = StockUtils.safeParseDouble(meta.regularMarketPrice, 0.0);
                            previousClose = StockUtils.safeParseDouble(meta.previousClose != null ? meta.previousClose : meta.chartPreviousClose, current);
                            fiftytwoHigh = StockUtils.safeParseDouble(meta.fiftyTwoWeekHigh, 0.0);
                            fiftytwoLow = StockUtils.safeParseDouble(meta.fiftyTwoWeekLow, 0.0);
                            todayHigh = StockUtils.safeParseDouble(meta.regularMarketDayHigh, current);
                            todayLow = StockUtils.safeParseDouble(meta.regularMarketDayLow, current);
                            volume = meta.regularMarketVolume != null ? meta.regularMarketVolume : "N/A";

                            tv52High.setText(String.format(Locale.getDefault(), "₹%.2f", fiftytwoHigh));
                            tv52Low.setText(String.format(Locale.getDefault(), "₹%.2f", fiftytwoLow));
                            tvTodayHigh.setText(String.format(Locale.getDefault(), "₹%.2f", todayHigh));
                            tvTodayLow.setText(String.format(Locale.getDefault(), "₹%.2f", todayLow));
                            tvVolume.setText(volume);
                            tvPrevClose.setText(String.format(Locale.getDefault(), "₹%.2f", previousClose));

                            updateProgressBars();
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<StockPriceModel> call, @NonNull Throwable t) {
                Log.e("OverviewFragment", "Failed to fetch stock data", t);
            }
        });
    }

    private void updateProgressBars() {
        if (!isAdded()) return;

        double todayDiff = todayHigh - todayLow;
        int progressToday = todayDiff > 0 ? (int) (((current - todayLow) / todayDiff) * 100) : 50;
        progressToday = Math.max(0, Math.min(100, progressToday));

        double f52Diff = fiftytwoHigh - fiftytwoLow;
        int progress52 = f52Diff > 0 ? (int) (((current - fiftytwoLow) / f52Diff) * 100) : 50;
        progress52 = Math.max(0, Math.min(100, progress52));

        final int pToday = progressToday;
        final int p52 = progress52;

        progressBarToday.post(() -> {
            if (!isAdded()) return;
            int width = progressBarToday.getWidth();
            if (width > 0 && indicatorToday != null) {
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) indicatorToday.getLayoutParams();
                params.setMarginStart((int) (((float) pToday / 100f) * width));
                indicatorToday.setLayoutParams(params);
            }
        });

        progressBar52.post(() -> {
            if (!isAdded()) return;
            int width = progressBar52.getWidth();
            if (width > 0 && indicator52 != null) {
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) indicator52.getLayoutParams();
                params.setMarginStart((int) (((float) p52 / 100f) * width));
                indicator52.setLayoutParams(params);
            }
        });
    }

    public void setupChart() {
        lineChart.getDescription().setEnabled(false);
        lineChart.setDrawGridBackground(false);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int index = (int) value;
                if (index >= 0 && index < timestamps.size()) {
                    long millis = timestamps.get(index) * 1000L;
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
                    return sdf.format(new Date(millis));
                }
                return "";
            }
        });

        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setDrawGridLines(true);
        leftAxis.setTextColor(Color.LTGRAY);

        lineChart.getAxisRight().setEnabled(false);
    }

    public void fetchChartData(String symbol) {
        StockPrice apiService = ApiClient.createService(StockPrice.class);
        Call<StockPriceModel> call = apiService.getChartData(symbol);
        call.enqueue(new Callback<StockPriceModel>() {
            @Override
            public void onResponse(@NonNull Call<StockPriceModel> call, @NonNull Response<StockPriceModel> response) {
                if (response.isSuccessful() && response.body() != null && isAdded()) {
                    StockPriceModel stockPriceModel = response.body();
                    if (stockPriceModel.chart != null && stockPriceModel.chart.result != null && stockPriceModel.chart.result.length > 0) {
                        StockPriceModel.Chart.Result res = stockPriceModel.chart.result[0];
                        if (res.timestamp != null && res.indicators != null && res.indicators.quote != null && res.indicators.quote.length > 0) {
                            timestamps.clear();
                            prices.clear();
                            timestamps.addAll(res.timestamp);
                            if (res.indicators.quote[0].close != null) {
                                prices.addAll(res.indicators.quote[0].close);
                            }

                            ArrayList<Entry> values = new ArrayList<>();
                            for (int i = 0; i < Math.min(timestamps.size(), prices.size()); i++) {
                                Float p = prices.get(i);
                                if (p != null) {
                                    values.add(new Entry(i, p));
                                }
                            }

                            if (!values.isEmpty()) {
                                LineDataSet lineDataSet = new LineDataSet(values, "Price");
                                boolean isPositive = (current - previousClose) >= 0;
                                int themeColor = isPositive ? Color.parseColor("#00D09C") : Color.parseColor("#EB5757");

                                lineDataSet.setColor(themeColor);
                                lineDataSet.setLineWidth(2f);
                                lineDataSet.setDrawCircles(false);
                                lineDataSet.setDrawValues(false);
                                lineDataSet.setDrawFilled(true);
                                lineDataSet.setFillAlpha(70);

                                try {
                                    int drawableId = isPositive ? R.drawable.green_gradiant_fill : R.drawable.red_gradiant_fill;
                                    Drawable drawable = ContextCompat.getDrawable(requireContext(), drawableId);
                                    lineDataSet.setFillDrawable(drawable);
                                } catch (Exception ignored) {}

                                LineData lineData = new LineData(lineDataSet);
                                lineChart.setData(lineData);
                                lineChart.invalidate();
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<StockPriceModel> call, @NonNull Throwable t) {
                Log.e("OverviewFragment", "Failed to fetch chart data", t);
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        if (priceRunnable != null) handler.removeCallbacks(priceRunnable);
        if (chartRunnable != null) handler.removeCallbacks(chartRunnable);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (priceRunnable != null) handler.removeCallbacks(priceRunnable);
        if (chartRunnable != null) handler.removeCallbacks(chartRunnable);
    }
}
