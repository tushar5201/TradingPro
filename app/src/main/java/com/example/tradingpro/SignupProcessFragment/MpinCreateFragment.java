package com.example.tradingpro.SignupProcessFragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.example.tradingpro.Constant_user_info;
import com.example.tradingpro.R;
import com.example.tradingpro.SignupProcessActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;

public class MpinCreateFragment extends Fragment {
    View view;
    EditText text1, text2, text3, text4, con1, con2, con3, con4;
    MaterialButton btnContinue, btnBack;
    String mpin, confMpin;
    RelativeLayout main;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_mpin_create, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        main = view.findViewById(R.id.main);
        text1 = view.findViewById(R.id.text1);
        text2 = view.findViewById(R.id.text2);
        text3 = view.findViewById(R.id.text3);
        text4 = view.findViewById(R.id.text4);
        con1 = view.findViewById(R.id.con1);
        con2 = view.findViewById(R.id.con2);
        con3 = view.findViewById(R.id.con3);
        con4 = view.findViewById(R.id.con4);

        btnContinue = view.findViewById(R.id.btnContinue);

        btnBack = view.findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> {
            Constant_user_info.currentStep -= 1;
            ((SignupProcessActivity) getActivity()).loadFragment(new IdInformationFragment());
        });

        btnContinue.setOnClickListener(v -> {
            mpin = text1.getText().toString() + text2.getText().toString() + text3.getText().toString() + text4.getText().toString();
            confMpin = con1.getText().toString() + con2.getText().toString() + con3.getText().toString() + con4.getText().toString();

            if (mpin.equals(confMpin) && !TextUtils.isEmpty(mpin) && !TextUtils.isEmpty(confMpin)) {
                SharedPreferences sp = getActivity().getSharedPreferences(Constant_user_info.SHARED_ID, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString(Constant_user_info.SHARED_MPIN, mpin);
                editor.commit();

                Constant_user_info.currentStep += 1;
                ((SignupProcessActivity) getActivity()).loadFragment(new BiometricsEnableFragment());
            } else {
                Snackbar.make(main, "Mpin not Matched", Snackbar.LENGTH_SHORT).show();
            }
        });
        text1.addTextChangedListener(otpbox1());
        text2.addTextChangedListener(otpbox2());
        text3.addTextChangedListener(otpbox3());
        con1.addTextChangedListener(otpConbox1());
        con2.addTextChangedListener(otpConbox2());
        con3.addTextChangedListener(otpConbox3());
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

    public TextWatcher otpConbox1() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if ((charSequence.toString().trim()).length() == 1) {
                    con2.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };
    }

    public TextWatcher otpConbox2() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if ((charSequence.toString().trim()).length() == 1) {
                    con3.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };
    }

    public TextWatcher otpConbox3() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if ((charSequence.toString().trim()).length() == 1) {
                    con4.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };
    }
}