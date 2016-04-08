package com.imageuploadlib.Utils;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.util.List;

/**
 * Created by Lakshay on 22-06-2015.
 */
public class ReCameraPreview extends SurfaceView implements SurfaceHolder.Callback, Camera.PreviewCallback {

    private static final String TAG = "ReCameraPreview";
    Camera mCamera;
    SurfaceHolder mSurfaceHolder;
    ReadyToTakePicture readyListener;

    public ReCameraPreview(Context context, Camera camera) {
        super(context);
        mCamera = camera;
        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            Log.e(TAG, "Surface Created");
            mCamera.setPreviewDisplay(mSurfaceHolder);
            mCamera.setPreviewCallback(this);
            mCamera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.e(TAG, "Surface Changed");

        if (mSurfaceHolder.getSurface() == null)
            return;

        try {
            mCamera.stopPreview();
        } catch (Exception e) {
            Log.e(TAG, "Exception : " + e.getMessage());
        }
        try {
            Camera.Parameters parameters = mCamera.getParameters();
            parameters.setPictureSize(720, 1280);
            mCamera.setPreviewDisplay(mSurfaceHolder);

        } catch (IOException e) {
            e.printStackTrace();
        }
        mCamera.startPreview();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.e(TAG, "Surface Destroyed ");
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
        }
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {

    }

    public interface ReadyToTakePicture {
        void readyToTakePicture(boolean ready);
    }

    public void setReadyListener(ReadyToTakePicture listener) {
        this.readyListener = listener;
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
}
