package com.gcloud.gaadi.ui;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.gcloud.gaadi.ApplicationController;
import com.gcloud.gaadi.R;
import com.gcloud.gaadi.adapter.UploadPhotosGridViewAdapter;
import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.model.PhotoInfo;
import com.gcloud.gaadi.utils.CommonUtils;
import com.gcloud.gaadi.utils.GAHelper;
import com.gcloud.gaadi.utils.GCLog;
import com.gcloud.gaadi.utils.ImageUploadUtils;
import com.gcloud.gaadi.utils.Utils;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*
 * @author gaurav.n
 */
public class PhotoUploadActivity extends Activity implements OnClickListener {

    public static final String KEY_ARRAYLIST_IMAGE = "KEY_ARRAYLIST_IMAGE";
    private static final int REQUEST_CODE_CAMERA = 2001;
    private static final int REQUEST_CODE_GALLERY = 2002;
    private static final String KEY_IMAGE_FILE_PATH = "IMAGE_FILE_PATH";
    public String propId;
    public ArrayList<String> uploadedImages;
    private UploadPhotosGridViewAdapter adapter;
    private String imageFilePath;
    private ArrayList<String> uploadedImagesPid;
    private File imagepath;

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED, null);
        finish();
        overridePendingTransition(0, R.anim.mapview_slide_down);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_layer_layout);
        int list[] = new int[]{R.id.addPhotoCamera, R.id.addPhotoGallary, R.id.closeLayer, R.id.done};
        Utils.setOnClickListener(findViewById(R.id.rootContainer), list, this);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (extras.containsKey(Constants.UPLOADED_IMAGES)) {
                //GCLog.e(getClass().getSimpleName(), "Photos present, now load them using aquery");
                uploadedImages = extras.getStringArrayList(Constants.UPLOADED_IMAGES);
//                uploadedImages = ApplicationController.imagesList;
                uploadedImagesPid = extras.getStringArrayList(Constants.UPLOADED_IMAGES);
                propId = extras.getString(Constants.STOCK_ID);
                init();

                if (uploadedImages != null) {
                    for (int i = 0; i < uploadedImages.size(); ++i) {
                        GCLog.e("Photo pid: " + uploadedImagesPid.get(i));
                        onPhotoReceive(uploadedImages.get(i), uploadedImagesPid.get(i), propId);
                    }
                }
            } else {
                init();
            }

            ArrayList<PhotoInfo> imagePathList = (ArrayList<PhotoInfo>) extras.getSerializable(PhotoUploadActivity.KEY_ARRAYLIST_IMAGE);
            if (imagePathList != null) {
                adapter.addAll(imagePathList);
            }
        } else {
            init();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        ApplicationController.getInstance().getGAHelper().sendScreenName(GAHelper.TrackerName.APP_TRACKER, Constants.CATEGORY_PHOTO_UPLOAD);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(KEY_ARRAYLIST_IMAGE, adapter.getList());
        outState.putString(KEY_IMAGE_FILE_PATH, imageFilePath);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            imageFilePath = savedInstanceState.getString(KEY_IMAGE_FILE_PATH);
            @SuppressWarnings("unchecked")
            ArrayList<PhotoInfo> imagePathList = (ArrayList<PhotoInfo>) savedInstanceState.getSerializable(PhotoUploadActivity.KEY_ARRAYLIST_IMAGE);
            if (imagePathList != null) {
                adapter.addAll(imagePathList);
            }
        }
    }

    @Override
    public void finish() {
        super.finish();
        //overridePendingTransition(0, R.anim.mapview_slide_down);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addPhotoCamera:
                imagepath = ImageUploadUtils.getImagepath();
                imageFilePath = imagepath.getPath();
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                Uri imageUri = null;

                try {
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Images.Media.TITLE, getString(R.string.app_name));
                    values.put(MediaStore.Images.Media.DATA, imageFilePath);
                    imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (imageUri != null) {
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                } else {
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageFilePath);
                }

                startActivityForResult(cameraIntent, REQUEST_CODE_CAMERA);
                return;

            case R.id.addPhotoGallary:
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            /*i.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            i.setAction(Intent.ACTION_GET_CONTENT);*/
                i.setType("image/*");
                startActivityForResult(i, REQUEST_CODE_GALLERY);
                return;

            case R.id.closeLayer:
                onBackPressed();
                return;

            case R.id.done:
                onDoneClick(v);

                return;
        }
    }

    private void onDoneClick(View v) {
        Intent intent = new Intent();
        GCLog.e("images in adapter: " + adapter.getCount());
        ArrayList<PhotoInfo> adapterList = adapter.getList();
        ArrayList<PhotoInfo> uploadList = (ArrayList<PhotoInfo>) onlyNewFiles(adapterList);
        int size = uploadList.size();
        GCLog.e("" + size);
        intent.putExtra(KEY_ARRAYLIST_IMAGE, adapterList);
//        intent.putExtra(Constants.STOCK_IMAGES,)
        setResult(RESULT_OK, intent);
        finish();
        overridePendingTransition(0, R.anim.mapview_slide_down);
    }

    public List<PhotoInfo> onlyNewFiles(List<PhotoInfo> list1) {

        if (uploadedImages == null)
            return list1;

        List<PhotoInfo> list = new ArrayList<PhotoInfo>();

        for (PhotoInfo t : list1) {
            if (!uploadedImages.contains(t.getPath())) {
                list.add(t);
            }
        }
        return list;
    }

    private void init() {

        GridView gridGallery = (GridView) findViewById(R.id.gridGallery);
        gridGallery.setFastScrollEnabled(true);
        adapter = new UploadPhotosGridViewAdapter(this);

        gridGallery.setAdapter(adapter);

        adapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                if (adapter != null) {
                    ((TextView) findViewById(R.id.photosCount)).setText(String.valueOf(adapter.getCount()));
                }
                super.onChanged();
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            if (imagepath != null) {
                try {
                    imagepath.delete();

                } catch (Exception e) {
                    GCLog.e("Image could not be deleted. Error: " + e.getMessage());

                }
            }
            return;
        }

        switch (requestCode) {
            case REQUEST_CODE_CAMERA:
                if (data != null && data.getData() != null) {
                    onPhotoReceive(data.getData());
                } else {
                    onPhotoReceive(imageFilePath);
                }

                return;
            case REQUEST_CODE_GALLERY:
                if (data != null && data.getData() != null) {
                    if (data.getDataString().contains("https")) {
                        int startIndex = data.getDataString().indexOf("https");
                        int endIndex = data.getDataString().length();
                        String url = "";
                        try {
                            url = URLDecoder.decode(data.getDataString().substring(startIndex, endIndex), "UTF-8");
                            onPhotoReceive(url, "", "");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        GCLog.e("Photo url path: " + url);

                    } else if (data.getDataString().contains("http")) {
                        int startIndex = data.getDataString().indexOf("http");
                        int endIndex = data.getDataString().length();
                        String url = "";
                        try {
                            url = URLDecoder.decode(data.getDataString().substring(startIndex, endIndex), "UTF-8");
                            onPhotoReceive(url, "", "");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        GCLog.e("Photo url path: " + url);

                    } else {
                        GCLog.e("Photo data : " + data.getData());
                        onPhotoReceive(data.getData());
                    }
                }
                return;
        }
    }

    private void onPhotoReceive(String photoPath, String pid, String propId) {
        if (adapter.getCount() >= Constants.MAX_IMAGE_LIMIT) {
            CommonUtils.showToast(getApplicationContext(), "Maximum Image Upload Limit is " + Constants.MAX_IMAGE_LIMIT, Toast.LENGTH_SHORT);
            return;
        }

        PhotoInfo photoInfo = new PhotoInfo(photoPath, Utils.getExifOrientation(photoPath));
        photoInfo.setPid(pid);
        photoInfo.setPropId(propId);

        if (!adapter.contains(photoInfo)) {
            File f = new File(photoPath);

            if (ImageUploadUtils.exceedFileMaxSize(f.length())) {
                photoPath = ImageUploadUtils.SaveCompressedFile(photoPath);

            }

            GCLog.e("photo size: " + (f.length() / (1024 * 1024)));

            if (!ImageUploadUtils.CheckValidImage(photoPath)) {
                Toast.makeText(getApplicationContext(), "Invalid Image Format", Toast.LENGTH_SHORT).show();
                return;
            }

            adapter.add(photoInfo);
            ((GridView) findViewById(R.id.gridGallery)).setAdapter(adapter);
        } else {
            Toast.makeText(getApplicationContext(), "Image Already Exists", Toast.LENGTH_SHORT).show();
        }
    }


    private void onPhotoReceive(String photoPath) {
        if (adapter.getCount() >= Constants.MAX_IMAGE_LIMIT) {
            Toast.makeText(getApplicationContext(), "Maximum Image Upload Limit is " + Constants.MAX_IMAGE_LIMIT, Toast.LENGTH_SHORT).show();
            return;
        }

        PhotoInfo photoInfo = new PhotoInfo(photoPath, Utils.getExifOrientation(photoPath));
        // GCLog.v("RESULT_Image_from_gal", photoInfo.toString());
        if (!adapter.contains(photoInfo)) {
            File f = new File(photoPath);
            GCLog.e("Photo path: " + photoPath.toString());
            if (ImageUploadUtils.exceedFileMaxSize(f.length())) {
                //	 GCLog.v(getClass().getSimpleName(), "compressing gal file");
                photoPath = ImageUploadUtils.SaveCompressedFile(photoPath);
                photoInfo.setPath(photoPath);
            }
            if (!ImageUploadUtils.CheckValidImage(photoPath)) {
                Toast.makeText(getApplicationContext(), "Invalid Image Format", Toast.LENGTH_SHORT).show();
                return;
            }
            //  GCLog.v("Camera saved Image path", picturePath);

            adapter.add(photoInfo);
            ((GridView) findViewById(R.id.gridGallery)).setAdapter(adapter);
        } else {
            Toast.makeText(getApplicationContext(), "Image Already Exists", Toast.LENGTH_SHORT).show();
        }
    }

    private void onPhotoReceive(Uri uri) {

        try {
            String[] filePathColumn = {MediaColumns.DATA};
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            cursor.moveToFirst();
            GCLog.e(Arrays.toString(cursor.getColumnNames()));
            int index = cursor.getColumnIndex(MediaColumns.DISPLAY_NAME);

            File file = new File(cursor.getString(index));
            GCLog.e("Photo path: " + cursor.getString(index));
            //TODO upload photos from fb/google
      /* reference: [UploadPhotosActivity.onPhotoReceive()-296]: [_display_name, _size, mime_type]
      04-17 16:52:05.729 5675-5675/com.gaadi.android.dealer E/CursorWindow﹕ Failed to read row 0, column -1 from a CursorWindow which has 1 rows, 3 columns.*/
            int columnDataIndex = cursor.getColumnIndex(filePathColumn[0]);
            if (columnDataIndex == -1) {
                columnDataIndex = cursor.getColumnIndex(MediaColumns.DISPLAY_NAME);
            }
            String photoPath = cursor.getString(columnDataIndex);
            cursor.close();
            onPhotoReceive(photoPath);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


  /*@Override
  public void onPhotoDeleted(Response<EditListingModel> response, NNAcresProgressDialog progressDialog, int adapterItemPosition) {

    if (progressDialog != null) {
      progressDialog.dismiss();
    }

    if (response.isSuccess()) {

      String code = response.getResult().getCode();
      String message = response.getResult().getMessage();

      if (code.equals("0")) {
        AndroidUtils.showLogoutDialog(
            this,
            Constants.SERVER_LOGOUT_TITLE,
            Constants.SERVER_LOGOUT_MESSAGE,
            Constants.SERVER_LOGOUT_BUTTON_TEXT);
        return;

      } else if (code.equals("1")) {
        AndroidUtils.showToast(this, message, Toast.LENGTH_LONG);
        return;

      } else if (code.equals("2")) {
        AndroidUtils.showLogoutDialog(
            this,
            Constants.SERVER_ACCOUNT_SUSPENDED_TITLE,
            Constants.SERVER_ACCOUNT_DEACTIVATED,
            Constants.SERVER_DEACTIVATED_CALL_SUPPORT,
            Constants.SERVER_DEACTIVATED_EMAIL_SUPPORT);
        return;

      } else if (code.equals("3")) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(Constants.SERVER_LOGOUT_TITLE);
        dialog.setMessage(Constants.SERVER_NO_LISTING_MESSAGE);
        dialog.setCancelable(false);

        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {

          @Override
          public void onClick(DialogInterface dialog, int which) {


            Intent intent = new Intent(PhotoUploadActivity.this, ResidentialSearchFormsMainActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            dialog.dismiss();

            startActivity(intent);

            PhotoUploadActivity.this.setResult(RESULT_CANCELED);


            PhotoUploadActivity.this.finish();
            Utils.animateActivity(PhotoUploadActivity.this, "back");
          }
        });

        dialog.show();
        return;


      } else if (code.equals("4")) {
        AndroidUtils.showLogoutDialog(
            PhotoUploadActivity.this,
            Constants.SERVER_LOGOUT_TITLE,
            Constants.SERVER_INVALID_USER_MESSAGE,
            Constants.SERVER_INVALID_USER_BUTTON_TEXT);
        return;

      } else if (code.equals("5")) {
        adapter.remove(adapterItemPosition);
        AndroidUtils.showToast(this, "Duplicate Listing", Toast.LENGTH_SHORT);

      } else if (code.equals("6")) {
        AndroidUtils.showErrorToast(this, "Photo not deleted. Try Again.", Toast.LENGTH_SHORT);

      } else if (code.equals("7")) {
        adapter.remove(adapterItemPosition);
        AndroidUtils.showMessageToast(this, "Photo deleted", Toast.LENGTH_SHORT);

      } else if (code.equals("8")) {
        final Dialog suspiciousDialog = new Dialog(PhotoUploadActivity.this);
        suspiciousDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        suspiciousDialog.setContentView(R.layout.suspicious_property);
        suspiciousDialog.setCanceledOnTouchOutside(false);

        TextView ok = (TextView) suspiciousDialog.findViewById(R.id.okTextView);
        ok.setOnClickListener(new OnClickListener() {

          @Override
          public void onClick(View v) {
            suspiciousDialog.dismiss();
            Intent intent = new Intent(PhotoUploadActivity.this, ManageListings.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
            Utils.animateActivity(PhotoUploadActivity.this, "back");

          }
        });

        if (!PhotoUploadActivity.this.isFinishing() && !suspiciousDialog.isShowing()) {
          suspiciousDialog.show();
        }

        return;

      }

      GCLog.e(getClass().getSimpleName(), "Photo delted response: " + response.getResult().toString());

    } else {
      AndroidUtils.handleError(response.getThrowable(), this);
    }

  }*/

    @Override
    protected void onResume() {
        super.onResume();
        //comScore.onEnterForeground();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //comScore.onExitForeground();
    }
}
