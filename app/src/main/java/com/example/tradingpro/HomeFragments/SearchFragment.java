package com.example.tradingpro.HomeFragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tradingpro.Adapter.SearchAdapter;
import com.example.tradingpro.Interfaces.YahooFinanceApi;
import com.example.tradingpro.Model.SearchModel;
import com.example.tradingpro.Model.YahooSearchResponse;
import com.example.tradingpro.Network.ApiClient;
import com.example.tradingpro.R;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchFragment extends Fragment {

    private TextInputEditText textInputEdSearch;
    private RecyclerView recyclerView;
    private SearchAdapter adapter;
    private final ArrayList<SearchModel> dataList = new ArrayList<>();
    private ProgressBar searchPbar;
    private final Handler searchHandler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        textInputEdSearch = view.findViewById(R.id.textInputEdSearch);
        recyclerView = view.findViewById(R.id.recycleSearch);
        searchPbar = view.findViewById(R.id.searchPbar);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new SearchAdapter(dataList, getContext());
        recyclerView.setAdapter(adapter);

        showPopularStocks();

        textInputEdSearch.requestFocus();
        InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(textInputEdSearch, InputMethodManager.SHOW_IMPLICIT);
        }

        textInputEdSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (searchRunnable != null) {
                    searchHandler.removeCallbacks(searchRunnable);
                }

                String query = charSequence != null ? charSequence.toString().trim() : "";
                if (query.isEmpty()) {
                    showPopularStocks();
                    searchPbar.setVisibility(View.GONE);
                } else {
                    searchPbar.setVisibility(View.VISIBLE);
                    searchRunnable = () -> searchStocks(query);
                    searchHandler.postDelayed(searchRunnable, 350); // 350ms debounce
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
    }

    private void showPopularStocks() {
        dataList.clear();
        dataList.add(new SearchModel("RELIANCE.NS", "Reliance Industries Limited • NSE"));
        dataList.add(new SearchModel("TCS.NS", "Tata Consultancy Services • NSE"));
        dataList.add(new SearchModel("INFY.NS", "Infosys Limited • NSE"));
        dataList.add(new SearchModel("HDFCBANK.NS", "HDFC Bank Limited • NSE"));
        dataList.add(new SearchModel("ICICIBANK.NS", "ICICI Bank Limited • NSE"));
        dataList.add(new SearchModel("SBIN.NS", "State Bank of India • NSE"));
        dataList.add(new SearchModel("BHARTIARTL.NS", "Bharti Airtel Limited • NSE"));
        dataList.add(new SearchModel("AAPL", "Apple Inc. • NASDAQ"));
        dataList.add(new SearchModel("NVDA", "NVIDIA Corporation • NASDAQ"));
        dataList.add(new SearchModel("TSLA", "Tesla, Inc. • NASDAQ"));
        adapter.notifyDataSetChanged();
    }

    private void searchStocks(String query) {
        YahooFinanceApi api = ApiClient.createService(YahooFinanceApi.class);
        api.searchStocks(query, 20, 0).enqueue(new Callback<YahooSearchResponse>() {
            @Override
            public void onResponse(@NonNull Call<YahooSearchResponse> call, @NonNull Response<YahooSearchResponse> response) {
                if (!isAdded()) return;
                searchPbar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null && response.body().quotes != null) {
                    dataList.clear();
                    List<YahooSearchResponse.SearchQuote> quotes = response.body().quotes;
                    for (YahooSearchResponse.SearchQuote q : quotes) {
                        if (q.symbol == null) continue;
                        String exch = q.exchDisp != null ? q.exchDisp : (q.exchange != null ? q.exchange : "");
                        String name = q.getDisplayName();
                        String desc = exch.isEmpty() ? name : String.format("%s • %s", name, exch);
                        dataList.add(new SearchModel(q.symbol, desc));
                    }
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(@NonNull Call<YahooSearchResponse> call, @NonNull Throwable t) {
                if (!isAdded()) return;
                searchPbar.setVisibility(View.GONE);
                Log.e("SearchFragment", "Search failed", t);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (searchRunnable != null) {
            searchHandler.removeCallbacks(searchRunnable);
        }
    }
}
