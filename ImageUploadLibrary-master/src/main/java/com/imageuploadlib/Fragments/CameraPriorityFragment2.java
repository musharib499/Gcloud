package com.imageuploadlib.Fragments;

/**
 * Created by Lakshay on 02-09-2015.
 */

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Rect;
import android.graphics.RectF;
import android.hardware.Camera;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.imageuploadlib.Activity.CameraPriorityActivity;
import com.imageuploadlib.Activity.GalleryActivity;
import com.imageuploadlib.Activity.ReviewImageActivity;
import com.imageuploadlib.R;
import com.imageuploadlib.Utils.CameraPreview;
import com.imageuploadlib.Utils.CommonUtils;
import com.imageuploadlib.Utils.Constants;
import com.imageuploadlib.Utils.DrawingView;
import com.imageuploadlib.Utils.FileInfo;
import com.imageuploadlib.Utils.PhotoParams;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Lakshay on 31-08-2015.
 */
public class CameraPriorityFragment2 extends Fragment implements View.OnClickListener, View.OnTouchListener, Camera.PictureCallback {

    private static final String TAG = "CameraPriorityFragment";
    private static final int REQUEST_REVIEW = 100;

    public static int maxNumberOfImages;
    public static final int GALLERY_PICK = 99;

    private int FOCUS_AREA_SIZE = 200;
    private Activity mActivity;
    private PhotoParams mPhotoParams;
    private DrawingView drawingView;
    private ImageView buttonCapture;
    private ImageView buttonGallery;
    public  ImageView buttonDone;
    public TextView tvTapToFocus;
    private TextView tvImageName;
    private ArrayList<FileInfo> imagesList = new ArrayList<>();
    private String capturedFilePath = "";
    private Camera mCamera;
    private CameraPreview mCameraPreview;
    private boolean readyToTakePicture = false;
    private FrameLayout mCameraLayout;
    private View fragmentView;
    private PictureTakenListener mPictureTakenListener;
    private boolean permissionAlreadyRequested = false;

    public interface PictureTakenListener {
        public void onPictureTaken(String filePath);
        public void onGalleryPicsCollected(ArrayList<FileInfo> infos);
        public void onPicturesCompleted();
    }


    public static CameraPriorityFragment2 getInstance(PhotoParams photoParams, PictureTakenListener pictureTakenListener) {
        CameraPriorityFragment2 fragment = new CameraPriorityFragment2();
        Bundle extras = new Bundle();
        extras.putSerializable(Constants.PHOTO_PARAMS, photoParams);
        fragment.mPictureTakenListener = pictureTakenListener;
        fragment.setArguments(extras);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        fragmentView = LayoutInflater.from(mActivity).inflate(R.layout.camera_priority_fragment_2, null);

        mPhotoParams = (PhotoParams) getArguments().getSerializable(Constants.PHOTO_PARAMS);
        maxNumberOfImages = mPhotoParams.getNoOfPhotos();
        PhotoParams.CameraOrientation orientation = mPhotoParams.getOrientation();

        //View to add rectangle on tap to focus
        drawingView = new DrawingView(mActivity);

        setOrientation(mActivity, orientation);

        buttonCapture = (ImageView) fragmentView.findViewById(R.id.buttonCapture);
        buttonGallery = (ImageView) fragmentView.findViewById(R.id.buttonGallery);
        buttonDone = (ImageView) fragmentView.findViewById(R.id.buttonDone);
        tvImageName = (TextView) fragmentView.findViewById(R.id.imageName);
        tvTapToFocus=(TextView) fragmentView.findViewById(R.id.tapToFocus);



        return fragmentView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ViewGroup.LayoutParams layoutParamsDrawing
                = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
                ViewGroup.LayoutParams.FILL_PARENT);

        mActivity.addContentView(drawingView, layoutParamsDrawing);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {

            if (requestCode == GALLERY_PICK) {
                imagesList = (ArrayList<FileInfo>) data.getSerializableExtra(GalleryActivity.GALLERY_SELECTED_PHOTOS);
                if (imagesList != null && imagesList.size()>0) {
                    buttonCapture.setTag("done");
                    onClick(buttonCapture);
                    mPictureTakenListener.onGalleryPicsCollected(imagesList);
                    imagesList.clear();
                }
            } else if (requestCode == REQUEST_REVIEW) {
                mPictureTakenListener.onPictureTaken(capturedFilePath);
            }
        } else {
            if (requestCode == REQUEST_REVIEW) {
                readyToTakePicture = true;
                buttonCapture.setEnabled(true);
            } else if (requestCode == GALLERY_PICK) {
                return;
            } else if (requestCode != 101) {
                mActivity.setResult(resultCode);
                mActivity.finish();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mCamera == null) {
            try {
                if (!permissionAlreadyRequested && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                        && !CommonUtils.checkForPermission(mActivity,
                        new String[]{Manifest.permission.CAMERA,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        Constants.REQUEST_PERMISSION_CAMERA, "Camera and Storage")) {
                    permissionAlreadyRequested = true;
                    return;
                }
                mCamera = Camera.open();

                //To set hardware camera rotation
                setCameraRotation();

                mCameraPreview = new CameraPreview(mActivity, mCamera);
                mCameraPreview.setReadyListener(new CameraPreview.ReadyToTakePicture() {
                    @Override
                    public void readyToTakePicture(boolean ready) {
                        readyToTakePicture = ready;
                    }
                });

                mCameraPreview.setOnTouchListener(this);

                mCameraLayout = (FrameLayout) fragmentView.findViewById(R.id.camera_preview);
                mCameraLayout.addView(mCameraPreview);

                //set the screen layout to fullscreen
                mActivity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                        WindowManager.LayoutParams.FLAG_FULLSCREEN);

                buttonCapture.setOnClickListener(this);
                buttonGallery.setOnClickListener(this);

            } catch (Exception e) {
                Log.e("Camera Open Exception", "" + e.getMessage());
            }

            buttonDone.setOnClickListener(this);
//            if (maxNumberOfImages == 0) {
//                buttonDone.setVisibility(View.VISIBLE);
//                buttonDone.setOnClickListener(CameraPriorityFragment2.this);
//            }
            //To make sure that name appears only after animation ends
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    if (mPhotoParams.getImageName() != null && !"".equals(mPhotoParams.getImageName())) {
                        tvImageName.setVisibility(View.VISIBLE);
                        tvImageName.setText(mPhotoParams.getImageName() + "");
                        tvImageName.setOnClickListener(CameraPriorityFragment2.this);
                    }
                }
            }, 1000);

        } else {
            Log.e(TAG, "camera not null");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        try {
            mCamera.setPreviewCallback(null);
            mCameraPreview.getHolder().removeCallback(mCameraPreview);
            mCamera.stopPreview();
            mCameraLayout.removeAllViews();
            mCamera.release();
            mCamera = null;
            mCameraPreview = null;
        } catch (Exception e) {
//            Log.e(TAG, e.getMessage());
        }

    }

    @Override
    public void onClick(View v) {

        //Toast.makeText(this, (v.getId()==R.id.bDone ? "done": "capture"), Toast.LENGTH_SHORT).show();
        if (v.getId() == R.id.buttonCapture) {
            if (readyToTakePicture) {
                mCamera.takePicture(null, null, this);
                readyToTakePicture = false;
                //llActionsCamera.setEnabled(false);
                buttonCapture.setEnabled(true);
            }
        } else if (v.getId() == R.id.buttonGallery) {
            //finish();
            Intent intent = new Intent(mActivity, GalleryActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra(GalleryActivity.MAX_COUNT, maxNumberOfImages);
            intent.putExtra(CameraPriorityActivity.FROM_PRIORITY_ACTIVITY, true);
            startActivityForResult(intent, GALLERY_PICK);

        } else if (v.getId() == R.id.buttonDone) {
            mPictureTakenListener.onPicturesCompleted();
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (mCamera != null) {
//            Camera camera = mCamera.getCamera();
            mCamera.cancelAutoFocus();
            final Rect focusRect = calculateTapArea(event.getX(), event.getY(), 1f);

            Camera.Parameters parameters = mCamera.getParameters();
            if (parameters.getFocusMode() != Camera.Parameters.FOCUS_MODE_AUTO) {
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
            }
            if (parameters.getMaxNumFocusAreas() > 0) {
                List<Camera.Area> mylist = new ArrayList<Camera.Area>();
                mylist.add(new Camera.Area(focusRect, 1000));
                parameters.setFocusAreas(mylist);
            }

            try {
                mCamera.cancelAutoFocus();
                mCamera.setParameters(parameters);
                mCamera.startPreview();
                mCamera.autoFocus(new Camera.AutoFocusCallback() {
                    @Override
                    public void onAutoFocus(boolean success, Camera camera) {
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

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        new ImagePostProcessing(mActivity, data).execute();
    }

    private class ImagePostProcessing extends AsyncTask<Void, Void, File> {

        private Context context;
        private byte[] data;
        private ProgressDialog progressDialog;

        public ImagePostProcessing(Context context, byte[] data) {
            this.context = context;
            this.data = data;
        }

        @Override
        protected File doInBackground(Void... params) {
            File pictureFile = Constants.getMediaOutputFile(Constants.TYPE_IMAGE);
            Log.e(TAG, pictureFile.getAbsolutePath());
            if (pictureFile == null) {
                Log.d(TAG, "Error creating media file, check storage permissions: ");
                return null;
            }

            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(data);
                fos.close();

                Uri pictureFileUri = Uri.parse("file://" + pictureFile.getAbsolutePath());
                mActivity.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                        pictureFileUri));

            } catch (FileNotFoundException e) {
                Log.d(TAG, "File not found: " + e.getMessage());
            } catch (IOException e) {
                Log.d(TAG, "Error accessing file: " + e.getMessage());
            }

            mCamera.startPreview();
            return pictureFile;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(context, null, "Saving Picture", true);
        }

        @Override
        protected void onPostExecute(File file) {
            super.onPostExecute(file);
            if (progressDialog != null)
                progressDialog.dismiss();
            if (file != null) {
                capturedFilePath = file.getPath();
                Intent intent = new Intent(mActivity, ReviewImageActivity.class);
                intent.putExtra(Constants.IMAGE_NAME, mPhotoParams.getImageName());
                intent.putExtra(Constants.IMAGE_PATH, file.getPath());
                startActivityForResult(intent, REQUEST_REVIEW);
//                updateCapturedPhotos(file);
                mCamera.startPreview();
            } else {
                Toast.makeText(context, "Camera Error. Kindly try again", Toast.LENGTH_SHORT).show();
                readyToTakePicture = true;
                buttonCapture.setEnabled(true);
                mCamera.startPreview();
            }
        }
    }



    private void setCameraRotation() {
        //STEP #1: Get rotation degrees
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(Camera.CameraInfo.CAMERA_FACING_BACK, info);
        int rotation = mActivity.getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break; //Natural orientation
            case Surface.ROTATION_90:
                degrees = 90;
                break; //Landscape left
            case Surface.ROTATION_180:
                degrees = 180;
                break;//Upside down
            case Surface.ROTATION_270:
                degrees = 270;
                break;//Landscape right
        }
        int rotate = (info.orientation - degrees + 360) % 360;

        //STEP #2: Set the 'rotation' parameter
        Camera.Parameters params = mCamera.getParameters();
        params.setRotation(rotate);
        mCamera.setParameters(params);
    }



    private void setOrientation(Activity activity, PhotoParams.CameraOrientation orientation) {
        if (orientation != null) {
            if (orientation.equals(PhotoParams.CameraOrientation.LANDSCAPE)) {
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            } else if (orientation.equals(PhotoParams.CameraOrientation.PORTRAIT)) {
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
        } else {
            Log.e(Constants.TAG, "No orientation set");
        }
    }


    private Rect calculateTapArea(float x, float y, float coefficient) {
        int areaSize = Float.valueOf(FOCUS_AREA_SIZE * coefficient).intValue();

        int left = clamp((int) x - areaSize / 2, 0, mCameraPreview.getWidth() - areaSize);
        int top = clamp((int) y - areaSize / 2, 0, mCameraPreview.getHeight() - areaSize);

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
}

