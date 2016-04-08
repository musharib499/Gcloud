package com.gcloud.gaadi.ui.Finance;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.MenuItem;

import com.gcloud.gaadi.Fragments.FinanceOthersFragment;
import com.gcloud.gaadi.R;
import com.gcloud.gaadi.ui.BaseActivity;

/**
 * Created by lakshaygirdhar on 19/10/15.
 */
public class FinanceOthersActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.finance_others_activity, frameLayout);
        FinanceOthersFragment fragment = FinanceOthersFragment.getInstance();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame1,fragment).commit();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitleMsg("Loan Quote Request");
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
}
