package com.example.tradingpro.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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
import com.google.android.material.navigation.NavigationView;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    CardView card1, card2, card3, card4, card5;
    ImageView img1, img2, img3, img4, img5;
    DrawerLayout drawerLayout;
    Toolbar toolbar;
    ImageButton buttondrawertoggle;
    NavigationView nav_view;
    TextView tvToolbarHeading;

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
        addFragment(new MarketsFragment());
// drawer..........

        drawerLayout = findViewById(R.id.main);
        toolbar = findViewById(R.id.toolbar);
        nav_view = findViewById(R.id.nav_view);
        tvToolbarHeading = findViewById(R.id.tvToolbarHeading);
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle abdt = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.nav_open, R.string.nav_close);
        drawerLayout.addDrawerListener(abdt);
        abdt.syncState();
        nav_view.setNavigationItemSelectedListener(this);

        nav_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if (item.getItemId() == R.id.navmarket) {
                    tvToolbarHeading.setText("Markets");
                    addFragment(new MarketsFragment());
                } else if (item.getItemId() == R.id.navfavourite) {
                    tvToolbarHeading.setText("Watchlist");
                    addFragment(new WatchlistFragment());
                } else if (item.getItemId() == R.id.navtnc) {
                    tvToolbarHeading.setText("Terms and Conditions");
                    addFragment(new TermsAndConditionFragment());
                } else if (item.getItemId() == R.id.navcontactus) {
                    tvToolbarHeading.setText("Contact Us");
                    addFragment(new ContactUsFragment());
                } else if (item.getItemId() == R.id.navProfile) {
                    Toast.makeText(HomeActivity.this, "hello", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(HomeActivity.this,UserProfileActivity.class);
                    startActivity(i);
                } else if (item.getItemId() == R.id.navlogout) {
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
        ft.add(R.id.frame, fragment);
        ft.commit();
    }

    public void replaceFragment(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.frame, fragment);
        ft.commit();
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }
}