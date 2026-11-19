package com.example.tradingpro.Trading;

public class TradeResult {
    private final boolean success;
    private final String message;
    private final double remainingBalance;
    private final OrderModel order;

    public TradeResult(boolean success, String message, double remainingBalance, OrderModel order) {
        this.success = success;
        this.message = message;
        this.remainingBalance = remainingBalance;
        this.order = order;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public double getRemainingBalance() {
        return remainingBalance;
    }

    public OrderModel getOrder() {
        return order;
    }
}
