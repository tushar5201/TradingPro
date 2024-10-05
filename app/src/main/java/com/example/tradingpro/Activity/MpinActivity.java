package com.example.tradingpro.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.tradingpro.Constant.Constant_user_info;
import com.example.tradingpro.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.Executor;

public class MpinActivity extends AppCompatActivity {

    TextView tvTitle;
    MaterialButton btnFingerprint, btnConfirm;
    BiometricPrompt biometricPrompt;
    BiometricPrompt.PromptInfo promptInfo;
    RelativeLayout main;
    EditText text1, text2, text3, text4;
    String mpin;
    ImageView backBtn;
    String emailOrPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mpin);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(30, systemBars.top, 30, 30);
            return insets;
        });


        tvTitle = findViewById(R.id.tvTitle);
        btnFingerprint = findViewById(R.id.btnFingerprint);
        btnConfirm = findViewById(R.id.btnConfirm);
        main = findViewById(R.id.main);
        text1 = findViewById(R.id.text1);
        text2 = findViewById(R.id.text2);
        text3 = findViewById(R.id.text3);
        text4 = findViewById(R.id.text4);
        backBtn = findViewById(R.id.backBtn);

//        fingerPrint();

        SharedPreferences sp = getSharedPreferences("Login", MODE_PRIVATE);
        String unm = sp.getString("unm", "");
        emailOrPhone = sp.getString("emailOrPhone", "");
        tvTitle.setText("\uD83D\uDC4B HI " + unm.toUpperCase());

        // Go to login when back click
//        backBtn.setOnClickListener(v -> {
//            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
//            finish();
//        });

        // Call fingerPrintCheck() to manage visibility of the button
//        fingerPrintCheck();


        // Initialize BiometricPrompt and PromptInfo
        Executor executor = ContextCompat.getMainExecutor(this);
        biometricPrompt = new BiometricPrompt(MpinActivity.this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                main.setVisibility(View.VISIBLE);
                startActivity(new Intent(MpinActivity.this, HomeActivity.class));
                finish();
            }

            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
            }
        });

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("TradingPro")
                .setDescription("Use Fingerprint To Login")
                .setDeviceCredentialAllowed(true) // This allows fallback to PIN/pattern/password if biometric fails
                .build();

        // Set the fingerprint button click listener
        btnFingerprint.setOnClickListener(v -> {
            if (biometricPrompt != null && promptInfo != null) {
                biometricPrompt.authenticate(promptInfo);
            }
        });


        // MPin confirmation logic
        btnConfirm.setOnClickListener(v -> {
            mpin = text1.getText().toString().trim() + text2.getText().toString().trim() + text3.getText().toString().trim() + text4.getText().toString().trim();
            try {
                Double phone = Double.parseDouble(emailOrPhone);
                verifyMpin(emailOrPhone, mpin);
            } catch (NumberFormatException err) {
                // Handle case when emailOrPhone is not a phone number
                verifyMpin(emailOrPhone, mpin);
            } catch (Error error) {
                Snackbar.make(main, "Error", Snackbar.LENGTH_SHORT).show();
            }
        });

        // OTP TextWatchers for MPIN
        text1.addTextChangedListener(otpbox1());
        text2.addTextChangedListener(otpbox2());
        text3.addTextChangedListener(otpbox3());
        text4.addTextChangedListener(otpbox4());
    }

    public void fingerPrint() {
        //        fingerprint
        BiometricManager biometricManager = BiometricManager.from(this);

        switch (biometricManager.canAuthenticate()) {
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                Snackbar.make(main, "Don't have fingerprint", Snackbar.LENGTH_SHORT).show();
                break;
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                Snackbar.make(main, "Not working", Snackbar.LENGTH_SHORT).show();
                break;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                Snackbar.make(main, "No figerprint assign", Snackbar.LENGTH_SHORT).show();
                break;
        }

        Executor executor = ContextCompat.getMainExecutor(this);

        biometricPrompt = new BiometricPrompt(MpinActivity.this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
//                Snackbar.make(main, "Login Success", Snackbar.LENGTH_SHORT).show();
                main.setVisibility(View.VISIBLE);
                startActivity(new Intent(MpinActivity.this, HomeActivity.class));
                finish();
            }

            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
            }
        });

        promptInfo = new BiometricPrompt.PromptInfo.Builder().setTitle("TradingPro")
                .setDescription("Use Fingerprint To Login").setDeviceCredentialAllowed(true).build();
        biometricPrompt.authenticate(promptInfo);
        finish();

    }

    public void fingerPrintCheck() {
        //        check fingerprint
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("user_info");
        Query query = databaseReference.orderByChild(Constant_user_info.KEY_PHONE).equalTo(emailOrPhone);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot data : snapshot.getChildren()) {
                        String finger = data.child(Constant_user_info.KEY_FINGERPRINT).getValue(String.class);

                        if (finger.equals("true")) {
                            fingerPrint();
                        } else {
                            btnFingerprint.setVisibility(View.INVISIBLE);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Snackbar.make(main, "Something went wrong ", BaseTransientBottomBar.LENGTH_SHORT).show();
            }
        });

    }

    public void verifyMpin(String emailOrPhone, String inputMpin) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("user_info");
        Query query;
        try {
            Double isPhone = Double.parseDouble(emailOrPhone);
            query = databaseReference.orderByChild(Constant_user_info.KEY_PHONE).equalTo(emailOrPhone);
        } catch (NumberFormatException err) {
            query = databaseReference.orderByChild(Constant_user_info.KEY_EMAIL).equalTo(emailOrPhone);
        }
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot data : snapshot.getChildren()) {
                        String mpin = data.child(Constant_user_info.KEY_MPIN).getValue(String.class);

                        if (mpin.equals(inputMpin)) {
                            startActivity(new Intent(MpinActivity.this, HomeActivity.class));
                            finish();
                        } else {
                            Snackbar.make(main, "Invalid MPIN ", Snackbar.LENGTH_SHORT).show();
                        }
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Snackbar.make(main, "Something went wrong ", BaseTransientBottomBar.LENGTH_SHORT).show();
            }
        });
    }

    public TextWatcher otpbox1() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if ((charSequence.toString().trim()).length() == 1) {
                    text2.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };
    }

    public TextWatcher otpbox2() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if ((charSequence.toString().trim()).length() == 1) {
                    text3.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };
    }

    public TextWatcher otpbox3() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if ((charSequence.toString().trim()).length() == 1) {
                    text4.requestFocus();

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };
    }

    public TextWatcher otpbox4() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mpin = text1.getText().toString().trim() + text2.getText().toString().trim() + text3.getText().toString().trim() + text4.getText().toString().trim();
                verifyMpin(emailOrPhone, mpin);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        };
    }

}