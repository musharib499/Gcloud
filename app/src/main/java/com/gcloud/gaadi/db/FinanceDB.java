package com.gcloud.gaadi.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabaseCorruptException;
import android.database.sqlite.SQLiteOpenHelper;

import com.crashlytics.android.Crashlytics;
import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.model.DocumentCategories;
import com.gcloud.gaadi.model.DocumentInfo;
import com.gcloud.gaadi.model.Finance.BankData;
import com.gcloud.gaadi.model.Finance.CompaniesData;
import com.gcloud.gaadi.model.Finance.EmploymentData;
import com.gcloud.gaadi.model.FinanceData;
import com.gcloud.gaadi.utils.CommonUtils;
import com.gcloud.gaadi.utils.GCLog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Lakshay on 25-05-2015.
 */
public class FinanceDB extends SQLiteOpenHelper {

    public static final int FINANCE_VERSION = 24;
    public static final String TAG_ID_TYPE = "tag_id_type";
    public static final String ISSUE_FINANCE_TABLE = "issue_finance";
    public static final String BANK_TABLE = "bank";
    public static final String EMPLOYMENT_TABLE = "employment";
    public static final String INDUSTRIES_TABLE = "industries";
    public static final String BANK_ID = "bank_id";
    public static final String BANK_NAME = "bank_name";
    public static final String UPDATED_TIMESTAMP = "updated_timestamp";

    public static final String EMPLOYMENT_ID = "employment_id";
    public static final String EMPLOYMENT_CAT = "employment_cat";
    public static final String TYPE_VALUES = "type_values";
    public static final String TYPE = "type";

    public static final String ID = "id";
    public static final String AUTOINCREMENT_ID = "_id";
    public static final String DATABASE_NAME = "finance.db";
    public static final String CAR_ID = "carid";
    public static final String IMAGE_PATH = "image_path";
    public static final String IMAGE_UPLOAD_STATUS = "status";
    public static final String TAG = "tag_id";
    public static final String IMAGE_REQUEST_NAME = "request_name";
    public static final String IMAGE_RESPONSE_NAME = "response_name";
    public static final String DOC_NAME = "doc_name";
    public static final String DOC_TAG_TABLE = "doc_tags";
    public static final String PARENT_ID = "parent_id";
    public static final String CURRENT_TIMESTAMP = "timestamp";
    public static final String[] ALL_COLUMNS_STOCKIMAGES_TABLE = new String[]{ID, CAR_ID, IMAGE_PATH,
            IMAGE_UPLOAD_STATUS, TAG, IMAGE_REQUEST_NAME, IMAGE_RESPONSE_NAME, CURRENT_TIMESTAMP};
    public static final String PARENT_CAT_NAME = "parent_category";
    public static final String[] ALL_COLUMNS_DOC_TAG = new String[]{TAG, DOC_NAME, PARENT_ID, PARENT_CAT_NAME};
    public static final String COMPANIES_TABLE = "companies";
    public static final String COMPANY_ID = "company_id";
    public static final String COMPANY_CATEGORY = "category";
    public static final String COMPANY_NAME = "company_name";
    public static final String COMPANY_TYPE = "type";
    public static final String UPDATED_TIME = "updated_time";
    public static final String APPLICATION_ID = "application_id";
    private static final String TOTAL_IMAGES = "total_images";
    private static final String UPLOADED_IMAGES = "uploaded_images";
    private static final String UPLOADING_SEQUENCE = "uploading_sequence";
    public static int sid = 0;
    public static int did = 0;
    public static int aid = 0;
    public static int bid = 0;
    public static int eid = 0;
    public static int cid = 0;
    public static int iid = 0;
    public final String YEAR = "year";
    public final String MAKE = "make";
    public final String MODEL = "model";
    public final String COLOR = "color";
    public final String KM_DRIVEN = "km_driven";
    public final String STOCK_PRICE = "stock_price";
    public final String[] ALL_COLUMNS_LOAN_TABLE = new String[]{ID, YEAR, MAKE, MODEL, KM_DRIVEN,
            COLOR, APPLICATION_ID, STOCK_PRICE, CURRENT_TIMESTAMP};
    public final String CUSTOMAR_ID = "customar_id";
    public final String PENDING_APPLICATION_TABLE = "Pending_applications";
    public final String IMAGE_UPLOAD_COUNT_TABLE = "image_upload_count";
    public final String IMAGE_REUPLOAD_COUNT_TABLE = "image_reupload_count";

    public FinanceDB(Context context) {
        super(context, context.getDatabasePath(DATABASE_NAME).getPath(), null, FINANCE_VERSION);
    }

    //To get the main categories of documents
    public static ArrayList<String> getCategories() {
        SQLiteDatabase db = com.gcloud.gaadi.ApplicationController.getFinanceDB().getReadableDatabase();
        ArrayList<String> categories = new ArrayList<>();
        db.beginTransaction();
        Cursor cursor = db.query(DOC_TAG_TABLE, new String[]{PARENT_CAT_NAME}, null, null, PARENT_CAT_NAME, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                categories.add(cursor.getString(cursor.getColumnIndex(PARENT_CAT_NAME)));
                cursor.moveToNext();
            }
            cursor.close();
        }
        db.endTransaction();
        return categories;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String ISSUE_FINANCE = "CREATE TABLE IF NOT EXISTS "
                + ISSUE_FINANCE_TABLE + " ("
                + ID + " INTEGER NOT NULL, "
                + CAR_ID + " TEXT NOT NULL, "
                + APPLICATION_ID + " TEXT NOT NULL , "
                + CUSTOMAR_ID + " TEXT NOT NULL , "
                + IMAGE_PATH + " TEXT NOT NULL, "
                + IMAGE_UPLOAD_STATUS + " TEXT NOT NULL, "
                + TAG + " TEXT NOT NULL, "
                + TAG_ID_TYPE + " TEXT NOT NULL, "
                + IMAGE_REQUEST_NAME + " TEXT NOT NULL, "
                + UPLOADING_SEQUENCE + " INTEGER NOT NULL, "
                + TOTAL_IMAGES + " INTEGER NOT NULL, "
                + CURRENT_TIMESTAMP + " TEXT NOT NULL, PRIMARY KEY (" + ID + ") , UNIQUE (" + IMAGE_REQUEST_NAME + ") ON CONFLICT REPLACE)";

        db.execSQL(ISSUE_FINANCE);

        final String DOC_CATEGORY_TAG = "CREATE TABLE IF NOT EXISTS "
                + DOC_TAG_TABLE + " ("
                + ID + " INTEGER NOT NULL, "
                + TAG + " TEXT NOT NULL, "
                + DOC_NAME + " TEXT NOT NULL, "
                + PARENT_ID + " INTEGER NOT NULL , "
                + PARENT_CAT_NAME + " TEXT , "
                + CURRENT_TIMESTAMP + " TEXT NOT NULL, PRIMARY KEY (" + ID + "))";

        db.execSQL(DOC_CATEGORY_TAG);


        final String PENDING_APPLICATIONS = "CREATE TABLE IF NOT EXISTS "
                + PENDING_APPLICATION_TABLE + " ("
                + ID + " INTEGER PRIMARY KEY AUTOINCREMENT , "
                + APPLICATION_ID + " TEXT NOT NULL)";

        db.execSQL(PENDING_APPLICATIONS);

        final String COMPANIES = "CREATE TABLE IF NOT EXISTS "
                + COMPANIES_TABLE + " ("
                + AUTOINCREMENT_ID + " INTEGER NOT NULL, "
                + COMPANY_CATEGORY + " TEXT NOT NULL, "
                + COMPANY_NAME + " TEXT NOT NULL, "
                + COMPANY_TYPE + " TEXT NOT NULL, "
                + UPDATED_TIME + " TEXT NOT NULL, "
                + COMPANY_ID + " TEXT NOT NULL, PRIMARY KEY (" + AUTOINCREMENT_ID + " ) , UNIQUE (" + COMPANY_ID + ") ON CONFLICT REPLACE)";

        db.execSQL(COMPANIES);

        final String BANK = "CREATE TABLE IF NOT EXISTS "
                + BANK_TABLE + " ("
                + AUTOINCREMENT_ID + " INTEGER PRIMARY KEY , "
                + BANK_ID + " TEXT NOT NULL , "
                + BANK_NAME + " TEXT NOT NULL, "
                + UPDATED_TIMESTAMP + " TEXT NOT NULL, UNIQUE (" + BANK_ID + ") ON CONFLICT REPLACE)";

        db.execSQL(BANK);

        final String EMPLOYMENT = "CREATE TABLE IF NOT EXISTS "
                + EMPLOYMENT_TABLE + " ("
                + AUTOINCREMENT_ID + " INTEGER PRIMARY KEY, "
                + EMPLOYMENT_ID + " TEXT  , "
                + EMPLOYMENT_CAT + " TEXT NOT NULL, "
                + TYPE + " TEXT NOT NULL, "
                + TYPE_VALUES + " TEXT NOT NULL,UNIQUE (" + TYPE_VALUES + ") ON CONFLICT REPLACE)";

        db.execSQL(EMPLOYMENT);

        final String INDUSTRY = "CREATE TABLE IF NOT EXISTS "
                + INDUSTRIES_TABLE + " ("
                + AUTOINCREMENT_ID + " INTEGER PRIMARY KEY, "
                + TYPE_VALUES + " TEXT NOT NULL,UNIQUE (" + TYPE_VALUES + ") ON CONFLICT REPLACE)";

        db.execSQL(INDUSTRY);

        // Table for notification count of image upload
        final String IMAGE_UPLOAD_COUNT = "CREATE TABLE IF NOT EXISTS "
                + IMAGE_UPLOAD_COUNT_TABLE + " ("
                + AUTOINCREMENT_ID + " INTEGER PRIMARY KEY, "
                + TOTAL_IMAGES + " INTEGER NOT NULL , "
                + UPLOADED_IMAGES + " INTEGER NOT NULL)";

        db.execSQL(IMAGE_UPLOAD_COUNT);

        // Table for notification count of image re-upload
        final String IMAGE_REUPLOAD_COUNT = "CREATE TABLE IF NOT EXISTS "
                + IMAGE_REUPLOAD_COUNT_TABLE + " ("
                + AUTOINCREMENT_ID + " INTEGER PRIMARY KEY, "
                + TOTAL_IMAGES + " INTEGER NOT NULL , "
                + UPLOADED_IMAGES + " INTEGER NOT NULL)";

        db.execSQL(IMAGE_REUPLOAD_COUNT);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        GCLog.e("On Upgrade");
        db.execSQL("DROP TABLE IF EXISTS " + ISSUE_FINANCE_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + DOC_TAG_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + PENDING_APPLICATION_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + COMPANIES_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + BANK_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + EMPLOYMENT_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + INDUSTRIES_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + IMAGE_UPLOAD_COUNT_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + IMAGE_REUPLOAD_COUNT_TABLE);
        onCreate(db);
    }

    public void insertPendingApplication(String applicationId) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.beginTransaction();
            ContentValues contentValues = new ContentValues();

            contentValues.put(APPLICATION_ID, applicationId + "");
            db.insertWithOnConflict(PENDING_APPLICATION_TABLE, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);

            db.setTransactionSuccessful();
        } catch (Exception e) {

            if (e != null) {
                Crashlytics.logException(e.getCause());
            }
        }
        db.endTransaction();
    }

    public void insertBankData(BankData bankData) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.beginTransaction();
            ContentValues contentValues = new ContentValues();
            contentValues.put(AUTOINCREMENT_ID, ++bid);
            contentValues.put(BANK_ID, bankData.getBank_id());
            contentValues.put(BANK_NAME, bankData.getBank_name());
            contentValues.put(UPDATED_TIMESTAMP, bankData.getUpdated_timestamp());
            db.insertWithOnConflict(BANK_TABLE, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);

            db.setTransactionSuccessful();
        } catch (Exception e) {
            if (e != null) {
                Crashlytics.logException(e.getCause());
            }
        }
        db.endTransaction();
    }

    public void insertBankData(List<BankData> bankDatas) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.beginTransaction();
            for (int i = 0; i < bankDatas.size(); i++) {
                BankData bankData = bankDatas.get(i);
                ContentValues contentValues = new ContentValues();
                contentValues.put(AUTOINCREMENT_ID, ++bid);
                contentValues.put(BANK_ID, bankData.getBank_id());
                contentValues.put(BANK_NAME, bankData.getBank_name());
                contentValues.put(UPDATED_TIMESTAMP, bankData.getUpdated_timestamp());
                db.insertWithOnConflict(BANK_TABLE, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            if (e != null) {
                Crashlytics.logException(e.getCause());
            }
        }
        db.endTransaction();
    }

    public void insertEmploymentData(EmploymentData employmentData) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.beginTransaction();
            ContentValues contentValues = new ContentValues();
            contentValues.put(EMPLOYMENT_ID, employmentData.getEmployment_id());
            contentValues.put(EMPLOYMENT_CAT, employmentData.getEmployment_cat());
            contentValues.put(TYPE, employmentData.getType());
            contentValues.put(TYPE_VALUES, employmentData.getType_values());
            db.insertWithOnConflict(EMPLOYMENT_TABLE, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);

            db.setTransactionSuccessful();
        } catch (Exception e) {
            if (e != null) {
                Crashlytics.logException(e.getCause());
            }
        }
        db.endTransaction();
    }

    public void insertEmploymentData(List<EmploymentData> employmentDatas) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.beginTransaction();
            for (int i = 0; i < employmentDatas.size(); i++) {
                EmploymentData employmentData = employmentDatas.get(i);
                ContentValues contentValues = new ContentValues();
                contentValues.put(AUTOINCREMENT_ID, ++eid);
                contentValues.put(EMPLOYMENT_ID, employmentData.getEmployment_id());
                contentValues.put(EMPLOYMENT_CAT, employmentData.getEmployment_cat());
                contentValues.put(TYPE, employmentData.getType());
                contentValues.put(TYPE_VALUES, employmentData.getType_values());
                db.insertWithOnConflict(EMPLOYMENT_TABLE, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
            }

            db.setTransactionSuccessful();
        } catch (Exception e) {
            if (e != null) {
                Crashlytics.logException(e.getCause());
            }
        }
        db.endTransaction();
    }

    public void insertIndustriesData(String[] industriesData) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.beginTransaction();
            for (int i = 0; i < industriesData.length; i++) {
                ContentValues contentValues = new ContentValues();
                //contentValues.put(AUTOINCREMENT_ID, ++eid);
                contentValues.put(TYPE_VALUES, industriesData[i]);
                db.insertWithOnConflict(INDUSTRIES_TABLE, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
            }

            db.setTransactionSuccessful();
        } catch (Exception e) {
            if (e != null) {
                Crashlytics.logException(e.getCause());
            }
        }
        db.endTransaction();
    }

    public void insertCompaniesData(CompaniesData companiesData) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.beginTransaction();
            ContentValues contentValues = new ContentValues();
            contentValues.put(COMPANY_ID, companiesData.getCompany_id());
            contentValues.put(COMPANY_NAME, companiesData.getCompany_name());
            contentValues.put(COMPANY_TYPE, companiesData.getType());
            contentValues.put(COMPANY_CATEGORY, companiesData.getCategory());
            contentValues.put(UPDATED_TIME, companiesData.getUpdated_time());
            db.insertWithOnConflict(COMPANIES_TABLE, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);

            db.setTransactionSuccessful();
        } catch (Exception e) {
            if (e != null) {
                Crashlytics.logException(e.getCause());
            }
        }
        db.endTransaction();
    }

    public void insertCompaniesData(List<CompaniesData> companiesDatas) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.beginTransaction();
            for (int i = 0; i < companiesDatas.size(); i++) {
                CompaniesData companiesData = companiesDatas.get(i);
                ContentValues contentValues = new ContentValues();
                contentValues.put(COMPANY_ID, companiesData.getCompany_id());
                contentValues.put(COMPANY_NAME, companiesData.getCompany_name());
                contentValues.put(COMPANY_TYPE, companiesData.getType());
                contentValues.put(COMPANY_CATEGORY, companiesData.getCategory());
                contentValues.put(UPDATED_TIME, companiesData.getUpdated_time());
                db.insertWithOnConflict(COMPANIES_TABLE, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
//            e.printStackTrace();
        }finally {
            db.endTransaction();
        }
    }

    public long insertFinanceImagesRecords(FinanceData financeData) {
        long insertId = -1;
        SQLiteDatabase db = this.getWritableDatabase();

        try {
            db.beginTransaction();
            ContentValues contentValues = new ContentValues();
            //contentValues.put(ID, ++sid);
            contentValues.put(CAR_ID, financeData.getCarId());
            contentValues.put(IMAGE_PATH, financeData.getImagePath());
            contentValues.put(IMAGE_UPLOAD_STATUS, financeData.getUploadStatus() + "");
            contentValues.put(TAG, financeData.getTag() + "");
            contentValues.put(IMAGE_REQUEST_NAME, financeData.getRequestName() + "");
            contentValues.put(CUSTOMAR_ID, financeData.getCustomarId());
            contentValues.put(APPLICATION_ID, financeData.getApplicationId());
            contentValues.put(TAG_ID_TYPE, financeData.getParentId());
            contentValues.put(CURRENT_TIMESTAMP, System.currentTimeMillis());
            contentValues.put(UPLOADING_SEQUENCE, financeData.getUploadingSequence());
            contentValues.put(TOTAL_IMAGES, financeData.getTotalImages());
//            contentValues.put(TAG_ID_TYPE, financeData.getTagTypeId());
            insertId = db.insertWithOnConflict(ISSUE_FINANCE_TABLE, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);

            db.setTransactionSuccessful();
            GCLog.e("*inserted*", "insertStockImagesRecords");
        } catch (Exception e) {
            if (e != null) {
                Crashlytics.logException(e.getCause());
            }
        }
        db.endTransaction();
        return insertId;
    }

    public void deleteImageUploadCount() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(IMAGE_UPLOAD_COUNT_TABLE, null, null);
    }

    public long insertImageUploadCount(int totalImages, int uploadedImages) {
        long insertId = -1;
        SQLiteDatabase db = this.getWritableDatabase();

        try {
            db.beginTransaction();
            ContentValues contentValues = new ContentValues();
            contentValues.put(AUTOINCREMENT_ID, ++iid);
            contentValues.put(TOTAL_IMAGES, totalImages);
            contentValues.put(UPLOADED_IMAGES, uploadedImages);
            insertId = db.insertWithOnConflict(IMAGE_UPLOAD_COUNT_TABLE, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);

            db.setTransactionSuccessful();
            GCLog.v("insertImageUploadCount" + totalImages + " " + uploadedImages);
        } catch (Exception e) {
            if (e != null) {
                Crashlytics.logException(e.getCause());
            }
        }
        db.endTransaction();
        return insertId;
    }

    public int getTotalImagesCount() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;
        int totalImageCount = 0;
        try {
            db.beginTransaction();
            cursor = db.query(IMAGE_UPLOAD_COUNT_TABLE, new String[]{}, null, null, null, null, null);
            if (cursor != null) {
                //cursor.moveToFirst();
                //if (cursor.getCount() > 0) {
                while (cursor.moveToNext()){
                    totalImageCount = cursor.getInt(cursor.getColumnIndex(TOTAL_IMAGES));
                }
            }
            cursor.close();
            db.setTransactionSuccessful();
        } catch (Exception e) {
            if (e != null) {
                Crashlytics.logException(e.getCause());
            }
        }
        db.endTransaction();
        return totalImageCount;
    }

    public int getUploadedImagesCount() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;
        int uploadedImageCount = 0;
        try {
            db.beginTransaction();
            cursor = db.query(IMAGE_UPLOAD_COUNT_TABLE, new String[]{}, null, null, null, null, null);
            if (cursor != null) {
                //cursor.moveToFirst();
                //if (cursor.getCount() > 0) {
                while (cursor.moveToNext()){
                    uploadedImageCount = cursor.getInt(cursor.getColumnIndex(UPLOADED_IMAGES));
                }
            }
            cursor.close();
            db.setTransactionSuccessful();
        } catch (Exception e) {
            if (e != null) {
                Crashlytics.logException(e.getCause());
            }
        }
        db.endTransaction();
        return uploadedImageCount;
    }

    public ArrayList<String> getFinanceImagesCount() {
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<String> listImagePath = new ArrayList<>();
        Cursor cursor = null;
        try {
            db.beginTransaction();
            cursor = db.query(ISSUE_FINANCE_TABLE, new String[]{}, null, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                if (cursor.getCount() > 0) {
                    listImagePath.add(cursor.getString(cursor.getColumnIndex(IMAGE_PATH)));
                }
            }
            cursor.close();
            db.setTransactionSuccessful();
            GCLog.e("*inserted*", "insertStockImagesRecords");
        } catch (Exception e) {
            if (e != null) {
                Crashlytics.logException(e.getCause());
            }
        }
        db.endTransaction();
        return listImagePath;
    }

    public long insertDocTags(ArrayList<DocumentInfo> documentInfos) {
        long insertId = -1;
        SQLiteDatabase db = this.getReadableDatabase();
        try {

            db.beginTransaction();

            for (DocumentInfo documentInfo : documentInfos) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(ID, ++did);
                contentValues.put(TAG, documentInfo.getTag());
                contentValues.put(DOC_NAME, documentInfo.getDocName());
                contentValues.put(PARENT_ID, documentInfo.getParentId());
                contentValues.put(PARENT_CAT_NAME, documentInfo.getParentCatName());
                contentValues.put(CURRENT_TIMESTAMP, System.currentTimeMillis());

                insertId = db.insertWithOnConflict(DOC_TAG_TABLE, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
            }
            db.setTransactionSuccessful();

            GCLog.e("*inserted*", "insertDocTagRecords");
        } catch (Exception e) {
            if (e != null) {
                Crashlytics.logException(e.getCause());
            }
        }
        db.endTransaction();
        return insertId;
    }

    //Get document name given its tag
    public String getDocName(int docTag) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selection = TAG + " = ? ";
        String[] selectionArgs = new String[]{docTag + ""};
        Cursor cursor = db.query(DOC_TAG_TABLE, new String[]{}, selection, selectionArgs, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            if (cursor.getCount() > 0) {
                return cursor.getString(cursor.getColumnIndex(DOC_NAME));
            }
            cursor.close();
        }
        return "";
    }

    //To get docs under a specific category
    public ArrayList<DocumentInfo> getCategoryDocs(String category) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = PARENT_CAT_NAME + " = ? ";
        String[] selectionArgs = new String[]{category};
        ArrayList<DocumentInfo> documentInfos = new ArrayList<>();
        db.beginTransaction();
        Cursor cursor = db.query(DOC_TAG_TABLE, ALL_COLUMNS_DOC_TAG, selection, selectionArgs, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                DocumentInfo item = new DocumentInfo();
                item.setTag(cursor.getInt(cursor.getColumnIndex(TAG)));
                item.setDocName(cursor.getString(cursor.getColumnIndex(DOC_NAME)));
                item.setParentCatName(cursor.getString(cursor.getColumnIndex(PARENT_CAT_NAME)));
                item.setParentId(cursor.getInt(cursor.getColumnIndex(PARENT_ID)));
                documentInfos.add(item);
                cursor.moveToNext();
            }
            cursor.close();
        }
        db.endTransaction();
        return documentInfos;
    }

    //get all of tags in main category
    public String[] getDocumentTagsInCategory(String category) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] tags = new String[20];
        String selection = PARENT_CAT_NAME + " = ? ";
        String[] selectionArgs = new String[]{category};
        Cursor cursor = db.query(DOC_TAG_TABLE, ALL_COLUMNS_DOC_TAG, selection, selectionArgs, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            int i = 0;
            while (!cursor.isAfterLast()) {
                tags[i] = cursor.getString(cursor.getColumnIndex(TAG));
                cursor.moveToNext();
                i++;
            }
            cursor.close();
        }
        return tags;
    }

    public ArrayList<String> getImagesByTags(String[] tags) {
        ArrayList<String> images = new ArrayList<>();
        String tagString = "";
        SQLiteDatabase db = this.getReadableDatabase();
        int i = 0;
        for (String tag : tags) {
            if (i == 0) {
                tagString = tag;
            } else {
                tagString += "," + tag;
            }
            i++;
        }
        GCLog.e(Constants.TAG, "Tag String " + tagString);
        String sql = "SELECT " + IMAGE_PATH + " from " + ISSUE_FINANCE_TABLE;//+ " where "+ TAG + " in ('"+tagString+"')";
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor != null && cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                images.add(cursor.getString(cursor.getColumnIndex(IMAGE_PATH)));
                cursor.moveToNext();
            }
        }
        return images;
    }

    //list of images basis on category
    public ArrayList<String> getImagesByCategory(String category) {
        ArrayList<String> images = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(DOC_TAG_TABLE, new String[]{TAG}, PARENT_CAT_NAME + " =?", new String[]{category}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            int i = 0;
            String tags = "";
//            String[] tags ;
            while (!cursor.isAfterLast()) {
                String tag = cursor.getString(cursor.getColumnIndex(TAG));
                if (i == 0) {
                    tags = tag;
                } else {
                    tags += "','" + tag;
                }
                i++;
                cursor.moveToNext();
            }
            cursor.close();
            String[] inTags = tags.split(",");

            GCLog.e(Constants.TAG, "Tags in category " + category + " tags : " + tags);

            if (inTags.length != 0) {
                String sql = "Select " + IMAGE_PATH + " from " + ISSUE_FINANCE_TABLE + " where " + TAG + " in ('" + tags + "')";
                Cursor cursor1 = db.rawQuery(sql, null);
//                Cursor cursor1 = db.query(ISSUE_FINANCE_TABLE, new String[]{IMAGE_PATH}, TAG +" =? " ,inTags, null, null, null);
                if (cursor1 != null && cursor1.moveToFirst()) {
                    while (!cursor1.isAfterLast()) {
                        String imagePath = cursor1.getString(cursor1.getColumnIndex(IMAGE_PATH));
                        images.add(imagePath);
                        cursor1.moveToNext();
                    }
                    cursor1.close();
                }
            }
        }
        return images;
    }

    public ArrayList<String> getImagesByCategory(Context context, String category, HashMap<String, DocumentCategories> categoriesHashMap) {
        ArrayList<String> images = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        DocumentCategories docCategoryObj = categoriesHashMap.get(category);
        int size = docCategoryObj.getSubCategories().size();
        if (size > 0) {
            int i = 0;
            String tags = "";
//            String[] tags ;
            for (i = 0; i < size; i++) {
                if (i == 0) {
                    tags = docCategoryObj.getSubCategories().get(i).getId() + "";
                } else {
                    tags += "','" + docCategoryObj.getSubCategories().get(i).getId() + "";
                }
            }

            String[] inTags = tags.split(",");

            GCLog.e(Constants.TAG, "Tags in category " + category + " tags : " + tags);

            if (inTags.length != 0) {
                String sql = "Select " + IMAGE_PATH + " from " + ISSUE_FINANCE_TABLE + " where " + TAG + " in ('" + tags + "') and " + APPLICATION_ID + " = " + CommonUtils.getStringSharedPreference(context, Constants.FINANCE_APP_ID, "");
                Cursor cursor1 = db.rawQuery(sql, null);
//                Cursor cursor1 = db.query(ISSUE_FINANCE_TABLE, new String[]{IMAGE_PATH}, TAG +" =? " ,inTags, null, null, null);
                if (cursor1 != null && cursor1.moveToFirst()) {
                    while (!cursor1.isAfterLast()) {
                        String imagePath = cursor1.getString(cursor1.getColumnIndex(IMAGE_PATH));
                        images.add(imagePath);
                        cursor1.moveToNext();
                    }
                    cursor1.close();
                }
            }
        }
        return images;
    }


    public ArrayList<FinanceData> getImagesForUpload() {
        ArrayList<FinanceData> financeDatas = new ArrayList<>();
        Cursor imagePathCursor;
        SQLiteDatabase db = null;
        try {
            db = this.getReadableDatabase();
            db.beginTransaction();
            Cursor appIdCursor = db.query(true,
                    PENDING_APPLICATION_TABLE,
                    new String[]{APPLICATION_ID},
                    null, null, null, null, null, null);
            if (appIdCursor != null) {
                if (appIdCursor.moveToFirst()) {
                    while (!appIdCursor.isAfterLast()) {
                        imagePathCursor = db.query(false,
                                ISSUE_FINANCE_TABLE,
                                new String[]{ID, IMAGE_PATH, IMAGE_REQUEST_NAME, TAG, APPLICATION_ID, CUSTOMAR_ID, TAG_ID_TYPE, UPLOADING_SEQUENCE, TOTAL_IMAGES},
                                APPLICATION_ID + " = ? and (" + IMAGE_UPLOAD_STATUS + "=? or " + IMAGE_UPLOAD_STATUS + "=? )",
                                new String[]{appIdCursor.getString(appIdCursor.getColumnIndex(APPLICATION_ID)), "", "fail"},
                                null, null, null, null);
                        if (imagePathCursor != null && imagePathCursor.moveToFirst()) {
                            while (!imagePathCursor.isAfterLast()) {
                                FinanceData data = new FinanceData(imagePathCursor.getInt(imagePathCursor.getColumnIndex(ID)),
                                        imagePathCursor.getString(imagePathCursor.getColumnIndex(IMAGE_PATH)),
                                        imagePathCursor.getString(imagePathCursor.getColumnIndex(IMAGE_REQUEST_NAME)),
                                        imagePathCursor.getString(imagePathCursor.getColumnIndex(APPLICATION_ID)),
                                        imagePathCursor.getString(imagePathCursor.getColumnIndex(CUSTOMAR_ID)),
                                        imagePathCursor.getString(imagePathCursor.getColumnIndex(TAG)),
                                        imagePathCursor.getString(imagePathCursor.getColumnIndex(TAG_ID_TYPE)),
                                        imagePathCursor.getInt(imagePathCursor.getColumnIndex(UPLOADING_SEQUENCE)),
                                        imagePathCursor.getInt(imagePathCursor.getColumnIndex(TOTAL_IMAGES)));
                                financeDatas.add(data);
                                imagePathCursor.moveToNext();
                            }
                            imagePathCursor.close();
                        }
                        appIdCursor.moveToNext();
                    }
                }
                db.setTransactionSuccessful();
                appIdCursor.close();
            }
        } catch (SQLiteDatabaseCorruptException e){
            recreateDb(db);
        } catch (Exception e){

        } finally {
            if(db != null)
                db.endTransaction();
        }
        return financeDatas;
    }

    private void recreateDb(SQLiteDatabase db) {
        GCLog.e(TAG, "On Recreate");
        db.execSQL("DROP TABLE IF EXISTS " + ISSUE_FINANCE_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + DOC_TAG_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + PENDING_APPLICATION_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + COMPANIES_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + BANK_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + EMPLOYMENT_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + IMAGE_UPLOAD_COUNT_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + IMAGE_REUPLOAD_COUNT_TABLE);
        onCreate(db);
    }

    public int updateSuccess(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        /*/ContentValues values = new ContentValues();
        values.put(IMAGE_RESPONSE_NAME, responseName);
        values.put(IMAGE_UPLOAD_STATUS, "done");
        return db.update(ISSUE_FINANCE_TABLE,
                values,
                ID + "=?",
                new String[]{String.valueOf(id)});/*/
        return db.delete(ISSUE_FINANCE_TABLE,
                ID + "=?",
                new String[]{"" + id});
        /**/
    }

    public void updateFailure(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(IMAGE_UPLOAD_STATUS, "fail");
        db.update(ISSUE_FINANCE_TABLE,
                values,
                ID + "=?",
                new String[]{String.valueOf(id)});
    }

    public void clearImageDetails() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor appIdCursor = db.query(true,
                PENDING_APPLICATION_TABLE,
                new String[]{APPLICATION_ID},
                null, null, null, null, null, null);
        Cursor imagePathCursor;
        if (appIdCursor != null) {
            if (appIdCursor.moveToFirst()) {
                while (!appIdCursor.isAfterLast()) {
                    imagePathCursor = db.query(false,
                            ISSUE_FINANCE_TABLE,
                            new String[]{ID},
                            APPLICATION_ID + " = ? and " + IMAGE_UPLOAD_STATUS + "=?",
                            new String[]{appIdCursor.getString(appIdCursor.getColumnIndex(APPLICATION_ID)), ""},
                            null, null, null, null);
                    if (imagePathCursor != null) {
                        if (imagePathCursor.getCount() == 0) {
                            /*db.delete(ISSUE_FINANCE_TABLE,
                                    APPLICATION_ID+"=?",
                                    new String[]{appIdCursor.getString(appIdCursor.getColumnIndex(APPLICATION_ID))});*/
                            db.delete(PENDING_APPLICATION_TABLE,
                                    APPLICATION_ID + "=?",
                                    new String[]{appIdCursor.getString(appIdCursor.getColumnIndex(APPLICATION_ID))});
                        }
                        imagePathCursor.close();
                    }
                    appIdCursor.moveToNext();
                }
            }
            appIdCursor.close();
        }
    }

    public void deleteDocTags() {
        SQLiteDatabase db = this.getWritableDatabase();
//        String selection = CAR_ID + " = ?";
//        String[] selectionArgs = new String[]{carID};
        db.delete(DOC_TAG_TABLE, null, null);
    }

    public ArrayList<Integer> getSuccessFailCount(String applicationId) {
        ArrayList<Integer> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        db.beginTransaction();
        Cursor cursor = db.query(false,
                ISSUE_FINANCE_TABLE,
                new String[]{ID},
                APPLICATION_ID + " = ? and " + IMAGE_UPLOAD_STATUS + "=?",
                new String[]{applicationId, ""},
                null, null, null, null);
        if (cursor != null)
            list.add(0, cursor.getCount());
        db.setTransactionSuccessful();
        db.endTransaction();
        db.beginTransaction();
        cursor = db.query(false,
                ISSUE_FINANCE_TABLE,
                new String[]{ID},
                APPLICATION_ID + " = ? and " + IMAGE_UPLOAD_STATUS + "=?",
                new String[]{applicationId, "fail"},
                null, null, null, null);
        if (cursor != null) {
            list.add(1, cursor.getCount());
            cursor.close();
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        return list;
    }


}
