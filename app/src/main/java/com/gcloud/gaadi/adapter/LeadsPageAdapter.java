package com.gcloud.gaadi.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import com.gcloud.gaadi.ui.AllCarsFragment;
import com.gcloud.gaadi.ui.SentCarsFragment;
import com.gcloud.gaadi.ui.SimilarCarsFragment;

/**
 * Created by Seema Pandit on 14-01-2015.
 */
public class LeadsPageAdapter extends FragmentStatePagerAdapter {
    //
    private static final String[] pageTitles = {"Suggested", "Sent", "All"};

    public LeadsPageAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        Fragment fragment = null;
        Bundle args = new Bundle();
        switch (i) {
            case 0:
                fragment = new SimilarCarsFragment();

                break;

            case 1:
                fragment = new SentCarsFragment();
                break;

            case 2:
                fragment = new AllCarsFragment();
                break;


        }

        return fragment;
    }

    @Override
    public int getCount() {
        return pageTitles.length;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        return super.instantiateItem(container, position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return pageTitles[position];
    }


}
