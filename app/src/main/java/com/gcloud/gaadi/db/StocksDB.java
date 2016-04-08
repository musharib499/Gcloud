package com.gcloud.gaadi.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.gcloud.gaadi.model.StockDataModel;
import com.gcloud.gaadi.utils.GCLog;

/**
 * Created by Seema Pandit on 20-03-2015.
 */
public class StocksDB extends SQLiteOpenHelper {
    public static final String DATABASE_FILE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();
    public static final String DATABASE_NAME = "stocks.db";
    public static final String STOCK_TABLE = "stock";
    public static final String ADDSTOCK_ID = "stockId";
    public static final String MAKE = "make";
    public static final String MODEL = "model";
    public static final String MODEL_ID = "modelID";
    public static final String VERSION_NAME = "version";
    public static final String FUEL_TYPE = "fuel_type";
    public static final String ISCNG = "isCng";
    public static final String COLOR = "color";
    public static final String STOCK_YEAR = "stock_year";
    public static final String STOCK_MONTH = "stock_month";
    public static final String MODEL_LAUNCH_YEAR = "startYear";
    public static final String MODEL_END_YEAR = "endYear";
    public static final String KM = "km";
    public static final String OWNERS = "owners";
    public static final String REG_CITY = "reg_city";
    public static final String REG_NO = "reg_no";
    public static final String DEALERSHIP = "dealership";
    public static final String STOCKPRICE = "stockprice";
    public static final String INSURANCE_TYPE = "insurancetype";
    public static final String INSURANCE_YEAR = "insurance_year";
    public static final String INSURANCE_MONTH = "insurance_month";
    public static final String TAX = "tax";
    public static final String SELLTODEALER = "sell_to_dealer";
    public static final String DEALERMOBILE_NUM = "dealermobile";
    public static final String DEALER_PRICE = "dealerprice";
    public static final String COLOR_HEXCODE = "hexcode";
    public static final String MAKE_NAME = "makeName";
    public static final String VERSIONID = "versionId";
    public static final String TRANSMISSION = "transmission";
    public static final String EvenOdd = "odd_even";
    public static final String[] ALL_COLUMNS = new String[]{
            ADDSTOCK_ID,
            MAKE,
            MODEL,
            VERSION_NAME,
            MODEL_ID,
            FUEL_TYPE,
            ISCNG,
            MAKE_NAME,
            TRANSMISSION,
            VERSIONID,
            COLOR,
            COLOR_HEXCODE,
            STOCK_YEAR,
            STOCK_MONTH,
            KM,
            OWNERS,
            REG_CITY,
            REG_NO,
            DEALERSHIP,
            STOCKPRICE,
            INSURANCE_TYPE,
            INSURANCE_MONTH,
            INSURANCE_YEAR,
            TAX,
            SELLTODEALER,
            DEALERMOBILE_NUM,
            DEALER_PRICE,
            MODEL_LAUNCH_YEAR,
            MODEL_END_YEAR,
            EvenOdd
    };
    private static final int VERSION = 4;
    private static final String STOCK_SQL = "CREATE TABLE "
            + STOCK_TABLE + " ("
            + MAKE + " TEXT NOT NULL, "
            + ADDSTOCK_ID + " INTEGER NOT NULL, "
            + MODEL + " TEXT NOT NULL, "
            + VERSION_NAME + " TEXT NOT NULL, "
            + FUEL_TYPE + " TEXT NOT NULL, "
            + ISCNG + " TEXT NOT NULL, "
            + MODEL_ID + " TEXT NOT NULL, "
            + COLOR + " TEXT NOT NULL, "
            + STOCK_MONTH + " TEXT NOT NULL, "
            + MAKE_NAME + " TEXT NOT NULL, "
            + VERSIONID + " TEXT NOT NULL, "
            + TRANSMISSION + " TEXT NOT NULL, "
            + STOCK_YEAR + " TEXT NOT NULL, "
            + KM + " TEXT NOT NULL, "
            + OWNERS + " TEXT NOT NULL, "
            + REG_CITY + " TEXT NOT NULL, "
            + REG_NO + " TEXT NOT NULL, "
            + DEALERSHIP + " TEXT NOT NULL, "
            + STOCKPRICE + " TEXT NOT NULL, "
            + INSURANCE_TYPE + " TEXT NOT NULL, "
            + COLOR_HEXCODE + " TEXT NOT NULL, "
            + INSURANCE_MONTH + " TEXT NOT NULL, "
            + INSURANCE_YEAR + " TEXT NOT NULL, "
            + TAX + " TEXT NOT NULL, "
            + SELLTODEALER + " TEXT NOT NULL, "
            + DEALERMOBILE_NUM + " TEXT NOT NULL, "
            + MODEL_LAUNCH_YEAR + " TEXT NOT NULL, "
            + MODEL_END_YEAR + " TEXT NOT NULL, "
            + EvenOdd + " TEXT NOT NULL,"
            + DEALER_PRICE + " TEXT NOT NULL, PRIMARY KEY (" + ADDSTOCK_ID + "))";
    private Context mContext;


    public StocksDB(Context context) {
        super(context, context.getDatabasePath(DATABASE_NAME).getPath(), null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(STOCK_SQL);
    }


    public void deletePreviousData() {
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "delete from " + STOCK_TABLE;
        db.execSQL(sql);
        //GCLog.e("delete"+ "delete", "deleted");
    }

    public int getStocksCount() {
        String countQuery = "SELECT  * FROM " + STOCK_TABLE;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int cnt = cursor.getCount();
        cursor.close();
        GCLog.e("" + cnt);
        return cnt;


    }


    public long saveStockData(StockDataModel mStockDataModel) {
        long insertId = -1;
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.beginTransaction();
            ContentValues contentValues = new ContentValues();
            contentValues.put(ADDSTOCK_ID, mStockDataModel.getAddStockId());
            contentValues.put(MAKE, mStockDataModel.getMake());
            contentValues.put(MODEL, mStockDataModel.getModel());
            contentValues.put(VERSION_NAME, mStockDataModel.getVersion());
            contentValues.put(MODEL_ID, mStockDataModel.getModelID());

            contentValues.put(MAKE_NAME, mStockDataModel.getMakeName());
            contentValues.put(VERSIONID, mStockDataModel.getVersionId());
            contentValues.put(TRANSMISSION, mStockDataModel.getTransmission());
            contentValues.put(COLOR_HEXCODE, mStockDataModel.getHexcode());

            contentValues.put(FUEL_TYPE, mStockDataModel.getFuelType());
            contentValues.put(ISCNG, mStockDataModel.getIsCng());
            contentValues.put(COLOR, mStockDataModel.getColor());
            contentValues.put(STOCK_MONTH, mStockDataModel.getStockMonth());
            contentValues.put(STOCK_YEAR, mStockDataModel.getStockYear());
            contentValues.put(KM, mStockDataModel.getKm());
            contentValues.put(OWNERS, mStockDataModel.getOwners());
            contentValues.put(REG_CITY, mStockDataModel.getReg_city());
            contentValues.put(REG_NO, mStockDataModel.getReg_no());
            contentValues.put(DEALERSHIP, mStockDataModel.getDealership());
            contentValues.put(STOCKPRICE, mStockDataModel.getStockprice());
            contentValues.put(INSURANCE_TYPE, mStockDataModel.getInsurancetype());
            contentValues.put(INSURANCE_MONTH, mStockDataModel.getInsuranceMonth());
            contentValues.put(INSURANCE_YEAR, mStockDataModel.getInsuranceYear());
            contentValues.put(TAX, mStockDataModel.getTax());
            contentValues.put(SELLTODEALER, mStockDataModel.getSell_to_dealer());
            contentValues.put(DEALERMOBILE_NUM, mStockDataModel.getDealermobile());
            contentValues.put(DEALER_PRICE, mStockDataModel.getDealerprice());
            contentValues.put(MODEL_LAUNCH_YEAR, mStockDataModel.getStartYear());
            contentValues.put(MODEL_END_YEAR, mStockDataModel.getEndYear());
            contentValues.put(EvenOdd, mStockDataModel.getEvenOdd());

            // delete previous data from table
            deletePreviousData();

            // insert new data into table
            insertId = db.insertWithOnConflict(STOCK_TABLE, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);

            // set transaction successful
            db.setTransactionSuccessful();

        } catch (Exception e) {
            Crashlytics.log(Log.ERROR, "Exception in inserting new values for add stock", e.getMessage());
            GCLog.e("Exception in inserting new values for add stock: " + e.getMessage());
        } finally {
            db.endTransaction();
        }

        return insertId;
    }


    public StockDataModel getStocksData() {
        SQLiteDatabase db = this.getReadableDatabase();

        StockDataModel mStockDataModel = new StockDataModel();

        Cursor cursor = db.query(STOCK_TABLE, ALL_COLUMNS, null, null, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();

            mStockDataModel.setAddStockId(cursor.getInt(cursor.getColumnIndex(ADDSTOCK_ID)));
            mStockDataModel.setMake(cursor.getString(cursor.getColumnIndex(MAKE)));
            mStockDataModel.setModel(cursor.getString(cursor.getColumnIndex(MODEL)));
            mStockDataModel.setVersion(cursor.getString(cursor.getColumnIndex(VERSION_NAME)));
            mStockDataModel.setFuelType(cursor.getString(cursor.getColumnIndex(FUEL_TYPE)));
            mStockDataModel.setIsCng(cursor.getString(cursor.getColumnIndex(ISCNG)));
            mStockDataModel.setModelID(cursor.getString(cursor.getColumnIndex(MODEL_ID)));
            mStockDataModel.setHexcode(cursor.getString(cursor.getColumnIndex(COLOR_HEXCODE)));
            mStockDataModel.setVersionId(cursor.getString(cursor.getColumnIndex(VERSIONID)));
            mStockDataModel.setMakeName(cursor.getString(cursor.getColumnIndex(MAKE_NAME)));
            mStockDataModel.setTransmission(cursor.getString(cursor.getColumnIndex(TRANSMISSION)));

            mStockDataModel.setColor(cursor.getString(cursor.getColumnIndex(COLOR)));
            mStockDataModel.setStockMonth(cursor.getString(cursor.getColumnIndex(STOCK_MONTH)));
            mStockDataModel.setStockYear(cursor.getString(cursor.getColumnIndex(STOCK_YEAR)));
            mStockDataModel.setKm(cursor.getString(cursor.getColumnIndex(KM)));
            mStockDataModel.setOwners(cursor.getString(cursor.getColumnIndex(OWNERS)));
            mStockDataModel.setReg_city(cursor.getString(cursor.getColumnIndex(REG_CITY)));
            mStockDataModel.setReg_no(cursor.getString(cursor.getColumnIndex(REG_NO)));
            mStockDataModel.setDealership(cursor.getString(cursor.getColumnIndex(DEALERSHIP)));
            mStockDataModel.setStockprice(cursor.getString(cursor.getColumnIndex(STOCKPRICE)));
            mStockDataModel.setInsurancetype(cursor.getString(cursor.getColumnIndex(INSURANCE_TYPE)));
            mStockDataModel.setInsuranceMonth(cursor.getString(cursor.getColumnIndex(INSURANCE_MONTH)));
            mStockDataModel.setInsuranceYear(cursor.getString(cursor.getColumnIndex(INSURANCE_YEAR)));
            mStockDataModel.setTax(cursor.getString(cursor.getColumnIndex(TAX)));
            mStockDataModel.setSell_to_dealer(cursor.getString(cursor.getColumnIndex(SELLTODEALER)));
            mStockDataModel.setDealermobile(cursor.getString(cursor.getColumnIndex(DEALERMOBILE_NUM)));
            mStockDataModel.setDealerprice(cursor.getString(cursor.getColumnIndex(DEALER_PRICE)));
            mStockDataModel.setStartYear(cursor.getString(cursor.getColumnIndex(MODEL_LAUNCH_YEAR)));
            mStockDataModel.setEndYear(cursor.getString(cursor.getColumnIndex(MODEL_END_YEAR)));
            mStockDataModel.setEvenOdd(cursor.getString(cursor.getColumnIndex(EvenOdd)));

        }
        cursor.close();

        return mStockDataModel;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + STOCK_TABLE);
        // db.execSQL("ALTER TABLE " + STOCK_TABLE + " ADD "+ COLOR_HEXCODE + " TEXT ");

        onCreate(db);

    }
}
