package com.gcloud.gaadi.utils;

import android.content.Context;
import android.util.Log;

import com.gcloud.gaadi.ApplicationController;
import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.model.DocumentCategories;
import com.gcloud.gaadi.model.DocumentInfo;
import com.gcloud.gaadi.model.Finance.BanksDataResponse;
import com.gcloud.gaadi.model.Finance.CompaniesDataResponse;
import com.gcloud.gaadi.model.Finance.EmploymentDataResponse;
import com.gcloud.gaadi.model.Finance.FinanceLoanCasesListModel;
import com.gcloud.gaadi.model.Finance.IndustriesDataResponse;
import com.gcloud.gaadi.model.SubCategory;
import com.gcloud.gaadi.retrofit.RetrofitRequest;
import com.imageuploadlib.Utils.ActiveDocumentMetrics;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

/**
 * Created by lakshaygirdhar on 6/10/15.
 */
public class FinanceUtils {

    public static void printLog(Response response) {
        String json = new String(((TypedByteArray) response.getBody()).getBytes());
        Log.v("failure", json.toString());
    }

    public static ArrayList<String> getDocumentCategories() {
        ApplicationController controller = ApplicationController.getInstance();
        if(controller.getDocumentCategories() == null){
            ArrayList<String> documentCategories = new ArrayList<>();
            documentCategories.add(0, Constants.FINANCE_APPLICATION_FORM);
            documentCategories.add(1, Constants.ID_PROOFS);
            documentCategories.add(2, Constants.ADDRESS_PROOFS);
            documentCategories.add(3, Constants.INCOME_PROOFS);
            documentCategories.add(4, Constants.PAN_CARD);
            documentCategories.add(5, Constants.RC_COPY);
            documentCategories.add(6, Constants.RTO_DOCUMENTS);
            documentCategories.add(7, Constants.SUPPORTING_DOCUMENTS);
            documentCategories.add(8, Constants.OTHER_DOCS);
            controller.setDocumentCategories(documentCategories);
        }
        return controller.getDocumentCategories();
    }

    public static ArrayList<String> getDocumentCategories(ArrayList<DocumentCategories> arrayList) {
        int size = arrayList.size();
        ArrayList<String> documentCategories = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            documentCategories.add(i, arrayList.get(i).getCategoryName());
        }
        return documentCategories;
    }

    public static HashMap<String, DocumentCategories> getDocumentCategoriesMap(ArrayList<DocumentCategories> arrayList) {
        int size = arrayList.size();
        HashMap<String, DocumentCategories> docCategoriesMap = new HashMap<>();
        for (int i = 0; i < size; i++) {
            docCategoriesMap.put(arrayList.get(i).getCategoryName(), arrayList.get(i));
        }
        return docCategoriesMap;
    }

    public static HashMap<String, DocumentCategories> getDocCategoriesIDMap(ArrayList<DocumentCategories> arrayList) {
        int size = arrayList.size();
        HashMap<String, DocumentCategories> docCategoriesMap = new HashMap<>();
        for (int i = 0; i < size; i++) {
            docCategoriesMap.put(arrayList.get(i).getId(), arrayList.get(i));
        }
        return docCategoriesMap;
    }

    public static ArrayList<DocumentInfo> getCategoriesDoc(String categoryName, DocumentCategories docCategoryObj) {
        ArrayList<DocumentInfo> documentInfos = new ArrayList<>();
        if (docCategoryObj != null) {
            int size = docCategoryObj.getSubCategories().size();
            for (int i = 0; i < size; i++) {
                SubCategory subCategoryObj = docCategoryObj.getSubCategories().get(i);
                DocumentInfo item = new DocumentInfo();
                item.setTag(subCategoryObj.getId());
                item.setDocName(subCategoryObj.getCategoryName());
                item.setParentCatName(categoryName);
                item.setParentId(Integer.parseInt(subCategoryObj.getParentId()));
                documentInfos.add(item);
            }
        }
        return documentInfos;
    }

    public static HashMap<String, ActiveDocumentMetrics> getActiveDocumentMetrics() {

        ApplicationController controller = ApplicationController.getInstance();
        if(controller.getActiveDocumentMetricsHashMap() == null) {
            HashMap<String, ActiveDocumentMetrics> activeDocumentMetricsHashMap = new HashMap<>();
            activeDocumentMetricsHashMap.put(Constants.FINANCE_APPLICATION_FORM, new ActiveDocumentMetrics(1, 2));
            activeDocumentMetricsHashMap.put(Constants.ID_PROOFS, new ActiveDocumentMetrics(0, 1));
            activeDocumentMetricsHashMap.put(Constants.ADDRESS_PROOFS, new ActiveDocumentMetrics(0, 1));
            activeDocumentMetricsHashMap.put(Constants.INCOME_PROOFS, new ActiveDocumentMetrics(0, 1));
            activeDocumentMetricsHashMap.put(Constants.PAN_CARD, new ActiveDocumentMetrics(0, 1));
            activeDocumentMetricsHashMap.put(Constants.RC_COPY, new ActiveDocumentMetrics(0, 1));
            activeDocumentMetricsHashMap.put(Constants.RTO_DOCUMENTS, new ActiveDocumentMetrics(0, 0));
            activeDocumentMetricsHashMap.put(Constants.SUPPORTING_DOCUMENTS, new ActiveDocumentMetrics(0, 0));
            activeDocumentMetricsHashMap.put(Constants.OTHER_DOCS, new ActiveDocumentMetrics(0, 0));
            controller.setActiveDocumentMetricsHashMap(activeDocumentMetricsHashMap);
        }
        return controller.getActiveDocumentMetricsHashMap();
    }

    public static void makeLoanListRequest(LoanList type, int pageNo, Callback<?> callback, String month, String year) {

        try {
            HashMap<String, String> params = new HashMap<>();
            params.put(Constants.PAGE_NUMBER, pageNo + "");
            params.put(Constants.MONTH, month);
            params.put(Constants.YEAR, year);

            switch (type) {
                case ACTIVE:
                    params.put(Constants.LOAN_CASE_STATUS_KEY, "0");
                    RetrofitRequest.getLoanCasesList(params, (Callback<FinanceLoanCasesListModel>) callback);
                    break;

                case COMPLETED:
                    params.put(Constants.LOAN_CASE_STATUS_KEY, "1");
                    RetrofitRequest.getLoanCasesList(params, (Callback<FinanceLoanCasesListModel>) callback);
                    break;



            }
        } catch (Exception e) {
            GCLog.e(Constants.TAG, "Exception : " + e.getMessage());
        }
    }

    /*public static void makeLoanCasesRequest(LoanType type, int pageNo, Callback<?> callback) {

        try {
            HashMap<String, String> params = new HashMap<>();
            params.put(Constants.PAGE_NUMBER, pageNo + "");
            switch (type) {
                case PENDING_CASE:
                    params.put(Constants.LOAN_CASE_STATUS_KEY, "0");
                    RetrofitRequest.getPendingLoanCasesList(params, (Callback<PendingLoanCaseModel>) callback);
                    break;

                case APPROVED_CASE:
                    params.put(Constants.LOAN_CASE_STATUS_KEY, "1");
                    RetrofitRequest.getApprovedLoanCasesList(params, (Callback<ApprovedLoanCaseModel>) callback);
                    break;

                case REJECTED_CASE:
                    params.put(Constants.LOAN_CASE_STATUS_KEY, "2");
                    RetrofitRequest.getRejectedLoanCasesList(params, (Callback<RejectedLoanCaseModel>) callback);
                    break;

            }
        } catch (Exception e) {
            GCLog.e(Constants.TAG, "Exception : " + e.getMessage());
        }
    }*/

    private static JSONObject getLoanParams(Context context) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Constants.METHOD_LABEL, Constants.LOAN_CASE_LIST);
            jsonObject.put(Constants.DEALER_USERNAME, CommonUtils.getStringSharedPreference(context, Constants.UC_DEALER_USERNAME, ""));
            jsonObject.put(Constants.DEALER_PASSWORD, CommonUtils.getStringSharedPreference(context, Constants.UC_DEALER_PASSWORD, ""));
            return jsonObject;
        } catch (Exception e) {
            GCLog.e(Constants.TAG, "Exception : " + e.getMessage());
        }
        return jsonObject;
    }

    public static void getBanksData(final boolean syncAll) {
        RetrofitRequest.getBanksData(new Callback<BanksDataResponse>() {
            @Override
            public void success(BanksDataResponse banksDataResponse, Response response) {
                //GCLog.e(Constants.TAG, "No of Banks : " + banksDataResponse.getBankDatas().length);
                ApplicationController.getFinanceDB().insertBankData(Arrays.asList(banksDataResponse.getBankDatas()));
                if(syncAll)
                    getCompaniesData(syncAll);
            }

            @Override
            public void failure(RetrofitError error) {
                GCLog.e(Constants.TAG, " Retrofit error ");
            }
        });
    }

    public static void getCompaniesData(final boolean syncAll) {
        RetrofitRequest.getCompaniesData(new Callback<CompaniesDataResponse>() {

            @Override
            public void success(CompaniesDataResponse companiesDataResponse, Response response) {
                //GCLog.e(Constants.TAG, "No of companies " + companiesDataResponse.getCompaniesDatas().length);
                ApplicationController.getFinanceDB().insertCompaniesData(Arrays.asList(companiesDataResponse.getCompaniesDatas()));
                if (syncAll)
                    getEmploymentData(syncAll);
            }

            @Override
            public void failure(RetrofitError error) {
                GCLog.e(Constants.TAG, "Retrofit Error");
            }
        });
    }

    public static void getEmploymentData(final boolean syncAll) {

        RetrofitRequest.getEmploymentData(new Callback<EmploymentDataResponse>() {
            @Override
            public void success(EmploymentDataResponse employmentDataResponse, Response response) {
                //GCLog.e(Constants.TAG, "Employment Data Size : " + employmentDataResponse.getEmploymentDatas().length);
                ApplicationController.getFinanceDB().insertEmploymentData(Arrays.asList(employmentDataResponse.getEmploymentDatas()));
                if (syncAll)
                    getIndustriesData(syncAll);
            }

            @Override
            public void failure(RetrofitError error) {
                GCLog.e(Constants.TAG, "Retrofit error ");
            }
        });
    }

    private static void getIndustriesData(boolean syncAll) {
        RetrofitRequest.getIndustriesData(new Callback<IndustriesDataResponse>() {
            @Override
            public void success(IndustriesDataResponse dataResponse, Response response) {
                ApplicationController.getFinanceDB().insertIndustriesData(dataResponse.getIndustriesList());

            }

            @Override
            public void failure(RetrofitError error) {
                GCLog.e(Constants.TAG, "Retrofit error ");
            }
        });
    }

    public static void performSync() {
        getBanksData(true);
//        getEmploymentData();
//        getCompaniesData();
    }

    public enum LoanType {PENDING_CASE, APPROVED_CASE, REJECTED_CASE}


    public enum LoanList {ACTIVE, COMPLETED}

}
