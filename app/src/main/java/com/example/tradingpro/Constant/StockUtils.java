package com.example.tradingpro.Constant;

import java.text.DecimalFormat;
import java.util.Locale;

public class StockUtils {
    private static final DecimalFormat DEC_FORMAT = new DecimalFormat("###,##0.00");
    private static final DecimalFormat SHORT_FORMAT = new DecimalFormat("###.##");

    public static double safeParseDouble(String value, double defaultValue) {
        if (value == null) return defaultValue;
        try {
            String cleaned = value.replaceAll("[^0-9.-]", "").trim();
            if (cleaned.isEmpty() || cleaned.equals("-") || cleaned.equals(".")) return defaultValue;
            return Double.parseDouble(cleaned);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static String formatPrice(double price) {
        return DEC_FORMAT.format(price);
    }

    public static String formatPoints(double points) {
        if (points > 0) {
            return "+" + DEC_FORMAT.format(points);
        } else if (points < 0) {
            return DEC_FORMAT.format(points);
        } else {
            return "0.00";
        }
    }

    public static String formatPercentage(double percentage) {
        if (percentage > 0) {
            return "(+" + SHORT_FORMAT.format(percentage) + "%)";
        } else if (percentage < 0) {
            return "(" + SHORT_FORMAT.format(percentage) + "%)";
        } else {
            return "(0.00%)";
        }
    }

    public static String getCurrencySymbol(String currency, String symbol) {
        if (currency != null && currency.equalsIgnoreCase("INR")) {
            return "₹";
        }
        if (symbol != null && (symbol.endsWith(".NS") || symbol.endsWith(".BO") || symbol.startsWith("^NSE") || symbol.startsWith("^BSE"))) {
            return "₹";
        }
        if (currency != null && currency.equalsIgnoreCase("USD")) {
            return "$";
        }
        return symbol != null && (symbol.endsWith(".NS") || symbol.endsWith(".BO")) ? "₹" : "$";
    }

    public static String cleanDisplaySymbol(String symbol) {
        if (symbol == null) return "";
        if (symbol.endsWith(".NS")) {
            return symbol.substring(0, symbol.length() - 3);
        }
        if (symbol.endsWith(".BO")) {
            return symbol.substring(0, symbol.length() - 3);
        }
        return symbol;
    }
}
