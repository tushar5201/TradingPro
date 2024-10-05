package com.example.tradingpro.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tradingpro.Activity.StockOverviewActivity;
import com.example.tradingpro.Model.GainerLosersModel;
import com.example.tradingpro.R;

import java.util.ArrayList;

public class GainerLosersAdapter extends RecyclerView.Adapter<GainerLosersAdapter.gainersViewHolder> {
    ArrayList<GainerLosersModel> list;
    Context context;

    public GainerLosersAdapter(ArrayList<GainerLosersModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public gainersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context con = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(con);
        View view = layoutInflater.inflate(R.layout.row_watchlist, parent, false);
        GainerLosersAdapter.gainersViewHolder gainersViewHolder = new GainerLosersAdapter.gainersViewHolder(view);
        return gainersViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull gainersViewHolder holder, int position) {
        GainerLosersModel gainerLosersModel = list.get(position);
        holder.symbol.setText(gainerLosersModel.getSymbol());
        holder.stockPrice.setText(gainerLosersModel.getPrice());
        if (Double.parseDouble(gainerLosersModel.getChange()) >= 0) {
            holder.stockPlusMinusPoints.setTextColor(Color.parseColor("#3FC33F"));
            holder.stockPlusMinusPercentage.setTextColor(Color.parseColor("#3FC33F"));
        } else {
            holder.stockPlusMinusPoints.setTextColor(Color.parseColor("#D53030"));
            holder.stockPlusMinusPercentage.setTextColor(Color.parseColor("#D53030"));
        }
        holder.stockPlusMinusPercentage.setText(gainerLosersModel.getChangesPercentage());
        holder.stockPlusMinusPoints.setText(gainerLosersModel.getChange());

//        holder.itemView.setOnClickListener(v-> {
//            String symbolName = gainerLosersModel.getSymbolName();
//            String symbol = gainerLosersModel.getSymbol();
//            Intent i1 = new Intent(v.getContext(), StockOverviewActivity.class);
//            i1.putExtra("symbolName", symbolName);
//            i1.putExtra("symbol", symbol);
//            v.getContext().startActivity(i1);
//        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class gainersViewHolder extends RecyclerView.ViewHolder {
        TextView symbol, symbolName, stockPrice, stockPlusMinusPercentage, stockPlusMinusPoints;
        ImageView plus;
        public gainersViewHolder(@NonNull View itemView) {
            super(itemView);

            symbol = itemView.findViewById(R.id.symbolWatch);
            stockPrice = itemView.findViewById(R.id.stockPriceWatch);
            stockPlusMinusPercentage = itemView.findViewById(R.id.stockPlusMinusPercentageWatch);
            stockPlusMinusPoints = itemView.findViewById(R.id.stockPlusMinusPointsWatch);
        }
    }
}
