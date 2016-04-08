package com.imageuploadlib.Activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.imageuploadlib.Adapters.CapturedImagesAdapter;
import com.imageuploadlib.Fragments.CameraItemsFragment;
import com.imageuploadlib.R;
import com.imageuploadlib.Utils.CameraPreview;
import com.imageuploadlib.Utils.Constants;
import com.imageuploadlib.Utils.FileInfo;
import com.imageuploadlib.Utils.PhotoParams;
import com.imageuploadlib.Utils.PhotoParams.CameraOrientation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

//import android.hardware.camera2.CameraManager;

/**
 * Created by Lakshay on 18-02-2015.
 */
public class CameraActivity extends Activity implements View.OnClickListener, Camera.PictureCallback {

    private static final String TAG = "CameraActivity";
    public static final String CAMERA_IMAGES = "captured_images";
    private Camera camera;
    private ImageView capturedImageView;
    CameraPreview cameraPreview;
    private LinearLayout llCapturedImages;
    LinearLayout llActionsCamera;
    int maxCount;

    public static Boolean readyToTakePicture = false;

    private ArrayList<FileInfo> imagesList = new ArrayList<FileInfo>();
    private CapturedImagesAdapter imagesAdapter;
    private ListView lvCaptureImages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_items);
        llActionsCamera = (LinearLayout) findViewById(R.id.llActionsCamera);
        PhotoParams params = (PhotoParams) getIntent().getSerializableExtra(CameraItemsFragment.PHOTO_PARAMS);
        maxCount = getIntent().getIntExtra(GalleryActivity.MAX_COUNT, 0);
        if (params != null) {
            CameraOrientation orientation = params.getOrientation();
            setOrientation(orientation);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (camera == null) {
            try {
                camera = Camera.open();

                cameraPreview = new CameraPreview(this, camera);
                FrameLayout camera_lLayout = (FrameLayout) findViewById(R.id.camera_preview);
                camera_lLayout.addView(cameraPreview);

                //set the screen layout to fullscreen
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                        WindowManager.LayoutParams.FLAG_FULLSCREEN);

                findViewById(R.id.button_capture).setOnClickListener(this);
                findViewById(R.id.bDone).setOnClickListener(this);
                findViewById(R.id.bBack).setOnClickListener(this);

                capturedImageView = (ImageView) findViewById(R.id.ivCaptured);
                lvCaptureImages = (ListView) findViewById(R.id.lvCapturedImages);
                imagesAdapter = new CapturedImagesAdapter(this, imagesList);
                lvCaptureImages.setAdapter(imagesAdapter);
                imagesAdapter.notifyDataSetChanged();
            } catch (Exception e) {
                Log.e("Camera Open Exception", "" + e.getMessage());
            }
        } else {
            Log.e(TAG, "camera not null");
        }
    }

    private void setOrientation(CameraOrientation orientation) {
        if (orientation != null) {
            if (orientation.equals(CameraOrientation.LANDSCAPE)) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            } else if (orientation.equals(CameraOrientation.PORTRAIT)) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button_capture) {
            if (imagesAdapter.getImageInfoArrayList().size() >= maxCount) {
                Toast.makeText(CameraActivity.this, "Maximum no. of images allowed is " + maxCount, Toast.LENGTH_LONG).show();
                return;
            }
            if (readyToTakePicture) {
                camera.takePicture(null, null, this);
                readyToTakePicture = false;
                llActionsCamera.setEnabled(false);
            }
        } else if (v.getId() == R.id.bBack) {
            finish();
        } else if (v.getId() == R.id.bDone) {
            Intent intent = new Intent();
            intent.putExtra(CAMERA_IMAGES, imagesAdapter.getImageInfoArrayList());
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    //updates the listview with the photos clicked by the camera
    private void updateCapturedPhotos(File pictureFile) {
        FileInfo fileInfo = new FileInfo();
        fileInfo.setFilePath(pictureFile.getAbsolutePath());
        fileInfo.setFileName(pictureFile.getAbsolutePath().substring(pictureFile.getAbsolutePath().lastIndexOf("/") + 1));
        fileInfo.setSource(FileInfo.SOURCE.PHONE_CAMERA);
//        imagesList.add(imageInfo);
        imagesAdapter.getImageInfoArrayList().add(fileInfo);
        imagesAdapter.notifyDataSetChanged();
        lvCaptureImages.smoothScrollToPosition(imagesAdapter.getCount() - 1);
        camera.startPreview();
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            camera.setPreviewCallback(null);
            cameraPreview.getHolder().removeCallback(cameraPreview);
//            mPreview.getHolder().removeCallback(mPreview);
            camera.stopPreview();
            camera.release();
            camera = null;
        } catch (Exception e) {
            //Log.e(TAG , e.getMessage());
        }

    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {

        File pictureFile = Constants.getMediaOutputFile(Constants.TYPE_IMAGE);
        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                Uri.parse("file://" + pictureFile.getAbsolutePath())));
        if (pictureFile == null) {
            Log.d(TAG, "Error creating media file, check storage permissions: " +
                    "");
            return;
        }
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            fos.write(data);
            fos.close();
            updateCapturedPhotos(pictureFile);
            readyToTakePicture = true;
            llActionsCamera.setEnabled(true);
        } catch (FileNotFoundException e) {
            Log.d(TAG, "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d(TAG, "Error accessing file: " + e.getMessage());
        }
    }
}
