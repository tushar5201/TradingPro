package com.example.tradingpro.Adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.tradingpro.OverviewTabFragment.AnalysisFragment;
import com.example.tradingpro.OverviewTabFragment.ChartFragment;
import com.example.tradingpro.OverviewTabFragment.OverviewFragment;

public class TabAdapter extends FragmentPagerAdapter {
    public TabAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        if (position==0){
            return new ChartFragment();
        } else if (position==1) {
            return new AnalysisFragment();
        } else {
            return new OverviewFragment();
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        if (position==0){
            return "Chart";
        } else if (position==1) {
            return "Analysis";
        } else {
            return "Overview";
        }
    }
}
