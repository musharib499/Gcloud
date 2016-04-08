package com.gcloud.gaadi.db;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.gcloud.gaadi.ApplicationController;
import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.events.DealerInsertEvent;
import com.gcloud.gaadi.events.MakeInsertEvent;
import com.gcloud.gaadi.events.ModelsInsertEvent;
import com.gcloud.gaadi.events.VersionsInsertEvent;
import com.gcloud.gaadi.model.CityData;
import com.gcloud.gaadi.model.DealerData;
import com.gcloud.gaadi.model.MakeObject;
import com.gcloud.gaadi.model.ModelObject;
import com.gcloud.gaadi.model.ShowroomData;
import com.gcloud.gaadi.model.VersionObject;
import com.gcloud.gaadi.syncadapter.GenericAccountService;
import com.gcloud.gaadi.syncadapter.SyncUtils;
import com.gcloud.gaadi.utils.CommonUtils;
import com.gcloud.gaadi.utils.GCLog;

import java.util.ArrayList;

/**
 * Created by ankit on 3/12/14.
 */
public class MakeModelVersionDB extends SQLiteOpenHelper {

    public static final String TABLE_NOTIFICATION = "tableNotification";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TIME = "time";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_JSON = "json";
    //public static final String  DATABASE_FILE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();
    public static final String DATABASE_NAME = "makemodelversion.db";
    public static final String MAKE_MODEL_VERSION_TABLE = "makemodelversion";
    public static final String MAKE_TABLE = "make";
    public static final String ID = "_id";
    public static final String MODEL_TABLE = "model";
    public static final String SHOWROOM_TABLE = "showroom";
    public static final String VERSION_TABLE = "version";
    public static final String DEALER_TABLE = "dealer";
    public static final String CITY_TABLE = "city";
    public static final String MMV_COLUMN_MAKE_MODEL_VERSION = "mmv";
    public static final String MMV_COLUMN_MODEL_VERSION = "mv";
    public static final String MMV_COLUMN_MAKE_MODEL = "mm";
    public static final String MODEL_RANK = "rank";
    public static final String MODEL_PARENT_ID = "parent_id";
    public static final String MMV_COLUMN_MAKE = "make";
    public static final String MMV_COLUMN_MODEL = "model";
    public static final String MMV_COLUMN_VERSION = "version";
    public static final String MMV_COLUMN_FUEL_TYPE = "fuelType";
    public static final String MODEL_LAUNCH_YEAR = "launchYear";
    public static final String MODEL_END_YEAR = "endYear";
    public static final String MMV_COLUMN_TRANSMISSION_TYPE = "transmission";
    public static final String MAKEID = "makeid";
    public static final String MODELID = "modelid";
    public static final String VERSIONID = "versionid";
    public static final String MODELNAME = "modelname";
    public static final String MAKENAME = "makename";
    public static final String VERSIONNAME = "versionname";
    public static final String FUEL_TYPE = "fueltype";
    public static final String TRANSMISSION_TYPE = "transmission";
    public static final String DEALER_ID = "dealer_id";
    public static final String DEALER_ORG = "organization";
    public static final String DEALER_USERNAME = "dealer_username";
    public static final String DEALER_UCDID = "dealer_ucdid";
    public static final String DEALER_MOBILE_SMS = "dealer_mobile_sms";
    public static final String DEALER_EMAIL = "dealer_email";
    public static final String DEALER_PASSWORD = "dealer_password";
    public static final String CITY_ID = "cityid";
    public static final String CITY_NAME = "cityname";
    public static final String STATE_ID = "stateid";
    public static final String REGION_ID = "regionid";
    public static final String ORDER_BY = "orderby";
    public static final String DEALERID = "dealerId";
    public static final String SHOWROOMID = "showroomid";
    public static final String SHOWROOMNAME = "showroomname";
    public static final String[] MODEL_ALL_COLUMNS = new String[]{
            ID,
            MODELID,
            MODELNAME,
            MAKEID,
            MAKENAME,
            MMV_COLUMN_MAKE_MODEL,
            MODEL_RANK,
            MODEL_PARENT_ID
    };
    public static final String[] SHOWROOMS_ALL_COLUMNS = new String[]{
            ID,
            DEALERID,
            SHOWROOMID,
            SHOWROOMNAME
    };
    private static final int VERSION = 7;
    private static final String CITY_SQL = "CREATE TABLE "
            + CITY_TABLE + " ("
            + ID + " INTEGER NOT NULL, "
            + CITY_ID + " TEXT NOT NULL, "
            + CITY_NAME + " TEXT NOT NULL, "
            + STATE_ID + " TEXT NOT NULL, "
            + ORDER_BY + " TEXT NOT NULL, "
            + REGION_ID + " TEXT NOT NULL, PRIMARY KEY (" + ID + ", " + CITY_ID + "))";
    private static final String SHOWROOM_SQL = "CREATE TABLE "
            + SHOWROOM_TABLE + " ("
            + ID + " INTEGER NOT NULL, "
            + DEALERID + " TEXT NOT NULL, "
            + SHOWROOMID + " TEXT NOT NULL, "
            + SHOWROOMNAME + " TEXT NOT NULL, PRIMARY KEY (" + ID + ", " + SHOWROOMID + "))";
    private static final String DEALER_SQL = "CREATE TABLE "
            + DEALER_TABLE + " ("
            + ID + " INTEGER NOT NULL, "
            + DEALER_ID + " TEXT NOT NULL, "
            + DEALER_UCDID + " TEXT NOT NULL, "
            + DEALER_USERNAME + " TEXT NOT NULL, "
            + DEALER_EMAIL + " TEXT NOT NULL, "
            + DEALER_MOBILE_SMS + " TEXT NOT NULL, "
            + DEALER_PASSWORD + " TEXT NOT NULL, "
            + DEALER_ORG + " TEXT NOT NULL, PRIMARY KEY (" + ID + ", " + DEALER_ID + "))";
    private static final String VERSION_SQL = "CREATE TABLE "
            + VERSION_TABLE + " ("
            + ID + " INTEGER NOT NULL, "
            + VERSIONID + " TEXT NOT NULL, "
            + VERSIONNAME + " TEXT NOT NULL, "
            + MODELID + " TEXT NOT NULL, "
            + FUEL_TYPE + " TEXT NOT NULL, "
            + TRANSMISSION_TYPE + " TEXT NOT NULL, "
            + MMV_COLUMN_MAKE_MODEL + " TEXT NOT NULL, "
            + MMV_COLUMN_MODEL_VERSION + " TEXT NOT NULL, "
            + MMV_COLUMN_MAKE_MODEL_VERSION + " TEXT NOT NULL, "
            + MAKENAME + " TEXT NOT NULL, "
            + MODELNAME + " TEXT NOT NULL, "
            + MODEL_LAUNCH_YEAR + " TEXT NOT NULL, "
            + MODEL_END_YEAR + " TEXT NOT NULL, "
            + MAKEID + " TEXT NOT NULL, PRIMARY KEY (" + ID + ", " + VERSIONID + "))";
    private static final String MODEL_SQL = "CREATE TABLE "
            + MODEL_TABLE + " ("
            + ID + " INTEGER NOT NULL, "
            + MODELID + " TEXT NOT NULL, "
            + MODELNAME + " TEXT NOT NULL, "
            + MAKEID + " TEXT NOT NULL, "
            + MAKENAME + " TEXT NOT NULL, "
            + MODEL_RANK + " TEXT NOT NULL, "
            + MODEL_PARENT_ID + " TEXT NOT NULL, "
            + MMV_COLUMN_MAKE_MODEL + " TEXT NOT NULL, PRIMARY KEY (" + ID + ", " + MODELID + "))";
    private static final String MAKE_SQL = "CREATE TABLE "
            + MAKE_TABLE + " ("
            + ID + " INTEGER NOT NULL, "
            + MAKEID + " TEXT NOT NULL, "
            + MAKENAME + " TEXT NOT NULL, PRIMARY KEY (" + ID + ", " + MAKEID + "))";
    private static long mmvid = -1;
    private static long did = -1;
    private static long cid = -1;
    private static long sid = -1;
    private static String[] CITY_ALL_COLUMNS = new String[]{
            ID,
            CITY_ID,
            CITY_NAME,
            STATE_ID,
            REGION_ID,
            ORDER_BY
    };
    private static String[] MMV_ALL_COLUMNS = new String[]{
            ID,
            MMV_COLUMN_MAKE_MODEL_VERSION,
            MMV_COLUMN_MODEL_VERSION,
            MMV_COLUMN_MAKE_MODEL,
            MMV_COLUMN_MAKE,
            MMV_COLUMN_MODEL,
            MMV_COLUMN_VERSION,
            MMV_COLUMN_TRANSMISSION_TYPE,
            MMV_COLUMN_FUEL_TYPE,
            MAKEID,
            MODELID,
            VERSIONID
    };
    private static String[] VERSION_COLUMNS = new String[]{
            ID,
            VERSIONID,
            VERSIONNAME,
            MODELID,
            MAKEID,
            TRANSMISSION_TYPE,
            FUEL_TYPE,
            MAKENAME,
            MODELNAME,
            MMV_COLUMN_MAKE_MODEL,
            MMV_COLUMN_MODEL_VERSION,
            MMV_COLUMN_MAKE_MODEL_VERSION,
            MODEL_LAUNCH_YEAR,
            MODEL_END_YEAR

    };
    private static String[] DEALER_ALL_COLUMNS = new String[]{
            ID,
            DEALER_ID,
            DEALER_ORG,
            DEALER_UCDID,
            DEALER_USERNAME,
            DEALER_EMAIL,
            DEALER_MOBILE_SMS,
            DEALER_PASSWORD
    };
    private static String[] MAKE_COLUMNS = new String[]{ID, MAKEID, MAKENAME};
    private Context mContext;
    private String MAKE_MODEL_VERSION_SQL = "CREATE TABLE "
            + MAKE_MODEL_VERSION_TABLE
            + " ("
            + ID + " INTEGER NOT NULL, "
            + MMV_COLUMN_MAKE_MODEL_VERSION + " TEXT NOT NULL, "
            + MMV_COLUMN_MODEL_VERSION + " TEXT NOT NULL, "
            + MMV_COLUMN_MAKE_MODEL + " TEXT NOT NULL, "
            + MMV_COLUMN_MAKE + " TEXT NOT NULL, "
            + MMV_COLUMN_MODEL + " TEXT NOT NULL, "
            + MMV_COLUMN_VERSION + " TEXT NOT NULL, "
            + MMV_COLUMN_FUEL_TYPE + " TEXT NOT NULL, "
            + MMV_COLUMN_TRANSMISSION_TYPE + " TEXT NOT NULL, "
            + MAKEID + " TEXT NOT NULL, "
            + MODELID + " TEXT NOT NULL, "
            + VERSIONID + " TEXT NOT NULL, PRIMARY KEY(" + ID + ", " + MMV_COLUMN_MAKE_MODEL_VERSION + "))";


    public MakeModelVersionDB(Context context) {
        super(context, context.getDatabasePath(DATABASE_NAME).getPath(), null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        /*db.execSQL(MAKE_MODEL_VERSION_SQL);
        */

        ApplicationController.getEventBus().register(this);
        db.execSQL(MAKE_SQL);
        db.execSQL(MODEL_SQL);
        db.execSQL(VERSION_SQL);
        db.execSQL(DEALER_SQL);
        db.execSQL(CITY_SQL);
        db.execSQL(SHOWROOM_SQL);

        db.execSQL("CREATE TABLE " + TABLE_NOTIFICATION + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY, "
                + COLUMN_TIME + " TEXT NOT NULL, "
                + COLUMN_TYPE + " INTEGER NOT NULL, "
                + COLUMN_JSON + " TEXT NOT NULL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        /*db.execSQL("DROP TABLE IF EXISTS " + MAKE_MODEL_VERSION_TABLE);*/

        GCLog.e("version upgrade from " + oldVersion + " to " + newVersion);
        db.execSQL("DROP TABLE IF EXISTS " + MODEL_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + MAKE_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + VERSION_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + DEALER_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + CITY_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + SHOWROOM_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTIFICATION);

        onCreate(db);

        if (!ContentResolver.getMasterSyncAutomatically()) {
            ContentResolver.setMasterSyncAutomatically(true);
        }
        ContentResolver.requestSync(GenericAccountService.GetAccount(SyncUtils.ACCOUNT_TYPE),
                Constants.SPLASH_CONTENT_AUTHORITY,
                new Bundle());
    }


    public long insertDealers(ArrayList<DealerData> dealers) {
        long insertId = -1;
        SQLiteDatabase db = this.getWritableDatabase();

        try {
            db.beginTransaction();
            for (DealerData dealer : dealers) {
                GCLog.e("inside DB dealer = " + dealer.toString());
                ContentValues contentValues = new ContentValues();
                contentValues.put(ID, ++did);
                contentValues.put(DEALER_ID, dealer.getDealerId());
                contentValues.put(DEALER_ORG, dealer.getOrganization());
                contentValues.put(DEALER_UCDID, dealer.getUCdid());
                contentValues.put(DEALER_USERNAME, dealer.getmUsername());
                contentValues.put(DEALER_EMAIL, dealer.getmEmail());
                contentValues.put(DEALER_MOBILE_SMS, dealer.getMobileSms());
                contentValues.put(DEALER_PASSWORD, dealer.getDealerPassword());
                insertId = db.insertWithOnConflict(DEALER_TABLE, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            ApplicationController.getEventBus().post(new DealerInsertEvent(true, "Error inserting dealers in db."));
            GCLog.e("Some problem in inserting dealers data");
        }
        db.endTransaction();


        return insertId;
    }

    public long insertShowrooms(ArrayList<ShowroomData> showrooms) {
        long insertId = -1;
        SQLiteDatabase db = this.getWritableDatabase();

        try {
            db.beginTransaction();
            for (ShowroomData showroom : showrooms) {
                GCLog.e("inside DB showrooms = " + showrooms.toString());
                ContentValues contentValues = new ContentValues();
                contentValues.put(ID, ++sid);
                contentValues.put(DEALERID, showroom.getDealerId());
                contentValues.put(SHOWROOMID, showroom.getShowroomId());
                contentValues.put(SHOWROOMNAME, showroom.getShowroomName());
                insertId = db.insertWithOnConflict(SHOWROOM_TABLE, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            ApplicationController.getEventBus().post(new DealerInsertEvent(true, "Error inserting dealers in db."));
            GCLog.e("Some problem in inserting dealers data " + e.getMessage());
        }
        db.endTransaction();


        return insertId;
    }

    public ArrayList<ShowroomData> getShowroomsByDealerId(String dealerID) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = DEALERID + " = ? ";
        String[] selectionArgs = new String[]{dealerID};
        ArrayList<ShowroomData> showrooms = new ArrayList<>();
        Cursor cursor = db.query(SHOWROOM_TABLE, SHOWROOMS_ALL_COLUMNS, selection, selectionArgs, null, null, null);
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            ShowroomData showroomData = new ShowroomData();
            showroomData.setDealerId(cursor.getString(cursor.getColumnIndex(DEALERID)));
            showroomData.setShowroomId(cursor.getString(cursor.getColumnIndex(SHOWROOMID)));
            showroomData.setShowroomName(cursor.getString(cursor.getColumnIndex(SHOWROOMNAME)));
            showrooms.add(showroomData);
            cursor.moveToNext();
        }
        return showrooms;

    }

    public ArrayList<ShowroomData> getShowrooms() {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<ShowroomData> showrooms = new ArrayList<ShowroomData>();
        Cursor cursor = db.query(SHOWROOM_TABLE, SHOWROOMS_ALL_COLUMNS, null, null, null, null, null);
        cursor.moveToFirst();

        int i = 0;
        while (i < cursor.getCount()) {
            ShowroomData showroomData = new ShowroomData();
            showroomData.setDealerId(cursor.getString(cursor.getColumnIndex(DEALERID)));
            showroomData.setShowroomId(cursor.getString(cursor.getColumnIndex(SHOWROOMID)));
            showroomData.setShowroomName(cursor.getString(cursor.getColumnIndex(SHOWROOMNAME)));
            showrooms.add(showroomData);
            ++i;
            cursor.moveToNext();
        }
        return showrooms;
    }


    public ArrayList<DealerData> getDealers() {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<DealerData> dealers = new ArrayList<DealerData>();
        Cursor cursor = db.query(DEALER_TABLE, DEALER_ALL_COLUMNS, null, null, null, null, null);
        cursor.moveToFirst();

        int i = 0;
        while (i < cursor.getCount()) {
            DealerData dealer = new DealerData();
            dealer.setId(cursor.getLong(cursor.getColumnIndex(ID)));
            dealer.setDealerId(cursor.getString(cursor.getColumnIndex(DEALER_ID)));
            dealer.setOrganization(cursor.getString(cursor.getColumnIndex(DEALER_ORG)));
            dealer.setUCdid(cursor.getString(cursor.getColumnIndex(DEALER_UCDID)));
            dealer.setmEmail(cursor.getString(cursor.getColumnIndex(DEALER_EMAIL)));
            dealer.setMobileSms(cursor.getString(cursor.getColumnIndex(DEALER_MOBILE_SMS)));
            dealer.setmUsername(cursor.getString(cursor.getColumnIndex(DEALER_USERNAME)));
            dealer.setDealerPassword(cursor.getString(cursor.getColumnIndex(DEALER_PASSWORD)));

            dealers.add(dealer);
            ++i;
            cursor.moveToNext();
        }

        return dealers;
    }

    public long insertVersions(ArrayList<VersionObject> versions) {
        long insertId = -1;
        SQLiteDatabase db = this.getWritableDatabase();

        //  GCLog.e("version data: " + versions.toString());
        try {
            db.beginTransaction();

            for (VersionObject version : versions) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(ID, Integer.parseInt(version.getVersionid()));
                contentValues.put(VERSIONID, version.getVersionid());
                contentValues.put(VERSIONNAME, version.getVersionName());
                contentValues.put(MODELID, version.getModelId());
                contentValues.put(MAKEID, version.getMakeId());
                contentValues.put(TRANSMISSION_TYPE, version.getTransmission());
                contentValues.put(MMV_COLUMN_MAKE_MODEL, version.getMakeModel());
                contentValues.put(MMV_COLUMN_MODEL_VERSION, version.getModelVersion());
                contentValues.put(MMV_COLUMN_MAKE_MODEL_VERSION, version.getMakeModelVersion());
                contentValues.put(MAKENAME, version.getMake());
                contentValues.put(MODELNAME, version.getModel());
                contentValues.put(FUEL_TYPE, version.getFuelType());
                contentValues.put(MODEL_LAUNCH_YEAR, version.getStart_year());
                contentValues.put(MODEL_END_YEAR, version.getEnd_year());
                insertId = db.insertWithOnConflict(VERSION_TABLE, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
            }

            ApplicationController.getEventBus().post(new VersionsInsertEvent(false, "InsertedSuccessfully"));
            db.setTransactionSuccessful();

        } catch (Exception e) {

            final Handler handler = new Handler(new Handler.Callback() {

                @Override
                public boolean handleMessage(Message msg) {
                    if (msg.arg1 == 1) {
                        ApplicationController.getEventBus().post(new VersionsInsertEvent(true, "Error inserting versions in db."));
                    }
                    return false;
                }
            });

            final Message msg = new Message();


            new Thread() {
                public void run() {
                    msg.arg1 = 1;
                    handler.sendMessage(msg);
                }
            }.start();


            GCLog.e("There was some error in inserting versions" + e.getMessage());

        } finally {
            db.endTransaction();
        }

        return insertId;
    }

    public ArrayList<VersionObject> getVersions() {
        ArrayList<VersionObject> versionObjects = new ArrayList<VersionObject>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(VERSION_TABLE, VERSION_COLUMNS, null, null, null, null, null);
        cursor.moveToFirst();

        int i = 0;
        int totalCount = cursor.getCount();

        while (i < totalCount) {
            int id = cursor.getInt(cursor.getColumnIndex(ID));
            String versionId = cursor.getString(cursor.getColumnIndex(VERSIONID));
            String versionName = cursor.getString(cursor.getColumnIndex(VERSIONNAME));
            String modelId = cursor.getString(cursor.getColumnIndex(MODELID));
            String makeId = cursor.getString(cursor.getColumnIndex(MAKEID));
            String fuelType = cursor.getString(cursor.getColumnIndex(FUEL_TYPE));
            String transmissionType = cursor.getString(cursor.getColumnIndex(TRANSMISSION_TYPE));
            String makeModel = cursor.getString(cursor.getColumnIndex(MMV_COLUMN_MAKE_MODEL));
            String modelVersion = cursor.getString(cursor.getColumnIndex(MMV_COLUMN_MODEL_VERSION));
            String makeModelVersion = cursor.getString(cursor.getColumnIndex(MMV_COLUMN_MAKE_MODEL_VERSION));
            String make = cursor.getString(cursor.getColumnIndex(MAKENAME));
            String model = cursor.getString(cursor.getColumnIndex(MODELNAME));

            VersionObject versionObject = new VersionObject();
            versionObject.setId(id);
            versionObject.setVersionid(versionId);
            versionObject.setVersionName(versionName);
            versionObject.setModelId(modelId);
            versionObject.setMakeId(makeId);
            versionObject.setFuelType(fuelType);
            versionObject.setTransmission(transmissionType);
            versionObject.setMakeModel(makeModel);
            versionObject.setModelVersion(modelVersion);
            versionObject.setMakeModelVersion(makeModelVersion);
            versionObject.setMake(make);
            versionObject.setModel(model);
            versionObjects.add(versionObject);
            ++i;
            cursor.moveToNext();

        }
        cursor.close();

        return versionObjects;
    }

    public long insertMakes(ArrayList<MakeObject> makeObjects) {
        long insertId = -1;
        SQLiteDatabase db = this.getWritableDatabase();
        /*String sql = "INSERT INTO " + MAKE_TABLE + " VALUES(?, ?, ?);";
        SQLiteStatement statement = db.compileStatement(sql);*/

        try {
            db.beginTransaction();

            for (MakeObject makeObject : makeObjects) {
                /*statement.clearBindings();
                statement.bindLong(1, Long.parseLong(makeObject.getMakeId()));
                statement.bindString(2, makeObject.getMakeId());
                statement.bindString(3, makeObject.getMake());
                insertId = statement.executeInsert();*/
                ContentValues contentValues = new ContentValues();
                contentValues.put(ID, Integer.parseInt(makeObject.getMakeId()));
                contentValues.put(MAKEID, makeObject.getMakeId());
                contentValues.put(MAKENAME, makeObject.getMake());
                insertId = db.insertWithOnConflict(MAKE_TABLE, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
            }
            db.setTransactionSuccessful();

        } catch (Exception e) {

            GCLog.e("Exception in inserting makes: " + e.getMessage());

            final Handler handler = new Handler(new Handler.Callback() {

                @Override
                public boolean handleMessage(Message msg) {
                    if (msg.arg1 == 1) {
                        ApplicationController.getEventBus().post(new MakeInsertEvent(true, "Error inserting makes in db."));
                    }
                    return false;
                }
            });

            final Message msg = new Message();


            new Thread() {
                public void run() {
                    msg.arg1 = 1;
                    handler.sendMessage(msg);
                }
            }.start();


        } finally {
            db.endTransaction();
        }

        return insertId;
    }

    public ArrayList<MakeObject> getMakes() {
        SQLiteDatabase db = this.getReadableDatabase();

        ArrayList<MakeObject> makes = new ArrayList<MakeObject>();

        Cursor cursor = db.query(MAKE_TABLE, MAKE_COLUMNS, null, null, null, null, null);

        cursor.moveToFirst();
        int i = 0;
        int totalCount = cursor.getCount();

        while (i < totalCount) {
            int id = cursor.getInt(cursor.getColumnIndex(ID));
            String makeid = cursor.getString(cursor.getColumnIndex(MAKEID));
            String makename = cursor.getString(cursor.getColumnIndex(MAKENAME));
            MakeObject makeObject = new MakeObject();
            makeObject.setId(id);
            makeObject.setMakeId(makeid);
            makeObject.setMake(makename);
            makes.add(makeObject);
            ++i;
            cursor.moveToNext();
        }
        cursor.close();

        makes.trimToSize();
        return makes;
    }

    public long insertModels(ArrayList<ModelObject> models) {
        long insertId = -1;
        SQLiteDatabase db = this.getWritableDatabase();
        /*String sql = "INSERT INTO " + MODEL_TABLE + " VALUES(?, ?, ?, ?, ?, ?);";
        SQLiteStatement statement = db.compileStatement(sql);*/

        try {
            db.beginTransaction();

            for (ModelObject model : models) {

                /*statement.clearBindings();
                statement.bindLong(1, Long.parseLong(model.getModelId()));
                statement.bindString(2, model.getModelId());
                statement.bindString(3, model.getModelName());
                statement.bindString(4, model.getMakeId());
                statement.bindString(5, model.getMakename());
                statement.bindString(6, model.getMakeModel());

                insertId = statement.executeInsert();*/
                ContentValues contentValues = new ContentValues();
                contentValues.put(ID, Integer.parseInt(model.getModelId()));
                contentValues.put(MODELID, model.getModelId());
                contentValues.put(MODELNAME, model.getModelName());
                contentValues.put(MAKEID, model.getMakeId());
                contentValues.put(MAKENAME, model.getMakename());
                contentValues.put(MMV_COLUMN_MAKE_MODEL, model.getMakeModel());
                contentValues.put(MODEL_RANK, model.getRank());
                contentValues.put(MODEL_PARENT_ID, model.getParentModelId());
                insertId = db.insertWithOnConflict(MODEL_TABLE, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);

            }
            db.setTransactionSuccessful();

        } catch (Exception e) {

            GCLog.e("Exception: " + e.getMessage());

            final Handler handler = new Handler(new Handler.Callback() {

                @Override
                public boolean handleMessage(Message msg) {
                    if (msg.arg1 == 1) {
                        ApplicationController.getEventBus().post(new ModelsInsertEvent(true, "Error inserting models in db."));
                    }
                    return false;
                }
            });

            final Message msg = new Message();


            new Thread() {
                public void run() {
                    msg.arg1 = 1;
                    handler.sendMessage(msg);
                }
            }.start();


        } finally {
            db.endTransaction();
        }

        return insertId;
    }

    public ArrayList<ModelObject> getModels() {
        ArrayList<ModelObject> models = new ArrayList<ModelObject>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(MODEL_TABLE, MODEL_ALL_COLUMNS, null, null, null, null, null);

        cursor.moveToFirst();
        int i = 0;
        int totalCount = cursor.getCount();

        while (i < totalCount) {
            int id = cursor.getInt(cursor.getColumnIndex(ID));
            String modelId = cursor.getString(cursor.getColumnIndex(MODELID));
            String modelName = cursor.getString(cursor.getColumnIndex(MODELNAME));
            String makeId = cursor.getString(cursor.getColumnIndex(MAKEID));
            String makename = cursor.getString(cursor.getColumnIndex(MAKENAME));
            String makeModel = cursor.getString(cursor.getColumnIndex(MMV_COLUMN_MAKE_MODEL));
            String rank = cursor.getString(cursor.getColumnIndex(MODEL_RANK));
            String parentId = cursor.getString(cursor.getColumnIndex(MODEL_PARENT_ID));

            ModelObject modelObject = new ModelObject();
            modelObject.setId(id);
            modelObject.setModelId(modelId);
            modelObject.setModelName(modelName);
            modelObject.setMakeId(makeId);
            modelObject.setMakename(makename);
            modelObject.setMakeModel(makeModel);
            modelObject.setRank(rank);
            modelObject.setParentModelId(parentId);
            models.add(modelObject);
            ++i;
            cursor.moveToNext();

        }

        cursor.close();
        models.trimToSize();

        return models;
    }

    public long insertRecords(/*ArrayList<MakeObject> makeObjects, ArrayList<ModelObject> modelObjects, */ArrayList<VersionObject> versionObjects) {
        long insertId = -1;
        SQLiteDatabase db = this.getWritableDatabase();

        /*String sql = "INSERT INTO " + MAKE_MODEL_VERSION_TABLE + " VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
        SQLiteStatement statement = db.compileStatement(sql);*/

        try {
            db.beginTransaction();

            /*for (MakeObject makeObject : makeObjects) {
                for (ModelObject modelObject : modelObjects) {*/
            for (VersionObject versionObject : versionObjects) {
                        /*if (versionObject.getModelId().equals(modelObject.getModelId())
                                && modelObject.getMakeId().equals(makeObject.getMakeId())
                                && versionObject.getMakeId().equals(makeObject.getMakeId())) {*/

                ContentValues contentValues = new ContentValues();
                contentValues.put(ID, ++mmvid);
                contentValues.put(MMV_COLUMN_MAKE, versionObject.getMake());
                contentValues.put(MMV_COLUMN_MODEL, versionObject.getModel());
                contentValues.put(MMV_COLUMN_VERSION, versionObject.getVersionName());
                contentValues.put(MAKEID, versionObject.getMakeId());
                contentValues.put(MODELID, versionObject.getModelId());
                contentValues.put(VERSIONID, versionObject.getVersionid());
                contentValues.put(MMV_COLUMN_MAKE_MODEL,
                        versionObject.getMakeModel());
                contentValues.put(MMV_COLUMN_MODEL_VERSION,
                        versionObject.getModelVersion());
                contentValues.put(MMV_COLUMN_MAKE_MODEL_VERSION,
                        versionObject.getMakeModelVersion());
                contentValues.put(MMV_COLUMN_TRANSMISSION_TYPE, versionObject.getTransmission());
                contentValues.put(MMV_COLUMN_FUEL_TYPE, versionObject.getFuelType());
                GCLog.e("values to be inserted = " + contentValues.toString());

                insertId = db.insert(MAKE_MODEL_VERSION_TABLE, null, contentValues);
            }

            //continue;
                    /*}
                }
            }*/

            db.setTransactionSuccessful();

        } catch (Exception e) {

            GCLog.e("Some exception occurred ::: " + e.getMessage());
        } finally {
            db.endTransaction();
        }

        return insertId;
    }

    public ArrayList<VersionObject> getRecords() {
        ArrayList<VersionObject> records = new ArrayList<VersionObject>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(VERSION_TABLE, VERSION_COLUMNS, null, null, null, null, null);
        cursor.moveToFirst();

        int i = 0;
        int totalCount = cursor.getCount();

        while (i < totalCount) {
            VersionObject versionObject = new VersionObject();
            versionObject.setId(cursor.getInt(cursor.getColumnIndex(ID)));
            versionObject.setMakeId(cursor.getString(cursor.getColumnIndex(MAKEID)));
            versionObject.setMake(cursor.getString(cursor.getColumnIndex(MAKENAME)));
            versionObject.setModelId(cursor.getString(cursor.getColumnIndex(MODELID)));
            versionObject.setModel(cursor.getString(cursor.getColumnIndex(MODELNAME)));
            versionObject.setVersionid(cursor.getString(cursor.getColumnIndex(VERSIONID)));
            versionObject.setVersionName(cursor.getString(cursor.getColumnIndex(VERSIONNAME)));
            versionObject.setMakeModel(cursor.getString(cursor.getColumnIndex(MMV_COLUMN_MAKE_MODEL)));
            versionObject.setModelVersion(cursor.getString(cursor.getColumnIndex(MMV_COLUMN_MODEL_VERSION)));
            versionObject.setMakeModelVersion(cursor.getString(cursor.getColumnIndex(MMV_COLUMN_MAKE_MODEL_VERSION)));


            records.add(versionObject);
            ++i;
            cursor.moveToNext();
        }

        records.trimToSize();
        return records;
    }

    public Cursor getMakeModelVersionRecords(CharSequence constraint) {

        Cursor cursor;
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = MMV_COLUMN_MAKE_MODEL_VERSION + " LIKE ? ";
        String[] selectionArgs = new String[]{"%" + constraint + "%"};
        cursor = db.query(VERSION_TABLE, VERSION_COLUMNS, selection, selectionArgs, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                return cursor;
            }
        }

        return null;
    }

    public Cursor getModelRecords(CharSequence constraint, boolean onlyParents) {

        Cursor cursor;

        SQLiteDatabase db = this.getReadableDatabase();
        String selection;


        if (onlyParents) {
            selection = MODELNAME + " LIKE ? " +
                    " AND " +
                    MODEL_PARENT_ID + " = 0";

        } else {
            selection = MODELNAME + " LIKE ? ";

        }

        String[] selectionArgs = new String[]{constraint + "%"};
        String orderBy = "CAST(" + MODEL_RANK + " AS INTEGER)";
        cursor = db.query(MODEL_TABLE, MODEL_ALL_COLUMNS, selection, selectionArgs, null, null, orderBy);
        cursor.moveToFirst();

        if (cursor.getCount() == 0) {

            if (onlyParents) {
                selection = MAKENAME + " LIKE ? " +
                        " AND " +
                        MODEL_PARENT_ID + " = 0";

            } else {
                selection = MAKENAME + " LIKE ? ";

            }

            String[] selectnArgs = new String[]{constraint + "%"};
            orderBy = MODEL_RANK + " ASC";
            cursor = db.query(MODEL_TABLE, MODEL_ALL_COLUMNS, selection, selectnArgs, null, null, orderBy);
        }
        return cursor;
    }


    public Cursor getModelRecords(CharSequence constraint, String makeId, boolean onlyParents) {
        Cursor cursor;
        SQLiteDatabase db = this.getReadableDatabase();
        String selection;


        if (onlyParents) {
            selection = MODELNAME + " LIKE ? " +
                    " AND " +
                    MAKEID + " = ? " +
                    " AND " +
                    MODEL_PARENT_ID + " = 0";

        } else {
            selection = MODELNAME + " LIKE ? " +
                    " AND " +
                    MAKEID + " = ? ";

        }


        String[] selectionArgs = new String[]{
                constraint + "%",
                makeId
        };

        cursor = db.query(MODEL_TABLE, MODEL_ALL_COLUMNS, selection, selectionArgs, null, null, null);
        cursor.moveToFirst();

        return cursor;
    }

    public Cursor getVersionForMakeModel(VersionObject versionObject) {
        Cursor cursor;

        SQLiteDatabase db = this.getReadableDatabase();
        String selection = MAKEID + " = ? AND "
                + MODELID + " = ? AND "
                + FUEL_TYPE + " = ? ";

        String[] selectionArgsPetrol = new String[]{
                versionObject.getMakeId(),
                versionObject.getModelId(),
                "Petrol"
        };

        String[] selectionArgsDiesel = new String[]{
                versionObject.getMakeId(),
                versionObject.getModelId(),
                "Diesel"
        };

        String[] selectionArgsCNG = new String[]{
                versionObject.getMakeId(),
                versionObject.getModelId(),
                "CNG"
        };

        String[] selectionArgsLPG = new String[]{
                versionObject.getMakeId(),
                versionObject.getModelId(),
                "LPG"
        };

        String[] selectionArgsHybrid = new String[]{
                versionObject.getMakeId(),
                versionObject.getModelId(),
                "Hybrid"
        };

        String[] selectionArgsElectric = new String[]{
                versionObject.getMakeId(),
                versionObject.getModelId(),
                "Electric"
        };

        ArrayList<String[]> list = new ArrayList<>();
        list.add(selectionArgsPetrol);
        list.add(selectionArgsDiesel);
        list.add(selectionArgsCNG);
        list.add(selectionArgsLPG);
        list.add(selectionArgsHybrid);
        list.add(selectionArgsElectric);

        list.trimToSize();


        MatrixCursor matrixCursor = new MatrixCursor(VERSION_COLUMNS);

        try {


            for (int j = 0; j < list.size(); ++j) {
                cursor = db.query(VERSION_TABLE, VERSION_COLUMNS, selection, list.get(j), null, null, null);
                cursor.moveToFirst();

                for (int i = 0; i < cursor.getCount(); ++i) {
                    if (i == 0) {
                        matrixCursor.addRow(new String[]{String.valueOf(0), "", cursor.getString(cursor.getColumnIndex(FUEL_TYPE)), "", "", "", "", "", "", "", "", "", "", ""});
                    }
                    matrixCursor.addRow(new String[]{
                            String.valueOf(cursor.getInt(cursor.getColumnIndex(ID))),
                            cursor.getString(cursor.getColumnIndex(VERSIONID)),
                            cursor.getString(cursor.getColumnIndex(VERSIONNAME)),
                            cursor.getString(cursor.getColumnIndex(MODELID)),
                            cursor.getString(cursor.getColumnIndex(MAKEID)),
                            cursor.getString(cursor.getColumnIndex(TRANSMISSION_TYPE)),
                            cursor.getString(cursor.getColumnIndex(FUEL_TYPE)),
                            cursor.getString(cursor.getColumnIndex(MAKENAME)),
                            cursor.getString(cursor.getColumnIndex(MODELNAME)),
                            cursor.getString(cursor.getColumnIndex(MMV_COLUMN_MAKE_MODEL)),
                            cursor.getString(cursor.getColumnIndex(MMV_COLUMN_MODEL_VERSION)),
                            cursor.getString(cursor.getColumnIndex(MMV_COLUMN_MAKE_MODEL_VERSION)),
                            cursor.getString(cursor.getColumnIndex(MODEL_LAUNCH_YEAR)),
                            cursor.getString(cursor.getColumnIndex(MODEL_END_YEAR))

                    });
                    cursor.moveToNext();
                }
            }
        } catch (IllegalArgumentException e) {
            Crashlytics.log(Log.ASSERT, "make-model-version error", "requested make model version: " + versionObject.toString());
            Crashlytics.logException(e);
            Crashlytics.setUserEmail(CommonUtils.getStringSharedPreference(ApplicationController.getInstance(), Constants.UC_DEALER_EMAIL, ""));
        }


        return matrixCursor;
    }


    public Cursor getVersionForMakeModelfromDB(String makeID, String ModelID) {
        Cursor cursor;
/*
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = MAKEID + " = ? AND "
                + MODELID + " = ? ";

        String[] selectionArgs = new String[]{
                makeID,
                ModelID
        };

        //cursor = db.query(VERSION_TABLE, VERSION_COLUMNS, selection, selectionArgs, null, null, null);
        cursor = db.query(VERSION_TABLE, VERSION_COLUMNS, selection, selectionArgs, null, null, FUEL_TYPE + " DESC");*/

        SQLiteDatabase db = this.getReadableDatabase();
        String selection = MAKEID + " = ? AND "
                + MODELID + " = ? AND "
                + FUEL_TYPE + " = ? ";

        String[] selectionArgsPetrol = new String[]{
                makeID,
                ModelID,
                "Petrol"
        };

        String[] selectionArgsDiesel = new String[]{
                makeID,
                ModelID,
                "Diesel"
        };

        String[] selectionArgsCNG = new String[]{
                makeID,
                ModelID,
                "CNG"
        };

        String[] selectionArgsLPG = new String[]{
                makeID,
                ModelID,
                "LPG"
        };

        String[] selectionArgsHybrid = new String[]{
                makeID,
                ModelID,
                "Hybrid"
        };

        String[] selectionArgsElectric = new String[]{
                makeID,
                ModelID,
                "Electric"
        };

        ArrayList<String[]> list = new ArrayList<>();
        list.add(selectionArgsPetrol);
        list.add(selectionArgsDiesel);
        list.add(selectionArgsCNG);
        list.add(selectionArgsLPG);
        list.add(selectionArgsHybrid);
        list.add(selectionArgsElectric);

        list.trimToSize();


        MatrixCursor matrixCursor = new MatrixCursor(VERSION_COLUMNS);

        for (int j = 0; j < list.size(); ++j) {
            cursor = db.query(VERSION_TABLE, VERSION_COLUMNS, selection, list.get(j), null, null, null);
            cursor.moveToFirst();

            for (int i = 0; i < cursor.getCount(); ++i) {
                if (i == 0) {
                    matrixCursor.addRow(new String[]{String.valueOf(0), "", cursor.getString(cursor.getColumnIndex(FUEL_TYPE)), "", "", "", "", "", "", "", "", "", "", ""});
                }
                matrixCursor.addRow(new String[]{
                        String.valueOf(cursor.getInt(cursor.getColumnIndex(ID))),
                        cursor.getString(cursor.getColumnIndex(VERSIONID)),
                        cursor.getString(cursor.getColumnIndex(VERSIONNAME)),
                        cursor.getString(cursor.getColumnIndex(MODELID)),
                        cursor.getString(cursor.getColumnIndex(MAKEID)),
                        cursor.getString(cursor.getColumnIndex(TRANSMISSION_TYPE)),
                        cursor.getString(cursor.getColumnIndex(FUEL_TYPE)),
                        cursor.getString(cursor.getColumnIndex(MAKENAME)),
                        cursor.getString(cursor.getColumnIndex(MODELNAME)),
                        cursor.getString(cursor.getColumnIndex(MMV_COLUMN_MAKE_MODEL)),
                        cursor.getString(cursor.getColumnIndex(MMV_COLUMN_MODEL_VERSION)),
                        cursor.getString(cursor.getColumnIndex(MMV_COLUMN_MAKE_MODEL_VERSION)),
                        cursor.getString(cursor.getColumnIndex(MODEL_LAUNCH_YEAR)),
                        cursor.getString(cursor.getColumnIndex(MODEL_END_YEAR))
                });
                cursor.moveToNext();
            }
            cursor.close();
        }


        return matrixCursor;
    }

    public Cursor getVersionRecordsForMakeModel(CharSequence constraint, VersionObject model) {
        Cursor cursor;

        SQLiteDatabase db = this.getReadableDatabase();
        String selection = VERSIONNAME + " LIKE ? AND "
                + MAKEID + " = ? AND "
                + MODELID + " = ? ";
        String[] selectionArgs = new String[]{
                "%" + constraint + "%",
                model.getMakeId(),
                model.getModelId()
        };
        cursor = db.query(VERSION_TABLE, VERSION_COLUMNS, selection, selectionArgs, null, null, FUEL_TYPE + " DESC");
        return cursor;
    }

    public void removeDealers() {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(DEALER_TABLE, null, null);

    }

    public void removeShowrooms() {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(SHOWROOM_TABLE, null, null);

    }

    public long insertCities(ArrayList<CityData> cityList) {
        long insertId = -1;
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.beginTransaction();
            for (CityData city : cityList) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(ID, ++cid);
                contentValues.put(CITY_ID, city.getCityId());
                contentValues.put(CITY_NAME, city.getCityName());
                contentValues.put(STATE_ID, city.getStateId());
                contentValues.put(REGION_ID, city.getRegionId());
                contentValues.put(ORDER_BY, city.getOrderBy());

                insertId = db.insertWithOnConflict(CITY_TABLE, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);

            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            // TODO send some error event
            GCLog.e("Some problem while inserting city data." + e.getMessage());
        } finally {
            db.endTransaction();
        }

        return insertId;
    }

    public Cursor getCityRecords(CharSequence constraint) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = CITY_NAME + " LIKE ? ";
        String[] selectionArgs = new String[]{
                "%" + constraint + "%",
        };
        String orderBy = "CAST(" + ORDER_BY + " AS INTEGER)";
        Cursor cursor = db.query(CITY_TABLE, CITY_ALL_COLUMNS, selection, selectionArgs, null, null, orderBy);
        cursor.moveToFirst();

        return cursor;
    }

    // method to get cityId on the basis of cityname
    public String getCityId(String cityName) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = CITY_NAME + " LIKE ? ";
        String[] selectionArgs = new String[]{
                "%" + cityName + "%",
        };
        Cursor cursor = db.query(CITY_TABLE, new String[]{CITY_ID}, selection, selectionArgs, null, null, null);
        String cityId = "";
        if (cursor.moveToFirst()) {
            cityId = cursor.getString(cursor.getColumnIndex(MakeModelVersionDB.CITY_ID));
        }
        return cityId;
    }

    public void removeCities() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(CITY_TABLE, null, null);
    }

    public Cursor getMakeRecords(CharSequence constraint) {
        Cursor cursor;

        SQLiteDatabase db = this.getReadableDatabase();

        String selection = MAKENAME + " LIKE ? ";
        String[] selectionArgs = new String[]{
                constraint + "%"
        };

        cursor = db.query(MAKE_TABLE, MAKE_COLUMNS, selection, selectionArgs, null, null, null);
        cursor.moveToFirst();

        return cursor;
    }

}
