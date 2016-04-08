package com.gcloud.gaadi.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.gcloud.gaadi.ui.SellerLeadsFutureFragment;
import com.gcloud.gaadi.ui.SellerLeadsNYCFragment;
import com.gcloud.gaadi.ui.SellerLeadsPastFragment;
import com.gcloud.gaadi.ui.SellerLeadsTodaysFragment;

/**
 * Created by ankitgarg on 20/08/15.
 */
public class SellerLeadsManagePagerAdapter extends FragmentPagerAdapter {

    private static final String[] pageTitles = {"No Action Taken", "Today's", "Past", "Upcoming"};

    public SellerLeadsManagePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        Fragment fragment = null;
        Bundle args = new Bundle();
        switch (i) {
            case 0:
                fragment = new SellerLeadsNYCFragment();
                break;

            case 1:
                fragment = new SellerLeadsTodaysFragment();
                break;

            case 2:
                fragment = new SellerLeadsPastFragment();
                break;

            case 3:
                fragment = new SellerLeadsFutureFragment();
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
