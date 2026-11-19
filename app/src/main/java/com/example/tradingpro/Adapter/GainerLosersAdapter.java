package com.example.tradingpro.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tradingpro.Activity.StockOverviewActivity;
import com.example.tradingpro.Constant.StockUtils;
import com.example.tradingpro.Model.GainerLosersModel;
import com.example.tradingpro.R;

import java.util.ArrayList;

public class GainerLosersAdapter extends RecyclerView.Adapter<GainerLosersAdapter.gainersViewHolder> {
    private final ArrayList<GainerLosersModel> list;
    private final Context context;

    public GainerLosersAdapter(ArrayList<GainerLosersModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public gainersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_watchlist, parent, false);
        return new gainersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull gainersViewHolder holder, int position) {
        GainerLosersModel item = list.get(position);

        holder.symbol.setText(StockUtils.cleanDisplaySymbol(item.getSymbol()));
        if (holder.symbolExchange != null) {
            String name = item.getSymbolName();
            if (name == null || name.isEmpty() || name.equals(item.getSymbol())) {
                holder.symbolExchange.setText(item.getExchange());
            } else {
                holder.symbolExchange.setText(String.format("%s • %s", item.getExchange(), name));
            }
        }

        holder.stockPrice.setText(item.getPrice());
        holder.stockPlusMinusPercentage.setText(item.getChangesPercentage());
        holder.stockPlusMinusPoints.setText(item.getChange());

        double changeVal = StockUtils.safeParseDouble(item.getChange(), 0.0);
        int color = changeVal >= 0 ? Color.parseColor("#00D09C") : Color.parseColor("#EB5757");
        holder.stockPlusMinusPoints.setTextColor(color);
        holder.stockPlusMinusPercentage.setTextColor(color);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), StockOverviewActivity.class);
            intent.putExtra("symbolName", item.getSymbolName());
            intent.putExtra("symbol", item.getSymbol());
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class gainersViewHolder extends RecyclerView.ViewHolder {
        TextView symbol, symbolExchange, stockPrice, stockPlusMinusPercentage, stockPlusMinusPoints;

        public gainersViewHolder(@NonNull View itemView) {
            super(itemView);
            symbol = itemView.findViewById(R.id.symbolWatch);
            symbolExchange = itemView.findViewById(R.id.symbolExchangeWatch);
            stockPrice = itemView.findViewById(R.id.stockPriceWatch);
            stockPlusMinusPercentage = itemView.findViewById(R.id.stockPlusMinusPercentageWatch);
            stockPlusMinusPoints = itemView.findViewById(R.id.stockPlusMinusPointsWatch);
        }
    }
}
