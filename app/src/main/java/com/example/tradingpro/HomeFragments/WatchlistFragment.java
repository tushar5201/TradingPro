package com.example.tradingpro.HomeFragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.tradingpro.Adapter.SearchAdapter;
import com.example.tradingpro.Adapter.SwipeToDelete;
import com.example.tradingpro.Adapter.WatchlistAdapter;
import com.example.tradingpro.Constant.Constant_user_info;
import com.example.tradingpro.Interfaces.IndicesApi;
import com.example.tradingpro.Model.IndicesModel;
import com.example.tradingpro.Model.IndicesResponseModel;
import com.example.tradingpro.Model.WatchlistModel;
import com.example.tradingpro.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WatchlistFragment extends Fragment {

    View view;
    RecyclerView recycleWatchlist;
    WatchlistAdapter adapter;
    ArrayList<WatchlistModel> list = new ArrayList<>();
    String stockPlusMinusPercentage, stockPlusMinusPoints, symbol, symbolName, stockPrice, previousClose;
    ArrayList<WatchlistModel> dataList = new ArrayList<>();
    private Handler handler = new Handler();
    private Runnable runnable;
    ArrayList<String> arrayListTemp = new ArrayList<>();
    ProgressBar watchlistPbar;
    RelativeLayout watchlistRelative;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_watchlist, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recycleWatchlist = view.findViewById(R.id.recycleWatchlist);
        watchlistPbar = view.findViewById(R.id.watchlistPbar);
        watchlistPbar.setVisibility(View.VISIBLE);
        watchlistRelative = view.findViewById(R.id.watchlistRelative);

//        shared preferences name
        SharedPreferences sp = getContext().getSharedPreferences("Login", Context.MODE_PRIVATE);
        String unm = sp.getString("unm", "");

//        calling
        fetchWatchlist(unm);


        recycleWatchlist.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new WatchlistAdapter(dataList, getContext());
        recycleWatchlist.setAdapter(adapter);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDelete(adapter, getContext()));
        itemTouchHelper.attachToRecyclerView(recycleWatchlist);
    }

    public void fetchWatchlist(String username) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Constant_user_info.TABLE_NAME);
        Query query = reference.orderByChild("username").equalTo(username);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Retrieve the current watchlist
                    if (snapshot.hasChild("watchlist")) {
                        for (DataSnapshot stockSnapshot : snapshot.child("watchlist").getChildren()) {
                            // Get the stock symbol as a String
                            String symbol1 = stockSnapshot.getValue(String.class);
                            arrayListTemp.add(symbol1);

                            //calling price
//                            fetchStockPrices(symbol1);

                            // If you want to use WatchlistModel, create an instance of it
                            WatchlistModel model = new WatchlistModel(symbol1);

                            // Add the model to your list
                            list.add(model);
//                            Toast.makeText(getContext(), arrayListTemp.toString(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        watchlistPbar.setVisibility(View.GONE);
                        Snackbar.make(watchlistRelative, "No stocks added in watchlist", Snackbar.LENGTH_SHORT).show();
                    }
                    adapter.notifyDataSetChanged();
                }

                runnable = new Runnable() {
                    @Override
                    public void run() {
                        int i = 0;
                        for (String symbol : arrayListTemp) {
//                            fetch stock price
                            fetchStockPrices(symbol, i);
                            i++;
                        }
                        handler.postDelayed(this, 300000);
                    }
                };
                handler.post(runnable);
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "cancel", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void fetchStockPrices(String stockSymbol, int position) {
        dataList.clear();

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
                        symbolName = stockResponse.chart.result[0].meta.symbolFullname;
                        stockPlusMinusPoints = decim.format(Double.parseDouble(stockPrice) - Double.parseDouble(previousClose));
                        stockPlusMinusPercentage = "  (" + decim.format(((Double.parseDouble(stockPlusMinusPoints) * 100) / Double.parseDouble(stockPrice))) + "%)";
                        if (Double.parseDouble(stockPlusMinusPoints) > 0) {
                            stockPlusMinusPoints = "+" + stockPlusMinusPoints;
                            stockPlusMinusPercentage = " (+" + decim.format(((Double.parseDouble(stockPlusMinusPoints) * 100) / Double.parseDouble(stockPrice))) + "%)";
                        }
//                        Toast.makeText(getContext(), stockPrice, Toast.LENGTH_SHORT).show();
                        ensureListSize(dataList, arrayListTemp.size());

                        dataList.set(position, new WatchlistModel(symbol, symbolName, stockPrice, stockPlusMinusPoints, stockPlusMinusPercentage));
//                        dataList.add(stockPrice, stockPlusMinusPoints, stockPlusMinusPercentage);
//                        adapter.notifyDataSetChanged();
                        adapter.notifyItemChanged(position);
                        watchlistPbar.setVisibility(View.GONE);
                    } else {
                        Log.d("problem", "some Problem");
                        watchlistPbar.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    watchlistPbar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<IndicesResponseModel> call, Throwable t) {
                t.printStackTrace();
                watchlistPbar.setVisibility(View.GONE);
            }
        });
    }

    private void ensureListSize(ArrayList<WatchlistModel> list, int size) {
        while (list.size() < size) {
            list.add(new WatchlistModel("", "", "0", "0", "0"));
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }
}