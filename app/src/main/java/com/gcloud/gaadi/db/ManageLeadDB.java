package com.gcloud.gaadi.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Gaurav on 10-06-2015.
 */
public class ManageLeadDB extends SQLiteOpenHelper {

    final static private String DATABASE_NAME = "leadManage.db";
    final static private int DB_VERSION = 1;
    private static final String TAG = "ManageLeadDB";
    final public static String TABLE_NAME = "leadManage";
    //final private String ID = "rowId";
    final public static String LEAD_ID = "leadId";
    final public static String NAME = "name";
    final public static String MAKE_ID = "makeId";
    final public static String MODEL_NAME = "modelName";
    final public static String LEAD_DATE = "leadDate";
    final public static String CURRENT_TIMESTAMP = "currentTime";
    final public static String NOTIFICATION_TIME = "notificationTime";
    final public static String REQUEST_CODE = "requestCode";
    final public static String LMS_STATUS = "lmsStatus";
    final public static String SERVER_STATUS = "serverStatus";
    final public static String NUMBER = "number";
    final public static String SYNCED = "synced";


    public ManageLeadDB(Context context) {
        super(context, context.getDatabasePath(DATABASE_NAME).getName(), null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String query = "CREATE TABLE "
                + TABLE_NAME + " ("
                + LEAD_ID + " TEXT , "
                + NAME + " TEXT NOT NULL, "
                + MAKE_ID + " TEXT NOT NULL, "
                + MODEL_NAME + " TEXT , "
                + NUMBER + " TEXT , "
//                + LEAD_DATE + " TEXT , "
                + LMS_STATUS + " TEXT , "
                + SERVER_STATUS + " TEXT , "
                + CURRENT_TIMESTAMP + " TEXT , "
                + REQUEST_CODE + " INT NOT NULL , "
                + SYNCED + " INT NOT NULL , "
                + NOTIFICATION_TIME + " TEXT , PRIMARY KEY (" + NUMBER + "))";
        sqLiteDatabase.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    /*public String getStatus(String *//*leadId*//*number) {
        String status = "";
        SQLiteDatabase db = this.getReadableDatabase();
        db.beginTransaction();
        Cursor cursor = db.query(true, TABLE_NAME, new String[]{LMS_STATUS}, NUMBER + " = ?", new String[]{number}, null, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                status = cursor.getString(cursor.getColumnIndex(LMS_STATUS));
            }
            cursor.close();
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        return status;
    }*/

    /*public void insert(LeadData leadData, int synced) {
        ContentValues values = new ContentValues();
        values.put(LEAD_ID, leadData.getLeadId());
        values.put(NAME, leadData.getName());
        values.put(MAKE_ID, leadData.getMakeId());
        values.put(MODEL_NAME, leadData.getModelName());
//        values.put(LEAD_DATE, leadData.getDate());
        values.put(NOTIFICATION_TIME, "0");
        values.put(LMS_STATUS, leadData.getStatus());
        values.put(SERVER_STATUS, leadData.getApiStatus());
        values.put(NUMBER, leadData.getNumber());
        values.put(CURRENT_TIMESTAMP, String.valueOf(DateTime.now().getMillis()));
        values.put(REQUEST_CODE, leadData.getRequestCode());
        values.put(SYNCED, synced);
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        db.insertWithOnConflict(TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.setTransactionSuccessful();
        db.endTransaction();
    }*/

    /*public void updateLMSData(LeadData leadData) {
        ContentValues values = new ContentValues();
        values.put(NAME, leadData.getName());
        values.put(MAKE_ID, leadData.getMakeId());
        values.put(MODEL_NAME, leadData.getModelName());
//        values.put(LEAD_DATE, leadData.getDate());
        values.put(NOTIFICATION_TIME, leadData.getNotificationTime());
        values.put(LMS_STATUS, leadData.getStatus());
        values.put(SERVER_STATUS, leadData.getApiStatus());
        values.put(CURRENT_TIMESTAMP, String.valueOf(leadData.getChangeTime()));
        values.put(SYNCED, 1);
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        db.updateWithOnConflict(TABLE_NAME, values, NUMBER + " = ?", new String[]{leadData.getNumber()}, SQLiteDatabase.CONFLICT_REPLACE);
        db.setTransactionSuccessful();
        db.endTransaction();
    }*/

    /*public boolean isSlotAvailable(long time) {
        boolean result = false;
        SQLiteDatabase db = this.getReadableDatabase();
        db.beginTransaction();
        Cursor cursor = db.query(true,
                TABLE_NAME,
                new String[]{NOTIFICATION_TIME},
                NOTIFICATION_TIME + " = ?",
                new String[]{Long.toString(time)},
                null, null, null, null);
        if (cursor != null) {
            result = cursor.getCount() == 0;
            cursor.close();
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        return result;
    }*/

    /*public void update(String status, String *//*leadId*//*number) {
        ContentValues values = new ContentValues();
        values.put(LMS_STATUS, status);
        values.put(SERVER_STATUS, "Closed");
        values.put(CURRENT_TIMESTAMP, String.valueOf(DateTime.now().getMillis()));
        values.put(SYNCED, 0);
        //GCLog.e("current time" + DateTime.now().getMillis());
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        db.updateWithOnConflict(TABLE_NAME, values, NUMBER + " = ?", new String[]{number}, SQLiteDatabase.CONFLICT_REPLACE);
        db.setTransactionSuccessful();
        db.endTransaction();
    }*/


    /*public void updateNotificationTime(String *//*leadId*//*number, long notificationTime, String status, String apiStatus) {
        ContentValues values = new ContentValues();
        values.put(NOTIFICATION_TIME, Long.toString(notificationTime));
        values.put(LMS_STATUS, status);
        values.put(SERVER_STATUS, apiStatus);
        values.put(CURRENT_TIMESTAMP, String.valueOf(DateTime.now().getMillis()));
        values.put(SYNCED, 0);
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        if (db.updateWithOnConflict(TABLE_NAME, values, NUMBER + " = ?", new String[]{number}, SQLiteDatabase.CONFLICT_REPLACE) == 1)
            GCLog.w(TAG, "update Worked");
        db.setTransactionSuccessful();
        db.endTransaction();
    }
    public void delete(String leadId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        db.delete(TABLE_NAME, LEAD_ID + " = ?", new String[]{leadId});
        db.setTransactionSuccessful();
        db.endTransaction();
    }*/

    /*public ArrayList<LeadData> getCompleteTable() {
        ArrayList<LeadData> result = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        db.beginTransaction();
        Cursor cursor = db.query(true, TABLE_NAME, null,
                NOTIFICATION_TIME+" > ?",
                new String[]{Long.toString(DateTime.now().getMillis()+60000)}, // get data for which notification time
                null, null, null, null);                                                        // is 1 minute from now
        if (cursor.moveToFirst()) {
//            GCLog.w("Gaurav", "entered move to first count: "+cursor.getCount());
            while (!cursor.isAfterLast()) {
//                GCLog.w("Gaurav", "entered while");
                result.add(getLeadData(cursor));
                cursor.moveToNext();
            }
            cursor.close();
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        return result;
    }*/

    /*public ArrayList<LeadData> getUnsyncedData() {
        ArrayList<LeadData> result = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        db.beginTransaction();
        Cursor cursor = db.query(true, TABLE_NAME, null,
                SYNCED + " = ?",
                new String[]{"0"}, // get data for which notification time
                null, null, null, null);                                                        // is 1 minute from now
        if (cursor.moveToFirst()) {
//            GCLog.w("Gaurav", "entered move to first count: "+cursor.getCount());
            while (!cursor.isAfterLast()) {
//                GCLog.w("Gaurav", "entered while");
                result.add(getLeadData(cursor));
                cursor.moveToNext();
            }
            cursor.close();
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        return result;
    }*/

    /*private LeadData getLeadData(Cursor cursor) {
        LeadData leadData = new LeadData();
        leadData.setStatus(cursor.getString(cursor.getColumnIndex(LMS_STATUS)));
        leadData.setName(cursor.getString(cursor.getColumnIndex(NAME)));
        leadData.setNumber(cursor.getString(cursor.getColumnIndex(NUMBER)));
        leadData.setLeadId(cursor.getString(cursor.getColumnIndex(LEAD_ID)));
        leadData.setMakeId(cursor.getString(cursor.getColumnIndex(MAKE_ID)));
        leadData.setModelName(cursor.getString(cursor.getColumnIndex(MODEL_NAME)));
        leadData.setNotificationTime(Long.valueOf(cursor.getString(cursor.getColumnIndex(NOTIFICATION_TIME))));
        leadData.setChangeTime(Long.valueOf(cursor.getString(cursor.getColumnIndex(CURRENT_TIMESTAMP))));
        leadData.setApiStatus(cursor.getString(cursor.getColumnIndex(SERVER_STATUS)));
        //GCLog.w("LeadManageDatabase", "notification time from db: " + Long.toString(leadData.getNotificationTime()));
        return leadData;
    }*/

    /*public long getNotificationTime(String *//*leadId*//*number) {
        long result = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        db.beginTransaction();
        Cursor cursor = db.query(true,
                TABLE_NAME,
                new String[]{NOTIFICATION_TIME},
                NUMBER + " = ?", new String[]{number},
                null, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                result = Long.valueOf(cursor.getString(cursor.getColumnIndex(NOTIFICATION_TIME)));
                cursor.close();
            }
        }
        db.setTransactionSuccessful(); db.endTransaction();
        return result;
    }*/

    /*public int getRequestCode(String number) {
        int result = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        db.beginTransaction();
        Cursor cursor = db.query(true,
                TABLE_NAME,
                new String[]{REQUEST_CODE},
                NUMBER + " = ?", new String[]{number},
                null, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                result = cursor.getInt(cursor.getColumnIndex(REQUEST_CODE));
                cursor.close();
            }
        }
        db.setTransactionSuccessful(); db.endTransaction();
        return result;
    }*/

    /*public LeadData getCompleteData(String number) {
        LeadData result = new LeadData();
        SQLiteDatabase db = this.getReadableDatabase();
        db.beginTransaction();
        Cursor cursor = db.query(true,
                TABLE_NAME,
                null,
                NUMBER + " = ?", new String[]{number},
                null, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                result.setRequestCode(cursor.getInt(cursor.getColumnIndex(REQUEST_CODE)));
                result.setLeadId(cursor.getString(cursor.getColumnIndex(LEAD_ID)));
                result.setNumber(number);
                result.setStatus(cursor.getString(cursor.getColumnIndex(LMS_STATUS)));
                result.setApiStatus(cursor.getString(cursor.getColumnIndex(SERVER_STATUS)));
                result.setMakeId(cursor.getString(cursor.getColumnIndex(MAKE_ID)));
                result.setModelName(cursor.getString(cursor.getColumnIndex(MODEL_NAME)));
                result.setName(cursor.getString(cursor.getColumnIndex(NAME)));
                result.setNotificationTime(Long.valueOf(cursor.getString(cursor.getColumnIndex(NOTIFICATION_TIME))));
                result.setChangeTime(Long.valueOf(cursor.getString(cursor.getColumnIndex(CURRENT_TIMESTAMP))));
                //GCLog.e("gaurav time from db: " + Long.valueOf(cursor.getString(cursor.getColumnIndex(CURRENT_TIMESTAMP))));
            } else {
                result = null;
            }
            cursor.close();
        } else {
            result = null;
        }
        db.setTransactionSuccessful(); db.endTransaction();
        return result;
    }*/
}
