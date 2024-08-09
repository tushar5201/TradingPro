package com.example.tradingpro;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends AppCompatActivity {

    Button btnLogin;
    TextView btnsignup;
    TextInputLayout textInputLayoutEmailOrPhnLog, textInputLayoutPasswordLog;
    TextInputEditText textInputEdEmailOrPhnLog, textInputEdPasswordLog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

//        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES); //For night mode theme

        btnLogin = findViewById(R.id.btnLogin);
        btnsignup = findViewById(R.id.btnsignup);
        textInputLayoutEmailOrPhnLog = findViewById(R.id.textInputLayoutEmailOrPhnLog);
        textInputLayoutPasswordLog = findViewById(R.id.textInputLayoutPasswordLog);
        textInputEdEmailOrPhnLog = findViewById(R.id.textInputEdEmailOrPhnLog);
        textInputEdPasswordLog = findViewById(R.id.textInputEdPasswordLog);

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