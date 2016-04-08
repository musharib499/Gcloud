package com.gcloud.gaadi.insurance;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.gcloud.gaadi.EndlessScroll.RecyclerViewEndlessScrollListener;
import com.gcloud.gaadi.R;
import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.db.MakeModelVersionDB;
import com.gcloud.gaadi.model.GeneralResponse;
import com.gcloud.gaadi.model.InspectedCarsInsuranceModel;
import com.gcloud.gaadi.model.InsuranceInspectedCarData;
import com.gcloud.gaadi.model.StockDetailModel;
import com.gcloud.gaadi.model.StocksModel;
import com.gcloud.gaadi.retrofit.RetrofitRequest;
import com.gcloud.gaadi.service.DatabaseInsertionService;
import com.gcloud.gaadi.ui.BaseActivity;
import com.gcloud.gaadi.ui.FilterFragment;
import com.gcloud.gaadi.ui.StocksActivity;
import com.gcloud.gaadi.utils.CommonUtils;
import com.gcloud.gaadi.utils.GCLog;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class AllCasesActivity extends BaseActivity {

    private static final int FILTER_ACTION = 101;
    private RecyclerView recyclerView;
    private InsuranceAllCasesAdapter adapter;

    private ProgressBar progressBar;
    private TextView progressText;
    private FrameLayout noDataReceived;
    private LinearLayoutManager manager;

    private Integer totalCount = 0;
    private Boolean hasNext;
    private ArrayList<InsuranceInspectedCarData> carDataList;
    private HashMap<String, String> params;
    private String notificationAction = "", month = "", year = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_all_cases, frameLayout);

        StocksActivity.filterOptionMap = new HashMap<>();
        StocksActivity.filterParams = new HashMap<>();
        StocksActivity.filterOptionCount = new HashMap<>();
        StocksActivity.makeHashMap = new LinkedHashMap<>();

        carDataList = new ArrayList<>();
        params = new HashMap<>();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AllCasesActivity.this, InsuranceAllCasesFilterActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivityForResult(intent, FILTER_ACTION);
            }
        });

        manager = new LinearLayoutManager(this);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(manager);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressText = (TextView) findViewById(R.id.progressText);

        noDataReceived = (FrameLayout) findViewById(R.id.noDataReceived);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            GCLog.e("extras are not null");
            if (extras.containsKey(Constants.FROM_NOTIFICATION)) {
               // sellerLeadId = extras.getString("leadId");
                //leadName = extras.getString("leadname");
                NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                manager.cancel(Integer.parseInt(extras.getString("nID")));

                if (extras.containsKey("rowId")) {
                    ContentValues values = new ContentValues();
                    values.put(MakeModelVersionDB.COLUMN_TYPE, 2);

                    startService(new Intent(this, DatabaseInsertionService.class)
                            .putExtra(Constants.ACTION, "update")
                            .putExtra(Constants.PROVIDER_URI, Uri.parse("content://"
                                    + Constants.NOTIFICATION_CONTENT_AUTHORITY + "/" + MakeModelVersionDB.TABLE_NOTIFICATION))
                            .putExtra(Constants.CONTENT_VALUES, values)
                            .putExtra(Constants.SELECTION, MakeModelVersionDB.COLUMN_ID + " = ?")
                            .putExtra(Constants.SELECTION_ARGS, new String[]{String.valueOf(extras.getLong("rowId", 0))}));
                }

                if (!extras.containsKey("read")) {
                    HashMap<String, String> params = new HashMap<>();
                    params.put(Constants.GCM_RESPONSE_CODE, "0"); // notification was opened and was destined for this dealer.
                    params.put(Constants.SCREEN_NAME, getClass().getSimpleName());
                    params.put(Constants.GCM_MESSAGE, extras.toString());
                    params.put(Constants.METHOD_LABEL, Constants.LOG_NOTIFICATION_METHOD);

                    RetrofitRequest.makeLogNotificationRequest(this, params, new Callback<GeneralResponse>() {
                        @Override
                        public void success(GeneralResponse generalResponse, retrofit.client.Response response) {

                        }

                        @Override
                        public void failure(RetrofitError error) {

                        }
                    });
                }

                InsuranceInspectedCarData carData =
                        (InsuranceInspectedCarData) extras.getSerializable(Constants.INSURANCE_INSPECTED_CAR_DATA);
                if (carData != null) {
                    notificationAction = extras.getString(Constants.ACTION);
                    if (!(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                            && !CommonUtils.checkForPermission(this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            101, "Storage"))) {
                        downloadFile(carData);
                    }
                }
            } else if (extras.containsKey("month") && extras.containsKey("year")) {
                month = extras.getString("month");
                year = extras.getString("year");
            } else {
                /*leadName = ((LeadDetailModel) getIntent().getSerializableExtra(Constants.MODEL_DATA)).getName();
                getSupportActionBar().setTitle(leadName);
                setSupportActionBar(toolBar);
                sellerLeadId = ((LeadDetailModel) getIntent().getSerializableExtra(Constants.MODEL_DATA)).getLeadId();*/
            }
        }

        getAllCases(1);
    }

    private void getAllCases(int pageNumber) {
        progressBar.setVisibility(View.VISIBLE);
        progressText.setVisibility(View.VISIBLE);

        params.put(Constants.METHOD_LABEL, Constants.GET_INSURANCE_CASES);
        params.put(Constants.PAGE, String.valueOf(pageNumber));
        params.put(Constants.RPP, "10");
        params.put("type", "result");
        params.put("records", "all");
        if (month != null && !month.isEmpty()) {
            params.put("month", month);
        }
        if (year != null && !year.isEmpty()) {
            params.put("year", year);
        }
        RetrofitRequest.getInspectedCarsForInsurance(params,
                new Callback<InspectedCarsInsuranceModel>() {
                    @Override
                    public void success(final InspectedCarsInsuranceModel response, Response res) {
                        progressBar.setVisibility(View.GONE);
                        progressText.setVisibility(View.GONE);
                        //GCLog.e("status: " + response.getStatus());
                        if (response.getStatus().equals("T")) {
                            toolbar.setTitle("All Cases (" + response.getTotalRecords() + ")");
                            if (response.getTotalRecords() == 0) {
                                showNoDataFound();
                                recyclerView.setVisibility(View.GONE);
                            } else {
                                if (totalCount == 0) {
                                    noDataReceived.setVisibility(View.GONE);
                                    if (response.getFilters() != null) {
                                        makeFilterRequest(response.getFilters());
                                    }
                                    totalCount = response.getTotalRecords();
                                    carDataList.clear();
                                    carDataList.addAll(response.getInspectedCars());
                                    adapter = new InsuranceAllCasesAdapter(AllCasesActivity.this, carDataList);
                                    if (recyclerView.getVisibility() != View.VISIBLE) {
                                        recyclerView.setVisibility(View.VISIBLE);
                                    }
                                    recyclerView.setAdapter(adapter);
                                    recyclerView.setOnScrollListener(new RecyclerViewEndlessScrollListener(manager, totalCount) {
                                        @Override
                                        public void onLoadMore(int nextPageNo) {
                                            if (response.getHasNext() == 1) {
                                                getAllCases(nextPageNo);
                                            }
                                        }
                                    });
                                } else {
                                    carDataList.addAll(response.getInspectedCars());
                                    adapter.notifyDataSetChanged();
                                }
                            }
                        } else {
                            if (totalCount == 0) {
                                toolbar.setTitle("All Cases (0)");
                                showNoDataFound();
                                recyclerView.setVisibility(View.GONE);
                            }
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        progressBar.setVisibility(View.GONE);
                        progressText.setVisibility(View.GONE);

                        if (error.getCause() instanceof IOException) {
                            showNoInternetAvailable();
                            return;
                        }

                        if (totalCount == 0) {
                            toolbar.setTitle("All Cases (0)");
                            showNoDataFound();
                            recyclerView.setVisibility(View.GONE);
                        }
                    }
                });
    }

    private void showNoInternetAvailable() {
        noDataReceived.setVisibility(View.VISIBLE);
        noDataReceived.removeAllViews();

        View view = LayoutInflater.from(this).inflate(R.layout.layout_error, null, false);
        view.findViewById(R.id.retry).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                retryToLoadData(null);
            }
        });

        noDataReceived.addView(view);
    }

    private void showNoDataFound() {
        noDataReceived.setVisibility(View.VISIBLE);
        noDataReceived.removeAllViews();

        View view = LayoutInflater.from(this).inflate(R.layout.layout_error, null, false);
        ((ImageView) view.findViewById(R.id.no_internet_img)).setImageResource(R.drawable.no_result_icons);
        ((TextView) view.findViewById(R.id.errorMessage)).setText(R.string.noresult);
        view.findViewById(R.id.checkconnection).setVisibility(View.GONE);
        view.findViewById(R.id.retry).setVisibility(View.GONE);

        noDataReceived.addView(view);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_all_cases, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    public void retryToLoadData(View view) {
        noDataReceived.setVisibility(View.GONE);
        totalCount = 0;
        getAllCases(1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case FILTER_ACTION:
                if (resultCode == RESULT_OK) {
                    Bundle extras = data.getExtras();
                    if (extras != null) {
                        boolean applyFilter = extras.getBoolean("applyFilter");
                        filterCarList(applyFilter);
                    }
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101
                && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            downloadFile((InsuranceInspectedCarData) getIntent().getSerializableExtra(Constants.INSURANCE_INSPECTED_CAR_DATA));
        }
    }

    private void filterCarList(boolean applyFilter) {
        params.clear();
        int count = 0;
        for (Map.Entry entry : StocksActivity.filterParams.entrySet()) {
            if (((ArrayList<String>) entry.getValue()).size() > 0) {
                count++;
                StringBuilder values = new StringBuilder();
                for (String value : (ArrayList<String>) entry.getValue()) {
                    if (values.length() > 0) {
                        values.append(",");
                    }
                    values.append(value);
                }
                params.put((String) entry.getKey(), values.toString());
            }
        }
        if ((StocksActivity.filterParams.get("reg no") != null && StocksActivity.filterParams.get("reg no").get(0).isEmpty()) && count > 0) {
            count--;
        }
        if ((StocksActivity.filterParams.get("req no") != null && StocksActivity.filterParams.get("req no").get(0).isEmpty()) && count > 0) {
            count--;
        }
        setFabCounter(count);
        if (applyFilter) {
            totalCount = 0;
            getAllCases(1);
        }
    }

    private void makeFilterRequest(StocksModel.Filters filterResponse) {
        if (StocksActivity.filterOptionMap == null) {
            return;
        }
        if (!StocksActivity.filterOptionMap.containsKey("filters")
                || StocksActivity.filterOptionMap.get("filters") == null) {
            //GCLog.e("filters created");
            StocksActivity.filterOptionMap.put("filters", FilterFragment.getInstance(true,
                    InsuranceAllCasesFilterActivity.list));
            StocksActivity.filterOptionMap.put("req no", FilterFragment.getInstance(true, "By Request No",
                    "req no", InputType.TYPE_CLASS_NUMBER));
            StocksActivity.filterOptionMap.put("reg no", FilterFragment.getInstance(true, "By Reg no"));
            for (StockDetailModel model : filterResponse.getMake()) {
                StocksActivity.makeHashMap.put(model.getMake(), String.valueOf(model.getMakeid()));
            }
            StocksActivity.filterOptionMap.put("make", FilterFragment.getInstance(false, StocksActivity.makeHashMap, false));
            LinkedHashMap<String, String> modelMap = new LinkedHashMap<>();
            for (StockDetailModel model : filterResponse.getModel()) {
                modelMap.put(model.getModel(), String.valueOf(model.getMakeid()));
            }
            StocksActivity.filterOptionMap.put("model", FilterFragment.getInstance(false, modelMap, false));

            StocksActivity.filterOptionMap.put("status", FilterFragment.getInstance(false, filterResponse.getStatus()));
            StocksActivity.filterOptionMap.put("insurance type",
                    FilterFragment.getInstance(false, filterResponse.getInsuranceType()));
            StocksActivity.filterOptionMap.put("insurer", FilterFragment.getInstance(false, filterResponse.getInsurer()));

            fab.setVisibility(View.VISIBLE);
        }
    }

    public void downloadFile(InsuranceInspectedCarData data) {
        File directory = new File(Environment.getExternalStorageDirectory(), "Gaadi Gcloud/Insurance Docs");
        if (!directory.exists())
            if (!directory.mkdirs()) {
                GCLog.e("directory can't be created");
                return;
            }

        String fileName = data.getRequestId() + "_" +
                data.getMake() + "_" + data.getModel() + "_" + data.getCarVersion() + "_" +
                data.getInsurer() + data.getPolicyDocUrl().substring(data.getPolicyDocUrl().lastIndexOf("."));
        fileName = fileName.replaceAll(" ", "_");

        //GCLog.e("filePath: " + directory.getAbsolutePath() + "/" + fileName);

        File file = new File(directory, fileName);
        if (!file.exists()) {
            new FileDownLoadAsyncTask(this, file).execute(data.getPolicyDocUrl());
        } else {
            if (notificationAction.equals("download")) {
                openFile(file);
            } else if (notificationAction.equals("share")) {
                share(file);
            }
        }
    }

    private void openFile(File file) {
        Intent fileOpenIntent = new Intent(Intent.ACTION_VIEW);
        fileOpenIntent.setDataAndType(Uri.fromFile(file), "application/pdf");
        fileOpenIntent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        try {
            startActivity(Intent.createChooser(fileOpenIntent, "Open File"));
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void share(File file) {
        Intent fileOpenIntent = new Intent(Intent.ACTION_SEND);
        fileOpenIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
        fileOpenIntent.setType("application/pdf");
        fileOpenIntent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        try {
            startActivity(Intent.createChooser(fileOpenIntent, "Share File"));
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    private class FileDownLoadAsyncTask extends AsyncTask<String, Integer, File> {

        private Context context;
        private File file;
        private ProgressDialog dialog;

        public FileDownLoadAsyncTask(Context context, File file) {
            this.context = context;
            this.file = file;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(context);
            dialog.setMessage("Downloading file. Please wait...");
            dialog.setIndeterminate(false);
            dialog.setMax(100);
            dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected File doInBackground(String... strings) {
            int count;
            try {
                URL url = new URL(strings[0]);
                URLConnection connection = url.openConnection();
                connection.connect();

                int lengthOfFile = connection.getContentLength();

                InputStream input = new BufferedInputStream(url.openStream(), 8192);
                OutputStream output = new FileOutputStream(file);

                byte data[] = new byte[1024];
                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    publishProgress((int) ((total * 100) / lengthOfFile));
                    output.write(data, 0, count);
                }
                output.flush();
                output.close();
                input.close();
                return this.file;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(File file) {
            super.onPostExecute(file);
            if (context == null || ((Activity) context).isFinishing()) {    // Fabric #840 by gaurav.kumar@gaadi.com
                return;
            }
            dialog.dismiss();
            if (file != null) {
                if (notificationAction.equals("download")) {
                    openFile(file);
                } else if (notificationAction.equals("share")) {
                    share(file);
                }
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            dialog.setProgress(values[0]);
        }
    }
}
