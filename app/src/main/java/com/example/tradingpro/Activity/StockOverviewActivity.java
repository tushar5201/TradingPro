package com.example.tradingpro.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager.widget.ViewPager;

import com.example.tradingpro.Adapter.OverviewTabAdapter;
import com.example.tradingpro.Constant.Constant_user_info;
import com.example.tradingpro.Constant.StockUtils;
import com.example.tradingpro.Interfaces.StockPrice;
import com.example.tradingpro.Model.StockPriceModel;
import com.example.tradingpro.Network.ApiClient;
import com.example.tradingpro.OverviewTabFragment.CompanyProfileFragment;
import com.example.tradingpro.OverviewTabFragment.OverviewFragment;
import com.example.tradingpro.R;
import com.example.tradingpro.Trading.HoldingModel;
import com.example.tradingpro.Trading.TradeResult;
import com.example.tradingpro.Trading.TradingManager;
import com.github.mikephil.charting.charts.CandleStickChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StockOverviewActivity extends AppCompatActivity {

    private TabLayout tabstock;
    private ViewPager viewpagerstock;
    private ImageView cancelButton, imgBtnAddWatchList;
    private TextView stockPrice, stockPlusMinusPoints, stockPlusMinusPercentage, tvHoldingStatus;
    private MaterialButton btnBuy, btnSell;

    private final Handler handler = new Handler();
    private Runnable runnable;

    private String previousClose = "0.0";
    private String price = "0.0";
    private String plusMinusPoints = "0.0";
    private String plusMinusPercentage = "0.0";
    private String symbol = "";
    private String symbolName = "";
    private double currentMarketPrice = 0.0;

    private CandleStickChart candleStickChart;
    private final ArrayList<Integer> timestamps = new ArrayList<>();
    private final ArrayList<Float> highs = new ArrayList<>();
    private final ArrayList<Float> lows = new ArrayList<>();
    private final ArrayList<Float> opens = new ArrayList<>();
    private final ArrayList<Float> closes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_stock_overview);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(30, systemBars.top + 10, 30, systemBars.bottom + 10);
            return insets;
        });

        tabstock = findViewById(R.id.tabstock);
        viewpagerstock = findViewById(R.id.viewpagerstock);
        stockPrice = findViewById(R.id.stockPrice);
        stockPlusMinusPoints = findViewById(R.id.stockPlusMinusPoints);
        stockPlusMinusPercentage = findViewById(R.id.stockPlusMinusPercentage);
        cancelButton = findViewById(R.id.cancelButton);
        imgBtnAddWatchList = findViewById(R.id.imgBtnAddWatchList);
        tvHoldingStatus = findViewById(R.id.tvHoldingStatus);
        btnBuy = findViewById(R.id.btnBuy);
        btnSell = findViewById(R.id.btnSell);
        candleStickChart = findViewById(R.id.candleChart);

        // Getting stock intent
        Intent intent = getIntent();
        symbolName = intent.getStringExtra("symbolName");
        symbol = intent.getStringExtra("symbol");
        if (symbol == null || symbol.isEmpty()) {
            symbol = "RELIANCE.NS";
        }
        if (symbolName == null || symbolName.isEmpty()) {
            symbolName = StockUtils.cleanDisplaySymbol(symbol);
        }

        ((TextView) findViewById(R.id.symbolFullName)).setText(symbolName);
        ((TextView) findViewById(R.id.stockName)).setText(symbol);

        cancelButton.setOnClickListener(v -> finish());

        // Watchlist add button
        if (imgBtnAddWatchList != null) {
            imgBtnAddWatchList.setOnClickListener(v -> addToWatchlist(symbol));
        }

        // Buy and Sell click handlers
        if (btnBuy != null) {
            btnBuy.setOnClickListener(v -> showTradeBottomSheet(true));
        }
        if (btnSell != null) {
            btnSell.setOnClickListener(v -> showTradeBottomSheet(false));
        }

        updateHoldingStatus();

        // Orientation setup
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            candlechart();
        } else {
            OverviewFragment overviewFragment = new OverviewFragment();
            CompanyProfileFragment companyProfileFragment = new CompanyProfileFragment();
            Bundle bundle = new Bundle();
            bundle.putString("symbol", symbol);
            companyProfileFragment.setArguments(bundle);
            overviewFragment.setArguments(bundle);

            OverviewTabAdapter adapter = new OverviewTabAdapter(getSupportFragmentManager());
            adapter.addFragment(overviewFragment);
            adapter.addFragment(companyProfileFragment);
            viewpagerstock.setAdapter(adapter);
            tabstock.setupWithViewPager(viewpagerstock);
        }

        // Live polling
        runnable = new Runnable() {
            @Override
            public void run() {
                fetchStockPrice(symbol);
                handler.postDelayed(this, 3000);
            }
        };
        handler.post(runnable);
    }

    private void updateHoldingStatus() {
        if (tvHoldingStatus == null) return;
        HoldingModel holding = TradingManager.getInstance(this).getHolding(symbol);
        if (holding != null && holding.getQuantity() > 0) {
            tvHoldingStatus.setVisibility(View.VISIBLE);
            tvHoldingStatus.setText(String.format(Locale.getDefault(), "Holdings: %d shares • Avg ₹%.2f • Value: ₹%.2f",
                    holding.getQuantity(), holding.getAvgBuyPrice(), holding.getCurrentValue()));
        } else {
            tvHoldingStatus.setVisibility(View.GONE);
        }
    }

    public void fetchStockPrice(String symbol) {
        StockPrice apiService = ApiClient.createService(StockPrice.class);
        Call<StockPriceModel> call = apiService.getChartData(symbol);

        call.enqueue(new Callback<StockPriceModel>() {
            @Override
            public void onResponse(@NonNull Call<StockPriceModel> call, @NonNull Response<StockPriceModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    StockPriceModel stockPriceModel = response.body();
                    if (stockPriceModel.chart != null && stockPriceModel.chart.result != null && stockPriceModel.chart.result.length > 0) {
                        StockPriceModel.Meta meta = stockPriceModel.chart.result[0].meta;
                        if (meta != null) {
                            price = meta.regularMarketPrice != null ? meta.regularMarketPrice : "0";
                            previousClose = meta.previousClose != null ? meta.previousClose : (meta.chartPreviousClose != null ? meta.chartPreviousClose : price);

                            double currentPriceNum = StockUtils.safeParseDouble(price, 0.0);
                            double prevCloseNum = StockUtils.safeParseDouble(previousClose, currentPriceNum);
                            currentMarketPrice = currentPriceNum;

                            // Update live price in portfolio
                            TradingManager.getInstance(StockOverviewActivity.this).updateHoldingPrice(symbol, currentMarketPrice);

                            double diff = currentPriceNum - prevCloseNum;
                            double pct = prevCloseNum > 0 ? (diff / prevCloseNum) * 100.0 : 0.0;

                            plusMinusPoints = StockUtils.formatPoints(diff);
                            plusMinusPercentage = StockUtils.formatPercentage(pct);

                            String currSym = StockUtils.getCurrencySymbol(meta.currency, symbol);
                            stockPrice.setText(String.format("%s%s", currSym, StockUtils.formatPrice(currentPriceNum)));
                            stockPlusMinusPoints.setText(plusMinusPoints);
                            stockPlusMinusPercentage.setText(plusMinusPercentage);

                            int color = diff >= 0 ? Color.parseColor("#00D09C") : Color.parseColor("#EB5757");
                            stockPlusMinusPoints.setTextColor(color);
                            stockPlusMinusPercentage.setTextColor(color);

                            updateHoldingStatus();
                        }
                    }
                } else {
                    Log.e("StockOverview", "Failed fetch stock price: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<StockPriceModel> call, @NonNull Throwable t) {
                Log.e("StockOverview", "Error fetching price", t);
            }
        });
    }

    private void showTradeBottomSheet(boolean isBuy) {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View sheetView = getLayoutInflater().inflate(R.layout.dialog_trade, null);
        dialog.setContentView(sheetView);

        TextView tvTradeTitle = sheetView.findViewById(R.id.tvTradeTitle);
        TextView tvTradeCompanyName = sheetView.findViewById(R.id.tvTradeCompanyName);
        TextView tvTradeLtp = sheetView.findViewById(R.id.tvTradeLtp);
        TextView tabDelivery = sheetView.findViewById(R.id.tabDelivery);
        TextView tabIntraday = sheetView.findViewById(R.id.tabIntraday);
        EditText etQuantity = sheetView.findViewById(R.id.etQuantity);
        MaterialButton btnQtyMinus = sheetView.findViewById(R.id.btnQtyMinus);
        MaterialButton btnQtyPlus = sheetView.findViewById(R.id.btnQtyPlus);
        TextView chipQty1 = sheetView.findViewById(R.id.chipQty1);
        TextView chipQty5 = sheetView.findViewById(R.id.chipQty5);
        TextView chipQty10 = sheetView.findViewById(R.id.chipQty10);
        TextView chipQty25 = sheetView.findViewById(R.id.chipQty25);
        TextView chipQtyAll = sheetView.findViewById(R.id.chipQtyAll);
        TextView tvApproxOrderValue = sheetView.findViewById(R.id.tvApproxOrderValue);
        TextView tvBalanceLabel = sheetView.findViewById(R.id.tvBalanceLabel);
        TextView tvAvailableBalance = sheetView.findViewById(R.id.tvAvailableBalance);
        TextView tvValidationError = sheetView.findViewById(R.id.tvValidationError);
        MaterialButton btnConfirmTrade = sheetView.findViewById(R.id.btnConfirmTrade);

        final String[] productType = {"DELIVERY"};
        TradingManager tradingManager = TradingManager.getInstance(this);
        HoldingModel existingHolding = tradingManager.getHolding(symbol);
        int ownedShares = existingHolding != null ? existingHolding.getQuantity() : 0;
        double balance = tradingManager.getWalletBalance();

        String cleanSym = StockUtils.cleanDisplaySymbol(symbol);
        String actionPrefix = isBuy ? "BUY" : "SELL";
        tvTradeTitle.setText(String.format("%s %s", actionPrefix, cleanSym));
        tvTradeCompanyName.setText(String.format("%s • NSE", symbolName));

        double ltp = currentMarketPrice > 0 ? currentMarketPrice : StockUtils.safeParseDouble(price, 1000.0);
        tvTradeLtp.setText(String.format(Locale.getDefault(), "₹%.2f", ltp));

        if (isBuy) {
            btnConfirmTrade.setText(String.format("BUY %s", cleanSym));
            btnConfirmTrade.setBackgroundColor(Color.parseColor("#00D09C"));
            tvBalanceLabel.setText("Available Balance");
            tvAvailableBalance.setText(String.format(Locale.getDefault(), "₹%.2f", balance));
            chipQtyAll.setVisibility(View.GONE);
        } else {
            btnConfirmTrade.setText(String.format("SELL %s", cleanSym));
            btnConfirmTrade.setBackgroundColor(Color.parseColor("#EB5757"));
            tvBalanceLabel.setText("Shares Owned");
            tvAvailableBalance.setText(String.format(Locale.getDefault(), "%d shares", ownedShares));
            chipQtyAll.setVisibility(ownedShares > 0 ? View.VISIBLE : View.GONE);
        }

        // Tabs
        tabDelivery.setOnClickListener(v -> {
            productType[0] = "DELIVERY";
            tabDelivery.setBackgroundResource(R.drawable.cradius);
            tabDelivery.setBackgroundTintList(getColorStateList(R.color.yellow));
            tabDelivery.setTextColor(Color.BLACK);
            tabIntraday.setBackground(null);
            tabIntraday.setTextColor(getColor(R.color.tabUnselect));
        });

        tabIntraday.setOnClickListener(v -> {
            productType[0] = "INTRADAY";
            tabIntraday.setBackgroundResource(R.drawable.cradius);
            tabIntraday.setBackgroundTintList(getColorStateList(R.color.yellow));
            tabIntraday.setTextColor(Color.BLACK);
            tabDelivery.setBackground(null);
            tabDelivery.setTextColor(getColor(R.color.tabUnselect));
        });

        Runnable updateCalculation = () -> {
            String qtyStr = etQuantity.getText().toString().trim();
            int qty = 1;
            try {
                qty = Integer.parseInt(qtyStr);
            } catch (Exception ignored) {}

            double total = qty * ltp;
            tvApproxOrderValue.setText(String.format(Locale.getDefault(), "₹%.2f", total));

            if (isBuy) {
                if (total > balance) {
                    tvValidationError.setVisibility(View.VISIBLE);
                    tvValidationError.setText(String.format(Locale.getDefault(), "Insufficient balance! Need ₹%.2f, have ₹%.2f", total, balance));
                    btnConfirmTrade.setEnabled(false);
                } else if (qty <= 0) {
                    tvValidationError.setVisibility(View.VISIBLE);
                    tvValidationError.setText("Quantity must be at least 1");
                    btnConfirmTrade.setEnabled(false);
                } else {
                    tvValidationError.setVisibility(View.GONE);
                    btnConfirmTrade.setEnabled(true);
                }
            } else {
                if (ownedShares <= 0) {
                    tvValidationError.setVisibility(View.VISIBLE);
                    tvValidationError.setText("You do not own any shares of " + cleanSym + " to sell.");
                    btnConfirmTrade.setEnabled(false);
                } else if (qty > ownedShares) {
                    tvValidationError.setVisibility(View.VISIBLE);
                    tvValidationError.setText(String.format(Locale.getDefault(), "You only own %d shares. Cannot sell %d.", ownedShares, qty));
                    btnConfirmTrade.setEnabled(false);
                } else if (qty <= 0) {
                    tvValidationError.setVisibility(View.VISIBLE);
                    tvValidationError.setText("Quantity must be at least 1");
                    btnConfirmTrade.setEnabled(false);
                } else {
                    tvValidationError.setVisibility(View.GONE);
                    btnConfirmTrade.setEnabled(true);
                }
            }
        };

        etQuantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateCalculation.run();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnQtyMinus.setOnClickListener(v -> {
            int q = 1;
            try { q = Integer.parseInt(etQuantity.getText().toString().trim()); } catch (Exception ignored) {}
            if (q > 1) {
                etQuantity.setText(String.valueOf(q - 1));
            }
        });

        btnQtyPlus.setOnClickListener(v -> {
            int q = 1;
            try { q = Integer.parseInt(etQuantity.getText().toString().trim()); } catch (Exception ignored) {}
            etQuantity.setText(String.valueOf(q + 1));
        });

        chipQty1.setOnClickListener(v -> {
            int q = 0;
            try { q = Integer.parseInt(etQuantity.getText().toString().trim()); } catch (Exception ignored) {}
            etQuantity.setText(String.valueOf(q + 1));
        });

        chipQty5.setOnClickListener(v -> {
            int q = 0;
            try { q = Integer.parseInt(etQuantity.getText().toString().trim()); } catch (Exception ignored) {}
            etQuantity.setText(String.valueOf(q + 5));
        });

        chipQty10.setOnClickListener(v -> {
            int q = 0;
            try { q = Integer.parseInt(etQuantity.getText().toString().trim()); } catch (Exception ignored) {}
            etQuantity.setText(String.valueOf(q + 10));
        });

        chipQty25.setOnClickListener(v -> {
            int q = 0;
            try { q = Integer.parseInt(etQuantity.getText().toString().trim()); } catch (Exception ignored) {}
            etQuantity.setText(String.valueOf(q + 25));
        });

        if (chipQtyAll != null) {
            chipQtyAll.setOnClickListener(v -> {
                if (ownedShares > 0) {
                    etQuantity.setText(String.valueOf(ownedShares));
                }
            });
        }

        updateCalculation.run();

        btnConfirmTrade.setOnClickListener(v -> {
            int qty = 1;
            try { qty = Integer.parseInt(etQuantity.getText().toString().trim()); } catch (Exception ignored) {}

            TradeResult result;
            if (isBuy) {
                result = tradingManager.executeBuy(symbol, symbolName, qty, ltp, productType[0]);
            } else {
                result = tradingManager.executeSell(symbol, symbolName, qty, ltp, productType[0]);
            }

            if (result.isSuccess()) {
                dialog.dismiss();
                showSuccessDialog(result, isBuy, qty, ltp);
                updateHoldingStatus();
            } else {
                Toast.makeText(StockOverviewActivity.this, result.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        dialog.show();
    }

    private void showSuccessDialog(TradeResult result, boolean isBuy, int quantity, double price) {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_trade_success, null);
        dialog.setContentView(view);

        TextView tvSuccessTitle = view.findViewById(R.id.tvSuccessTitle);
        TextView tvSuccessSub = view.findViewById(R.id.tvSuccessSub);
        TextView tvSuccessOrderId = view.findViewById(R.id.tvSuccessOrderId);
        TextView tvSuccessTotal = view.findViewById(R.id.tvSuccessTotal);
        TextView tvSuccessBalance = view.findViewById(R.id.tvSuccessBalance);
        MaterialButton btnSuccessDone = view.findViewById(R.id.btnSuccessDone);

        tvSuccessTitle.setText(isBuy ? "Order Executed Successfully!" : "Sold Successfully!");
        tvSuccessSub.setText(String.format(Locale.getDefault(), "%s %d shares of %s at ₹%.2f",
                isBuy ? "Bought" : "Sold", quantity, StockUtils.cleanDisplaySymbol(symbol), price));

        if (result.getOrder() != null) {
            tvSuccessOrderId.setText(result.getOrder().getOrderId());
            tvSuccessTotal.setText(String.format(Locale.getDefault(), "₹%.2f", result.getOrder().getTotalAmount()));
        }
        tvSuccessBalance.setText(String.format(Locale.getDefault(), "₹%.2f", result.getRemainingBalance()));

        btnSuccessDone.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void addToWatchlist(String stockSymbol) {
        SharedPreferences sp = getSharedPreferences(Constant_user_info.SHARED_LOGIN_ID, Context.MODE_PRIVATE);
        String unm = sp.getString(Constant_user_info.SHARED_LOGIN_USERNM, "");

        if (unm == null || unm.isEmpty()) {
            Toast.makeText(this, "Added to local watchlist!", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Constant_user_info.TABLE_NAME);
        Query query = reference.orderByChild("username").equalTo(unm);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Set<String> currentWatchlist = new HashSet<>();
                    if (snapshot.hasChild("watchlist")) {
                        for (DataSnapshot stockSnapshot : snapshot.child("watchlist").getChildren()) {
                            String s = stockSnapshot.getValue(String.class);
                            if (s != null) currentWatchlist.add(s);
                        }
                    }
                    currentWatchlist.add(stockSymbol);
                    snapshot.getRef().child("watchlist").setValue(new ArrayList<>(currentWatchlist))
                            .addOnSuccessListener(s -> Toast.makeText(StockOverviewActivity.this, "Added to Watchlist!", Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e -> Toast.makeText(StockOverviewActivity.this, "Failed to update Watchlist", Toast.LENGTH_SHORT).show());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(StockOverviewActivity.this, "Failed: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void candlechart() {
        if (candleStickChart == null) return;
        timestamps.clear();
        opens.clear();
        closes.clear();
        highs.clear();
        lows.clear();

        StockPrice apiService = ApiClient.createService(StockPrice.class);
        Call<StockPriceModel> call = apiService.getChartData(symbol);
        call.enqueue(new Callback<StockPriceModel>() {
            @Override
            public void onResponse(@NonNull Call<StockPriceModel> call, @NonNull Response<StockPriceModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    StockPriceModel stockPriceModel = response.body();
                    if (stockPriceModel.chart != null && stockPriceModel.chart.result != null && stockPriceModel.chart.result.length > 0) {
                        StockPriceModel.Chart.Result res = stockPriceModel.chart.result[0];
                        if (res.timestamp != null && res.indicators != null && res.indicators.quote != null && res.indicators.quote.length > 0) {
                            timestamps.addAll(res.timestamp);
                            if (res.indicators.quote[0].open != null) opens.addAll(res.indicators.quote[0].open);
                            if (res.indicators.quote[0].close != null) closes.addAll(res.indicators.quote[0].close);
                            if (res.indicators.quote[0].high != null) highs.addAll(res.indicators.quote[0].high);
                            if (res.indicators.quote[0].low != null) lows.addAll(res.indicators.quote[0].low);

                            ArrayList<CandleEntry> values = new ArrayList<>();
                            int size = Math.min(timestamps.size(), Math.min(opens.size(), Math.min(closes.size(), Math.min(highs.size(), lows.size()))));

                            for (int i = 0; i < size; i++) {
                                Float open = opens.get(i);
                                Float close = closes.get(i);
                                Float high = highs.get(i);
                                Float low = lows.get(i);
                                if (open != null && close != null && high != null && low != null) {
                                    values.add(new CandleEntry(i, high, low, open, close));
                                }
                            }

                            if (!values.isEmpty()) {
                                CandleDataSet set = new CandleDataSet(values, "Stock Prices");
                                set.setColor(Color.rgb(80, 80, 80));
                                set.setShadowColor(Color.DKGRAY);
                                set.setShadowWidth(0.5f);
                                set.setIncreasingColor(Color.parseColor("#00D09C"));
                                set.setIncreasingPaintStyle(Paint.Style.FILL);
                                set.setDecreasingColor(Color.parseColor("#EB5757"));
                                set.setDecreasingPaintStyle(Paint.Style.FILL);
                                set.setDrawValues(false);

                                CandleData data = new CandleData(set);
                                candleStickChart.setData(data);
                                candleStickChart.invalidate();

                                XAxis xAxis = candleStickChart.getXAxis();
                                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                                xAxis.setDrawGridLines(true);
                                xAxis.setGridColor(Color.LTGRAY);
                                xAxis.setValueFormatter(new ValueFormatter() {
                                    @Override
                                    public String getFormattedValue(float value) {
                                        int index = (int) value;
                                        if (index >= 0 && index < timestamps.size()) {
                                            long timestampInMillis = timestamps.get(index) * 1000L;
                                            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault());
                                            return sdf.format(new Date(timestampInMillis));
                                        }
                                        return "";
                                    }
                                });

                                YAxis leftAxis = candleStickChart.getAxisLeft();
                                leftAxis.setEnabled(false);

                                YAxis rightAxis = candleStickChart.getAxisRight();
                                rightAxis.setDrawGridLines(true);
                                rightAxis.setGridColor(Color.LTGRAY);
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<StockPriceModel> call, @NonNull Throwable t) {
                Log.e("StockOverview", "Chart fetch failed", t);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateHoldingStatus();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (handler != null && runnable != null) {
            handler.removeCallbacks(runnable);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null && runnable != null) {
            handler.removeCallbacks(runnable);
        }
    }
}
