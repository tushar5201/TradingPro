package com.example.tradingpro;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignupActivity extends AppCompatActivity {

    TextView tvLogin;
    TextInputLayout textInputLayoutName, textInputLayoutEmail, textInputLayoutPhone, textInputLayoutPassword;
    TextInputEditText textInputEdName, textInputEdEmail, textInputEdPhone, textInputEdPassword;
    Button btnRegister;
    MaterialCheckBox chkRemember;
    String name, email, phone, password;
    boolean resultCondition;
    RelativeLayout main;


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
        main = findViewById(R.id.main);

//        switch to login screen
        tvLogin.setOnClickListener(v -> {
            startActivity(new Intent(SignupActivity.this, LoginActivity.class));
        });

//        name regex validation on addtext change listner
        textInputEdName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String nameEd = charSequence.toString();
                Pattern pattern = Pattern.compile("^[a-zA-Z ]+$");
                Matcher matcher = pattern.matcher(nameEd);
                boolean res = matcher.find();
                if (res) {
                    textInputLayoutName.setError("");
                    textInputLayoutName.setHelperText("Valid Username");
                    resultCondition = true;
                } else {
                    textInputLayoutName.setError("Not Valid Username");
                    textInputLayoutName.setHelperText("");
                    resultCondition = false;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

//         email regex validation on addtext change listner
        textInputEdEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String emailEd = charSequence.toString();
                Pattern pattern = Pattern.compile("^[a-z][a-z0-9._\\-]+[@][a-z]+\\.[a-z]{2,3}$");
                Matcher matcher = pattern.matcher(emailEd);
                boolean isValidEmail = matcher.find();

                if (isValidEmail) {
                    textInputLayoutEmail.setHelperText("Valid email");
                    textInputLayoutEmail.setError("");
                    resultCondition = true;

                } else {
                    textInputLayoutEmail.setError("Not valid email");
                    textInputLayoutEmail.setHelperText("");
                    resultCondition = false;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

//      phone number
        textInputEdPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String phnEd = charSequence.toString();
                if(!phnEd.isEmpty()) {
                    textInputLayoutPhone.setError("");
                    textInputLayoutPhone.setHelperText(getString(R.string.required));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

//        password
        textInputEdPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                password(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

//        validation on btn click
        btnRegister.setOnClickListener(v -> {
            //        get text from edittext
            name = textInputEdName.getText().toString().trim();
            email = textInputEdEmail.getText().toString().trim();
            password = textInputEdPassword.getText().toString().trim();
            phone = textInputEdPhone.getText().toString().trim();

//            empty error
            if(!TextUtils.isEmpty(name) || !TextUtils.isEmpty(phone) || !TextUtils.isEmpty(email) || !TextUtils.isEmpty(password)) {
                resultCondition = true;

            }
            else {
                if (TextUtils.isEmpty(name)) {
                    textInputLayoutName.setError("Name Cannot be Empty");
                    textInputLayoutName.setHelperText("");
                    resultCondition = false;
                }
                if (TextUtils.isEmpty(email)) {
                    textInputLayoutEmail.setError("Email Cannot be Empty");
                    textInputLayoutEmail.setHelperText("");
                    resultCondition = false;
                }
                if (TextUtils.isEmpty(phone)) {
                    textInputLayoutPhone.setError("Phone Cannot be Empty");
                    textInputLayoutPhone.setHelperText("");
                    resultCondition = false;
                }
                if (TextUtils.isEmpty(password)) {
                    textInputLayoutPassword.setError("Password Cannot be Empty");
                    textInputLayoutPassword.setHelperText("");
                    resultCondition = false;
                }
            }

//            phone number 10 digit validation
            if (textInputEdPhone.length() != 10 && textInputEdPhone.length() >= 1) {
                textInputLayoutPhone.setHelperText("");
                textInputLayoutPhone.setError("Phone number Should be 10 Digit");
                resultCondition = false;
            } else {
                if(textInputEdPhone.length() == 10) {
                    resultCondition = true;
                }

            }

            String vidhanshu = String.valueOf(resultCondition);
            Toast.makeText(this, vidhanshu, Toast.LENGTH_SHORT).show();

//            Snackbar.make(main,vidhanshu, Snackbar.LENGTH_SHORT).show();

//            if all condition satisfied then add data to firebase
            if(resultCondition) {
//                insert();
            }
        });


    }

//    password validation method
    public void password(String passEd) {
        Boolean temp = true;
        if(!TextUtils.isEmpty(passEd)) {
            textInputLayoutPassword.setError("");
            textInputLayoutPassword.setHelperText(getString(R.string.required));
        }

        if(passEd.length() >= 8) {
//                    this will find special character
            Pattern patternSpec = Pattern.compile("^.*[^a-zA-Z0-9].*$");
            Matcher matcherSpec = patternSpec.matcher(passEd);
            boolean resultSpec = matcherSpec.find();

            if(!resultSpec) {
                textInputLayoutPassword.setError("Required Special Character");
                textInputLayoutPassword.setHelperText("");
                temp = false;
                resultCondition = false;
            }

//                    this will find uppercase character
            Pattern patternUpper = Pattern.compile("^.*[A-Z].*$");
            Matcher matcherUpper = patternUpper.matcher(passEd);
            boolean resultUpper = matcherUpper.find();

            if(!resultUpper) {
                textInputLayoutPassword.setError("Required Upper Character");
                textInputLayoutPassword.setHelperText("");
                temp = false;
                resultCondition = false;
            }

//                    this will find digit in password string
            Pattern patternDigit = Pattern.compile("^.*[0-9].*$");
            Matcher matcherDigit = patternDigit.matcher(passEd);
            Boolean resultDigit = matcherDigit.find();

            if(!resultDigit) {
                textInputLayoutPassword.setError("Required Number");
                textInputLayoutPassword.setHelperText("");
                temp = false;
                resultCondition = false;
            }
//                    for strong password msg
            if(temp) {
                textInputLayoutPassword.setError("");
                textInputLayoutPassword.setHelperText("Strong Password");
                resultCondition = true;
            }
        } else {
            textInputLayoutPassword.setError("Required atleast 8 Character");
            textInputLayoutPassword.setHelperText("");
            resultCondition = false;
        }

    }

//    record insert method
    public void insert() {
        Map<String, Object> map = new HashMap<>();
        map.put("username",name);
        map.put("phone",phone);
        map.put("email",email);
        map.put("password",password);

        FirebaseDatabase.getInstance().getReference().child("user_info")
                .push()
                .setValue(map)
                .addOnSuccessListener(sl -> {
                    Snackbar.make(main, "Successfully Inserted", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(fl -> {
                    Snackbar.make(main, "Something went wrong, please re-enter data!", Toast.LENGTH_SHORT).show();
                });

    }
}
