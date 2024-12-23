package com.example.tradingpro.Activity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.tradingpro.Constant.Constant_user_info;
import com.example.tradingpro.HomeFragments.ContactUsFragment;
import com.example.tradingpro.HomeFragments.MarketsFragment;
import com.example.tradingpro.HomeFragments.SearchFragment;
import com.example.tradingpro.HomeFragments.TermsAndConditionFragment;
import com.example.tradingpro.HomeFragments.WatchlistFragment;
import com.example.tradingpro.R;
import com.example.tradingpro.internetCheck;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    CardView card1, card2, card3, card4, card5, cardBottomNav;
    ImageView img1, img2, img3, img4, img5;
    DrawerLayout drawerLayout;
    Toolbar toolbar;
    ImageButton buttondrawertoggle;
    NavigationView nav_view;
    TextView tvToolbarHeading;
    FrameLayout frame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });



//        check internet permission
        checkInternetPermission();

        if(savedInstanceState == null) {
            addFragment(new MarketsFragment());
        }









// drawer..........
        frame = findViewById(R.id.frame);
        drawerLayout = findViewById(R.id.main);
        toolbar = findViewById(R.id.toolbar);
        nav_view = findViewById(R.id.nav_view);
        tvToolbarHeading = findViewById(R.id.tvToolbarHeading);
        cardBottomNav = findViewById(R.id.cardBottomNav);
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle abdt = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.nav_open, R.string.nav_close);
        drawerLayout.addDrawerListener(abdt);
        abdt.syncState();
        nav_view.setNavigationItemSelectedListener(this);

        nav_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if (item.getItemId() == R.id.navmarket) {
                    cardBottomNav.setVisibility(View.VISIBLE);
                    tvToolbarHeading.setText("Markets");
                    BottomIcon1();
                    replaceFragment(new MarketsFragment());
                } else if (item.getItemId() == R.id.navfavourite) {
                    cardBottomNav.setVisibility(View.VISIBLE);
                    tvToolbarHeading.setText("Watchlist");
                    bottomIcon2();
                    replaceFragment(new WatchlistFragment());
                } else if (item.getItemId() == R.id.navtnc) {
                    tvToolbarHeading.setText("Terms and Conditions");
                    cardBottomNav.setVisibility(View.GONE);
                    replaceFragment(new TermsAndConditionFragment());
                } else if (item.getItemId() == R.id.navcontactus) {
                    cardBottomNav.setVisibility(View.VISIBLE);
//                    tvToolbarHeading.setText("Contact Us");
//                    addFragment(new ContactUsFragment());
                    Intent i1 = new Intent(Intent.ACTION_DIAL);
                    i1.setData(Uri.parse("tel:+916351650589"));
                    startActivity(i1);
                } else if (item.getItemId() == R.id.navShare) {
                    Intent i1 = new Intent(Intent.ACTION_SEND);
                    i1.setType("text/plain");
                    i1.putExtra(Intent.EXTRA_TEXT, "https://github.com/tushar5201/TradingPro");
                    startActivity(Intent.createChooser(i1, "Share with"));
                } else if (item.getItemId() == R.id.navProfile) {
                    cardBottomNav.setVisibility(View.VISIBLE);
                    Intent i = new Intent(HomeActivity.this,UserProfileActivity.class);
                    startActivity(i);
                }else if(item.getItemId() == R.id.navfeedback){
                    cardBottomNav.setVisibility(View.VISIBLE);
                    DialogPlus dialogPlus = DialogPlus.newDialog(HomeActivity.this)
                            .setExpanded(true, 1200)
                            .setContentHolder(new ViewHolder(R.layout.dialog_feedback))
                            .create()
                            ;

                    dialogPlus.show();

                    View view = dialogPlus.getHolderView();

                    TextInputEditText textInputEdMsg = view.findViewById(R.id.textInputEdMsg);
                    MaterialButton btnSubmitFeedback = view.findViewById(R.id.btnSubmitFeedback);
                    TextInputLayout textInputLayoutMsg = view.findViewById(R.id.textInputLayoutMsg);
                    TextView tvEmailorPhn = view.findViewById(R.id.tvEmailorPhn);

                    SharedPreferences sp = getSharedPreferences(Constant_user_info.SHARED_LOGIN_ID, MODE_PRIVATE);
                    String nameOrEmail = sp.getString(Constant_user_info.SHARED_LOGIN_EMAILORPHONE, String.valueOf(false));

                    tvEmailorPhn.setText(nameOrEmail);

                    btnSubmitFeedback.setOnClickListener(v -> {

                        try{

                            Double isPhone = Double.parseDouble(nameOrEmail);
                            isPhone.getClass();

                            if(ContextCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions(HomeActivity.this, new String[]{Manifest.permission.SEND_SMS}, 1);
                                Snackbar.make(drawerLayout, "Permission Denied!", Snackbar.LENGTH_SHORT).show();
                            } else {
                                    SmsManager sms = SmsManager.getDefault();
                                    sms.sendTextMessage("+91"+nameOrEmail, null, textInputEdMsg.getText().toString().trim(), null, null);
                                Snackbar.make(drawerLayout, "Feedback sent successfully", Snackbar.LENGTH_SHORT).show();
                                dialogPlus.dismiss();
                            }


                        }catch(NumberFormatException err){

                            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                            emailIntent.setData(Uri.parse("mailto:"));
                            emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"tusharlakadiya@gmail.com"});
                            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Feedback");
                            emailIntent.putExtra(Intent.EXTRA_TEXT, textInputEdMsg.getText().toString().trim());
                            startActivity(Intent.createChooser(emailIntent, "TradingPro"));
                            dialogPlus.dismiss();
                        }catch (Error err){
                            Snackbar.make(drawerLayout, "Something went wrong!", Snackbar.LENGTH_SHORT).show();
                        }
                    });


                }
                else if (item.getItemId() == R.id.navlogout) {
                    cardBottomNav.setVisibility(View.VISIBLE);
                    DialogPlus dialog = DialogPlus.newDialog(HomeActivity.this)
                            .setExpanded(true, 700)
                            .setContentHolder(new ViewHolder(R.layout.dialog_logout))
                            .create();

                    View view =dialog.getHolderView();

                    Button btnLogoutYes = view.findViewById(R.id.btnLogoutYes);
                    Button btnLogoutNo = view.findViewById(R.id.btnLogoutNo);

                    btnLogoutYes.setOnClickListener(v -> {
                        SharedPreferences sp = getSharedPreferences(Constant_user_info.SHARED_LOGIN_ID, MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.clear();
                        editor.commit();
                        dialog.dismiss();
                        Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                        startActivity(intent);
                    });

                    btnLogoutNo.setOnClickListener(v -> {
                        dialog.dismiss();
                    });
                    dialog.show();
                }
                drawerLayout.closeDrawer(GravityCompat.START);

                return true;
            }
        });

//        get header view of drawer
        View headerView = nav_view.getHeaderView(0);

//        bind the textview for set name n email
        TextView navUserName = headerView.findViewById(R.id.DrawerHeaderUserName);
        TextView navEmail = headerView.findViewById(R.id.DrawerHeaderEmail);

//        get name from shared preferences
        SharedPreferences sp = getSharedPreferences(Constant_user_info.SHARED_LOGIN_ID, MODE_PRIVATE);

        String headerName = sp.getString(Constant_user_info.SHARED_LOGIN_USERNM, String.valueOf(false));
        String headerEmail = sp.getString(Constant_user_info.SHARED_LOGIN_EMAILORPHONE, String.valueOf(false));

//       set name in header drawerlayout
        navUserName.setText(headerName.toUpperCase());
        navEmail.setText(headerEmail);

//        bottom navigation
        card1 = findViewById(R.id.card1);
        card2 = findViewById(R.id.card2);
        card3 = findViewById(R.id.card3);
//        card4 = findViewById(R.id.card4);
//        card5 = findViewById(R.id.card5);

        img1 = findViewById(R.id.img1);
        img2 = findViewById(R.id.img2);
        img3 = findViewById(R.id.img3);
//        img4 = findViewById(R.id.img4);
//        img5 = findViewById(R.id.img5);

        BottomIcon1();

        card1.setOnClickListener(v -> {
            BottomIcon1();
            replaceFragment(new MarketsFragment());
            tvToolbarHeading.setText("Markets");
        });

        card2.setOnClickListener(v -> {
            bottomIcon2();
            replaceFragment(new WatchlistFragment());
            tvToolbarHeading.setText("Watchlist");
        });

//        card3
        card3.setOnClickListener(v -> {
            bottomIcon3();
            replaceFragment(new SearchFragment());
            tvToolbarHeading.setText("Search");
        });

//        card4.setOnClickListener(v -> {
//            bottomIcon4();
//        });
//
//        card5.setOnClickListener(v -> {
//            bottomIcon5();
//        });
    }

    private void checkInternetPermission() {
        if (internetCheck.isCheckInternet(this)) {
        } else {
            Toast.makeText(this, "No internet access", Toast.LENGTH_LONG).show();
//            Snackbar.make(, "NO INTERNET CONNECTION!", Snackbar.LENGTH_SHORT).show();
            // Handle no internet scenario
        }
    }

    private void BottomIcon1() {
        GradientDrawable backgroundDrawable = new GradientDrawable();
        backgroundDrawable.setCornerRadius(80f);
        card1.setBackground(backgroundDrawable);
        backgroundDrawable.setColor(Color.parseColor("#fee83f"));
        img1.setImageResource(R.drawable.market);

//            other icon bg remove
        GradientDrawable backgroundDrawableOther = new GradientDrawable();
        backgroundDrawableOther.setCornerRadius(80f);
        backgroundDrawableOther.setColor(Color.parseColor("#000000"));
        card2.setBackground(backgroundDrawableOther);
        card3.setBackground(backgroundDrawableOther);

//            other icon color change
        img2.setImageResource(R.drawable.watchlist_white);
        img3.setImageResource(R.drawable.search_white);

    }

    private void bottomIcon2() {
        GradientDrawable backgroundDrawable = new GradientDrawable();
        backgroundDrawable.setCornerRadius(80f);
        card2.setBackground(backgroundDrawable);
        backgroundDrawable.setColor(Color.parseColor("#fee83f"));
        img2.setImageResource(R.drawable.watchlist);

//            other icon bg remove
        GradientDrawable backgroundDrawableOther = new GradientDrawable();
        backgroundDrawableOther.setCornerRadius(80f);
        backgroundDrawableOther.setColor(Color.parseColor("#000000"));
        card1.setBackground(backgroundDrawableOther);
        card3.setBackground(backgroundDrawableOther);

//            other icon color change
        img1.setImageResource(R.drawable.market_white);
        img3.setImageResource(R.drawable.search_white);

    }

    private void bottomIcon3() {
        GradientDrawable backgroundDrawable = new GradientDrawable();
        backgroundDrawable.setCornerRadius(80f);
        card3.setBackground(backgroundDrawable);
        backgroundDrawable.setColor(Color.parseColor("#fee83f"));
        img3.setImageResource(R.drawable.search_);

//            other icon bg remove
        GradientDrawable backgroundDrawableOther = new GradientDrawable();
        backgroundDrawableOther.setCornerRadius(80f);
        backgroundDrawableOther.setColor(Color.parseColor("#000000"));
        card1.setBackground(backgroundDrawableOther);
        card2.setBackground(backgroundDrawableOther);

//            other icon color change
        img1.setImageResource(R.drawable.market_white);
        img2.setImageResource(R.drawable.watchlist_white);
    }






    public void addFragment(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.frame, fragment);
        ft.commit();
    }




    public void replaceFragment(Fragment fragment) {
        clearStackToRoot();
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.frame, fragment);
        ft.addToBackStack(null);
        ft.commit();
    }


    public void clearStackToRoot() {
        if(getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }

    @Override
    public void onBackPressed() {
        if(getSupportFragmentManager().getBackStackEntryCount() == 0) {
            super.onBackPressed();
//            Toast.makeText(this, "if", Toast.LENGTH_SHORT).show();

        } else {
            if((tvToolbarHeading.getText().toString()).equals("Markets")) {
                super.onBackPressed();
//                Toast.makeText(this, "toast", Toast.LENGTH_SHORT).show();

            }

//            Toast.makeText(this, "else", Toast.LENGTH_SHORT).show();
            BottomIcon1();
            cardBottomNav.setVisibility(View.VISIBLE);
            getSupportFragmentManager().popBackStack();
            tvToolbarHeading.setText("Markets");

        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }
}