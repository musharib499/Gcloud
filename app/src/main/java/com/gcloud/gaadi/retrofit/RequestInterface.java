package com.gcloud.gaadi.retrofit;

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
import com.gcloud.gaadi.model.CustomerDetailModel;
import com.gcloud.gaadi.model.DealerToDealerNoChangeModel;
import com.gcloud.gaadi.model.DocumentCategories;
import com.gcloud.gaadi.model.FilterResponseModel;
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
import com.gcloud.gaadi.model.InsuranceCaseAddedModel;
import com.gcloud.gaadi.model.InsuranceCaseStatusModel;
import com.gcloud.gaadi.model.InsuranceCustomerDetailsModel;
import com.gcloud.gaadi.model.InsuranceImageUploadModel;
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

import java.util.HashMap;
import java.util.Map;

import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FieldMap;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.PartMap;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.http.QueryMap;
import retrofit.mime.TypedFile;

public interface RequestInterface {

    @FormUrlEncoded
    @POST("/{path}")
    void makeCityRequest(@Path("path") String path,
                         @FieldMap Map<String, String> map,
                         Callback<CityModel> cb);

    @FormUrlEncoded
    @POST("/{path}")
    void makePasswordChangeRequest(@Path("path") String path,
                                   @FieldMap Map<String, String> map,
                                   Callback<PasswordChangeModel> cb);

    @FormUrlEncoded
    @POST("/{path}")
    void makeDealerToDealerNoChangeRequest(@Path("path") String path,
                                           @FieldMap Map<String, String> map,
                                           Callback<DealerToDealerNoChangeModel> cb);


    @Multipart
    @POST("/{path}")
    void makeImageUploadRequest(@Path("path") String path,
                                @Part(Constants.EVALUATIONDATA) String keyMap,
                                @Part("financeImg") TypedFile image,
                                Callback<ImageUploadResponse> cb);


    @Multipart
    @POST("/{path}")
    ImageUploadResponse makeStockImageUploadRequest(@Path("path") String path,
                                                    @PartMap Map<String, String> map,
                                                    @Part("stockImg") TypedFile image);


    @FormUrlEncoded
    @POST("/{path}")
    void makeMakeRequest(@Path("path") String path,
                         @FieldMap Map<String, String> map,
                         Callback<Make> cb);

    @FormUrlEncoded
    @POST("/{path}")
    void makeModelRequest(@Path("path") String path,
                          @FieldMap Map<String, String> map,
                          Callback<Model> cb);

    @FormUrlEncoded
    @POST("/{path}")
    void makeLoginUserRequest(@Path("path") String path,
                              @FieldMap Map<String, String> map,
                              Callback<LoginUserModel> cb);


    @FormUrlEncoded
    @POST("/{path}")
    void makeLogNotificationRequest(@Path("path") String path,
                                    @FieldMap Map<String, String> map,
                                    Callback<GeneralResponse> cb);

    @FormUrlEncoded
    @POST("/{path}")
    void makeRTORequest(@Path("path") String path,
                        @FieldMap Map<String, String> map,
                        Callback<RTOModel> cb);

    @FormUrlEncoded
    @POST("/{path}")
    void makeUpgradeRequest(@Path("path") String path,
                            @FieldMap Map<String, String> map,
                            Callback<UpgradeCheckModel> cb);

    @FormUrlEncoded
    @POST("/{path}")
    void makeVersionRequest(@Path("path") String path,
                            @FieldMap Map<String, String> map,
                            Callback<Version> cb);

    @FormUrlEncoded
    @POST("/{path}")
    void getLeadData(
            @Path("path") String urlPath,
            @FieldMap Map<String, String> params,
            Callback<LeadsModel> cb);


    @FormUrlEncoded
    @POST("/{path}")
    void getSellerLeads(
            @Path("path") String urlPath,
            @FieldMap Map<String, String> params,
            Callback<LeadsModel> cb);

    @FormUrlEncoded
    @POST("/{path}")
    void getSellerLeadsDetails(
            @Path("path") String urlPath,
            @FieldMap Map<String, String> params,
            Callback<SellerLeadsDetailModel> cb);

    @FormUrlEncoded
    @POST("/{path}")
    void updateSellerLeadsDetails(
            @Path("path") String urlPath,
            @FieldMap Map<String, String> params,
            Callback<AddLeadModel> cb);
    // For Evaluation System

    @FormUrlEncoded
    @POST("/{path}")
    void getRSAAvailability(@Path("path") String path,
                            @Field(Constants.EVALUATIONDATA) String evaluationData,
                            Callback<RSAAvailabilityModel> cb);


    @FormUrlEncoded
    @POST("/{path}")
    void getValidCarRSA(@Path("path") String path,
                        @Field(Constants.EVALUATIONDATA) String evaluationData,
                        Callback<ValidCarRSAModel> cb);

    @FormUrlEncoded
    @POST("/{path}")
    void postRSACustomerData(@Path("path") String urlPath, @FieldMap Map<String, String> params, Callback<RSACustomerInfoResponseModel> cb);

    @FormUrlEncoded
    @POST("/{path}")
    void getInsuranceAvailability(
            @Path("path") String urlPath,
            @FieldMap Map<String, String> params, Callback<InsuranceAvailabilityModel> cb);

    @FormUrlEncoded
    @POST("/{path}")
    void putFinanceCustomerData(
            @Path("path") String path,
            @Field(Constants.EVALUATIONDATA) String evaluationData,
            Callback<CustomerDetailModel> cb);


    @FormUrlEncoded
    @POST("/{path}")
    void logRSAEvent(
            @Path("path") String path,
            @Field(Constants.EVALUATIONDATA) String params,
            Callback<RSALogEventModel> cb);


    // For insurance system

    @FormUrlEncoded
    @POST("/{path}")
    void getInspectedCarsForInsurance(
            @Path("path") String path,
            @FieldMap Map<String, String> params,
            Callback<InspectedCarsInsuranceModel> cb);

    @FormUrlEncoded
    @POST("/{path}")
    void getExistingInsuranceCases(
            @Path("path") String path,
            @FieldMap Map<String, String> insuranceParams,
            Callback<InsuranceCaseStatusModel> cb);

    @FormUrlEncoded
    @POST("/{path}")
    void putNewInsuranceCase(
            @Path("path") String path,
            @FieldMap Map<String, String> insuranceParams,
            Callback<NewInsuranceCaseModel> cb);

    @FormUrlEncoded
    @POST("/{path}")
    void getInsuranceQuotes(
            @Path("path") String path,
            @FieldMap Map<String, String> insuranceParams,
            Callback<InsuranceQuotesModel> cb);

    @FormUrlEncoded
    @POST("/{path}")
    void putInsuranceCase(
            @Path("path") String path,
            @FieldMap Map<String, String> insuranceParams,
            Callback<InsuranceCaseAddedModel> cb);

    @FormUrlEncoded
    @POST("/{path}")
    void putCustomerDetailsForInsurance(
            @Path("path") String path,
            @FieldMap Map<String, String> insuranceParams,
            Callback<InsuranceCustomerDetailsModel> cb);


    @FormUrlEncoded
    @POST("/{path}")
    void putInsuranceQuoteDetails(
            @Path("path") String path,
            @FieldMap Map<String, String> insuranceParams,
            Callback<GeneralResponse> cb);

    @FormUrlEncoded
    @POST("/{path}")
    void putPaymentDetails(
            @Path("path") String path,
            @FieldMap Map<String, String> insuranceParams,
            Callback<PaymentDetailsModel> cb);

    @Multipart
    @POST("/{path}")
    void putInsuranceDocument(
            @Path("path") String path,
            @Part("filename") TypedFile file,
            @QueryMap Map<String, String> insuranceParams,
            Callback<InsuranceImageUploadModel> cb);

    @FormUrlEncoded
    @POST("/{path}")
    void submitFinanceApplication(
            @Path("path") String path,
            @Field(Constants.EVALUATIONDATA) String params,
            Callback<GeneralResponse> cb);

    @Multipart
    @POST("/{path}")
    GeneralResponse uploadInsuranceDocuments(@Path("path") String path,
                                  @Part("requestdata") String params,
                                             @Part("doc_file") TypedFile file);

    @FormUrlEncoded
    @POST("/{path}")
    void getInsuranceDashboard(@Path("path") String urlPath,
                               @FieldMap Map<String, String> params,
                               Callback<InsuranceDashboardModel> cb);

    @FormUrlEncoded
    @POST("/{path}")
    LMSServerDataPullModel pullLMSData(@Path("path") String urlPath,
                                       @FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("/{path}")
    LMSServerDataPushModel pushLMSData(@Path("path") String urlPath,
                                       @FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("/{path}")
    LeadsModel getBuyerLeads(@Path("path") String urlPath,
                             @FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("/{path}")
    GeneralResponse uploadEventTracking(@Path("path") String urlPath, @FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("/{path}")
    void addToStock(@Path("path") String urlPath, @FieldMap Map<String, String> params, Callback<GeneralResponse> cb);

    @FormUrlEncoded
    @POST("/{path}")
    void sendFeedBack(@Path("path") String urlPath,
                      @FieldMap Map<String, String> params, Callback<GeneralResponse> cb);

    @FormUrlEncoded
    @POST("/{path}")
    void sendTruPriceFeedBack(@Path("path") String urlPath,
                      @FieldMap Map<String, String> params, Callback<GeneralResponse> cb);

    @FormUrlEncoded
    @POST("/{path}")
    void sendUsedCarValuatinData(@Path("path") String urlPath,
                              @FieldMap Map<String, String> params, Callback<UsedCarValuationModel> cb);
    /* Finance Revamp APis Start here */

    @GET("/{path}")
    void getBanksData(@Path("path") String banksDataMethod, Callback<BanksDataResponse> callback);

    @GET("/{path}")
    void getCompaniesData(@Path("path") String companiesMethod, Callback<CompaniesDataResponse> callback);

    @GET("/{path}")
    void getEmploymentData(@Path("path") String employmentMethod, Callback<EmploymentDataResponse> callback);

    @GET("/{path}")
    void getIndustriesData(@Path("path") String employmentMethod, Callback<IndustriesDataResponse> callback);

    @GET("/{path}")
    void getFinanceCounts(@Path("path") String method, Callback<FinanceDashboardResponse> cb);

    @GET("/{path}")
    void getFinanceAvailableCars(@Path("path") String method, @QueryMap HashMap<String, String> params, Callback<AvailableCarsModel> callback);

    @GET("/{path}")
    void getPendingLoanCasesList(@Path("path") String path, @QueryMap HashMap<String, String> params, Callback<PendingLoanCaseModel> cb);

    @GET("/{path}")
    void getLoanCasesList(@Path("path") String path, @QueryMap HashMap<String, String> params, Callback<FinanceLoanCasesListModel> cb);

    @GET("/{path}")
    void getRejectedLoanCasesList(@Path("path") String path, @QueryMap HashMap<String, String> params, Callback<RejectedLoanCaseModel> cb);

    @GET("/{path}")
    void getApprovedLoanCasesList(@Path("path") String path, @QueryMap HashMap<String, String> params, Callback<ApprovedLoanCaseModel> cb);

    @GET("/{path}")
    void getDocCategoryData(@Path("path") String path, Callback<DocumentCategories[]> cb);

    @Multipart
    @POST("/{path}")
    ImageUploadResponse makeImageUploadRequest(@Path("path") String path, @Part(Constants.EVALUATIONDATA) FinanceData financeData, @Part("financeImg") TypedFile image);

    @Multipart
    @POST("/{path}")
    void postFinanceFormData(@Path("path") String financeSaveForm, @Part(Constants.EVALUATIONDATA) FinanceFormsRequestData mRequestData, Callback<LoanOffersResponse> callback);

    @GET("/{path}")
    void getReuploadCategories(@Path("path") String financeReuploadItems, @Query("finance_lead_id") String mFinanceId, Callback<GetReuploadItemsResponse> callback);

    @Multipart
    @POST("/{path}")
    void postAppliedLoanData(@Path("path") String urlPath, @Part(Constants.EVALUATIONDATA) String string,
                             Callback<UpdateFinanceLeadModel> cb);

    /* Finannce Revamp Apis End Here */


    @FormUrlEncoded
    @POST("/{path}")
    void getStockFilters(@Path("path") String urlPath,
                         @FieldMap Map<String, String> params, Callback<FilterResponseModel> cb);

    @FormUrlEncoded
    @POST("/{path}")
    void makeStockRequest(@Path("path") String urlPath,
                          @FieldMap Map<String, String> params, Callback<StocksModel> cb);

    @FormUrlEncoded
    @POST("/{path}")
    void registerGCMIdRequest(
            @Path("path") String urlPath,
            @FieldMap Map<String, String> params,
            Callback<GeneralResponse> cb);

    @FormUrlEncoded
    @POST("/{path}")
    void variantColorsRequest(@Path("path") String urlPath,
                              @FieldMap Map<String, String> params, Callback<VariantColorsModel> cb);


    @FormUrlEncoded
    @POST("/{path}")
    void stockViewRequest(@Path("path") String urlPath,
                          @FieldMap Map<String, String> params, Callback<StockDetailModel> cb);

    @FormUrlEncoded
    @POST("/{path}")
    void callTrackerRequest(@Path("path") String urlPath,
                            @FieldMap Map<String, String> params, Callback<CallTrackModel> cb);

    @FormUrlEncoded
    @POST("/{path}")
    void LeadsRequest(@Path("path") String urlPath,
                            @FieldMap Map<String, String> params, Callback<LeadsModel> cb);

    @FormUrlEncoded
    @POST("/{path}")
    void viewCertifiedCarRequest(@Path("path") String urlPath,
                      @Field (Constants.EVALUATIONDATA) String evaluationData,Callback<ViewCertifiedCarModel>cb);

    @FormUrlEncoded
    @POST("/{path}")
    void viewIssuedWarrantyRequest(@Path("path") String urlPath,
                                 @Field (Constants.EVALUATIONDATA) String evaluationData,Callback<ViewCertifiedCarModel>cb);



    @FormUrlEncoded
    @POST("/{path}")
    void warrantyDetailRequest(@Path("path") String urlPath,
                                   @Field (Constants.EVALUATIONDATA) String evaluationData,Callback<WarrantyDetailModel>cb);


    @FormUrlEncoded
    @POST("/{path}")
    void InventoriesRequest(@Path("path") String urlPath,
                      @FieldMap Map<String, String> params, Callback<InventoriesModel> cb);

    @FormUrlEncoded
    @POST("/{path}")
    void InactiveInventoriesRequest(@Path("path") String urlPath,
                            @FieldMap Map<String, String> params, Callback<InventoriesModel> cb);

    @FormUrlEncoded
    @POST("/{path}")
    void editPriceRequest(@Path("path") String urlPath,
                            @FieldMap Map<String, String> params, Callback<UpdatePriceModel> cb);
    @FormUrlEncoded
    @POST("/{path}")
    void editPriceRequestInactive(@Path("path") String urlPath,
                          @FieldMap Map<String, String> params, Callback<UpdatePriceModel> cb);

    @FormUrlEncoded
    @POST("/{path}")
    void certificationDetailRequest(@Path("path") String urlPath,
                                   @Field (Constants.EVALUATIONDATA) String evaluationData,Callback<CertificationDetailModel>cb);
    @FormUrlEncoded
    @POST("/{path}")
    void stocksRequest(@Path("path") String urlPath,
                          @FieldMap Map<String, String> params, Callback<StocksModel> cb);

    @FormUrlEncoded
    @POST("/{path}")
    void showroomRequest(@Path("path") String urlPath,
                       @FieldMap Map<String, String> params, Callback<ShowroomModel> cb);


    @FormUrlEncoded
    @POST("/{path}")
    void allCarRequest(@Path("path") String urlPath,
                       @FieldMap Map<String, String> params, Callback<InventoriesModel> cb);

    @FormUrlEncoded
    @POST("/{path}")
    void leadEditRequest(@Path("path") String urlPath, @FieldMap Map<String, String> params,
            Callback<AddLeadModel> cb);

    @FormUrlEncoded
    @POST("/{path}")
    AddLeadModel leadEditRequest(@Path("path") String urlPath, @FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("/{path}")
    void leadAddRequest(@Path("path") String urlPath, @FieldMap Map<String, String> params,
            Callback<AddLeadModel> cb);

    @FormUrlEncoded
    @POST("/{path}")
    void warrantyCertifiedCars(@Path("path") String evaluationPath,
                               @FieldMap Map<String, String> params, Callback<ViewCertifiedCarModel> callback);

    @FormUrlEncoded
    @POST("/{path}")
    void warrantyIssuedRequest(@Path("path") String evaluationPath,
                               @FieldMap Map<String, String> params, Callback<ViewCertifiedCarModel> callback);

    @FormUrlEncoded
    @POST("/{path}")
    void stockUpdateRequest(@Path("path") String evaluationPath,
                            @FieldMap Map<String, String> params, Callback<StockAddModel> callback);

    @FormUrlEncoded
    @POST("/{path}")
    void stockAddRequest(@Path("path") String evaluationPath,
                         @FieldMap Map<String, String> params, Callback<StockAddModel> callback);

    @FormUrlEncoded
    @POST("/{path}")
    void shareCarsRequest(@Path("path") String path,
                          @FieldMap Map<String, String> map,
                          Callback<GeneralResponse> cb);

    @FormUrlEncoded
    @POST("/{path}")
    void addOnD2DRequest(@Path("path") String path,
                         @FieldMap Map<String, String> map,
                         Callback<GeneralResponse> cb);

    @FormUrlEncoded
    @POST("/{path}")
    void bringTopRequest(@Path("path") String path,
                         @FieldMap Map<String, String> map,
                         Callback<GeneralResponse> cb);

    @FormUrlEncoded
    @POST("/{path}")
    void leadViewRequest(@Path("path") String path,
                         @FieldMap Map<String, String> map,
                         Callback<GeneralResponse> cb);

    @FormUrlEncoded
    @POST("/{path}")
    void ShareCarsRequest(@Path("path") String path,
                          @FieldMap Map<String, String> map,
                          Callback<GeneralResponse> cb);


    @FormUrlEncoded
    @POST("/{path}")
    void ViewCommentsRequest(@Path("path") String evaluationPath,
                             @FieldMap Map<String, String> params, Callback<CommentsModel> callback);


    @FormUrlEncoded
    @POST("/{path}")
    void logNotificationRequest(@Path("path") String path,
                                @FieldMap Map<String, String> map,
                                Callback<GeneralResponse> cb);

    @FormUrlEncoded
    @POST("/{path}")
    void MakePremiumRequest(@Path("path") String evaluationPath,
                            @FieldMap Map<String, String> params, Callback<MakePremiumModel> callback);


    @FormUrlEncoded
    @POST("/{path}")
    void makeStockRemoveRequest(@Path("path") String path,
                                @FieldMap Map<String, String> map,
                                Callback<GeneralResponse> cb);

    @FormUrlEncoded
    @POST("/{path}")
    void markInventorySoldRequest(@Path("path") String path,
                                  @FieldMap Map<String, String> map,
                                  Callback<GeneralResponse> cb);


    @FormUrlEncoded
    @POST("/{path}")
    void stocksRequest(@Path("path") String urlPath,
                                    @Field(Constants.EVALUATIONDATA) String evaluationData, Callback<ViewCertifiedCarModel> cb);

    @FormUrlEncoded
    @POST("/{path}")
    void RemoveFromD2DRequest(@Path("path") String path,
                              @FieldMap Map<String, String> map,
                              Callback<GeneralResponse> cb);
    @FormUrlEncoded
    @POST("/{path}")
    void emailRequest(@Path("path") String path,
                      @FieldMap Map<String, String> map,
                      Callback<GeneralResponse> cb);
    @FormUrlEncoded
    @POST("/{path}")
    void shareCarsRequestwhatsup(@Path("path") String path,
                                 @FieldMap Map<String, String> map,
                                 Callback<GeneralResponse> cb);

    @FormUrlEncoded
    @POST("/{path}")
    void sendImagesOrderRequest(@Path("path") String path,
                                @FieldMap Map<String, String> map,
                                Callback<GeneralResponse> cb);
    @FormUrlEncoded
    @POST("/{path}")
    void makeRequest(@Path("path") String evaluationPath,
                            @FieldMap Map<String, String> params, Callback<Make> callback);

    @FormUrlEncoded
    @POST("/{path}")
    void modelRequest(@Path("path") String evaluationPath,
                     @FieldMap Map<String, String> params, Callback<Model> callback);
    @FormUrlEncoded
    @POST("/{path}")
    void versionRequest(@Path("path") String evaluationPath,
                      @FieldMap Map<String, String> params, Callback<Version> callback);
    @FormUrlEncoded
    @POST("/{path}")
    void cityRequest(@Path("path") String evaluationPath,
                        @FieldMap Map<String, String> params, Callback<CityModel> callback);

    @FormUrlEncoded
    @POST("/{path}")
    void photoRequest(@Path("path") String evaluationPath,
                     @FieldMap Map<String, String> params, Callback<InventoryImageDeleteResponse> callback);

    //RSA
    @FormUrlEncoded
    @POST("/{path}")
    void getRSACounts(@Path("path") String method,
                      @FieldMap Map<String, String> params, Callback<RSADashboardResponseModel> cb);

    @FormUrlEncoded
    @POST("/{path}")
    void getRSAAvailableCars(@Path("path") String method,
                             @FieldMap Map<String, String> params, Callback<RSACarsModel> cb);

    @FormUrlEncoded
    @POST("/{path}")
    void makeStockViewRequest(@Path("path") String urlPath,
                              @FieldMap Map<String, String> params, Callback<StocksModel> cb);
}
