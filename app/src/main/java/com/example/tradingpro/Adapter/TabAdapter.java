package com.example.tradingpro.Adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.tradingpro.OverviewTabFragment.ChartFragment;
import com.example.tradingpro.OverviewTabFragment.CompanyProfileFragment;
import com.example.tradingpro.OverviewTabFragment.OverviewFragment;

import java.util.ArrayList;
import java.util.List;

public class TabAdapter extends FragmentPagerAdapter {
    private final List<Fragment> fragmentList = new ArrayList<>();

    public TabAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    public void addFragment(Fragment fragment) {
        fragmentList.add(fragment);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        if (position==0){
            return "Overview";
        }
//        else if (position==1) {
//            return "Chart";
//        }
        else {
            return "Company Profile";
        }
    }
}
