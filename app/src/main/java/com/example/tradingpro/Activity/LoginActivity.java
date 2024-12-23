package com.example.tradingpro.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.constraintlayout.motion.widget.MotionScene;

import com.example.tradingpro.Constant.Constant_user_info;
import com.example.tradingpro.R;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    Button btnLogin;
    TextView btnsignup;
    TextInputLayout textInputLayoutEmailOrPhnLog, textInputLayoutPasswordLog;
    TextInputEditText textInputEdEmailOrPhnLog, textInputEdPasswordLog;
    MotionLayout main;
    String emailOrPhone, password, user, pass, username;
    RelativeLayout mainRelative;
    MaterialCheckBox chkRemember;
    Boolean flag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        mainRelative = findViewById(R.id.realtiveLogin);
        btnLogin = findViewById(R.id.btnLogin);
        btnsignup = findViewById(R.id.btnsignup);
        textInputLayoutEmailOrPhnLog = findViewById(R.id.textInputLayoutEmailOrPhnLog);
        textInputLayoutPasswordLog = findViewById(R.id.textInputLayoutPasswordLog);
        textInputEdEmailOrPhnLog = findViewById(R.id.textInputEdEmailOrPhnLog);
        textInputEdPasswordLog = findViewById(R.id.textInputEdPasswordLog);
        chkRemember = findViewById(R.id.chkRemember);
        main = findViewById(R.id.main);

//        this for motion layout - second time open splash won't open
        if (!Constant_user_info.isCheckSplash) {
            MotionScene.Transition transition = main.getTransition(R.id.transition_splash);
            transition.setAutoTransition(MotionScene.Transition.AUTO_JUMP_TO_END);
        } else {
            MotionScene.Transition transition = main.getTransition(R.id.transition_splash);
            transition.setAutoTransition(MotionScene.Transition.AUTO_ANIMATE_TO_END);
        }

        SharedPreferences sp = getSharedPreferences(Constant_user_info.SHARED_LOGIN_ID, MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        if (sp.contains("unm")) {
            startActivity(new Intent(getApplicationContext(), MpinActivity.class));
            finish();
        }

        btnsignup.setOnClickListener(v -> {
            Constant_user_info.isCheckSplash = false;
            Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
            startActivity(intent);
            finish();
        });

        btnLogin.setOnClickListener(v -> {
            emailOrPhone = textInputEdEmailOrPhnLog.getText().toString().trim();
            password = textInputEdPasswordLog.getText().toString().trim();

            if (isValidAllField()) {
                try {
                    Double isPhone = Double.parseDouble(emailOrPhone);
                    isPhone.getClass();
                    checkPhone(editor);
                } catch (NumberFormatException err) {
                    checkEmail(editor);
                    if (flag && chkRemember.isChecked()) {
                        Toast.makeText(this, "hello", Toast.LENGTH_SHORT).show();
                        editor.putString(Constant_user_info.SHARED_LOGIN_USERNM, username);
                        editor.putString(Constant_user_info.SHARED_LOGIN_EMAILORPHONE, emailOrPhone);
                        editor.commit();
                        startActivity(new Intent(getApplicationContext(), MpinActivity.class));
                        finish();
                    } else if (flag && !chkRemember.isChecked()) {
                        Toast.makeText(this, "hii", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), MpinActivity.class));
                        finish();
                    } else {
//                        Toast.makeText(this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                    }
                } catch (Error error) {
                    Snackbar.make(mainRelative, "Something went wrong!", Snackbar.LENGTH_SHORT).show();
                }
            }
        });
        textInputEdEmailOrPhnLog.addTextChangedListener(createEmailWatcher());
        textInputEdPasswordLog.addTextChangedListener(createpasswordWatcher());
    }

    public void checkEmail(SharedPreferences.Editor editor) {
        user = textInputEdEmailOrPhnLog.getText().toString().trim();
        pass = textInputEdPasswordLog.getText().toString().trim();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Constant_user_info.TABLE_NAME);
        Query query = reference.orderByChild(Constant_user_info.KEY_EMAIL).equalTo(user);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot data : snapshot.getChildren()) {
                        username = data.child(Constant_user_info.KEY_USERNAME).getValue(String.class);
                        String password = data.child(Constant_user_info.KEY_PASSWORD).getValue(String.class);

                        if (password.equals(pass)) {
                            flag = true;
                            if (chkRemember.isChecked()) {
                                editor.putString("unm", username);
                                editor.putString("emailOrPhone", emailOrPhone);
                                editor.commit();
                                startActivity(new Intent(getApplicationContext(), MpinActivity.class));
                                finish();
                            } else if (flag && !chkRemember.isChecked()) {
                                startActivity(new Intent(getApplicationContext(), MpinActivity.class));
                                finish();
                            }
                        } else {
                            Snackbar.make(mainRelative, "Invalid credentials ", Snackbar.LENGTH_SHORT).show();
                            flag = false;
                        }
                    }
                } else {
                    Snackbar.make(mainRelative, "User Does't Exist ", Snackbar.LENGTH_SHORT).show();
                    textInputLayoutEmailOrPhnLog.requestFocus();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Snackbar.make(mainRelative, "Something went wrong ", BaseTransientBottomBar.LENGTH_SHORT).show();
            }
        });
    }

    public void checkPhone(SharedPreferences.Editor editor) {

        user = textInputEdEmailOrPhnLog.getText().toString().trim();
        pass = textInputEdPasswordLog.getText().toString().trim();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Constant_user_info.TABLE_NAME);
        Query queryphone = reference.orderByChild("phone").equalTo(user);

        queryphone.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot data : snapshot.getChildren()) {
                        username = data.child(Constant_user_info.KEY_USERNAME).getValue(String.class);
                        String password = data.child(Constant_user_info.KEY_PASSWORD).getValue(String.class);

                        if (password.equals(pass)) {
                            flag = true;
                            if (chkRemember.isChecked()) {
                                editor.putString("unm", username);
                                editor.putString("emailOrPhone", emailOrPhone);
                                editor.commit();
                                startActivity(new Intent(getApplicationContext(), MpinActivity.class));
                                finish();
                            } else if (flag && !chkRemember.isChecked()) {
                                startActivity(new Intent(getApplicationContext(), MpinActivity.class));
                                finish();
                            }
                        } else {
                            Snackbar.make(mainRelative, "Invalid credentials ", Snackbar.LENGTH_SHORT).show();
                            flag = false;
                        }
                    }
                } else {
                    Snackbar.make(mainRelative, "User Does't Exist ", Snackbar.LENGTH_SHORT).show();
                    textInputLayoutEmailOrPhnLog.requestFocus();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Snackbar.make(mainRelative, "Something went wrong ", BaseTransientBottomBar.LENGTH_SHORT).show();
            }
        });
    }

    public boolean isValidAllField() {
        boolean isEmailValid = validemailphone();
        boolean isPasswordValid = validpassword();

        return isEmailValid && isPasswordValid;
    }

    public TextWatcher createEmailWatcher() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                validemailphone();
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        };
    }

    public TextWatcher createpasswordWatcher() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                validpassword();
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        };
    }

    public boolean validemailphone() {
        String emailED = textInputEdEmailOrPhnLog.getText().toString();
        if (TextUtils.isEmpty(emailED)) {
            textInputLayoutEmailOrPhnLog.setError("Email or Phone can't be Empty");
            return false;
        } else {
            textInputLayoutEmailOrPhnLog.setError(null);
            return true;
        }
    }

    public boolean validpassword() {
        String passwordEd = textInputEdPasswordLog.getText().toString();
        if (TextUtils.isEmpty(passwordEd)) {
            textInputLayoutPasswordLog.setError("Password can't be Empty");
            return false;
        } else {
            textInputLayoutPasswordLog.setError(null);
            return true;
        }
    }

}