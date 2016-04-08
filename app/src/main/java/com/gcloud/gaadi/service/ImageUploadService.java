package com.gcloud.gaadi.service;

import android.app.IntentService;
import android.content.Intent;

import com.gcloud.gaadi.ApplicationController;
import com.gcloud.gaadi.R;
import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.model.ImageUploadResponse;
import com.gcloud.gaadi.retrofit.RetrofitRequest;
import com.gcloud.gaadi.ui.StockAddActivity;
import com.gcloud.gaadi.utils.CommonUtils;
import com.gcloud.gaadi.utils.GAHelper;
import com.gcloud.gaadi.utils.GCLog;
import com.gcloud.gaadi.utils.ImageUploadUtils;
import com.imageuploadlib.Databases.StockImagesDB;
import com.imageuploadlib.Model.StockImageData;
import com.imageuploadlib.Model.StockImageOrderData;

import org.apache.http.util.ByteArrayBuffer;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;

import retrofit.RetrofitError;

/**
 * Created by ankit on 21/11/14.
 */
public class ImageUploadService extends IntentService {

    private static final String TAG = "ImageUploadService";
    public static String SERVICE_RUNNING = "Service_running_";
    public static String IMAGE_UPLOAD_SERVICE = "imageUploadService";
    public static String REQUEST_IMAGE_NAME = "request_image_name";
    public static String RESPONSE_IMAGE_NAME = "response_image_name";
    String carId;
    //    GAHelper gaHelper;
    ArrayList<StockImageData> imageData;
    String[] uploadOrder;
    String[] mapOrder;
    String mapOrderString;
    private String makeModelVersion = "";

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public ImageUploadService() {
        super("Stock Add");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        CommonUtils.setBooleanSharedPreference(getApplicationContext(), IMAGE_UPLOAD_SERVICE, true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        CommonUtils.setBooleanSharedPreference(getApplicationContext(), IMAGE_UPLOAD_SERVICE, false);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        StockImagesDB imagesDB = new StockImagesDB(getApplicationContext());
        carId = intent.getStringExtra(Constants.CAR_ID);

        if (carId == null)
            return;
        imageData = imagesDB.getStockImageDataById(carId, "S");

        CommonUtils.setBooleanSharedPreference(getApplicationContext(), SERVICE_RUNNING + carId, true);

//        gaHelper = new GAHelper(getApplicationContext());
        int successfullUploadCount = 0;

        if (imageData != null && imageData.size() > 0) {
            makeModelVersion = imageData.get(0).getMakeModelVersion();
            CommonUtils.createNotificationTile(this, Integer.parseInt(carId), R.drawable.ic_notification, makeModelVersion, "Starting photo upload", imageData.size(), 0, intent, makeModelVersion);
            StockImageOrderData imageOrderData = imagesDB.getStockImageOrderDataById(carId);
            if (imageOrderData == null  // Fabric crash #1033
                    || imageOrderData.getImageUploadOrder() == null
                    || imageOrderData.getImageUploadOrder().isEmpty()) {
                return;
            }
            uploadOrder = imageOrderData.getImageUploadOrder().split(",");
            mapOrderString = imageOrderData.getMapOrder();
            mapOrder = imageOrderData.getMapOrder().split(",");
            GCLog.e(" Upload order length : " + uploadOrder.length);
        } else {
            GCLog.e("ImageData is null");
        }

        for (int i = 0; i < imageData.size(); i++) {
            StockImageData stockImageData = imageData.get(i);

            if (stockImageData.getDealerId() != CommonUtils.getIntSharedPreference(getApplicationContext(), Constants.UC_DEALER_ID, -1))
                continue;

            GCLog.e(" " + stockImageData.toString());
            String imagePath = stockImageData.getImagePath();

            try {
                GCLog.e("image path : " + imagePath);
              /*  HttpClient httpClient = new DefaultHttpClient();
                HttpContext httpContext = new BasicHttpContext();
                HttpPost httpPost = new HttpPost(Constants.getImageUploadURL(this));
                MultipartEntity multipartContent = new MultipartEntity();

                FileBody fileBody = new FileBody(new File(stockImageData.getImagePath()));*/
                // multipartContent.addPart("source", new StringBody("Android GCloud App"));
                String extension = imagePath.substring(imagePath.lastIndexOf("."));
                String name = "used_car_" + i + "_" + carId + extension;
                // StringBody fileName = new StringBody(name);

                int index = getNextIndex(carId, imagesDB);
                uploadOrder[index] = name;

                String newOrder = convertArrayToString(uploadOrder);
                imagesDB.updateStockImageOrderData(newOrder, carId);
                imagesDB.updateStockImageRequestName(name, carId, imagePath);
                HashMap<String, String> params = new HashMap<>();
                params.put("file_name", name);
                params.put("car_id", carId);


              /*  multipartContent.addPart("file_name", fileName); // ,"image/jpeg"
                multipartContent.addPart("stockImg", fileBody);
                multipartContent.addPart("car_id", new StringBody(carId));
                multipartContent.addPart(Constants.APP_NAME, new StringBody(CommonUtils.getStringSharedPreference(this, Constants.APP_PACKAGE_NAME, "")));
                multipartContent.addPart(Constants.OWNER, new StringBody(BuildConfig.CLOUD_OWNER));
                multipartContent.addPart(Constants.SERVICE_EXECUTIVE_ID, new StringBody(CommonUtils.getStringSharedPreference(this, Constants.SERVICE_EXECUTIVE_ID, "")));
                multipartContent.addPart(Constants.API_KEY_LABEL, new StringBody(Constants.API_KEY));
                multipartContent.addPart(Constants.ANDROID_ID, new StringBody(CommonUtils.getStringSharedPreference(this, Constants.ANDROID_ID, "")));
                multipartContent.addPart(Constants.UCDID,
                        new StringBody(String.valueOf(CommonUtils.getIntSharedPreference(this, Constants.UC_DEALER_ID, -1))));
                multipartContent.addPart(Constants.DEALER_USERNAME,
                        new StringBody(CommonUtils.getStringSharedPreference(this, Constants.UC_DEALER_USERNAME, "")));
                multipartContent.addPart(Constants.SERVICE_EXECUTIVE_LOGIN, new StringBody(String.valueOf(CommonUtils.getBooleanSharedPreference(this, Constants.SERVICE_EXECUTIVE_LOGIN, false))));
                multipartContent.addPart(Constants.APP_VERSION, new StringBody(CommonUtils.getStringSharedPreference(this, Constants.APP_VERSION_CODE, "")));

                GCLog.e("MultiPartcontent ", multipartContent.toString());
                GCLog.e("Source , file_name , car_id ,api_key , ucdid , username ,SERVICE_EXECUTIVE_LOGIN , APP_VERSION", new StringBody(String.valueOf(CommonUtils.getIntSharedPreference(this, Constants.UC_DEALER_ID, -1))) + "," + new StringBody(CommonUtils.getStringSharedPreference(this, Constants.UC_DEALER_USERNAME, "")) + "," + new StringBody(String.valueOf(CommonUtils.getBooleanSharedPreference(this, Constants.SERVICE_EXECUTIVE_LOGIN, false))) + "," + new StringBody(CommonUtils.getStringSharedPreference(this, Constants.APP_VERSION_CODE, "")));
                httpPost.setEntity(multipartContent);
                HttpResponse response = httpClient.execute(httpPost, httpContext);
                String responses = EntityUtils.toString(response.getEntity());*/


                ImageUploadResponse response = RetrofitRequest.stockImagesUploadRequest(stockImageData.getImagePath(),
                        params);

                if (response.getStatus().equalsIgnoreCase("T")) {
                    // JSONObject object = new JSONObject(responses);
                    ++successfullUploadCount;
                    publishImageResult("T", i, imageData.size(), false, makeModelVersion);
                    String requestImageName = response.getRequest_image_name();
                    String responseImageName = response.getResponse_image_name();
                    GCLog.e("Success ImageUpload");

                    //Update Response name corresponding to request image name
                    imagesDB.updateStockImageData(requestImageName, responseImageName, "T", carId);

                    //update the new order by replacing request name to response name
                    StockImageOrderData order = imagesDB.getStockImageOrderDataById(carId);
                    String oldOrder = order.getImageUploadOrder();
                    if (requestImageName != null) {
                        String latestOrder = oldOrder.replace(requestImageName, responseImageName);
                        imagesDB.updateStockImageOrderData(latestOrder, carId);
                        uploadOrder = latestOrder.split(",");
                    }

                }
                //  GCLog.e(Constants.TAG, "image upload response: " + responses);
            } catch (RetrofitError error) {
                GCLog.e(error.getMessage());
            } catch (Exception e) {
                GCLog.e(e.getMessage());
            }
        }

        CommonUtils.setBooleanSharedPreference(getApplicationContext(), SERVICE_RUNNING + carId, false);
        if (successfullUploadCount == imageData.size() && successfullUploadCount != 0) {
            publishImageResult("C", successfullUploadCount, 0, false, makeModelVersion);
        } else {
            publishImageResult("F", successfullUploadCount, 0, false, makeModelVersion);
        }
    }

    private int getNextIndex(String carId, StockImagesDB imagesDB) {
        StockImageOrderData orderDataById = imagesDB.getStockImageOrderDataById(carId);
        String mapOrderById = orderDataById.getMapOrder();
        String[] mapOrder = mapOrderById.split(",");
        int index = Integer.parseInt(mapOrder[0]);
        if (index == -1) {
            index = 0;
        }

        //update the mapOrder by popping out element before first comma
        int indexOfComma = mapOrderById.indexOf(",");
        String storeOrder = mapOrderById.substring(indexOfComma + 1);
        imagesDB.updateStockMapOrderData(storeOrder, carId);
        return index;
    }


    private String convertArrayToString(String[] uploadOrder) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < uploadOrder.length; i++) {
            if (i != uploadOrder.length - 1) {
                sb.append(uploadOrder[i] + ",");
            } else
                sb.append(uploadOrder[i]);
        }
        String joined = sb.toString();
        return joined;
    }

    private void publishImageResult(String status, int count, int size, boolean start, String makeModelVersion) {
        int countImages = imageData.size();
        try {
            Intent intent = new Intent();
            if ("T".equalsIgnoreCase(status)) {
                CommonUtils.createNotificationTile(
                        this,
                        Integer.parseInt(carId),
                        R.drawable.ic_notification,
                        makeModelVersion, "Uploaded " + (count + 1) + " of " + countImages + " photos.",
                        countImages,
                        count,
                        intent, makeModelVersion);
            } else if ("C".equalsIgnoreCase(status)) {
                CommonUtils.createNotificationTile(
                        this,
                        Integer.parseInt(carId),
                        R.drawable.ic_notification,
                        makeModelVersion, "All photos uploaded successfully.\nYour photos will be visible in 5-10 minutes.",
                        countImages,
                        0,
                        intent, makeModelVersion);
                ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER, Constants.IMAGE_UPLOAD_SERVICE, Constants.IMAGE_UPLOAD, Constants.SUCCESS, countImages + "", 0L);
                StockAddActivity.sendOrderImages(getApplicationContext(), carId);
            } else {
                if (countImages > 0) {
                    CommonUtils.createNotificationTile(
                            this,
                            Integer.parseInt(carId),
                            R.drawable.ic_notification,
                            makeModelVersion, "Failed to upload " + (countImages - count) + " out of " + countImages + " photos",
                            countImages,
                            0,
                            intent, makeModelVersion);
                    ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER, Constants.IMAGE_UPLOAD_SERVICE, Constants.IMAGE_UPLOAD, Constants.FAIL, (countImages - count) + "", 0L);
                    StockAddActivity.sendOrderImages(getApplicationContext(), carId);
                }
            }
        } catch (Exception e) {
            GCLog.e("Some exception occurred. " + e.getMessage());
        }
    }

   /* private int uploadDownloadedImage(File file, String carId, int successfullUploadCount) {

        String responses = "";
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpContext httpContext = new BasicHttpContext();
            HttpPost httpPost = new HttpPost(Constants.getImageUploadURL(this));
            MultipartEntity multipartContent = new MultipartEntity();
            multipartContent.addPart("source", new StringBody("Android GCloud App"));
            String extension = file.getPath().substring(file.getPath().lastIndexOf("."));
            multipartContent.addPart("file_name", new StringBody("used_car_" + carId + "_" + carId + extension)); // ,"image/jpeg"
            multipartContent.addPart("stockImg", new FileBody(file));
            multipartContent.addPart("car_id", new StringBody(carId));
            multipartContent.addPart(Constants.APP_NAME, new StringBody(CommonUtils.getStringSharedPreference(this, Constants.APP_PACKAGE_NAME, "")));
            multipartContent.addPart(Constants.OWNER, new StringBody(BuildConfig.CLOUD_OWNER));
            multipartContent.addPart(Constants.API_KEY_LABEL, new StringBody(Constants.API_KEY));
            multipartContent.addPart(Constants.SERVICE_EXECUTIVE_ID, new StringBody(CommonUtils.getStringSharedPreference(this, Constants.SERVICE_EXECUTIVE_ID, "")));
            multipartContent.addPart(Constants.ANDROID_ID, new StringBody(CommonUtils.getStringSharedPreference(this, Constants.ANDROID_ID, "")));
            multipartContent.addPart(Constants.UCDID,
                    new StringBody(String.valueOf(CommonUtils.getIntSharedPreference(this, Constants.UC_DEALER_ID, -1))));
            multipartContent.addPart(Constants.DEALER_USERNAME,
                    new StringBody(CommonUtils.getStringSharedPreference(this, Constants.UC_DEALER_USERNAME, "")));
            multipartContent.addPart(Constants.SERVICE_EXECUTIVE_LOGIN, new StringBody(String.valueOf(CommonUtils.getBooleanSharedPreference(this, Constants.SERVICE_EXECUTIVE_LOGIN, false))));
            multipartContent.addPart(Constants.APP_VERSION, new StringBody(CommonUtils.getStringSharedPreference(this, Constants.APP_VERSION_CODE, "")));
            long totalSize = multipartContent.getContentLength();

            httpPost.setEntity(multipartContent);
            HttpResponse response = httpClient.execute(httpPost, httpContext);

            responses = EntityUtils.toString(response.getEntity());

            if (responses.contains("\"status\":\"T\"")) {
                ++successfullUploadCount;
            }

            publishImageResult(responses, successfullUploadCount, imageData.size(), false, makeModelVersion);

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return successfullUploadCount;
    }*/

    private File downloadFile(String photoUrl) {
        File file = ImageUploadUtils.getImagepath();
        try {
            URL url = new URL(photoUrl);
            URLConnection ucon = url.openConnection();

    /*
     * Define InputStreams to read from the URLConnection.
     */
            InputStream is = ucon.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);

    /*
     * Read bytes to the Buffer until there is nothing more to read(-1).
     */
            ByteArrayBuffer baf = new ByteArrayBuffer(1024);
            int current = 0;
            while ((current = bis.read()) != -1) {
                baf.append((byte) current);
            }

    /* Convert the Bytes read to a Stream. */
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(baf.toByteArray());
            fos.close();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }
}