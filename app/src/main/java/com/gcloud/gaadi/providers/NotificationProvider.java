package com.gcloud.gaadi.providers;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.gcloud.gaadi.ApplicationController;
import com.gcloud.gaadi.db.MakeModelVersionDB;

public class NotificationProvider extends ContentProvider {
    public NotificationProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = ApplicationController.getMakeModelVersionDB().getWritableDatabase();
        int count = db.delete(MakeModelVersionDB.TABLE_NOTIFICATION, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = ApplicationController.getMakeModelVersionDB().getWritableDatabase();
        long rowId = db.insert(MakeModelVersionDB.TABLE_NOTIFICATION, null, values);
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, rowId);
    }

    @Override
    public boolean onCreate() {
        // TODO: Implement this to initialize your content provider on startup.
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = ApplicationController.getMakeModelVersionDB().getReadableDatabase();
        Cursor cursor = db.query(MakeModelVersionDB.TABLE_NOTIFICATION,
                projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        SQLiteDatabase db = ApplicationController.getMakeModelVersionDB().getWritableDatabase();
        int count = db.update(MakeModelVersionDB.TABLE_NOTIFICATION, values, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }
}
