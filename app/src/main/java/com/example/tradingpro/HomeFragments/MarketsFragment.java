package com.example.tradingpro.HomeFragments;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.example.tradingpro.Adapter.IndicesAdapter;
import com.example.tradingpro.Adapter.MarketsTabAdapter;
import com.example.tradingpro.Constant.StockUtils;
import com.example.tradingpro.HomeFragments.marketFragments.TopGainersFragment;
import com.example.tradingpro.HomeFragments.marketFragments.TopLosersFragment;
import com.example.tradingpro.Interfaces.IndicesApi;
import com.example.tradingpro.Model.IndicesModel;
import com.example.tradingpro.Model.IndicesResponseModel;
import com.example.tradingpro.Network.ApiClient;
import com.example.tradingpro.R;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MarketsFragment extends Fragment {

    private RecyclerView recyclerView;
    private IndicesAdapter adapter;
    private ProgressBar marketsPbar;
    private final ArrayList<IndicesModel> dataList = new ArrayList<>(4);
    private final Handler handler = new Handler();
    private Runnable runnable;
    private TabLayout tabMarkets;
    private ViewPager viewpagerMarkets;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_markets, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.rcylIndices);
        marketsPbar = view.findViewById(R.id.marketsPbar);
        if (marketsPbar != null) marketsPbar.setVisibility(View.VISIBLE);
        tabMarkets = view.findViewById(R.id.tabMarkets);
        viewpagerMarkets = view.findViewById(R.id.viewpagerMarkets);

        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        ensureListSize(dataList, 4);
        adapter = new IndicesAdapter(getContext(), dataList);
        recyclerView.setAdapter(adapter);

        runnable = new Runnable() {
            @Override
            public void run() {
                fetchStockPrices(0, "^NSEI", "https://s3-symbol-logo.tradingview.com/indices/nifty-50--600.png", "NIFTY 50");
                fetchStockPrices(1, "^NSEBANK", "https://s3-symbol-logo.tradingview.com/sector/financial--600.png", "BANK NIFTY");
                fetchStockPrices(2, "^BSESN", "https://s3-symbol-logo.tradingview.com/indices/bse-sensex--600.png", "SENSEX");
                fetchStockPrices(3, "^INDIAVIX", "https://s3-symbol-logo.tradingview.com/indices/india-vix--600.png", "INDIA VIX");
                handler.postDelayed(this, 5000);
            }
        };
        handler.post(runnable);

        TopGainersFragment topGainersFragment = new TopGainersFragment();
        TopLosersFragment topLosersFragment = new TopLosersFragment();

        if (getActivity() != null) {
            MarketsTabAdapter tabAdapter = new MarketsTabAdapter(getChildFragmentManager());
            tabAdapter.addFragment(topGainersFragment);
            tabAdapter.addFragment(topLosersFragment);
            viewpagerMarkets.setAdapter(tabAdapter);
            tabMarkets.setupWithViewPager(viewpagerMarkets);
        }
    }

    private void fetchStockPrices(int pos, String stockSymbol, String symbolIcon, String fallbackName) {
        IndicesApi api = ApiClient.createService(IndicesApi.class);

        api.getIndicesPrice(stockSymbol).enqueue(new Callback<IndicesResponseModel>() {
            @Override
            public void onResponse(@NonNull Call<IndicesResponseModel> call, @NonNull Response<IndicesResponseModel> response) {
                if (!isAdded()) return;
                try {
                    if (response.isSuccessful() && response.body() != null) {
                        IndicesResponseModel stockResponse = response.body();
                        if (stockResponse.chart != null && stockResponse.chart.result != null && stockResponse.chart.result.length > 0) {
                            IndicesResponseModel.Meta meta = stockResponse.chart.result[0].meta;
                            if (meta != null) {
                                String previousClose = meta.getPrevClose();
                                String stockPrice = meta.regularMarketPrice != null ? meta.regularMarketPrice : "0";
                                String symbol = meta.symbol != null ? meta.symbol : stockSymbol;
                                String symbolFullName = meta.getFullName();
                                if (symbolFullName == null || symbolFullName.isEmpty() || symbolFullName.equals(symbol)) {
                                    symbolFullName = fallbackName;
                                }

                                double pNum = StockUtils.safeParseDouble(stockPrice, 0.0);
                                double prevNum = StockUtils.safeParseDouble(previousClose, pNum);
                                double diff = pNum - prevNum;
                                double pct = prevNum > 0 ? (diff / prevNum) * 100.0 : 0.0;

                                String pointsStr = StockUtils.formatPoints(diff);
                                String pctStr = "  " + StockUtils.formatPercentage(pct);
                                String priceStr = StockUtils.formatPrice(pNum);

                                ensureListSize(dataList, 4);
                                dataList.set(pos, new IndicesModel(symbol, symbolFullName, priceStr, pointsStr, pctStr, symbolIcon));
                                adapter.notifyItemChanged(pos);

                                if (marketsPbar != null) marketsPbar.setVisibility(View.GONE);
                                return;
                            }
                        }
                    }
                    if (marketsPbar != null) marketsPbar.setVisibility(View.GONE);
                } catch (Exception e) {
                    Log.e("MarketsFragment", "Parsing error in indices", e);
                    if (marketsPbar != null) marketsPbar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<IndicesResponseModel> call, @NonNull Throwable t) {
                if (marketsPbar != null) marketsPbar.setVisibility(View.GONE);
                Log.e("MarketsFragment", "Indices fetch failed for " + stockSymbol, t);
            }
        });
    }

    private void ensureListSize(ArrayList<IndicesModel> list, int size) {
        while (list.size() < size) {
            list.add(new IndicesModel("", "", "0.00", "0.00", "(0.00%)", ""));
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (handler != null && runnable != null) {
            handler.removeCallbacks(runnable);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (handler != null && runnable != null) {
            handler.removeCallbacks(runnable);
        }
    }
}
