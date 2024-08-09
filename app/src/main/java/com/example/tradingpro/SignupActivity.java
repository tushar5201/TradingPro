package com.example.tradingpro;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class SignupActivity extends AppCompatActivity {

    TextView tvLogin;
    TextInputLayout textInputLayoutName, textInputLayoutEmail, textInputLayoutPhone, textInputLayoutPassword;
    TextInputEditText textInputEdName, textInputEdEmail, textInputEdPhone, textInputEdPassword;
    Button btnRegister;
    MaterialCheckBox chkRemember;
    String name, email, phone, password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);

        tvLogin = findViewById(R.id.tvlogin);
        textInputLayoutName = findViewById(R.id.textInputLayoutName);
        textInputLayoutEmail = findViewById(R.id.textInputLayoutEmail);
        textInputLayoutPhone = findViewById(R.id.textInputLayoutPhone);
        textInputLayoutPassword = findViewById(R.id.textInputLayoutPassword);
        textInputEdName = findViewById(R.id.textInputEdName);
        textInputEdEmail = findViewById(R.id.textInputEdEmail);
        textInputEdPhone = findViewById(R.id.textInputEdPhone);
        textInputEdPassword = findViewById(R.id.textInputEdPassword);
        btnRegister = findViewById(R.id.btnRegister);
        chkRemember = findViewById(R.id.chkRemember);

//        switch to login screen
        tvLogin.setOnClickListener(v -> {
            startActivity(new Intent(SignupActivity.this,LoginActivity.class));
        });

//        validation
        btnRegister.setOnClickListener(v-> {

            //        get text from edittext
            name = textInputEdName.getText().toString().trim();
            email = textInputEdEmail.getText().toString().trim();
            password = textInputEdPassword.getText().toString().trim();
            phone = textInputEdPhone.getText().toString().trim();

//            empty error
            if(TextUtils.isEmpty(name)) {
                textInputLayoutName.setError("Name Cannot be Empty");
                textInputLayoutName.setHelperText("");
            } else {
                textInputLayoutName.setError("");
                textInputLayoutName.setHelperText(getString(R.string.required));
            }
            if(TextUtils.isEmpty(email)) {
                textInputLayoutEmail.setError("Email Cannot be Empty");
                textInputLayoutName.setHelperText("");
            } else {
                textInputLayoutEmail.setError("");
                textInputLayoutEmail.setHelperText(getString(R.string.required));
            }
            if (TextUtils.isEmpty(phone)){
                textInputLayoutPhone.setError("Phone Cannot be Empty");
                textInputLayoutPhone.setError("");
            } else {
                textInputLayoutPhone.setError("");
                textInputLayoutPhone.setHelperText(getString(R.string.required));
            }
            if (TextUtils.isEmpty(password)){
                textInputLayoutPassword.setError("Password Cannot be Empty");
                textInputLayoutPassword.setHelperText("");
            } else {
                textInputLayoutPassword.setError("");
                textInputLayoutPassword.setHelperText(getString(R.string.required));
            }

//            phone number 10 digit validation
            if(textInputEdPhone.length() != 10) {
                textInputLayoutPhone.setHelperText("");
                textInputLayoutPhone.setError("Phone number Should be 10 Digit");
            } else {
                textInputLayoutPhone.setHelperText(getString(R.string.required));
                textInputLayoutPhone.setError("");
            }

//            user name validation
            if(!TextUtils.isEmpty(name) && name.matches("^[a-zA-Z]$")) {
                textInputLayoutName.setError("");
                textInputLayoutName.setHelperText(getString(R.string.required));
            } else {
                textInputLayoutName.setError("Please Enter Valid Username");
                textInputLayoutName.setHelperText("");
            }
        });
    }
}