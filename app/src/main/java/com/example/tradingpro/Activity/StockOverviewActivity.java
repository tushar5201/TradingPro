package com.example.tradingpro.Activity;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager.widget.ViewPager;

import com.example.tradingpro.Adapter.TabAdapter;
import com.example.tradingpro.R;
import com.google.android.material.tabs.TabLayout;

public class StockOverviewActivity extends AppCompatActivity {

    TabLayout tabstock;
    ViewPager viewpagerstock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_stock_overview);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tabstock = findViewById(R.id.tabstock);
        viewpagerstock = findViewById(R.id.viewpagerstock);

        TabAdapter adapter = new TabAdapter(getSupportFragmentManager());
        viewpagerstock.setAdapter(adapter);

        tabstock.setupWithViewPager(viewpagerstock);
    }
}