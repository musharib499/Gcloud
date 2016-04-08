package com.gcloud.gaadi.ui.Finance;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gcloud.gaadi.ApplicationController;
import com.gcloud.gaadi.R;
import com.gcloud.gaadi.adapter.DocInfoGridAdapter;
import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.db.FinanceDBHelper;
import com.gcloud.gaadi.model.CarItemModel;
import com.gcloud.gaadi.model.DocumentCategories;
import com.gcloud.gaadi.model.DocumentInfo;
import com.gcloud.gaadi.model.FinanceData;
import com.gcloud.gaadi.ui.BaseActivity;
import com.gcloud.gaadi.utils.CommonUtils;
import com.gcloud.gaadi.utils.FinanceUtils;
import com.gcloud.gaadi.utils.GAHelper;
import com.gcloud.gaadi.utils.GCLog;
import com.google.gson.Gson;
import com.imageuploadlib.Fragments.CameraPriorityFragment2;
import com.imageuploadlib.Utils.ActiveDocumentMetrics;
import com.imageuploadlib.Utils.FileInfo;
import com.imageuploadlib.Utils.PhotoParams;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;

//import com.getbase.floatingactionbutton.FloatingActionButton;
//import com.getbase.floatingactionbutton.FloatingActionsMenu;

/**
 * Created by Lakshay on 02-09-2015.
 */
public class FinanceCollectImagesActivity extends BaseActivity implements DocInfoGridAdapter.DocInfoTapListener, CameraPriorityFragment2.PictureTakenListener, View.OnClickListener {

    public static HashSet<String> selectedTags = new HashSet<>();
    public static CarItemModel mCarItemModel;
    public static long timePhase2Start;
    //private com.getbase.floatingactionbutton.FloatingActionButton floatingActionButton;
    //private LinearLayout llCapturedImageLayout;
    static boolean clicked = false;
    PhotoParams params;
    ArrayList<FinanceData> financeImagesList = new ArrayList<>();
    ArrayList<DocumentCategories> documentCategoriesArrayList = new ArrayList<>();
    HashMap<String, DocumentCategories> documentCategoriesMap = new HashMap<>();
    int parentCategoryId;
    private ArrayList<String> mDocCategories;
    private int mActiveCategoryIndex = 0;
    private DrawerLayout mDrawerLayout;
    private DocumentInfo mActiveInfo;
    private ArrayList<DocumentInfo> mActiveDocumentInfos;
    private ActiveDocumentMetrics mActiveDocumentMetrics;
    private ArrayList<String> mSelectedImages = new ArrayList<>();
    private Button mNextDocument;
    private GridView mGvDocInfos;
    private DocInfoGridAdapter mGridAdapter;
    //private FloatingActionsMenu menuMultipleActions;
    private HashMap<String, ActiveDocumentMetrics> mActiveDocumentMetricsMap;
    private CameraPriorityFragment2 cameraFragment;
    private Menu menu;
    private TextView tvTotalImage;
    private ImageView ivFinanceCoachMark;
    private boolean isCoachMarkVisible = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.finance_collect_images);

        getLayoutInflater().inflate(R.layout.finance_collect_images, frameLayout);

        mCarItemModel = (CarItemModel) getIntent().getExtras().getSerializable(Constants.CAR_MODEL);
        documentCategoriesArrayList = (ArrayList<DocumentCategories>) getIntent().getExtras().getSerializable(Constants.DOCUMENT_CATEGORIES);
//        ArrayList<String> images = ApplicationController.getFinanceDB().getImagesByTags(new String[]{"1", "2"});
        if(mCarItemModel != null)
        {

            Gson gson = new Gson();
            String json = gson.toJson(mCarItemModel);

            CommonUtils.setStringSharedPreference(FinanceCollectImagesActivity.this, Constants.CAR_DATA, json);

        }
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        mDrawerLayout.openDrawer(Gravity.LEFT);

        //llCapturedImageLayout = (LinearLayout) findViewById(R.id.llImageInflator);
        mNextDocument = (Button) findViewById(R.id.bNextDocument);
        tvTotalImage = ((TextView) findViewById(R.id.tvImageCount));
        ivFinanceCoachMark = (ImageView) findViewById(R.id.coachMarksImage);
        ivFinanceCoachMark.setOnClickListener(this);
//        mDocCategories = new ArrayList<String>();
        // mDocCategories = FinanceUtils.getDocumentCategories();
        mDocCategories = FinanceUtils.getDocumentCategories(documentCategoriesArrayList);
        documentCategoriesMap = FinanceUtils.getDocumentCategoriesMap(documentCategoriesArrayList);
        selectedTags.clear();
        mGvDocInfos = (GridView) findViewById(R.id.gvDocInfos);
        String mActiveCategory = mDocCategories.get(mActiveCategoryIndex);
        //mActiveDocumentInfos = ApplicationController.getFinanceDB().getCategoryDocs(mActiveCategory);
        mActiveDocumentInfos = FinanceUtils.getCategoriesDoc(mActiveCategory, documentCategoriesMap.get(mActiveCategory));
        mGridAdapter = new DocInfoGridAdapter(this, mActiveDocumentInfos, this);
        mGvDocInfos.setAdapter(mGridAdapter);

        FragmentManager manager = getSupportFragmentManager();
        params = new PhotoParams();
        params.setNoOfPhotos(1);
        params.setMode(PhotoParams.MODE.CAMERA_PRIORITY);
        cameraFragment = CameraPriorityFragment2.getInstance(params, this);
        manager.beginTransaction().replace(R.id.content_frame1, cameraFragment).commit();

        toolbar.setBackgroundColor(getResources().getColor(R.color.black));
        getSupportActionBar().setTitle(Constants.FINANCE_APPLICATION_FORM);

        mActiveDocumentMetricsMap = FinanceUtils.getActiveDocumentMetrics();
        mActiveDocumentMetrics = mActiveDocumentMetricsMap.get(mDocCategories.get(mActiveCategoryIndex));
        ApplicationController.getInstance().getGAHelper().sendScreenName(GAHelper.TrackerName.APP_TRACKER, Constants.CATEGORY_FINANCE_COLLECT_IMAGES);

        mNextDocument.setOnClickListener(this);
        timePhase2Start = Calendar.getInstance().getTimeInMillis();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onBackPressed() {
        if(mSelectedImages.size()>0)
        new AlertDialog.Builder(FinanceCollectImagesActivity.this)
                .setTitle("Remove Images")
                .setMessage("All images will be lost. Are you sure you want to quit?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        FinanceDBHelper.deleteImages(CommonUtils.getStringSharedPreference(FinanceCollectImagesActivity.this, Constants.FINANCE_APP_ID, ""));
                        FinanceCollectImagesActivity.this.finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create().show();
        else
            super.onBackPressed();

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mSelectedImages.size() == 0) {
            cameraFragment.buttonDone.setVisibility(View.GONE);
            GCLog.e(Constants.TAG, "Set Visibility Gone");
        } else {
            cameraFragment.buttonDone.setVisibility(View.VISIBLE);
            GCLog.e(Constants.TAG, "Set Visibility Visible");
        }

        /* Handing for Application Form Next Button Only*/
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mSelectedImages.size() == 0)
                    cameraFragment.buttonDone.setVisibility(View.GONE);
            }
        }, 1200);

        if (mActiveCategoryIndex == 0) {
            tvTotalImage.setVisibility(View.GONE);
        } else {
            tvTotalImage.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.finance_documents_menu, menu);
        this.menu = menu;
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onTap(DocumentInfo info) {
        cameraFragment.tvTapToFocus.setVisibility(View.VISIBLE);
        if (validateSelection(info)){
            selectedTags.add(info.getTag()+"");
            mDrawerLayout.closeDrawer(Gravity.LEFT);
            mActiveInfo = info;
            if(mActiveCategoryIndex == 0) {
                cameraFragment.buttonDone.setVisibility(View.GONE);
            }
            /*Display Tap to focus in a text view for 3sec */
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    TranslateAnimation anim = new TranslateAnimation(0, 0, 0, -100);
                    anim.setDuration(1000);
                    cameraFragment.tvTapToFocus.startAnimation(anim);
                    cameraFragment.tvTapToFocus.setVisibility(View.GONE);
                }
            }, 3000);
        /*if (menuMultipleActions != null) {
            for (int i = 0; i <= mSelectedImages.size(); i++) {
                System.out.println("i value ==> " + i);
                menuMultipleActions.removeButton((FloatingActionButton)menuMultipleActions.getChildAt(i));
            }
        }*/
            //llCapturedImageLayout.removeAllViews();
            mSelectedImages.clear();
            tvTotalImage.setText(0 + "");
            toolbar.setSubtitle(mActiveInfo.getDocName());
            params.setImageName(mActiveInfo.getDocName());
        }
    }

    private boolean validateSelection(DocumentInfo info) {
        if(selectedTags.contains(info.getTag()+"")) {
            Toast.makeText(this, info.getDocName() + " is already selected !!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(info.getTag() == 20 && selectedTags.contains("16")){
            Toast.makeText(this, info.getDocName() + " is already selected !!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(info.getTag() == 18 && selectedTags.contains("15")){
            Toast.makeText(this, info.getDocName() + " is already selected !!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(info.getTag() == 21 && selectedTags.contains("17")){
            Toast.makeText(this, info.getDocName() + " is already selected !!", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    @Override
    public void onPictureTaken(String filePath) {
        mSelectedImages.add(filePath);
        cameraFragment.buttonDone.setVisibility(View.VISIBLE);
        tvTotalImage.setText(mSelectedImages.size() + "");

        mActiveDocumentMetrics.setMinNumberOfTypes(mActiveDocumentMetrics.getMinNumberOfTypes() - 1);
        if (mSelectedImages.size() == mActiveDocumentMetrics.getMaxNumberOfPhotos()) {
            mDrawerLayout.openDrawer(Gravity.LEFT);
            insertImages(mSelectedImages);
            if ((mActiveCategoryIndex == 0 && selectedTags.contains("8") && selectedTags.contains("9")) || parentCategoryId == 42 || parentCategoryId == 56 || mActiveCategoryIndex == mDocCategories.size()) {  //If Page 1 and page 2 selected
                mNextDocument.setVisibility(View.VISIBLE);
            } else {
                mNextDocument.setVisibility(View.GONE);
            }
        }
        if ((parentCategoryId == 5 && (selectedTags.contains("25") || selectedTags.contains("26") || selectedTags.contains("33") || selectedTags.contains("55"))) || parentCategoryId == 42 || parentCategoryId == 56) {
            if (mSelectedImages.size() == 1 && isCoachMarkVisible) {
                ivFinanceCoachMark.setVisibility(View.VISIBLE);
                isCoachMarkVisible = false;
            }
            insertImages(mSelectedImages);
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            mNextDocument.setVisibility(View.VISIBLE);
        }

        if (mActiveCategoryIndex == 0) {
            toolbar.setSubtitle("");
        }
    }

    @Override
    public void onGalleryPicsCollected(ArrayList<FileInfo> infos) {

        if (mActiveCategoryIndex != 0) {    //If selected files from gallery then not move to next category rather stay on same
            insertImages(CommonUtils.getImagesFromInfos(infos));
            mDrawerLayout.openDrawer(Gravity.LEFT);
            toolbar.setSubtitle("");
            getNextCategoryDocs();
            cameraFragment.buttonDone.setVisibility(View.GONE);

        } else {
            onPictureTaken(infos.get(0).getFilePath());
        }
    }

    private void insertImages(ArrayList<String> mSelectedImages) {
        int size = mSelectedImages.size();
        String appId = CommonUtils.getStringSharedPreference(this, Constants.FINANCE_APP_ID, "");  //this was set in financeloadingfragment
        String custId = CommonUtils.getStringSharedPreference(this, Constants.FINANCE_CUSTOMER_ID, "");  //this was set in financeloadingfragment
        String carId = CommonUtils.getStringSharedPreference(this, Constants.FINANCE_CAR_ID, "");  //this was set in financeformsactivity
        ApplicationController.getFinanceDB().insertPendingApplication(appId);
        for (int i = 0; i < size; i++) {
            FinanceData financeData = new FinanceData();
            financeData.setImagePath(mSelectedImages.get(i));
            financeData.setApplicationId(appId);
            financeData.setCustomarId(custId);
            financeData.setCarId(carId);
            financeData.setUploadStatus("");
            financeData.setTag(mActiveInfo.getTag());
            financeData.setTagTypeId(mActiveInfo.getParentId() + "");
            String requestName = appId + "_" + custId + "_" + mActiveInfo.getTag() + "_" + i;
            financeData.setRequestName(requestName);
          /*  financeData.setUploadingSequence(i + 1);
            financeData.setTotalImages(size);*/
            financeImagesList.add(financeData);
            //ApplicationController.getFinanceDB().insertFinanceImagesRecords(financeData);
            GCLog.e(Constants.TAG, "Image Inserted : " + requestName + " Image Path : " + financeData.getImagePath());
        }
    }

    @Override
    public void onPicturesCompleted() {
        insertImages(mSelectedImages);
        mDrawerLayout.openDrawer(Gravity.LEFT);
        toolbar.setSubtitle("");
        getNextCategoryDocs();
        cameraFragment.buttonDone.setVisibility(View.GONE);


        if (mActiveCategoryIndex >= mDocCategories.size() - 1){
            mNextDocument.setText("Review Documents");
            mNextDocument.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bNextDocument:
                if (mActiveCategoryIndex >= mDocCategories.size()-1) {
                    long timePhase2 = Calendar.getInstance().getTimeInMillis() - timePhase2Start;
                    ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER, Constants.CATEGORY_FINANCE_COLLECT_IMAGES,
                            Constants.CATEGORY_FINANCE_COLLECT_IMAGES, Constants.ACTION_FINANCE_PHASE2, Constants.TIME_PHASE_2, timePhase2/1000);
                    ApplicationController.getInstance().getGAHelper().sendUserTimings(GAHelper.TrackerName.APP_TRACKER, Constants.EVENT_FINANCE_PHOTOS, timePhase2, Constants.CATEGORY_FINANCE_INSPECTED_CARS, Constants.CATEGORY_FINANCE_INSPECTED_CARS);
                   int i = 1, size;
                    size = financeImagesList.size();
                    for (FinanceData financeImageData: financeImagesList) {
                        financeImageData.setUploadingSequence(i);
                        financeImageData.setTotalImages(size);
                        ApplicationController.getFinanceDB().insertFinanceImagesRecords(financeImageData);
                        i++;
                    }
                    Intent intent = new Intent(this, FinanceReviewDocumentsActivity.class);
                    intent.putExtra(Constants.DOCUMENT_CATEGORIES, documentCategoriesArrayList);
                    startActivity(intent);
                    finish();
                } else {
                    cameraFragment.buttonDone.setVisibility(View.GONE);
                    getNextCategoryDocs();
                    if (mActiveCategoryIndex == mDocCategories.size() - 1) {
                        mNextDocument.setVisibility(View.VISIBLE);
                        mNextDocument.setText("Review Documents");
                    } else
                    mNextDocument.setVisibility(View.GONE);
                }
                break;
            case R.id.coachMarksImage:
                findViewById(R.id.coachMarksImage).setVisibility(View.GONE);
                break;
        }
    }

    private void getNextCategoryDocs() {
        if (mActiveCategoryIndex < mDocCategories.size())              //Condition applied to manage for other documents only
            mActiveCategoryIndex++;
        String category = "";

        CameraPriorityFragment2.maxNumberOfImages = 0;

        if (mActiveCategoryIndex < mDocCategories.size()) {
            if(mActiveCategoryIndex == 1 || mActiveCategoryIndex == 2)
            {
                getSupportActionBar().setTitle(mDocCategories.get(mActiveCategoryIndex));
                getSupportActionBar().setSubtitle("Choose any one");
            }
            else {
                getSupportActionBar().setTitle(mDocCategories.get(mActiveCategoryIndex));
            }
            category = mDocCategories.get(mActiveCategoryIndex);
            // mActiveDocumentInfos = ApplicationController.getFinanceDB().getCategoryDocs(category);
            mActiveDocumentInfos = FinanceUtils.getCategoriesDoc(category, documentCategoriesMap.get(category));
            parentCategoryId = Integer.parseInt(documentCategoriesMap.get(category).getId());
            if (parentCategoryId == 42 || parentCategoryId == 56) {
                mNextDocument.setVisibility(View.VISIBLE);
            }
            mGridAdapter.setmDocumentInfos(mActiveDocumentInfos);
            mActiveDocumentMetrics = mActiveDocumentMetricsMap.get(category);
//            if (mActiveDocumentMetrics.getMinNumberOfTypes() == 0) {
//                mNextDocument.setVisibility(View.VISIBLE);
//            } else {
//                mNextDocument.setVisibility(View.GONE);
//            }
            TranslateAnimation animation = new TranslateAnimation(0, 0, 400, 0);
            animation.setDuration(1000);
            mGvDocInfos.startAnimation(animation);
            int title = mActiveCategoryIndex + 1;
            menu.findItem(R.id.tvCountSteps).setTitle(title + "/" + mDocCategories.size());
        } else if (mActiveCategoryIndex == mDocCategories.size()) { //specific case for category other documents
            mDrawerLayout.openDrawer(Gravity.LEFT);
            mNextDocument.setVisibility(View.VISIBLE);
        } else {
            Intent intent = new Intent(this, FinanceReviewDocumentsActivity.class);
            intent.putExtra(Constants.DOCUMENT_CATEGORIES, documentCategoriesArrayList);
            startActivity(intent);
            finish();
        }
        if ((mActiveCategoryIndex == 0 && selectedTags.contains("8") && selectedTags.contains("9")) || parentCategoryId == 42 || parentCategoryId == 56 || mActiveCategoryIndex == mDocCategories.size()) {  //If Page 1 and page 2 selected
            mNextDocument.setVisibility(View.VISIBLE);
        } else {
            mNextDocument.setVisibility(View.GONE);
        }

        isCoachMarkVisible = true;
    }

    private Bitmap getImageFromPath(String filePath) {
        System.out.print("File Path => " + filePath);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);
        System.out.print("Bitmap byte count => " + bitmap.getByteCount());
        return bitmap;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
               onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
