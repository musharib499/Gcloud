package com.gcloud.gaadi.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import com.gcloud.gaadi.ui.DealerActiveInventoryFragment;
import com.gcloud.gaadi.ui.DealerInactiveInventoryFragment;
import com.gcloud.gaadi.ui.DealerSearchFragment;

/**
 * Created by ankit on 24/11/14.
 */
public class DealerPlatformAdapter extends FragmentStatePagerAdapter {
    private static final String[] pageTitles = {"Search Cars", "Active", "Inactive"};
    Fragment fragment = null;
    FragmentManager fm;
    SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();

    public DealerPlatformAdapter(FragmentManager fm) {
        super(fm);
        this.fm = fm;
    }

    @Override
    public Fragment getItem(int i) {

        Bundle args = new Bundle();
        switch (i) {
            case 0:
                fragment = new DealerSearchFragment();
//                fm.beginTransaction().replace(R.id.viewPager)

                fragment.setArguments(args);

                break;

            case 1:
                fragment = new DealerActiveInventoryFragment();
                fragment.setArguments(args);

                break;

            case 2:
                fragment = new DealerInactiveInventoryFragment();
                fragment.setArguments(args);

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
        registeredFragments.remove(position);
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
