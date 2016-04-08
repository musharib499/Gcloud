package com.gcloud.gaadi.syncadapter;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.db.ManageLeadDB;

/**
 * Created by Gaurav on 25-08-2015.
 */
public class SplashContentProvider extends ContentProvider {

    private UriMatcher sUriMatcher = new UriMatcher(-1);

    @Override
    public boolean onCreate() {
        sUriMatcher.addURI(Constants.LMS_CONTENT_AUTHORITY, ManageLeadDB.TABLE_NAME, 1);
        return true;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
