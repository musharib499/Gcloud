package com.gcloud.gaadi.providers;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.gcloud.gaadi.ApplicationController;
import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.db.LeadsOfflineDB;

/**
 * Created by gauravkumar on 21/9/15.
 */
public class LeadsContentProvider extends ContentProvider {

    private UriMatcher sUriMatcher = new UriMatcher(-1);

    @Override
    public boolean onCreate() {
        sUriMatcher.addURI(Constants.LEADS_CONTENT_AUTHORITY, LeadsOfflineDB.TABLE_NAME, 1);
        sUriMatcher.addURI(Constants.LEADS_CONTENT_AUTHORITY, LeadsOfflineDB.CALL_LOG_TABLE_NAME, 2);
        sUriMatcher.addURI(Constants.LEADS_CONTENT_AUTHORITY, LeadsOfflineDB.TABLE_OFFLINE_UPDATE, 3);
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = ApplicationController.getLeadsOfflineDB().getReadableDatabase();
        switch (sUriMatcher.match(uri)) {
            case 1:
                return db.query(LeadsOfflineDB.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);

            case 2:
                return db.query(LeadsOfflineDB.CALL_LOG_TABLE_NAME,
                        projection, selection, selectionArgs, null, null, sortOrder);

            case 3:
                return db.query(LeadsOfflineDB.TABLE_OFFLINE_UPDATE, projection,
                        selection, selectionArgs, null, null, sortOrder);
        }
        return null;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = ApplicationController.getLeadsOfflineDB().getWritableDatabase();
        switch (sUriMatcher.match(uri)) {
            case 1:
                return ContentUris.withAppendedId(uri,
                        db.insertWithOnConflict(LeadsOfflineDB.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE));

            case 2:
                return ContentUris.withAppendedId(uri,
                        db.insertWithOnConflict(LeadsOfflineDB.CALL_LOG_TABLE_NAME, null, values,
                                SQLiteDatabase.CONFLICT_REPLACE));

            case 3:
                return ContentUris.withAppendedId(uri,
                        db.insertWithOnConflict(LeadsOfflineDB.TABLE_OFFLINE_UPDATE, null, values,
                                SQLiteDatabase.CONFLICT_REPLACE));
        }
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = ApplicationController.getLeadsOfflineDB().getWritableDatabase();
        switch (sUriMatcher.match(uri)) {
            case 1:
                return db.delete(LeadsOfflineDB.TABLE_NAME, selection, selectionArgs);

            case 2:
                return db.delete(LeadsOfflineDB.CALL_LOG_TABLE_NAME, selection, selectionArgs);

            case 3:
                return db.delete(LeadsOfflineDB.TABLE_OFFLINE_UPDATE, selection, selectionArgs);
        }
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = ApplicationController.getLeadsOfflineDB().getWritableDatabase();
        switch (sUriMatcher.match(uri)) {
            case 1:
                return db.update(LeadsOfflineDB.TABLE_NAME, values, selection, selectionArgs);
        }
        return 0;
    }
}
