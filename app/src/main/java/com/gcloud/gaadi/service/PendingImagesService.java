package com.gcloud.gaadi.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.gcloud.gaadi.ApplicationController;
import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.insurance.InsuranceBackgroundImageUploadService;
import com.gcloud.gaadi.model.FinanceData;
import com.gcloud.gaadi.retrofit.FinanceImagesUploadService;
import com.gcloud.gaadi.utils.CommonUtils;
import com.gcloud.gaadi.utils.GCLog;
import com.imageuploadlib.Databases.StockImagesDB;
import com.imageuploadlib.Model.StockImageData;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by Lakshay on 03-04-2015.
 */
public class PendingImagesService extends Service {

    public static String PENDING_IMAGES_SERVICE = "pendingService";
    private final String TAG = PendingImagesService.this.getClass().getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        // GCLog.e(Constants.TAG, "Created Service");

        StockImagesDB imagesDB = new StockImagesDB(getApplicationContext());
        if (CommonUtils.isNetworkAvailable(getApplicationContext())) {
            final ArrayList<StockImageData> pendingImages = imagesDB.getPendingImages();
            if (pendingImages.size() > 0) {
                if (!CommonUtils.getBooleanSharedPreference(getApplicationContext(), ImageUploadService.IMAGE_UPLOAD_SERVICE, false)) {
                    if (!CommonUtils.getBooleanSharedPreference(getApplicationContext(), PENDING_IMAGES_SERVICE, false)) {
                        //String carId = "";

                        final Context context = this;
                        GCLog.e("Start Image Upload");

                        final HashSet<String> carIds = new HashSet<>();
                        for (int i = 0; i < pendingImages.size(); i++) {
                            GCLog.e(pendingImages.get(i).toString());
                            carIds.add(pendingImages.get(i).getCarId());
                        }

                        Thread thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                //CommonUtils.setBooleanSharedPreference(getApplicationContext(), PENDING_IMAGES_SERVICE, true);
                                //CommonUtils.uploadImages(getApplicationContext(), pendingImages);
                                for (String carId: carIds) {
                                    Intent startImageUpload = new Intent(context, ImageUploadService.class);
                                    startImageUpload.putExtra(Constants.CAR_ID, carId);
                                    startService(startImageUpload);
                                }
                            }
                        });

                        thread.start();

                        /*StockImageOrderData imageOrderData = imagesDB.getStockImageOrderDataById(carId);
                        String order = imageOrderData.getImageUploadOrder();
                        GCLog.e(order);*/
                    } else {
                        GCLog.e("Pending Images Service Already Running");
                    }
                } else {
                    GCLog.e(ImageUploadService.IMAGE_UPLOAD_SERVICE + " already running");
                }
            } else {
                //GCLog.e(Constants.TAG, "No Pending Images ");
            }

            ArrayList<FinanceData> datas = ApplicationController.getFinanceDB().getImagesForUpload();
            if (datas.size() > 0) {
                startService(new Intent(getApplicationContext(), FinanceImagesUploadService.class));
            }

            startService(new Intent(this, InsuranceBackgroundImageUploadService.class));
        } else {
            GCLog.e("Network not available");
        }
        stopSelf();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //GCLog.e(Constants.TAG, "Destroyed Service");
    }

    @Override
    public IBinder onBind(Intent intent) {
        //GCLog.e(Constants.TAG, "Service onBind");
        return null;
    }
}
