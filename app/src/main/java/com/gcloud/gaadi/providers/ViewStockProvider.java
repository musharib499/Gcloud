package com.gcloud.gaadi.providers;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.gcloud.gaadi.ApplicationController;
import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.db.GCloudStocksDB;
import com.gcloud.gaadi.db.ViewStockModel;

import java.util.HashMap;

/**
 * Created by alokmishra on 25/2/16.
 */
public class ViewStockProvider extends ContentProvider {

    private static final String TYPE_CURSOR_ITEM = "vnd.android.cursor.item/";
    private static final String TYPE_CURSOR_DIR = "vnd.android.cursor.dir/";

    private static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
    public static final String CONTENT_URI_BASE = "content://" + Constants.VIEWSTOCK_CONTENT_AUTHORITY;
    static final String PROVIDER_NAME = Constants.VIEWSTOCK_CONTENT_AUTHORITY;//"com.gcloud.gaadi.providers.ViewStockProvider";
    static final String URL = "content://" + PROVIDER_NAME + "/"+ ViewStockModel.AVAILABLE_TABLE;

    public static final Uri CONTENT_URI = Uri.parse(URL);

    private static final int URI_AVAILABLE_TYPE = 1;
    private static final int URI_AVAILABLE_TYPE_ID = 2;

    private static HashMap<String, String> values;

    static {
        URI_MATCHER.addURI(PROVIDER_NAME, ViewStockModel.AVAILABLE_TABLE, URI_AVAILABLE_TYPE);
        URI_MATCHER.addURI(PROVIDER_NAME, ViewStockModel.AVAILABLE_TABLE + "/#", URI_AVAILABLE_TYPE_ID);
    }

    private SQLiteDatabase db;


    @Override
    public boolean onCreate() {
       /* db = ApplicationController.getWritableDB();
        if (db != null) {
            return true;
        }
        return false;*/
        Context context = getContext();
        GCloudStocksDB dbHelper = new GCloudStocksDB(context);
        db = dbHelper.getWritableDatabase();
        if (db != null) {
            return true;
        }
        return false;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        switch (URI_MATCHER.match(uri)) {

            case URI_AVAILABLE_TYPE:
                qb.setTables(ViewStockModel.AVAILABLE_TABLE);
                qb.setProjectionMap(values);
                break;

            case URI_AVAILABLE_TYPE_ID:
                qb.setTables(ViewStockModel.AVAILABLE_TABLE);
                qb.setProjectionMap(values);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        Cursor c = qb.query(db, projection, selection, selectionArgs, null,
                null, sortOrder);
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {

        int match = URI_MATCHER.match(uri);

        switch (match) {

            case URI_AVAILABLE_TYPE:
                return TYPE_CURSOR_DIR + ViewStockModel.AVAILABLE_TABLE;

            case URI_AVAILABLE_TYPE_ID:
                return TYPE_CURSOR_ITEM + ViewStockModel.AVAILABLE_TABLE;

        }
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        db = ApplicationController.getWritableDB();
        long rowID;

        switch (URI_MATCHER.match(uri)) {

            case URI_AVAILABLE_TYPE:
                rowID = db.insertWithOnConflict(ViewStockModel.AVAILABLE_TABLE, "", values, SQLiteDatabase.CONFLICT_REPLACE);
                if (rowID > 0) {
                    Uri _uri = ContentUris.withAppendedId(uri, rowID);
                    return _uri;
                }

            case URI_AVAILABLE_TYPE_ID:
                rowID = db.insertWithOnConflict(ViewStockModel.AVAILABLE_TABLE, "", values, SQLiteDatabase.CONFLICT_REPLACE);
                if (rowID > 0) {
                    Uri _uri = ContentUris.withAppendedId(uri, rowID);
                    return _uri;
                }

            default:
                throw new SQLException("Failed to add a record into " + uri);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        db = ApplicationController.getWritableDB();
        int count = 0;

        switch (URI_MATCHER.match(uri)) {

            case URI_AVAILABLE_TYPE:
                count = db.delete(ViewStockModel.AVAILABLE_TABLE, selection, selectionArgs);
                break;

            case URI_AVAILABLE_TYPE_ID:
                count = db.delete(ViewStockModel.AVAILABLE_TABLE, selection, selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);

        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        db = ApplicationController.getWritableDB();
        int count = 0;
        switch (URI_MATCHER.match(uri)) {
            case URI_AVAILABLE_TYPE:
                count = db.update(ViewStockModel.AVAILABLE_TABLE, values, selection, selectionArgs);
                break;

            case URI_AVAILABLE_TYPE_ID:
                count = db.update(ViewStockModel.AVAILABLE_TABLE, values, selection, selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }
}
