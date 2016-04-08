package com.gcloud.gaadi.ui.Finance;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.gcloud.gaadi.ApplicationController;
import com.gcloud.gaadi.Fragments.FinanceReviewDocumentsFragment;
import com.gcloud.gaadi.R;
import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.model.DocumentCategories;
import com.gcloud.gaadi.ui.ThankYouFinance;
import com.gcloud.gaadi.utils.GAHelper;

import java.util.ArrayList;

/**
 * Created by Lakshay on 09-09-2015.
 */
public class FinanceReviewDocumentsActivity extends AppCompatActivity implements View.OnClickListener {

    private FragmentManager mFragmentManager;
    private Button bSubmit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.finance_review_acitivity);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.tool_bar);
        mToolbar.setTitle("Review Documents");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //FinanceToolbarActivity.setStepProgress(4);
        ArrayList<DocumentCategories> mDocCategories = (ArrayList<DocumentCategories>) getIntent().getSerializableExtra(Constants.DOCUMENT_CATEGORIES);
        bSubmit = (Button) findViewById(R.id.bSubmitApplication);
        Fragment fragment = FinanceReviewDocumentsFragment.getInstance(mDocCategories);
        mFragmentManager = getSupportFragmentManager();
        mFragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

        bSubmit.setOnClickListener(this);

        ApplicationController.getInstance().getGAHelper().sendScreenName(GAHelper.TrackerName.APP_TRACKER, Constants.CATEGORY_FINANCE_REVIEW_DOC);

    }

    @Override
    public void onResume() {
        super.onResume();
        ApplicationController.getEventBus().register(this);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        ApplicationController.getEventBus().unregister(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bSubmitApplication:
//                Intent intent = new Intent(this, RetrofitImageUploadService.class);
//                startService(intent);

                Intent intent1 = new Intent(this, ThankYouFinance.class);
                startActivity(intent1);
                finish();
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(FinanceReviewDocumentsActivity.this)
                .setTitle("Remove Images")
                .setMessage("All images will be lost. Are you sure you want to quit?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        // FinanceDBHelper.deleteImages(CommonUtils.getStringSharedPreference(FinanceCollectImagesActivity.this, Constants.FINANCE_APP_ID, ""));
                        FinanceReviewDocumentsActivity.this.finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create().show();

    }
}
