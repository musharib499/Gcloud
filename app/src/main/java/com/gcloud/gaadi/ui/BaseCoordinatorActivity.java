package com.gcloud.gaadi.ui;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.gcloud.gaadi.R;

public class BaseCoordinatorActivity extends AppCompatActivity {
    protected NestedScrollView nestedScrollView;
    protected Toolbar toolbar;
    protected TabLayout tabLayout;
    protected AppBarLayout appBarLayout;
    protected FloatingActionButton fab;
    protected FrameLayout frameLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_coordinator);

        //  nestedScrollView= (NestedScrollView) findViewById(R.id.nestedScrollView);
        frameLayout = (FrameLayout) findViewById(R.id.content_frame);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        appBarLayout = (AppBarLayout) findViewById(R.id.appbar);


        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
