package com.gcloud.gaadi.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

import com.gcloud.gaadi.model.VersionObject;

import java.util.ArrayList;

/**
 * Created by ankit on 5/12/14.
 */
public class VersionDB extends SQLiteOpenHelper {

    public static final String DATABASE_FILE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();
    public static final String DATABASE_NAME = "version.db";
    public static final String VERSION_TABLE = "version";
    public static final String ID = "_id";
    public static final String MODEL_ID = "modelid";
    public static final String MAKE_ID = "makeid";
    public static final String VERSION_ID = "versionid";
    public static final String VERSION_NAME = "versionname";
    private static final int VERSION = 1;
    private static final String VERSION_SQL = "CREATE TABLE "
            + VERSION_TABLE + " ("
            + ID + " INTEGER NOT NULL, "
            + VERSION_ID + " TEXT NOT NULL, "
            + VERSION_NAME + " TEXT NOT NULL, "
            + MODEL_ID + " TEXT NOT NULL, "
            + MAKE_ID + " TEXT NOT NULL, PRIMARY KEY (" + ID + ", " + VERSION_ID + "))";
    private static String[] ALL_COLUMNS = new String[]{
            ID,
            VERSION_ID,
            VERSION_NAME,
            MODEL_ID,
            MAKE_ID
    };
    private Context mContext;

    public VersionDB(Context context) {
        super(context, context.getDatabasePath(DATABASE_NAME).getPath(), null, VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(VERSION_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + VERSION_TABLE);
        onCreate(db);
    }

    public long insertVersions(ArrayList<VersionObject> versions) {
        long insertId = -1;
        SQLiteDatabase db = this.getWritableDatabase();

        for (VersionObject version : versions) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(ID, Integer.parseInt(version.getVersionid()));
            contentValues.put(VERSION_ID, version.getVersionid());
            contentValues.put(VERSION_NAME, version.getVersionName());
            contentValues.put(MODEL_ID, version.getModelId());
            contentValues.put(MAKE_ID, version.getMakeId());
            insertId = db.insertWithOnConflict(VERSION_TABLE, null, contentValues, SQLiteDatabase.CONFLICT_IGNORE);

        }
        return insertId;
    }

    public ArrayList<VersionObject> getVersions() {
        ArrayList<VersionObject> versionObjects = new ArrayList<VersionObject>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(VERSION_TABLE, ALL_COLUMNS, null, null, null, null, null);
        cursor.moveToFirst();

        while (!cursor.isLast()) {
            int id = cursor.getInt(cursor.getColumnIndex(ID));
            String versionId = cursor.getString(cursor.getColumnIndex(VERSION_ID));
            String versionName = cursor.getString(cursor.getColumnIndex(VERSION_NAME));
            String modelId = cursor.getString(cursor.getColumnIndex(MODEL_ID));
            String makeId = cursor.getString(cursor.getColumnIndex(MAKE_ID));
            VersionObject versionObject = new VersionObject();
            versionObject.setId(id);
            versionObject.setVersionid(versionId);
            versionObject.setVersionName(versionName);
            versionObject.setModelId(modelId);
            versionObject.setMakeId(makeId);
            versionObjects.add(versionObject);
            cursor.moveToNext();

        }
        cursor.close();

        return versionObjects;
    }

}
