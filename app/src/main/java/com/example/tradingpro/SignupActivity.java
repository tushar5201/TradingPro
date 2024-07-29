package com.example.tradingpro;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SignupActivity extends AppCompatActivity {

    TextView btnlogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);

        btnlogin = findViewById(R.id.tvlogin);

        btnlogin.setOnClickListener(v -> {
            Intent i1 = new Intent(SignupActivity.this,LoginActivity.class);
            startActivity(i1);
        });
    }
}