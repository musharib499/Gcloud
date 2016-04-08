package com.gcloud.gaadi.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by gaurav on 2/3/16.
 */
public class InsuranceDB extends SQLiteOpenHelper {

    public static final String TABLE_IMAGES = "imagesTable";
    public static final String COLUMN_PROCESS_ID = "processId";
    public static final String COLUMN_CAR_ID = "carId";
    public static final String COLUMN_DOC_NAME = "docName";
    public static final String COLUMN_IMAGE_PATH = "imagePath";
    public static final String COLUMN_POLICY_ID = "policyId";

    public InsuranceDB(Context context) {
        super(context, "insurance", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_IMAGES + " ( "
                + COLUMN_CAR_ID + " INTEGER NOT NULL, "
                + COLUMN_PROCESS_ID + " INTEGER NOT NULL, "
                + COLUMN_DOC_NAME + " TEXT NOT NULL, "
                + COLUMN_POLICY_ID + " INTEGER DEFAULT 0, "
                + COLUMN_IMAGE_PATH + " TEXT NOT NULL, PRIMARY KEY (" + COLUMN_PROCESS_ID + ", " + COLUMN_DOC_NAME + "))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_IMAGES);
        onCreate(db);
    }
}
