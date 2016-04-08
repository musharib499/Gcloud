package com.gcloud.gaadi.service;

import android.app.IntentService;
import android.content.Intent;
import android.widget.Toast;

import com.gcloud.gaadi.ApplicationController;
import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.db.DBFunction;
import com.gcloud.gaadi.events.SetTabTextEvent;
import com.gcloud.gaadi.model.StocksModel;
import com.gcloud.gaadi.retrofit.RetrofitRequest;
import com.gcloud.gaadi.utils.CommonUtils;
import com.gcloud.gaadi.utils.GCLog;
import com.google.gson.Gson;

import java.util.HashMap;

import retrofit.Callback;
import retrofit.RetrofitError;

/**
 * Created by alokmishra on 7/3/16.
 */
public class SyncStocksService extends IntentService {

    public SyncStocksService() {
        super("StockLists");
    }

    private String time = "";
    private Boolean isShowActive = true;
    private Boolean isShowInActive = true;

    @Override
    protected void onHandleIntent(Intent intent) {
        CommonUtils.setBooleanSharedPreference(ApplicationController.getInstance(), Constants.IS_ACTIVE_RUNNING_STOCK_SERVICE, true);
        CommonUtils.setBooleanSharedPreference(ApplicationController.getInstance(), Constants.IS_INACTIVE_RUNNING_STOCK_SERVICE, true);
        CommonUtils.setBooleanSharedPreference(ApplicationController.getInstance(), Constants.IS_ERROR_STOCK_SERVICE, false);
        time = CommonUtils.getStringSharedPreference(ApplicationController.getInstance(), Constants.STOCK_CHANGE_TIME, "");
        performSync(1);
    }

    private synchronized void performSync(final int pageNo) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(Constants.DEALER_USERNAME, CommonUtils.getStringSharedPreference(getBaseContext(), Constants.UC_DEALER_USERNAME, ""));
        params.put(Constants.UCDID, String.valueOf(CommonUtils.getIntSharedPreference(getBaseContext(), Constants.UC_DEALER_ID, -1)));
        params.put(Constants.PAGE_NO, String.valueOf(pageNo));
        params.put(Constants.METHOD_LABEL, Constants.VIEW_STOCK_LIST_API);
        params.put(Constants.STOCK_CHANGE_TIME, time);

        params.put(Constants.RPP, "100");
        GCLog.e("service start" + pageNo + " time := " + time);
        //params.put(Constants.TRUST_MARK_CERTIFIED, trustMark);
        RetrofitRequest.makeStockViewRequest(params, new Callback<StocksModel>() {
            @Override
            public void success(final StocksModel response, retrofit.client.Response res) {
                if ((response != null) && "T".equalsIgnoreCase(response.getStatus())) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            DBFunction.insertData(getBaseContext(), response.getStocks());
                            if (response.getStocks() != null) {
                                CommonUtils.setStringSharedPreference(ApplicationController.getInstance(),
                                        Constants.STOCK_CHANGE_TIME, response.getStocks().get(response.getStocks().size() - 1).getChangeTime());
                            }

                            if(pageNo == 1) {
                                if (response.getFilters() != null) {
                                    DBFunction.insertFilterData(new Gson().toJson(response.getFilters()), new Gson().toJson(response.getInspectedFilters()));
                                    Intent intent = new Intent("Initiate Filters");
                                    ApplicationController.getEventBus().post(intent);
                                }
                            }

                            if (response.isHasNext()) {
                                if (DBFunction.getActiveLists() > 0 && isShowActive) {
                                    isShowActive = false;
                                    CommonUtils.setBooleanSharedPreference(ApplicationController.getInstance(), Constants.IS_ACTIVE_RUNNING_STOCK_SERVICE, false);
                                }
                                if (DBFunction.getInActiveLists() > 0 && isShowInActive) {
                                    isShowInActive = false;
                                    CommonUtils.setBooleanSharedPreference(ApplicationController.getInstance(), Constants.IS_INACTIVE_RUNNING_STOCK_SERVICE, false);
                                }
                               /* try {
                                    Thread.sleep(3000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }*/
                                performSync(pageNo + 1);
                            } else {
                                // TODOO from DB

                                CommonUtils.setBooleanSharedPreference(ApplicationController.getInstance(), Constants.IS_ACTIVE_RUNNING_STOCK_SERVICE, false);
                                CommonUtils.setBooleanSharedPreference(ApplicationController.getInstance(), Constants.IS_INACTIVE_RUNNING_STOCK_SERVICE, false);
                                int x = DBFunction.getActiveLists();
                                int y = DBFunction.getInActiveLists();
                              /*  int a = DBFunction.getInspectedLists();
                                int b = DBFunction.getFinanceInsuranceLists();*/
                              /*  ApplicationController.getEventBus().post(
                                        new SetTabTextEvent("Available (" + x + ")", Constants.AVAILABLE_STOCKS));
                                ApplicationController.getEventBus().post(
                                        new SetTabTextEvent("Removed (" + y + ")", Constants.REMOVED_STOCKS));
*/

                            }

                        }
                    }).start();

                    GCLog.e("response := " + response.isHasNext());
                } else {
                    CommonUtils.showToast(ApplicationController.getInstance(), "Server Error", Toast.LENGTH_SHORT);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                CommonUtils.setBooleanSharedPreference(ApplicationController.getInstance(), Constants.IS_ERROR_STOCK_SERVICE, true);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                       int x = DBFunction.getActiveLists();
                       int y = DBFunction.getInActiveLists();
                      /*  ApplicationController.getEventBus().post(
                                new SetTabTextEvent("Available (" + x + ")", Constants.AVAILABLE_STOCKS));
                        ApplicationController.getEventBus().post(
                                new SetTabTextEvent("Removed (" + y + ")", Constants.REMOVED_STOCKS));*/
                    }
                }).start();

                GCLog.e( "error Message := " + error.getMessage());
            }
        });
    }
}
