package com.example.tradingpro.OverviewTabFragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.tradingpro.Constant.StockUtils;
import com.example.tradingpro.Interfaces.StockPrice;
import com.example.tradingpro.Model.StockPriceModel;
import com.example.tradingpro.Network.ApiClient;
import com.example.tradingpro.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CompanyProfileFragment extends Fragment {

    private View view;
    private TextView tvMarketcap, tvExchange, tvDebt2Eq, tvTotalDebt, tvTotalRevenue, tvPE, tvPB, tvBookVal, tvWebsite, tvDesc;
    private String receivedSymbol = "RELIANCE.NS";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_company_profile, container, false);
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

        tvExchange = view.findViewById(R.id.tvExchange);
        tvMarketcap = view.findViewById(R.id.tvMarketcap);
        tvTotalDebt = view.findViewById(R.id.tvTotalDebt);
        tvTotalRevenue = view.findViewById(R.id.tvTotalRevenue);
        tvPB = view.findViewById(R.id.tvPB);
        tvPE = view.findViewById(R.id.tvPE);
        tvDebt2Eq = view.findViewById(R.id.tvDebt2Eq);
        tvBookVal = view.findViewById(R.id.tvBookVal);
        tvWebsite = view.findViewById(R.id.tvWebsite);
        tvDesc = view.findViewById(R.id.tvDesc);

        populateDefaultsFromChart();
        fetchDetailsSafely();
    }

    private void populateDefaultsFromChart() {
        String cleanSym = StockUtils.cleanDisplaySymbol(receivedSymbol);
        tvExchange.setText(receivedSymbol.endsWith(".NS") ? "NSE" : (receivedSymbol.endsWith(".BO") ? "BSE" : "NASDAQ"));
        tvWebsite.setText(String.format("https://www.google.com/finance/quote/%s", receivedSymbol));
        tvDesc.setText(String.format(Locale.getDefault(), "%s is a publicly traded company on the %s exchange.", cleanSym, tvExchange.getText().toString()));

        // Also fetch live data from our reliable ApiClient
        StockPrice api = ApiClient.createService(StockPrice.class);
        api.getChartData(receivedSymbol).enqueue(new Callback<StockPriceModel>() {
            @Override
            public void onResponse(@NonNull Call<StockPriceModel> call, @NonNull Response<StockPriceModel> response) {
                if (response.isSuccessful() && response.body() != null && isAdded()) {
                    StockPriceModel model = response.body();
                    if (model.chart != null && model.chart.result != null && model.chart.result.length > 0) {
                        StockPriceModel.Meta meta = model.chart.result[0].meta;
                        if (meta != null) {
                            if (meta.fullExchangeName != null) tvExchange.setText(meta.fullExchangeName);
                            if (meta.longName != null) {
                                tvDesc.setText(String.format(Locale.getDefault(), "%s (%s) is listed on %s. Real-time market metrics are dynamically tracked.",
                                        meta.longName, cleanSym, tvExchange.getText().toString()));
                            }
                            if (meta.regularMarketVolume != null) {
                                tvTotalRevenue.setText(meta.regularMarketVolume + " shares");
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<StockPriceModel> call, @NonNull Throwable t) {
                Log.e("CompanyProfile", "Failed to get chart meta", t);
            }
        });
    }

    private void fetchDetailsSafely() {
        if (getContext() == null) return;
        String url = "https://apidojo-yahoo-finance-v1.p.rapidapi.com/stock/v2/get-analysis?symbol=" + receivedSymbol + "&region=IN";
        RequestQueue queue = Volley.newRequestQueue(requireContext());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, url, null,
                response -> {
                    try {
                        if (response.has("price")) {
                            JSONObject price = response.getJSONObject("price");
                            if (price.has("exchangeName")) tvExchange.setText(price.getString("exchangeName"));
                            if (price.has("marketCap") && price.getJSONObject("marketCap").has("fmt")) {
                                tvMarketcap.setText(price.getJSONObject("marketCap").getString("fmt"));
                            }
                        }
                        if (response.has("financialData")) {
                            JSONObject financialData = response.getJSONObject("financialData");
                            if (financialData.has("debtToEquity") && financialData.getJSONObject("debtToEquity").has("fmt")) {
                                tvDebt2Eq.setText(financialData.getJSONObject("debtToEquity").getString("fmt"));
                            }
                            if (financialData.has("totalDebt") && financialData.getJSONObject("totalDebt").has("fmt")) {
                                tvTotalDebt.setText(financialData.getJSONObject("totalDebt").getString("fmt"));
                            }
                            if (financialData.has("totalRevenue") && financialData.getJSONObject("totalRevenue").has("fmt")) {
                                tvTotalRevenue.setText(financialData.getJSONObject("totalRevenue").getString("fmt"));
                            }
                        }
                        if (response.has("summaryDetail") && response.getJSONObject("summaryDetail").has("trailingPE")) {
                            JSONObject trailingPE = response.getJSONObject("summaryDetail").getJSONObject("trailingPE");
                            if (trailingPE.has("fmt")) tvPE.setText(trailingPE.getString("fmt"));
                        }
                    } catch (Exception e) {
                        Log.e("CompanyProfile", "Parsing error handled safely", e);
                    }
                },
                error -> Log.e("CompanyProfile", "Network error handled safely: " + error.getMessage())
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("x-rapidapi-key", "c92e06d5c6mshfce3e7cdfaad84ap1f19dfjsn2f60255af180");
                return headers;
            }
        };

        queue.add(jsonObjectRequest);
    }
}
