package org.jarucas.breakapp.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Javier on 10/09/2018.
 */

public class CustomMenuAdapter extends FragmentPagerAdapter {

    private final List<Fragment> mFragmentList = new ArrayList<>();

    public CustomMenuAdapter(final FragmentManager fm) {
        super(fm);
    }


    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    public void addFragment(final Fragment fragment) {
        mFragmentList.add(fragment);

    }

    @Override
    public CharSequence getPageTitle(int position) {
        return null;
    }
}
