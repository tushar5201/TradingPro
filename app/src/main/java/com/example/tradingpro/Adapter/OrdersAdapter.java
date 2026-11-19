package com.example.tradingpro.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tradingpro.Constant.StockUtils;
import com.example.tradingpro.R;
import com.example.tradingpro.Trading.OrderModel;

import java.util.List;
import java.util.Locale;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.OrderViewHolder> {

    private final List<OrderModel> list;
    private final Context context;

    public OrdersAdapter(List<OrderModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        OrderModel item = list.get(position);

        boolean isBuy = "BUY".equalsIgnoreCase(item.getOrderType());
        holder.tvOrderTypeBadge.setText(item.getOrderType());
        if (isBuy) {
            holder.tvOrderTypeBadge.setTextColor(Color.parseColor("#00D09C"));
            holder.tvOrderTypeBadge.setBackgroundTintList(context.getColorStateList(R.color.white));
            holder.tvOrderTypeBadge.setBackgroundColor(Color.parseColor("#E8F8F4"));
        } else {
            holder.tvOrderTypeBadge.setTextColor(Color.parseColor("#EB5757"));
            holder.tvOrderTypeBadge.setBackgroundTintList(context.getColorStateList(R.color.white));
            holder.tvOrderTypeBadge.setBackgroundColor(Color.parseColor("#FEE2E2"));
        }

        holder.tvOrderSymbol.setText(StockUtils.cleanDisplaySymbol(item.getSymbol()));
        holder.tvOrderTotalAmount.setText(String.format(Locale.getDefault(), "₹%.2f", item.getTotalAmount()));
        holder.tvOrderSharesAndPrice.setText(String.format(Locale.getDefault(), "%d shares • ₹%.2f • %s", item.getQuantity(), item.getPrice(), item.getProductType()));
        holder.tvOrderStatusBadge.setText(item.getStatus());
        holder.tvOrderDate.setText(item.getFormattedDate());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderTypeBadge, tvOrderSymbol, tvOrderTotalAmount, tvOrderSharesAndPrice, tvOrderStatusBadge, tvOrderDate;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderTypeBadge = itemView.findViewById(R.id.tvOrderTypeBadge);
            tvOrderSymbol = itemView.findViewById(R.id.tvOrderSymbol);
            tvOrderTotalAmount = itemView.findViewById(R.id.tvOrderTotalAmount);
            tvOrderSharesAndPrice = itemView.findViewById(R.id.tvOrderSharesAndPrice);
            tvOrderStatusBadge = itemView.findViewById(R.id.tvOrderStatusBadge);
            tvOrderDate = itemView.findViewById(R.id.tvOrderDate);
        }
    }
}
