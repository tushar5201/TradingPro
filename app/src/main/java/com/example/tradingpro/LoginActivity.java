package com.example.tradingpro;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LoginActivity extends AppCompatActivity {

    Button btnLogin;
    TextView btnsignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        btnLogin = findViewById(R.id.btnLogin);
        btnsignup = findViewById(R.id.btnsignup);

        btnLogin.setOnClickListener(v-> {
//            Intent i1 = new Intent(LoginActivity.this, Data.class);
//            startActivity(i1);
        });

        btnsignup.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this,SignupActivity.class);
            startActivity(intent);
        });
    }
}