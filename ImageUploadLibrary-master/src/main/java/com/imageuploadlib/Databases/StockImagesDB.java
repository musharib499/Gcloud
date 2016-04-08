package com.imageuploadlib.Databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import com.imageuploadlib.Model.StockImageData;
import com.imageuploadlib.Model.StockImageOrderData;

import java.util.ArrayList;

/**
 * Created by Seema Pandit on 27-03-2015.
 */
public class StockImagesDB extends SQLiteOpenHelper {

    public static final String DATABASE_FILE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();
    public static final String DATABASE_NAME = "stockimages.db";
    public static final String STOCKIMAGES_TABLE = "stockimages";
    public static final String ID = "_id";
    public static final String CAR_ID = "carid";
    public static final String IMAGE_PATH = "imagepath";
    public static final String IMAGE_UPLOAD_STATUS = "imageuploadstatus";
    public static final String IMAGE_SOURCE = "imagesource";
    public static final String TAG = "tag";
    public static final String DEALER_ID = "dealer_id";
    public static final String IMAGE_REQUEST_NAME = "requestname";
    public static final String IMAGE_RESPONSE_NAME = "responsename";
    public static final String MAKE_MODEL_VERSION = "mmv";
    public static final String CURRENT_TIMESTAMP = "currenttimestamp";
    public static final String STOCKIMAGESORDER_TABLE = "stockimagesorder";
    public static final String ID_IMAGEORDER = "id";
    public static final String CAR_ID_IMAGEORDER = "carid";
    public static final String IMAGE_ORDER = "imageorder";
    public static final String CURRENT_TIMESTAMP_IMAGEORDER = "currenttimestamp";
    private static final String MAP_ORDER = "mapOrder";
    private static final int VERSION = 2;
    private static final String STOCKIMAGES_SQL = "CREATE TABLE "
            + STOCKIMAGES_TABLE + " ("
            + ID + " INTEGER NOT NULL, "
            + CAR_ID + " TEXT NOT NULL, "
            + IMAGE_PATH + " TEXT NOT NULL, "
            + IMAGE_UPLOAD_STATUS + " TEXT NOT NULL, "
            + IMAGE_SOURCE + " TEXT NOT NULL, "
            + TAG + " TEXT , "
            + DEALER_ID + " INTEGER , "
            + IMAGE_REQUEST_NAME + " TEXT , "
            + IMAGE_RESPONSE_NAME + " TEXT , "
            + MAKE_MODEL_VERSION + " TEXT NOT NULL, "
            + CURRENT_TIMESTAMP + " TEXT NOT NULL, PRIMARY KEY (" + ID + "))";
    private static final String STOCKIMAGESORDER_SQL = "CREATE TABLE "
            + STOCKIMAGESORDER_TABLE + " ("
            + ID_IMAGEORDER + " INTEGER NOT NULL, "
            + CAR_ID_IMAGEORDER + " TEXT NOT NULL, "
            + IMAGE_ORDER + " TEXT NOT NULL, "
            + MAP_ORDER + " TEXT,"
            + CURRENT_TIMESTAMP_IMAGEORDER + " TEXT NOT NULL, PRIMARY KEY (" + ID_IMAGEORDER + "))";
    private static long sid = -1;
    private static long oid = -1;
    private static String[] ALL_COLUMNS_STOCKIMAGES_TABLE = new String[]

            {ID, CAR_ID, IMAGE_PATH,
                    IMAGE_UPLOAD_STATUS, IMAGE_SOURCE,
                    TAG, DEALER_ID, IMAGE_REQUEST_NAME, IMAGE_RESPONSE_NAME,
                    MAKE_MODEL_VERSION, CURRENT_TIMESTAMP};
    private static String[] ALL_COLUMNS_STOCKIMAGESORDER_TABLE = new String[]

            {ID_IMAGEORDER, CAR_ID_IMAGEORDER, IMAGE_ORDER, MAP_ORDER, CURRENT_TIMESTAMP_IMAGEORDER};
    private Context mContext;

    public StockImagesDB(Context context) {
        super(context, context.getDatabasePath(DATABASE_NAME).getPath(), null, VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(STOCKIMAGES_SQL);
        db.execSQL(STOCKIMAGESORDER_SQL);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i2) {
        db.execSQL("DROP TABLE IF EXISTS " + STOCKIMAGES_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + STOCKIMAGESORDER_TABLE);
        onCreate(db);
    }

    public long insertStockImagesRecords(StockImageData stockImagesData) {
        long insertId = -1;
        SQLiteDatabase db = this.getWritableDatabase();

        try {
            db.beginTransaction();

            ContentValues contentValues = new ContentValues();
            contentValues.put(ID, ++sid);
            contentValues.put(CAR_ID, stockImagesData.getCarId());
            contentValues.put(IMAGE_PATH, stockImagesData.getImagePath());
            contentValues.put(IMAGE_UPLOAD_STATUS, stockImagesData.getImageUploadStatus() + "");
            contentValues.put(IMAGE_SOURCE, stockImagesData.getImageSource() + "");
            contentValues.put(TAG, stockImagesData.getTag() + "");
            contentValues.put(DEALER_ID, stockImagesData.getDealerId());
            contentValues.put(IMAGE_REQUEST_NAME, stockImagesData.getRequestImageName() + "");
            contentValues.put(IMAGE_RESPONSE_NAME, stockImagesData.getResponseImageName() + "");
            contentValues.put(MAKE_MODEL_VERSION, stockImagesData.getMakeModelVersion() + "");
            contentValues.put(CURRENT_TIMESTAMP, stockImagesData.getCurrentTimeStamp());
            insertId = db.insertWithOnConflict(STOCKIMAGES_TABLE, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);

            db.setTransactionSuccessful();
            Log.i("*inserted*", "insertStockImagesRecords");
        } catch (Exception e) {
            e.printStackTrace();
        }
        db.endTransaction();
        return insertId;
    }

    public long insertStockImagesOrder(StockImageOrderData stockImagesorderData) {
        long insertId = -1;
        SQLiteDatabase db = this.getWritableDatabase();

        try {
            db.beginTransaction();
            Log.e(TAG, stockImagesorderData.toString());
            ContentValues contentValues = new ContentValues();
            contentValues.put(ID_IMAGEORDER, ++oid);
            contentValues.put(CAR_ID_IMAGEORDER, stockImagesorderData.getCarID());
            contentValues.put(IMAGE_ORDER, stockImagesorderData.getImageUploadOrder());
            contentValues.put(MAP_ORDER, stockImagesorderData.getMapOrder());
            contentValues.put(CURRENT_TIMESTAMP_IMAGEORDER, System.currentTimeMillis());
            insertId = db.insertWithOnConflict(STOCKIMAGESORDER_TABLE, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);

            db.setTransactionSuccessful();
            Log.e("*inserted*", "insertStockImagesOrder");
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        db.endTransaction();
        return insertId;
    }

    public ArrayList<StockImageData> getStockImageDataById(String carID, String status) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = CAR_ID + " = ? AND " + IMAGE_UPLOAD_STATUS + "= ?";
        String[] selectionArgs = new String[]{carID, status};
        ArrayList<StockImageData> stockImageDatas = new ArrayList<>();
        Cursor cursor = db.query(STOCKIMAGES_TABLE, ALL_COLUMNS_STOCKIMAGES_TABLE, selection, selectionArgs, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                StockImageData mStockImageData = new StockImageData();
                mStockImageData.setId(cursor.getInt(cursor.getColumnIndex(ID)));
                mStockImageData.setCarId(cursor.getString(cursor.getColumnIndex(CAR_ID)));
                mStockImageData.setImagePath(cursor.getString(cursor.getColumnIndex(IMAGE_PATH)));
                mStockImageData.setImageUploadStatus(cursor.getString(cursor.getColumnIndex(IMAGE_UPLOAD_STATUS)));
                mStockImageData.setImageSource(cursor.getString(cursor.getColumnIndex(IMAGE_SOURCE)));
                mStockImageData.setDealerId(cursor.getInt(cursor.getColumnIndex(DEALER_ID)));
                mStockImageData.setTag(cursor.getString(cursor.getColumnIndex(TAG)));
                mStockImageData.setRequestImageName(cursor.getString(cursor.getColumnIndex(IMAGE_REQUEST_NAME)));
                mStockImageData.setResponseImageName(cursor.getString(cursor.getColumnIndex(IMAGE_RESPONSE_NAME)));
                mStockImageData.setMakeModelVersion(cursor.getString(cursor.getColumnIndex(MAKE_MODEL_VERSION)));
                mStockImageData.setCurrentTimeStamp(cursor.getString(cursor.getColumnIndex(CURRENT_TIMESTAMP)));
                stockImageDatas.add(mStockImageData);
                cursor.moveToNext();
            }
            cursor.close();
        }
        return stockImageDatas;
    }

    public StockImageOrderData getStockImageOrderDataById(String carID_imageOrder) {
        StockImageOrderData mStockImageOrderDataData = new StockImageOrderData();
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = CAR_ID_IMAGEORDER + " = ?";
        String[] selectionArgs = new String[]{carID_imageOrder};
        Cursor cursor = db.query(STOCKIMAGESORDER_TABLE, ALL_COLUMNS_STOCKIMAGESORDER_TABLE, selection, selectionArgs, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            if (cursor.getCount() > 0) {
                mStockImageOrderDataData.setId(cursor.getInt(cursor.getColumnIndex(ID_IMAGEORDER)));
                mStockImageOrderDataData.setCarID(cursor.getString(cursor.getColumnIndex(CAR_ID_IMAGEORDER)));
                mStockImageOrderDataData.setImageUploadOrder(cursor.getString(cursor.getColumnIndex(IMAGE_ORDER)));
                mStockImageOrderDataData.setMapOrder(cursor.getString(cursor.getColumnIndex(MAP_ORDER)));
                mStockImageOrderDataData.setCurrentTimeStamp(cursor.getString(cursor.getColumnIndex(CURRENT_TIMESTAMP_IMAGEORDER)));
            }
            cursor.close();
        }
        return mStockImageOrderDataData;
    }

    public void deleteStockImageData(String carID) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = CAR_ID + " = ?";
        String[] selectionArgs = new String[]{carID};
        db.delete(STOCKIMAGES_TABLE, selection, selectionArgs);
    }

    public void deleteStockImageOrderData(String imageOrder_carID) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = CAR_ID_IMAGEORDER + " = ?";
        String[] selectionArgs = new String[]{imageOrder_carID};
        db.delete(STOCKIMAGESORDER_TABLE, selection, selectionArgs);
    }


    public void updateStockImageData(String requestImageName, String responseImageName, String updatedStatus, String carID) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = CAR_ID + " = ? AND " + IMAGE_REQUEST_NAME + " = ?";
        String[] selectionArgs = new String[]{carID, requestImageName};
        ContentValues dataToUpdate = new ContentValues();
        dataToUpdate.put(IMAGE_RESPONSE_NAME, responseImageName);
        dataToUpdate.put(CURRENT_TIMESTAMP, System.currentTimeMillis());
        dataToUpdate.put(IMAGE_UPLOAD_STATUS, updatedStatus);
        db.update(STOCKIMAGES_TABLE, dataToUpdate, selection, selectionArgs);
    }

    public void updateStockImageRequestName(String requestName, String carID, String imagePath) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = CAR_ID + " = ? AND " + IMAGE_PATH + " = ?";
        String[] selectionArgs = new String[]{carID, imagePath};
        ContentValues dataToUpdate = new ContentValues();
        dataToUpdate.put(IMAGE_REQUEST_NAME, requestName);
        dataToUpdate.put(CURRENT_TIMESTAMP, System.currentTimeMillis());
        db.update(STOCKIMAGES_TABLE, dataToUpdate, selection, selectionArgs);
    }

    public void updateStockImageOrderData(String updatedOrder, String imageOrder_carID) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = CAR_ID_IMAGEORDER + " = ?";
        String[] selectionArgs = new String[]{imageOrder_carID};
        ContentValues dataToUpdate = new ContentValues();
        dataToUpdate.put(IMAGE_ORDER, updatedOrder);
//        dataToUpdate.put(MAP_ORDER , mapOrder);
        dataToUpdate.put(CURRENT_TIMESTAMP_IMAGEORDER, System.currentTimeMillis());
        db.update(STOCKIMAGESORDER_TABLE, dataToUpdate, selection, selectionArgs);
    }

    public void updateStockMapOrderData(String mapOrder, String imageOrder_carID) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = CAR_ID_IMAGEORDER + " = ?";
        String[] selectionArgs = new String[]{imageOrder_carID};
        ContentValues dataToUpdate = new ContentValues();
        dataToUpdate.put(MAP_ORDER, mapOrder);
//        dataToUpdate.put(MAP_ORDER , mapOrder);
        dataToUpdate.put(CURRENT_TIMESTAMP_IMAGEORDER, System.currentTimeMillis());
        db.update(STOCKIMAGESORDER_TABLE, dataToUpdate, selection, selectionArgs);
    }

    public ArrayList<StockImageData> getPendingImages() {
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = IMAGE_UPLOAD_STATUS + "=?";
        String[] selectionArgs = new String[]{"S"};
        Cursor cursor = db.query(STOCKIMAGES_TABLE, ALL_COLUMNS_STOCKIMAGES_TABLE, selection, selectionArgs, null, null, null);
        ArrayList<StockImageData> stockImageDatas = new ArrayList<>();
        if (cursor != null) {
            cursor.moveToFirst();
            int size = cursor.getCount();
            for (int i = 0; i < size; i++) {
                StockImageData mStockImageData = new StockImageData();
                mStockImageData.setId(cursor.getInt(cursor.getColumnIndex(ID)));
                mStockImageData.setCarId(cursor.getString(cursor.getColumnIndex(CAR_ID)));
                mStockImageData.setImagePath(cursor.getString(cursor.getColumnIndex(IMAGE_PATH)));
                mStockImageData.setImageUploadStatus(cursor.getString(cursor.getColumnIndex(IMAGE_UPLOAD_STATUS)));
                mStockImageData.setImageSource(cursor.getString(cursor.getColumnIndex(IMAGE_SOURCE)));
                mStockImageData.setTag(cursor.getString(cursor.getColumnIndex(TAG)));
                mStockImageData.setDealerId(cursor.getInt(cursor.getColumnIndex(DEALER_ID)));
                mStockImageData.setRequestImageName(cursor.getString(cursor.getColumnIndex(IMAGE_REQUEST_NAME)));
                mStockImageData.setResponseImageName(cursor.getString(cursor.getColumnIndex(IMAGE_RESPONSE_NAME)));
                mStockImageData.setMakeModelVersion(cursor.getString(cursor.getColumnIndex(MAKE_MODEL_VERSION)));
                mStockImageData.setCurrentTimeStamp(cursor.getString(cursor.getColumnIndex(CURRENT_TIMESTAMP)));
                stockImageDatas.add(mStockImageData);
                cursor.moveToNext();
            }
            cursor.close();
        }
        return stockImageDatas;
    }
}
