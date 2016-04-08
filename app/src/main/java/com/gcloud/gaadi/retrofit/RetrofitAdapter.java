package com.gcloud.gaadi.retrofit;

import android.content.Context;
import android.util.Base64;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.gcloud.gaadi.ApplicationController;
import com.gcloud.gaadi.BuildConfig;
import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.utils.CommonUtils;
import com.gcloud.gaadi.utils.GAHelper;
import com.gcloud.gaadi.utils.GCLog;
import com.squareup.okhttp.ConnectionPool;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.android.MainThreadExecutor;

public class RetrofitAdapter {

    public static RestAdapter getRestAdapter() {
        String serviceUrl = Constants.getWebServiceURL(ApplicationController.getInstance());
        String urlBase = serviceUrl.substring(0, serviceUrl.lastIndexOf("/"));
        //String urlPath = serviceUrl.substring(serviceUrl.lastIndexOf("/")+1);

        final OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.setConnectTimeout(Constants.CONNECT_TIMEOUT_SECS, TimeUnit.SECONDS);
        okHttpClient.setWriteTimeout(Constants.WRITE_TIMEOUT_SECS, TimeUnit.SECONDS);
        okHttpClient.setReadTimeout(Constants.READ_TIMEOUT_SECS, TimeUnit.SECONDS);
        okHttpClient.setConnectionPool(new ConnectionPool(Constants.CONNECTION_POOL_SIZE, Constants.CONNECTION_MAX_IDLE_TIME_MS));
        okHttpClient.setRetryOnConnectionFailure(true);

        okHttpClient.interceptors().add(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();

                // try the request
                Response response = chain.proceed(request);

                int tryCount = 0;

                CustomEvent customEvent = new CustomEvent("Network Performance");
                customEvent.putCustomAttribute("URL", request.url().toString());
                //customEvent.putCustomAttribute("HEADERS", request.headers().toString());
                //customEvent.putCustomAttribute("BODY", request.body().toString());
                customEvent.putCustomAttribute("STATUS CODE", response.code());
                customEvent.putCustomAttribute("NETWORK TYPE", CommonUtils.getNetworkType());
                customEvent.putCustomAttribute("RETRY COUNT", tryCount);

                Answers.getInstance().logCustom(customEvent);

                while (!response.isSuccessful() && tryCount < 3) {

                    GCLog.d("Request is not successful - " + tryCount);

                    tryCount++;

                    try {
                        CustomEvent customEventS = new CustomEvent("Network Performance");
                        customEventS.putCustomAttribute("URL", request.url().toString());
                        //customEventS.putCustomAttribute("HEADERS", request.headers().toString());
                        //customEventS.putCustomAttribute("BODY", request.body().toString());
                        customEventS.putCustomAttribute("STATUS CODE", response.code());
                        customEventS.putCustomAttribute("NETWORK TYPE", CommonUtils.getNetworkType());
                        customEventS.putCustomAttribute("RETRY COUNT", tryCount);

                        Answers.getInstance().logCustom(customEventS);

                        ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER, getClass().getSimpleName(), request.urlString(), response.message(), response.code() + "  ::  " + tryCount, 0);

                    } catch (Exception e) {
                        Crashlytics.logException(e.getCause());
                        CustomEvent customEventF = new CustomEvent("Network Performance");
                        customEventF.putCustomAttribute("URL", request.url().toString());
                        //customEventF.putCustomAttribute("HEADERS", request.headers().toString());
                        //customEventF.putCustomAttribute("BODY", request.body().toString());
                        customEventF.putCustomAttribute("ERROR", e.getMessage());
                        customEventF.putCustomAttribute("NETWORK TYPE", CommonUtils.getNetworkType());
                        customEventF.putCustomAttribute("RETRY COUNT", tryCount);

                        Answers.getInstance().logCustom(customEventF);
                    }


                    // retry the request
                    response = chain.proceed(request);
                }

                // otherwise just pass the original response on
                return response;
            }
        });

        return new RestAdapter.Builder()
                .setEndpoint(urlBase)
                .setExecutors(Executors.newFixedThreadPool(Constants.CONNECTION_POOL_SIZE), new MainThreadExecutor())
                .setClient(new CustomOKHttpClient(okHttpClient))
                .setLogLevel(!Constants.ENVIRONMENT.equals("PROD") ? RestAdapter.LogLevel.FULL : RestAdapter.LogLevel.NONE)
                .build();
    }

    public static RestAdapter getBaseAdapter(Context context) {
        String serviceUrl = BuildConfig.FINANCE_REST_HOST;

        final OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.setConnectTimeout(Constants.CONNECT_TIMEOUT_SECS, TimeUnit.SECONDS);
        okHttpClient.setWriteTimeout(Constants.WRITE_TIMEOUT_SECS, TimeUnit.SECONDS);
        okHttpClient.setReadTimeout(Constants.READ_TIMEOUT_SECS, TimeUnit.SECONDS);
        okHttpClient.setConnectionPool(new ConnectionPool(Constants.CONNECTION_POOL_SIZE_FINANCE, Constants.CONNECTION_MAX_IDLE_TIME_MS));

        okHttpClient.interceptors().add(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();

                // try the request
                Response response = chain.proceed(request);

                int tryCount = 0;

                CustomEvent customEvent = new CustomEvent("Network Performance");
                customEvent.putCustomAttribute("URL", request.urlString().toString().length() > 100 ? request.urlString().substring(0, 100) : request.urlString().toString());
                //customEvent.putCustomAttribute("HEADERS", request.headers().toString());
                //customEvent.putCustomAttribute("BODY", request.body() != null ? request.body().toString() : "empty");
                customEvent.putCustomAttribute("STATUS CODE", response.code());
                customEvent.putCustomAttribute("NETWORK TYPE", CommonUtils.getNetworkType());
                customEvent.putCustomAttribute("RETRY COUNT", tryCount);

                Answers.getInstance().logCustom(customEvent);

                while (!response.isSuccessful() && tryCount < 3) {

                    GCLog.d("Request is not successful - " + tryCount);

                    tryCount++;

                    try {
                        CustomEvent customEventS = new CustomEvent("Network Performance");
                        customEventS.putCustomAttribute("URL", request.url().toString());
                        //customEventS.putCustomAttribute("HEADERS", request.headers().toString());
                        //customEventS.putCustomAttribute("BODY", request.body() != null ? request.body().toString() : "empty");
                        customEventS.putCustomAttribute("STATUS CODE", response.code());
                        customEventS.putCustomAttribute("NETWORK TYPE", CommonUtils.getNetworkType());
                        customEventS.putCustomAttribute("RETRY COUNT", tryCount);

                        Answers.getInstance().logCustom(customEventS);

                        ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER, getClass().getSimpleName(), request.urlString(), response.message(), response.code() + "  ::  " + tryCount, 0);
                    } catch (Exception e) {
                        Crashlytics.logException(e.getCause());

                        CustomEvent customEventF = new CustomEvent("Network Performance");
                        customEventF.putCustomAttribute("URL", request.url().toString());
                        //customEventF.putCustomAttribute("HEADERS", request.headers().toString());
                        //customEventF.putCustomAttribute("BODY", request.body() != null ? request.body().toString() : "empty");
                        customEventF.putCustomAttribute("ERROR", e.getMessage());
                        customEventF.putCustomAttribute("NETWORK TYPE", CommonUtils.getNetworkType());
                        customEventF.putCustomAttribute("RETRY COUNT", tryCount);

                        Answers.getInstance().logCustom(customEventF);

                    }


                    // retry the request
                    response = chain.proceed(request);
                }

                // otherwise just pass the original response on
                return response;
            }
        });

        return new RestAdapter.Builder()
                .setEndpoint(serviceUrl)
                .setClient(new CustomOKHttpClient(okHttpClient))
                .setLogLevel(!Constants.ENVIRONMENT.equals("PROD") ? RestAdapter.LogLevel.FULL : RestAdapter.LogLevel.NONE)
                .setRequestInterceptor(new RequestInterceptor() {
                    @Override
                    public void intercept(RequestFacade request) {
                        ApplicationController instance = ApplicationController.getInstance();
                        String username = CommonUtils.getStringSharedPreference(instance, Constants.UC_DEALER_USERNAME, "");
                        String password = CommonUtils.getStringSharedPreference(instance, Constants.UC_DEALER_PASSWORD, "");
                        String authetication = "";
                        try {
                            byte[] data = (username + ":" + password).getBytes("UTF-8");
                            authetication = Base64.encodeToString(data, Base64.DEFAULT);
                        } catch (UnsupportedEncodingException e) {
                            //e.printStackTrace();
                        }
                        request.addHeader(Constants.ANDROIDID, CommonUtils.getStringSharedPreference(instance, Constants.ANDROID_ID, ""));
                        request.addHeader(Constants.APPVERSION, CommonUtils.getStringSharedPreference(instance, Constants.APP_VERSION_CODE, ""));
                        request.addHeader(Constants.AUTHENTICATION, authetication);
                        request.addHeader(Constants.PACKAGENAME, Constants.PACKAGE_FINANCE);
                        request.addHeader(Constants.UCDID, CommonUtils.getIntSharedPreference(instance, Constants.UC_DEALER_ID, -1) + "");
                        request.addHeader(Constants.API_KEY_LABEL, Constants.API_KEY);
                    }
                })
                .build();
    }


    public static RestAdapter getEvaluationRestAdapter() {
        String serviceUrl = Constants.getWarrantyWebServiceURL(ApplicationController.getInstance());
        GCLog.e(Constants.TAG, "warranty service url: " + serviceUrl);
        String urlBase = serviceUrl.substring(0, serviceUrl.lastIndexOf("/"));

        final OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.setConnectTimeout(Constants.CONNECT_TIMEOUT_SECS, TimeUnit.SECONDS);
        okHttpClient.setWriteTimeout(Constants.WRITE_TIMEOUT_SECS, TimeUnit.SECONDS);
        okHttpClient.setReadTimeout(Constants.READ_TIMEOUT_SECS, TimeUnit.SECONDS);
        okHttpClient.setConnectionPool(new ConnectionPool(Constants.CONNECTION_POOL_SIZE_EVALUATOR, Constants.CONNECTION_MAX_IDLE_TIME_MS));

        okHttpClient.interceptors().add(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();

                // try the request
                Response response = chain.proceed(request);

                int tryCount = 0;

                CustomEvent customEvent = new CustomEvent("Network Performance");
                customEvent.putCustomAttribute("URL", request.url().toString());
                //customEvent.putCustomAttribute("HEADERS", request.headers().toString());
                //customEvent.putCustomAttribute("BODY", request.body().toString());
                customEvent.putCustomAttribute("STATUS CODE", response.code());
                customEvent.putCustomAttribute("NETWORK TYPE", CommonUtils.getNetworkType());
                customEvent.putCustomAttribute("RETRY COUNT", tryCount);

                Answers.getInstance().logCustom(customEvent);


                while (!response.isSuccessful() && tryCount < 3) {

                    GCLog.d("Request is not successful - " + tryCount);

                    tryCount++;

                    try {
                        CustomEvent customEventS = new CustomEvent("Network Performance");
                        customEventS.putCustomAttribute("URL", request.url().toString());
                        //customEventS.putCustomAttribute("HEADERS", request.headers().toString());
                        //customEventS.putCustomAttribute("BODY", request.body().toString());
                        customEventS.putCustomAttribute("STATUS CODE", response.code());
                        customEventS.putCustomAttribute("NETWORK TYPE", CommonUtils.getNetworkType());
                        customEventS.putCustomAttribute("RETRY COUNT", tryCount);

                        Answers.getInstance().logCustom(customEventS);

                        ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER, getClass().getSimpleName(), request.urlString(), response.message(), response.code() + "  ::  " + tryCount, 0);
                    } catch (Exception e) {
                        Crashlytics.logException(e.getCause());

                        CustomEvent customEventF = new CustomEvent("Network Performance");
                        customEventF.putCustomAttribute("URL", request.url().toString());
                        //customEventF.putCustomAttribute("HEADERS", request.headers().toString());
                        //customEventF.putCustomAttribute("BODY", request.body().toString());
                        customEventF.putCustomAttribute("ERROR", e.getMessage());
                        customEventF.putCustomAttribute("NETWORK TYPE", CommonUtils.getNetworkType());
                        customEventF.putCustomAttribute("RETRY COUNT", tryCount);

                        Answers.getInstance().logCustom(customEventF);

                    }


                    // retry the request
                    response = chain.proceed(request);
                }

                // otherwise just pass the original response on
                return response;
            }
        });

        return new RestAdapter.Builder()
                .setEndpoint(urlBase)
                .setClient(new CustomOKHttpClient(okHttpClient))
                .setLogLevel(!Constants.ENVIRONMENT.equals("PROD") ? RestAdapter.LogLevel.FULL : RestAdapter.LogLevel.NONE)
                .build();
    }

    /*public static RestAdapter getInsuranceRestAdapter() {
        String baseURL = Constants.getInsuranceWebServiceBaseURL(ApplicationController.getInstance());

        final OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.setConnectTimeout(25, TimeUnit.SECONDS);
        okHttpClient.setWriteTimeout(25, TimeUnit.SECONDS);
        okHttpClient.setReadTimeout(25, TimeUnit.SECONDS);

        return new RestAdapter.Builder()
                .setEndpoint(baseURL)
                .setClient(new OkClient(okHttpClient))
                .setLogLevel(!Constants.ENVIRONMENT.equals("PROD") ? RestAdapter.LogLevel.FULL : RestAdapter.LogLevel.NONE)
                .build();
    }*/


    public static RestAdapter getImageUploadRestAdapter() {
        String serviceUrl = Constants.getImageUploadURL(ApplicationController.getInstance());
        String urlBase = serviceUrl.substring(0, serviceUrl.lastIndexOf("/"));
        //String urlPath = serviceUrl.substring(serviceUrl.lastIndexOf("/")+1);

        return new RestAdapter.Builder()
                .setEndpoint(urlBase)
                .setLogLevel(!Constants.ENVIRONMENT.equals("PROD") ? RestAdapter.LogLevel.FULL : RestAdapter.LogLevel.NONE)
                .build();
    }

    public static RestAdapter getTruPriceRestAdapter() {
        String serviceUrl = Constants.getTruPriceWebServiceURL(ApplicationController.getInstance());
        String urlBase = serviceUrl.substring(0, serviceUrl.lastIndexOf("/"));
        //String urlPath = serviceUrl.substring(serviceUrl.lastIndexOf("/")+1);

        return new RestAdapter.Builder()
                .setEndpoint(urlBase)
                .setLogLevel(!Constants.ENVIRONMENT.equals("PROD") ? RestAdapter.LogLevel.FULL : RestAdapter.LogLevel.NONE)
                .build();
    }

    /*public static RestAdapter getLoginRestAdapter(Context context) {
        String serviceUrl = Constants.getWebServiceURL(context);
        String urlBase = serviceUrl.substring(0, serviceUrl.lastIndexOf("/"));
        //String urlPath = serviceUrl.substring(serviceUrl.lastIndexOf("/")+1);

        final OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.setConnectTimeout(10000, TimeUnit.MICROSECONDS);
        okHttpClient.setWriteTimeout(10000, TimeUnit.MICROSECONDS);
        okHttpClient.setReadTimeout(10000, TimeUnit.MICROSECONDS);

        return new RestAdapter.Builder()
                .setEndpoint(urlBase)
                .setClient(new OkClient(okHttpClient))
                .build();
    }*/

    public static String getUrlPath(Context context) {
        String serviceUrl = Constants.getWebServiceURL(context);
        return serviceUrl.substring(serviceUrl.lastIndexOf("/") + 1);
    }

    public static String getTruPriceUrlPath(Context context) {
        String serviceUrl = Constants.getTruPriceWebServiceURL(context);
        return serviceUrl.substring(serviceUrl.lastIndexOf("/") + 1);
    }

    public static String getEvaluationPath() {
        String serviceUrl = Constants.getWarrantyWebServiceURL(ApplicationController.getInstance());
        return serviceUrl.substring(serviceUrl.lastIndexOf("/") + 1);
    }

    public static String getImageUploadPath() {
        String serviceUrl = Constants.getImageUploadURL(ApplicationController.getInstance());
        return serviceUrl.substring(serviceUrl.lastIndexOf("/") + 1);
    }
}
