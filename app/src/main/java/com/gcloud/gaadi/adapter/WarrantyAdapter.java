package com.gcloud.gaadi.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import com.gcloud.gaadi.ui.CertifiedCarsFragment;
import com.gcloud.gaadi.ui.IssuedWarrantyFragment;

/**
 * Created by Seema Pandit on 14-01-2015.
 */
public class WarrantyAdapter extends FragmentPagerAdapter {
    //"Issued Warranty"
    private static final String[] pageTitles = {"Certified Cars", "Issued Warranty"
    };
    private SparseArray<Fragment> registeredFragments = new SparseArray<>();

    public WarrantyAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        Fragment fragment = null;
        Bundle args = new Bundle();
        switch (i) {
            case 0:
                fragment = new CertifiedCarsFragment();
                break;

            case 1:
                fragment = new IssuedWarrantyFragment();
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
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        registeredFragments.put(position, fragment);
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
    }

    public Fragment getRegisteredFragment(int position) {
        return registeredFragments.get(position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return pageTitles[position];
    }

}
