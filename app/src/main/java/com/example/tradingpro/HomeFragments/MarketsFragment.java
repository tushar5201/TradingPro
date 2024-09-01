package com.example.tradingpro.HomeFragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tradingpro.Adapter.IndicesAdapter;
import com.example.tradingpro.Interfaces.IndicesApi;
import com.example.tradingpro.Model.IndicesModel;
import com.example.tradingpro.Model.IndicesResponseModel;
import com.example.tradingpro.R;

import java.text.DecimalFormat;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MarketsFragment extends Fragment {
    View view;
    RecyclerView recyclerView;
    IndicesAdapter adapter;
    String symbol, symbolFullName, stockPrice, stockPlusMinusPoints, stockPlusMinusPercentage, previousClose;
    private ArrayList<IndicesModel> dataList;
    private Handler handler = new Handler();
    private Runnable runnable;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_markets, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.rcylIndices);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        dataList = new ArrayList<>(5);
        adapter = new IndicesAdapter(getContext(), dataList);
        recyclerView.setAdapter(adapter);

        runnable = new Runnable() {
            @Override
            public void run() {
                // Call the method to fetch stock prices
                fetchStockPrices(0, "^NSEI", "https://s3-symbol-logo.tradingview.com/indices/nifty-50--600.png");
                fetchStockPrices(1, "^NSEBANK", "https://s3-symbol-logo.tradingview.com/sector/financial--600.png");
                fetchStockPrices(2, "^BSESN", "https://s3-symbol-logo.tradingview.com/indices/bse-sensex--600.png");
                fetchStockPrices(3, "NIFTY_FIN_SERVICE.NS", "https://s3-symbol-logo.tradingview.com/indices/finnifty--600.png");
                fetchStockPrices(4, "^INDIAVIX", "https://s3-symbol-logo.tradingview.com/indices/india-vix--600.png");
                handler.postDelayed(this, 10000);
            }
        };
        handler.post(runnable);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private void fetchStockPrices(int pos,String stockSymbol, String symbolIcon) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://query1.finance.yahoo.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        IndicesApi api = retrofit.create(IndicesApi.class);

        api.getIndicesPrice(stockSymbol).enqueue(new Callback<IndicesResponseModel>() {
            @Override
            public void onResponse(Call<IndicesResponseModel> call, retrofit2.Response<IndicesResponseModel> response) {
                try {
                    if (response.isSuccessful() && response.body() != null) {
                        DecimalFormat decim = new DecimalFormat("###.##");
                        IndicesResponseModel stockResponse = response.body();
                        previousClose = stockResponse.chart.result[0].meta.previousClose;
                        stockPrice = stockResponse.chart.result[0].meta.regularMarketPrice;
                        symbol = stockResponse.chart.result[0].meta.symbol;
                        symbolFullName = stockResponse.chart.result[0].meta.symbolFullname;
                        stockPlusMinusPoints = decim.format(Double.parseDouble(stockPrice) - Double.parseDouble(previousClose));
                        stockPlusMinusPercentage = "  (" + decim.format(((Double.parseDouble(stockPlusMinusPoints) * 100) / Double.parseDouble(stockPrice))) + "%)";
                        if (Double.parseDouble(stockPlusMinusPoints) > 0) {
                            stockPlusMinusPoints = "+" + stockPlusMinusPoints;
                            stockPlusMinusPercentage = " (+" + decim.format(((Double.parseDouble(stockPlusMinusPoints) * 100) / Double.parseDouble(stockPrice))) + "%)";
                        }
                        ensureListSize(dataList, 5);

                            dataList.set(pos, new IndicesModel(symbol, symbolFullName, stockPrice, stockPlusMinusPoints, stockPlusMinusPercentage, symbolIcon));
                        adapter.notifyDataSetChanged();
                    } else {
                        Log.d("problem", "some Problem");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<IndicesResponseModel> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void ensureListSize(ArrayList<IndicesModel> list, int size) {
        while (list.size() < size) {
            list.add(new IndicesModel("", "", "0", "0", "0", ""));
        }
    }
}