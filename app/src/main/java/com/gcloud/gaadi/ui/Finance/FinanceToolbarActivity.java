package com.gcloud.gaadi.ui.Finance;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.gcloud.gaadi.R;

/**
 * Created by Manish on 9/16/2015.
 */
public class FinanceToolbarActivity extends AppCompatActivity {
    protected FrameLayout myFrameLayout;
    protected android.support.v7.widget.Toolbar toolbar;
    private static View circleView1, circleView2, circleView3, circleView4;
    private static TextView tvDigit1, tvDigit2, tvDigit3, tvDigit4;
    private static Context context;
    CoordinatorLayout coordinatorLayout;
    AppBarLayout appBarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = FinanceToolbarActivity.this;
        setContentView(R.layout.activity_finance_toolbar);
        initializeWidgets();
    }

    private void initializeWidgets() {
        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        myFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        circleView1 = (View) findViewById(R.id.view1);
        circleView2 = (View) findViewById(R.id.view2);
        circleView3 = (View) findViewById(R.id.view3);
        circleView4 = (View) findViewById(R.id.view4);
        tvDigit1 = (TextView) findViewById(R.id.tv_circle_number1);
        tvDigit2 = (TextView) findViewById(R.id.tv_circle_number2);
        tvDigit3 = (TextView) findViewById(R.id.tv_circle_number3);
        tvDigit4 = (TextView) findViewById(R.id.tv_circle_number4);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        appBarLayout = (AppBarLayout) findViewById(R.id.appBarLayout);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title);
        toolbar.setTitle(title);
    }

    public Toolbar getToolbar() {
        return toolbar;
    }

    public static void setStepProgress(int i) {
        switch (i) {
            case 1:
                circleView1.setBackgroundResource(R.drawable.white_circle);
                circleView2.setBackgroundResource(R.drawable.light_pink_circle);
                circleView3.setBackgroundResource(R.drawable.light_pink_circle);
                circleView4.setBackgroundResource(R.drawable.light_pink_circle);
                tvDigit1.setTextColor(context.getResources().getColor(R.color.dark_red));
                tvDigit2.setTextColor(context.getResources().getColor(R.color.white));
                tvDigit3.setTextColor(context.getResources().getColor(R.color.white));
                tvDigit4.setTextColor(context.getResources().getColor(R.color.white));
                break;
            case 2:
                circleView2.setBackgroundResource(R.drawable.white_circle);
                circleView1.setBackgroundResource(R.drawable.light_pink_circle);
                circleView3.setBackgroundResource(R.drawable.light_pink_circle);
                circleView4.setBackgroundResource(R.drawable.light_pink_circle);
                tvDigit2.setTextColor(context.getResources().getColor(R.color.dark_red));
                tvDigit1.setTextColor(context.getResources().getColor(R.color.white));
                tvDigit3.setTextColor(context.getResources().getColor(R.color.white));
                tvDigit4.setTextColor(context.getResources().getColor(R.color.white));
                break;
            case 3:
                circleView3.setBackgroundResource(R.drawable.white_circle);
                circleView2.setBackgroundResource(R.drawable.light_pink_circle);
                circleView1.setBackgroundResource(R.drawable.light_pink_circle);
                circleView4.setBackgroundResource(R.drawable.light_pink_circle);
                tvDigit3.setTextColor(context.getResources().getColor(R.color.dark_red));
                tvDigit2.setTextColor(context.getResources().getColor(R.color.white));
                tvDigit1.setTextColor(context.getResources().getColor(R.color.white));
                tvDigit4.setTextColor(context.getResources().getColor(R.color.white));
                break;
            case 4:
                circleView4.setBackgroundResource(R.drawable.white_circle);
                circleView2.setBackgroundResource(R.drawable.light_pink_circle);
                circleView3.setBackgroundResource(R.drawable.light_pink_circle);
                circleView1.setBackgroundResource(R.drawable.light_pink_circle);
                tvDigit4.setTextColor(context.getResources().getColor(R.color.dark_red));
                tvDigit2.setTextColor(context.getResources().getColor(R.color.white));
                tvDigit3.setTextColor(context.getResources().getColor(R.color.white));
                tvDigit1.setTextColor(context.getResources().getColor(R.color.white));
                break;
        }
    }
}
