package com.gcloud.gaadi.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.gcloud.gaadi.R;

/**
 * Created by ankit on 25/11/14.
 */
public class LeadFollowupTodayFragment extends Fragment implements
        SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {

    private View rootView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ImageView searchView;

    public LeadFollowupTodayFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_leads_today, container, false);
        /*searchView = (ImageView) rootView.findViewById(R.id.search);
        searchView.setOnClickListener(this);*/
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);

        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onRefresh() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }
    }
}
