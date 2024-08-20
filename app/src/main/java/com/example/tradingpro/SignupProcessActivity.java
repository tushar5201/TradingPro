package com.example.tradingpro;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.tradingpro.SignupProcessFragment.BiometricsEnableFragment;
import com.example.tradingpro.SignupProcessFragment.DisclaimerFragment;
import com.example.tradingpro.SignupProcessFragment.IdInformationFragment;
import com.example.tradingpro.SignupProcessFragment.MpinCreateFragment;
import com.example.tradingpro.SignupProcessFragment.PersonalInformationFragment;
import com.shuhart.stepview.StepView;

import java.util.Arrays;
import java.util.List;

public class SignupProcessActivity extends AppCompatActivity {

    StepView stepView;
    FragmentManager fragmentManager;
    Button btnContinue;

    List<Fragment> fragments = Arrays.asList(
            new PersonalInformationFragment(),
            new IdInformationFragment(),
            new MpinCreateFragment(),
            new BiometricsEnableFragment(),
            new DisclaimerFragment()
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup_process);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        stepView = findViewById(R.id.step_view);
//        btnContinue = findViewById(R.id.btnContinue);
        fragmentManager = getSupportFragmentManager();

        stepView.setStepsNumber(fragments.size());
        Fragment PersonalInformationFragment = new PersonalInformationFragment();
        loadFragment(PersonalInformationFragment);

//        btnContinue.setOnClickListener(v -> {
//            if (Constant_user_info.currentStep < fragments.size() - 1) {
//                Constant_user_info.currentStep++;
//                stepView.go(Constant_user_info.currentStep, true);
//                loadFragment(Constant_user_info.currentStep);
//            } else {
//                Toast.makeText(this, "Completed.", Toast.LENGTH_SHORT).show();
//            }
//        });
    }

    public void loadFragment(Fragment fragment) {
        stepView.go(Constant_user_info.currentStep, true);
        fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit();
    }

//    public void loadFragment(Fragment fragment) {
//// create a FragmentManager
//
//// create a FragmentTransaction to begin the transaction and replace the Fragment
//        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
//// replace the FrameLayout with new Fragment
//        fragmentTransaction.replace(R.id.fragment_container, fragment);
//        fragmentTransaction.commit(); // save the changes
//    }


//    public void insertShared() {
//        SharedPreferences sp = getSharedPreferences(Constant_user_info.SHARED_ID, MODE_PRIVATE);
//        SharedPreferences.Editor editor = sp.edit();
//        editor.putString(Constant_user_info.SHARED_USERNM, name);
//        editor.putString(Constant_user_info.SHARED_EMAIL, email);
//        editor.putString(Constant_user_info.SHARED_PASS, password);
//        editor.putString(Constant_user_info.SHARED_PHONE, phone);
//        editor.commit();
//    }
}