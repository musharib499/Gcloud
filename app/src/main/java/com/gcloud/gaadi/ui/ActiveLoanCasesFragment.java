package com.gcloud.gaadi.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gcloud.gaadi.ApplicationController;
import com.gcloud.gaadi.EndlessScroll.RecyclerViewEndlessScrollListener;
import com.gcloud.gaadi.R;
import com.gcloud.gaadi.adapter.FinanceLoanCasesListAdapter;
import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.events.SetTabTextEvent;
import com.gcloud.gaadi.model.Finance.FinanceLoanCasesListModel;
import com.gcloud.gaadi.model.LoanApplication;
import com.gcloud.gaadi.ui.Finance.FinanceCasesStatusActivity;
import com.gcloud.gaadi.utils.CommonUtils;
import com.gcloud.gaadi.utils.FinanceUtils;
import com.gcloud.gaadi.utils.GCLog;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link ActiveLoanCasesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

public class ActiveLoanCasesFragment extends android.support.v4.app.Fragment implements Callback<FinanceLoanCasesListModel> {

    static String selectedMonth;
    static String selectedYear;
    // TODO: Rename and change types of parameters
    protected RecyclerView recyclerView;
    protected LinearLayoutManager layoutManager;
    protected ProgressBar mProgressBar;
    protected RelativeLayout errorLayout;
    protected String mStatus;
    int pageNumber = 1;
    private Activity mActivity;
    private FinanceLoanCasesListAdapter financeLoanCasesListAdapter;
    private ArrayList<LoanApplication> loanCases;
    private View rootView;
    private FinanceCasesStatusActivity.OnLoanCaseContentAvailable onLoanCaseContentAvailable;


    public static ActiveLoanCasesFragment newInstance(FinanceCasesStatusActivity.OnLoanCaseContentAvailable listener, String mon, String year) {
        selectedMonth = mon;
        selectedYear = year;
        ActiveLoanCasesFragment fragment = new ActiveLoanCasesFragment();
        fragment.onLoanCaseContentAvailable = listener;
        Bundle args = new Bundle();
        // args.putString(ARG_PARAM1, param1);
        //fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_approve, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        mProgressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        errorLayout = (RelativeLayout) rootView.findViewById(R.id.errorLayout);

        mProgressBar.setVisibility(View.VISIBLE);
        errorLayout.setVisibility(View.GONE);

        FinanceUtils.makeLoanListRequest(FinanceUtils.LoanList.ACTIVE, 1, this, selectedMonth, selectedYear);
    }


    @Override
    public void success(final FinanceLoanCasesListModel loanCaseModel, Response response) {

        if ("T".equalsIgnoreCase(loanCaseModel.getStatus())) {
            if (loanCaseModel.getTotalRecords() > 0) {

                GCLog.e(Constants.TAG, loanCaseModel.toString());
                mProgressBar.setVisibility(View.GONE);
                if (pageNumber == 1) {
                    ApplicationController.getEventBus().post(new SetTabTextEvent("Active("+Integer.toString(loanCaseModel.getTotalRecords())+")", Constants.ACTIVE_LOAN_STATUS));
                    loanCases = loanCaseModel.getLoanCasesList();
                    financeLoanCasesListAdapter = new FinanceLoanCasesListAdapter(getActivity(), loanCases);
                    recyclerView.setAdapter(financeLoanCasesListAdapter);
                    recyclerView.setOnScrollListener(new RecyclerViewEndlessScrollListener(layoutManager, loanCaseModel.getTotalRecords()) {

                        @Override
                        public void onLoadMore(int nextPageNo) {
                            try {
                                pageNumber = nextPageNo;
                                if (loanCaseModel.isHasNext()) {
                                    FinanceUtils.makeLoanListRequest(FinanceUtils.LoanList.ACTIVE, nextPageNo, ActiveLoanCasesFragment.this, selectedMonth, selectedYear);
                                }
                            } catch (Exception e) {
                                GCLog.e("exception: " + e.getMessage());
                            }
                        }
                    });
                    financeLoanCasesListAdapter.notifyDataSetChanged();
                } else {
                    loanCases.addAll(loanCaseModel.getLoanCasesList());
                    financeLoanCasesListAdapter.notifyDataSetChanged();
                }
            } else {
                mProgressBar.setVisibility(View.GONE);
                errorLayout.setVisibility(View.VISIBLE);
                ((TextView) errorLayout.findViewById(R.id.errorMessage)).setText("No records found");
                errorLayout.findViewById(R.id.retry).setVisibility(View.GONE);
            }

            if (onLoanCaseContentAvailable != null) {
                onLoanCaseContentAvailable.onContentAvailable(loanCaseModel, Constants.ACTIVE_LOAN_STATUS);
            }
        }
    }

    @Override
    public void failure(RetrofitError error) {
        CommonUtils.showErrorToast(mActivity, error, Toast.LENGTH_SHORT);
    }
}





