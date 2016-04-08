package com.gcloud.gaadi.db;

import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.RemoteException;
import android.view.View;

import com.gcloud.gaadi.ApplicationController;
import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.model.StockItemModel;
import com.gcloud.gaadi.model.StocksModel;
import com.gcloud.gaadi.providers.ViewStockProvider;
import com.gcloud.gaadi.utils.GCLog;
import com.google.gson.Gson;

import java.util.ArrayList;

import static com.gcloud.gaadi.providers.ViewStockProvider.CONTENT_URI;

/**
 * Created by alokmishra on 23/2/16.
 */
public class DBFunction {


    public static StockItemModel getStockModel(Cursor c) {
        SQLiteDatabase db = ApplicationController.getReadableDB();
        db.beginTransaction();
        Cursor cursor = c;
        StockItemModel model = new StockItemModel();
        try {
            if (cursor != null && cursor.moveToFirst()) {
                model.setStockId(cursor.getString(cursor.getColumnIndex(ViewStockModel.STOCK_ID)));
                model.setDealerPlatform(cursor.getString(cursor.getColumnIndex(ViewStockModel.DEALER_PLATFORM)));
                model.setFinanceList(cursor.getString(cursor.getColumnIndex(ViewStockModel.FINANCE_LIST)));
                model.setChangeTime(cursor.getString(cursor.getColumnIndex(ViewStockModel.CHANGE_TIME)));
                model.setCarCertification(cursor.getString(cursor.getColumnIndex(ViewStockModel.CAR_CERTIFICATION)));
                model.setKms(cursor.getString(cursor.getColumnIndex(ViewStockModel.KMS)));
                model.setUserName(cursor.getString(cursor.getColumnIndex(ViewStockModel.USER_NAME)));
                model.setMakeName(cursor.getString(cursor.getColumnIndex(ViewStockModel.MAKE_NAME)));
                model.setModelName(cursor.getString(cursor.getColumnIndex(ViewStockModel.MODEL_NAME)));
                model.setVersionName(cursor.getString(cursor.getColumnIndex(ViewStockModel.VERSION_NAME)));
                model.setMake(cursor.getInt(cursor.getColumnIndex(ViewStockModel.MAKE)));
                model.setPriceSort(cursor.getInt(cursor.getColumnIndex(ViewStockModel.PRICE_SORT)));
                model.setKmSort(cursor.getInt(cursor.getColumnIndex(ViewStockModel.KM_SORT)));
                model.setModelVersion(cursor.getString(cursor.getColumnIndex(ViewStockModel.MODEL_VERSION)));
                model.setDealerPrice(cursor.getString(cursor.getColumnIndex(ViewStockModel.DEALER_PRICE)));
                model.setColor(cursor.getString(cursor.getColumnIndex(ViewStockModel.COLOR)));
                model.setMm(cursor.getString(cursor.getColumnIndex(ViewStockModel.MM)));
                model.setFuelType(cursor.getString(cursor.getColumnIndex(ViewStockModel.FUEL_TYPE)));
                model.setRegistrationNumber(cursor.getString(cursor.getColumnIndex(ViewStockModel.REG_NO)));
                model.setModelYear(cursor.getString(cursor.getColumnIndex(ViewStockModel.MODEL_YEAR)));
                model.setD2dMobile(cursor.getString(cursor.getColumnIndex(ViewStockModel.MOBILE)));
                model.setD2dEmail(cursor.getString(cursor.getColumnIndex(ViewStockModel.EMAIL)));
                model.setShowroomId(cursor.getString(cursor.getColumnIndex(ViewStockModel.SHOWROOM_ID)));
                model.setGaadiId(cursor.getString(cursor.getColumnIndex(ViewStockModel.GAADI_ID)));
                model.setCreatedDate(cursor.getString(cursor.getColumnIndex(ViewStockModel.CREATE_DATE)));
                model.setDomain(cursor.getString(cursor.getColumnIndex(ViewStockModel.DOMAIN)));
                model.setCarId(cursor.getString(cursor.getColumnIndex(ViewStockModel.CAR_ID)));
                model.setHexCode(cursor.getString(cursor.getColumnIndex(ViewStockModel.HEX_CODE)));
                model.setTrusmarkCertified(cursor.getString(cursor.getColumnIndex(ViewStockModel.TRUST_MARK_CERTIFY)));
                model.setActive(cursor.getString(cursor.getColumnIndex(ViewStockModel.ACTIVE)));
                model.setShareText(cursor.getString(cursor.getColumnIndex(ViewStockModel.SHARE_TEXT)));
                model.setTotalLeads(cursor.getString(cursor.getColumnIndex(ViewStockModel.TOTAL_LEADS)));
                model.setCertification(cursor.getString(cursor.getColumnIndex(ViewStockModel.CAR_CERTIFICATION)));
                model.setDealerPrice(cursor.getString(cursor.getColumnIndex(ViewStockModel.DEALER_PRICE)));
                model.setImageIcon(cursor.getString(cursor.getColumnIndex(ViewStockModel.IMAGE_ICON)));
                model.setAreaOfCover(cursor.getString(cursor.getColumnIndex(ViewStockModel.AREAOFCOVER)));
                model.setInspectedCar(cursor.getString(cursor.getColumnIndex(ViewStockModel.INSPECTED_CAR)));
                model.setUpdateTime(cursor.getString(cursor.getColumnIndex(ViewStockModel.UPDATE_TIME)));
            }
            GCLog.e("mishra", "stocks model := " + model);
            return model;
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
        return null;
    }

    public static StockItemModel getSingleStockModel(Cursor c) {
        SQLiteDatabase db = ApplicationController.getReadableDB();
        db.beginTransaction();
        Cursor cursor = c;
        StockItemModel model = new StockItemModel();
        try {
            if (cursor != null) {
                model.setStockId(cursor.getString(cursor.getColumnIndex(ViewStockModel.STOCK_ID)));
                model.setDealerPlatform(cursor.getString(cursor.getColumnIndex(ViewStockModel.DEALER_PLATFORM)));
                model.setFinanceList(cursor.getString(cursor.getColumnIndex(ViewStockModel.FINANCE_LIST)));
                model.setChangeTime(cursor.getString(cursor.getColumnIndex(ViewStockModel.CHANGE_TIME)));
                model.setCarCertification(cursor.getString(cursor.getColumnIndex(ViewStockModel.CAR_CERTIFICATION)));
                model.setKms(cursor.getString(cursor.getColumnIndex(ViewStockModel.KMS)));
                model.setUserName(cursor.getString(cursor.getColumnIndex(ViewStockModel.USER_NAME)));
                model.setMakeName(cursor.getString(cursor.getColumnIndex(ViewStockModel.MAKE_NAME)));
                model.setModelName(cursor.getString(cursor.getColumnIndex(ViewStockModel.MODEL_NAME)));
                model.setVersionName(cursor.getString(cursor.getColumnIndex(ViewStockModel.VERSION_NAME)));
                model.setMake(cursor.getInt(cursor.getColumnIndex(ViewStockModel.MAKE)));
                model.setPriceSort(cursor.getInt(cursor.getColumnIndex(ViewStockModel.PRICE_SORT)));
                model.setKmSort(cursor.getInt(cursor.getColumnIndex(ViewStockModel.KM_SORT)));
                model.setModelVersion(cursor.getString(cursor.getColumnIndex(ViewStockModel.MODEL_VERSION)));
                model.setDealerPrice(cursor.getString(cursor.getColumnIndex(ViewStockModel.DEALER_PRICE)));
                model.setColor(cursor.getString(cursor.getColumnIndex(ViewStockModel.COLOR)));
                model.setMm(cursor.getString(cursor.getColumnIndex(ViewStockModel.MM)));
                model.setFuelType(cursor.getString(cursor.getColumnIndex(ViewStockModel.FUEL_TYPE)));
                model.setRegistrationNumber(cursor.getString(cursor.getColumnIndex(ViewStockModel.REG_NO)));
                model.setModelYear(cursor.getString(cursor.getColumnIndex(ViewStockModel.MODEL_YEAR)));
                model.setD2dMobile(cursor.getString(cursor.getColumnIndex(ViewStockModel.MOBILE)));
                model.setD2dEmail(cursor.getString(cursor.getColumnIndex(ViewStockModel.EMAIL)));
                model.setShowroomId(cursor.getString(cursor.getColumnIndex(ViewStockModel.SHOWROOM_ID)));
                model.setGaadiId(cursor.getString(cursor.getColumnIndex(ViewStockModel.GAADI_ID)));
                model.setCreatedDate(cursor.getString(cursor.getColumnIndex(ViewStockModel.CREATE_DATE)));
                model.setDomain(cursor.getString(cursor.getColumnIndex(ViewStockModel.DOMAIN)));
                model.setCarId(cursor.getString(cursor.getColumnIndex(ViewStockModel.CAR_ID)));
                model.setHexCode(cursor.getString(cursor.getColumnIndex(ViewStockModel.HEX_CODE)));
                model.setTrusmarkCertified(cursor.getString(cursor.getColumnIndex(ViewStockModel.TRUST_MARK_CERTIFY)));
                model.setActive(cursor.getString(cursor.getColumnIndex(ViewStockModel.ACTIVE)));
                model.setShareText(cursor.getString(cursor.getColumnIndex(ViewStockModel.SHARE_TEXT)));
                model.setTotalLeads(cursor.getString(cursor.getColumnIndex(ViewStockModel.TOTAL_LEADS)));
                model.setCertification(cursor.getString(cursor.getColumnIndex(ViewStockModel.CAR_CERTIFICATION)));
                model.setDealerPrice(cursor.getString(cursor.getColumnIndex(ViewStockModel.DEALER_PRICE)));
                model.setImageIcon(cursor.getString(cursor.getColumnIndex(ViewStockModel.IMAGE_ICON)));
                model.setAreaOfCover(cursor.getString(cursor.getColumnIndex(ViewStockModel.AREAOFCOVER)));
                model.setInspectedCar(cursor.getString(cursor.getColumnIndex(ViewStockModel.INSPECTED_CAR)));
                model.setUpdateTime(cursor.getString(cursor.getColumnIndex(ViewStockModel.UPDATE_TIME)));
            }
            return model;
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
        return null;
    }

    public static ArrayList<StockItemModel> getStockForFilterList(String dealerId) {
        SQLiteDatabase db = ApplicationController.getReadableDB();
        ArrayList<StockItemModel> stocks = new ArrayList<>();
        db.beginTransaction();
        Cursor cursor = null;
        String regiNo = "";
        String make = "";
        String fuelType = "";
        String mfgYear = "";
        String status = "";
        try {
            regiNo = regiNo.length() > 0 ? " AND " + ViewStockModel.REG_NO + " LIKE '" + regiNo + "%'" : "";
            make = make.length() > 0 ? " AND " + ViewStockModel.MAKE + " IN " + " ( " + make + " )" : "";
            fuelType = fuelType.length() > 0 ? " AND " + ViewStockModel.FUEL_TYPE + " IN " + " ( " + fuelType + " ) " : "";
            mfgYear = mfgYear.length() > 0 ? " AND " + ViewStockModel.MODEL_YEAR + " IN " + "( " + mfgYear + " ) " : "";

            String selection = ViewStockModel.STOCK_ID + " = " + regiNo + mfgYear + fuelType + make;

            cursor = db.query(ViewStockModel.AVAILABLE_TABLE, ViewStockModel.TABLE_COLUMNS, selection, null, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    StockItemModel inv = new StockItemModel();
                    StockItemModel model = new StockItemModel();
                    model.setStockId(cursor.getString(cursor.getColumnIndex(ViewStockModel.STOCK_ID)));
                    model.setDealerPlatform(cursor.getString(cursor.getColumnIndex(ViewStockModel.DEALER_PLATFORM)));
                    model.setFinanceList(cursor.getString(cursor.getColumnIndex(ViewStockModel.FINANCE_LIST)));
                    model.setChangeTime(cursor.getString(cursor.getColumnIndex(ViewStockModel.CHANGE_TIME)));
                    model.setCarCertification(cursor.getString(cursor.getColumnIndex(ViewStockModel.CAR_CERTIFICATION)));
                    model.setKms(cursor.getString(cursor.getColumnIndex(ViewStockModel.KMS)));
                    model.setUserName(cursor.getString(cursor.getColumnIndex(ViewStockModel.USER_NAME)));
                    model.setMake(cursor.getInt(cursor.getColumnIndex(ViewStockModel.MAKE)));
                    model.setModelVersion(cursor.getString(cursor.getColumnIndex(ViewStockModel.MODEL_VERSION)));
                    model.setDealerPrice(cursor.getString(cursor.getColumnIndex(ViewStockModel.DEALER_PRICE)));
                    model.setColor(cursor.getString(cursor.getColumnIndex(ViewStockModel.COLOR)));
                    model.setMm(cursor.getString(cursor.getColumnIndex(ViewStockModel.MM)));
                    model.setFuelType(cursor.getString(cursor.getColumnIndex(ViewStockModel.FUEL_TYPE)));
                    model.setRegistrationNumber(cursor.getString(cursor.getColumnIndex(ViewStockModel.REG_NO)));
                    model.setModelYear(cursor.getString(cursor.getColumnIndex(ViewStockModel.MODEL_YEAR)));
                    model.setD2dMobile(cursor.getString(cursor.getColumnIndex(ViewStockModel.MOBILE)));
                    model.setD2dEmail(cursor.getString(cursor.getColumnIndex(ViewStockModel.EMAIL)));
                    model.setShowroomId(cursor.getString(cursor.getColumnIndex(ViewStockModel.SHOWROOM_ID)));
                    model.setGaadiId(cursor.getString(cursor.getColumnIndex(ViewStockModel.GAADI_ID)));
                    model.setCreatedDate(cursor.getString(cursor.getColumnIndex(ViewStockModel.CREATE_DATE)));
                    model.setDomain(cursor.getString(cursor.getColumnIndex(ViewStockModel.DOMAIN)));
                    model.setCarId(cursor.getString(cursor.getColumnIndex(ViewStockModel.CAR_ID)));
                    model.setHexCode(cursor.getString(cursor.getColumnIndex(ViewStockModel.HEX_CODE)));
                    model.setTrusmarkCertified(cursor.getString(cursor.getColumnIndex(ViewStockModel.TRUST_MARK_CERTIFY)));
                    model.setActive(cursor.getString(cursor.getColumnIndex(ViewStockModel.ACTIVE)));
                    model.setShareText(cursor.getString(cursor.getColumnIndex(ViewStockModel.SHARE_TEXT)));
                    model.setTotalLeads(cursor.getString(cursor.getColumnIndex(ViewStockModel.TOTAL_LEADS)));
                    model.setCertification(cursor.getString(cursor.getColumnIndex(ViewStockModel.CAR_CERTIFICATION)));
                    model.setDealerPrice(cursor.getString(cursor.getColumnIndex(ViewStockModel.DEALER_PRICE)));
                    model.setImageIcon(cursor.getString(cursor.getColumnIndex(ViewStockModel.IMAGE_ICON)));
                    model.setAreaOfCover(cursor.getString(cursor.getColumnIndex(ViewStockModel.AREAOFCOVER)));
                    model.setInspectedCar(cursor.getString(cursor.getColumnIndex(ViewStockModel.INSPECTED_CAR)));
                    model.setUpdateTime(cursor.getString(cursor.getColumnIndex(ViewStockModel.UPDATE_TIME)));
                    stocks.add(model);
                    cursor.moveToNext();
                }
            }
            return stocks;
        } catch (SQLiteException e) {
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            db.endTransaction();
        }
        return null;
    }

    public static ArrayList<StockItemModel> getAvailableStockLists(String isActive) {
        SQLiteDatabase db = ApplicationController.getReadableDB();
        ArrayList<StockItemModel> stocks = new ArrayList<>();
        db.beginTransaction();
        Cursor cursor = null;
        try {
            cursor = db.query(ViewStockModel.AVAILABLE_TABLE, ViewStockModel.TABLE_COLUMNS, ViewStockModel.ACTIVE + "=?", new String[]{isActive}, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    StockItemModel model = new StockItemModel();
                    model.setStockId(cursor.getString(cursor.getColumnIndex(ViewStockModel.STOCK_ID)));
                    model.setDealerPlatform(cursor.getString(cursor.getColumnIndex(ViewStockModel.DEALER_PLATFORM)));
                    model.setMakeName(cursor.getString(cursor.getColumnIndex(ViewStockModel.MAKE_NAME)));
                    model.setModelName(cursor.getString(cursor.getColumnIndex(ViewStockModel.MODEL_NAME)));
                    model.setVersionName(cursor.getString(cursor.getColumnIndex(ViewStockModel.VERSION_NAME)));
                    model.setFinanceList(cursor.getString(cursor.getColumnIndex(ViewStockModel.FINANCE_LIST)));
                    model.setChangeTime(cursor.getString(cursor.getColumnIndex(ViewStockModel.CHANGE_TIME)));
                    model.setCarCertification(cursor.getString(cursor.getColumnIndex(ViewStockModel.CAR_CERTIFICATION)));
                    model.setKms(cursor.getString(cursor.getColumnIndex(ViewStockModel.KMS)));
                    model.setPriceSort(cursor.getInt(cursor.getColumnIndex(ViewStockModel.PRICE_SORT)));
                    model.setKmSort(cursor.getInt(cursor.getColumnIndex(ViewStockModel.KM_SORT)));
                    model.setUserName(cursor.getString(cursor.getColumnIndex(ViewStockModel.USER_NAME)));
                    model.setMake(cursor.getInt(cursor.getColumnIndex(ViewStockModel.MAKE)));
                    model.setModelVersion(cursor.getString(cursor.getColumnIndex(ViewStockModel.MODEL_VERSION)));
                    model.setDealerPrice(cursor.getString(cursor.getColumnIndex(ViewStockModel.DEALER_PRICE)));
                    model.setColor(cursor.getString(cursor.getColumnIndex(ViewStockModel.COLOR)));
                    model.setMm(cursor.getString(cursor.getColumnIndex(ViewStockModel.MM)));
                    model.setFuelType(cursor.getString(cursor.getColumnIndex(ViewStockModel.FUEL_TYPE)));
                    model.setRegistrationNumber(cursor.getString(cursor.getColumnIndex(ViewStockModel.REG_NO)));
                    model.setModelYear(cursor.getString(cursor.getColumnIndex(ViewStockModel.MODEL_YEAR)));
                    model.setD2dMobile(cursor.getString(cursor.getColumnIndex(ViewStockModel.MOBILE)));
                    model.setD2dEmail(cursor.getString(cursor.getColumnIndex(ViewStockModel.EMAIL)));
                    model.setShowroomId(cursor.getString(cursor.getColumnIndex(ViewStockModel.SHOWROOM_ID)));
                    model.setGaadiId(cursor.getString(cursor.getColumnIndex(ViewStockModel.GAADI_ID)));
                    model.setCreatedDate(cursor.getString(cursor.getColumnIndex(ViewStockModel.CREATE_DATE)));
                    model.setDomain(cursor.getString(cursor.getColumnIndex(ViewStockModel.DOMAIN)));
                    model.setCarId(cursor.getString(cursor.getColumnIndex(ViewStockModel.CAR_ID)));
                    model.setHexCode(cursor.getString(cursor.getColumnIndex(ViewStockModel.HEX_CODE)));
                    model.setTrusmarkCertified(cursor.getString(cursor.getColumnIndex(ViewStockModel.TRUST_MARK_CERTIFY)));
                    model.setActive(cursor.getString(cursor.getColumnIndex(ViewStockModel.ACTIVE)));
                    model.setShareText(cursor.getString(cursor.getColumnIndex(ViewStockModel.SHARE_TEXT)));
                    model.setTotalLeads(cursor.getString(cursor.getColumnIndex(ViewStockModel.TOTAL_LEADS)));
                    model.setCertification(cursor.getString(cursor.getColumnIndex(ViewStockModel.CAR_CERTIFICATION)));
                    model.setDealerPrice(cursor.getString(cursor.getColumnIndex(ViewStockModel.DEALER_PRICE)));
                    model.setImageIcon(cursor.getString(cursor.getColumnIndex(ViewStockModel.IMAGE_ICON)));
                    model.setAreaOfCover(cursor.getString(cursor.getColumnIndex(ViewStockModel.AREAOFCOVER)));
                    model.setInspectedCar(cursor.getString(cursor.getColumnIndex(ViewStockModel.INSPECTED_CAR)));
                    model.setUpdateTime(cursor.getString(cursor.getColumnIndex(ViewStockModel.UPDATE_TIME)));
                    stocks.add(model);
                    cursor.moveToNext();
                }
            }
            GCLog.e("mishra", "stocks size := " + stocks.size());
            return stocks;
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            db.endTransaction();
        }
        return null;
    }

    public static int getActiveLists() {
        SQLiteDatabase db = ApplicationController.getReadableDB();
        db.beginTransaction();
        Cursor cursor = null;
        try {
             String[] projection = new String[]{ViewStockModel.STOCK_ID};
            cursor = db.query(ViewStockModel.AVAILABLE_TABLE, projection, ViewStockModel.ACTIVE+" =? ", new String[]{"1"}, null, null, null, null);
            cursor.getCount();
            return cursor.getCount();
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            db.endTransaction();
        }
        return 0;
    }

    public static int getInActiveLists() {
        SQLiteDatabase db = ApplicationController.getReadableDB();
        db.beginTransaction();
        Cursor cursor = null;
        try {
            String[] projection = new String[]{ViewStockModel.STOCK_ID};
            cursor = db.query(ViewStockModel.AVAILABLE_TABLE, projection, ViewStockModel.ACTIVE+" =? ", new String[]{"0"}, null, null, null, null);
            return cursor.getCount();
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            db.endTransaction();
        }
        return 0;
    }

    public static int getFinanceInsuranceLists() {
        SQLiteDatabase db = ApplicationController.getReadableDB();
        db.beginTransaction();
        Cursor cursor = null;
        try {
            String[] projection = new String[]{ViewStockModel.STOCK_ID};
            cursor = db.query(ViewStockModel.AVAILABLE_TABLE, projection, ViewStockModel.FINANCE_LIST+" =? ", new String[]{"1"}, null, null, null, null);
            return cursor.getCount();
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            db.endTransaction();
        }
        return 0;
    }

    public static int getInspectedLists() {
        SQLiteDatabase db = ApplicationController.getReadableDB();
        db.beginTransaction();
        Cursor cursor = null;
        try {
            String[] projection = new String[]{ViewStockModel.STOCK_ID};
            cursor = db.query(ViewStockModel.AVAILABLE_TABLE, projection, ViewStockModel.INSPECTED_CAR+" =? ", new String[]{"1"}, null, null, null, null);
            return cursor.getCount();
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            db.endTransaction();
        }
        return 0;
    }

    public static synchronized void insertData(Context context, ArrayList<StockItemModel> stocks) {
        SQLiteDatabase db = ApplicationController.getWritableDB();
//        db.beginTransaction();
        ContentValues[] contentValuesArr = null;
        try {
            if (stocks != null && stocks.size() > 0) {
                contentValuesArr= new ContentValues[stocks.size()];
//                ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
                for (int i = 0; i < stocks.size(); i++) {
                    StockItemModel model = stocks.get(i);
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(ViewStockModel.STOCK_ID, model.getStockId());
                    contentValues.put(ViewStockModel.DEALER_PLATFORM, model.getDealerPlatform());
                    contentValues.put(ViewStockModel.FINANCE_LIST, model.getFinanceList());
                    contentValues.put(ViewStockModel.CHANGE_TIME, model.getChangeTime());
                    contentValues.put(ViewStockModel.CAR_CERTIFICATION, model.getCarCertification());
                    contentValues.put(ViewStockModel.USER_NAME, model.getUserName());
                    contentValues.put(ViewStockModel.MAKE_NAME, model.getMakeName());
                    contentValues.put(ViewStockModel.MODEL_NAME, model.getModelName());
                    contentValues.put(ViewStockModel.VERSION_NAME, model.getVersionName());
                    contentValues.put(ViewStockModel.MAKE, model.getMake());
                    contentValues.put(ViewStockModel.MODEL_VERSION, model.getModelVersion());
                    contentValues.put(ViewStockModel.KMS, model.getKms());
                    contentValues.put(ViewStockModel.SHOWROOM_ID, model.getShowroomId());
                    contentValues.put(ViewStockModel.STOCK_PRICE, model.getStockPrice());
                    contentValues.put(ViewStockModel.COLOR, model.getColor());
                    contentValues.put(ViewStockModel.MM, model.getMm());
                    contentValues.put(ViewStockModel.PRICE_SORT, model.getPriceSort());
                    contentValues.put(ViewStockModel.KM_SORT, model.getKmSort());
                    contentValues.put(ViewStockModel.FUEL_TYPE, model.getFuelType());
                    contentValues.put(ViewStockModel.REG_NO, model.getRegistrationNumber());
                    contentValues.put(ViewStockModel.MODEL_YEAR, model.getModelYear());
                    contentValues.put(ViewStockModel.MOBILE, model.getD2dMobile());
                    contentValues.put(ViewStockModel.EMAIL, model.getD2dEmail());
                    contentValues.put(ViewStockModel.TRUST_MARK_CERTIFY, model.getTrusmarkCertified());
                    contentValues.put(ViewStockModel.SHOWROOM_ID, model.getShowroomId());
                    contentValues.put(ViewStockModel.GAADI_ID, model.getGaadiId());
                    contentValues.put(ViewStockModel.CREATE_DATE, model.getCreatedDate());
                    contentValues.put(ViewStockModel.DOMAIN, model.getDomain());
                    contentValues.put(ViewStockModel.CAR_ID, model.getCarId());
                    contentValues.put(ViewStockModel.HEX_CODE, model.getHexCode());
                    contentValues.put(ViewStockModel.ACTIVE, model.getActive());
                    contentValues.put(ViewStockModel.SHARE_TEXT, model.getShareText());
                    contentValues.put(ViewStockModel.TOTAL_LEADS, model.getTotalLeads());
                    contentValues.put(ViewStockModel.CAR_CERTIFICATION, model.getCertification());
                    contentValues.put(ViewStockModel.DEALER_PRICE, model.getDealerPrice());
                    contentValues.put(ViewStockModel.IMAGE_ICON, model.getImageIcon());
                    contentValues.put(ViewStockModel.AREAOFCOVER, model.getAreaOfCover());
                    contentValues.put(ViewStockModel.INSPECTED_CAR, model.getInspectedCar());
                    if (model.getUpdateTime() == null) {
                        contentValues.put(ViewStockModel.UPDATE_TIME, System.currentTimeMillis() + "");
                    } else {
                        contentValues.put(ViewStockModel.UPDATE_TIME, model.getUpdateTime());
                    }
                    contentValuesArr[i]=contentValues;

//                    ops.add(ContentProviderOperation.newInsert(CONTENT_URI).withValues(contentValues).withYieldAllowed(true).build());
                }

//                context.getContentResolver().applyBatch(Constants.VIEWSTOCK_CONTENT_AUTHORITY,ops);
                context.getContentResolver().bulkInsert(ViewStockProvider.CONTENT_URI, contentValuesArr);
            }
//            db.setTransactionSuccessful();
            context.getContentResolver().notifyChange(CONTENT_URI, null);
        }catch (SQLiteException e ){
            e.printStackTrace();
        } /*catch (RemoteException e) {
            e.printStackTrace();
        } catch (OperationApplicationException e) {
            e.printStackTrace();
        }*/ finally {
//            db.endTransaction();
        }
    }

    public static void clearStocksTable() {
        SQLiteDatabase db = ApplicationController.getWritableDB();
        db.beginTransaction();
        try {
            ApplicationController.getInstance().getContentResolver().delete(CONTENT_URI,null,null);
            db.delete(ViewStockModel.FILTER_TABLE, null, null);
            db.setTransactionSuccessful();
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }

    }

    public static synchronized void insertFilterData(String data, String inspectedFilter) {
        SQLiteDatabase db = ApplicationController.getWritableDB();
        db.beginTransaction();
        try {
            db.delete(ViewStockModel.FILTER_TABLE,null,null);
            StocksModel.Filters model = new StocksModel().new Filters();
            ContentValues values = new ContentValues();
            if(data != null)
                values.put(ViewStockModel.FILTER_ACTIVE, data);
            if(inspectedFilter != null)
                values.put(ViewStockModel.FILTER_INSPECTED, inspectedFilter);
            if (model.getUpdateTime() == null) {
                values.put(ViewStockModel.UPDATE_TIME, System.currentTimeMillis() + "");
            } else {
                values.put(ViewStockModel.UPDATE_TIME, model.getUpdateTime());
            }
            db.insertWithOnConflict(ViewStockModel.FILTER_TABLE, null, values, SQLiteDatabase.CONFLICT_REPLACE);

            db.setTransactionSuccessful();
        } catch (SQLiteException e) {

        } finally {
            if (db != null) {
                db.endTransaction();
            }
        }
    }

    public static StocksModel.Filters getFilterData() {
        SQLiteDatabase db = ApplicationController.getReadableDB();
        StocksModel.Filters filterList = new StocksModel().new Filters();
        Cursor cursor = null;
        // db.beginTransaction();
        try {
            cursor = db.query(ViewStockModel.FILTER_TABLE, ViewStockModel.FILTER_COLUMNS, null, null, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                filterList = new Gson().fromJson((String) cursor.getString(cursor.getColumnIndex(ViewStockModel.FILTER_ACTIVE)), StocksModel.Filters.class);
                return filterList;
            }
            return filterList;
        } catch (SQLiteException e) {
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return null;
    }

   /* public static void update(Context mContext, String active, String carId) {
        SQLiteDatabase db = ApplicationController.getWritableDB();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(ViewStockModel.AVAILABLE_TABLE, active);
//            db.update(ViewStockModel.AVAILABLE_TABLE, values, ViewStockModel.CAR_ID + "=?", new String[]{carId});
            mContext.getContentResolver().update(CONTENT_URI, values, ViewStockModel.CAR_ID + " =? ", new String[]{carId});
        } catch (SQLiteException e) {
        } finally {
            db.endTransaction();
        }
    }*/

    public static StocksModel.Filters getInspectedFilterData() {
        SQLiteDatabase db = ApplicationController.getReadableDB();
        StocksModel.Filters filterList = new StocksModel().new Filters();
        Cursor cursor = null;
        // db.beginTransaction();
        try {
            cursor = db.query(ViewStockModel.FILTER_TABLE, ViewStockModel.FILTER_COLUMNS, null, null, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                filterList = new Gson().fromJson((String) cursor.getString(cursor.getColumnIndex(ViewStockModel.FILTER_ACTIVE)), StocksModel.Filters.class);
            }
            return filterList;
        } catch (SQLiteException e) {
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return filterList;
    }

}
