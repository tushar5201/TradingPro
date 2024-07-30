package com.example.tradingpro;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SplashScreenActivity extends AppCompatActivity {

    ImageView imgLogo;
    TextView tvAppname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash_screen);


        Animation zoom;
        final Animation[] fade = new Animation[1];
        imgLogo = findViewById(R.id.imgLogo);
        tvAppname = findViewById(R.id.tvAppname);

        zoom = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom_anim);
        imgLogo.startAnimation(zoom);

        fade[0] = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_anim);
        tvAppname.startAnimation(fade[0]);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                tvAppname.setText("...");
                fade[0] = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_anim);
                tvAppname.startAnimation(fade[0]);
            }
        }, 2500);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent mainIntent = new Intent(SplashScreenActivity.this, LoginActivity.class);
                startActivity(mainIntent);
                finish();
            }
        }, 4200);

    }
}