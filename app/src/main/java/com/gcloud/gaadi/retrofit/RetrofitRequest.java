package com.gcloud.gaadi.retrofit;

import android.content.Context;

import com.gcloud.gaadi.ApplicationController;
import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.insurance.InsuranceDashboardModel;
import com.gcloud.gaadi.lms.LMSServerDataPullModel;
import com.gcloud.gaadi.lms.LMSServerDataPushModel;
import com.gcloud.gaadi.model.AddLeadModel;
import com.gcloud.gaadi.model.ApprovedLoanCaseModel;
import com.gcloud.gaadi.model.AvailableCarsModel;
import com.gcloud.gaadi.model.CallTrackModel;
import com.gcloud.gaadi.model.CertificationDetailModel;
import com.gcloud.gaadi.model.CityModel;
import com.gcloud.gaadi.model.CommentsModel;
import com.gcloud.gaadi.model.DealerToDealerNoChangeModel;
import com.gcloud.gaadi.model.DocumentCategories;
import com.gcloud.gaadi.model.Finance.BanksDataResponse;
import com.gcloud.gaadi.model.Finance.CompaniesDataResponse;
import com.gcloud.gaadi.model.Finance.EmploymentDataResponse;
import com.gcloud.gaadi.model.Finance.FinanceDashboardResponse;
import com.gcloud.gaadi.model.Finance.FinanceFormsRequestData;
import com.gcloud.gaadi.model.Finance.FinanceLoanCasesListModel;
import com.gcloud.gaadi.model.Finance.GetReuploadItemsResponse;
import com.gcloud.gaadi.model.Finance.IndustriesDataResponse;
import com.gcloud.gaadi.model.Finance.LoanOffersResponse;
import com.gcloud.gaadi.model.Finance.UpdateFinanceLeadModel;
import com.gcloud.gaadi.model.FinanceData;
import com.gcloud.gaadi.model.GeneralResponse;
import com.gcloud.gaadi.model.ImageUploadResponse;
import com.gcloud.gaadi.model.InspectedCarsInsuranceModel;
import com.gcloud.gaadi.model.InsuranceAvailabilityModel;
import com.gcloud.gaadi.model.InsuranceCustomerDetailsModel;
import com.gcloud.gaadi.model.InsuranceQuotesModel;
import com.gcloud.gaadi.model.InventoriesModel;
import com.gcloud.gaadi.model.InventoryImageDeleteResponse;
import com.gcloud.gaadi.model.LeadsModel;
import com.gcloud.gaadi.model.LoginUserModel;
import com.gcloud.gaadi.model.Make;
import com.gcloud.gaadi.model.MakePremiumModel;
import com.gcloud.gaadi.model.Model;
import com.gcloud.gaadi.model.NewInsuranceCaseModel;
import com.gcloud.gaadi.model.PasswordChangeModel;
import com.gcloud.gaadi.model.PaymentDetailsModel;
import com.gcloud.gaadi.model.PendingLoanCaseModel;
import com.gcloud.gaadi.model.RSAAvailabilityModel;
import com.gcloud.gaadi.model.RSACustomerInfoResponseModel;
import com.gcloud.gaadi.model.RSALogEventModel;
import com.gcloud.gaadi.model.RTOModel;
import com.gcloud.gaadi.model.RejectedLoanCaseModel;
import com.gcloud.gaadi.model.SellerLeadsDetailModel;
import com.gcloud.gaadi.model.ShowroomModel;
import com.gcloud.gaadi.model.StockAddModel;
import com.gcloud.gaadi.model.StockDetailModel;
import com.gcloud.gaadi.model.StocksModel;
import com.gcloud.gaadi.model.UpdatePriceModel;
import com.gcloud.gaadi.model.UpgradeCheckModel;
import com.gcloud.gaadi.model.UsedCarValuationModel;
import com.gcloud.gaadi.model.ValidCarRSAModel;
import com.gcloud.gaadi.model.VariantColorsModel;
import com.gcloud.gaadi.model.Version;
import com.gcloud.gaadi.model.ViewCertifiedCarModel;
import com.gcloud.gaadi.model.WarrantyDetailModel;
import com.gcloud.gaadi.rsa.RSAModel.RSACarsModel;
import com.gcloud.gaadi.rsa.RSAModel.RSADashboardResponseModel;
import com.gcloud.gaadi.utils.CommonUtils;
import com.gcloud.gaadi.utils.GCLog;
import com.gcloud.gaadi.utils.LogFile;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;

import retrofit.Callback;
import retrofit.mime.TypedFile;

/**
 * Created by Gaurav on 15-05-2015.
 */
public class RetrofitRequest {

    private static RequestInterface requestInterface = RetrofitAdapter.getRestAdapter().create(RequestInterface.class);

    private static RequestInterface evaluationRequestInterface = RetrofitAdapter.getEvaluationRestAdapter().create(RequestInterface.class);

    private static RequestInterface imageUploadRequestInterface = RetrofitAdapter.getImageUploadRestAdapter().create(RequestInterface.class);

    private static RequestInterface baseInterface = RetrofitAdapter.getBaseAdapter(ApplicationController.getInstance()).create(RequestInterface.class);

    private static RequestInterface truPriceRequestInterface = RetrofitAdapter.getTruPriceRestAdapter().create(RequestInterface.class);


    public static void makeCityRequest(Context context,
                                       HashMap<String, String> map, Callback<CityModel> cb) {
        requestInterface.makeCityRequest(RetrofitAdapter.getUrlPath(context),
                new RetrofitFieldMap(context, map).getParams(),
                cb);
    }

    public static void makePasswordChangeRequest(Context context,
                                                 HashMap<String, String> map, Callback<PasswordChangeModel> cb) {
        requestInterface.makePasswordChangeRequest(RetrofitAdapter.getUrlPath(context),
                new RetrofitFieldMap(context, map).getParams(),
                cb);
    }

    public static void makeDealerToDealerNoChangeRequest(Context context,
                                                         HashMap<String, String> map, Callback<DealerToDealerNoChangeModel> cb) {
        requestInterface.makeDealerToDealerNoChangeRequest(RetrofitAdapter.getUrlPath(context),
                new RetrofitFieldMap(context, map).getParams(),
                cb);
    }

    public static void makeImageUploadRequest(String filePath, JSONObject jsonObject,
                                              Callback<ImageUploadResponse> cb) throws JSONException {
        GCLog.e("path: " + filePath);
        //String fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
        TypedFile image = new TypedFile("image/*", new File(filePath));
        if (jsonObject == null)
            jsonObject = new JSONObject();
        jsonObject.put(Constants.METHOD_LABEL, Constants.SAVE_FINANCE_DOC_IMAGE);
        jsonObject.put(Constants.DEALER_USERNAME,
                CommonUtils.getStringSharedPreference(ApplicationController.getInstance(),
                        Constants.UC_DEALER_USERNAME, ""));
        jsonObject.put(Constants.DEALER_PASSWORD,
                CommonUtils.getStringSharedPreference(ApplicationController.getInstance(),
                        Constants.UC_DEALER_PASSWORD, ""));
        evaluationRequestInterface
                .makeImageUploadRequest(RetrofitAdapter.getEvaluationPath(),
                        new RetrofitFieldMap(ApplicationController.getInstance(), jsonObject).jsonParams(),
                        image,
                        cb);

    }

    public static ImageUploadResponse makeImageUploadRequestSequential(String filePath, FinanceData financeData) throws JSONException {
        GCLog.e("path: " + filePath);
        LogFile logFile = LogFile.getInstance();
        logFile.createFileOnDevice(true);
        logFile.writeToFile("ImageUploadRequest","File Size : "+new File(filePath).length()+" KB");
        //String fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
        TypedFile image = new TypedFile("image/*", new File(filePath));
        return baseInterface
                .makeImageUploadRequest(Constants.SAVE_FINANCE_DOC_IMAGE, financeData, image);

//        return evaluationRequestInterface
//                .uploadImage(RetrofitAdapter.getEvaluationPath(),
//                        financeData,
//                        image);
    }

    public static ImageUploadResponse stockImagesUploadRequest(String filePath, HashMap<String, String> map) throws JSONException {
        GCLog.e("path: " + filePath);
        //String fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
        TypedFile image = new TypedFile("image/*", new File(filePath));
     /*   if (jsonObject == null)
            jsonObject = new JSONObject();
      //  jsonObject.put(Constants.METHOD_LABEL, Constants.SAVE_FINANCE_DOC_IMAGE);
        jsonObject.put(Constants.DEALER_USERNAME,
                CommonUtils.getStringSharedPreference(ApplicationController.getInstance(),
                        Constants.UC_DEALER_USERNAME, ""));
        jsonObject.put(Constants.DEALER_PASSWORD,
                CommonUtils.getStringSharedPreference(ApplicationController.getInstance(),
                        Constants.UC_DEALER_PASSWORD, ""));*/
        return imageUploadRequestInterface
                .makeStockImageUploadRequest(RetrofitAdapter.getImageUploadPath(),
                        new RetrofitFieldMap(ApplicationController.getInstance(), map).stockImagejsonParams(),
                        image);
    }


    public static void makeMakeRequest(Context context,
                                       HashMap<String, String> map, Callback<Make> cb) {
        requestInterface.makeMakeRequest(RetrofitAdapter.getUrlPath(context),
                new RetrofitFieldMap(context, map).getParams(),
                cb);
    }

    public static void makeModelRequest(Context context,
                                        HashMap<String, String> map, Callback<Model> cb) {
        requestInterface.makeModelRequest(RetrofitAdapter.getUrlPath(context),
                new RetrofitFieldMap(context, map).getParams(),
                cb);
    }


    public static void makeLoginUserRequest(Context context,
                                            HashMap<String, String> map, Callback<LoginUserModel> cb) {
        requestInterface.makeLoginUserRequest(RetrofitAdapter.getUrlPath(context),
                new RetrofitFieldMap(context, map).getParams(),
                cb);
    }

    public static void makeLogNotificationRequest(Context context,
                                                  HashMap<String, String> map, Callback<GeneralResponse> cb) {
        requestInterface.makeLogNotificationRequest(RetrofitAdapter.getUrlPath(context),
                new RetrofitFieldMap(context, map).getParams(),
                cb);
    }

    public static void makeUpgradeRequest(Context context,
                                          HashMap<String, String> map, Callback<UpgradeCheckModel> cb) {
        requestInterface.makeUpgradeRequest(RetrofitAdapter.getUrlPath(context),
                new RetrofitFieldMap(context, map).getParams(),
                cb);
    }

    public static void makeVersionRequest(Context context,
                                          HashMap<String, String> map, Callback<Version> cb) {
        requestInterface.makeVersionRequest(RetrofitAdapter.getUrlPath(context),
                new RetrofitFieldMap(context, map).getParams(),
                cb);
    }

    public static void getInsuranceAvailability(HashMap<String, String> params, Callback<InsuranceAvailabilityModel> cb) {
        requestInterface.getInsuranceAvailability(RetrofitAdapter.getUrlPath(ApplicationController.getInstance()),
                new RetrofitFieldMap(ApplicationController.getInstance(), params).getParams(), cb);
    }

    public static void getLeadData(HashMap<String, String> params, Callback<LeadsModel> cb) {
        requestInterface.getLeadData(RetrofitAdapter.getUrlPath(ApplicationController.getInstance()),
                new RetrofitFieldMap(ApplicationController.getInstance(), params).getParams(), cb);
    }

    public static void getSellerLeads(HashMap<String, String> params, Callback<LeadsModel> cb) {
        requestInterface.getSellerLeads(RetrofitAdapter.getUrlPath(ApplicationController.getInstance()),
                new RetrofitFieldMap(ApplicationController.getInstance(), params).getParams(), cb);
    }

    public static void getSellerLeadsDetails(HashMap<String, String> params, Callback<SellerLeadsDetailModel> cb) {
        requestInterface.getSellerLeadsDetails(RetrofitAdapter.getUrlPath(ApplicationController.getInstance()),
                new RetrofitFieldMap(ApplicationController.getInstance(), params).getParams(), cb);
    }

    public static void updateSellerLeadsDetails(HashMap<String, String> params, Callback<AddLeadModel> cb) {
        requestInterface.updateSellerLeadsDetails(RetrofitAdapter.getUrlPath(ApplicationController.getInstance()),
                new RetrofitFieldMap(ApplicationController.getInstance(), params).getSellerLeadParams(), cb);
    }

    // For Evaluation System
    public static void getRSAAvailability(JSONObject jsonObject, Callback<RSAAvailabilityModel> cb) {
        evaluationRequestInterface.getRSAAvailability(RetrofitAdapter.getEvaluationPath(),
                new RetrofitFieldMap(ApplicationController.getInstance(), jsonObject).jsonParams(), cb);
    }

    public static void getValidCarRSA(JSONObject jsonObject, Callback<ValidCarRSAModel> cb) {
        evaluationRequestInterface.getValidCarRSA(RetrofitAdapter.getEvaluationPath(),
                new RetrofitFieldMap(ApplicationController.getInstance(), jsonObject).jsonParams(), cb);
    }

    public static void postRSACustomerData(HashMap<String, String> map, Callback<RSACustomerInfoResponseModel> cb) {
        requestInterface.postRSACustomerData(RetrofitAdapter.getUrlPath(ApplicationController.getInstance()),
                new RetrofitFieldMap(ApplicationController.getInstance(), map).getParams(), cb);
    }

    public static void getDocumentTagFinance(Callback<DocumentCategories[]> cb) {
        baseInterface.getDocCategoryData(Constants.GET_FINANCE_DOC_INFOS, cb);
    }


    public static void submitFinanceApplication(JSONObject jsonObject, Callback<GeneralResponse> cb) {
        evaluationRequestInterface.submitFinanceApplication(RetrofitAdapter.getEvaluationPath(),
                new RetrofitFieldMap(ApplicationController.getInstance(), jsonObject).jsonParams(), cb);
    }


    public static void getRejectedLoanCasesList(HashMap<String, String> params, Callback<RejectedLoanCaseModel> cb) {
        baseInterface.getRejectedLoanCasesList(Constants.APPLICATION_LIST, params, cb);
    }

    public static void getApprovedLoanCasesList(HashMap<String, String> params, Callback<ApprovedLoanCaseModel> cb) {
        baseInterface.getApprovedLoanCasesList(Constants.APPLICATION_LIST, params, cb);
    }

    public static void getPendingLoanCasesList(HashMap<String, String> params, Callback<PendingLoanCaseModel> cb) {
        baseInterface.getPendingLoanCasesList(Constants.APPLICATION_LIST, params, cb);
    }

    public static void getLoanCasesList(HashMap<String, String> params, Callback<FinanceLoanCasesListModel> cb) {
        baseInterface.getLoanCasesList(Constants.APPLICATION_LIST, params, cb);
    }


    public static void logRSAEvent(JSONObject jsonObject, Callback<RSALogEventModel> cb) {
        evaluationRequestInterface.logRSAEvent(RetrofitAdapter.getEvaluationPath(),
                new RetrofitFieldMap(ApplicationController.getInstance(), jsonObject).jsonParams(), cb);
    }


    // For Insurance System

    public static void getInspectedCarsForInsurance(
            HashMap<String, String> params,
            Callback<InspectedCarsInsuranceModel> cb) {

        requestInterface.getInspectedCarsForInsurance(RetrofitAdapter.getUrlPath(ApplicationController.getInstance()),
                new RetrofitFieldMap(ApplicationController.getInstance(), params).getParams(), cb);
    }

    public static void getInsuranceDashboard(
            HashMap<String, String> params,
            Callback<InsuranceDashboardModel> cb) {

        requestInterface.getInsuranceDashboard(RetrofitAdapter.getUrlPath(ApplicationController.getInstance()),
                new RetrofitFieldMap(ApplicationController.getInstance(), params).getParams(),
                cb);
    }


    public static void putNewInsuranceCase(
            HashMap<String, String> params,
            Callback<NewInsuranceCaseModel> cb) {

        requestInterface.putNewInsuranceCase(RetrofitAdapter.getUrlPath(ApplicationController.getInstance()),
                new RetrofitFieldMap(ApplicationController.getInstance(), params).getParams(), cb);
    }

    public static void getInsuranceQuotes(
            HashMap<String, String> params,
            Callback<InsuranceQuotesModel> cb) {

        requestInterface.getInsuranceQuotes(RetrofitAdapter.getUrlPath(ApplicationController.getInstance()),
                new RetrofitFieldMap(ApplicationController.getInstance(), params).getParams(), cb);
    }


    public static void putCustomerDetailsForInsurance(
            HashMap<String, String> params,
            Callback<InsuranceCustomerDetailsModel> cb) {

        requestInterface.putCustomerDetailsForInsurance(RetrofitAdapter.getUrlPath(ApplicationController.getInstance()),
                new RetrofitFieldMap(ApplicationController.getInstance(), params).getParams(), cb);

    }

    public static void putInsuranceQuoteDetails(
            HashMap<String, String> params,
            Callback<GeneralResponse> cb) {

        requestInterface.putInsuranceQuoteDetails(RetrofitAdapter.getUrlPath(ApplicationController.getInstance()),
                new RetrofitFieldMap(ApplicationController.getInstance(), params).getParams(), cb);

    }


    public static GeneralResponse uploadInsuranceDocument(String filePath,
                                                          JSONObject params) {
        TypedFile file = new TypedFile("image/*", new File(filePath));

        return requestInterface.uploadInsuranceDocuments(RetrofitAdapter.getUrlPath(ApplicationController.getInstance()),
                new RetrofitFieldMap(ApplicationController.getInstance(), params).jsonParams(),
                file);
    }

    public static void putPaymentDetails(HashMap<String, String> params,
                                         Callback<PaymentDetailsModel> cb) {

        requestInterface.putPaymentDetails(RetrofitAdapter.getUrlPath(ApplicationController.getInstance()),
                new RetrofitFieldMap(ApplicationController.getInstance(), params).getParams(), cb);
    }


    public static void makeRTORequest(Context context,
                                      HashMap<String, String> map, Callback<RTOModel> cb) {
        requestInterface.makeRTORequest(RetrofitAdapter.getUrlPath(context),
                new RetrofitFieldMap(context, map).getParams(),
                cb);
    }

    public static LMSServerDataPullModel pullLMSData(Context context,
                                                     HashMap<String, String> params) {
        return requestInterface.pullLMSData(RetrofitAdapter.getUrlPath(context),
                new RetrofitFieldMap(context, params).getParams());
    }

    public static LMSServerDataPushModel pushLMSData(Context context, HashMap<String, String> jsonParams) {
        return requestInterface.pushLMSData(RetrofitAdapter.getUrlPath(context),
                new RetrofitFieldMap(context, jsonParams).getParams());
    }

    public static LeadsModel getBuyerLeads(HashMap<String, String> params) {
        return requestInterface.getBuyerLeads(RetrofitAdapter.getUrlPath(ApplicationController.getInstance()),
                new RetrofitFieldMap(ApplicationController.getInstance(), params).getParams());
    }

    public static GeneralResponse uploadeventTracking(HashMap<String, String> params) {
        return requestInterface.uploadEventTracking(RetrofitAdapter.getUrlPath(ApplicationController.getInstance()),
                new RetrofitFieldMap(ApplicationController.getInstance(), params).getParams());
    }

    public static void addToStock(HashMap<String, String> params, Callback<GeneralResponse> cb) {
        requestInterface.addToStock(RetrofitAdapter.getUrlPath(ApplicationController.getInstance()),
                new RetrofitFieldMap(ApplicationController.getInstance(), params).getParams(),
                cb);
    }


    //Finance Api Methods
    public static void getFinanceStats(Callback<FinanceDashboardResponse> cb) {
        baseInterface.getFinanceCounts(Constants.getFinanceStatsMethod,
                cb);
    }

    public static void getBanksData(Callback<BanksDataResponse> callback) {
        baseInterface.getBanksData(Constants.BANKS_DATA_METHOD, callback);
    }

    public static void getCompaniesData(Callback<CompaniesDataResponse> callback) {
        baseInterface.getCompaniesData(Constants.COMPANIES_METHOD, callback);
    }

    public static void getFinanceAvailableCars(HashMap<String, String> params, Callback<AvailableCarsModel> callback) {
        baseInterface.getFinanceAvailableCars(Constants.AVAILAIBLE_CARS_METHOD, params, callback);
    }

    public static void sendFeedBack(HashMap<String, String> params, Callback<GeneralResponse> cb) {
        requestInterface.sendFeedBack(RetrofitAdapter.getUrlPath(ApplicationController.getInstance()),
                new RetrofitFieldMap(ApplicationController.getInstance(), params).getParams(),
                cb);
    }

    public static void sendTruPriceFeedBack(HashMap<String, String> params, Callback<GeneralResponse> cb) {
        truPriceRequestInterface.sendTruPriceFeedBack(RetrofitAdapter.getTruPriceUrlPath(ApplicationController.getInstance()),
                new RetrofitFieldMap(ApplicationController.getInstance(), params).getTruPriceParams(),
                cb);
    }

    public static void sendUsedCarValuationData(HashMap<String, String> params, Callback<UsedCarValuationModel> cb) {
        truPriceRequestInterface.sendUsedCarValuatinData(RetrofitAdapter.getTruPriceUrlPath(ApplicationController.getInstance()),
                new RetrofitFieldMap(ApplicationController.getInstance(), params).getTruPriceParams(),
                cb);
    }


    public static void getEmploymentData(Callback<EmploymentDataResponse> callback) {
        baseInterface.getEmploymentData(Constants.EMPLOYMENT_METHOD, callback);
    }

    public static void getIndustriesData(Callback<IndustriesDataResponse> callback) {
        baseInterface.getIndustriesData(Constants.INDUSTRY_METHOD, callback);
    }

    public static void makeStockRequest(HashMap<String, String> params, Callback<StocksModel> cb) {
        requestInterface.makeStockRequest(RetrofitAdapter.getUrlPath(ApplicationController.getInstance()),
                new RetrofitFieldMap(ApplicationController.getInstance(), params).getParams(),
                cb);
    }

    public static void registerGCMIdRequest(HashMap<String, String> params, Callback<GeneralResponse> cb) {
        requestInterface.registerGCMIdRequest(RetrofitAdapter.getUrlPath(ApplicationController.getInstance()),
                new RetrofitFieldMap(ApplicationController.getInstance(), params).getParams(),
                cb);
    }

    public static void variantColorsRequest(HashMap<String, String> params, Callback<VariantColorsModel> cb) {
        requestInterface.variantColorsRequest(RetrofitAdapter.getUrlPath(ApplicationController.getInstance()),
                new RetrofitFieldMap(ApplicationController.getInstance(), params).getParams(),
                cb);
    }

    public static void stockViewRequest(HashMap<String, String> params, Callback<StockDetailModel> callback) {
        requestInterface.stockViewRequest(RetrofitAdapter.getUrlPath(ApplicationController.getInstance()),
                new RetrofitFieldMap(ApplicationController.getInstance(), params).getParams(),
                callback);
    }

    public static void callTrackRequest(HashMap<String, String> params, Callback<CallTrackModel> callback) {
        requestInterface.callTrackerRequest(RetrofitAdapter.getUrlPath(ApplicationController.getInstance()),
                new RetrofitFieldMap(ApplicationController.getInstance(), params).getParams(),
                callback);
    }

    public static void postFinanceFormData(FinanceFormsRequestData mRequestData, Callback<LoanOffersResponse> callback) {
        baseInterface.postFinanceFormData(Constants.FINANCE_SAVE_FORM, mRequestData, callback);
    }

    public static void getReuploadCategories(String mFinanceId ,Callback<GetReuploadItemsResponse> callback) {
        baseInterface.getReuploadCategories(Constants.FINANCE_REUPLOAD_ITEMS, mFinanceId, callback);
    }

    public static void postAppliedLoanData(String path, String string,
                                           Callback<UpdateFinanceLeadModel> callback) {

        baseInterface.postAppliedLoanData(path, string, callback);
    }



    public static void LeadsRequest(HashMap<String, String> params, Callback<LeadsModel> cb) {
        requestInterface.LeadsRequest(RetrofitAdapter.getUrlPath(ApplicationController.getInstance()),
                new RetrofitFieldMap(ApplicationController.getInstance(), params).getParams(),
                cb);
    }
    public static void viewCertifiedCarRequest(JSONObject jsonObject, Callback<ViewCertifiedCarModel> cb) {
        evaluationRequestInterface.viewCertifiedCarRequest(RetrofitAdapter.getEvaluationPath(),
                new RetrofitFieldMap(ApplicationController.getInstance(), jsonObject).jsonParams(),
                cb);
    }

    public static void InventoriesRequest(HashMap<String, String> params, Callback<InventoriesModel> cb) {
        requestInterface.InventoriesRequest(RetrofitAdapter.getUrlPath(ApplicationController.getInstance()),
                new RetrofitFieldMap(ApplicationController.getInstance(), params).getParams(),
                cb);
    }

    public static void InactiveInventoriesRequest(HashMap<String, String> params, Callback<InventoriesModel> cb) {
        requestInterface.InactiveInventoriesRequest(RetrofitAdapter.getUrlPath(ApplicationController.getInstance()),
                new RetrofitFieldMap(ApplicationController.getInstance(), params).getParams(),
                cb);
    }
    /* Commented by Gaurav. The implementation seemed wrong
    public static void ViewIssuedWarrantyRequest(JSONObject jsonObject, Callback<ViewCertifiedCarModel> cb) {
        evaluationRequestInterface.viewIssuedWarrantyRequest(RetrofitAdapter.getEvaluationPath(),
                new RetrofitFieldMap(ApplicationController.getInstance(), jsonObject).jsonParams(),
                cb);
    }*/

    public static void WarrantyDetailRequest(JSONObject jsonObject, Callback<WarrantyDetailModel> cb) {
        evaluationRequestInterface.warrantyDetailRequest(RetrofitAdapter.getEvaluationPath(),
                new RetrofitFieldMap(ApplicationController.getInstance(), jsonObject).jsonParams(),
                cb);
    }

    public static void makeCityRequest(HashMap<String, String> params, Callback<CityModel> callback) {
        requestInterface.makeCityRequest(RetrofitAdapter.getUrlPath(ApplicationController.getInstance()),
                new RetrofitFieldMap(ApplicationController.getInstance(), params).getParams(),
                callback);
    }


    public static void EditPriceRequest(HashMap<String, String> params, Callback<UpdatePriceModel> cb) {
        requestInterface.editPriceRequest(RetrofitAdapter.getUrlPath(ApplicationController.getInstance()),
                new RetrofitFieldMap(ApplicationController.getInstance(), params).getParams(),
                cb);
    }

    public static void EditPriceRequestInactive(HashMap<String, String> params, Callback<UpdatePriceModel> cb) {
        requestInterface.editPriceRequestInactive(RetrofitAdapter.getUrlPath(ApplicationController.getInstance()),
                new RetrofitFieldMap(ApplicationController.getInstance(), params).getParams(),
                cb);
    }

    public static void certificationDetailRequest(JSONObject jsonObject, Callback<CertificationDetailModel> cb) {
        evaluationRequestInterface.certificationDetailRequest(RetrofitAdapter.getEvaluationPath(),
                new RetrofitFieldMap(ApplicationController.getInstance(), jsonObject).jsonParams(),
                cb);
    }

    public static void stocksRequest (HashMap<String, String> params, Callback<StocksModel> cb) {
        requestInterface.stocksRequest(RetrofitAdapter.getUrlPath(ApplicationController.getInstance()),
                new RetrofitFieldMap(ApplicationController.getInstance(), params).getParams(),
                cb);
    }

    public static void showroomRequest (HashMap<String, String> params, Callback<ShowroomModel> cb) {
        requestInterface.showroomRequest(RetrofitAdapter.getUrlPath(ApplicationController.getInstance()),
                new RetrofitFieldMap(ApplicationController.getInstance(), params).getParams(),
                cb);
    }

    public static void allCarRequest(HashMap<String, String> params, Callback<InventoriesModel> cb) {
        requestInterface.allCarRequest(RetrofitAdapter.getUrlPath(ApplicationController.getInstance()),
                new RetrofitFieldMap(ApplicationController.getInstance(), params).getParams(),
                cb);
    }

    public static void leadEditRequest(HashMap<String, String> params, Callback<AddLeadModel> cb) {
        requestInterface.leadEditRequest(RetrofitAdapter.getUrlPath(ApplicationController.getInstance()),
                new RetrofitFieldMap(ApplicationController.getInstance(), params).getParams(), cb);
    }

    public static AddLeadModel leadEditRequest(HashMap<String, String> params) {
        return requestInterface.leadEditRequest(RetrofitAdapter.getUrlPath(ApplicationController.getInstance()),
                new RetrofitFieldMap(ApplicationController.getInstance(), params).getParams());
    }

    public static void leadAddRequest(HashMap<String, String> params, Callback<AddLeadModel> cb) {
        requestInterface.leadAddRequest(RetrofitAdapter.getUrlPath(ApplicationController.getInstance()),
                new RetrofitFieldMap(ApplicationController.getInstance(), params).getParams(), cb);
    }

    public static void warrantyCertifiedCars(HashMap<String, String> params, Callback<ViewCertifiedCarModel> callback) {
        evaluationRequestInterface.warrantyCertifiedCars(RetrofitAdapter.getEvaluationPath(),
                new RetrofitFieldMap(ApplicationController.getInstance(), params).getParams(), callback);
    }

    public static void warrantyIssuedRequest(HashMap<String, String> params, Callback<ViewCertifiedCarModel> callback) {
        evaluationRequestInterface.warrantyIssuedRequest(RetrofitAdapter.getEvaluationPath(),
                new RetrofitFieldMap(ApplicationController.getInstance(), params).getParams(), callback);
    }

    public static void stockUpdateRequest(HashMap<String, String> params, Callback<StockAddModel> cb) {
        requestInterface.stockUpdateRequest(RetrofitAdapter.getUrlPath(ApplicationController.getInstance()),
                new RetrofitFieldMap(ApplicationController.getInstance(), params).getParams(), cb);
    }

    public static void stockAddRequest(HashMap<String, String> params, Callback<StockAddModel> cb) {
        requestInterface.stockAddRequest(RetrofitAdapter.getUrlPath(ApplicationController.getInstance()),
                new RetrofitFieldMap(ApplicationController.getInstance(), params).getParams(), cb);
    }

    public static void shareCarsRequest(Context context,
                                        HashMap<String, String> map, Callback<GeneralResponse> cb) {
        requestInterface.shareCarsRequest(RetrofitAdapter.getUrlPath(context),
                new RetrofitFieldMap(context, map).getParams(),
                cb);
    }

    public static void addOnD2DRequest(Context context,
                                       HashMap<String, String> map, Callback<GeneralResponse> cb) {
        requestInterface.addOnD2DRequest(RetrofitAdapter.getUrlPath(context),
                new RetrofitFieldMap(context, map).getParams(),
                cb);
    }
    public static void bringTopRequest(Context context,
                                       HashMap<String, String> map, Callback<GeneralResponse> cb) {
        requestInterface.bringTopRequest(RetrofitAdapter.getUrlPath(context),
                new RetrofitFieldMap(context, map).getParams(),
                cb);
    }

    public static void leadViewRequest(Context context,
                                       HashMap<String, String> map, Callback<GeneralResponse> cb) {
        requestInterface.leadViewRequest(RetrofitAdapter.getUrlPath(context),
                new RetrofitFieldMap(context, map).getParams(),
                cb);
    }
    public static void ShareCarsRequest(Context context,
                                        HashMap<String, String> map, Callback<GeneralResponse> cb) {
        requestInterface.ShareCarsRequest(RetrofitAdapter.getUrlPath(context),
                new RetrofitFieldMap(context, map).getParams(),
                cb);
    }

    public static void ViewCommentsRequest(HashMap<String, String> params, Callback<CommentsModel> cb) {
        requestInterface.ViewCommentsRequest(RetrofitAdapter.getUrlPath(ApplicationController.getInstance()),
                new RetrofitFieldMap(ApplicationController.getInstance(), params).getParams(), cb);
    }

    public static void logNotificationRequest(Context context,
                                              HashMap<String, String> map, Callback<GeneralResponse> cb) {
        requestInterface.logNotificationRequest(RetrofitAdapter.getUrlPath(context),
                new RetrofitFieldMap(context, map).getParams(),
                cb);
    }

    public static void MakePremiumRequest(HashMap<String, String> params, Callback<MakePremiumModel> cb) {
        requestInterface.MakePremiumRequest(RetrofitAdapter.getUrlPath(ApplicationController.getInstance()),
                new RetrofitFieldMap(ApplicationController.getInstance(), params).getParams(), cb);
    }
    public static void makeStockRemoveRequest(Context context,
                                              HashMap<String, String> map, Callback<GeneralResponse> cb) {
        requestInterface.makeStockRemoveRequest(RetrofitAdapter.getUrlPath(context),
                new RetrofitFieldMap(context, map).getParams(),
                cb);
    }

    public static void markInventorySoldRequest(Context context,
                                              HashMap<String, String> map, Callback<GeneralResponse> cb) {
        requestInterface.markInventorySoldRequest(RetrofitAdapter.getUrlPath(context),
                new RetrofitFieldMap(context, map).getParams(),
                cb);
    }

    public static void stocksRequest(JSONObject jsonObject, Callback<ViewCertifiedCarModel> cb) {
        evaluationRequestInterface.stocksRequest(RetrofitAdapter.getEvaluationPath(),
                new RetrofitFieldMap(ApplicationController.getInstance(), jsonObject).jsonParams(),
                cb);
    }

    public static void RemoveFromD2DRequest(Context context,
                                              HashMap<String, String> map, Callback<GeneralResponse> cb) {
        requestInterface.RemoveFromD2DRequest(RetrofitAdapter.getUrlPath(context),
                new RetrofitFieldMap(context, map).getParams(),
                cb);
    }
    public static void emailRequest(Context context,
                                            HashMap<String, String> map, Callback<GeneralResponse> cb) {
        requestInterface.emailRequest(RetrofitAdapter.getUrlPath(context),
                new RetrofitFieldMap(context, map).getParams(),
                cb);
    }

    public static void shareCarsRequestwhatsup(Context context,
                                    HashMap<String, String> map, Callback<GeneralResponse> cb) {
        requestInterface.shareCarsRequestwhatsup(RetrofitAdapter.getUrlPath(context),
                new RetrofitFieldMap(context, map).getParams(),
                cb);
    }

    public static void sendImagesOrderRequest(Context context,
                                               HashMap<String, String> map, Callback<GeneralResponse> cb) {
        requestInterface.sendImagesOrderRequest(RetrofitAdapter.getUrlPath(context),
                new RetrofitFieldMap(context, map).getParams(),
                cb);
    }


    public static void makeRequest(HashMap<String, String> params, Callback<Make> cb) {
        requestInterface.makeRequest(RetrofitAdapter.getUrlPath(ApplicationController.getInstance()),
                new RetrofitFieldMap(ApplicationController.getInstance(), params).getParams(), cb);
    }

    public static void modelRequest(HashMap<String, String> params, Callback<Model> cb) {
        requestInterface.modelRequest(RetrofitAdapter.getUrlPath(ApplicationController.getInstance()),
                new RetrofitFieldMap(ApplicationController.getInstance(), params).getParams(), cb);
    }

    public static void versionRequest(HashMap<String, String> params, Callback<Version> cb) {
        requestInterface.versionRequest(RetrofitAdapter.getUrlPath(ApplicationController.getInstance()),
                new RetrofitFieldMap(ApplicationController.getInstance(), params).getParams(), cb);
    }


    public static void cityRequest(HashMap<String, String> params, Callback<CityModel> cb) {
        requestInterface.cityRequest(RetrofitAdapter.getUrlPath(ApplicationController.getInstance()),
                new RetrofitFieldMap(ApplicationController.getInstance(), params).getParams(), cb);
    }

    public static void photoRequest(HashMap<String, String> params, Callback<InventoryImageDeleteResponse> cb) {
        requestInterface.photoRequest(RetrofitAdapter.getUrlPath(ApplicationController.getInstance()),
                new RetrofitFieldMap(ApplicationController.getInstance(), params).getParams(), cb);
    }

    //RSA API
    public static void getRSAAvailableCars(HashMap<String, String> params, Callback<RSACarsModel> callback) {
        requestInterface.getRSAAvailableCars(RetrofitAdapter.getUrlPath(ApplicationController.getInstance()),
                new RetrofitFieldMap(ApplicationController.getInstance(), params).getParams(),
                callback);
    }

    public static void getRSAStats(HashMap<String, String> params, Callback<RSADashboardResponseModel> callback) {
        requestInterface.getRSACounts(RetrofitAdapter.getUrlPath(ApplicationController.getInstance()),
                new RetrofitFieldMap(ApplicationController.getInstance(), params).getParams(),
                callback);
    }

    public static void makeStockViewRequest(HashMap<String, String> params, Callback<StocksModel> cb) {
        requestInterface.makeStockViewRequest(RetrofitAdapter.getUrlPath(ApplicationController.getInstance()),
                new RetrofitFieldMap(ApplicationController.getInstance(), params).getParams(),
                cb);
    }
}












