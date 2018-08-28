package com.irfan.ilham.tugasakhir;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class ViewPagerAdapterUser extends FragmentStatePagerAdapter {


    public ViewPagerAdapterUser(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        if (position == 0) {
            fragment = new UserDetailFragment();
        } else if (position == 1) {
            fragment = new HomeFragment();
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return 2;
    }
}
