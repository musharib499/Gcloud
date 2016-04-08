package com.gcloud.gaadi.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.gcloud.gaadi.constants.Constants;

/**
 * Created by gauravkumar on 17/9/15.
 */
public class LeadsOfflineDB extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "leadsOffline.db";
    public static final String TABLE_NAME = "leads";
    public static final String LEAD_TYPE = "leadType";      // Can only take Buyer or Seller
    public static final String FOLLOW_DATE_ANDROID = "followDateAndroid";
    public static final String CHANGE_TIME = "changeTime";
    //public static final String SYNC = "sync";

    //public static final String DEALER_ID = "dealerID";
    //public static final String CAR_ID = "car_id";
    //public static final String YEAR = "year";
    //public static final String COLOR = "color";
    //public static final String KM = "km";
    //public static final String IMAGE_ICON = "imageIcon";
    //public static final String PRICE = "price";
    //public static final String COMMENTS = "comments";
    //public static final String EMAIL_ID = "emailID";
    //public static final String FOLLOW_DATE = "followDate";
    public static final String NAME = "name";
    public static final String NUMBER = "number";
    public static final String BUDGET_FROM = "budgetfrom";
    public static final String BUDGET_TO = "budgetto";
    //public static final String BUDGET = "budget";
    //public static final String MAKE_ID = "makeID";
    //public static final String MAKE = "make";
    //public static final String MODEL = "model";
    public static final String SOURCE = "source";
    public static final String LEAD_STATUS = "lead_status";
    public static final String VERIFIED = "verified";
    //public static final String DATE_TIME = "dateTime";
    //public static final String LEAD_ID = "leadID";
    //public static final String VERIFIED = "verified";
    public static final String JSON_FORMAT = "jsonFormat";
    /*public static final String CAR_LIST = "carList";
    public static final String COMMENT_LIST = "commentList";*/

    public static final String CALL_LOG_TABLE_NAME = "callLogs";
    public static final String CALL_LOG_ID = "callLogId";
    public static final String CALL_LOG_TIME = "callLogTime";

    public static final String TABLE_OFFLINE_UPDATE = "tableOfflineUpdate";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_JSON = "json";
    private static final int VERSION = 4;

    public LeadsOfflineDB(Context context) {
        super(context, context.getDatabasePath(DATABASE_NAME).getPath(), null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE "
                + TABLE_NAME + " ("
                //+ DEALER_ID + " TEXT NOT NULL, "
                + LEAD_TYPE + " TEXT NOT NULL, "
                + FOLLOW_DATE_ANDROID + " TEXT, "
                + CHANGE_TIME + " TEXT NOT NULL, "
                //+ SYNC + " TEXT, "
                //+ CAR_ID + " TEXT NOT NULL, "
                //+ YEAR + " TEXT NOT NULL, "
                //+ COLOR + " TEXT, "
                //+ KM + " TEXT NOT NULL, "
                //+ IMAGE_ICON + " TEXT, "
                //+ PRICE + " TEXT, "
                //+ COMMENTS + " TEXT, "
                //+ EMAIL_ID + " TEXT, "
                //+ FOLLOW_DATE + " TEXT, "
                + NAME + " TEXT, "
                + NUMBER + " TEXT NOT NULL, "
                + BUDGET_FROM + " TEXT, "
                + BUDGET_TO + " TEXT, "
                //+ BUDGET + " TEXT, "
                //+ MAKE_ID + " TEXT NOT NULL, "
                //+ MAKE + " TEXT NOT NULL, "
                //+ MODEL + " TEXT NOT NULL, "
                + SOURCE + " TEXT, "
                + LEAD_STATUS + " TEXT, "
                + VERIFIED + " TEXT, "
                //+ DATE_TIME + " TEXT NOT NULL, "
                + JSON_FORMAT + " TEXT NOT NULL, PRIMARY KEY (" + NUMBER + "))";
                /*+ CAR_LIST + " TEXT, "
                + COMMENT_LIST + " TEXT, "*/
        //+ LEAD_ID + " TEXT NOT NULL, "
        //+ VERIFIED + " TEXT NOT NULL, PRIMARY KEY (" + NUMBER + "))";

        String callLogsTable = "CREATE TABLE "
                + CALL_LOG_TABLE_NAME + " ("
                + CALL_LOG_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
                + Constants.SHARE_SOURCE + " TEXT NOT NULL, "
                + Constants.SHARE_TYPE + " TEXT NOT NULL, "
                + Constants.MOBILE_NUM + " TEXT, "
                + CALL_LOG_TIME + " TEXT, "
                + Constants.CAR_ID + " TEXT NOT NULL)";
        db.execSQL(createTable);
        db.execSQL(callLogsTable);

        db.execSQL("CREATE TABLE " + TABLE_OFFLINE_UPDATE + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
                + COLUMN_JSON + " TEXT NOT NULL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + CALL_LOG_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_OFFLINE_UPDATE);
        onCreate(db);
    }

    /*public long insertInCallLog(String shareSource, String shareType, String mobile, String carId) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Constants.SHARE_SOURCE, shareSource);
        values.put(Constants.SHARE_TYPE, shareType);
        values.put(Constants.MOBILE_NUM, mobile);
        values.put(Constants.CAR_ID, carId);
        values.put(CALL_LOG_TIME, CommonUtils.MillisToSQLTime(DateTime.now().getMillis()));

        return db.insert(CALL_LOG_TABLE_NAME, null, values);
    }

    public Cursor getCallLogs() {
        SQLiteDatabase db = this.getReadableDatabase();
        db.beginTransaction();

        Cursor cursor = db.query(CALL_LOG_TABLE_NAME, null, null, null, null, null, null);

        db.setTransactionSuccessful();
        db.endTransaction();

        return cursor;
    }

    public long insertLeadDetailModel(String leadType,
                                      LeadDetailModel model) {
        long result = -1;
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        //values.put(SYNC, "1");
        values.put(LEAD_TYPE, leadType);
        values.put(FOLLOW_DATE_ANDROID, String.valueOf(CommonUtils.SQLTimeToMillis(model.getFollowDate())));
        //values.put(SYNC, "1");
        values.put(CHANGE_TIME, String.valueOf(CommonUtils.SQLTimeToMillis(model.getChangeTime())));
        //values.put(DEALER_ID, model.getDealerID());
        //values.put(CAR_ID, model.getCarID());
        //values.put(YEAR, model.getYear());
        //values.put(COLOR, model.getColor());
        //values.put(KM, model.getKm());
        //values.put(IMAGE_ICON, model.getImageIcon());
        //values.put(PRICE, model.getPrice());
        //values.put(COMMENTS, model.getComments());
        //values.put(EMAIL_ID, model.getEmailID());
        //values.put(FOLLOW_DATE, model.getFollowDate());
        values.put(NAME, model.getName());
        values.put(NUMBER, model.getNumber());
        values.put(BUDGET_FROM, model.getBudgetfrom());
        values.put(BUDGET_TO, model.getBudgetto());
        //values.put(BUDGET, model.getBudget());
        //values.put(MAKE_ID, String.valueOf(model.getMakeId()));
        //values.put(MAKE, model.getMakename());
        //values.put(MODEL, model.getModel());
        values.put(SOURCE, model.getSource());
        values.put(LEAD_STATUS, model.getLeadStatus());
        //values.put(DATE_TIME, model.getDateTimeUnFormatted());
        //values.put(LEAD_ID, model.getLeadId());
        //values.put(VERIFIED, model.getVerified());
        values.put(JSON_FORMAT, new Gson().toJson(model));
        *//*if (model.getCommentsArrayList() == null || model.getCommentsArrayList().isEmpty())
            values.put(COMMENT_LIST, "");
        else {
            JSONObject object = new JSONObject();
            try {
                object.put("comment", model.getCommentsArrayList());
                values.put(COMMENT_LIST, new Gson().toJson(object));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (model.getCarsList() == null || model.getCarsList().isEmpty())
            values.put(CAR_LIST, "");
        else{
            JSONObject object = new JSONObject();
            try {
                object.put("car_list", model.getCommentsArrayList());
                values.put(CAR_LIST, new Gson().toJson(object));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }*//*

        result = db.insertWithOnConflict(TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        return result;
    }

    public LeadsModel getLeadsModel(String leadType, String leadState,
                                    int pageNumber, String select, ArrayList<String> selectList) {
        LeadsModel model = null;
        SQLiteDatabase db = this.getReadableDatabase();
        db.beginTransaction();

        ArrayList<String> selectionList = new ArrayList<>();
        StringBuilder selection = new StringBuilder();
        if (!select.isEmpty()) {
            selection.append(select).append(" AND ");
            selectionList.addAll(selectList);
        }
        selection.append(LEAD_TYPE + " = ? AND " + FOLLOW_DATE_ANDROID);
        selectionList.add(leadType);

        switch (leadState) {
            case "NYCL":    // Not Yet Called Lead
                selection.append(" = ?");
                selectionList.add("0");
                break;

            case "TL":      // Today Lead
                selection.append(" > ? AND " + FOLLOW_DATE_ANDROID + " < ?");
                selectionList.add(String.valueOf(DateTime.now().withTimeAtStartOfDay().getMillis()));
                selectionList.add(String.valueOf(DateTime.now().plusDays(1).withTimeAtStartOfDay().getMillis()));
                break;

            case "PL":      // Past Lead
                selection.append(" < ? AND " + FOLLOW_DATE_ANDROID + " > ?");
                selectionList.add(String.valueOf(DateTime.now().withTimeAtStartOfDay().getMillis()));
                selectionList.add(String.valueOf(0));
                break;

            case "UL":      // Upcoming Lead
                selection.append(" > ?");
                selectionList.add(String.valueOf(DateTime.now().plusDays(1).withTimeAtStartOfDay().getMillis()));
                break;
        }

        StringBuilder limit = new StringBuilder().append((pageNumber - 1) * 10).append(", 10");

        //GCLog.e(selection.toString(), selectionList.toString());

        Cursor cursor = db.query(TABLE_NAME,
                null,
                selection.toString(),
                selectionList.toArray(new String[selectionList.size()]),
                null, null,
                CHANGE_TIME + " DESC",
                limit.toString());

        if (cursor == null) {
            GCLog.e("cursor is null");
            return null;
        }

        if (cursor.moveToFirst()) {
            model = new LeadsModel();
            model.setLeads(new ArrayList<LeadDetailModel>());
            //model = new Gson().fromJson(cursor.getString(cursor.getColumnIndex(JSON_DATA)), LeadsModel.class);
            while (!cursor.isAfterLast()) {
                model.getLeads().add(getLeadDetailModel(cursor));
                cursor.moveToNext();
            }
            cursor.close();
        } else {
            GCLog.e("cursor is blank");
            cursor.close();
        }

        db.setTransactionSuccessful();
        db.endTransaction();

        return model;
    }

    private LeadDetailModel getLeadDetailModel(Cursor cursor) {
        *//*LeadDetailModel model = new LeadDetailModel();
        model.setDealerID(cursor.getString(cursor.getColumnIndex(DEALER_ID)));
        model.setCarID(cursor.getString(cursor.getColumnIndex(CAR_ID)));
        model.setYear(cursor.getString(cursor.getColumnIndex(YEAR)));
        model.setColor(cursor.getString(cursor.getColumnIndex(COLOR)));
        model.setKm(cursor.getString(cursor.getColumnIndex(KM)));
        model.setImageIcon(cursor.getString(cursor.getColumnIndex(IMAGE_ICON)));
        model.setPrice(cursor.getString(cursor.getColumnIndex(PRICE)));
        model.setComments(cursor.getString(cursor.getColumnIndex(COMMENTS)));
        model.setEmailID(cursor.getString(cursor.getColumnIndex(EMAIL_ID)));
        model.setFollowDate(cursor.getString(cursor.getColumnIndex(FOLLOW_DATE)));
        model.setName(cursor.getString(cursor.getColumnIndex(NAME)));
        model.setNumber(cursor.getString(cursor.getColumnIndex(NUMBER)));
        model.setBudgetfrom(cursor.getString(cursor.getColumnIndex(BUDGET_FROM)));
        model.setBudgetto(cursor.getString(cursor.getColumnIndex(BUDGET_TO)));
        model.setBudget(cursor.getString(cursor.getColumnIndex(BUDGET)));
        model.setMakeId(Integer.valueOf(cursor.getString(cursor.getColumnIndex(MAKE_ID))));
        model.setMakename(cursor.getString(cursor.getColumnIndex(MAKE)));
        model.setModel(cursor.getString(cursor.getColumnIndex(MODEL)));
        model.setSource(cursor.getString(cursor.getColumnIndex(SOURCE)));
        model.setLeadStatus(cursor.getString(cursor.getColumnIndex(LEAD_STATUS)));
        model.setDateTime(cursor.getString(cursor.getColumnIndex(DATE_TIME)));
        model.setLeadId(cursor.getString(cursor.getColumnIndex(LEAD_ID)));
        model.setVerified(cursor.getString(cursor.getColumnIndex(VERIFIED)));
        if (!cursor.getString(cursor.getColumnIndex(CAR_LIST)).isEmpty()) {
            model.setCarsList(new Gson().fromJson(cursor.getString(cursor.getColumnIndex(CAR_LIST)), LeadDetailModel.class).getCarsList());
        }
        if (!cursor.getString(cursor.getColumnIndex(COMMENT_LIST)).isEmpty()) {
            GCLog.e("json "+cursor.getString(cursor.getColumnIndex(COMMENT_LIST)));
            model.setCommentsArrayList(new Gson().fromJson(cursor.getString(cursor.getColumnIndex(COMMENT_LIST)), LeadDetailModel.class).getCommentsArrayList());
        }*//*
        return new Gson().fromJson(cursor.getString(cursor.getColumnIndex(JSON_FORMAT)), LeadDetailModel.class);
    }

    public void deleteOldData() {
        SQLiteDatabase db = this.getWritableDatabase();

        String oldTime = String.valueOf(DateTime.now().minusMonths(6).withTimeAtStartOfDay().getMillis());

        db.delete(TABLE_NAME, CHANGE_TIME + " < ?", new String[]{oldTime});
    }

    public void deleteCallLog(int callLogId) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(CALL_LOG_TABLE_NAME, CALL_LOG_ID + " = ?", new String[]{String.valueOf(callLogId)});
    }*/

    /*public long getChangeTime(String number) {
        long changeTime = 0;

        SQLiteDatabase db = this.getReadableDatabase();
        db.beginTransaction();

        Cursor cursor = db.query(TABLE_NAME,
                new String[]{CHANGE_TIME},
                NUMBER + " = ?",
                new String[]{number},
                null, null, null);

        if (cursor == null || !cursor.moveToFirst()) {
            changeTime =  0;
        } else {
            changeTime =  Long.valueOf(cursor.getString(cursor.getColumnIndex(CHANGE_TIME)));
        }

        if (cursor != null) {
            cursor.close();
        }

        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();

        return changeTime;
    }*/
}
