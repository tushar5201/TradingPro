package com.example.tradingpro.Trading;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.tradingpro.Constant.Constant_user_info;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class TradingManager {
    private static final String PREF_NAME = "TradingPro_TradingData";
    private static final String KEY_WALLET_PREFIX = "wallet_balance_";
    private static final String KEY_HOLDINGS_PREFIX = "holdings_";
    private static final String KEY_ORDERS_PREFIX = "orders_";
    public static final double DEFAULT_INITIAL_BALANCE = 100000.00; // Rs 1 Lakh default

    private static TradingManager instance;
    private final Context context;
    private final SharedPreferences prefs;
    private final Gson gson;

    private TradingManager(Context context) {
        this.context = context.getApplicationContext();
        this.prefs = this.context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        this.gson = new Gson();
    }

    public static synchronized TradingManager getInstance(Context context) {
        if (instance == null) {
            instance = new TradingManager(context);
        }
        return instance;
    }

    private String getUserId() {
        SharedPreferences loginPrefs = context.getSharedPreferences(Constant_user_info.SHARED_LOGIN_ID, Context.MODE_PRIVATE);
        String unm = loginPrefs.getString(Constant_user_info.SHARED_LOGIN_USERNM, "");
        if (unm == null || unm.trim().isEmpty()) {
            return "default_trader";
        }
        return unm.trim().toLowerCase(Locale.ROOT);
    }

    public synchronized double getWalletBalance() {
        String key = KEY_WALLET_PREFIX + getUserId();
        if (!prefs.contains(key)) {
            prefs.edit().putFloat(key, (float) DEFAULT_INITIAL_BALANCE).apply();
            return DEFAULT_INITIAL_BALANCE;
        }
        return prefs.getFloat(key, (float) DEFAULT_INITIAL_BALANCE);
    }

    public synchronized void setWalletBalance(double balance) {
        String key = KEY_WALLET_PREFIX + getUserId();
        prefs.edit().putFloat(key, (float) balance).apply();
    }

    public synchronized void addFunds(double amount) {
        if (amount > 0) {
            double current = getWalletBalance();
            setWalletBalance(current + amount);
        }
    }

    public synchronized List<HoldingModel> getHoldings() {
        String key = KEY_HOLDINGS_PREFIX + getUserId();
        String json = prefs.getString(key, null);
        if (json == null || json.isEmpty()) {
            return new ArrayList<>();
        }
        Type type = new TypeToken<ArrayList<HoldingModel>>() {}.getType();
        List<HoldingModel> list = gson.fromJson(json, type);
        return list != null ? list : new ArrayList<>();
    }

    public synchronized HoldingModel getHolding(String symbol) {
        if (symbol == null) return null;
        List<HoldingModel> holdings = getHoldings();
        for (HoldingModel h : holdings) {
            if (symbol.equalsIgnoreCase(h.getSymbol())) {
                return h;
            }
        }
        return null;
    }

    private synchronized void saveHoldings(List<HoldingModel> holdings) {
        String key = KEY_HOLDINGS_PREFIX + getUserId();
        String json = gson.toJson(holdings);
        prefs.edit().putString(key, json).apply();
    }

    public synchronized List<OrderModel> getOrders() {
        String key = KEY_ORDERS_PREFIX + getUserId();
        String json = prefs.getString(key, null);
        if (json == null || json.isEmpty()) {
            return new ArrayList<>();
        }
        Type type = new TypeToken<ArrayList<OrderModel>>() {}.getType();
        List<OrderModel> list = gson.fromJson(json, type);
        return list != null ? list : new ArrayList<>();
    }

    private synchronized void saveOrders(List<OrderModel> orders) {
        String key = KEY_ORDERS_PREFIX + getUserId();
        String json = gson.toJson(orders);
        prefs.edit().putString(key, json).apply();
    }

    public synchronized TradeResult executeBuy(String symbol, String companyName, int quantity, double price, String productType) {
        if (symbol == null || symbol.trim().isEmpty()) {
            return new TradeResult(false, "Invalid stock symbol", getWalletBalance(), null);
        }
        if (quantity <= 0) {
            return new TradeResult(false, "Quantity must be at least 1", getWalletBalance(), null);
        }
        if (price <= 0) {
            return new TradeResult(false, "Invalid stock price", getWalletBalance(), null);
        }

        double totalAmount = quantity * price;
        double currentBalance = getWalletBalance();

        if (totalAmount > currentBalance) {
            return new TradeResult(false, String.format(Locale.getDefault(), "Insufficient funds! Required: ₹%.2f, Available: ₹%.2f", totalAmount, currentBalance), currentBalance, null);
        }

        // Deduct money
        double newBalance = currentBalance - totalAmount;
        setWalletBalance(newBalance);

        // Update Holdings
        List<HoldingModel> holdings = getHoldings();
        HoldingModel existing = null;
        for (HoldingModel h : holdings) {
            if (symbol.equalsIgnoreCase(h.getSymbol())) {
                existing = h;
                break;
            }
        }

        if (existing != null) {
            int oldQty = existing.getQuantity();
            double oldAvg = existing.getAvgBuyPrice();
            int newQty = oldQty + quantity;
            double newAvg = ((oldQty * oldAvg) + totalAmount) / newQty;

            existing.setQuantity(newQty);
            existing.setAvgBuyPrice(newAvg);
            existing.setCurrentPrice(price);
            if (companyName != null && !companyName.isEmpty()) {
                existing.setCompanyName(companyName);
            }
        } else {
            HoldingModel newHolding = new HoldingModel(symbol, companyName, quantity, price, price, productType, "NSE");
            holdings.add(newHolding);
        }
        saveHoldings(holdings);

        // Add Order
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault());
        String dateStr = sdf.format(new Date());
        String orderId = "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        OrderModel order = new OrderModel(orderId, symbol, companyName, "BUY", productType, quantity, price, totalAmount, "COMPLETED", System.currentTimeMillis(), dateStr);
        List<OrderModel> orders = getOrders();
        orders.add(0, order); // Most recent at top
        saveOrders(orders);

        String successMsg = String.format(Locale.getDefault(), "Successfully bought %d shares of %s at ₹%.2f", quantity, symbol, price);
        return new TradeResult(true, successMsg, newBalance, order);
    }

    public synchronized TradeResult executeSell(String symbol, String companyName, int quantity, double price, String productType) {
        if (symbol == null || symbol.trim().isEmpty()) {
            return new TradeResult(false, "Invalid stock symbol", getWalletBalance(), null);
        }
        if (quantity <= 0) {
            return new TradeResult(false, "Quantity must be at least 1", getWalletBalance(), null);
        }
        if (price <= 0) {
            return new TradeResult(false, "Invalid stock price", getWalletBalance(), null);
        }

        List<HoldingModel> holdings = getHoldings();
        HoldingModel existing = null;
        int existingIndex = -1;
        for (int i = 0; i < holdings.size(); i++) {
            if (symbol.equalsIgnoreCase(holdings.get(i).getSymbol())) {
                existing = holdings.get(i);
                existingIndex = i;
                break;
            }
        }

        if (existing == null || existing.getQuantity() < quantity) {
            int owned = existing != null ? existing.getQuantity() : 0;
            return new TradeResult(false, String.format(Locale.getDefault(), "You only own %d shares of %s. Cannot sell %d.", owned, symbol, quantity), getWalletBalance(), null);
        }

        double totalAmount = quantity * price;
        double currentBalance = getWalletBalance();
        double newBalance = currentBalance + totalAmount;
        setWalletBalance(newBalance);

        // Update Holdings
        if (existing.getQuantity() == quantity) {
            holdings.remove(existingIndex);
        } else {
            existing.setQuantity(existing.getQuantity() - quantity);
            existing.setCurrentPrice(price);
        }
        saveHoldings(holdings);

        // Add Order
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault());
        String dateStr = sdf.format(new Date());
        String orderId = "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        OrderModel order = new OrderModel(orderId, symbol, companyName, "SELL", productType, quantity, price, totalAmount, "COMPLETED", System.currentTimeMillis(), dateStr);
        List<OrderModel> orders = getOrders();
        orders.add(0, order);
        saveOrders(orders);

        String successMsg = String.format(Locale.getDefault(), "Successfully sold %d shares of %s at ₹%.2f", quantity, symbol, price);
        return new TradeResult(true, successMsg, newBalance, order);
    }

    public synchronized void updateHoldingPrice(String symbol, double currentPrice) {
        if (symbol == null || currentPrice <= 0) return;
        List<HoldingModel> holdings = getHoldings();
        boolean changed = false;
        for (HoldingModel h : holdings) {
            if (symbol.equalsIgnoreCase(h.getSymbol())) {
                h.setCurrentPrice(currentPrice);
                changed = true;
                break;
            }
        }
        if (changed) {
            saveHoldings(holdings);
        }
    }
}
