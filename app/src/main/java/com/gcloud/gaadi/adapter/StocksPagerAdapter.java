package com.gcloud.gaadi.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import com.gcloud.gaadi.ui.AvailableStocksFragment;
import com.gcloud.gaadi.ui.RemovedStocksFragment;
import com.gcloud.gaadi.ui.StocksActivity;


/**
 * Created by priyarawat on 17/9/15.
 */
public class StocksPagerAdapter extends FragmentPagerAdapter {

    private static final String[] pageTitles = {"Available", "Removed"};
    private StocksActivity.OverFlowMenuListener overFlowMenuListener;
    Fragment fragment = null;
    FragmentManager fm;
    SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();

    public StocksPagerAdapter(FragmentManager fm, StocksActivity.OverFlowMenuListener listener) {
        super(fm);
        this.overFlowMenuListener = listener;
    }

    @Override
    public Fragment getItem(int i) {
        switch (i) {
            case 0:
                return AvailableStocksFragment.newInstance(overFlowMenuListener);
            case 1:
               return RemovedStocksFragment.newInstance(overFlowMenuListener);
            default:
                return null;
        }


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
