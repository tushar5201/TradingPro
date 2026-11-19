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
import com.example.tradingpro.R;
import com.example.tradingpro.Trading.HoldingModel;
import com.google.android.material.button.MaterialButton;

import java.util.List;
import java.util.Locale;

public class HoldingsAdapter extends RecyclerView.Adapter<HoldingsAdapter.HoldingViewHolder> {

    public interface OnSellClickListener {
        void onSell(HoldingModel holding);
    }

    private final List<HoldingModel> list;
    private final Context context;
    private final OnSellClickListener sellClickListener;

    public HoldingsAdapter(List<HoldingModel> list, Context context, OnSellClickListener sellClickListener) {
        this.list = list;
        this.context = context;
        this.sellClickListener = sellClickListener;
    }

    @NonNull
    @Override
    public HoldingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_holding, parent, false);
        return new HoldingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HoldingViewHolder holder, int position) {
        HoldingModel item = list.get(position);

        holder.tvHoldingSymbol.setText(StockUtils.cleanDisplaySymbol(item.getSymbol()));
        holder.tvHoldingCompanyName.setText(item.getCompanyName());
        holder.tvHoldingBadge.setText(String.format("%s • %s", item.getExchange(), item.getProductType()));

        holder.tvHoldingCurValue.setText(String.format(Locale.getDefault(), "₹%.2f", item.getCurrentValue()));
        holder.tvHoldingLtp.setText(String.format(Locale.getDefault(), "LTP ₹%.2f", item.getCurrentPrice()));

        holder.tvHoldingSharesAndAvg.setText(String.format(Locale.getDefault(), "%d shares • Avg. ₹%.2f", item.getQuantity(), item.getAvgBuyPrice()));

        double pnl = item.getProfitLoss();
        double pnlPct = item.getProfitLossPercentage();
        int color = pnl >= 0 ? Color.parseColor("#00D09C") : Color.parseColor("#EB5757");
        String pnlStr = (pnl >= 0 ? "+₹" : "-₹") + StockUtils.formatPrice(Math.abs(pnl)) + " " + StockUtils.formatPercentage(pnlPct);

        holder.tvHoldingReturns.setText(pnlStr);
        holder.tvHoldingReturns.setTextColor(color);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), StockOverviewActivity.class);
            intent.putExtra("symbol", item.getSymbol());
            intent.putExtra("symbolName", item.getCompanyName());
            v.getContext().startActivity(intent);
        });

        holder.btnHoldingSell.setOnClickListener(v -> {
            if (sellClickListener != null) {
                sellClickListener.onSell(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class HoldingViewHolder extends RecyclerView.ViewHolder {
        TextView tvHoldingSymbol, tvHoldingBadge, tvHoldingCompanyName, tvHoldingCurValue, tvHoldingLtp;
        TextView tvHoldingSharesAndAvg, tvHoldingReturns;
        MaterialButton btnHoldingSell;

        public HoldingViewHolder(@NonNull View itemView) {
            super(itemView);
            tvHoldingSymbol = itemView.findViewById(R.id.tvHoldingSymbol);
            tvHoldingBadge = itemView.findViewById(R.id.tvHoldingBadge);
            tvHoldingCompanyName = itemView.findViewById(R.id.tvHoldingCompanyName);
            tvHoldingCurValue = itemView.findViewById(R.id.tvHoldingCurValue);
            tvHoldingLtp = itemView.findViewById(R.id.tvHoldingLtp);
            tvHoldingSharesAndAvg = itemView.findViewById(R.id.tvHoldingSharesAndAvg);
            tvHoldingReturns = itemView.findViewById(R.id.tvHoldingReturns);
            btnHoldingSell = itemView.findViewById(R.id.btnHoldingSell);
        }
    }
}
