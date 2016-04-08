package com.gcloud.gaadi;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.utils.GAHelper;
import com.imageuploadlib.Fragments.CameraItemsFragment;
import com.imageuploadlib.Utils.FileInfo;
import com.imageuploadlib.Utils.PhotoParams;

import java.util.ArrayList;

/**
 * Created by Lakshay on 18-03-2015.
 */
public class PhotoUpload extends FragmentActivity implements CameraItemsFragment.ImagesHandler {

    public static final String KEY_ARRAYLIST_IMAGES = "images";
    private static final String TAG = "PhotoUpload";
    public static final String KEY_ARRAYLIST_DELETED_IMAGES = "deletedImages";
    private ArrayList<?> selectedImages;
    private String propId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.photo_activity);
        Bundle extras = getIntent().getExtras();


        if (extras != null) {
            propId = extras.getString(Constants.STOCK_ID);
            selectedImages = (ArrayList<?>) getIntent().getExtras().getSerializable(KEY_ARRAYLIST_IMAGES);
        }

        PhotoParams params = new PhotoParams(Constants.getImageUploadURL(getApplicationContext()));
        params.setOrientation(PhotoParams.CameraOrientation.LANDSCAPE);

        CameraItemsFragment cameraItemsFragment = CameraItemsFragment.newInstance(this, params, this, selectedImages,
                R.drawable.image_load_default_big, R.drawable.gallery_placeholder);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.photoFragment, cameraItemsFragment).commit();
    }

    @Override
    public void onBackPressed() {

        if (com.imageuploadlib.Utils.ApplicationController.selectedFiles != null) {
            com.imageuploadlib.Utils.ApplicationController.selectedFiles.clear();
        }

        super.onBackPressed();
    }

    @Override
    public void outputImages(ArrayList<FileInfo> files, ArrayList<FileInfo> deletedImages) {
        Intent intent = new Intent();
        Bundle args = new Bundle();
        args.putSerializable(KEY_ARRAYLIST_IMAGES, files);
        if (deletedImages != null) {
            args.putSerializable(KEY_ARRAYLIST_DELETED_IMAGES, deletedImages);
        }
        intent.putExtras(args);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void dragImagesHandler(int first, int second) {
        for (int i = 0; i < ApplicationController.orderImages.size(); i++) {
            Log.e(TAG, "Order : " + ApplicationController.orderImages.get(i));
        }
    }

    @Override
    public void gaHandler(String screen, String category, String action, String label, Long value) {
        ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER, screen, category, action, label, value);
    }
}
