package com.gcloud.gaadi.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.gcloud.gaadi.ui.NotYetCalledFollowUpLeadsFragment;
import com.gcloud.gaadi.ui.PastFollowUpLeadsFragment;
import com.gcloud.gaadi.ui.TodayFollowUpLeadsFragment;
import com.gcloud.gaadi.ui.UpcomingFollowUpLeadsFragment;

/**
 * Created by ankit on 25/11/14.
 */
public class LeadsManagePagerAdapter extends FragmentPagerAdapter {

    private static final String[] pageTitles = {"No Action Taken", "Today's", "Past", "Upcoming"};

    public LeadsManagePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        Fragment fragment = null;
        Bundle args = new Bundle();
        switch (i) {
            case 0:
                fragment = new NotYetCalledFollowUpLeadsFragment();
                break;

            case 1:
                fragment = new TodayFollowUpLeadsFragment();
                break;

            case 2:
                fragment = new PastFollowUpLeadsFragment();
                break;

            case 3:
                fragment = new UpcomingFollowUpLeadsFragment();
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
