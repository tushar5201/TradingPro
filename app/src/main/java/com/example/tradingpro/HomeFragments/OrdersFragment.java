package com.example.tradingpro.HomeFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tradingpro.Adapter.OrdersAdapter;
import com.example.tradingpro.R;
import com.example.tradingpro.Trading.OrderModel;
import com.example.tradingpro.Trading.TradingManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class OrdersFragment extends Fragment {

    private RecyclerView rcylOrders;
    private TextView tvOrdersCount;
    private LinearLayout layoutOrdersEmpty;
    private OrdersAdapter adapter;
    private final List<OrderModel> ordersList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_orders, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rcylOrders = view.findViewById(R.id.rcylOrders);
        tvOrdersCount = view.findViewById(R.id.tvOrdersCount);
        layoutOrdersEmpty = view.findViewById(R.id.layoutOrdersEmpty);

        rcylOrders.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new OrdersAdapter(ordersList, getContext());
        rcylOrders.setAdapter(adapter);

        loadOrders();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadOrders();
    }

    private void loadOrders() {
        if (!isAdded()) return;
        TradingManager manager = TradingManager.getInstance(requireContext());
        List<OrderModel> stored = manager.getOrders();

        ordersList.clear();
        ordersList.addAll(stored);
        adapter.notifyDataSetChanged();

        if (ordersList.isEmpty()) {
            layoutOrdersEmpty.setVisibility(View.VISIBLE);
            rcylOrders.setVisibility(View.GONE);
            tvOrdersCount.setText("0 Orders");
        } else {
            layoutOrdersEmpty.setVisibility(View.GONE);
            rcylOrders.setVisibility(View.VISIBLE);
            tvOrdersCount.setText(String.format(Locale.getDefault(), "%d Orders", ordersList.size()));
        }
    }
}
