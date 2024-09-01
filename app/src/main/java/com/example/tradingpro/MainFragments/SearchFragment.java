package com.example.tradingpro.MainFragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.tradingpro.R;
import com.example.tradingpro.Adapter.SearchAdapter;
import com.example.tradingpro.Model.SearchModel;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class SearchFragment extends Fragment {
    View view;
    TextInputEditText textInputEdSearch;
    RecyclerView recyclerView;
    SearchAdapter adapter;
    TextView tvHello;
    private ArrayList<SearchModel> dataList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_search, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        textInputEdSearch = view.findViewById(R.id.textInputEdSearch);
        recyclerView = view.findViewById(R.id.recycleSearch);
//        tvHello = view.findViewById(R.id.tvHello);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        dataList = new ArrayList<>();
        adapter = new SearchAdapter(dataList, getContext());
        recyclerView.setAdapter(adapter);


        textInputEdSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                fetchDataFromApi(charSequence);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                fetchDataFromApi(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }


    public void fetchDataFromApi(CharSequence ticker) {
        dataList.clear();
        String url = "https://financialmodelingprep.com/api/v3/search?query=" + ticker + "&apikey=cbPCo25kpjzxSisVooydhMfEKvXdbozi&exchange=NSE&limit=10";
        RequestQueue queue = Volley.newRequestQueue(getContext());
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            // Parse JSON array
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject jsonObject = response.getJSONObject(i);
                                String title = jsonObject.getString("symbol");
                                String description = jsonObject.getString("name");

                                dataList.add(new SearchModel(title, description));
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