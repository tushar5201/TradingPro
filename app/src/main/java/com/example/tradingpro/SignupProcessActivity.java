package com.example.tradingpro;

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

import com.shuhart.stepview.StepView;

import java.util.Arrays;
import java.util.List;

public class SignupProcessActivity extends AppCompatActivity {

    StepView stepView;
    FragmentManager fragmentManager;
    public int currentStep = 0;
    Button btnContinue;

    List<Fragment> fragments = Arrays.asList(
            new PersonalInformationFragment(),
            new IdInformationFragment(),
            new MpinFragment(),
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
        btnContinue = findViewById(R.id.btnContinue);
        fragmentManager = getSupportFragmentManager();

        stepView.setStepsNumber(fragments.size());
        loadFragment(currentStep);

        btnContinue.setOnClickListener(v -> {
            if (currentStep < fragments.size() - 1) {
                currentStep++;
                stepView.go(currentStep, true);
                loadFragment(currentStep);
            } else {
                Toast.makeText(this, "Completed.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadFragment(int step) {
        Fragment fragment = fragments.get(step);
        fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit();
    }

    public void loadFragment(Fragment fragment) {
// create a FragmentManager

// create a FragmentTransaction to begin the transaction and replace the Fragment
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
// replace the FrameLayout with new Fragment
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit(); // save the changes
    }
}