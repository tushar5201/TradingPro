package com.example.tradingpro.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tradingpro.Activity.StockOverviewActivity;
import com.example.tradingpro.Model.SearchModel;
import com.example.tradingpro.Model.WatchlistModel;
import com.example.tradingpro.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import java.util.ArrayList;

public class WatchlistAdapter extends RecyclerView.Adapter<WatchlistAdapter.viewHolderWatchList> {
    ArrayList<WatchlistModel> list1;
    Context context;

    public WatchlistAdapter(ArrayList<WatchlistModel> list, Context context) {
        this.list1 = list;
        this.context = context;
    }

    @NonNull
    @Override
    public viewHolderWatchList onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context con = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(con);
        View view = layoutInflater.inflate(R.layout.row_watchlist, parent, false);
        WatchlistAdapter.viewHolderWatchList viewHolderWatchList = new WatchlistAdapter.viewHolderWatchList(view);
        return viewHolderWatchList;
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolderWatchList holder, int position) {
        WatchlistModel watchlistModel = list1.get(position);
        holder.symbol.setText(watchlistModel.getSymbol());
        holder.stockPrice.setText(watchlistModel.getStockPrice());
        if (Double.parseDouble(watchlistModel.getStockPlusMinusPoints()) >= 0) {
            holder.stockPlusMinusPoints.setTextColor(Color.parseColor("#3FC33F"));
            holder.stockPlusMinusPercentage.setTextColor(Color.parseColor("#3FC33F"));
        } else {
            holder.stockPlusMinusPoints.setTextColor(Color.parseColor("#D53030"));
            holder.stockPlusMinusPercentage.setTextColor(Color.parseColor("#D53030"));
        }
        holder.stockPlusMinusPercentage.setText(watchlistModel.getStockPlusMinusPercentage());
        holder.stockPlusMinusPoints.setText(watchlistModel.getStockPlusMinusPoints());

        holder.itemView.setOnClickListener(v-> {
            String symbolName = watchlistModel.getSymbolName();
            String symbol = watchlistModel.getSymbol();
            Intent i1 = new Intent(v.getContext(), StockOverviewActivity.class);
            i1.putExtra("symbolName", symbolName);
            i1.putExtra("symbol", symbol);
            v.getContext().startActivity(i1);
        });
    }

    @Override
    public int getItemCount() {
        return list1.size();
    }

    class viewHolderWatchList extends RecyclerView.ViewHolder {
        TextView symbol, stockPrice, stockPlusMinusPercentage, stockPlusMinusPoints;
        public viewHolderWatchList(@NonNull View itemView) {
            super(itemView);
            symbol = itemView.findViewById(R.id.symbolWatch);
            stockPrice = itemView.findViewById(R.id.stockPriceWatch);
            stockPlusMinusPercentage = itemView.findViewById(R.id.stockPlusMinusPercentageWatch);
            stockPlusMinusPoints = itemView.findViewById(R.id.stockPlusMinusPointsWatch);
        }
    }
}
