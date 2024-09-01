package com.example.tradingpro.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tradingpro.Activity.StockOverviewActivity;
import com.example.tradingpro.R;
import com.example.tradingpro.Model.SearchModel;

import java.util.ArrayList;

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
