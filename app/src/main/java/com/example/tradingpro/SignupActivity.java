package com.example.tradingpro;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tradingpro.SignupProcessFragment.PersonalInformationFragment;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class SignupActivity extends AppCompatActivity {

    TextView tvLogin;
    TextInputLayout textInputLayoutName, textInputLayoutEmail, textInputLayoutPhone, textInputLayoutPassword;
    TextInputEditText textInputEdName, textInputEdEmail, textInputEdPhone, textInputEdPassword;
    Button btnRegister;
    MaterialCheckBox chkRemember;
    String name, email, phone, password;
    RelativeLayout main;
    boolean flag;
    ProgressBar progressBar;


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
        progressBar = findViewById(R.id.progressBar);
        main = findViewById(R.id.main);

//        switch to login screen
        tvLogin.setOnClickListener(v -> {
            startActivity(new Intent(SignupActivity.this, LoginActivity.class));
        });


        btnRegister.setOnClickListener(v -> {
            //        get text from edittext
            name = textInputEdName.getText().toString().trim();
            email = textInputEdEmail.getText().toString().trim();
            password = textInputEdPassword.getText().toString().trim();
            phone = textInputEdPhone.getText().toString().trim();

            if (isValidAllField()) {
                if (chkRemember.isChecked()) {
                    emailFoundValidate();
                    if (flag) {
                        sendOtp();
                        insertShared();
                    }
                } else {
                    Snackbar.make(main, "Please accpect terms and conditionn", Snackbar.LENGTH_SHORT).show();
                }
            } else {
                Snackbar.make(main, "Please correct the errors", Snackbar.LENGTH_SHORT).show();
            }
        });

        textInputEdName.addTextChangedListener(createNameTextWatcher());
        textInputEdEmail.addTextChangedListener(createEmailTextWatcher());
        textInputEdPhone.addTextChangedListener(createPhoneTextWatcher());
        textInputEdPassword.addTextChangedListener(createPasswordTextWatcher());
    }

    public boolean isValidAllField() {
        boolean isNameValid = validateName();
        boolean isEmailValid = validateEmail();
        boolean isPhoneValid = validatePhone();
        boolean isPasswordValid = validatePassword();

        return isNameValid && isEmailValid && isPhoneValid && isPasswordValid;
    }

    //    create textwatcher for all field(NAME)
    public TextWatcher createNameTextWatcher() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                validateName();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };
    }

    //    EMAIL
    public TextWatcher createEmailTextWatcher() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                validateEmail();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };
    }

    //    PHONE
    public TextWatcher createPhoneTextWatcher() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                validatePhone();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };
    }

    //    PASSWORD
    public TextWatcher createPasswordTextWatcher() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                validatePassword();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };
    }

    //    name validation
    public boolean validateName() {
        String nameEd = textInputEdName.getText().toString().trim();
        if (TextUtils.isEmpty(nameEd)) {
            textInputLayoutName.setError("Name cannot be empty");
            return false;
        } else if (!nameEd.matches("^[a-zA-Z ]+$")) {
            textInputLayoutName.setError("Not a valid username");
            return false;
        } else {
            textInputLayoutName.setError(null);
            textInputLayoutName.setHelperText("Valid Username");
            return true;
        }
    }

    // Email validation
    public boolean validateEmail() {
        String emailEd = textInputEdEmail.getText().toString().trim();
        if (TextUtils.isEmpty(emailEd)) {
            textInputLayoutEmail.setError("Email cannot be empty");
            return false;
        } else if (!emailEd.matches("^[a-z][a-z0-9._\\-]+[@][a-z]+\\.[a-z]{2,3}$")) {
            textInputLayoutEmail.setError("Not a valid email");
            return false;
        } else {
            textInputLayoutEmail.setError(null);
            textInputLayoutEmail.setHelperText("Valid email");
            return true;
        }
    }

    // Phone validation
    private boolean validatePhone() {
        String phoneEd = textInputEdPhone.getText().toString().trim();
        if (TextUtils.isEmpty(phoneEd)) {
            textInputLayoutPhone.setError("Phone cannot be empty");
            return false;
        } else if (phoneEd.length() != 10) {
            textInputLayoutPhone.setError("Phone number should be 10 digits");
            return false;
        } else {
            textInputLayoutPhone.setError(null);
            textInputLayoutPhone.setHelperText("Valid phone number");
            return true;
        }
    }

    // Password validation
    private boolean validatePassword() {
        String passEd = textInputEdPassword.getText().toString().trim();
        if (TextUtils.isEmpty(passEd)) {
            textInputLayoutPassword.setError("Password cannot be empty");
            return false;
        } else if (passEd.length() < 8) {
            textInputLayoutPassword.setError("Password must be at least 8 characters");
            return false;
        } else if (!passEd.matches(".*[A-Z].*")) {
            textInputLayoutPassword.setError("Password must contain an uppercase letter");
            return false;
        } else if (!passEd.matches(".*[0-9].*")) {
            textInputLayoutPassword.setError("Password must contain a number");
            return false;
        } else if (!passEd.matches(".*[^a-zA-Z0-9].*")) {
            textInputLayoutPassword.setError("Password must contain a special character");
            return false;
        } else {
            textInputLayoutPassword.setError(null);
            textInputLayoutPassword.setHelperText("Strong Password");
            return true;
        }
    }

    public void emailFoundValidate() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(Constant_user_info.TABLE_NAME);
        Query queryMail = databaseReference.orderByChild(Constant_user_info.KEY_EMAIL).equalTo(email);
        Query queryPhn = databaseReference.orderByChild(Constant_user_info.KEY_PHONE).equalTo(phone);


        queryMail.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    flag = false;
                    Snackbar.make(main, "Email is already registered", BaseTransientBottomBar.LENGTH_SHORT).show();
                } else {
                    flag = true;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Snackbar.make(main, "Something went wrong ", BaseTransientBottomBar.LENGTH_SHORT).show();
            }
        });

        queryPhn.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    flag = false;
                    Snackbar.make(main, "Phone number is already registered", BaseTransientBottomBar.LENGTH_SHORT).show();
                } else {
                    flag = true;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Snackbar.make(main, "Something went wrong ", BaseTransientBottomBar.LENGTH_SHORT).show();

            }
        });
    }

    public void sendOtp() {
        progressBar.setVisibility(View.VISIBLE);
        btnRegister.setVisibility(View.INVISIBLE);
        Toast.makeText(SignupActivity.this, "hello", Toast.LENGTH_SHORT).show();
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91" + phone,
                60,
                TimeUnit.SECONDS,
                SignupActivity.this,
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        progressBar.setVisibility(View.GONE);
                        btnRegister.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        progressBar.setVisibility(View.GONE);
                        btnRegister.setVisibility(View.VISIBLE);
                        Snackbar.make(main, e.getMessage(), Snackbar.LENGTH_SHORT).show();
                        Toast.makeText(SignupActivity.this, "verify fail", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        progressBar.setVisibility(View.GONE);
                        btnRegister.setVisibility(View.VISIBLE);
                        Intent intent = new Intent(SignupActivity.this, OtpVerifyActivity.class);
                        intent.putExtra("phone", phone);
                        intent.putExtra("backendOtp", s);
                        startActivity(intent);
                    }
                }
        );
    }

    public void insertShared() {
        SharedPreferences sp = getSharedPreferences(Constant_user_info.SHARED_ID, MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(Constant_user_info.SHARED_USERNM, name);
        editor.putString(Constant_user_info.SHARED_EMAIL, email);
        editor.putString(Constant_user_info.SHARED_PASS, password);
        editor.putString(Constant_user_info.SHARED_PHONE, phone);
        editor.commit();
    }
}
