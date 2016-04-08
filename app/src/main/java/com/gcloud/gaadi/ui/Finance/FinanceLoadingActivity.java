package com.gcloud.gaadi.ui.Finance;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.MenuItem;

import com.gcloud.gaadi.Fragments.FinanceLoadingFragment;
import com.gcloud.gaadi.R;
import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.model.CarItemModel;

/**
 * Created by Manish on 10/20/2015.
 */
public class FinanceLoadingActivity extends FinanceToolbarActivity implements FinanceLoadingFragment.FinanceLoadingListener
{
    private FragmentManager mFragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.finance_forms_activity, myFrameLayout);
        setToolBar();
        CarItemModel model = (CarItemModel) getIntent().getExtras().getSerializable(Constants.CAR_MODEL);
        FinanceLoadingFragment fragment = FinanceLoadingFragment.getInstance(this,model);
        mFragmentManager = getSupportFragmentManager();
        mFragmentManager.beginTransaction().replace(R.id.content_layout, fragment).commit();
    }

    private void setToolBar() {
//        getSupportActionBar().setTitle(Constants.FINANCE_FORMS);
        toolbar.setTitle("Loan Offers");
        FinanceToolbarActivity.setStepProgress(2);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        setTitle("Customer Information");
    }

    @Override
    public void onOthersOffers() {
        Intent intent = new Intent(this, FinanceOthersActivity.class);
        startActivity(intent);
    }
}
