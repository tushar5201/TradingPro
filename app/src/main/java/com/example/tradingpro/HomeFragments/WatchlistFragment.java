package com.example.tradingpro.HomeFragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.tradingpro.Adapter.SearchAdapter;
import com.example.tradingpro.Adapter.WatchlistAdapter;
import com.example.tradingpro.Constant.Constant_user_info;
import com.example.tradingpro.Model.WatchlistModel;
import com.example.tradingpro.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WatchlistFragment extends Fragment {

    View view;
    RecyclerView recycleWatchlist;
    WatchlistAdapter adapter;
    ArrayList<WatchlistModel> list = new ArrayList<>();


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

//        shared preferences name
        SharedPreferences sp = getContext().getSharedPreferences("Login", Context.MODE_PRIVATE);
        String unm = sp.getString("unm", "");

//        calling
        fetchWatchlist(unm);

        recycleWatchlist.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new WatchlistAdapter(list, getContext());
        recycleWatchlist.setAdapter(adapter);
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
                            String symbol = stockSnapshot.getValue(String.class);

                            // If you want to use WatchlistModel, create an instance of it
                            WatchlistModel model = new WatchlistModel(symbol);

                            // Add the model to your list
                            list.add(model);
                        }
                    }
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "cancel", Toast.LENGTH_SHORT).show();

            }
        });

    }
}