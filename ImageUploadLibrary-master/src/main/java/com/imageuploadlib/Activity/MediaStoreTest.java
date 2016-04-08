package com.imageuploadlib.Activity;

/**
 * Created by Lakshay on 08-04-2015.
 */

import android.content.Intent;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.widget.GridView;

import com.imageuploadlib.R;
import com.imageuploadlib.Utils.Constants;


public class MediaStoreTest extends FragmentActivity implements LoaderManager.LoaderCallbacks<Cursor>, MediaScannerConnection.MediaScannerConnectionClient {


    private static final String FILE_TYPE = "*.jpg";
    SimpleCursorAdapter mAdapter;
    Uri myUri;
    private String SCAN_PATH;
    private MediaScannerConnection conn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.media_store_test);

        myUri = (Uri) getIntent().getExtras().get(Constants.URI_FOLDER);
        GridView gridView = (GridView) findViewById(R.id.gridview);

        mAdapter = new SimpleCursorAdapter(
                getBaseContext(),
                R.layout.gridview,
                null,
                new String[]{"_data"},
                new int[]{R.id.img},
                0
        );

        gridView.setAdapter(mAdapter);

        getSupportLoaderManager().initLoader(0, null, this);

//        Log.e(Constants.TAG , "media store test oncreate");
//        SCAN_PATH = Environment.getExternalStorageDirectory().toString() + "/DCIM/";
//
//        if (conn != null) {
//            conn.disconnect();
//        }
//        conn = new MediaScannerConnection(this, this);
//        conn.connect();

    }

    /**
     * A callback method invoked by the loader when initLoader() is called
     */
    @Override
    public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
//        Uri uri = MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI;

//        ArrayList<String> img = new ArrayList<>();
//        img.add(MediaStore.Images.Media._ID);
////        String[] proj={MediaStore.Images.Media.DATA};
//        final String imagesDirectory = "/storage/sdcard0/DCIM/";
//        String where = "_data LIKE '/storage/sdcard0/DCIM/%' AND (SELECT LENGTH(_data) - LENGTH(REPLACE(_data, '/', ''))) = 4";
//        String selection = MediaStore.Images.Media.DATA + " LIKE '" + imagesDirectory + "%'";
////        return new CursorLoader(this, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, img.toArray(new String[img.size()]), where, null, null);
//        return new CursorLoader(this, myUri, null, null, null, null);
        String[] projection = {
                MediaStore.Images.Media.DATA
        };
        CursorLoader cursorLoader = new CursorLoader(this, myUri, projection, null, null, null);

        return cursorLoader;
    }

    /**
     * A callback method, invoked after the requested content provider returned all the data
     */
    @Override
    public void onLoadFinished(Loader<Cursor> arg0, Cursor data) {
//        mAdapter.swapCursor(data);
        if (data != null) {
            int columnIndex = data.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

            data.moveToFirst();
//            imagePath = data.getString(columnIndex);
        } else {
//            imagePath = myUri.getPath();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onMediaScannerConnected() {
        Log.e("onMediaScannerConnected", "success" + conn);
        conn.scanFile(SCAN_PATH, FILE_TYPE);
    }

    @Override
    public void onScanCompleted(String s, Uri uri) {
        try {
            Log.e("onScanCompleted", uri + "success" + conn);
            if (uri != null) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(uri);
                startActivity(intent);
            }
        } catch (Exception e) {
            Log.e(Constants.TAG, e.getMessage());
        } finally {
            conn.disconnect();
            conn = null;
        }
    }
}


