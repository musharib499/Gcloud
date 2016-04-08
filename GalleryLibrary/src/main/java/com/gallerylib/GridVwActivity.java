package com.gallerylib;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


public class GridVwActivity extends BaseActivityGallery implements View.OnClickListener {
    ImageView closeActivityImgVw;
    TextView tv_imagesCount;
    LinearLayout lay_actionBar;
    // private ActionBar mActionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid_vw);
        ///  mActionBar = getSupportActionBar();
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        //   toolbar.add(R.layout.gridvw_custom_actionbar);
        //   mActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        lay_actionBar = (LinearLayout) findViewById(R.id.lay_actionbar);
        lay_actionBar.setVisibility(View.VISIBLE);
        // Toolbar parent = (Toolbar) mActionBar.getCustomView().getParent();//first get parent toolbar of current action bar
        /// parent.setContentInsetsAbsolute(0, 0);
        closeActivityImgVw = (ImageView) toolbar.findViewById(R.id.closeImgVw);
        closeActivityImgVw.setOnClickListener(this);

        tv_imagesCount = (TextView) toolbar.findViewById(R.id.images_count);

        tv_imagesCount.setText("Gallery");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_grid_vw, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.closeImgVw) {
            GridVwActivity.this.finish();
            overridePendingTransition(0, R.anim.mapview_slide_down);
        }
    }
}
