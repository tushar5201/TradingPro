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
import android.widget.Toast;

import com.example.tradingpro.Constant_user_info;
import com.example.tradingpro.R;
import com.example.tradingpro.SignupProcessActivity;
import com.google.android.material.button.MaterialButton;

public class IdInformationFragment extends Fragment {
    View view;
    MaterialButton btnContinue, btnBack;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_id_information, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        SharedPreferences sp = getContext().getSharedPreferences(Constant_user_info.SHARED_ID, Context.MODE_PRIVATE);
//        String address = sp.getString(Constant_user_info.SHARED_ADDRESS, "");
//        Toast.makeText(getContext(), address, Toast.LENGTH_SHORT).show();
        btnContinue = view.findViewById(R.id.btnContinue);
        btnBack = view.findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> {
            Constant_user_info.currentStep -= 1;
            PersonalInformationFragment personalInformationFragment = new PersonalInformationFragment();
            ((SignupProcessActivity) getActivity()).loadFragment(personalInformationFragment);
        });

        btnContinue.setOnClickListener(v -> {
            Constant_user_info.currentStep += 1;
            MpinCreateFragment mpinCreateFragment = new MpinCreateFragment();
            ((SignupProcessActivity) getActivity()).loadFragment(mpinCreateFragment);
        });
    }
}