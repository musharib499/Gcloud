package com.gcloud.gaadi.ui;

/**
 * Created by Lakshay on 08-04-2015.
 */

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.widget.GridView;

import com.gcloud.gaadi.R;


public class MediaStoreTest extends FragmentActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    SimpleCursorAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.media_store_test);

        /** Getting a reference to gridview of the MainActivity layout */
        GridView gridView = (GridView) findViewById(R.id.gridview);

        /** Create an adapter for the gridview */
        /** This adapter defines the data and the layout for the grid view */
        mAdapter = new SimpleCursorAdapter(
                getBaseContext(),
                R.layout.gridview,
                null,
                new String[]{"_data"},
                new int[]{R.id.img},
                0
        );

        /** Setting adapter for the gridview */
        gridView.setAdapter(mAdapter);

        /** Loader to get images from the SD Card */
        getSupportLoaderManager().initLoader(0, null, this);

    }

    /**
     * A callback method invoked by the loader when initLoader() is called
     */
    @Override
    public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
        Uri uri = MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI;
        return new CursorLoader(this, uri, null, null, null, null);
    }

    /**
     * A callback method, invoked after the requested content provider returned all the data
     */
    @Override
    public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {
        mAdapter.swapCursor(arg1);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> arg0) {
        // TODO Auto-generated method stub
    }
}

