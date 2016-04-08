package com.gcloud.gaadi.insurance;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.gcloud.gaadi.ApplicationController;
import com.gcloud.gaadi.R;
import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.db.InsuranceDB;
import com.gcloud.gaadi.model.FormItem;
import com.gcloud.gaadi.model.GeneralResponse;
import com.gcloud.gaadi.model.InsuranceInspectedCarData;
import com.gcloud.gaadi.ui.BaseActivity;
import com.gcloud.gaadi.ui.PaymentModeScreen;
import com.gcloud.gaadi.utils.CommonUtils;
import com.gcloud.gaadi.utils.GCLog;
import com.imageuploadlib.PhotosLibrary;
import com.imageuploadlib.Utils.PhotoParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class InsuranceDocument extends BaseActivity implements View.OnClickListener,
        InsuranceForm30Adapter.AllImagesLoaded /*, DocumentUploadComplete*/ {

    /*private String mAgentID;
    private String mInsurerID;
//    private GAHelper mGAHelper;
    private QuoteDetails mQuoteDetails;
    private String mInsuranceCaseId = "";

    TextView updatePrevPolicyCopy,
            form29Label,
            form30Label,
            sellingLabel,
            purchaseLabel,
            ncbLabel;

    Button nextButton;

    boolean brandNewCar;
    RecyclerView policyCopy, rcView, form29View, form30View, vhsellingView, vhpurchaseView, ncbView;
    ImageAdapter policyAdapter, rcAdapter, form29Adapter, form30Adapter, vhSellingAdapter, vhPurchaseAdapter, ncbAdapter;
    static int size = 0;
    public int uploadCount, maxUploadCount;
    String reqType;
    private ArrayList<String> mInsuranceCities;
    private InsuranceInspectedCarData insuranceInspectedCarData;*/
    public static HashMap<String, Boolean> AllValidPush = new HashMap<>();
    public static boolean moveToNextScreen = false;
    private final Uri INSURANCE_IMAGE_URI = Uri.parse("content://"
            + Constants.INSURANCE_CONTENT_AUTHORITY + "/" + InsuranceDB.TABLE_IMAGES);
    private final String[] rcCopyKeys = {"doc_rc_copy", "doc_rc_copy_2"};
    /*boolean mToastShown = false;
    //final String url = "http://images.forbes.com/media/lists/companies/google_416x416.jpg";
    String url = null;
    public static HashMap<String,HashMap<Integer,String>> docMap = new HashMap<>();*/
    private ImageView /*form30Image1, form30Image2, form30Image3, form30Image1Delete, form30Image2Delete, form30Image3Delete, form30Tick,*/
            /*rcCopyImage, rcCopyTick,
            rcCopyDelete, form29Tick,*/
            form29Image, form29ImageDelete;
    private LinearLayout form29Layout;
    //private RelativeLayout form30Image3Layout, rcCopyLayout;
    private GridView form30GridView, rcCopyGridView;
    //    private Button nextButton;
    private InsuranceForm30Adapter adapter, rcCopyAdapter;
    private TextView /*rcCopyRetry,*/ form29Retry, form30Title;
    private ProgressBar /*rcCopyProgressbar,*/ form29Progressbar;
    private ScrollView parentScrollView;

    private ActionBar mActionBar;
    private boolean inspected = false;
    private String form29ImagePath = "", /*rcCopyImagePath = "",*/
            processId = "", carId = "", rcCopyUrl = "", rcCopyUrl2 = "";

    private int countImagesUploaded = 0;
    private int totalImagesToUpload;

    private Bundle intentData;

    //private GCProgressDialog progressDialog;

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        /*outState.putStringArrayList(com.gcloud.gaadi.constants.Constants.INSURANCE_CITIES, mInsuranceCities);
        outState.putSerializable(Constants.QUOTE_DETAILS, mQuoteDetails);
        outState.putString(Constants.AGENT_ID, mAgentID);
        outState.putString(Constants.INSURER_ID, mInsurerID);
        outState.putString(Constants.INSURANCE_CASE_ID, mInsuranceCaseId);*/
        outState.putString("rcCopyUrl", rcCopyUrl);
        outState.putString("rcCopyUrl2", rcCopyUrl2);
        outState.putString("form29ImagePath", form29ImagePath);
        ArrayList<String> images = new ArrayList<>();
        for (int i=0; i<adapter.getImagePathMap().size(); i++) {
            if (adapter.getImagePathMap().get(i) != null) {
                images.add(adapter.getImagePathMap().get(i).getImagePath());
            } else {
                images.add(null);
            }
        }
        outState.putSerializable("form30", images);
        ArrayList<String> rcImages = new ArrayList<>();
        for (int i=0; i<rcCopyAdapter.getImagePathMap().size(); i++) {
            if (rcCopyAdapter.getImagePathMap().get(i) != null) {
                rcImages.add(rcCopyAdapter.getImagePathMap().get(i).getImagePath());
            } else {
                rcImages.add(null);
            }
        }
        outState.putSerializable("rcCopy", rcImages);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        /*mInsuranceCities = savedInstanceState.getStringArrayList(com.gcloud.gaadi.constants.Constants.INSURANCE_CITIES);
        mQuoteDetails = (QuoteDetails) savedInstanceState.getSerializable(Constants.QUOTE_DETAILS);
        mAgentID = savedInstanceState.getString(Constants.AGENT_ID);
        mInsurerID = savedInstanceState.getString(Constants.INSURER_ID);
        mInsuranceCaseId = savedInstanceState.getString(Constants.INSURANCE_CASE_ID);*/
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        ArrayList<String> images = new ArrayList<>();
        /*if (savedInstanceState.containsKey("rcCopyImagePath")
                && savedInstanceState.getString("rcCopyImagePath") != null
                && !savedInstanceState.getString("rcCopyImagePath").isEmpty()) {
            images.add(savedInstanceState.getString("rcCopyImagePath"));
            bundle.putSerializable(Constants.RESULT_IMAGES, images);
            intent.putExtras(bundle);
            onActivityResult(5, RESULT_OK, intent);
        } else*/
        if (savedInstanceState.containsKey("rcCopyUrl")
                && savedInstanceState.getString("rcCopyUrl") != null
                && !savedInstanceState.getString("rcCopyUrl").isEmpty()) {
            rcCopyUrl = savedInstanceState.getString("rcCopyUrl");
            rcCopyAdapter.setImagePathMap(0, rcCopyUrl);
        }
        if (savedInstanceState.containsKey("rcCopyUrl2")
                && savedInstanceState.getString("rcCopyUrl2") != null
                && !savedInstanceState.getString("rcCopyUrl2").isEmpty()) {
            rcCopyUrl2 = savedInstanceState.getString("rcCopyUrl2");
            rcCopyAdapter.setImagePathMap(1, rcCopyUrl2);
        }
        if (savedInstanceState.containsKey("form29ImagePath")
                && savedInstanceState.getString("form29ImagePath") != null
                && !savedInstanceState.getString("form29ImagePath").isEmpty()) {
            images.add(savedInstanceState.getString("form29ImagePath"));
            bundle.putSerializable(Constants.RESULT_IMAGES, images);
            intent.putExtras(bundle);
            onActivityResult(3, RESULT_OK, intent);
        }
        if (savedInstanceState.containsKey("form30")
                && savedInstanceState.getSerializable("form30") != null) {
            ArrayList<String> list = (ArrayList<String>) savedInstanceState.getSerializable("form30");
            for (int i=0; i<list.size(); i++) {
                if (list.get(i) == null) {
                    continue;
                }
                images.clear();
                images.add(list.get(i));
                bundle.putSerializable(Constants.RESULT_IMAGES, images);
                intent.putExtras(bundle);
                onActivityResult(i, RESULT_OK, intent);
            }
        }
        if (savedInstanceState.containsKey("rcCopy")
                && savedInstanceState.getSerializable("rcCopy") != null) {
            ArrayList<String> list = (ArrayList<String>) savedInstanceState.getSerializable("rcCopy");
            for (int i=0; i<list.size(); i++) {
                if (list.get(i) == null) {
                    continue;
                }
                images.clear();
                images.add(list.get(i));
                bundle.putSerializable(Constants.RESULT_IMAGES, images);
                intent.putExtras(bundle);
                onActivityResult(6+i, RESULT_OK, intent);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Constants.REQUEST_PERMISSION_CAMERA) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //onClick(findViewById(R.id.cheque_layout));
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_insurance_document, frameLayout);

//        mGAHelper = new GAHelper(this);

        mActionBar = getSupportActionBar();
        mActionBar.hide();
//        mActionBar.setDisplayHomeAsUpEnabled(true);

        parentScrollView = (ScrollView) findViewById(R.id.scrollView);

//        nextButton = (Button) findViewById(R.id.nextButton);
        findViewById(R.id.nextButton).setOnClickListener(this);

        form29Image = (ImageView) findViewById(R.id.form_29_image);
        form29ImageDelete = (ImageView) findViewById(R.id.form_29_delete);
//        form29Tick = (ImageView) findViewById(R.id.form_29_tick);
        form29Layout = (LinearLayout) findViewById(R.id.form_29_layout);
        form29Layout.setOnClickListener(this);
        form29Retry = (TextView) findViewById(R.id.form_29_retry);
        form29Progressbar = (ProgressBar) findViewById(R.id.form_29_progressbar);

        /*form30Image1 = (ImageView) findViewById(R.id.form_30_image_1);
        form30Image1Delete = (ImageView) findViewById(R.id.form_30_image_1_delete);
        form30Image2 = (ImageView) findViewById(R.id.form_30_image_2);
        form30Image2Delete = (ImageView) findViewById(R.id.form_30_image_2_delete);
        form30Image3 = (ImageView) findViewById(R.id.form_30_image_3);
        form30Image3Delete = (ImageView) findViewById(R.id.form_30_image_3_delete);*/
        form30Title = (TextView) findViewById(R.id.form30Title);
//        form30Tick = (ImageView) findViewById(R.id.form_30_tick);
        form30GridView = (GridView) findViewById(R.id.form30GridView);
//        form30Image3Layout = (RelativeLayout) findViewById(R.id.form_30_image_3_layout);

        rcCopyGridView = (GridView) findViewById(R.id.rcCopyGridView);
        /*rcCopyImage = (ImageView) findViewById(R.id.rc_copy_image);
//        rcCopyTick = (ImageView) findViewById(R.id.rc_copy_tick);
        rcCopyDelete = (ImageView) findViewById(R.id.rc_copy_delete);
        rcCopyLayout = (RelativeLayout) findViewById(R.id.rc_copy_layout);
        rcCopyLayout.setOnClickListener(this);
        rcCopyRetry = (TextView) findViewById(R.id.rc_copy_retry);
        rcCopyProgressbar = (ProgressBar) findViewById(R.id.rc_copy_progressbar);*/

        //progressDialog = new GCProgressDialog(this, this, "Please wait while images are uploading");
        //progressDialog.setCancelable(false);

        intentData = getIntent().getExtras();
        if (intentData != null) {
            rcCopyAdapter = new InsuranceForm30Adapter(this, 2, this);
            rcCopyGridView.setAdapter(rcCopyAdapter);
            setGridViewHeightBasedOnChildren(rcCopyGridView, 2);

            processId = intentData.getString(Constants.PROCESS_ID);
            carId = ((InsuranceInspectedCarData) intentData.get(Constants.INSURANCE_INSPECTED_CAR_DATA)).getCarId();
            GCLog.e("processID: " + processId);
            if (intentData.containsKey(Constants.SELECTED_CASE)
                    && intentData.getString(Constants.SELECTED_CASE).equalsIgnoreCase(Constants.INSPECTED_CARS)) {
                GCLog.e("inspected present");
                inspected = true;
                adapter = new InsuranceForm30Adapter(this, 2, this);
                rcCopyUrl = ((InsuranceInspectedCarData) intentData.get(Constants.INSURANCE_INSPECTED_CAR_DATA)).getRcUrl();
                rcCopyUrl2 = ((InsuranceInspectedCarData) intentData.get(Constants.INSURANCE_INSPECTED_CAR_DATA)).getRcUrl2();
                if (!rcCopyUrl.isEmpty()) {
                    rcCopyAdapter.setImagePathMap(0, rcCopyUrl);
                }
                if (!rcCopyUrl2.isEmpty()) {
                    rcCopyAdapter.setImagePathMap(1, rcCopyUrl2);
                }
            } else {
                GCLog.e("inspected not present");
                form30Title.setText("Previous Policy Papers");
                form29Layout.setVisibility(View.GONE);
//                form30Image3Layout.setVisibility(View.VISIBLE);
                adapter = new InsuranceForm30Adapter(this, 3, this);
            }
            form30GridView.setAdapter(adapter);
            setGridViewHeightBasedOnChildren(form30GridView, 2);
        } else {
            GCLog.e("intentData is null");
        }

        form30GridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                PhotoParams params = new PhotoParams();
                if (inspected)
                    params.setImageName("Form 30 Page " + (i + 1));
                else
                    params.setImageName("Prev Policy Page " + (i + 1));
                params.setMode(PhotoParams.MODE.CAMERA_PRIORITY);
                params.setRequestCode(i);
                params.setNoOfPhotos(1);
                PhotosLibrary.collectPhotos(InsuranceDocument.this, params);
            }
        });
        rcCopyGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PhotoParams params = new PhotoParams();
                params.setImageName("RC Copy Page " + (position + 1));
                params.setOrientation(PhotoParams.CameraOrientation.LANDSCAPE);
                params.setMode(PhotoParams.MODE.CAMERA_PRIORITY);
                params.setRequestCode(6 + position);
                params.setNoOfPhotos(1);
                PhotosLibrary.collectPhotos(InsuranceDocument.this, params);
            }
        });

        parentScrollView.smoothScrollTo(0, 0); // To prevent scroll moving to gridview automatically

        /*Intent intent = getIntent();
        if (intent != null) {
            mQuoteDetails = (QuoteDetails) intent.getSerializableExtra(Constants.QUOTE_DETAILS);
            mInsuranceCities = intent.getStringArrayListExtra(com.gcloud.gaadi.constants.Constants.INSURANCE_CITIES);
            brandNewCar = intent.getBooleanExtra(Constants.INSURANCE_IS_NEW_CAR, false);
            reqType = intent.getStringExtra(Constants.INSURANCE_REQUEST_TYPE);
            mInsuranceCaseId = intent.getStringExtra(Constants.INSURANCE_CASE_ID);
            mAgentID = intent.getStringExtra(Constants.AGENT_ID);
            mInsurerID = intent.getStringExtra(Constants.INSURER_ID);
            insuranceInspectedCarData = (InsuranceInspectedCarData) intent.getSerializableExtra(Constants.INSURANCE_INSPECTED_CAR_DATA);
            if(insuranceInspectedCarData!=null) {
                url = insuranceInspectedCarData.getRcUrl();
            }
        }


        updatePrevPolicyCopy = (TextView) findViewById(R.id.textView7);
        policyCopy = (RecyclerView) findViewById(R.id.policyView);
        rcView = (RecyclerView) findViewById(R.id.rcView);
        form29View = (RecyclerView) findViewById(R.id.form29View);
        form30View = (RecyclerView) findViewById(R.id.form30View);
        vhsellingView = (RecyclerView) findViewById(R.id.vhSellingView);
        vhpurchaseView = (RecyclerView) findViewById(R.id.vhPurchaseView);
        ncbView = (RecyclerView) findViewById(R.id.ncbView);
        form29Label = (TextView) findViewById(R.id.textView9);
        form30Label = (TextView) findViewById(R.id.textView10);
        sellingLabel = (TextView) findViewById(R.id.textView11);
        purchaseLabel = (TextView) findViewById(R.id.textView12);
        ncbLabel = (TextView) findViewById(R.id.textView13);

        nextButton = (Button) findViewById(R.id.nextButton);

        nextButton.setOnClickListener(this);

        if(savedInstanceState == null){
            initilizeMap();
        }

        LinearLayoutManager lm = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);

        policyCopy.setLayoutManager(lm);

        policyAdapter = new ImageAdapter(this, Constants.PREV_POLICY_COPY, 3, Constants.policyCode, mAgentID, mInsuranceCaseId,docMap.get(Constants.PREV_POLICY_COPY));

        policyCopy.setAdapter(policyAdapter);

        policyCopy.setHasFixedSize(true);

        LinearLayoutManager lmRc = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);

        rcView.setLayoutManager(lmRc);

        rcAdapter = new ImageAdapter(this, Constants.RC_COPY, 1, Constants.rcCode, mAgentID, mInsuranceCaseId, docMap.get(Constants.RC_COPY),url);

        rcView.setAdapter(rcAdapter);

        rcView.setHasFixedSize(true);

        LinearLayoutManager lmform29 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);

        form29View.setLayoutManager(lmform29);

        form29Adapter = new ImageAdapter(this, Constants.FORM29_COPY, 1, Constants.form29Code, mAgentID, mInsuranceCaseId,docMap.get(Constants.FORM29_COPY));

        form29View.setAdapter(form29Adapter);

        form29View.setHasFixedSize(true);

        LinearLayoutManager lmform30 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);

        form30View.setLayoutManager(lmform30);

        form30Adapter = new ImageAdapter(this, Constants.FORM30_COPY, 2, Constants.form30Code, mAgentID, mInsuranceCaseId,docMap.get(Constants.FORM30_COPY));

        form30View.setAdapter(form30Adapter);

        form30View.setHasFixedSize(true);

        LinearLayoutManager lmvhsell = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);

        vhsellingView.setLayoutManager(lmvhsell);

        vhSellingAdapter = new ImageAdapter(this, Constants.VH_SELLING_COPY, 1, Constants.sellCode, mAgentID, mInsuranceCaseId,docMap.get(Constants.VH_SELLING_COPY));

        vhsellingView.setAdapter(vhSellingAdapter);

        vhsellingView.setHasFixedSize(true);

        LinearLayoutManager lmvhp = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);

        vhpurchaseView.setLayoutManager(lmvhp);

        vhPurchaseAdapter = new ImageAdapter(this, Constants.VH_PURCHASE_COPY, 1, Constants.purchaseCode, mAgentID, mInsuranceCaseId,docMap.get(Constants.VH_PURCHASE_COPY));

        vhpurchaseView.setAdapter(vhPurchaseAdapter);

        vhpurchaseView.setHasFixedSize(true);

        LinearLayoutManager lmncb = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);

        ncbView.setLayoutManager(lmncb);

        ncbAdapter = new ImageAdapter(this, Constants.NCB_COPY, 2, Constants.ncbCode, mAgentID, mInsuranceCaseId,docMap.get(Constants.NCB_COPY));

        ncbView.setAdapter(ncbAdapter);

        ncbView.setHasFixedSize(true);

        if (brandNewCar) {
            updatePrevPolicyCopy.setText("Invoice Copy");
        }

        if (reqType!=null && !(reqType.equals("Owner Changed, Policy Not Expired, NCB Transfer")
                || reqType.equals("Owner Changed, Policy Not Expired, No NCB Transfer")
                || reqType.equals("Owner Changed, Policy Expired"))) {
            form29Label.setVisibility(View.GONE);
            form29View.setVisibility(View.GONE);
            form30Label.setVisibility(View.GONE);
            form30View.setVisibility(View.GONE);
        }
        if (reqType!=null &&!reqType.equals("Owner Changed, Policy Expired")) {
            sellingLabel.setVisibility(View.GONE);
            vhsellingView.setVisibility(View.GONE);
            purchaseLabel.setVisibility(View.GONE);
            vhpurchaseView.setVisibility(View.GONE);
        }
        if (reqType!=null && !reqType.equals("Owner Changed, Policy Not Expired, NCB Transfer")) {
            ncbLabel.setVisibility(View.GONE);
            ncbView.setVisibility(View.GONE);
        }


        AllValidPush.put(Constants.PREV_POLICY_COPY, false);
        AllValidPush.put(Constants.RC_COPY, false);
        AllValidPush.put(Constants.FORM29_COPY, false);
        AllValidPush.put(Constants.FORM30_COPY, false);
        AllValidPush.put(Constants.VH_SELLING_COPY, false);
        AllValidPush.put(Constants.VH_PURCHASE_COPY, false);
        AllValidPush.put(Constants.NCB_COPY, false);*/

    }


    public void initilizeMap() {
        /*docMap.clear();
        ImageAdapter.imageItemMap.clear();
        AllValidPush.clear();
        size = 0;
        moveToNextScreen = false;*/
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        CommonUtils.startActivityTransition(InsuranceDocument.this, Constants.TRANSITION_RIIGHT);
    }

    public void onClick(View v) {
        ArrayList<String> photoName;
        PhotoParams params;
        switch (v.getId()) {

            case R.id.iv_actionBack:
                onBackPressed();
                break;

            case R.id.nextButton:
//                mToastShown = false;
                // imageUploadOnServer();      //TODO need to uncomment this and remove launchInsuranceCustomerScreen();
//                launchInsuranceCustomerScreen();
                String error = "";
                ArrayList<FormItem> pathList = adapter.getImagePathMap();
                if (inspected) {
                    if (rcCopyUrl.isEmpty() && rcCopyUrl2.isEmpty() && rcCopyAdapter.getImagePathMapSize() < 1)
                        error += "RC Copy is required. ";
                    if (adapter.getImagePathMapSize() < 2)
                        error += "Form 30 should have 2 images. ";
                    if (form29ImagePath.isEmpty())
                        error += "Form 29 is blank.";
                } else {
                    GCLog.e("gaurav not inspected");
                    if (adapter.getImagePathMapSize() < 1)
                        error += "Previous Policy should have at least 1 image. ";
                    if (rcCopyAdapter.getImagePathMapSize() < 1)
                        error += "RC Copy is blank.";
                }
                if (error.isEmpty()) {
                    FormItem item;
                    HashMap<String, FormItem> namePathMap = new HashMap<>();
                    for (int count = 0; count < pathList.size(); count++) {
                        String key = "";
                        if (inspected) {
                            key = "doc_form_30_image";
                        } else {
                            key = "doc_prev_policy_copy_image";
                        }
                        if (pathList.get(count) != null) {
                            pathList.get(count).setItemText(key + (count + 1));
                            namePathMap.put(key + (count + 1), pathList.get(count));
                        }
                    }
                    if (inspected) {
                        item = new FormItem();
                        item.setImageProgress(form29Progressbar);
                        item.setRetry(form29Retry);
                        item.setImagePath(form29ImagePath);
                        item.setItemText("doc_form_29");
                        namePathMap.put("doc_form_29", item);
                        if (rcCopyAdapter.getImagePathMapSize() > 0) {
                            ArrayList<FormItem> rcPathList = rcCopyAdapter.getImagePathMap();
                            for (int i = 0; i < rcPathList.size(); i++) {
                                if (rcPathList.get(i) != null) {
                                    rcPathList.get(i).setItemText(rcCopyKeys[i]);
                                    namePathMap.put(rcCopyKeys[i], rcPathList.get(i));
                                }
                            }
                            /*item = new FormItem();
                            item.setImageProgress(rcCopyProgressbar);
                            item.setRetry(rcCopyRetry);
                            item.setImagePath(rcCopyImagePath);
                            item.setItemText("doc_rc_copy");
                            namePathMap.put("doc_rc_copy", item);*/
                        }
                    } else {
                        ArrayList<FormItem> rcPathList = rcCopyAdapter.getImagePathMap();
                        for (int i = 0; i < rcPathList.size(); i++) {
                            if (rcPathList.get(i) != null) {
                                rcPathList.get(i).setItemText(rcCopyKeys[i]);
                                namePathMap.put(rcCopyKeys[i], rcPathList.get(i));
                            }
                        }
                        /*item = new FormItem();
                        item.setImageProgress(rcCopyProgressbar);
                        item.setRetry(rcCopyRetry);
                        item.setImagePath(rcCopyImagePath);
                        item.setItemText("doc_rc_copy");
                        namePathMap.put("doc_rc_copy", item);*/
                    }
                    totalImagesToUpload = namePathMap.size();
                    GCLog.e("total images to upload: " + totalImagesToUpload);
                    countImagesUploaded = 0;
                    /*if (progressDialog != null && !progressDialog.isShowing()) {
                        progressDialog.show();
                    }*/
                    insertImagesData(namePathMap);
                    //initiateImageUpload(namePathMap);
                } else {
                    Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.form_29_layout:
                params = new PhotoParams();
                params.setImageName("Form 29");
                params.setOrientation(PhotoParams.CameraOrientation.PORTRAIT);
                params.setMode(PhotoParams.MODE.CAMERA_PRIORITY);
                params.setRequestCode(3);
                params.setNoOfPhotos(1);
                PhotosLibrary.collectPhotos(InsuranceDocument.this, params);
                break;

            /*case R.id.rc_copy_layout:
                params = new PhotoParams();
                params.setImageName("RC Copy");
                params.setMode(PhotoParams.MODE.CAMERA_PRIORITY);
                params.setOrientation(PhotoParams.CameraOrientation.LANDSCAPE);
                params.setRequestCode(5);
                params.setNoOfPhotos(1);
                PhotosLibrary.collectPhotos(InsuranceDocument.this, params);
                break;*/

            case R.id.form_29_delete:
//                form29Image.setImageBitmap(null);
                Glide.with(ApplicationController.getInstance())
                        .load(android.R.color.transparent)
                        .into(form29Image);
                hide(form29ImageDelete);
//                form29Tick.setImageResource(R.drawable.tick);
                form29ImagePath = "";
                if (form29Retry.getVisibility() == View.VISIBLE)
                    form29Retry.setVisibility(View.GONE);
                break;

            /*case R.id.rc_copy_delete:
//                rcCopyImage.setImageBitmap(null);
                Glide.with(ApplicationController.getInstance())
                        .load(android.R.color.transparent)
                        .into(rcCopyImage);
                hide(rcCopyDelete);
//                rcCopyTick.setImageResource(R.drawable.tick);
                rcCopyImagePath = "";
                if (rcCopyRetry.getVisibility() == View.VISIBLE)
                    rcCopyRetry.setVisibility(View.GONE);
                break;*/
        }
    }

    private void insertImagesData(HashMap<String, FormItem> namePathMap) {
        ProgressDialog progressDialog = ProgressDialog.show(this, "", getString(R.string.please_wait), true);
        /*Intent databaseIntent = new Intent(this, DatabaseInsertionService.class);
        databaseIntent.putExtra(Constants.ACTION, "insert");
        databaseIntent.putExtra(Constants.PROVIDER_URI, INSURANCE_IMAGE_URI);*/

        ArrayList<ContentValues> valuesList = new ArrayList<>();
        for (Map.Entry entry : namePathMap.entrySet()) {
            ContentValues values = new ContentValues();
            values.put(InsuranceDB.COLUMN_CAR_ID, (carId != null && !carId.isEmpty()) ? Integer.parseInt(carId) : 0);
            values.put(InsuranceDB.COLUMN_PROCESS_ID, Integer.parseInt(processId));
            values.put(InsuranceDB.COLUMN_DOC_NAME, (String) entry.getKey());
            values.put(InsuranceDB.COLUMN_IMAGE_PATH, ((FormItem) entry.getValue()).getImagePath());

            /*databaseIntent.putExtra(Constants.CONTENT_VALUES, values);
            startService(databaseIntent);*/
            valuesList.add(values);
        }
        if (valuesList.size() > 0) {
            getContentResolver().bulkInsert(INSURANCE_IMAGE_URI, valuesList.toArray(new ContentValues[valuesList.size()]));
        }

        startService(new Intent(this, InsuranceBackgroundImageUploadService.class));

        Intent intent = new Intent(this, PaymentModeScreen.class);
        intent.putExtras(intentData);

        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        startActivityForResult(intent, Constants.INSURANCE_CASE_FOR_INSPECTED_CAR);
    }

    private void initiateImageUpload(HashMap<String, FormItem> namePathMap) {
//        CommonUtils.showToast(this, "Please wait while images are uploading", Toast.LENGTH_SHORT);
        for (Map.Entry entry : namePathMap.entrySet()) {
            GCLog.e(inspected + " document name: " + entry.getKey());
            JSONObject params = new JSONObject();
            try {
                params.put(Constants.METHOD_LABEL, "uploadInsuranceDocuments");
                params.put("process_id", processId);
                params.put("document_name", entry.getKey());
                params.put("car_id", carId);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            show(((FormItem) entry.getValue()).getImageProgress());
            ((FormItem) entry.getValue()).setShowProgressBar(true);
            adapter.notifyDataSetChanged();
            rcCopyAdapter.notifyDataSetChanged();
            /*RetrofitRequest.uploadInsuranceDocument(((FormItem) entry.getValue()).getImagePath(),
                    params,
                    new RetroCallback(this, (FormItem) entry.getValue()));*/
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ArrayList<String> imagesOutput;
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 0:
                case 1:
                case 2:
                    imagesOutput = (ArrayList<String>) data.getSerializableExtra(Constants.RESULT_IMAGES);
                    adapter.setImagePathMap(requestCode, imagesOutput.get(0));
                    break;
                case 3:
                    imagesOutput = (ArrayList<String>) data.getSerializableExtra(Constants.RESULT_IMAGES);
                    GCLog.e("form 29 path: " + imagesOutput.get(0));
                    Glide.with(ApplicationController.getInstance())
                            .load("file://" + imagesOutput.get(0))
                            .into(form29Image);
                    form29ImagePath = imagesOutput.get(0);
//                    form29Image.setImageURI(Uri.fromFile(new File(imagesOutput.get(0))));
                    show(form29ImageDelete);
                    form29ImageDelete.setOnClickListener(this);
//                    form29Tick.setImageResource(R.drawable.tick_active);
                    break;
                case 6:
                case 7:
                    imagesOutput = (ArrayList<String>) data.getSerializableExtra(Constants.RESULT_IMAGES);
                    rcCopyAdapter.setImagePathMap(requestCode-6, imagesOutput.get(0));
                    break;
                /*case 5:
                    imagesOutput = (ArrayList<String>) data.getSerializableExtra(Constants.RESULT_IMAGES);
                    GCLog.e("rc copy path: " + imagesOutput.get(0));
                    rcCopyImagePath = imagesOutput.get(0);
                    rcCopyUrl = "";
                    Glide.with(ApplicationController.getInstance())
                            .load("file://" + rcCopyImagePath)
                            .into(rcCopyImage);
//                    rcCopyImage.setImageURI(Uri.fromFile(new File(rcCopyImagePath)));
                    show(rcCopyDelete);
                    rcCopyDelete.setOnClickListener(this);
//                    rcCopyTick.setImageResource(R.drawable.tick_active);
                    break;*/
                case Constants.INSURANCE_CASE_FOR_INSPECTED_CAR:
                    setResult(RESULT_OK);
                    finish();
                    finishActivity(Constants.INSURANCE_CASE_FOR_INSPECTED_CAR);
                    break;
            }
        }
    }
/*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
        ArrayList<String> list = null;
        if (data != null) {
            list = data.getStringArrayListExtra(Constants.RESULT_IMAGES);
        }

            HashMap<Integer,String> map = docMap.get(CommonUtils.getPolicyName(requestCode));
            if(map == null) {
                map = new HashMap<>();
            }
            if(list!=null) {
                map.put(ImageAdapter.position_selected, list.get(0));
                docMap.put(CommonUtils.getPolicyName(requestCode), map);
            }
            switch (requestCode) {
                case Constants.policyCode:
                    policyAdapter.setImageOnView(map);
                    break;

                case Constants.rcCode:
                    rcAdapter.setImageOnView(map);
                    break;

                case Constants.form29Code:
                    form29Adapter.setImageOnView(map);

                    break;

                case Constants.form30Code:
                    form30Adapter.setImageOnView(map);
                    break;

                case Constants.sellCode:
                    vhSellingAdapter.setImageOnView(map);
                    break;

                case Constants.purchaseCode:
                    vhPurchaseAdapter.setImageOnView(map);
                    break;

                case Constants.ncbCode:
                    ncbAdapter.setImageOnView(map);
                    break;

                case com.gcloud.gaadi.constants.Constants.INSURANCE_CASE_FOR_INSPECTED_CAR:
                    setResult(RESULT_OK);
                    finish();
                    finishActivity(Constants.INSURANCE_CASE_FOR_INSPECTED_CAR);
                    break;

            }
        }
    }
    public void imageUploadOnServer() {
        GCLog.i(Constants.TAG, "upload count in imageUploadOnServer=" + uploadCount);
        GCLog.i(Constants.TAG, "upload count in imageUploadOnServer =" + maxUploadCount);
        maxUploadCount = 0;
        uploadCount = 0;

        makeRetrofitRequest(Constants.PREV_POLICY_COPY);
        if(url == null) {
            makeRetrofitRequest(Constants.RC_COPY);
        }
        if (reqType!=null && (reqType.equals("Owner Changed, Policy Not Expired, NCB Transfer")
                || reqType.equals("Owner Changed, Policy Not Expired, No NCB Transfer")
                || reqType.equals("Owner Changed, Policy Expired"))) {
            makeRetrofitRequest(Constants.FORM29_COPY);
            makeRetrofitRequest(Constants.FORM30_COPY);
        }
        if (reqType!=null && reqType.equals("Owner Changed, Policy Expired")) {
            makeRetrofitRequest(Constants.VH_SELLING_COPY);
            makeRetrofitRequest(Constants.VH_PURCHASE_COPY);
        }
        if (reqType!=null && reqType.equals("Owner Changed, Policy Not Expired, NCB Transfer")) {
            makeRetrofitRequest(Constants.NCB_COPY);
        }

    }


    public void makeRetrofitRequest(final String key) {
        HashMap<Integer,String> map = docMap.get(key);
        if (map != null) {
            if (key == Constants.FORM30_COPY) {
                if (map.size() == 2)
                    uploadImage(key, map);
                else if (mToastShown == false) {
                    CommonUtils.showToast(this, "Upload more Image for " + key.toUpperCase(), Toast.LENGTH_LONG);
                    mToastShown = true;
                }
            } else {
                uploadImage(key, map);
            }
        } else if (mToastShown == false) {
            if (key.equals(Constants.PREV_POLICY_COPY)) {
                if (brandNewCar) {
                    CommonUtils.showToast(this, "Upload Image for INVOICECOPY", Toast.LENGTH_LONG);
                    mToastShown = true;
                } else if (reqType!=null && reqType.equalsIgnoreCase("Rollover")) {   // Mandatory for rollOver Case but not for insepected car
                    CommonUtils.showToast(this, "Upload Image for " + key.toUpperCase(), Toast.LENGTH_LONG);
                    mToastShown = true;
                }
            } else if(!(key.equals(Constants.VH_SELLING_COPY) || key.equals(Constants.VH_PURCHASE_COPY))){
                CommonUtils.showToast(this, "Upload Image for " + key.toUpperCase(), Toast.LENGTH_LONG);
                mToastShown = true;
            }
        }
    }

    private void uploadImage(String key, HashMap<Integer,String> map) {
        size = map.size();
        maxUploadCount += size;
        GCLog.i(Constants.TAG, "upload count in makeRetrofitRequest for key =" + uploadCount + " " + key);
        GCLog.i(Constants.TAG, "upload count in makeRetrofitRequest =" + maxUploadCount + " " + key);
        for (Map.Entry<Integer,String> item : map.entrySet()) {
            HashMap<String, String> params = new HashMap<>();
            params.put(Constants.METHOD_LABEL, "putInsuranceDocumentImages");
            params.put(Constants.INSURANCE_AGENT_ID, mAgentID);
            params.put(Constants.IMAGE_NAME, item.getValue());
            params.put(Constants.INSURANCE_CASE_ID, mInsuranceCaseId);
            params.put(Constants.DOCUMENT_TYPE, key);
            RetrofitRequest.putInsuranceDocuments("upload_doc.php",
                    item.getValue(),
                    params,
                    new DocumentUploadCallback(this, ImageAdapter.imageItemMap.get(key + item.getValue()), this));
        }
    }

    private void launchInsuranceCustomerScreen() {
        Intent intent = new Intent(this, InsuranceCustomerDetailScreen.class);
        intent.putStringArrayListExtra(com.gcloud.gaadi.constants.Constants.INSURANCE_CITIES, mInsuranceCities);
        intent.putExtra(Constants.INSURANCE_CASE_ID, mInsuranceCaseId);
        intent.putExtra(Constants.INSURER_ID, mInsurerID);
        intent.putExtra(Constants.AGENT_ID, mAgentID);
        intent.putExtra(Constants.QUOTE_DETAILS, mQuoteDetails);
        startActivityForResult(intent, com.gcloud.gaadi.constants.Constants.INSURANCE_CASE_FOR_INSPECTED_CAR);
        CommonUtils.startActivityTransition(InsuranceDocument.this, Constants.TRANSITION_LEFT);
    }
    @Override
    public void onImageUploadComplete(ImageItem imageItem) {
        GCLog.i(Constants.TAG, "upload count in onImageUploadComplete=" + uploadCount);
        GCLog.i(Constants.TAG, "upload count in onImageUploadComplete =" + maxUploadCount);
        GCLog.i(Constants.TAG, "Move to next Screen 1 on ImageUploadComplete = " + moveToNextScreen);
        uploadCount++;
        if (uploadCount == maxUploadCount) {
            moveToNextScreen = true;
        }
        GCLog.i(Constants.TAG, "Move to next Screen 2 on ImageUploadComplete = " + moveToNextScreen);
        if (moveToNextScreen) {

            //HashMap<Integer,String> map = docMap.
            for (Map.Entry<String, Boolean> entry : AllValidPush.entrySet()) {
                if (reqType!=null && (reqType.equals("Owner Changed, Policy Not Expired, NCB Transfer")
                        || reqType.equals("Owner Changed, Policy Not Expired, No NCB Transfer")
                        || reqType.equals("Owner Changed, Policy Expired")) && (entry.getKey().equals(Constants.FORM29_COPY) || entry.getKey().equals(Constants.FORM30_COPY))) {
                    if (entry.getValue() == false) {
                        moveToNextScreen = false;
                        break;
                    }
                } else if (reqType!=null && reqType.equals("Owner Changed, Policy Not Expired, NCB Transfer") && entry.getKey().equals(Constants.NCB_COPY)) {
                    if (entry.getValue() == false) {
                        moveToNextScreen = false;
                        break;
                    }
                } *//*else if (reqType.equals("Owner Changed, Policy Expired") && (entry.getKey().equals(Constants.VH_SELLING_COPY) || entry.getKey().equals(Constants.VH_PURCHASE_COPY))) {
                    if (entry.getValue() == false) {
                        moveToNextScreen = false;
                        break;
                    }
                } *//*else if (((entry.getKey().equals(Constants.PREV_POLICY_COPY) && reqType!=null && reqType.equalsIgnoreCase("Rollover")) || (url == null && entry.getKey().equals(Constants.RC_COPY))) && entry.getValue() == false) {
                    moveToNextScreen = false;
                    break;
                }
            }
            if (moveToNextScreen)
            {
                launchInsuranceCustomerScreen();// To launch the payment screen
            }
        }
    }*/

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
//        form30Tick.setImageResource(resId);
    }

    private void show(View view) {
        view.setVisibility(View.VISIBLE);
    }

    private void hide(View view) {
        view.setVisibility(View.GONE);
    }

    private class RetroCallback implements Callback<GeneralResponse>, View.OnClickListener {

        private Context context;
        private FormItem formItem;

        public RetroCallback(Context context, FormItem item) {
            this.context = context;
            this.formItem = item;

            hide(formItem.getRetry());
            formItem.setShowRetry(false);
            adapter.notifyDataSetChanged();
            rcCopyAdapter.notifyDataSetChanged();
        }

        @Override
        public void success(GeneralResponse response, Response res) {
            GCLog.e("Retro status: " + response.getStatus() + " message: " + response.getError() + " " + response.getMessage());
            if (response.getStatus().equalsIgnoreCase("T")) {
                countImagesUploaded++;
                if (countImagesUploaded == totalImagesToUpload) {
                    CommonUtils.showToast(InsuranceDocument.this, "All images uploaded successfully", Toast.LENGTH_SHORT);
                    Intent intent = new Intent(context, PaymentModeScreen.class);
                    intent.putExtras(intentData);
                    startActivityForResult(intent, Constants.INSURANCE_CASE_FOR_INSPECTED_CAR);
                    /*if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }*/
                }
            } else {
                /*if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }*/
                GCLog.e("gaurav failed");
                show(formItem.getRetry());
                formItem.setShowRetry(true);
                formItem.getRetry().setOnClickListener(this);
            }
            hide(formItem.getImageProgress());
            formItem.setShowProgressBar(false);
            adapter.notifyDataSetChanged();
            rcCopyAdapter.notifyDataSetChanged();
        }

        @Override
        public void failure(RetrofitError error) {
            /*if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }*/
            hide(formItem.getImageProgress());
            formItem.setShowProgressBar(false);
            show(formItem.getRetry());
            formItem.setShowRetry(true);
            formItem.getRetry().setOnClickListener(this);
            GCLog.e("RetroError: " + error.toString());
            adapter.notifyDataSetChanged();
            rcCopyAdapter.notifyDataSetChanged();
        }

        @Override
        public void onClick(View view) {
            GCLog.e("gaurav onclick called");
            hide(view);
            show(formItem.getImageProgress());
            formItem.setShowProgressBar(true);
            HashMap<String, FormItem> map = new HashMap<>();
            map.put(formItem.getItemText(), formItem);
            initiateImageUpload(map);
        }
    }
}
