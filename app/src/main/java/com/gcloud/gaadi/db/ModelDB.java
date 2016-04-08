package com.gcloud.gaadi.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.os.Environment;

import com.gcloud.gaadi.model.ModelObject;
import com.gcloud.gaadi.utils.GCLog;

import java.util.ArrayList;

/**
 * Created by ankit on 5/12/14.
 */
public class ModelDB extends SQLiteOpenHelper {

    public static final String DATABASE_FILE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();
    public static final String DATABASE_NAME = "model.db";
    public static final String MODEL_TABLE = "model";
    public static final String ID = "_id";
    public static final String MODEL_ID = "modelid";
    public static final String MODEL_NAME = "modelname";
    public static final String MAKE_ID = "makeid";
    public static final String[] ALL_COLUMNS = new String[]{
            ID,
            MODEL_ID,
            MODEL_NAME,
            MAKE_ID
    };
    private static final int VERSION = 1;
    private static final String MODEL_SQL = "CREATE TABLE "
            + MODEL_TABLE + " ("
            + ID + " INTEGER NOT NULL, "
            + MODEL_ID + " TEXT NOT NULL, "
            + MODEL_NAME + " TEXT NOT NULL, "
            + MAKE_ID + " TEXT NOT NULL, PRIMARY KEY (" + ID + ", " + MODEL_ID + "))";
    private Context mContext;

    public ModelDB(Context context) {
        super(context, context.getDatabasePath(DATABASE_NAME).getPath(), null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(MODEL_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MODEL_TABLE);
        onCreate(db);
    }

    public long insertModels(ArrayList<ModelObject> models) {
        long insertId = -1;
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "INSERT INTO " + MODEL_TABLE + " VALUES(?, ?, ?, ?);";
        SQLiteStatement statement = db.compileStatement(sql);

        try {
            db.beginTransaction();

            for (ModelObject model : models) {

                statement.clearBindings();
                statement.bindLong(1, Long.parseLong(model.getModelId()));
                statement.bindString(2, model.getModelId());
                statement.bindString(3, model.getModelName());
                statement.bindString(4, model.getMakeId());

                insertId = statement.executeInsert();
                /*ContentValues contentValues = new ContentValues();
                contentValues.put(ID, Integer.parseInt(model.getModelId()));
                contentValues.put(MODEL_ID, model.getModelId());
                contentValues.put(MODEL_NAME, model.getModelName());
                contentValues.put(MAKE_ID, model.getMakeId());
                insertId = db.insertWithOnConflict(MODEL_TABLE, null, contentValues, SQLiteDatabase.CONFLICT_IGNORE);*/
            }
            db.setTransactionSuccessful();

        } catch (Exception e) {

        } finally {
            db.endTransaction();
        }

        return insertId;
    }

    public ArrayList<ModelObject> getModels() {
        ArrayList<ModelObject> models = new ArrayList<ModelObject>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(MODEL_TABLE, ALL_COLUMNS, null, null, null, null, null);

        cursor.moveToFirst();

        while (!cursor.isLast()) {
            int id = cursor.getInt(cursor.getColumnIndex(ID));
            String modelId = cursor.getString(cursor.getColumnIndex(MODEL_ID));
            String modelName = cursor.getString(cursor.getColumnIndex(MODEL_NAME));
            String makeId = cursor.getString(cursor.getColumnIndex(MAKE_ID));

            ModelObject modelObject = new ModelObject();
            modelObject.setId(id);
            modelObject.setModelId(modelId);
            modelObject.setModelName(modelName);
            modelObject.setMakeId(makeId);
            models.add(modelObject);
            cursor.moveToNext();

        }

        cursor.close();
        models.trimToSize();

        return models;
    }

    public ModelObject getModelById(String makeId, String modelId) {
        ModelObject modelObject = new ModelObject();

        GCLog.e("make id = " + makeId + ", modelid = " + modelId);

        SQLiteDatabase db = this.getReadableDatabase();
        String selection = MAKE_ID + "=? AND " + MODEL_ID + "=?";
        String[] selectionArgs = new String[]{makeId, modelId};
        Cursor cursor = db.rawQuery("SELECT * FROM " + MODEL_TABLE + " WHERE " + MAKE_ID + "='" + makeId + "' AND " + MODEL_ID + "='" + modelId + "'", null);
        /*Cursor cursor = db.query(MODEL_TABLE, MODEL_ALL_COLUMNS, selection, selectionArgs, null, null, null);*/
        cursor.moveToFirst();
        //modelObject.setId(cursor.getInt(cursor.getColumnIndex(ID)));
        modelObject.setMakeId(cursor.getString(cursor.getColumnIndex(MAKE_ID)));
        modelObject.setModelId(cursor.getString(cursor.getColumnIndex(MODEL_ID)));
        modelObject.setModelName(cursor.getString(cursor.getColumnIndex(MODEL_NAME)));

        return modelObject;
    }
}
