package com.gcloud.gaadi.ui;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.View;

import com.gcloud.gaadi.Fragments.NotificationFragment;

public class NotificationActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getLayoutInflater().inflate(R.layout.activity_notification, frameLayout);

        viewPager.setVisibility(View.VISIBLE);
        tabLayout.setVisibility(View.VISIBLE);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);

        viewPager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return NotificationFragment.newInstance(position + 1, new UpdateTabText() {
                    @Override
                    public void updateTabText(int position, int count) {
                        position--;
                        tabLayout.getTabAt(position).setText(count == 0 ?
                                getPageTitle(position) : getPageTitle(position) + " (" + count + ")");
                    }
                });
            }

            @Override
            public int getCount() {
                return 2;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return new String[]{"Unread", "Read"}[position];
            }
        });
        tabLayout.setupWithViewPager(viewPager);
    }

    public interface UpdateTabText {
        void updateTabText(int position, int count);
    }
}
