package com.imageuploadlib.Activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import com.imageuploadlib.Fragments.CameraItemsFragment;
import com.imageuploadlib.R;
import com.imageuploadlib.Utils.Constants;
import com.imageuploadlib.Utils.FileInfo;
import com.imageuploadlib.Utils.PhotoParams;

import java.util.ArrayList;

public class NeutralActivity extends FragmentActivity implements CameraItemsFragment.ImagesHandler {

    public static final String KEY_ARRAYLIST_IMAGES = "images";
    private static final String TAG = "PhotoUpload";
    public static final String KEY_ARRAYLIST_DELETED_IMAGES = "deletedImages";
    private ArrayList<?> selectedImages;
    private String propId;
    private CameraItemsFragment cameraItemsFragment;
    //  GAHelper gaHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_neutral);
        Bundle extras = getIntent().getExtras();

        // gaHelper = new GAHelper(this);
        PhotoParams params = (PhotoParams) getIntent().getSerializableExtra(CameraItemsFragment.PHOTO_PARAMS);

        if (params != null) {
            selectedImages = params.getImagePathList();
        } else {
            params = new PhotoParams();
        }
      /*  if (extras != null) {
            propId = extras.getString(Constants.STOCK_ID);
            selectedImages = (ArrayList<?>) getIntent().getExtras().getSerializable(KEY_ARRAYLIST_IMAGES);
        }*/


        params.setOrientation(PhotoParams.CameraOrientation.LANDSCAPE);

        cameraItemsFragment = CameraItemsFragment.newInstance(this, params, this, selectedImages,
                R.drawable.image_load_default_big, R.drawable.image_load_default_small);
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
      /*  for (int i = 0; i < ApplicationController.orderImages.size(); i++) {
            Log.e(TAG, "Order : " + ApplicationController.orderImages.get(i));
        }*/
    }

    @Override
    public void gaHandler(String screen, String category, String action, String label, Long value) {
        //gaHelper.sendEvent(GAHelper.TrackerName.APP_TRACKER, screen, category, action, label, value);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Constants.REQUEST_PERMISSION_CAMERA) {
            if (grantResults.length > 1
                    && (grantResults[0] != PackageManager.PERMISSION_GRANTED
                            || grantResults[1] != PackageManager.PERMISSION_GRANTED)) {
                return;
            }
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                cameraItemsFragment.onActivityResult(requestCode,
                        RESULT_OK, null);
            }
        } else if (requestCode == Constants.REQUEST_PERMISSION_READ_EXTERNAL_STORAGE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                cameraItemsFragment.onActivityResult(requestCode,
                        RESULT_OK, null);
            }
        }
    }
}
