package com.gcloud.gaadi;

import android.content.Context;

import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.model.GeneralResponse;
import com.gcloud.gaadi.retrofit.RetrofitRequest;
import com.gcloud.gaadi.utils.GCLog;
import com.imageuploadlib.Databases.StockImagesDB;
import com.imageuploadlib.Model.StockImageOrderData;

import java.util.HashMap;

import retrofit.Callback;
import retrofit.RetrofitError;

/**
 * Created by Lakshay on 24-03-2015.
 */
public class ImageOrdersThread extends Thread {

    private static final String TAG = "ImageOrdersThread";
    String carId;
    private Context context;

    public ImageOrdersThread(Context context, String carId) {
        this.context = context;
        this.carId = carId;
    }

    @Override
    public void run() {
        super.run();

        //for (int i = 0; i < 5; i++) {
//        try {
//            Thread.sleep(5000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

        HashMap<String, String> params = new HashMap<>();
        params.put(Constants.CAR_ID, carId);


        StockImagesDB imagesDB = new StockImagesDB(context);

        StockImageOrderData imageOrderData = imagesDB.getStockImageOrderDataById(carId);
        String order = imageOrderData.getImageUploadOrder();
        params.put(Constants.IMAGES_ORDER, order);
        params.put(Constants.API_KEY_LABEL, Constants.API_KEY);
        params.put(Constants.API_RESPONSE_FORMAT_LABEL, Constants.API_RESPONSE_FORMAT);
        params.put(Constants.METHOD_LABEL, Constants.IMAGES_UPLOAD_ORDER);
        GCLog.e("Images Order : " + order);
        GCLog.e("Car_id : " + carId);
        GCLog.e("Url : " + Constants.getWebServiceURL(context));

        RetrofitRequest.sendImagesOrderRequest(context, params, new Callback<GeneralResponse>() {
            @Override
            public void success(GeneralResponse generalResponse, retrofit.client.Response response) {
                GCLog.e("Response : " + generalResponse.getMessage());
            }

            @Override
            public void failure(RetrofitError error) {
                GCLog.e("Error 123: " + error.getMessage());
            }
        });

      /*  SendImagesOrderRequest sendImagesOrderRequest = new SendImagesOrderRequest(context, Request.Method.POST,
                Constants.getWebServiceURL(context),
                new Response.Listener<GeneralResponse>() {
                    @Override
                    public void onResponse(GeneralResponse response) {

                        GCLog.e("Response : " + response.getMessage());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                GCLog.e("Error 123: " + error.getMessage());
            }
        }, params);
        ApplicationController.getInstance().addToRequestQueue(sendImagesOrderRequest);*/
    }
}