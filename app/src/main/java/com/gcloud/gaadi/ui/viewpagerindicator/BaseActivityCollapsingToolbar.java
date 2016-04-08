package com.gcloud.gaadi.ui.viewpagerindicator;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gcloud.gaadi.ApplicationController;
import com.gcloud.gaadi.R;
import com.gcloud.gaadi.ui.NoSwipeViewPager;
import com.gcloud.gaadi.utils.GCLog;

public class BaseActivityCollapsingToolbar extends AppCompatActivity {
    //  protected FrameLayout  frameLayout;
    protected Toolbar toolbar;
    protected ImageView toolbar_image;
    protected NoSwipeViewPager viewPager;
    protected TabLayout tabLayout;
    protected FloatingActionButton fab;
    protected TextView sub_tittle;
    protected FrameLayout frameLayout;
    protected NestedScrollView nestedScrollView;
    protected ImageView imgBackDrop;
    protected AppBarLayout appBarLayout;
    protected TextView fab_counter;
    protected RelativeLayout fab_lay_count;

    protected CollapsingToolbarLayout collapsingToolbarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initActivityTransitions();

        setContentView(R.layout.activity_base_coordinator);
        // frameLayout=(FrameLayout)findViewById(R.id.content_frame);
        viewPager = (NoSwipeViewPager) findViewById(R.id.viewpager);
        appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        //ViewCompat.setTransitionName(findViewById(R.id.appbar), "Hello");
        //supportPostponeEnterTransition();
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        fab_counter= (TextView) findViewById(R.id.fab_counter);
        fab_lay_count = (RelativeLayout) findViewById(R.id.fab_lay_count);
        //  sub_tittle= (TextView) findViewById(R.id.sub_tital);
        //   frameLayout=(FrameLayout)findViewById(R.id.content_frame);
        //nestedScrollView=(NestedScrollView)findViewById(R.id.nestedScrollVw);
        //  imgBackDrop=(ImageView)findViewById(R.id.imgBackdrop);
        //  collapsingToolbarLayout= (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


    }
    protected void setFabCounter(int count) {
        if (count > 0)
        {
            if (fab_lay_count.getVisibility() == View.GONE) {

                fab_lay_count.setVisibility(View.VISIBLE);
            }
        }else
        {
            if (fab_lay_count.getVisibility() == View.VISIBLE) {

                fab_lay_count.setVisibility(View.GONE);
            }
        }
        fab_counter.setText(String.valueOf(count));
    }

    protected void setCollapsingTittle(String tittle, String subtittle) {
        collapsingToolbarLayout.setTitle(tittle);
        if (subtittle != null) {
            sub_tittle.setText(subtittle);
        }
        collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));
    }

    protected void setTitleMsg(String msg) {
        toolbar.setTitle(msg);
        setSupportActionBar(toolbar);

    }

    protected void setCollapsingImageToolbar() {
        tabLayout.setVisibility(View.GONE);
        viewPager.setVisibility(View.GONE);
        fab.setVisibility(View.GONE);
        appBarLayout.setLayoutParams(new AppBarLayout.LayoutParams(100, 256));

        // appBarLayout.setLayoutParams(new  AppBarLayout.LayoutParams(appBarLayout.getWidth(),256));

        nestedScrollView.setVisibility(View.VISIBLE);
        imgBackDrop.setVisibility(View.VISIBLE);

    }

    private void initActivityTransitions() {
      //  getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Slide transition = new Slide();
            transition.excludeTarget(android.R.id.statusBarBackground, true);
            getWindow().setEnterTransition(transition);
            getWindow().setReturnTransition(transition);
        }

    }

    private void applyPalette() {
        int primaryDark = getResources().getColor(R.color.primary_dark);
        int primary = getResources().getColor(R.color.primary);
        collapsingToolbarLayout.setContentScrimColor(primary);
        collapsingToolbarLayout.setStatusBarScrimColor(primaryDark);

        supportStartPostponedEnterTransition();
    }

    protected void setupCollapsingToolbarLayout() {

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        if (collapsingToolbarLayout != null) {
            collapsingToolbarLayout.setTitle(toolbar.getTitle());
            //collapsingToolbarLayout.setCollapsedTitleTextColor(0xED1C24);
            //collapsingToolbarLayout.setExpandedTitleColor(0xED1C24);
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        GCLog.e("BaseActivity onPostResume");
        if (!ApplicationController.checkInternetConnectivity()) {
            Snackbar snackbar = Snackbar.make(findViewById(R.id.rootView),
                    "No Connection",
                    Snackbar.LENGTH_LONG)
                    .setAction("Switch ON", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS));
                        }
                    })
                    .setActionTextColor(getResources().getColor(R.color.switch_on_internet_button));
            View snackBarView = snackbar.getView();
            snackBarView.setBackgroundColor(getResources().getColor(R.color.black_semi_transparent));
            snackbar.show();
        }
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
