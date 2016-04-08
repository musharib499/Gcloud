package com.gcloud.gaadi.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.gcloud.gaadi.ApplicationController;
import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.model.DocumentCategories;
import com.gcloud.gaadi.model.DocumentInfo;
import com.gcloud.gaadi.model.SubCategory;
import com.gcloud.gaadi.retrofit.RetrofitRequest;
import com.gcloud.gaadi.utils.GCLog;

import org.json.JSONObject;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by lakshaygirdhar on 15/10/15.
 */
public class FinanceDBHelper {

    public static Cursor getCompanies(String companyLike) {
        SQLiteDatabase db = ApplicationController.getFinanceDB(false);
        String sql = "select " + FinanceDB.COMPANY_NAME + "," + FinanceDB.COMPANY_ID + "," + FinanceDB.AUTOINCREMENT_ID + " from " + FinanceDB.COMPANIES_TABLE + " where "
                + FinanceDB.COMPANY_NAME + " like '%" + companyLike + "%'";

        Cursor cursor = db.rawQuery(sql, null);

        return cursor;
    }

    public static Cursor getBanks(String bankLike) {
        SQLiteDatabase db = ApplicationController.getFinanceDB(false);
        String sql = "select " + FinanceDB.BANK_NAME + "," + FinanceDB.BANK_ID + "," + FinanceDB.AUTOINCREMENT_ID + " from " + FinanceDB.BANK_TABLE + " where "
                + FinanceDB.BANK_NAME + " like '%" + bankLike + "%'";

        Cursor cursor = db.rawQuery(sql, null);
        return cursor;
    }

    public static int getAllBanksCounts() {
        SQLiteDatabase db = ApplicationController.getFinanceDB(false);
        String sql = "select  * from " + FinanceDB.BANK_TABLE;
        Cursor cursor = db.rawQuery(sql, null);
        return cursor.getCount();
    }

    public static int getAllCompanyCounts() {
        SQLiteDatabase db = ApplicationController.getFinanceDB(false);
        String sql = "select  * from " + FinanceDB.COMPANIES_TABLE;
        Cursor cursor = db.rawQuery(sql, null);
        return cursor.getCount();
    }

    public static void deleteImages(String appId){
        if(appId==null || appId.equals(""))
            return;
        SQLiteDatabase db = ApplicationController.getFinanceDB(true);
        db.delete(FinanceDB.ISSUE_FINANCE_TABLE,FinanceDB.APPLICATION_ID+"=?",new String[]{appId});
    }

    public static int getAllEmploymentCounts() {
        SQLiteDatabase db = ApplicationController.getFinanceDB(false);
        String sql = "select  * from " + FinanceDB.EMPLOYMENT_TABLE;
        Cursor cursor = db.rawQuery(sql, null);
        return cursor.getCount();
    }

    public static int getAllIndustriesCounts() {
        SQLiteDatabase db = ApplicationController.getFinanceDB(false);
        String sql = "select  * from " + FinanceDB.INDUSTRIES_TABLE;
        Cursor cursor = db.rawQuery(sql, null);
        return cursor.getCount();
    }

    public static void insertFinanceDocTags() throws Exception {

        ApplicationController.getFinanceDB().deleteDocTags();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Constants.METHOD_LABEL, Constants.GET_FINANCE_DOC_INFOS);

        RetrofitRequest.getDocumentTagFinance(new Callback<DocumentCategories[]>() {
            @Override
            public void success(DocumentCategories[] documentInfoses, Response response) {

                ArrayList<DocumentInfo> documentInfos = new ArrayList<DocumentInfo>();
                for (int i = 0; i < documentInfoses.length; i++) {
                    DocumentCategories docCategory = documentInfoses[i];
                    GCLog.e("Category : " + docCategory.toString());
                    ArrayList<SubCategory> subCategories = docCategory.getSubCategories();
                    for (SubCategory subCategory : subCategories) {

                        DocumentInfo documentInfo = new DocumentInfo();
                        documentInfo.setDocName(subCategory.getCategoryName());
                        documentInfo.setTag(subCategory.getId());
                        documentInfo.setParentId(Integer.parseInt(subCategory.getParentId()));
                        documentInfo.setParentCatName(docCategory.getCategoryName());
                        documentInfos.add(documentInfo);
                        GCLog.e("SubCategory : " + subCategory.toString());
                    }
                }
                ApplicationController.getFinanceDB().insertDocTags(documentInfos);
            }

            @Override
            public void failure(RetrofitError error) {
                GCLog.e("Failure Docs");
            }
        });

    }

    public static ArrayList<String> getProfession(String employment) {
        SQLiteDatabase db = ApplicationController.getFinanceDB(false);
        ArrayList<String> professions = new ArrayList<>();
        GCLog.e(Constants.TAG, "Employment Cat " + employment);
        Cursor cursor = db.query(FinanceDB.EMPLOYMENT_TABLE, new String[]{FinanceDB.TYPE_VALUES}, FinanceDB.EMPLOYMENT_CAT + " = ?", new String[]{employment}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            GCLog.e(Constants.TAG, "Cursor Not Null");
            professions.add("Select Type");
            while (!cursor.isAfterLast()) {
                professions.add(cursor.getString(cursor.getColumnIndexOrThrow(FinanceDB.TYPE_VALUES)));
                cursor.moveToNext();
            }
        } else {
            GCLog.e(Constants.TAG, "Cursor Null");
        }
        return professions;
    }

    public static ArrayList<String> getIndustries() {
        SQLiteDatabase db = ApplicationController.getFinanceDB(false);
        ArrayList<String> industriesList = new ArrayList<>();
        Cursor cursor = db.query(FinanceDB.INDUSTRIES_TABLE, new String[]{FinanceDB.TYPE_VALUES}, null, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            GCLog.e(Constants.TAG, "Cursor Not Null");
            industriesList.add("Select Type");
            while (!cursor.isAfterLast()) {
                industriesList.add(cursor.getString(cursor.getColumnIndexOrThrow(FinanceDB.TYPE_VALUES)));
                cursor.moveToNext();
            }
        } else {
            GCLog.e(Constants.TAG, "Cursor Null");
        }
        return industriesList;
    }


    public static String getBankId(String name){
        SQLiteDatabase database = ApplicationController.getFinanceDB(false);
        Cursor cursor = database.query(FinanceDB.BANK_TABLE,new String[]{FinanceDB.BANK_ID},FinanceDB.BANK_NAME+" =?",new String[]{name},null,null,null);
        String bankId = "";
        if(cursor!=null&&cursor.moveToFirst()){
            while (!cursor.isAfterLast()){
                bankId = cursor.getString(cursor.getColumnIndexOrThrow(FinanceDB.BANK_ID));
                cursor.moveToNext();
            }
        }
        return bankId;
    }

    public static String getParentCategoryName(String parent_cat_id) {
        SQLiteDatabase database = ApplicationController.getFinanceDB(false);
        Cursor cursor = database.query(FinanceDB.DOC_TAG_TABLE,new String[]{FinanceDB.PARENT_CAT_NAME},FinanceDB.PARENT_ID+" = ?",new String[]{parent_cat_id},null,null,null);
        if(cursor!=null&&cursor.moveToFirst()){
            while (!cursor.isAfterLast()){
                return cursor.getString(cursor.getColumnIndexOrThrow(FinanceDB.PARENT_CAT_NAME));
            }
        }
        return "";
    }
}
