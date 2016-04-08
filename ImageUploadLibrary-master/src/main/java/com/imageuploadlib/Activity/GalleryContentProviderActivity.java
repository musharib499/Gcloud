package com.imageuploadlib.Activity;


import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.widget.GridView;

import com.imageuploadlib.Adapters.CustomGalleryCursorAdapter;
import com.imageuploadlib.R;

/**
 * Created by ankitgarg on 09/04/15.
 */
public class GalleryContentProviderActivity extends ActionBarActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    GridView mGridView;
    String[] mProjection = {
            "_data",
            "_id",
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME

    };
    private CustomGalleryCursorAdapter mAdapter;
    private Cursor mCursor;
    private String selection = MediaStore.Images.Media.BUCKET_DISPLAY_NAME + " =?";
    private Uri mUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    private String[] selectionArgs;
    private String order = MediaStore.Images.Media.DATE_TAKEN + " DESC";

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (mCursor != null) {
            mCursor.close();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.folder_files);

        mGridView = (GridView) findViewById(R.id.gvFolderPhotos);

        String folderName = getIntent().getStringExtra(GalleryActivity.FOLDER_NAME);
        selectionArgs = new String[]{folderName};

        if (folderName != null && folderName.equals(".thumbnails")) {
            mUri = MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI;
            selection = null;
            selectionArgs = null;
            mProjection = null;
            order = MediaStore.Images.Thumbnails.IMAGE_ID + " DESC";
        }

        mCursor = getContentResolver().query(mUri, mProjection, selection, selectionArgs, order);
        mCursor.moveToFirst();

        mAdapter = new CustomGalleryCursorAdapter(this, mCursor);
        mGridView.setAdapter(mAdapter);

        getLoaderManager().initLoader(1, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        /** Invoking the uri */
        return new CursorLoader(this, mUri, mProjection, selection, selectionArgs, order);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }
}
