package com.example.tradingpro.SignupProcessFragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.tradingpro.Constant.Constant_user_info;
import com.example.tradingpro.Activity.LoginActivity;
import com.example.tradingpro.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class DisclaimerFragment extends Fragment {

    View view;
    MaterialButton btnContinue;
    CheckBox chkAgree;
    RelativeLayout main;
    String name, phone, email, password, address, mpin, fingerprint;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_disclaimer, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnContinue = view.findViewById(R.id.btnContinue);
        chkAgree = view.findViewById(R.id.chkAgree);
        main = view.findViewById(R.id.main);

        SharedPreferences sp = getActivity().getSharedPreferences(Constant_user_info.SHARED_ID, Context.MODE_PRIVATE);
        name = sp.getString(Constant_user_info.SHARED_USERNM, "");
        phone = sp.getString(Constant_user_info.SHARED_PHONE, "");
        email = sp.getString(Constant_user_info.SHARED_EMAIL, "");
        password = sp.getString(Constant_user_info.SHARED_PASS, "");
        address = sp.getString(Constant_user_info.SHARED_ADDRESS, "");
        mpin = sp.getString(Constant_user_info.SHARED_MPIN, "");
        fingerprint = sp.getString(Constant_user_info.SHARED_FINGERPRINT, "");



        btnContinue.setOnClickListener(v -> {
            if (chkAgree.isChecked()) {
                insert();
                SharedPreferences.Editor editor = sp.edit();
                editor.clear();
                editor.apply();
                startActivity(new Intent(getContext(), LoginActivity.class));
                Constant_user_info.isCheckFrag = "true";
            } else {
                Snackbar.make(main, "Please Agree Terms and Conditions", Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    //    record insert method
    public void insert() {
        Map<String, Object> map = new HashMap<>();
        map.put(Constant_user_info.KEY_USERNAME, name);
        map.put(Constant_user_info.KEY_PHONE, phone);
        map.put(Constant_user_info.KEY_EMAIL, email);
        map.put(Constant_user_info.KEY_PASSWORD, password);
        map.put(Constant_user_info.KEY_ADDRESS, address);
        map.put(Constant_user_info.KEY_MPIN, mpin);
        map.put(Constant_user_info.KEY_FINGERPRINT, fingerprint);

        FirebaseDatabase.getInstance().getReference().child(Constant_user_info.TABLE_NAME)
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