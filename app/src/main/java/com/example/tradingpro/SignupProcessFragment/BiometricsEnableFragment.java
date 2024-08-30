package com.example.tradingpro.SignupProcessFragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.tradingpro.Constant_user_info;
import com.example.tradingpro.R;
import com.example.tradingpro.SignupProcessActivity;
import com.google.android.material.button.MaterialButton;

public class BiometricsEnableFragment extends Fragment {

    View view;
    MaterialButton btnContinue;
    RadioButton radioEnable, radioNotnow;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_biometrics_enable, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnContinue = view.findViewById(R.id.btnContinue);
        radioEnable = view.findViewById(R.id.radioEnable);
        radioNotnow = view.findViewById(R.id.radioNotnow);

        btnContinue.setOnClickListener(v -> {
            SharedPreferences sp = getActivity().getSharedPreferences(Constant_user_info.SHARED_ID, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString(Constant_user_info.SHARED_FINGERPRINT, String.valueOf(radioEnable.isChecked()));
            editor.commit();

            Constant_user_info.currentStep += 1;

            ((SignupProcessActivity) getActivity()).loadFragment(new DisclaimerFragment());
        });
    }
}