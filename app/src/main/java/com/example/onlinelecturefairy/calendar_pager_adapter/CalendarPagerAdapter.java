package com.example.onlinelecturefairy.calendar_pager_adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class CalendarPagerAdapter extends FragmentStatePagerAdapter {
    private final int TOTAL_AMOUNT = 3;

    Context mContext;

    public CalendarPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
        //mContext = context;
    }

    @Override
    public int getCount() {
        return Integer.MAX_VALUE;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return null;
    }
}
