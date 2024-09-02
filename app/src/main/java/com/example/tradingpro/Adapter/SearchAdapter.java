package com.example.tradingpro.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tradingpro.Activity.StockOverviewActivity;
import com.example.tradingpro.Constant.Constant_user_info;
import com.example.tradingpro.R;
import com.example.tradingpro.Model.SearchModel;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.searchViewHolder>{

    ArrayList<SearchModel> list;
    Context context;


    public SearchAdapter(ArrayList<SearchModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public searchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context con = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(con);
        View view = layoutInflater.inflate(R.layout.row_search, parent, false);
        searchViewHolder searchViewHolder = new searchViewHolder(view);
        return searchViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull searchViewHolder holder, int position) {
        SearchModel searchModel = list.get(position);
        holder.symbol.setText(searchModel.getSymbol());
        holder.symbolName.setText(searchModel.getSymbolName());

        holder.plus.setOnClickListener(v -> {
            String symbol = searchModel.getSymbol();

            SharedPreferences sp = context.getSharedPreferences("Login", Context.MODE_PRIVATE);
            String unm = sp.getString("unm", "");

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Constant_user_info.TABLE_NAME);
            Query query = reference.orderByChild("username").equalTo(unm);

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        // Retrieve the current watchlist
                        Set<String> currentWatchlist = new HashSet<>();
                        if (snapshot.hasChild("watchlist")) {
                            for (DataSnapshot stockSnapshot : snapshot.child("watchlist").getChildren()) {
                                currentWatchlist.add(stockSnapshot.getValue(String.class));
                            }
                        }

                        // Append new stock to the existing watchlist
                        currentWatchlist.addAll(Collections.singleton(symbol));

                        List<String> updatedWatchlist = new ArrayList<>(currentWatchlist);

                        // Update the watchlist field
                        snapshot.getRef().child("watchlist").setValue(updatedWatchlist)
                                .addOnSuccessListener(s-> {
                                    Toast.makeText(context, "update", Toast.LENGTH_SHORT).show();
                                })

                                .addOnFailureListener(e -> {
                                    // Handle any errors
                                    Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
                                });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(context, "cancel", Toast.LENGTH_SHORT).show();

                }
            });

        });

        holder.itemView.setOnClickListener(v-> {
            String symbolName = searchModel.getSymbolName();
            String symbol = searchModel.getSymbol();
            Intent i1 = new Intent(v.getContext(), StockOverviewActivity.class);
            i1.putExtra("symbolName", symbolName);
            i1.putExtra("symbol", symbol);
            v.getContext().startActivity(i1);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class searchViewHolder extends RecyclerView.ViewHolder {

         TextView symbol, symbolName;
         ImageView plus;
         public searchViewHolder(@NonNull View itemView) {
             super(itemView);
             symbol = itemView.findViewById(R.id.symbol);
             symbolName = itemView.findViewById(R.id.symbolName);
             plus = itemView.findViewById(R.id.plus);
         }
     }
}
