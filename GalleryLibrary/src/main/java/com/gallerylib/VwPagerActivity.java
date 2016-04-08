package com.gallerylib;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.gallerylib.adapters.VwPagerAdapter;
import com.gallerylib.interfaces.IPositionHandler;
import com.gallerylib.interfaces.IViewAnimation;

import java.util.ArrayList;


public class VwPagerActivity extends BaseActivityGallery implements ViewPager.OnPageChangeListener {

    public static final String IMAGE_PARAMS = "imageParams";
    static IPositionHandler posHandler;
    static IViewAnimation viewAnimation;
    ArrayList<String> imgUrlsList;
    int posSelected;
    ImageView iv_displayGridList, closeActivityImgVw;
    TextView tv_currentPage;
    ViewPager imagesPager;
    ParamsToDisplay paramsToDisplay;
    String modelVersion;
    // private ActionBar mActionBar;

    public static Intent makeNewInstance(ParamsToDisplay params, Activity mContext) {
        if (mContext instanceof IPositionHandler)
            posHandler = (IPositionHandler) mContext;
        if (mContext instanceof IViewAnimation)
            viewAnimation = (IViewAnimation) mContext;
        Intent intent = new Intent(mContext, VwPagerActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(IMAGE_PARAMS, params);
        intent.putExtras(bundle);
        return intent;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        getLayoutInflater().inflate(R.layout.activity_vw_pager, frameLayout);
        Animation anim = AnimationUtils.loadAnimation(VwPagerActivity.this, R.anim.translate_vwpager);
        toolbar.setVisibility(View.GONE);
     /*   mActionBar = getSupportActionBar();
        mActionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.transparent_vwPager)));
        mActionBar.setDisplayShowTitleEnabled(false);
        mActionBar.setDisplayShowHomeEnabled(false);
       // mActionBar.setDisplayShowCustomEnabled(true);
       // mActionBar.setCustomView(R.layout.custom_actionbar_stocks);
       // mActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        mActionBar.setDisplayHomeAsUpEnabled(true);*/

        // imageLabel = (TextView) mActionBar.getCustomView().findViewById(R.id.label);

        imagesPager = (ViewPager) findViewById(R.id.imagesPager);
        tv_currentPage = (TextView) findViewById(R.id.tv_currentPage);
        if (savedInstanceState != null) {
            paramsToDisplay = (ParamsToDisplay) savedInstanceState.getSerializable(IMAGE_PARAMS);
            imgUrlsList = paramsToDisplay.getImagesUrlList();
            modelVersion = paramsToDisplay.getMakeModelVersion();
            posSelected = savedInstanceState.getInt("POSITION");
        } else if (getIntent().getExtras() != null) {
            Bundle b = getIntent().getExtras();

            paramsToDisplay = (ParamsToDisplay) b.getSerializable(IMAGE_PARAMS);
            imgUrlsList = paramsToDisplay.getImagesUrlList();
            modelVersion = paramsToDisplay.getMakeModelVersion();
            posSelected = paramsToDisplay.getImgSelectedPosition();
        }

        VwPagerAdapter imagesAdapter = new VwPagerAdapter(this, imgUrlsList);
        imagesPager.setAdapter(imagesAdapter);
        imagesPager.setOffscreenPageLimit(2);
        imagesPager.setCurrentItem(posSelected);
        imagesPager.setOnPageChangeListener(this);
        if (viewAnimation != null)
            viewAnimation.applyAnimation(imagesPager);
      /*  Animation fade_in = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);

        fade_in.setInterpolator(new AccelerateInterpolator());
        fade_in.setDuration(2000);

        mActionBar.getCustomView().startAnimation(fade_in);*/
        tv_currentPage.setText((imagesPager.getCurrentItem() + 1) + " of " + imgUrlsList.size());

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


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        //  imageLabel.setText(modelVersion);
    }

    @Override
    public void onPageSelected(int position) {
        //  imageLabel.setText(modelVersion);
        tv_currentPage.setText(position + 1 + " of " + imgUrlsList.size());
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onBackPressed() {
        if (posHandler != null)
            posHandler.positionHandlerCallBack(imagesPager.getCurrentItem());
        VwPagerActivity.this.finish();
        super.onBackPressed();
    }

    public void animationSet() {
        // Scaling
        Animation scale = new ScaleAnimation(0.5f, 1.5f, 0.5f, 1.5f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
// 1 second duration
        scale.setDuration(1000);
// Moving up
        Animation slideUp = new TranslateAnimation(100, 0, -100, 0);
// 1 second duration
        slideUp.setDuration(1000);
// Animation set to join both scaling and moving
        AnimationSet animSet = new AnimationSet(true);
        animSet.setFillEnabled(true);
        //animSet.addAnimation(scale);
        animSet.addAnimation(slideUp);
// Launching animation set
        imagesPager.startAnimation(animSet);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(IMAGE_PARAMS, paramsToDisplay);
        outState.putInt("POSITION", imagesPager.getCurrentItem());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        paramsToDisplay = (ParamsToDisplay) savedInstanceState.getSerializable(IMAGE_PARAMS);
        posSelected = savedInstanceState.getInt("POSITION");
    }
}
