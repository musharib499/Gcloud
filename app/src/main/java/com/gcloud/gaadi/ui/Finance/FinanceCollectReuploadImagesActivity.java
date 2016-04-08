package com.gcloud.gaadi.ui.Finance;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.gcloud.gaadi.R;
import com.gcloud.gaadi.adapter.DocInfoGridAdapter;
import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.model.DocumentCategories;
import com.gcloud.gaadi.model.DocumentInfo;
import com.gcloud.gaadi.ui.BaseActivity;
import com.gcloud.gaadi.utils.CommonUtils;
import com.gcloud.gaadi.utils.FinanceUtils;
import com.imageuploadlib.Fragments.CameraPriorityFragment2;
import com.imageuploadlib.Utils.FileInfo;
import com.imageuploadlib.Utils.PhotoParams;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by lakshaygirdhar on 17/10/15.
 */
public class FinanceCollectReuploadImagesActivity extends BaseActivity implements DocInfoGridAdapter.DocInfoTapListener, CameraPriorityFragment2.PictureTakenListener {

    ArrayList<DocumentCategories> documentCategoriesArrayList;
    private DrawerLayout mDrawerLayout;
    private Button mNextDocument;
    private DocInfoGridAdapter mGridAdapter;
    private GridView mGvDocInfos;
    private DocumentInfo mActiveDocumentInfo;
    private TextView tvTotalImage;
    private ArrayList<String> mSelectedImages = new ArrayList<>();
    private CameraPriorityFragment2 cameraFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.collect_reupload,frameLayout);

        String mActiveCategory = getIntent().getExtras().getString(Constants.ACTIVE_CATEGORY);
        documentCategoriesArrayList = (ArrayList<DocumentCategories>) getIntent().getExtras().getSerializable(Constants.DOCUMENT_CATEGORIES);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        mDrawerLayout.openDrawer(Gravity.LEFT);

        tvTotalImage = (TextView)findViewById(R.id.tvImageCount);

        mGvDocInfos = (GridView) findViewById(R.id.gvDocInfos);

        mNextDocument = (Button) findViewById(R.id.bNextDocument);

        //mGridAdapter = new DocInfoGridAdapter(this, ApplicationController.getFinanceDB().getCategoryDocs(mActiveCategory), this);
        HashMap<String, DocumentCategories> documentCategoriesHashMap = FinanceUtils.getDocumentCategoriesMap(documentCategoriesArrayList);
        mGridAdapter = new DocInfoGridAdapter(this, FinanceUtils.getCategoriesDoc(mActiveCategory, documentCategoriesHashMap.get(mActiveCategory)), this);
        mGvDocInfos.setAdapter(mGridAdapter);

        TranslateAnimation animation = new TranslateAnimation(0, 0, CommonUtils.getDisplayMetrics(this).heightPixels, 0);
        animation.setDuration(1000);
        mGvDocInfos.startAnimation(animation);
        mGvDocInfos.setVisibility(View.VISIBLE);

        toolbar.setBackgroundColor(getResources().getColor(R.color.black));
        setTitleMsg(mActiveCategory);

        FragmentManager manager = getSupportFragmentManager();
        PhotoParams params = new PhotoParams();
        params.setMode(PhotoParams.MODE.CAMERA_PRIORITY);
        cameraFragment = CameraPriorityFragment2.getInstance(params, this);
        manager.beginTransaction().replace(R.id.content_frame1, cameraFragment).commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public void onTap(DocumentInfo info) {
        cameraFragment.tvTapToFocus.setVisibility(View.VISIBLE);
        mDrawerLayout.closeDrawer(Gravity.LEFT);
        mActiveDocumentInfo = info;
        mSelectedImages.clear();
        tvTotalImage.setText(0 + "");
        toolbar.setSubtitle(mActiveDocumentInfo.getDocName());
         /*after 3sec the visibility of text view displaying Tap to focus becomes GONE */
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
               TranslateAnimation anim = new TranslateAnimation(0, 0, 0, -100);
                anim.setDuration(1000);
                cameraFragment.tvTapToFocus.startAnimation(anim);
                cameraFragment.tvTapToFocus.setVisibility(View.GONE);
            }
        }, 3000);
    }

    @Override
    public void onPictureTaken(String filePath) {
        mSelectedImages.add(filePath);
        tvTotalImage.setText(mSelectedImages.size() + "");
        if(mActiveDocumentInfo.getParentCatName().equalsIgnoreCase(Constants.FINANCE_APPLICATION_FORM)){
            tvTotalImage.setVisibility(View.GONE);

            Intent intent = new Intent();
            mActiveDocumentInfo.setImages(mSelectedImages);
            intent.putExtra(Constants.RESULT_IMAGES,mActiveDocumentInfo);
            setResult(RESULT_OK, intent);
            finish();
        } else {
            cameraFragment.buttonDone.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onGalleryPicsCollected(ArrayList<FileInfo> infos) {
        Intent intent = new Intent();
        mActiveDocumentInfo.setImages(CommonUtils.getImagesFromInfos(infos));
        intent.putExtra(Constants.RESULT_IMAGES,mActiveDocumentInfo);
        setResult(RESULT_OK,intent);
        finish();
    }

    @Override
    public void onPicturesCompleted() {
//        insertImages();
        Intent intent = new Intent();
        mActiveDocumentInfo.setImages(mSelectedImages);
        intent.putExtra(Constants.RESULT_IMAGES,mActiveDocumentInfo);
        setResult(RESULT_OK, intent);
        finish();
    }

   /* private void insertImages() {
        int size = mSelectedImages.size();
        String appId = FinanceReuploadActivity.financeId;
        String custId = FinanceReuploadActivity.customarId;
        String carId = FinanceReuploadActivity.carId;
        ApplicationController.getFinanceDB().insertPendingApplication(appId);

        for (int i = 0; i < size; i++) {
            FinanceData financeData = new FinanceData();
            financeData.setImagePath(mSelectedImages.get(i));
            financeData.setApplicationId(appId);
            financeData.setCustomarId(custId);
            financeData.setCarId(carId);
            financeData.setParentId(mActiveDocumentInfo.getParentId());
            financeData.setUploadStatus("");
            financeData.setTag(mActiveDocumentInfo.getTag());
            financeData.setTagTypeId(mActiveDocumentInfo.getParentId() + "");
            String requestName = appId + "_" + custId + "_" + mActiveDocumentInfo.getTag() + "_" + i;
            financeData.setRequestName(requestName);
            ApplicationController.getFinanceDB().insertFinanceImagesRecords(financeData);
            GCLog.e(Constants.TAG, "Image Inserted : " + requestName + " Image Path : " + financeData.getImagePath());
        }
    }*/
}
