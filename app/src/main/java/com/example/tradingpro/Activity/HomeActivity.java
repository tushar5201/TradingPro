package com.example.tradingpro.Activity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
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
import com.example.tradingpro.HomeFragments.HoldingsFragment;
import com.example.tradingpro.HomeFragments.MarketsFragment;
import com.example.tradingpro.HomeFragments.OrdersFragment;
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

    private CardView card1, cardHoldings, cardOrders, card2, card3, cardBottomNav;
    private ImageView img1, imgHoldings, imgOrders, img2, img3;
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private NavigationView nav_view;
    private TextView tvToolbarHeading;

    private int currentTab = 0;

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

        checkInternetPermission();

        drawerLayout = findViewById(R.id.main);
        toolbar = findViewById(R.id.toolbar);
        nav_view = findViewById(R.id.nav_view);
        tvToolbarHeading = findViewById(R.id.tvToolbarHeading);
        cardBottomNav = findViewById(R.id.cardBottomNav);
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle abdt = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.nav_open, R.string.nav_close);
        drawerLayout.addDrawerListener(abdt);
        abdt.syncState();

        nav_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.navmarket) {
                    cardBottomNav.setVisibility(View.VISIBLE);
                    selectBottomTab(0);
                } else if (id == R.id.navportfolio) {
                    cardBottomNav.setVisibility(View.VISIBLE);
                    selectBottomTab(1);
                } else if (id == R.id.navoders) {
                    cardBottomNav.setVisibility(View.VISIBLE);
                    selectBottomTab(2);
                } else if (id == R.id.navfavourite) {
                    cardBottomNav.setVisibility(View.VISIBLE);
                    selectBottomTab(3);
                } else if (id == R.id.navtnc) {
                    tvToolbarHeading.setText("Terms and Conditions");
                    cardBottomNav.setVisibility(View.GONE);
                    replaceFragment(new TermsAndConditionFragment());
                } else if (id == R.id.navcontactus) {
                    Intent i1 = new Intent(Intent.ACTION_DIAL);
                    i1.setData(Uri.parse("tel:+916351650589"));
                    startActivity(i1);
                } else if (id == R.id.navShare) {
                    Intent i1 = new Intent(Intent.ACTION_SEND);
                    i1.setType("text/plain");
                    i1.putExtra(Intent.EXTRA_TEXT, "https://github.com/tushar5201/TradingPro");
                    startActivity(Intent.createChooser(i1, "Share with"));
                } else if (id == R.id.navProfile) {
                    Intent i = new Intent(HomeActivity.this, UserProfileActivity.class);
                    startActivity(i);
                } else if (id == R.id.navfeedback) {
                    showFeedbackDialog();
                } else if (id == R.id.navlogout) {
                    showLogoutDialog();
                }
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        // Header view of drawer
        View headerView = nav_view.getHeaderView(0);
        if (headerView != null) {
            TextView navUserName = headerView.findViewById(R.id.DrawerHeaderUserName);
            TextView navEmail = headerView.findViewById(R.id.DrawerHeaderEmail);

            SharedPreferences sp = getSharedPreferences(Constant_user_info.SHARED_LOGIN_ID, MODE_PRIVATE);
            String headerName = sp.getString(Constant_user_info.SHARED_LOGIN_USERNM, "Trader");
            String headerEmail = sp.getString(Constant_user_info.SHARED_LOGIN_EMAILORPHONE, "trader@tradingpro.com");

            if (navUserName != null) navUserName.setText(headerName.toUpperCase());
            if (navEmail != null) navEmail.setText(headerEmail);
        }

        // Bottom Navigation Views
        card1 = findViewById(R.id.card1);
        cardHoldings = findViewById(R.id.cardHoldings);
        cardOrders = findViewById(R.id.cardOrders);
        card2 = findViewById(R.id.card2);
        card3 = findViewById(R.id.card3);

        img1 = findViewById(R.id.img1);
        imgHoldings = findViewById(R.id.imgHoldings);
        imgOrders = findViewById(R.id.imgOrders);
        img2 = findViewById(R.id.img2);
        img3 = findViewById(R.id.img3);

        card1.setOnClickListener(v -> selectBottomTab(0));
        cardHoldings.setOnClickListener(v -> selectBottomTab(1));
        cardOrders.setOnClickListener(v -> selectBottomTab(2));
        card2.setOnClickListener(v -> selectBottomTab(3));
        card3.setOnClickListener(v -> selectBottomTab(4));

        if (savedInstanceState == null) {
            selectBottomTab(0);
        }
    }

    public void selectBottomTab(int index) {
        currentTab = index;

        // Reset all card backgrounds to black, icons to white
        setCardStyle(card1, img1, false, R.drawable.market_white);
        setCardStyle(cardHoldings, imgHoldings, false, R.drawable.portfolio_white);
        setCardStyle(cardOrders, imgOrders, false, R.drawable.order_white);
        setCardStyle(card2, img2, false, R.drawable.watchlist_white);
        setCardStyle(card3, img3, false, R.drawable.search_white);

        cardBottomNav.setVisibility(View.VISIBLE);

        if (index == 0) {
            setCardStyle(card1, img1, true, R.drawable.market);
            tvToolbarHeading.setText("Markets");
            replaceFragment(new MarketsFragment());
        } else if (index == 1) {
            setCardStyle(cardHoldings, imgHoldings, true, R.drawable.portfolio);
            tvToolbarHeading.setText("Holdings");
            replaceFragment(new HoldingsFragment());
        } else if (index == 2) {
            setCardStyle(cardOrders, imgOrders, true, R.drawable.order);
            tvToolbarHeading.setText("Orders");
            replaceFragment(new OrdersFragment());
        } else if (index == 3) {
            setCardStyle(card2, img2, true, R.drawable.watchlist);
            tvToolbarHeading.setText("Watchlist");
            replaceFragment(new WatchlistFragment());
        } else if (index == 4) {
            setCardStyle(card3, img3, true, R.drawable.search_);
            tvToolbarHeading.setText("Search");
            replaceFragment(new SearchFragment());
        }
    }

    private void setCardStyle(CardView card, ImageView img, boolean isSelected, int iconRes) {
        if (card == null || img == null) return;
        GradientDrawable gd = new GradientDrawable();
        gd.setCornerRadius(80f);
        if (isSelected) {
            gd.setColor(Color.parseColor("#fee83f"));
        } else {
            gd.setColor(Color.parseColor("#000000"));
        }
        card.setBackground(gd);
        img.setImageResource(iconRes);
    }

    public void switchToMarkets() {
        selectBottomTab(0);
    }

    private void showFeedbackDialog() {
        DialogPlus dialogPlus = DialogPlus.newDialog(HomeActivity.this)
                .setExpanded(true, 1200)
                .setContentHolder(new ViewHolder(R.layout.dialog_feedback))
                .create();

        dialogPlus.show();
        View view = dialogPlus.getHolderView();

        TextInputEditText textInputEdMsg = view.findViewById(R.id.textInputEdMsg);
        MaterialButton btnSubmitFeedback = view.findViewById(R.id.btnSubmitFeedback);
        TextView tvEmailorPhn = view.findViewById(R.id.tvEmailorPhn);

        SharedPreferences sp = getSharedPreferences(Constant_user_info.SHARED_LOGIN_ID, MODE_PRIVATE);
        String nameOrEmail = sp.getString(Constant_user_info.SHARED_LOGIN_EMAILORPHONE, "User");
        tvEmailorPhn.setText(nameOrEmail);

        btnSubmitFeedback.setOnClickListener(v -> {
            try {
                Double isPhone = Double.parseDouble(nameOrEmail);
                if (ContextCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(HomeActivity.this, new String[]{Manifest.permission.SEND_SMS}, 1);
                    Snackbar.make(drawerLayout, "Permission Denied!", Snackbar.LENGTH_SHORT).show();
                } else {
                    SmsManager sms = SmsManager.getDefault();
                    sms.sendTextMessage("+91" + nameOrEmail, null, textInputEdMsg.getText().toString().trim(), null, null);
                    Snackbar.make(drawerLayout, "Feedback sent successfully", Snackbar.LENGTH_SHORT).show();
                    dialogPlus.dismiss();
                }
            } catch (NumberFormatException err) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                emailIntent.setData(Uri.parse("mailto:"));
                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"support@tradingpro.com"});
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Feedback");
                emailIntent.putExtra(Intent.EXTRA_TEXT, textInputEdMsg.getText().toString().trim());
                startActivity(Intent.createChooser(emailIntent, "TradingPro"));
                dialogPlus.dismiss();
            } catch (Exception err) {
                Snackbar.make(drawerLayout, "Something went wrong!", Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    private void showLogoutDialog() {
        DialogPlus dialog = DialogPlus.newDialog(HomeActivity.this)
                .setExpanded(true, 700)
                .setContentHolder(new ViewHolder(R.layout.dialog_logout))
                .create();

        View view = dialog.getHolderView();
        Button btnLogoutYes = view.findViewById(R.id.btnLogoutYes);
        Button btnLogoutNo = view.findViewById(R.id.btnLogoutNo);

        btnLogoutYes.setOnClickListener(v -> {
            SharedPreferences sp = getSharedPreferences(Constant_user_info.SHARED_LOGIN_ID, MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.clear();
            editor.apply();
            dialog.dismiss();
            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        btnLogoutNo.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void checkInternetPermission() {
        if (!internetCheck.isCheckInternet(this)) {
            Toast.makeText(this, "No internet access", Toast.LENGTH_SHORT).show();
        }
    }

    public void addFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }

    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }
}
