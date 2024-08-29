package com.example.tradingpro.SignupProcessFragment;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.tradingpro.Constant_user_info;
import com.example.tradingpro.R;
import com.example.tradingpro.SignupActivity;
import com.example.tradingpro.SignupProcessActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class PersonalInformationFragment extends Fragment {
    View view;
    TextInputLayout resilayout, addlayout, citylayout, statelayout, countrylayout, codelayout;
    TextInputEditText edresi, edaddress, edcity, edstate, edcountry, edcode;
    String temp;
    MaterialButton btnContinue;
    FragmentManager fragmentManager;
    String address, city, country, state, pinCode;
    String fullAddress;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_personal_information, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        resilayout = view.findViewById(R.id.resilayout);
        addlayout = view.findViewById(R.id.addlayout);
        citylayout = view.findViewById(R.id.citylayout);
        statelayout = view.findViewById(R.id.statelayout);
        countrylayout = view.findViewById(R.id.countrylayout);
        codelayout = view.findViewById(R.id.codelayout);
        edresi = view.findViewById(R.id.edresi);
        edaddress = view.findViewById(R.id.edaddress);
        edcity = view.findViewById(R.id.edcity);
        edstate = view.findViewById(R.id.edstate);
        edcountry = view.findViewById(R.id.edcountry);
        edcode = view.findViewById(R.id.edcode);
        btnContinue = view.findViewById(R.id.btnContinue);

        btnContinue.setOnClickListener(v -> {
            if (isValidAllField()) {
                fullAddress = edaddress.getText().toString().trim() + ", " + edcity.getText().toString().trim() + ", " + edstate.getText().toString().trim() + ", " + edcountry.getText().toString().trim() + "-" + edcode.getText().toString().trim();
                insertShared();
                Constant_user_info.currentStep += 1;
                ((SignupProcessActivity) getActivity()).loadFragment(new IdInformationFragment());
            }
        });



        edaddress.addTextChangedListener(addressTextwatcher());
        edcity.addTextChangedListener(cityTextwatcher());
        edstate.addTextChangedListener(stateTextwatcher());
        edcountry.addTextChangedListener(countryTextwatcher());
        edcode.addTextChangedListener(pincodeTextwatcher());
    }


    public void insertShared() {
        SharedPreferences sp = getContext().getSharedPreferences(Constant_user_info.SHARED_ID, MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(Constant_user_info.SHARED_ADDRESS, fullAddress);
        editor.commit();
    }


    public boolean isValidAllField() {
        boolean isAddressValid = validateAddress();
        boolean isCityValid = validateCity();
        boolean isStateValid = validateState();
        boolean isCountryValid = validateCountry();
        boolean isPincodeValid = validatePincode();
        boolean checkPincodeTemp = checkPincode();

        return isAddressValid && isCityValid && isStateValid && isCountryValid && isPincodeValid && checkPincodeTemp;
    }

    private boolean checkPincode() {
        String temp = edcode.getText().toString().trim();
        if(temp.length() != 6 && !TextUtils.isEmpty(temp)) {
            codelayout.setError("Pincode shoulde be 6 digit");
            codelayout.setHelperText(null);
            return false;
        } else {
//            codelayout.setError(null);
//            codelayout.setHelperText(getString(R.string.required));
            return true;
        }
    }

    private TextWatcher addressTextwatcher() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                validateAddress();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };
    }

    private TextWatcher cityTextwatcher() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                validateCity();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };
    }

    private TextWatcher countryTextwatcher() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                validateCountry();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };
    }

    private TextWatcher stateTextwatcher() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                validateState();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };
    }

    private TextWatcher pincodeTextwatcher() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                validatePincode();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };
    }


    public boolean validateAddress() {
        String address = edaddress.getText().toString().trim();
        if (TextUtils.isEmpty(address)) {
            addlayout.setError("Address required");
            return false;
        } else {
            addlayout.setError(null);
            addlayout.setHelperText(getString(R.string.required));
            return true;
        }
    }

    public boolean validateCity() {
        String city = edcity.getText().toString().trim();
        if (TextUtils.isEmpty(city)) {
            citylayout.setError("City required");
            return false;
        } else {
            citylayout.setError(null);
            citylayout.setHelperText(getString(R.string.required));
            return true;
        }
    }

    public boolean validateState() {
        String state = edstate.getText().toString().trim();
        if (TextUtils.isEmpty(state)) {
            statelayout.setError("State required");
            return false;
        } else {
            statelayout.setError(null);
            statelayout.setHelperText(getString(R.string.required));
            return true;
        }
    }

    public boolean validateCountry() {
        String country = edcountry.getText().toString().trim();
        if (TextUtils.isEmpty(country)) {
            countrylayout.setError("Country required");
            return false;
        } else {
            countrylayout.setError(null);
            countrylayout.setHelperText(getString(R.string.required));
            return true;
        }
    }

    public boolean validatePincode() {
        String code = edcode.getText().toString().trim();
        if (TextUtils.isEmpty(code)) {
            codelayout.setError("Pincode required");
            return false;
        } else {
            codelayout.setError(null);
            codelayout.setHelperText(getString(R.string.required));
            return true;
        }
    }
}