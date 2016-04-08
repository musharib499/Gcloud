package com.gcloud.gaadi.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by alokmishra on 23/2/16.
 */
public class GCloudStocksDB extends SQLiteOpenHelper{

    private static final int DB_VERSION = 17;
    private static final String GCloud_DB = "gcloud.db";

    private static final String AVAILABLE_VIEW_STOCK_SQL = "CREATE TABLE "
            + ViewStockModel.AVAILABLE_TABLE + " ("
            + " _id "+" INTEGER PRIMARY KEY AUTOINCREMENT , "
            + ViewStockModel.STOCK_ID + " TEXT NOT NULL , "
            + ViewStockModel.DEALER_PLATFORM + " TEXT  , "
            + ViewStockModel.FINANCE_LIST + " TEXT  , "
            + ViewStockModel.CHANGE_TIME + " TEXT  , "
            + ViewStockModel.CAR_CERTIFICATION + " TEXT , "
            + ViewStockModel.USER_NAME + " TEXT  , "
            + ViewStockModel.MAKE + " TEXT , "
            + ViewStockModel.MAKE_NAME + " TEXT , "
            + ViewStockModel.MODEL_NAME + " TEXT , "
            + ViewStockModel.VERSION_NAME + " TEXT , "
            + ViewStockModel.MODEL_VERSION + " TEXT , "
            + ViewStockModel.KMS + " TEXT , "
            + ViewStockModel.STOCK_PRICE + " TEXT  , "
            + ViewStockModel.COLOR + " TEXT , "
            + ViewStockModel.MM + " TEXT , "
            + ViewStockModel.FUEL_TYPE + " TEXT  , "
            + ViewStockModel.REG_NO + " TEXT , "
            + ViewStockModel.MODEL_YEAR + " TEXT  , "
            + ViewStockModel.MOBILE + " TEXT  , "
            + ViewStockModel.EMAIL + " TEXT  , "
            + ViewStockModel.SHOWROOM_ID + " TEXT , "
            + ViewStockModel.GAADI_ID + " TEXT , "
            + ViewStockModel.CAR_ID + " TEXT  , "
            + ViewStockModel.CREATE_DATE + " TEXT  , "
            + ViewStockModel.SHARE_TEXT + " TEXT , "
            + ViewStockModel.HEX_CODE + " TEXT , "
            + ViewStockModel.TRUST_MARK_CERTIFY + " TEXT  , "
            + ViewStockModel.ACTIVE + " TEXT NOT NULL , "
            + ViewStockModel.DOMAIN + " TEXT , "
            + ViewStockModel.IMAGE_ICON + " TEXT , "
            + ViewStockModel.AREAOFCOVER + " TEXT  , "
            + ViewStockModel.PRICE_SORT + " INTEGER  , "
            + ViewStockModel.KM_SORT + " INTEGER  , "
            + ViewStockModel.INSPECTED_CAR + " TEXT , "
            + ViewStockModel.TOTAL_LEADS + " TEXT , "
            + ViewStockModel.DEALER_PRICE + " TEXT  , "
            + ViewStockModel.UPDATE_TIME + " TEXT NOT NULL , UNIQUE ( " + ViewStockModel.STOCK_ID + ")"
            + ")";


    private static final String FILTER_STOCK_SQL = "CREATE TABLE "
            + ViewStockModel.FILTER_TABLE + " ("
            + " _id "+" INTEGER PRIMARY KEY AUTOINCREMENT , "
            + ViewStockModel.FILTER_ACTIVE + " TEXT , "
            + ViewStockModel.FILTER_INSPECTED + " TEXT , "
            + ViewStockModel.UPDATE_TIME + " TEXT NOT NULL , UNIQUE ( " + "_id" + ")"
            + ")";


    public GCloudStocksDB(Context context){
        super(context, GCloud_DB, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(AVAILABLE_VIEW_STOCK_SQL);
        db.execSQL(FILTER_STOCK_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + ViewStockModel.AVAILABLE_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + ViewStockModel.FILTER_TABLE);
        onCreate(db);
    }
}
