package com.imageuploadlib.Activity;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.imageuploadlib.R;
import com.imageuploadlib.Utils.Constants;

/**
 * Created by Lakshay on 10-08-2015.
 */
public class ReviewImageActivity extends AppCompatActivity implements View.OnClickListener {

    protected android.support.v7.widget.Toolbar toolbar;
    ImageView bDone, bCancel;
    RelativeLayout relativeLayout;
    Boolean flag=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();
        String imagePath = extras.getString(Constants.IMAGE_PATH);
        String imageName = extras.getString(Constants.IMAGE_NAME);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.review_image_activity);
        relativeLayout=(RelativeLayout)findViewById(R.id.relativeLayout);
        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        TextView tvImageName = (TextView) findViewById(R.id.imageName);
        flag=extras.getBoolean(Constants.FLAG);

        if(flag){

            toolbar.setVisibility(View.VISIBLE);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            tvImageName.setVisibility(View.GONE);
            final Drawable upArrow = ContextCompat.getDrawable(ReviewImageActivity.this, R.drawable.abc_ic_ab_back_material);
            upArrow.setColorFilter(ContextCompat.getColor(ReviewImageActivity.this, R.color.white), PorterDuff.Mode.SRC_ATOP);
            getSupportActionBar().setHomeAsUpIndicator(upArrow);
            relativeLayout.setVisibility(View.GONE);
        }
        else {
            toolbar.setVisibility(View.GONE);
            tvImageName.setVisibility(View.VISIBLE);
            bDone = (ImageView) findViewById(R.id.bDone);
            bCancel = (ImageView) findViewById(R.id.bCancel);
            relativeLayout.setVisibility(View.VISIBLE);
            bCancel.setOnClickListener(this);
            bDone.setOnClickListener(this);
        }

        ImageView ivReview = (ImageView) findViewById(R.id.ivReviewImage);
        if(imageName != null)
        {
            tvImageName.setVisibility(View.VISIBLE);
            tvImageName.setText(imageName);
        }
        else
        {
            tvImageName.setVisibility(View.GONE);
        }

        if (imagePath != null) {
            Glide.with(this).load("file://" + imagePath).into(ivReview);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.bDone) {
            setResult(RESULT_OK);
            finish();
        } else if (id == R.id.bCancel) {
            setResult(RESULT_CANCELED);
            finish();
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }
}
