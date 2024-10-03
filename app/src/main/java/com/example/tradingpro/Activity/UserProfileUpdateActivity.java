package com.example.tradingpro.Activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.tradingpro.Constant.Constant_user_info;
import com.example.tradingpro.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.orhanobut.dialogplus.DialogPlus;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class UserProfileUpdateActivity extends AppCompatActivity {

    TextInputLayout textInputLayoutUpdateName,textInputLayoutUpdatePhone,textInputLayoutUpdateEmail,textInputLayoutUpdatePassword;
    TextInputEditText textInputEdUpdateName,textInputEdUpdatePhone,textInputEdUpdateEmail,textInputEdUpdatePassword;
    RadioButton updateradioEnable, updateradioNotnow;
    MaterialButton btnProfileUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_profile_update);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(40,100,40,40);
            return insets;
        });

        textInputLayoutUpdateName=findViewById(R.id.textInputLayoutUpdateName);
        textInputLayoutUpdatePhone=findViewById(R.id.textInputLayoutUpdatePhone);
        textInputLayoutUpdateEmail=findViewById(R.id.textInputLayoutUpdateEmail);
        textInputLayoutUpdatePassword=findViewById(R.id.textInputLayoutUpdatePassword);
        textInputEdUpdateName=findViewById(R.id.textInputEdUpdateName);
        textInputEdUpdatePhone=findViewById(R.id.textInputEdUpdatePhone);
        textInputEdUpdateEmail=findViewById(R.id.textInputEdUpdateEmail);
        textInputEdUpdatePassword=findViewById(R.id.textInputEdUpdatePassword);
        updateradioEnable=findViewById(R.id.updateradioEnable);
        updateradioNotnow=findViewById(R.id.updateradioNotnow);
        btnProfileUpdate=findViewById(R.id.btnProfileUpdate);


        Intent i1 = getIntent();

        String name = i1.getStringExtra("username");
        String phone = i1.getStringExtra("phone");
        String email = i1.getStringExtra("email");
        String password = i1.getStringExtra("password");
        String finger = i1.getStringExtra("finger");

        textInputEdUpdateName.setText(name);
        textInputEdUpdateEmail.setText(email);
        textInputEdUpdatePhone.setText(phone);
        textInputEdUpdatePassword.setText(password);

        Toast.makeText(this, finger, Toast.LENGTH_SHORT).show();
        if (Objects.equals(finger, "true")) {
            updateradioEnable.setChecked(true);
            updateradioNotnow.setChecked(false);
        } else {
            updateradioEnable.setChecked(false);
            updateradioNotnow.setChecked(true);
        }

        btnProfileUpdate.setOnClickListener(v -> {


            Map<String, Object> map = new HashMap<>();
            map.put("email", textInputEdUpdateEmail.getText().toString());
            map.put("fingerprint", String.valueOf(updateradioEnable.isChecked()));
            map.put("password", textInputEdUpdatePassword.getText().toString());
            map.put("phone", textInputEdUpdatePhone.getText().toString());
            map.put("username", textInputEdUpdateName.getText().toString());

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("user_info");
            Query query = databaseReference.orderByChild("email").equalTo(textInputEdUpdateEmail.getText().toString());

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()){
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                            snapshot.getRef().updateChildren(map).addOnCompleteListener(task -> {
                               if (task.isSuccessful()){
                                   Toast.makeText(UserProfileUpdateActivity.this, "Record Updated", Toast.LENGTH_SHORT).show();

                                   SharedPreferences sp = getSharedPreferences(Constant_user_info.SHARED_LOGIN_ID, MODE_PRIVATE);
                                   SharedPreferences.Editor ed = sp.edit();
                                   ed.putString(Constant_user_info.SHARED_LOGIN_USERNM, textInputEdUpdateName.getText().toString());
                                   ed.commit();

                                   Intent intent = new Intent(UserProfileUpdateActivity.this, UserProfileActivity.class);
                                   startActivity(intent);
                               } else {
                                    Toast.makeText(UserProfileUpdateActivity.this, "Record updated failed", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    } else {
                        Toast.makeText(UserProfileUpdateActivity.this, "No Record Found", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(UserProfileUpdateActivity.this, "Error" + databaseError.getMessage(), Toast.LENGTH_SHORT).show();

                }
            });

//          FirebaseDatabase.getInstance().getReference().child("user_info")
//                          .child(getRef(position).getKey()).updateChildren(map)
//                            .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                @Override
//                                public void onSuccess(Void unused) {
//                                    Toast.makeText(UserProfileUpdateActivity.this, "Update Success", Toast.LENGTH_SHORT).show();
//                                }
//                            })
//                            .addOnFailureListener(new OnFailureListener() {
//                                @Override
//                                public void onFailure(@NonNull Exception e) {
//                                    Toast.makeText(UserProfileUpdateActivity.this, "Update Failed", Toast.LENGTH_SHORT).show();
//                                }
//                            });
        });







    }
}