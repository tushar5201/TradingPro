package com.example.tradingpro.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tradingpro.Model.IndicesModel;
import com.example.tradingpro.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class IndicesAdapter extends RecyclerView.Adapter<IndicesAdapter.IndicesViewHolder> {
    Context context;
    ArrayList<IndicesModel> list;

    public IndicesAdapter(Context context, ArrayList<IndicesModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public IndicesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context con = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(con);
        View view = layoutInflater.inflate(R.layout.indices_card, parent, false);
        IndicesViewHolder indicesViewHolder = new IndicesViewHolder(view);
        return indicesViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull IndicesViewHolder holder, int position) {
        IndicesModel model = list.get(position);
        holder.symbol.setText(model.getSymbol());
        holder.symbolFullName.setText(model.getSymbolFullName());
        holder.stockPrice.setText(model.getStockPrice());
        if (Double.parseDouble(model.getStockPlusMinusPoints()) >= 0) {
            holder.stockPlusMinusPoints.setTextColor(Color.parseColor("#3FC33F"));
            holder.stockPlusMinusPercentage.setTextColor(Color.parseColor("#3FC33F"));
        } else {
            holder.stockPlusMinusPoints.setTextColor(Color.parseColor("#D53030"));
            holder.stockPlusMinusPercentage.setTextColor(Color.parseColor("#D53030"));
        }
        holder.stockPlusMinusPoints.setText(model.getStockPlusMinusPoints());
        holder.stockPlusMinusPercentage.setText(model.getStockPlusMinusPercentage());
        Glide.with(context).load(model.getSymbolIcon()).into(holder.symbolIcon);
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
