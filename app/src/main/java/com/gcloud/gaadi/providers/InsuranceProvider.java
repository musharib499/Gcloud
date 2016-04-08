package com.gcloud.gaadi.providers;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.gcloud.gaadi.ApplicationController;
import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.db.InsuranceDB;

public class InsuranceProvider extends ContentProvider {

    private UriMatcher sUriMatcher = new UriMatcher(-1);

    public InsuranceProvider() {
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = ApplicationController.getInsuranceDB().getWritableDatabase();
        switch (sUriMatcher.match(uri)) {
            case 1:
                return db.delete(InsuranceDB.TABLE_IMAGES, selection, selectionArgs);

            default:
                return 0;
        }
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        return super.bulkInsert(uri, values);
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        SQLiteDatabase db = ApplicationController.getInsuranceDB().getWritableDatabase();
        switch (sUriMatcher.match(uri)) {
            case 1:
                return ContentUris.withAppendedId(uri,
                        db.insertWithOnConflict(InsuranceDB.TABLE_IMAGES, null, values, SQLiteDatabase.CONFLICT_REPLACE));

            default:
                return null;
        }
    }

    @Override
    public boolean onCreate() {
        sUriMatcher.addURI(Constants.INSURANCE_CONTENT_AUTHORITY, InsuranceDB.TABLE_IMAGES, 1);
        return true;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = ApplicationController.getInsuranceDB().getReadableDatabase();
        switch (sUriMatcher.match(uri)) {
            case 1:
                return db.query(InsuranceDB.TABLE_IMAGES, projection, selection, selectionArgs, null, null, sortOrder);

            default:
                return null;
        }
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        SQLiteDatabase db = ApplicationController.getInsuranceDB().getWritableDatabase();
        switch (sUriMatcher.match(uri)) {
            case 1:
                return db.updateWithOnConflict(InsuranceDB.TABLE_IMAGES, values, selection, selectionArgs, SQLiteDatabase.CONFLICT_REPLACE);

            default:
                return 0;
        }
    }
}
