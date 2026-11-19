package com.example.tradingpro.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tradingpro.Constant.StockUtils;
import com.example.tradingpro.Model.IndicesModel;
import com.example.tradingpro.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class IndicesAdapter extends RecyclerView.Adapter<IndicesAdapter.IndicesViewHolder> {
    private final Context context;
    private final ArrayList<IndicesModel> list;

    public IndicesAdapter(Context context, ArrayList<IndicesModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public IndicesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.indices_card, parent, false);
        return new IndicesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IndicesViewHolder holder, int position) {
        IndicesModel model = list.get(position);
        holder.symbol.setText(model.getSymbol());
        holder.symbolFullName.setText(model.getSymbolFullName());
        holder.stockPrice.setText(model.getStockPrice());

        double points = StockUtils.safeParseDouble(model.getStockPlusMinusPoints(), 0.0);
        int color = points >= 0 ? Color.parseColor("#00D09C") : Color.parseColor("#EB5757");
        holder.stockPlusMinusPoints.setTextColor(color);
        holder.stockPlusMinusPercentage.setTextColor(color);

        holder.stockPlusMinusPoints.setText(model.getStockPlusMinusPoints());
        holder.stockPlusMinusPercentage.setText(model.getStockPlusMinusPercentage());

        if (model.getSymbolIcon() != null && !model.getSymbolIcon().isEmpty()) {
            Glide.with(context)
                    .load(model.getSymbolIcon())
                    .placeholder(R.drawable.nifty_icon)
                    .error(R.drawable.nifty_icon)
                    .into(holder.symbolIcon);
        } else {
            holder.symbolIcon.setImageResource(R.drawable.nifty_icon);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class IndicesViewHolder extends RecyclerView.ViewHolder {
        TextView symbol, symbolFullName, stockPrice, stockPlusMinusPoints, stockPlusMinusPercentage;
        CircleImageView symbolIcon;

        public IndicesViewHolder(@NonNull View itemView) {
            super(itemView);
            symbol = itemView.findViewById(R.id.tvSymbol);
            symbolFullName = itemView.findViewById(R.id.tvSymbolName);
            stockPrice = itemView.findViewById(R.id.tvSymbolPrice);
            stockPlusMinusPoints = itemView.findViewById(R.id.tvPlusMinusPoints);
            stockPlusMinusPercentage = itemView.findViewById(R.id.tvPlusMinusPercentage);
            symbolIcon = itemView.findViewById(R.id.symbolIcon);
        }
    }
}
