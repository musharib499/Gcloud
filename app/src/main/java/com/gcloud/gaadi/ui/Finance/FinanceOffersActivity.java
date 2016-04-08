package com.gcloud.gaadi.ui.Finance;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.widget.Toast;

import com.gcloud.gaadi.ApplicationController;
import com.gcloud.gaadi.Fragments.FinanceOffersFragment;
import com.gcloud.gaadi.R;
import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.events.ApplicationCompleteEvent;
import com.gcloud.gaadi.model.CarItemModel;
import com.gcloud.gaadi.model.Finance.BankOffers;
import com.gcloud.gaadi.utils.GAHelper;
import com.gcloud.gaadi.utils.GCLog;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Lakshay on 08-09-2015.
 */
public class FinanceOffersActivity extends FinanceToolbarActivity {

    public static long startTime;
    public static long leaveTime;
    public static long totalTimeOnFinanceOffersActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getLayoutInflater().inflate(R.layout.finance_loan_offers_layout, myFrameLayout);
        ArrayList<BankOffers> bankOffers = (ArrayList<BankOffers>) getIntent().getExtras().getSerializable(Constants.BANK_OFFER);
        CarItemModel model = (CarItemModel) getIntent().getExtras().getSerializable(Constants.CAR_MODEL);

        ApplicationController.getInstance().getGAHelper().sendScreenName(GAHelper.TrackerName.APP_TRACKER, Constants.CATEGORY_FINANCE_LOAN_OFFERS);
        FinanceOffersFragment fragment = FinanceOffersFragment.getInstance(bankOffers,model);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flLoanOffers, fragment).commit();

        toolbar.setTitle(Constants.FINANCE_OFFERS_ACTIVITY);
        FinanceToolbarActivity.setStepProgress(3);

        setTitle("Loan Offers");

        ApplicationController.getEventBus().register(this);

        Calendar calendar = Calendar.getInstance();
        startTime = calendar.getTimeInMillis();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ApplicationController.getEventBus().unregister(this);
    }

    @Subscribe
    public void onApplicationCompleted(ApplicationCompleteEvent event) {
        GCLog.e(Constants.TAG, "finish availablecarsactivity");
        finish();
    }

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        //Toast.makeText(this, "Closed", Toast.LENGTH_SHORT).show();
        Calendar calender = Calendar.getInstance();
        leaveTime = calender.getTimeInMillis();
        totalTimeOnFinanceOffersActivity = totalTimeOnFinanceOffersActivity + (leaveTime - startTime);
    }
}
