package com.example.tradingpro.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.tradingpro.Constant.Constant_user_info;
import com.example.tradingpro.R;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

public class UserProfileActivity extends AppCompatActivity {

    TextView profileUserName, profilePhone, profileEmail, profileMpin, profileFingerprint, profilePass;
    String username = "", phone = "", email = "", password = "", fingerprint = "", mpin = "";
    MaterialButton btnProfileDel,btnProfileEdit;
    Intent i1;
    ImageView profileBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(40, 100, 40, 40);
            return insets;
        });


        profileUserName = findViewById(R.id.profileUserName);
        profilePhone = findViewById(R.id.profilePhone);
        profileEmail = findViewById(R.id.profileEmail);
        profileMpin = findViewById(R.id.profileMpin);
        profileFingerprint = findViewById(R.id.profileFingerprint);
        profilePass = findViewById(R.id.profilePass);
        btnProfileDel = findViewById(R.id.btnProfileDel);
        btnProfileEdit = findViewById(R.id.btnProfileEdit);
        profileBack = findViewById(R.id.profileBack);

        profileBack.setOnClickListener(v -> {
            Intent intent = new Intent(UserProfileActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        });

        SharedPreferences sp = getSharedPreferences(Constant_user_info.SHARED_LOGIN_ID, MODE_PRIVATE);

        String matchUnm = sp.getString(Constant_user_info.SHARED_LOGIN_USERNM, String.valueOf(false));
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("user_info");
        databaseReference.orderByChild("username").equalTo(matchUnm)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                phone = snapshot.child("phone").getValue(String.class);
                                email = snapshot.child("email").getValue(String.class);
                                username = snapshot.child("username").getValue(String.class);
                                mpin = snapshot.child("mpin").getValue(String.class);
                                password = snapshot.child("password").getValue(String.class);
                                fingerprint = snapshot.child("fingerprint").getValue(String.class);
                                //Toast.makeText(UserProfileActivity.this, phone, Toast.LENGTH_SHORT).show();

                                profileUserName.setText(username);
                                profileEmail.setText(email);
                                profileMpin.setText(mpin);
                                profileFingerprint.setText(fingerprint);
                                profilePhone.setText(phone);
                                profilePass.setText(password);

                                btnProfileEdit.setEnabled(true);
                                btnProfileDel.setEnabled(true);

                                i1 = new Intent(getApplicationContext(), UserProfileUpdateActivity.class);
                                i1.putExtra("username", username);
                                i1.putExtra("email", email);
                                i1.putExtra("phone",phone);
                                i1.putExtra("password",password);
                                i1.putExtra("finger",fingerprint);
                                i1.putExtra("mpin", mpin);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


        btnProfileEdit.setOnClickListener(v -> {
//            Intent i1 = new Intent(UserProfileActivity.this, UserProfileUpdateActivity.class);
//            Toast.makeText(this, "username", Toast.LENGTH_SHORT).show();
//
//            //i1.putExtra("username",profileUserName.getText());
//            startActivity(i1);
            startActivity(i1);
            finish();
        });

        btnProfileDel.setOnClickListener(v -> {

            DialogPlus dialog = DialogPlus.newDialog(UserProfileActivity.this)
                    .setExpanded(true, 700)
                    .setContentHolder(new ViewHolder(R.layout.dialog_profile_delete))
                    .create();

            View view = dialog.getHolderView();

            Button btnDeleteYes = view.findViewById(R.id.btnDeleteYes);
            Button btnDeleteNo = view.findViewById(R.id.btnDeleteNo);

            btnDeleteYes.setOnClickListener(v1 -> {
                databaseReference.orderByChild("email").equalTo(email)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                                    String userId = userSnapshot.getKey();

                                    databaseReference.child(userId).removeValue()
                                            .addOnCompleteListener(task -> {
                                                SharedPreferences.Editor editor = sp.edit();
                                                editor.clear();
                                                editor.commit();
                                                dialog.dismiss();
                                                Intent intent = new Intent(UserProfileActivity.this, LoginActivity.class);
                                                startActivity(intent);
                                                finish();
                                            });
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(UserProfileActivity.this, "Account deletion failed", Toast.LENGTH_SHORT).show();
                            }
                        });
            });

            btnDeleteNo.setOnClickListener(v1 -> {
                dialog.dismiss();
            });
            dialog.show();
        });
    }
}