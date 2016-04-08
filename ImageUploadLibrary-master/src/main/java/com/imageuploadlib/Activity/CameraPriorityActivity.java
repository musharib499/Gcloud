package com.imageuploadlib.Activity;

import android.content.Intent;
import android.graphics.Rect;
import android.graphics.RectF;
import android.hardware.Camera;
import android.media.ExifInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.imageuploadlib.Fragments.CameraItemsFragment;
import com.imageuploadlib.Fragments.CameraPriorityFragment;
import com.imageuploadlib.R;
import com.imageuploadlib.Utils.CameraPreview;
import com.imageuploadlib.Utils.Constants;
import com.imageuploadlib.Utils.DrawingView;
import com.imageuploadlib.Utils.FileInfo;
import com.imageuploadlib.Utils.PhotoParams;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CameraPriorityActivity extends AppCompatActivity
        implements View.OnTouchListener {

    private static final String TAG = "CameraPriorityActivity";
    public static final int GALLERY_PICK = 99;
    public static final String FROM_PRIORITY_ACTIVITY = "fromPriorityActivity";
    private Camera camera;
    PhotoParams photoParams;
    public Boolean readyToTakePicture = false;
    CameraPreview cameraPreview;

    //Things to be restored on config change
    private ArrayList<FileInfo> imagesList = new ArrayList<>();

    private int FOCUS_AREA_SIZE = 200;
    DrawingView drawingView;
    FrameLayout camera_lLayout;

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.slide_out_bottom);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_priority_items);

        photoParams = (PhotoParams) getIntent().getSerializableExtra(CameraItemsFragment.PHOTO_PARAMS);
        CameraPriorityFragment fragment = CameraPriorityFragment.getInstance(photoParams);
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.content_frame, fragment).commit();
    }


    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            camera.setPreviewCallback(null);
            cameraPreview.getHolder().removeCallback(cameraPreview);
            camera.stopPreview();
            camera_lLayout.removeAllViews();
            camera.release();
            camera = null;
            cameraPreview = null;
        } catch (Exception e) {
           // Log.e(TAG, e.getMessage());
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable(Constants.IMAGES_SELECTED, imagesList);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        if (camera != null) {
//            Camera camera = mCamera.getCamera();
            camera.cancelAutoFocus();
            final Rect focusRect = calculateTapArea(event.getX(), event.getY(), 1f);

            Camera.Parameters parameters = camera.getParameters();
            if (parameters.getFocusMode() != Camera.Parameters.FOCUS_MODE_AUTO) {
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
            }
            if (parameters.getMaxNumFocusAreas() > 0) {
                List<Camera.Area> mylist = new ArrayList<Camera.Area>();
                mylist.add(new Camera.Area(focusRect, 1000));
                parameters.setFocusAreas(mylist);
            }

            try {
                camera.cancelAutoFocus();
                camera.setParameters(parameters);
                camera.startPreview();
                camera.autoFocus(new Camera.AutoFocusCallback() {
                    @Override
                    public void onAutoFocus(boolean success, Camera camera) {
//                        if (camera.getParameters().getFocusMode() != Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE) {
//                            Camera.Parameters parameters = camera.getParameters();
//                            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
//                            if (parameters.getMaxNumFocusAreas() > 0) {
//                                parameters.setFocusAreas(null);
//                            }
//                            camera.setParameters(parameters);
//                            camera.startPreview();   //causing crash here
//                        }
                    }
                });

                drawingView.setHaveTouch(true, focusRect);
                drawingView.invalidate();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        drawingView.setHaveTouch(false, focusRect);
                        drawingView.invalidate();
                    }
                }, 1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    private Rect calculateTapArea(float x, float y, float coefficient) {
        int areaSize = Float.valueOf(FOCUS_AREA_SIZE * coefficient).intValue();

        int left = clamp((int) x - areaSize / 2, 0, cameraPreview.getWidth() - areaSize);
        int top = clamp((int) y - areaSize / 2, 0, cameraPreview.getHeight() - areaSize);

        RectF rectF = new RectF(left, top, left + areaSize, top + areaSize);
//        matrix.mapRect(rectF);

        return new Rect(Math.round(rectF.left), Math.round(rectF.top), Math.round(rectF.right), Math.round(rectF.bottom));
    }

    private int clamp(int x, int min, int max) {
        if (x > max) {
            return max;
        }
        if (x < min) {
            return min;
        }
        return x;
    }

    private void focusOnTouch(MotionEvent event) {
        if (camera != null) {

            Camera.Parameters parameters = camera.getParameters();
            if (parameters.getMaxNumMeteringAreas() > 0) {
                Log.i(TAG, "fancy !");
                Rect rect = calculateFocusArea(event.getX(), event.getY());

                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                List<Camera.Area> meteringAreas = new ArrayList<Camera.Area>();
                meteringAreas.add(new Camera.Area(rect, 800));
                parameters.setFocusAreas(meteringAreas);

                camera.setParameters(parameters);
                camera.autoFocus(mAutoFocusTakePictureCallback);
            } else {
                camera.autoFocus(mAutoFocusTakePictureCallback);
            }
        }
    }

    private Camera.AutoFocusCallback mAutoFocusTakePictureCallback = new Camera.AutoFocusCallback() {
        @Override
        public void onAutoFocus(boolean success, Camera camera) {
            if (success) {
                // do something...
                Log.i("tap_to_focus", "success!");
            } else {
                // do something...
                Log.i("tap_to_focus", "fail!");
            }
        }
    };

    private Rect calculateFocusArea(float x, float y) {
        int left = clamp(Float.valueOf((x / cameraPreview.getWidth()) * 2000 - 1000).intValue(), FOCUS_AREA_SIZE);
        int top = clamp(Float.valueOf((y / cameraPreview.getHeight()) * 2000 - 1000).intValue(), FOCUS_AREA_SIZE);

        return new Rect(left, top, left + FOCUS_AREA_SIZE, top + FOCUS_AREA_SIZE);
    }

    private int clamp(int touchCoordinateInCameraReper, int focusAreaSize) {
        int result;
        if (Math.abs(touchCoordinateInCameraReper) + focusAreaSize / 2 > 1000) {
            if (touchCoordinateInCameraReper > 0) {
                result = 1000 - focusAreaSize / 2;
            } else {
                result = -1000 + focusAreaSize / 2;
            }
        } else {
            result = touchCoordinateInCameraReper - focusAreaSize / 2;
        }
        return result;
    }

    private int getExifRotation(File pictureFile) {
        ExifInterface ei = null;
        try {
            ei = new ExifInterface(pictureFile.getPath());
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            Log.e(Constants.TAG, "Exif orientation : " + orientation);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    return 90;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    return 180;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    return 270;
                case ExifInterface.ORIENTATION_UNDEFINED:
                    return 90;
                default:
                    return 0;
                // etc.
            }
        } catch (IOException e) {
            Log.e(Constants.TAG, "Exception Message : " + e.getMessage());
        }
        return 0;
    }
}
