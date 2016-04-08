package com.imageuploadlib.Activity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.imageuploadlib.Adapters.ImagesFoldersAdapter;
import com.imageuploadlib.R;
import com.imageuploadlib.Utils.ApplicationController;
import com.imageuploadlib.Utils.CommonUtils;
import com.imageuploadlib.Utils.Constants;
import com.imageuploadlib.Utils.FileInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

//import com.soundcloud.android.crop.Crop;


public class GalleryActivity extends BaseActivityGallery implements AdapterView.OnItemClickListener {

    public static final String MAX_COUNT = "maxCount";
    public static final String FILES_IN_FOLDER = "folder_files";
    public static final String FOLDER_NAME = "folder_name";
    public static final String GALLERY_SELECTED_PHOTOS = "galleryPhotos";
    public static final String ALREADY_SELECTED_FILES = "alreadySelectedFiles";
    private static final String TAG = "GalleryActivity";
    private static final int REQUEST_FOLDER_FILES = 100;
    private static final String GALLERY_DELETED_PHOTOS = "deletedPhotos";
    private ArrayList<FileInfo> folders = new ArrayList<FileInfo>();
    private ImagesFoldersAdapter adapter;
    private ArrayList<FileInfo> selectedFiles = new ArrayList<>();
    private File destinationFolder;
    private int croppedImagesCount = 0;
    private ArrayList<String> croppedImages = new ArrayList<>();
    private Boolean fromPriorityActivity = false;

    private int maxCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        getLayoutInflater().inflate(R.layout.activity_gallery, frameLayout);

        getSupportActionBar().setTitle("Gallery");
        toolbar.setTitle("Gallery");
        //    actionBar.setDisplayHomeAsUpEnabled(true);

        GridView gvFolders = (GridView) findViewById(R.id.gvFolders);
//        alreadySelectedFiles = (ArrayList<FileInfo>) getIntent().getSerializableExtra(CameraItemsFragment.CAMERA_ITEMS_SELECTED_FILES);
        if (getIntent().getExtras() != null && getIntent().getExtras().containsKey(MAX_COUNT))
            maxCount = getIntent().getExtras().getInt(MAX_COUNT);
        if (getIntent().getExtras() != null && getIntent().getExtras().containsKey(CameraPriorityActivity.FROM_PRIORITY_ACTIVITY))
            fromPriorityActivity = getIntent().getExtras().getBoolean(CameraPriorityActivity.FROM_PRIORITY_ACTIVITY);
        adapter = new ImagesFoldersAdapter(this, folders);
        gvFolders.setAdapter(adapter);

//        ArrayList<FileInfo> foldersAvailable = getFolders();

        Uri uri = CommonUtils.getImageStoreUri();
        String[] PROJECTION_BUCKET = {
                "" + MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
                MediaStore.Images.ImageColumns.DATA
        };

        Cursor mCursor = getContentResolver().query(uri, PROJECTION_BUCKET, "\"1) GROUP BY 1,(1\"", null, null);
//        Cursor mCursor = getContentResolver().query(uri, PROJECTION_BUCKET, "\"1) GROUP BY "+ MediaStore.Images.Media.BUCKET_DISPLAY_NAME, null, null);
        if (mCursor == null) {
            Toast.makeText(GalleryActivity.this, "Gallery cannot be opened.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        mCursor.moveToFirst();


        HashMap<String, Integer> mapFolders = new HashMap<>();
        for (int i = 0; i < mCursor.getCount(); i++) {
            String bucketName = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME));

            Log.e(Constants.TAG, bucketName);
            Integer count = mapFolders.get(bucketName);
            if (count != null) {
                int countValue = count.intValue();
                Integer newCount = new Integer(countValue + 1);
                mapFolders.put(bucketName, newCount);
            } else {
                mapFolders.put(bucketName, new Integer(1));
            }
            mCursor.moveToNext();
        }
        mCursor.close();

        String selection = MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME + "= ?";
        String orderBy = MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC LIMIT 1";
        for (HashMap.Entry<String, Integer> entry : mapFolders.entrySet()) {
            Log.e(Constants.TAG, "Folder : " + entry.getKey() + " Count : " + entry.getValue());
            FileInfo fileInfo = new FileInfo();
            fileInfo.setDisplayName(entry.getKey());
            fileInfo.setFileCount(entry.getValue());

            Cursor cursorImage = getContentResolver().query(uri, PROJECTION_BUCKET, selection, new String[]{fileInfo.getDisplayName()}, orderBy);
            cursorImage.moveToFirst();
            for (int j = 0; j < cursorImage.getCount(); j++) {
                String imagePath = cursorImage.getString(cursorImage.getColumnIndex(MediaStore.Images.ImageColumns.DATA));
                Log.e(Constants.TAG, "Image Path : " + imagePath);
                fileInfo.setFilePath(imagePath);
                cursorImage.moveToNext();
            }
            folders.add(fileInfo);
            cursorImage.close();
        }

        adapter.notifyDataSetChanged();
        gvFolders.setOnItemClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Log.e(TAG, "On Activity Rresult");
        Intent intent = new Intent();
        if (resultCode == FolderFiles.RESULT_SKIP_FOLDERS) {
            //Log.e(TAG, "Skip Folders");
//            if (maxCount == 0) {
//            ArrayList<FileInfo> fileInfos = (ArrayList<FileInfo>) data.getExtras().getSerializable(FolderFiles.SELECTED_FILES);
//            Log.e("Gaurav", "FileInfos Size " + fileInfos.size());
            intent.putExtra(GALLERY_SELECTED_PHOTOS, data.getExtras().getSerializable(FolderFiles.SELECTED_FILES));
//            intent.putExtra(GALLERY_DELETED_PHOTOS, data.getExtras().getSerializable(FolderFiles.DELETED_PHOTOS);
            setResult(RESULT_OK, intent);
            finish();
            /*} else {
                selectedFiles = (ArrayList<FileInfo>) data.getExtras().getSerializable(FolderFiles.SELECTED_FILES);
                Log.e("GalleryActivity", "size: "+selectedFiles.size());
                if (selectedFiles.size() > 0) {
                    destinationFolder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Gaadi Gcloud/temp");
                    if (!destinationFolder.exists())
                        if (!destinationFolder.mkdirs()) {
                            Log.e("GalleryActivity", "");
                            setResult(RESULT_CANCELED, null);
                            finish();
                        }
                    for(FileInfo info : selectedFiles){
                        croppedImages.add(info.getFilePath());
                    }
                }
            }*/
        } else if (resultCode == RESULT_OK && requestCode == 1001) {//Crop.REQUEST_CROP) {
            if (croppedImagesCount == (selectedFiles.size() - 1)) {
                intent.putStringArrayListExtra("cropped", croppedImages);
                setResult(RESULT_OK, intent);
                finish();
            } else
                for (FileInfo info : selectedFiles) {
                    croppedImages.add(info.getFilePath());
                }
//                croppedImages.add(cropImage(selectedFiles.get(++croppedImagesCount)));
        }
    }

    private String cropImage(FileInfo info) {
        File destination = new File(destinationFolder.getAbsolutePath() + File.separator +
                info.getFilePath().substring(info.getFilePath().lastIndexOf("/") + 1));
        /*Crop.of(Uri.fromFile(new File(info.getFilePath())),
                Uri.fromFile(destination))
                .withAspect(3, 4)
//                .withMaxSize(300, 400)
                .start(this);*/
        return destination.getAbsolutePath();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_gallery, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return false;
        }
        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //Log.e(TAG ,"On Item Click " +  position + "");
        FileInfo fileInfo = folders.get(position);
//        File file = new File(fileInfo.getFileName());
        //Log.e(TAG ,"File path to open" + fileInfo.getFileName());

        Intent intent = new Intent(this, FolderFiles.class);
//        Intent intent = new Intent(this, GalleryContentProviderActivity.class);
        intent.putExtra(FOLDER_NAME, fileInfo.getDisplayName());
        if (maxCount > 0)
            intent.putExtra(MAX_COUNT, maxCount);
        if (fromPriorityActivity)
            intent.putExtra(CameraPriorityActivity.FROM_PRIORITY_ACTIVITY, true);
        //intent.putExtra(FILES_IN_FOLDER, filesInFolder);
//        intent.putExtra(ALREADY_SELECTED_FILES , alreadySelectedFiles);
        startActivityForResult(intent, REQUEST_FOLDER_FILES);
    }

    private ArrayList<FileInfo> getFolders() {

        //Directory Pictures
        File pathPictures = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        //Log.e(Constants.TAG, "External Path :" + pathPictures.toString());
        ArrayList<FileInfo> files1 = getAllFoldersInfo(pathPictures);

        //Directory DCIM
        File pathDCIM = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        ArrayList<FileInfo> files2 = getAllFoldersInfo(pathDCIM);
        files1.addAll(files2);

        //SD-Card Mounted
        String secStore = System.getenv("SECONDARY_STORAGE");
        //Log.e(Constants.TAG, "Sec Store : "+secStore);
        try {
            if (secStore != null) {
                int index = secStore.indexOf(":");
                String externalStorage = "";
                if (index > 0) {
                    externalStorage = secStore.substring(0, index);

                } else {
                    externalStorage = secStore;
                }
                String externalStorageDCIM = externalStorage + "/DCIM";
                File externalFile = new File(externalStorageDCIM);

                ArrayList<FileInfo> externalFiles = getAllFoldersInfo(externalFile);
                files1.addAll(externalFiles);

            }

        } catch (Exception e) {
            //Log.e(Constants.TAG, e.getMessage());
        }

        //WhatsApp Images
        File pathWhatsApp = new File(Environment.getExternalStorageDirectory() + "/WhatsApp/Media/WhatsApp Images");
        Log.e(Constants.TAG, pathWhatsApp.getAbsolutePath());
        ArrayList<FileInfo> files3 = getAllFoldersInfo(pathWhatsApp);
        files1.addAll(files3);


        //Download Directory
        File pathDownload = new File(Environment.getExternalStorageDirectory() + "/Download");
        Log.e(Constants.TAG, "pathDownload : " + pathDownload.getName());

        ArrayList<FileInfo> files4 = getAllFoldersInfo(pathDownload);
        files1.addAll(files4);

        return files1;
    }

    private String verifyFolder(File file) {
        File[] filesInFolder = file.listFiles();
        if (filesInFolder != null && filesInFolder.length > 0) {
            for (File file1 : filesInFolder) {
                if (file1.getName().contains("jpg") || file1.getName().contains("jpeg") || file1.getName().contains("png")) {
                    return file1.getAbsolutePath();
                }
            }
        }
        return "";
    }

    private ArrayList<FileInfo> getAllFoldersInfo(File file) {

        ArrayList<FileInfo> allFiles = new ArrayList<FileInfo>();
        File[] contentPictures = file.listFiles();

        if ((contentPictures == null) || (contentPictures.length == 0)) {
            Log.e(Constants.TAG, "No Files found at the path mentioned");
        } else {
            Boolean makeSelfFolder = false;
            for (File folder : contentPictures) {
                if (!folder.isDirectory()) {
                    makeSelfFolder = true;
                }
                String valid = verifyFolder(folder);
                if (folder.getName().equals("Sent"))
                    continue;
                if (valid.length() > 0) {
                    FileInfo fileInfo = new FileInfo();
                    fileInfo.setDisplayName(folder.getName());
                    fileInfo.setFileName(folder.getAbsolutePath());
                    File[] imagesInFolder = folder.listFiles();
                    if (imagesInFolder != null) {
                        if (imagesInFolder.length == 0) {
                            fileInfo.setFilePath(folder.getAbsolutePath());
                        } else {
                            fileInfo.setType(FileInfo.FILE_TYPE.FOLDER);
                            fileInfo.setFileCount(imagesInFolder.length);
                            fileInfo.setFilePath(imagesInFolder[imagesInFolder.length - 1].getAbsolutePath());
                        }
                        allFiles.add(fileInfo);
                    }
                }
            }
            if (makeSelfFolder) {
                FileInfo selfFolder = new FileInfo();
                selfFolder.setDisplayName(file.getName());
                selfFolder.setType(FileInfo.FILE_TYPE.FOLDER);
                selfFolder.setFileName(file.getAbsolutePath());
                selfFolder.setFilePath(contentPictures[contentPictures.length - 1].getAbsolutePath());
                allFiles.add(selfFolder);
            }
        }
        return allFiles;
    }

    private ArrayList<FileInfo> getFilesInFolder(File file) {
        ArrayList<FileInfo> allFiles = new ArrayList<FileInfo>();
        File[] contentPictures = file.listFiles();

        if (contentPictures == null || contentPictures.length == 0) {
            Log.e(TAG, "No Files found at the path mentioned");
        } else {
            for (int i = contentPictures.length - 1; i >= 0; i--) {
                File individualFile = contentPictures[i];
                if (individualFile.getName().contains("jpg") || individualFile.getName().contains("jpeg") || individualFile.getName().contains("png")) {
                    FileInfo fileInfo = new FileInfo();
                    fileInfo.setDisplayName(individualFile.getName());
                    fileInfo.setFilePath(individualFile.getAbsolutePath());

                    if (checkIfAlreadyPresent(fileInfo))
                        fileInfo.setSelected(true);
                    allFiles.add(fileInfo);
                }
            }
        }
        return allFiles;
    }

    public boolean checkIfAlreadyPresent(FileInfo fileInfo) {
        if (ApplicationController.selectedFiles != null && ApplicationController.selectedFiles.size() > 0) {
            if (ApplicationController.selectedFiles.contains(fileInfo.getFilePath()))
                return true;
        }
        return false;
    }
}
