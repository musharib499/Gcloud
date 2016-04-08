package com.gcloud.gaadi.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gcloud.gaadi.ApplicationController;
import com.gcloud.gaadi.R;
import com.gcloud.gaadi.utils.GCLog;

import java.util.Arrays;

public class BaseActivity extends AppCompatActivity {
    protected FrameLayout frameLayout;
    protected Toolbar toolbar;
    protected TabLayout tabLayout;
    protected LinearLayout lay_sub_tital, lay_coll_sub_title;
    protected TextView sub_tital, coll_title, coll_sub_title;
    protected ImageView make_logo;
    protected TextView fab_counter;

    protected FloatingActionButton fab;
    protected ViewPager viewPager;
    protected RelativeLayout fab_lay_count;
    LinearLayout parentLayout;
    String [] modelsList = {"HTC Desire 526GPLUS dual sim"};

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static boolean hasSoftKeys(WindowManager windowManager) {
        Display d = windowManager.getDefaultDisplay();

        DisplayMetrics realDisplayMetrics = new DisplayMetrics();
        d.getRealMetrics(realDisplayMetrics);

        int realHeight = realDisplayMetrics.heightPixels;
        int realWidth = realDisplayMetrics.widthPixels;

        DisplayMetrics displayMetrics = new DisplayMetrics();
        d.getMetrics(displayMetrics);

        int displayHeight = displayMetrics.heightPixels;
        int displayWidth = displayMetrics.widthPixels;

        return (realWidth - displayWidth) > 0 || (realHeight - displayHeight) > 0;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        setContentView(R.layout.activity_base_activty);
        frameLayout = (FrameLayout) findViewById(R.id.content_frame);
        parentLayout = (LinearLayout) findViewById(R.id.parent_layout);
      //  setFabButton();
        viewPager = (ViewPager) findViewById(R.id.viewPager);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        lay_sub_tital = (LinearLayout) findViewById(R.id.lay_sub_tital);
        sub_tital = (TextView) findViewById(R.id.sub_tital);
        fab_counter = (TextView) findViewById(R.id.fab_counter);
        lay_coll_sub_title = (LinearLayout) findViewById(R.id.lay_coll_sub_title);
        coll_sub_title = (TextView) findViewById(R.id.coll_sub_title);
        coll_title = (TextView) findViewById(R.id.coll_title);
        make_logo = (ImageView) findViewById(R.id.make_logo);
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        fab_lay_count = (RelativeLayout) findViewById(R.id.fab_lay_count);


        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

    }

    public void setFabButton()
    {
        CoordinatorLayout.LayoutParams p = (CoordinatorLayout.LayoutParams) fab.getLayoutParams();
        p.setAnchorId(View.NO_ID);
        fab.setLayoutParams(p);
        fab.setVisibility(View.GONE);
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

    protected void setTitleMsg(String msg) {
        toolbar.setTitle(msg);
        setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setDisplayShowHomeEnabled(true);

    }

    protected void setSubTittle(String tittle, String subtittle) {
        lay_coll_sub_title.setVisibility(View.VISIBLE);
        coll_title.setText(tittle);
        coll_sub_title.setText(subtittle);
        //collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        GCLog.e("BaseActivity onPostResume");
        if (!ApplicationController.checkInternetConnectivity()) {
            Snackbar snackbar = Snackbar.make(findViewById(R.id.parent_layout),
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
  /*  public void onTooggle(View v)
    {
        v.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_FULLSCREEN |View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void adjustLayoutIfNavigationBarExist()
    {
        if (Arrays.asList(modelsList).contains(Build.MODEL)) {
           return;
        }

        boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
        boolean hasHomeKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_HOME);
        boolean hasMenuKey = ViewConfiguration.get(BaseActivity.this).hasPermanentMenuKey();
        Log.e("has backKey", "1" + hasBackKey);
        Log.e("has hasHomeKey", "1" + hasHomeKey);
        Log.e("has hasMenuKey", "1" + hasMenuKey);
        if (hasBackKey && hasHomeKey) {

            // no navigation bar, unless it is enabled in the settings
            Log.e("check run", "true");
           if(hasSoftKeys(getWindowManager()))
            {
              //  parentLayout.setPadding(0, 0, 0, getNavigationBarHeight(BaseActivity.this, getApplicationContext().getResources().getConfiguration().orientation));

            }

        } else {
            // 99% sure there's a navigation bar
            Log.e("check run", "false");

          //  parentLayout.setPadding(0, 0, 0, getNavigationBarHeight(BaseActivity.this, getApplicationContext().getResources().getConfiguration().orientation));
        }
    }

    private int getNavigationBarHeight(Context context, int orientation) {
        Resources resources = context.getResources();

        int id = resources.getIdentifier(
                orientation == Configuration.ORIENTATION_PORTRAIT ? "navigation_bar_height" : "navigation_bar_height_landscape",
                "dimen", "android");
        if (id > 0) {
            return resources.getDimensionPixelSize(id);
        }
        return 0;
    }



}
