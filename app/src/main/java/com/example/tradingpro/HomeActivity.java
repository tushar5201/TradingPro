package com.example.tradingpro;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class HomeActivity extends AppCompatActivity {

    CardView card1, card2, card3, card4, card5;
    ImageView img1, img2, img3, img4, img5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        card1 = findViewById(R.id.card1);
        card2 = findViewById(R.id.card2);
        card3 = findViewById(R.id.card3);
        card4 = findViewById(R.id.card4);
        card5 = findViewById(R.id.card5);

        img1 = findViewById(R.id.img1);
        img2 = findViewById(R.id.img2);
        img3 = findViewById(R.id.img3);
        img4 = findViewById(R.id.img4);
        img5 = findViewById(R.id.img5);

        BottomIcon1();

        card1.setOnClickListener(v -> {
            BottomIcon1();
        });

        card2.setOnClickListener(v -> {
            bottomIcon2();
        });

//        card3
        card3.setOnClickListener(v -> {
            bottomIcon3();
        });

        card4.setOnClickListener(v -> {
            bottomIcon4();
        });

        card5.setOnClickListener(v -> {
            bottomIcon5();
        });
    }

    private void BottomIcon1() {
        GradientDrawable backgroundDrawable = new GradientDrawable();
        backgroundDrawable.setCornerRadius(80f);
        card1.setBackground(backgroundDrawable);
        backgroundDrawable.setColor(Color.parseColor("#fee83f"));
        img1.setImageResource(R.drawable.market);

//            other icon bg remove
        GradientDrawable backgroundDrawableOther = new GradientDrawable();
        backgroundDrawableOther.setCornerRadius(80f);
        backgroundDrawableOther.setColor(Color.parseColor("#000000"));
        card2.setBackground(backgroundDrawableOther);
        card3.setBackground(backgroundDrawableOther);
        card4.setBackground(backgroundDrawableOther);
        card5.setBackground(backgroundDrawableOther);

//            other icon color change
        img2.setImageResource(R.drawable.watchlist_white);
        img3.setImageResource(R.drawable.search_white);
        img4.setImageResource(R.drawable.order_white);
        img5.setImageResource(R.drawable.portfolio_white);

    }

    private void bottomIcon2() {
        GradientDrawable backgroundDrawable = new GradientDrawable();
        backgroundDrawable.setCornerRadius(80f);
        card2.setBackground(backgroundDrawable);
        backgroundDrawable.setColor(Color.parseColor("#fee83f"));
        img2.setImageResource(R.drawable.watchlist);

//            other icon bg remove
        GradientDrawable backgroundDrawableOther = new GradientDrawable();
        backgroundDrawableOther.setCornerRadius(80f);
        backgroundDrawableOther.setColor(Color.parseColor("#000000"));
        card1.setBackground(backgroundDrawableOther);
        card3.setBackground(backgroundDrawableOther);
        card4.setBackground(backgroundDrawableOther);
        card5.setBackground(backgroundDrawableOther);

//            other icon color change
        img1.setImageResource(R.drawable.market_white);
        img3.setImageResource(R.drawable.search_white);
        img4.setImageResource(R.drawable.order_white);
        img5.setImageResource(R.drawable.portfolio_white);

    }

    private void bottomIcon3() {
        GradientDrawable backgroundDrawable = new GradientDrawable();
        backgroundDrawable.setCornerRadius(80f);
        card3.setBackground(backgroundDrawable);
        backgroundDrawable.setColor(Color.parseColor("#fee83f"));
        img3.setImageResource(R.drawable.search_);

//            other icon bg remove
        GradientDrawable backgroundDrawableOther = new GradientDrawable();
        backgroundDrawableOther.setCornerRadius(80f);
        backgroundDrawableOther.setColor(Color.parseColor("#000000"));
        card1.setBackground(backgroundDrawableOther);
        card2.setBackground(backgroundDrawableOther);
        card4.setBackground(backgroundDrawableOther);
        card5.setBackground(backgroundDrawableOther);

//            other icon color change
        img1.setImageResource(R.drawable.market_white);
        img2.setImageResource(R.drawable.watchlist_white);
        img4.setImageResource(R.drawable.order_white);
        img5.setImageResource(R.drawable.portfolio_white);

    }

    private void bottomIcon4() {
        GradientDrawable backgroundDrawable = new GradientDrawable();
        backgroundDrawable.setCornerRadius(80f);
        card4.setBackground(backgroundDrawable);
        backgroundDrawable.setColor(Color.parseColor("#fee83f"));
        img4.setImageResource(R.drawable.order);

//            other icon bg remove
        GradientDrawable backgroundDrawableOther = new GradientDrawable();
        backgroundDrawableOther.setCornerRadius(80f);
        backgroundDrawableOther.setColor(Color.parseColor("#000000"));
        card1.setBackground(backgroundDrawableOther);
        card2.setBackground(backgroundDrawableOther);
        card3.setBackground(backgroundDrawableOther);
        card5.setBackground(backgroundDrawableOther);

//            other icon color change
        img1.setImageResource(R.drawable.market_white);
        img2.setImageResource(R.drawable.watchlist_white);
        img3.setImageResource(R.drawable.search_white);
        img5.setImageResource(R.drawable.portfolio_white);

    }

    private void bottomIcon5() {
        GradientDrawable backgroundDrawable = new GradientDrawable();
        backgroundDrawable.setCornerRadius(80f);
        card5.setBackground(backgroundDrawable);
        backgroundDrawable.setColor(Color.parseColor("#fee83f"));
        img5.setImageResource(R.drawable.portfolio);

//            other icon bg remove
        GradientDrawable backgroundDrawableOther = new GradientDrawable();
        backgroundDrawableOther.setCornerRadius(80f);
        backgroundDrawableOther.setColor(Color.parseColor("#000000"));
        card1.setBackground(backgroundDrawableOther);
        card2.setBackground(backgroundDrawableOther);
        card3.setBackground(backgroundDrawableOther);
        card4.setBackground(backgroundDrawableOther);

//            other icon color change
        img1.setImageResource(R.drawable.market_white);
        img2.setImageResource(R.drawable.watchlist_white);
        img3.setImageResource(R.drawable.search_white);
        img4.setImageResource(R.drawable.order_white);

    }
}