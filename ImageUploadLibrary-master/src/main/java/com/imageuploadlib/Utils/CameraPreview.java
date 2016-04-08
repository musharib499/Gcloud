package com.imageuploadlib.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Build;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.imageuploadlib.Activity.CameraActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lakshay on 18-02-2015.
 */
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {

    private static final String TAG = "CameraPreview";
    Camera mCamera;
    List<Camera.Size> mSupportedPreviewSizes;
    List<Camera.Size> mSupportedPictureSizes;
    Camera.Size mPreviewSize;
    Camera.Size mPictureSize;
    private SurfaceHolder holder;
    Display display;
    Activity mActivity;

    private static final int DEGREES_0 = 0;
    private static final int DEGREES_90 = 90;
    private static final int DEGREES_180 = 180;
    private static final int DEGREES_270 = 270;

    private ReadyToTakePicture readyListener = null;

    public CameraPreview(Activity context, Camera camera) {
        super(context);
        mActivity = context;
        Log.e(TAG, "constructor camerapreview");
        mCamera = camera;
        holder = getHolder();
        holder.addCallback(this);
        Camera.Parameters parameters = mCamera.getParameters();
        mSupportedPreviewSizes = parameters.getSupportedPreviewSizes();
        mSupportedPictureSizes = parameters.getSupportedPictureSizes();
        display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        configureCamera(getResources().getConfiguration());
    }

    private void configureCamera(Configuration configuration) {
        try {

            if (mCamera != null) {
                int width = getScreenWidth();
                int height = getScreenHeight();

                int displayOrientationDegrees = getDisplayOrientationDegrees(display);
                mCamera.setDisplayOrientation(displayOrientationDegrees);

                Camera.Size previewSize = mCamera.getParameters().getPreviewSize();
                float aspect = (float) previewSize.width / previewSize.height;

                Log.e(Constants.TAG, "Aspect : " + aspect);

                ViewGroup.LayoutParams cameraHolderParams = getLayoutParams();
                if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                    cameraHolderParams.height = height;
                    cameraHolderParams.width = (int) (height / aspect);

                    Log.e(Constants.TAG, " Camera width : " + cameraHolderParams.width + " Camera Height : " + cameraHolderParams.height);

                } else {
                    cameraHolderParams.width = width;
                    cameraHolderParams.height = (int) (width / aspect);

                    Log.e(Constants.TAG, " Camera width : " + cameraHolderParams.width + " Camera Height : " + cameraHolderParams.height);
                }

                setLayoutParams(cameraHolderParams);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private int getScreenWidth() {
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR2)
            return display.getWidth();
        else {
            Point size = new Point();
            display.getSize(size);
            return size.x;
        }
    }

    private int getDisplayOrientationDegrees(Display display) {
        int displayOrientationDegrees;
        int orientation = getResources().getConfiguration().orientation;

        switch (display.getRotation()) {
            case Surface.ROTATION_0:
                if (orientation == Configuration.ORIENTATION_PORTRAIT)
                    displayOrientationDegrees = DEGREES_90;
                else displayOrientationDegrees = DEGREES_0;
                break;
            case Surface.ROTATION_90:
                if (orientation == Configuration.ORIENTATION_LANDSCAPE)
                    displayOrientationDegrees = DEGREES_0;
                else displayOrientationDegrees = DEGREES_270;
                break;
            case Surface.ROTATION_180:
                if (orientation == Configuration.ORIENTATION_PORTRAIT)
                    displayOrientationDegrees = DEGREES_270;
                else displayOrientationDegrees = DEGREES_180;
                break;
            case Surface.ROTATION_270:
                if (orientation == Configuration.ORIENTATION_LANDSCAPE)
                    displayOrientationDegrees = DEGREES_180;
                else displayOrientationDegrees = DEGREES_90;
                break;
            default:
                displayOrientationDegrees = DEGREES_0;
        }

        return displayOrientationDegrees;
    }

    private int getScreenHeight() {
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR2)
            return display.getHeight();
        else {
            Point size = new Point();
            display.getSize(size);
            return size.y;
        }
    }

//    @Override
//    public void surfaceCreated(SurfaceHolder holder) {
//        try {
//            Log.e(TAG , "surface created");
//            mCamera.setPreviewDisplay(holder);
//            mCamera.startPreview();
//        } catch (IOException e) {
//
//            Log.e("Exception ", ""+e.getMessage().toString());
//        }
//    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        try {
            Log.e(TAG, "surface created");
            //set camera to continually auto-focus
            Camera.Parameters params = mCamera.getParameters();
//            if (params.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
//                params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
//            } else {
//                Choose another supported mode
//            }

            params.setPictureSize(mPictureSize.width, mPictureSize.height);
            // params.setPictureSize(1280,720);
            mCamera.setParameters(params);
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();

        } catch (IOException e) {

            Log.e("Exception ", "" + e.getMessage().toString());
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int width = resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        final int height = resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        setMeasuredDimension(width, height);

        if (mSupportedPreviewSizes != null) {
            mPreviewSize = getOptimalPreviewSize(mSupportedPreviewSizes, width, height);
        }
        if (mSupportedPictureSizes != null) {
            mPictureSize = getOptimalPreviewSize(mSupportedPictureSizes, 900, 1280);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.e(TAG, "Surface changed");
        try {
            mCamera.stopPreview();
        } catch (Exception e) {
            // ignore: tried to stop a non-existent preview
            Log.e(TAG, e.getMessage());
        }

        // set preview size and make any resize, rotate or
        // reformatting changes here

        // start preview with new settings
        try {
            Camera.Parameters parameters = mCamera.getParameters();
            parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
            holder.addCallback(this);
            mCamera.setPreviewDisplay(holder);
            mCamera.setParameters(parameters);
            mCamera.startPreview();
            CameraActivity.readyToTakePicture = true;
            if (readyListener != null)
                readyListener.readyToTakePicture(true);

        } catch (Exception e) {
            Log.e(TAG, "" + e.getMessage());
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
//        holder.removeCallback(this);
//        mCamera.stopPreview();
        CameraActivity.readyToTakePicture = false;
        if (readyListener != null)
            readyListener.readyToTakePicture(false);
//        mCamera.release();
        Log.e("Camera Preview", "Surface Destroyed");
    }

    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) h / w;

        if (sizes == null) return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }

//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//
//        if(event.getAction() == MotionEvent.ACTION_DOWN){
//            float x = event.getX();
//            float y = event.getY();
//
//            Rect touchRect = new Rect(
//                    (int)(x - 100),
//                    (int)(y - 100),
//                    (int)(x + 100),
//                    (int)(y + 100));
//
//
//            final Rect targetFocusRect = new Rect(
//                    touchRect.left * 2000/this.getWidth() - 1000,
//                    touchRect.top * 2000/this.getHeight() - 1000,
//                    touchRect.right * 2000/this.getWidth() - 1000,
//                    touchRect.bottom * 2000/this.getHeight() - 1000);
//
//            doTouchFocus(targetFocusRect);
////            if (drawingViewSet) {
////                drawingView.setHaveTouch(true, touchRect);
////                drawingView.invalidate();
////
////                // Remove the square indicator after 1000 msec
////                Handler handler = new Handler();
////                handler.postDelayed(new Runnable() {
////
////                    @Override
////                    public void run() {
////                        drawingView.setHaveTouch(false, new Rect(0,0,0,0));
////                        drawingView.invalidate();
////                    }
////                }, 1000);
////            }
//        }
//        return true;
//    }

    public void doTouchFocus(final Rect tfocusRect) {
        try {
            List<Camera.Area> focusList = new ArrayList<Camera.Area>();
            Camera.Area focusArea = new Camera.Area(tfocusRect, 1000);
            focusList.add(focusArea);

            Camera.Parameters param = mCamera.getParameters();
            param.setFocusAreas(focusList);
            param.setMeteringAreas(focusList);
            mCamera.setParameters(param);

            mCamera.autoFocus(myAutoFocusCallback);
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, "Unable to autofocus");
        }
    }

    Camera.AutoFocusCallback myAutoFocusCallback = new Camera.AutoFocusCallback() {

        @Override
        public void onAutoFocus(boolean arg0, Camera arg1) {
            if (arg0) {
                mCamera.cancelAutoFocus();
            }
        }
    };
    //    private void focusOnTouch(MotionEvent event) {
//        if (mCamera != null) {
//
//            mCamera.cancelAutoFocus();
//            Rect focusRect = calculateTapArea(event.getX(), event.getY(), 1f);
//            Rect meteringRect = calculateTapArea(event.getX(), event.getY(), 1.5f);
//
//            Camera.Parameters parameters = mCamera.getParameters();
//            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
//            ArrayList<Camera.Area> list = new ArrayList<>();
//            list.add(new Camera.Area(focusRect, 1000));
//            parameters.setFocusAreas(list);
//
//
//            ArrayList<Camera.Area> list1 = new ArrayList<>();
//            list1.add(new Camera.Area(meteringRect, 1000));
//            if (meteringAreaSupported) {
//                parameters.setMeteringAreas(list1);
//            }
//
//            mCamera.setParameters(parameters);
//            mCamera.autoFocus(this);
//        }
//    }

//    private Rect calculateTapArea(float x, float y, float coefficient) {
//        int areaSize = Float.valueOf(focusAreaSize * coefficient).intValue();
//
//        int left = clamp((int) x - areaSize / 2, 0, mPreviewSize.width - areaSize);
//        int top = clamp((int) y - areaSize / 2, 0, mPreviewSize.height - areaSize);
//
//        RectF rectF = new RectF(left, top, left + areaSize, top + areaSize);
//        matrix.mapRect(rectF);
//
//        return new Rect(Math.round(rectF.left), Math.round(rectF.top), Math.round(rectF.right), Math.round(rectF.bottom));
//    }

    private int clamp(int x, int min, int max) {
        if (x > max) {
            return max;
        }
        if (x < min) {
            return min;
        }
        return x;
    }


    public interface ReadyToTakePicture {
        void readyToTakePicture(boolean ready);
    }

    public void setReadyListener(ReadyToTakePicture listener) {
        this.readyListener = listener;
    }
}
