package com.example.tradingpro.HomeFragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tradingpro.Activity.HomeActivity;
import com.example.tradingpro.Activity.StockOverviewActivity;
import com.example.tradingpro.Adapter.HoldingsAdapter;
import com.example.tradingpro.Constant.StockUtils;
import com.example.tradingpro.Interfaces.StockPrice;
import com.example.tradingpro.Model.StockPriceModel;
import com.example.tradingpro.Network.ApiClient;
import com.example.tradingpro.R;
import com.example.tradingpro.Trading.HoldingModel;
import com.example.tradingpro.Trading.TradingManager;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HoldingsFragment extends Fragment implements HoldingsAdapter.OnSellClickListener {

    private TextView tvTotalPortfolioValue, tvTotalInvested, tvTotalReturns, tvWalletCash, tvHoldingsCount;
    private MaterialButton btnAddMoney;
    private RecyclerView rcylHoldings;
    private LinearLayout layoutHoldingsEmpty;
    private MaterialButton btnExploreStocks;

    private final List<HoldingModel> holdingsList = new ArrayList<>();
    private HoldingsAdapter adapter;
    private final Handler liveHandler = new Handler();
    private Runnable liveRunnable;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_holdings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvTotalPortfolioValue = view.findViewById(R.id.tvTotalPortfolioValue);
        tvTotalInvested = view.findViewById(R.id.tvTotalInvested);
        tvTotalReturns = view.findViewById(R.id.tvTotalReturns);
        tvWalletCash = view.findViewById(R.id.tvWalletCash);
        tvHoldingsCount = view.findViewById(R.id.tvHoldingsCount);
        btnAddMoney = view.findViewById(R.id.btnAddMoney);
        rcylHoldings = view.findViewById(R.id.rcylHoldings);
        layoutHoldingsEmpty = view.findViewById(R.id.layoutHoldingsEmpty);
        btnExploreStocks = view.findViewById(R.id.btnExploreStocks);

        rcylHoldings.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new HoldingsAdapter(holdingsList, getContext(), this);
        rcylHoldings.setAdapter(adapter);

        btnAddMoney.setOnClickListener(v -> showAddFundsBottomSheet());

        btnExploreStocks.setOnClickListener(v -> {
            if (getActivity() instanceof HomeActivity) {
                ((HomeActivity) getActivity()).switchToMarkets();
            }
        });

        loadHoldingsData();

        liveRunnable = new Runnable() {
            @Override
            public void run() {
                refreshLivePrices();
                liveHandler.postDelayed(this, 10000); // Poll every 10s
            }
        };
        liveHandler.postDelayed(liveRunnable, 5000);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadHoldingsData();
    }

    public void loadHoldingsData() {
        if (!isAdded()) return;
        TradingManager manager = TradingManager.getInstance(requireContext());
        List<HoldingModel> stored = manager.getHoldings();

        holdingsList.clear();
        holdingsList.addAll(stored);
        adapter.notifyDataSetChanged();

        updatePortfolioSummary();
    }

    private void updatePortfolioSummary() {
        if (!isAdded()) return;
        TradingManager manager = TradingManager.getInstance(requireContext());
        double balance = manager.getWalletBalance();
        tvWalletCash.setText(String.format(Locale.getDefault(), "₹%.2f", balance));

        if (holdingsList.isEmpty()) {
            layoutHoldingsEmpty.setVisibility(View.VISIBLE);
            rcylHoldings.setVisibility(View.GONE);
            tvHoldingsCount.setText("0 Stocks");
            tvTotalPortfolioValue.setText("₹0.00");
            tvTotalInvested.setText("₹0.00");
            tvTotalReturns.setText("₹0.00 (0.00%)");
            tvTotalReturns.setTextColor(Color.parseColor("#00D09C"));
            return;
        }

        layoutHoldingsEmpty.setVisibility(View.GONE);
        rcylHoldings.setVisibility(View.VISIBLE);
        tvHoldingsCount.setText(String.format(Locale.getDefault(), "%d Stocks", holdingsList.size()));

        double totalValue = 0.0;
        double totalInvested = 0.0;

        for (HoldingModel h : holdingsList) {
            totalValue += h.getCurrentValue();
            totalInvested += h.getTotalInvested();
        }

        tvTotalPortfolioValue.setText(String.format(Locale.getDefault(), "₹%.2f", totalValue));
        tvTotalInvested.setText(String.format(Locale.getDefault(), "₹%.2f", totalInvested));

        double pnl = totalValue - totalInvested;
        double pnlPct = totalInvested > 0 ? (pnl / totalInvested) * 100.0 : 0.0;

        int color = pnl >= 0 ? Color.parseColor("#00D09C") : Color.parseColor("#EB5757");
        String pnlStr = (pnl >= 0 ? "+₹" : "-₹") + StockUtils.formatPrice(Math.abs(pnl)) + " " + StockUtils.formatPercentage(pnlPct);

        tvTotalReturns.setText(pnlStr);
        tvTotalReturns.setTextColor(color);
    }

    private void refreshLivePrices() {
        if (!isAdded() || holdingsList.isEmpty()) return;
        StockPrice api = ApiClient.createService(StockPrice.class);

        for (int i = 0; i < holdingsList.size(); i++) {
            final int pos = i;
            HoldingModel h = holdingsList.get(pos);
            api.getChartData(h.getSymbol()).enqueue(new Callback<StockPriceModel>() {
                @Override
                public void onResponse(@NonNull Call<StockPriceModel> call, @NonNull Response<StockPriceModel> response) {
                    if (response.isSuccessful() && response.body() != null && isAdded()) {
                        StockPriceModel model = response.body();
                        if (model.chart != null && model.chart.result != null && model.chart.result.length > 0) {
                            StockPriceModel.Meta meta = model.chart.result[0].meta;
                            if (meta != null && meta.regularMarketPrice != null) {
                                double cur = StockUtils.safeParseDouble(meta.regularMarketPrice, 0.0);
                                if (cur > 0 && pos < holdingsList.size()) {
                                    holdingsList.get(pos).setCurrentPrice(cur);
                                    TradingManager.getInstance(requireContext()).updateHoldingPrice(h.getSymbol(), cur);
                                    adapter.notifyItemChanged(pos);
                                    updatePortfolioSummary();
                                }
                            }
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<StockPriceModel> call, @NonNull Throwable t) {}
            });
        }
    }

    private void showAddFundsBottomSheet() {
        if (!isAdded()) return;
        BottomSheetDialog dialog = new BottomSheetDialog(requireContext());
        View sheet = getLayoutInflater().inflate(R.layout.dialog_add_funds, null);
        dialog.setContentView(sheet);

        EditText etDepositAmount = sheet.findViewById(R.id.etDepositAmount);
        TextView chipAdd10k = sheet.findViewById(R.id.chipAdd10k);
        TextView chipAdd50k = sheet.findViewById(R.id.chipAdd50k);
        TextView chipAdd100k = sheet.findViewById(R.id.chipAdd100k);
        MaterialButton btnConfirmAddFunds = sheet.findViewById(R.id.btnConfirmAddFunds);

        chipAdd10k.setOnClickListener(v -> etDepositAmount.setText("10000"));
        chipAdd50k.setOnClickListener(v -> etDepositAmount.setText("50000"));
        chipAdd100k.setOnClickListener(v -> etDepositAmount.setText("100000"));

        btnConfirmAddFunds.setOnClickListener(v -> {
            String val = etDepositAmount.getText().toString().trim();
            double amt = StockUtils.safeParseDouble(val, 0.0);
            if (amt <= 0) {
                Toast.makeText(requireContext(), "Please enter a valid amount", Toast.LENGTH_SHORT).show();
                return;
            }
            TradingManager.getInstance(requireContext()).addFunds(amt);
            Toast.makeText(requireContext(), String.format(Locale.getDefault(), "Added ₹%.2f to your wallet!", amt), Toast.LENGTH_SHORT).show();
            dialog.dismiss();
            loadHoldingsData();
        });

        dialog.show();
    }

    @Override
    public void onSell(HoldingModel holding) {
        if (holding == null) return;
        Intent intent = new Intent(requireContext(), StockOverviewActivity.class);
        intent.putExtra("symbol", holding.getSymbol());
        intent.putExtra("symbolName", holding.getCompanyName());
        startActivity(intent);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (liveHandler != null && liveRunnable != null) {
            liveHandler.removeCallbacks(liveRunnable);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (liveHandler != null && liveRunnable != null) {
            liveHandler.removeCallbacks(liveRunnable);
        }
    }
}
