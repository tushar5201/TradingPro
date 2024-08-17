package com.example.tradingpro;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.constraintlayout.motion.widget.MotionScene;
import androidx.constraintlayout.widget.ConstraintSet;
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
    String emailOrPhone, password;
//    boolean isCheckSplash = true;
    MotionLayout main;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);


        btnLogin = findViewById(R.id.btnLogin);
        btnsignup = findViewById(R.id.btnsignup);
        textInputLayoutEmailOrPhnLog = findViewById(R.id.textInputLayoutEmailOrPhnLog);
        textInputLayoutPasswordLog = findViewById(R.id.textInputLayoutPasswordLog);
        textInputEdEmailOrPhnLog = findViewById(R.id.textInputEdEmailOrPhnLog);
        textInputEdPasswordLog = findViewById(R.id.textInputEdPasswordLog);
        main = findViewById(R.id.main);

//        main.setTransition(R.id.transition_splash);
//        main.setProgress(1.0f);

        if(!Constant_user_info.isCheckSplash) {
            MotionScene.Transition transition = main.getTransition(R.id.transition_splash);
            transition.setAutoTransition(MotionScene.Transition.AUTO_JUMP_TO_END);
        } else {
            MotionScene.Transition transition = main.getTransition(R.id.transition_splash);
            transition.setAutoTransition(MotionScene.Transition.AUTO_ANIMATE_TO_END);
        }

        btnLogin.setOnClickListener(v-> {
            emailOrPhone = textInputEdEmailOrPhnLog.getText().toString().trim();
            password = textInputEdPasswordLog.getText().toString().trim();
        });

        btnsignup.setOnClickListener(v -> {
            Constant_user_info.isCheckSplash = false;
            Intent intent = new Intent(LoginActivity.this,SignupActivity.class);
            startActivity(intent);
        });
    }
}