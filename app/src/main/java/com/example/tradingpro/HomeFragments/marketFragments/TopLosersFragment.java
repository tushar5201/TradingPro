package com.example.tradingpro.HomeFragments.marketFragments;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tradingpro.Adapter.GainerLosersAdapter;
import com.example.tradingpro.Constant.StockUtils;
import com.example.tradingpro.Interfaces.StockPrice;
import com.example.tradingpro.Interfaces.YahooFinanceApi;
import com.example.tradingpro.Model.GainerLosersModel;
import com.example.tradingpro.Model.ScreenerResponseModel;
import com.example.tradingpro.Model.StockPriceModel;
import com.example.tradingpro.Network.ApiClient;
import com.example.tradingpro.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TopLosersFragment extends Fragment {

    private RecyclerView rcylLosers;
    private GainerLosersAdapter adapter;
    private final ArrayList<GainerLosersModel> dataList = new ArrayList<>();
    private ProgressBar pbLosers;
    private TextView tvLosersEmpty, chipLosersNse, chipLosersGlobal;

    private boolean isNseSelected = true;
    private final Handler handler = new Handler();
    private Runnable liveRunnable;

    private static final String[] TOP_NSE_SYMBOLS = {
            "RELIANCE.NS", "TCS.NS", "INFY.NS", "HDFCBANK.NS", "ICICIBANK.NS",
            "BHARTIARTL.NS", "SBIN.NS", "ITC.NS", "LT.NS", "TATASTEEL.NS",
            "HINDUNILVR.NS", "KOTAKBANK.NS", "AXISBANK.NS", "BAJFINANCE.NS",
            "MARUTI.NS", "SUNPHARMA.NS", "TITAN.NS", "ULTRACEMCO.NS", "NTPC.NS",
            "ONGC.NS", "POWERGRID.NS", "ADANIENT.NS", "WIPRO.NS"
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_top_losers, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rcylLosers = view.findViewById(R.id.rcylLosers);
        pbLosers = view.findViewById(R.id.pbLosers);
        tvLosersEmpty = view.findViewById(R.id.tvLosersEmpty);
        chipLosersNse = view.findViewById(R.id.chipLosersNse);
        chipLosersGlobal = view.findViewById(R.id.chipLosersGlobal);

        rcylLosers.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new GainerLosersAdapter(dataList, getContext());
        rcylLosers.setAdapter(adapter);

        chipLosersNse.setOnClickListener(v -> {
            if (!isNseSelected) {
                isNseSelected = true;
                updateChipUI();
                fetchLosers(true);
            }
        });

        chipLosersGlobal.setOnClickListener(v -> {
            if (isNseSelected) {
                isNseSelected = false;
                updateChipUI();
                fetchLosers(true);
            }
        });

        fetchLosers(true);

        liveRunnable = new Runnable() {
            @Override
            public void run() {
                fetchLosers(false);
                handler.postDelayed(this, 10000); // 10s real-time poll
            }
        };
        handler.postDelayed(liveRunnable, 10000);
    }

    private void updateChipUI() {
        if (!isAdded()) return;
        if (isNseSelected) {
            chipLosersNse.setBackgroundResource(R.drawable.cradius);
            chipLosersNse.setBackgroundTintList(requireContext().getColorStateList(R.color.yellow));
            chipLosersNse.setTextColor(Color.BLACK);

            chipLosersGlobal.setBackgroundResource(R.drawable.ed_style);
            chipLosersGlobal.setBackgroundTintList(null);
            chipLosersGlobal.setTextColor(requireContext().getColor(R.color.desc_text));
        } else {
            chipLosersGlobal.setBackgroundResource(R.drawable.cradius);
            chipLosersGlobal.setBackgroundTintList(requireContext().getColorStateList(R.color.yellow));
            chipLosersGlobal.setTextColor(Color.BLACK);

            chipLosersNse.setBackgroundResource(R.drawable.ed_style);
            chipLosersNse.setBackgroundTintList(null);
            chipLosersNse.setTextColor(requireContext().getColor(R.color.desc_text));
        }
    }

    private void fetchLosers(boolean showLoading) {
        if (showLoading && pbLosers != null && dataList.isEmpty()) {
            pbLosers.setVisibility(View.VISIBLE);
        }

        if (isNseSelected) {
            fetchNseLosers();
        } else {
            fetchGlobalLosers();
        }
    }

    private void fetchGlobalLosers() {
        YahooFinanceApi api = ApiClient.createService(YahooFinanceApi.class);
        api.getPredefinedScreener(false, "day_losers", 25).enqueue(new Callback<ScreenerResponseModel>() {
            @Override
            public void onResponse(@NonNull Call<ScreenerResponseModel> call, @NonNull Response<ScreenerResponseModel> response) {
                if (!isAdded()) return;
                if (pbLosers != null) pbLosers.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null && response.body().finance != null) {
                    List<ScreenerResponseModel.Result> results = response.body().finance.result;
                    if (results != null && !results.isEmpty() && results.get(0).quotes != null) {
                        dataList.clear();
                        for (ScreenerResponseModel.Quote q : results.get(0).quotes) {
                            if (q.symbol == null) continue;
                            double price = q.regularMarketPrice != null ? q.regularMarketPrice : 0.0;
                            double change = q.regularMarketChange != null ? q.regularMarketChange : 0.0;
                            double pct = q.regularMarketChangePercent != null ? q.regularMarketChangePercent : 0.0;
                            String name = q.shortName != null ? q.shortName : (q.longName != null ? q.longName : q.symbol);
                            String exch = q.fullExchangeName != null ? q.fullExchangeName : "Global";

                            String priceStr = String.format(Locale.getDefault(), "$%.2f", price);
                            String changeStr = StockUtils.formatPoints(change);
                            String pctStr = StockUtils.formatPercentage(pct);

                            dataList.add(new GainerLosersModel(q.symbol, name, priceStr, changeStr, pctStr, exch));
                        }
                        adapter.notifyDataSetChanged();
                        if (tvLosersEmpty != null) {
                            tvLosersEmpty.setVisibility(dataList.isEmpty() ? View.VISIBLE : View.GONE);
                        }
                        return;
                    }
                }
                fetchNseLosers();
            }

            @Override
            public void onFailure(@NonNull Call<ScreenerResponseModel> call, @NonNull Throwable t) {
                Log.e("TopLosers", "Global screener failed, fallback to NSE", t);
                if (isAdded()) {
                    fetchNseLosers();
                }
            }
        });
    }

    private void fetchNseLosers() {
        final List<LoserCandidate> candidates = Collections.synchronizedList(new ArrayList<>());
        final int total = TOP_NSE_SYMBOLS.length;
        final int[] completed = {0};

        StockPrice api = ApiClient.createService(StockPrice.class);

        for (String sym : TOP_NSE_SYMBOLS) {
            api.getChartData(sym).enqueue(new Callback<StockPriceModel>() {
                @Override
                public void onResponse(@NonNull Call<StockPriceModel> call, @NonNull Response<StockPriceModel> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        StockPriceModel model = response.body();
                        if (model.chart != null && model.chart.result != null && model.chart.result.length > 0) {
                            StockPriceModel.Meta meta = model.chart.result[0].meta;
                            if (meta != null) {
                                double p = StockUtils.safeParseDouble(meta.regularMarketPrice, 0.0);
                                double prev = StockUtils.safeParseDouble(meta.previousClose != null ? meta.previousClose : meta.chartPreviousClose, p);
                                if (p > 0 && prev > 0) {
                                    double diff = p - prev;
                                    double pct = (diff / prev) * 100.0;
                                    String name = meta.shortName != null ? meta.shortName : (meta.longName != null ? meta.longName : sym);
                                    candidates.add(new LoserCandidate(sym, name, p, diff, pct));
                                }
                            }
                        }
                    }
                    checkCompletion();
                }

                @Override
                public void onFailure(@NonNull Call<StockPriceModel> call, @NonNull Throwable t) {
                    checkCompletion();
                }

                private void checkCompletion() {
                    completed[0]++;
                    if (completed[0] >= total && isAdded()) {
                        if (pbLosers != null) pbLosers.setVisibility(View.GONE);

                        // Sort candidates ascending by percentage (most negative first)
                        Collections.sort(candidates, (o1, o2) -> Double.compare(o1.pct, o2.pct));

                        dataList.clear();
                        for (LoserCandidate c : candidates) {
                            if (c.diff <= 0) { // Losers only
                                String priceStr = String.format(Locale.getDefault(), "₹%.2f", c.price);
                                String changeStr = StockUtils.formatPoints(c.diff);
                                String pctStr = StockUtils.formatPercentage(c.pct);
                                dataList.add(new GainerLosersModel(c.symbol, c.name, priceStr, changeStr, pctStr, "NSE"));
                            }
                        }
                        // If none were negative, show top 5 lowest
                        if (dataList.isEmpty() && !candidates.isEmpty()) {
                            for (int i = 0; i < Math.min(5, candidates.size()); i++) {
                                LoserCandidate c = candidates.get(i);
                                String priceStr = String.format(Locale.getDefault(), "₹%.2f", c.price);
                                String changeStr = StockUtils.formatPoints(c.diff);
                                String pctStr = StockUtils.formatPercentage(c.pct);
                                dataList.add(new GainerLosersModel(c.symbol, c.name, priceStr, changeStr, pctStr, "NSE"));
                            }
                        }

                        adapter.notifyDataSetChanged();
                        if (tvLosersEmpty != null) {
                            tvLosersEmpty.setVisibility(dataList.isEmpty() ? View.VISIBLE : View.GONE);
                        }
                    }
                }
            });
        }
    }

    private static class LoserCandidate {
        String symbol, name;
        double price, diff, pct;
        LoserCandidate(String symbol, String name, double price, double diff, double pct) {
            this.symbol = symbol;
            this.name = name;
            this.price = price;
            this.diff = diff;
            this.pct = pct;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (handler != null && liveRunnable != null) {
            handler.removeCallbacks(liveRunnable);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (handler != null && liveRunnable != null) {
            handler.removeCallbacks(liveRunnable);
        }
    }
}
