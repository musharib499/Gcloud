package com.gcloud.gaadi.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.os.Environment;

import com.gcloud.gaadi.model.MakeObject;

import java.util.ArrayList;

/**
 * Created by ankit on 5/12/14.
 */
public class MakeDB extends SQLiteOpenHelper {

    public static final String DATABASE_FILE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();
    public static final String DATABASE_NAME = "make.db";
    public static final String MAKE_TABLE = "make";
    public static final String ID = "_id";
    public static final String MAKE_ID = "makeid";
    public static final String MAKE_NAME = "makeName";
    private static final int VERSION = 1;
    private static final String MAKE_SQL = "CREATE TABLE "
            + MAKE_TABLE + " ("
            + ID + " INTEGER NOT NULL, "
            + MAKE_ID + " TEXT NOT NULL, "
            + MAKE_NAME + " TEXT NOT NULL, PRIMARY KEY (" + ID + ", " + MAKE_ID + "))";
    private static String[] ALL_COLUMNS = new String[]{ID, MAKE_ID, MAKE_NAME};
    private Context mContext;

    public MakeDB(Context context) {
        super(context, context.getDatabasePath(DATABASE_NAME).getPath(), null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(MAKE_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MAKE_TABLE);
        onCreate(db);
    }

    public long insertMakes(ArrayList<MakeObject> makeObjects) {
        long insertId = -1;
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "INSERT INTO " + MAKE_TABLE + " VALUES(?, ?, ?);";
        SQLiteStatement statement = db.compileStatement(sql);

        try {
            db.beginTransaction();

            for (MakeObject makeObject : makeObjects) {
                statement.clearBindings();
                statement.bindLong(1, Long.parseLong(makeObject.getMakeId()));
                statement.bindString(2, makeObject.getMakeId());
                statement.bindString(3, makeObject.getMake());
                insertId = statement.executeInsert();
                /*ContentValues contentValues = new ContentValues();
                contentValues.put(ID, Integer.parseInt(makeObject.getMakeId()));
                contentValues.put(MAKE_ID, makeObject.getMakeId());
                contentValues.put(MAKE_NAME, makeObject.getMakeid());
                insertId = db.insertWithOnConflict(MAKE_TABLE, null, contentValues, SQLiteDatabase.CONFLICT_IGNORE);*/
            }
            db.setTransactionSuccessful();

        } catch (Exception e) {

        } finally {
            db.endTransaction();
        }

        return insertId;
    }

    public ArrayList<MakeObject> getMakes() {
        SQLiteDatabase db = this.getReadableDatabase();

        ArrayList<MakeObject> makes = new ArrayList<MakeObject>();

        Cursor cursor = db.query(MAKE_TABLE, ALL_COLUMNS, null, null, null, null, null);

        cursor.moveToFirst();

        while (!cursor.isLast()) {
            int id = cursor.getInt(cursor.getColumnIndex(ID));
            String makeid = cursor.getString(cursor.getColumnIndex(MAKE_ID));
            String makename = cursor.getString(cursor.getColumnIndex(MAKE_NAME));
            MakeObject makeObject = new MakeObject();
            makeObject.setId(id);
            makeObject.setMakeId(makeid);
            makeObject.setMake(makename);
            makes.add(makeObject);
            cursor.moveToNext();
        }
        cursor.close();

        makes.trimToSize();
        return makes;
    }

    public MakeObject getMakeById(String makeId) {
        MakeObject makeObject = new MakeObject();
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = MAKE_ID + " = ?";
        String[] selectionArgs = new String[]{makeId};
        Cursor cursor = db.query(MAKE_TABLE, ALL_COLUMNS, selection, selectionArgs, null, null, null);
        cursor.moveToFirst();
        makeObject.setId(cursor.getInt(cursor.getColumnIndex(ID)));
        makeObject.setMakeId(cursor.getString(cursor.getColumnIndex(MAKE_ID)));
        makeObject.setMake(cursor.getString(cursor.getColumnIndex(MAKE_NAME)));

        return makeObject;
    }
}
