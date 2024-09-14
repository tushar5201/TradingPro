package com.example.tradingpro.HomeFragments.marketFragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.tradingpro.Adapter.GainerLosersAdapter;
import com.example.tradingpro.Model.GainerLosersModel;
import com.example.tradingpro.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class TopGainersFragment extends Fragment {

    View view;
    RecyclerView rcylGainers;
    GainerLosersAdapter adapter;
    private ArrayList<GainerLosersModel> dataList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_top_gainers, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rcylGainers = view.findViewById(R.id.rcylGainers);

        rcylGainers.setLayoutManager(new LinearLayoutManager(getContext()));
        dataList = new ArrayList<>();
        adapter = new GainerLosersAdapter(dataList, getContext());
        rcylGainers.setAdapter(adapter);

//        fetchDataFromApi();
    }

    private void fetchDataFromApi() {
        dataList.clear();
        String url = "https://financialmodelingprep.com/api/v3/stock_market/gainers?apikey=cbPCo25kpjzxSisVooydhMfEKvXdbozi";
        RequestQueue queue = Volley.newRequestQueue(getContext());
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            DecimalFormat decim = new DecimalFormat("###.##");
                            // Parse JSON array
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject jsonObject = response.getJSONObject(i);
                                String symbol = jsonObject.getString("symbol");
                                String symbolName = jsonObject.getString("name");
                                String change = jsonObject.getString("change");
                                String changesPercentage = jsonObject.getString("changesPercentage");
                                String price = jsonObject.getString("price");

                                dataList.add(new GainerLosersModel(symbol, symbolName, change, changesPercentage, price));
                            }

                            adapter.notifyDataSetChanged();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Handle error
            }
        });

        queue.add(jsonArrayRequest);

    }
}