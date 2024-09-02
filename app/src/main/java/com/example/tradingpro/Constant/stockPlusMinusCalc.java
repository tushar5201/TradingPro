package com.example.tradingpro.Constant;

import android.graphics.Color;

import com.example.tradingpro.Model.StockPriceModel;

import java.text.DecimalFormat;

public class stockPlusMinusCalc {


    public String plusMinusPer(String price, String previousClose) {
        String plusMinusPoints, plusMinusPercentage;

        DecimalFormat decim = new DecimalFormat("###.##");

        plusMinusPoints = decim.format(Double.parseDouble(price) - Double.parseDouble(previousClose));
        plusMinusPercentage = "  (" + decim.format(((Double.parseDouble(plusMinusPoints) * 100) / Double.parseDouble(price))) + "%)";

        return null;
    }
}
