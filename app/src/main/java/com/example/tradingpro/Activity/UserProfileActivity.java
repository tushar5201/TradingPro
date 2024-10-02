package com.example.tradingpro.Activity;

import android.content.SharedPreferences;
import android.os.Bundle;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserProfileActivity extends AppCompatActivity {

    TextView profileUserName,profilePhone,profileEmail,profileMpin,profileFingerprint,profilePass;
    String username,phone,email,password,fingerprint,mpin;
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

        profileUserName=findViewById(R.id.profileUserName);
        profilePhone=findViewById(R.id.profilePhone);
        profileEmail=findViewById(R.id.profileEmail);
        profileMpin=findViewById(R.id.profileMpin);
        profileFingerprint=findViewById(R.id.profileFingerprint);
        profilePass=findViewById(R.id.profilePass);

        SharedPreferences sp = getSharedPreferences(Constant_user_info.SHARED_LOGIN_ID, MODE_PRIVATE);

        String matchUnm = sp.getString(Constant_user_info.SHARED_LOGIN_USERNM, String.valueOf(false));

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("user_info");
        databaseReference.orderByChild("username").equalTo(matchUnm)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()){
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
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }
}