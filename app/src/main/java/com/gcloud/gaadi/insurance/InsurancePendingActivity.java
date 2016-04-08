package com.gcloud.gaadi.insurance;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.gcloud.gaadi.ApplicationController;
import com.gcloud.gaadi.R;
import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.model.FormItem;
import com.gcloud.gaadi.model.GeneralResponse;
import com.gcloud.gaadi.retrofit.RetrofitRequest;
import com.gcloud.gaadi.ui.BaseActivity;
import com.gcloud.gaadi.ui.GCProgressDialog;
import com.gcloud.gaadi.utils.CommonUtils;
import com.gcloud.gaadi.utils.OverFlowMenu;
import com.imageuploadlib.PhotosLibrary;
import com.imageuploadlib.Utils.PhotoParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit.RetrofitError;

public class InsurancePendingActivity extends BaseActivity implements View.OnClickListener, InsuranceForm30Adapter.AllImagesLoaded {

    private final String[] rcCopyKeys = {"doc_rc_copy", "doc_rc_copy_2"};
    private String form29ImagePath = "";
    private String chequeImagePath = "";
    private InsuranceForm30Adapter form30Adapter = null;
    private InsuranceForm30Adapter rcCopyAdapter;
    private InsuranceDashboardModel.PendingInsuranceCaseModel pendingModel;
    private int totalImageCount = 0, successCount = 0;
    private GCProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_insurance_pending);

        getLayoutInflater().inflate(R.layout.activity_insurance_pending, frameLayout);

        Bundle extras = getIntent().getExtras();

        if (extras == null || !extras.containsKey("pendingModel")) {
            finish();
            return;
        }

        pendingModel = (InsuranceDashboardModel.PendingInsuranceCaseModel) extras.getSerializable("pendingModel");

        if (pendingModel == null) {
            finish();
            return;
        }

        progressDialog = new GCProgressDialog(this, this, getString(R.string.please_wait));

        for (String field : pendingModel.getPendingDocuments()) {
            switch (field) {

                case "Rc Copy":
                    findViewById(R.id.rcCopyLayout).setVisibility(View.VISIBLE);
                    rcCopyAdapter = new InsuranceForm30Adapter(this, 2, this);
                    ((GridView) findViewById(R.id.rcCopyGridView)).setAdapter(rcCopyAdapter);
                    setGridViewHeightBasedOnChildren((GridView) findViewById(R.id.rcCopyGridView), 2);
                    break;

                case "Form 29":
                    findViewById(R.id.form_29_layout).setVisibility(View.VISIBLE);
                    findViewById(R.id.form_29_layout).setOnClickListener(this);
                    break;

                case "Cheque Copy":
                    findViewById(R.id.cheque_layout).setVisibility(View.VISIBLE);
                    findViewById(R.id.cheque_layout).setOnClickListener(this);
                    break;

                case "Previous Policy Copy":
                    findViewById(R.id.form30Layout).setVisibility(View.VISIBLE);
                    ((TextView) findViewById(R.id.form30Title)).setText("Previous Policy Copy*");
                    form30Adapter = new InsuranceForm30Adapter(this, 3, this);
                    ((GridView) findViewById(R.id.form30GridView)).setAdapter(form30Adapter);
                    setGridViewHeightBasedOnChildren((GridView) findViewById(R.id.form30GridView), 2);
                    break;
            }
        }

        if (form30Adapter == null && pendingModel.getForm30() != null) {
            for (int i = 0; i < pendingModel.getForm30().size(); i++) {
                form30Adapter = new InsuranceForm30Adapter(this, 2, this);
                ((GridView) findViewById(R.id.form30GridView)).setAdapter(form30Adapter);
                setGridViewHeightBasedOnChildren((GridView) findViewById(R.id.form30GridView), 2);
                if (pendingModel.getForm30().get(i).isEmpty()) {
                    findViewById(R.id.form30Layout).setVisibility(View.VISIBLE);
                } else {
                    form30Adapter.setImagePathMap(i, pendingModel.getForm30().get(i));
                }
            }
        }

        ((GridView) findViewById(R.id.form30GridView)).setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                PhotoParams params = new PhotoParams();
                if (form30Adapter.getCount() == 2)
                    params.setImageName("Form 30 Page " + (i + 1));
                else
                    params.setImageName("Prev Policy Page " + (i + 1));
                params.setOrientation(PhotoParams.CameraOrientation.PORTRAIT);
                params.setMode(PhotoParams.MODE.CAMERA_PRIORITY);
                params.setRequestCode(i);
                params.setNoOfPhotos(1);
                PhotosLibrary.collectPhotos(InsurancePendingActivity.this, params);
            }
        });
        ((GridView) findViewById(R.id.rcCopyGridView)).setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PhotoParams params = new PhotoParams();
                params.setImageName("RC Copy Page " + (position + 1));
                params.setOrientation(PhotoParams.CameraOrientation.LANDSCAPE);
                params.setMode(PhotoParams.MODE.CAMERA_PRIORITY);
                params.setRequestCode(6 + position);
                params.setNoOfPhotos(1);
                PhotosLibrary.collectPhotos(InsurancePendingActivity.this, params);
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putString("form29Image", form29ImagePath);
        outState.putString("chequeImage", chequeImagePath);

        ArrayList<String> images = new ArrayList<>();
        for (int i = 0; form30Adapter != null && i < form30Adapter.getImagePathMap().size(); i++) {
            if (form30Adapter.getImagePathMap().get(i) != null) {
                images.add(form30Adapter.getImagePathMap().get(i).getImagePath());
            } else {
                images.add(null);
            }
        }
        outState.putSerializable("form30", images);

        images.clear();
        for (int i = 0; rcCopyAdapter != null && i < rcCopyAdapter.getImagePathMap().size(); i++) {
            if (rcCopyAdapter.getImagePathMap().get(i) != null) {
                images.add(rcCopyAdapter.getImagePathMap().get(i).getImagePath());
            } else {
                images.add(null);
            }
        }
        outState.putSerializable("rcCopy", images);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        ArrayList<String> images = new ArrayList<>();
        Bundle bundle = new Bundle();

        if (savedInstanceState.containsKey("form29Image")) {
            images.clear();
            images.add(form29ImagePath);
            onActivityResult(3, RESULT_OK, new Intent().putExtra(Constants.RESULT_IMAGES, images));
        }
        if (savedInstanceState.containsKey("chequeImage")) {
            images.clear();
            images.add(chequeImagePath);
            onActivityResult(8, RESULT_OK, new Intent().putExtra(Constants.RESULT_IMAGES, images));
        }
        if (savedInstanceState.containsKey("form30")
                && savedInstanceState.getSerializable("form30") != null) {
            ArrayList<String> list = (ArrayList<String>) savedInstanceState.getSerializable("form30");
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i) == null) {
                    continue;
                }
                images.clear();
                images.add(list.get(i));
                bundle.putSerializable(Constants.RESULT_IMAGES, images);
                onActivityResult(i, RESULT_OK, new Intent().putExtras(bundle));
            }
        }
        if (savedInstanceState.containsKey("rcCopy")
                && savedInstanceState.getSerializable("rcCopy") != null) {
            ArrayList<String> list = (ArrayList<String>) savedInstanceState.getSerializable("rcCopy");
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i) == null) {
                    continue;
                }
                images.clear();
                images.add(list.get(i));
                bundle.putSerializable(Constants.RESULT_IMAGES, images);
                onActivityResult(6 + i, RESULT_OK, new Intent().putExtras(bundle));
            }
        }
    }

    @Override
    public void onClick(View view) {
        PhotoParams params;
        switch (view.getId()) {
            case R.id.form_29_layout:
                params = new PhotoParams();
                params.setImageName("Form 29");
                params.setOrientation(PhotoParams.CameraOrientation.PORTRAIT);
                params.setMode(PhotoParams.MODE.CAMERA_PRIORITY);
                params.setRequestCode(3);
                params.setNoOfPhotos(1);
                PhotosLibrary.collectPhotos(InsurancePendingActivity.this, params);
                break;

            case R.id.form_29_delete:
                Glide.with(ApplicationController.getInstance())
                        .load(android.R.color.transparent)
                        .into((ImageView) findViewById(R.id.form_29_image));
                findViewById(R.id.form_29_delete).setVisibility(View.GONE);
                form29ImagePath = "";
                if (findViewById(R.id.form_29_retry).getVisibility() == View.VISIBLE)
                    findViewById(R.id.form_29_retry).setVisibility(View.GONE);
                break;

            case R.id.cheque_layout:
                params = new PhotoParams();
                params.setImageName("Cheque Copy");
                params.setOrientation(PhotoParams.CameraOrientation.LANDSCAPE);
                params.setMode(PhotoParams.MODE.CAMERA_PRIORITY);
                params.setRequestCode(8);
                params.setNoOfPhotos(1);
                PhotosLibrary.collectPhotos(InsurancePendingActivity.this, params);
                break;

            case R.id.cheque_delete:
                Glide.with(ApplicationController.getInstance())
                        .load(android.R.color.transparent)
                        .into((ImageView) findViewById(R.id.cheque_image));
                findViewById(R.id.cheque_delete).setVisibility(View.GONE);
                chequeImagePath = "";
                if (findViewById(R.id.cheque_retry).getVisibility() == View.VISIBLE)
                    findViewById(R.id.cheque_retry).setVisibility(View.GONE);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        ArrayList<String> imagesOutput;
        switch (requestCode) {
            case 0:
            case 1:
            case 2:
                imagesOutput = (ArrayList<String>) data.getSerializableExtra(Constants.RESULT_IMAGES);
                form30Adapter.setImagePathMap(requestCode, imagesOutput.get(0));
                break;

            case 3:
                imagesOutput = (ArrayList<String>) data.getSerializableExtra(Constants.RESULT_IMAGES);
                Glide.with(ApplicationController.getInstance())
                        .load("file://" + imagesOutput.get(0))
                        .into((ImageView) findViewById(R.id.form_29_image));
                form29ImagePath = imagesOutput.get(0);
                findViewById(R.id.form_29_delete).setVisibility(View.VISIBLE);
                findViewById(R.id.form_29_delete).setOnClickListener(this);
                break;

            case 6:
            case 7:
                imagesOutput = (ArrayList<String>) data.getSerializableExtra(Constants.RESULT_IMAGES);
                rcCopyAdapter.setImagePathMap(requestCode - 6, imagesOutput.get(0));
                break;

            case 8:
                imagesOutput = (ArrayList<String>) data.getSerializableExtra(Constants.RESULT_IMAGES);
                Glide.with(ApplicationController.getInstance())
                        .load("file://" + imagesOutput.get(0))
                        .into((ImageView) findViewById(R.id.cheque_image));
                chequeImagePath = imagesOutput.get(0);
                findViewById(R.id.cheque_delete).setVisibility(View.VISIBLE);
                findViewById(R.id.cheque_delete).setOnClickListener(this);
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        OverFlowMenu.OverFlowMenuText(this, getString(R.string.done_label), 14, menu);
        OverFlowMenu.tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startImageUpload();
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    private void startImageUpload() {
        StringBuilder error = new StringBuilder();
        HashMap<String, FormItem> nameImagePath = new HashMap<>();

        if (findViewById(R.id.rcCopyLayout).getVisibility() == View.VISIBLE
                && rcCopyAdapter.getImagePathMapSize() < 1) {
            error.append(" RC Copy is required.");
        } else if (findViewById(R.id.rcCopyLayout).getVisibility() == View.VISIBLE) {
            ArrayList<FormItem> pathList = rcCopyAdapter.getImagePathMap();
            for (int count = 0; count < pathList.size(); count++) {
                if (pathList.get(count) != null) {
                    pathList.get(count).setItemText(rcCopyKeys[count]);
                    nameImagePath.put(rcCopyKeys[count], pathList.get(count));
                }
            }
        }
        if (findViewById(R.id.form_29_layout).getVisibility() == View.VISIBLE
                && form29ImagePath.isEmpty()) {
            error.append(" Form 29 is required.");
        } else if (findViewById(R.id.form_29_layout).getVisibility() == View.VISIBLE) {
            nameImagePath.put("doc_form_29", new FormItem(form29ImagePath, "doc_form_29",
                    (ProgressBar) findViewById(R.id.form_29_progressbar), (TextView) findViewById(R.id.form_29_retry)));
        }
        if (findViewById(R.id.form30Layout).getVisibility() == View.VISIBLE) {
            if (form30Adapter.getCount() == 2 && form30Adapter.getImagePathMapSize() < 2) {
                error.append(" Form 30 should have 2 images.");
            } else if (form30Adapter.getCount() == 3 && form30Adapter.getImagePathMapSize() < 1) {
                error.append(" Previous Policy Paper must have at least 1 image.");
            } else {
                ArrayList<FormItem> pathList = form30Adapter.getImagePathMap();
                String key = (form30Adapter.getCount() == 2) ? "doc_form_30_image" : "doc_prev_policy_copy_image";
                for (int count = 0; count < pathList.size(); count++) {
                    if (pathList.get(count) != null) {
                        pathList.get(count).setItemText(key + (count + 1));
                        nameImagePath.put(key + (count + 1), pathList.get(count));
                    }
                }
            }
        }
        if (findViewById(R.id.cheque_layout).getVisibility() == View.VISIBLE
                && chequeImagePath.isEmpty()) {
            error.append(" Cheque copy is required.");
        } else if (findViewById(R.id.cheque_layout).getVisibility() == View.VISIBLE) {
            nameImagePath.put("doc_cheque_copy", new FormItem(chequeImagePath, "doc_cheque_copy",
                    (ProgressBar) findViewById(R.id.cheque_progressbar), (TextView) findViewById(R.id.cheque_retry)));
        }
        if (error.length() > 0) {
            CommonUtils.showToast(this, error.toString(), Toast.LENGTH_LONG);
            return;
        }

        successCount = 0;
        totalImageCount = nameImagePath.size();
        if (progressDialog != null) {
            progressDialog.show();
        }
        initiateImageUpload(nameImagePath);
    }

    private void initiateImageUpload(HashMap<String, FormItem> namePathMap) {
        for (Map.Entry entry : namePathMap.entrySet()) {
            JSONObject params = new JSONObject();
            try {
                params.put(Constants.METHOD_LABEL, "uploadInsuranceDocuments");
                params.put("process_id", pendingModel.getProcessId());
                params.put("document_name", entry.getKey());
                params.put("car_id", pendingModel.getCarId());
                params.put("pending_upload", "1");

                ((FormItem) entry.getValue()).setJsonObject(params);
                ((FormItem) entry.getValue()).getImageProgress().setVisibility(View.VISIBLE);
                ((FormItem) entry.getValue()).setShowProgressBar(true);
                if (form30Adapter != null) form30Adapter.notifyDataSetChanged();
                if (rcCopyAdapter != null) rcCopyAdapter.notifyDataSetChanged();
                /*RetrofitRequest.uploadInsuranceDocument(((FormItem) entry.getValue()).getImagePath(),
                        params,
                        new RetroCallback(this, (FormItem) entry.getValue()));*/
                new ImageAsyncTask().execute((FormItem) entry.getValue());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void setGridViewHeightBasedOnChildren(GridView gridView, int columns) {
        ListAdapter listAdapter = gridView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        int items = listAdapter.getCount();
        int rows = 0;

        View listItem = listAdapter.getView(0, null, gridView);
        listItem.measure(0, 0);
        totalHeight = listItem.getMeasuredHeight();

        float x = 1;
        if (items > columns) {
            x = items / columns;
            rows = (int) (x + 1);
            totalHeight *= rows;
        }

        ViewGroup.LayoutParams params = gridView.getLayoutParams();
        params.height = totalHeight;
        gridView.setLayoutParams(params);

    }

    @Override
    public void onAllImagesLoaded(int resId) {

    }

    private class ImageAsyncTask extends AsyncTask<FormItem, Void, Boolean> implements View.OnClickListener {
        private FormItem formItem;

        @Override
        protected Boolean doInBackground(FormItem... params) {
            formItem = params[0];
            try {
                GeneralResponse response = RetrofitRequest.uploadInsuranceDocument(formItem.getImagePath(),
                        formItem.getJsonObject());
                return "T".equalsIgnoreCase(response.getStatus());
            } catch (RetrofitError error) {
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);
            formItem.getImageProgress().setVisibility(View.GONE);
            formItem.setShowProgressBar(false);
            if (success) {
                successCount++;
                if (successCount == totalImageCount) {
                    try {
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }
                    } catch (Exception e) {

                    }
                    CommonUtils.showToast(InsurancePendingActivity.this, "All Images Uploaded Successfully", Toast.LENGTH_SHORT);
                    setResult(RESULT_OK, new Intent().putExtras(getIntent().getExtras()));
                    finish();
                }
            } else {
                try {
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                    }
                } catch (Exception e) {

                }
                formItem.getRetry().setVisibility(View.VISIBLE);
                formItem.setShowRetry(true);
                formItem.getRetry().setOnClickListener(this);
            }
            if (rcCopyAdapter != null) rcCopyAdapter.notifyDataSetChanged();
            if (form30Adapter != null) form30Adapter.notifyDataSetChanged();
        }

        @Override
        public void onClick(View view) {
            view.setVisibility(View.GONE);
            formItem.getImageProgress().setVisibility(View.VISIBLE);
            formItem.setShowProgressBar(true);
            HashMap<String, FormItem> map = new HashMap<>();
            map.put(formItem.getItemText(), formItem);
            initiateImageUpload(map);
        }
    }
}
