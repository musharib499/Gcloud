package com.gcloud.gaadi.adapter;

/**
 * Created by Mushareb Ali on 25-08-2015.
 */

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.model.LoanCaseModel;
import com.gcloud.gaadi.ui.ActiveLoanCasesFragment;
import com.gcloud.gaadi.ui.Finance.FinanceCasesStatusActivity;
import com.gcloud.gaadi.ui.PendingFragment;


public class FinancePagerAdapter extends FragmentPagerAdapter implements FinanceCasesStatusActivity.OnLoanCaseContentAvailable {
    public static String[] arryString;
    String month;
    String year;

    public FinancePagerAdapter(FragmentManager fm, String month, String year) {
        super(fm);

      /*  String approvedCasesString = approvedLoanCases==0?"":"("+approvedLoanCases+")";
        String pendingLoanCasesString = pendingLoanCases==0?"":"("+pendingLoanCases+")";
        String rejectedLoanCasesString = rejectedLoanCases==0?"":"("+rejectedLoanCases+")";
        arryString = new String[]{"Approved" + approvedCasesString, "Pending" + pendingLoanCasesString, "Rejected "+rejectedLoanCasesString};
   */
        arryString = new String[]{"Active" , "Completed"};
        this.month = month;
        this.year = year;
    }
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:

                return ActiveLoanCasesFragment.newInstance(this, month, year);
            case 1:

                return PendingFragment.newInstance(this, month, year);
           /* case 2:

                return RejectedFragment.newInstance(this);*/
        }

        return null;

    }

    @Override
    public int getCount() {
        return arryString.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return arryString[position];
    }

    @Override
    public void onContentAvailable(LoanCaseModel loanCaseModel, int loanCaseStatus) {
        if (loanCaseStatus == Constants.ACTIVE_LOAN_STATUS){
            if(loanCaseModel.getTotalRecords() == 0){
                arryString[0] = "Active";
            } else {
                arryString[0] = "Active" + " (" + loanCaseModel.getTotalRecords() + ")";
            }
        }
        else{
            if(loanCaseModel.getTotalRecords() == 0){
                arryString[1] = "Completed";
            } else {

                arryString[1] = "Completed" + " (" + loanCaseModel.getTotalRecords() + ")";
            }
        }
       /* else if (loanCaseModel instanceof RejectedLoanCaseModel){

            if(loanCaseModel.getTotalRecords() == 0){
                arryString[2] = "Rejected";
            }else{
                arryString[2] = "Rejected" + " (" + loanCaseModel.getTotalRecords() + ")";
            }
        }*/

        this.notifyDataSetChanged();
    }
}

