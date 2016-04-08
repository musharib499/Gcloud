package com.imageuploadlib.Activity;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.imageuploadlib.R;
import com.imageuploadlib.Utils.Constants;
import com.imageuploadlib.Utils.ReCameraPreview;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Lakshay on 22-06-2015.
 */
public class ReCameraPriorityActivity extends AppCompatActivity implements View.OnClickListener,
        Camera.ShutterCallback, ReCameraPreview.ReadyToTakePicture, Camera.PictureCallback {

    private static final String TAG = "ReCameraPriority";
    Camera mCamera;
    Boolean readyToTakePicture = true;
    ReCameraPreview cameraPreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_priority_items);
        ImageButton button = (ImageButton) findViewById(R.id.buttonCapture);
        button.setOnClickListener(this);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mCamera == null) {
            mCamera = getCameraInstance();
        }
        setCameraOrientation(mCamera, this);
        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.camera_preview);
        cameraPreview = new ReCameraPreview(this, mCamera);
        cameraPreview.setReadyListener(this);
        frameLayout.addView(cameraPreview);
    }

    public static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK); // attempt to get a Camera instance
        } catch (Exception e) {
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }

    private void setCameraOrientation(Camera mCamera, Activity mActivity) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(Camera.CameraInfo.CAMERA_FACING_BACK, info);
        int rotation = mActivity.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        mCamera.setDisplayOrientation(result);
    }

    @Override
    public void onShutter() {
        Log.e(TAG, "onShutter");
    }

    @Override
    public void readyToTakePicture(boolean ready) {
        readyToTakePicture = ready;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.buttonCapture) {
            if (readyToTakePicture) {
                mCamera.takePicture(this, null, this);
                readyToTakePicture = false;
            }
        }
    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        Log.e(TAG, "onPictureTaken");
        readyToTakePicture = true;
//        mCamera.startPreview();

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
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mCamera.startPreview();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e(TAG, "onPause");
        try {
            mCamera.setPreviewCallback(null);
            cameraPreview.getHolder().removeCallback(cameraPreview);
//            mPreview.getHolder().removeCallback(mPreview);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }

    }
}
