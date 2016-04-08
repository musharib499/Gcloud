package com.gcloud.gaadi.syncadapter;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.gcloud.gaadi.ApplicationController;
import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.db.ManageLeadDB;

/**
 * Created by Gaurav on 25-08-2015.
 */
public class LMSContentProvider extends ContentProvider {

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
        Cursor cursor = null;
        switch (sUriMatcher.match(uri)) {
            case 1:
                SQLiteDatabase db = ApplicationController.getManageLeadDB().getReadableDatabase();
                db.beginTransaction();
                cursor = db.query(ManageLeadDB.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        sortOrder,
                        null, null);
                db.setTransactionSuccessful();
                db.endTransaction();

        }
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        switch (sUriMatcher.match(uri)) {
            case 1:
                SQLiteDatabase db = ApplicationController.getManageLeadDB().getWritableDatabase();
                return ContentUris.withAppendedId(uri,
                        db.insertWithOnConflict(ManageLeadDB.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE));
        }
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        switch (sUriMatcher.match(uri)) {
            case 1:
                SQLiteDatabase db = ApplicationController.getManageLeadDB().getWritableDatabase();
                return db.update(ManageLeadDB.TABLE_NAME, values, selection, selectionArgs);
        }
        return 0;
    }
}
