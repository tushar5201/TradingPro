package com.example.tradingpro.OverviewTabFragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.tradingpro.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

//import de.hdodenhof.circleimageview.CircleImageView;
import de.hdodenhof.circleimageview.CircleImageView;
//import io.finnhub.api.apis.DefaultApi;
//import io.finnhub.api.infrastructure.ApiClient;
//import io.finnhub.api.models.CompanyProfile;
//import io.finnhub.api.models.CompanyProfile2;
//import kotlinx.serialization.json.Json;

public class CompanyProfileFragment extends Fragment {

    View view;
    TextView tvExchange, tvSector, tvIndustry, tvMarketcap, tvTotalDebt, tvTotalRevenue, tvPE, tvPB, tvDebt2Eq, tvBookVal, tvWebsite, tvDesc;
    String receivedSymbol;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_company_profile, container, false);
        if (getArguments() != null) {
            receivedSymbol = getArguments().getString("symbol");
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvExchange = view.findViewById(R.id.tvExchange);
        tvSector = view.findViewById(R.id.tvSector);
        tvIndustry = view.findViewById(R.id.tvIndustry);
        tvMarketcap = view.findViewById(R.id.tvMarketcap);
        tvTotalDebt = view.findViewById(R.id.tvTotalDebt);
        tvTotalRevenue = view.findViewById(R.id.tvTotalRevenue);
        tvPB = view.findViewById(R.id.tvPB);
        tvPE = view.findViewById(R.id.tvPE);
        tvDebt2Eq = view.findViewById(R.id.tvDebt2Eq);
        tvBookVal = view.findViewById(R.id.tvBookVal);
        tvWebsite = view.findViewById(R.id.tvWebsite);
        tvDesc = view.findViewById(R.id.tvDesc);

        getData1();
        getData2();

    }


    public void getData1() {
//        String url = "https://yh-finance.p.rapidapi.com/stock/v3get-analysis?symbol=" + receivedSymbol + "&region=IN";
        String url = "https://apidojo-yahoo-finance-v1.p.rapidapi.com/stock/v2/get-analysis?symbol=" + receivedSymbol + "&region=IN";

        // Create a RequestQueue
        RequestQueue queue = Volley.newRequestQueue(getContext());

        // Create a JsonObjectRequest to fetch the data
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject price = response.getJSONObject("price");
                            String exchangeName = price.getString("exchangeName");
                            JSONObject marketCap = price.getJSONObject("marketCap");
                            String mktCap = marketCap.getString("fmt");
                            JSONObject financialData = response.getJSONObject("financialData");
                            JSONObject debtToEquity = financialData.getJSONObject("debtToEquity");
                            String debt2Eq = debtToEquity.getString("fmt");
                            JSONObject debt = financialData.getJSONObject("totalDebt");
                            String totalDebt = debt.getString("fmt");
                            JSONObject summaryDetail = response.getJSONObject("summaryDetail");
                            JSONObject trailingPE = summaryDetail.getJSONObject("trailingPE");
                            String PE = trailingPE.getString("fmt");
                            JSONObject revenue = financialData.getJSONObject("totalRevenue");
                            String totalRevenue = revenue.getString("fmt");

                            tvExchange.setText(exchangeName);
                            tvMarketcap.setText(mktCap);
                            tvDebt2Eq.setText(debt2Eq);
                            tvTotalDebt.setText(totalDebt);
                            tvTotalRevenue.setText(totalRevenue);
                            tvPE.setText(PE);
                        } catch (JSONException e) {
                            getData1();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle any errors
                        Log.e("API Error", error.toString());
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                // Add the API key in the headers
                Map<String, String> headers = new HashMap<>();
                headers.put("x-rapidapi-key", "c92e06d5c6mshfce3e7cdfaad84ap1f19dfjsn2f60255af180");  // Your API key
                return headers;
            }
        };

        // Add the request to the RequestQueue to make the API call
        queue.add(jsonObjectRequest);
    }


    public void getData2() {
        String url = "https://apidojo-yahoo-finance-v1.p.rapidapi.com/stock/get-fundamentals?region=IN&symbol=" + receivedSymbol + "&lang=en-US&modules=assetProfile%2CdefaultKeyStatistics%2CfinancialData";

        // Create a RequestQueue
        RequestQueue queue = Volley.newRequestQueue(getContext());

        // Create a JsonObjectRequest to fetch the data
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject quoteSummary = response.getJSONObject("quoteSummary");
                            JSONArray result = quoteSummary.getJSONArray("result");
                            JSONObject zero = result.getJSONObject(0);
                            JSONObject assetProfile = zero.getJSONObject("assetProfile");
                            String industry = assetProfile.getString("industry");
                            String sector = assetProfile.getString("sector");
                            String details = assetProfile.getString("longBusinessSummary");
                            String website = assetProfile.getString("website");

                            JSONObject defaultKeyStatistics = zero.getJSONObject("defaultKeyStatistics");
                            JSONObject bookVal = defaultKeyStatistics.getJSONObject("bookValue");
                            String bookValue = bookVal.getString("fmt");
                            JSONObject price2Book = defaultKeyStatistics.getJSONObject("priceToBook");
                            String PB = price2Book.getString("fmt");

                            tvIndustry.setText(industry);
                            tvSector.setText(sector);
                            tvDesc.setText(details);
                            tvWebsite.setText(website);
                            tvBookVal.setText(bookValue);
                            tvPB.setText(PB);


                            tvWebsite.setOnClickListener(v-> {
                                Intent i1 = new Intent(Intent.ACTION_VIEW, Uri.parse(website));
                                startActivity(i1);
                            });

                        } catch (JSONException e) {
                            getData2();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle any errors
                        Log.e("API Error", error.toString());
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                // Add the API key in the headers
                Map<String, String> headers = new HashMap<>();
                headers.put("x-rapidapi-key", "c92e06d5c6mshfce3e7cdfaad84ap1f19dfjsn2f60255af180");  // Your API key
                return headers;
            }
        };

        // Add the request to the RequestQueue to make the API call
        queue.add(jsonObjectRequest);
    }
}