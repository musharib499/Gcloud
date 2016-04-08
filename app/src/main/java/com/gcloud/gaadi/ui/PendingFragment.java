package com.gcloud.gaadi.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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
 * @author Lakshay
 */
public class PendingFragment extends Fragment implements Callback<FinanceLoanCasesListModel> {

    static String selectedMonth;
    static String selectedYear;
    protected RecyclerView recyclerView;
    protected LinearLayoutManager layoutManager;
    protected ProgressBar mProgressBar;
    protected RelativeLayout errorLayout;
    protected String mStatus;
    private Activity mActivity;
    private ArrayList<LoanApplication> loanCases;
    private FinanceLoanCasesListAdapter loanCasesAdapter;
    private int pageNumber = 1;
    private View rootView;
    private FinanceCasesStatusActivity.OnLoanCaseContentAvailable onLoanCaseContentAvailable;

    public static PendingFragment newInstance(FinanceCasesStatusActivity.OnLoanCaseContentAvailable listener, String month, String year) {
        selectedMonth = month;
        selectedYear = year;
        PendingFragment fragment = new PendingFragment();
        fragment.onLoanCaseContentAvailable = listener;
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getFragmentActivity();
    }
    
    private Activity getFragmentActivity() {
        return mActivity;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        layoutManager = new LinearLayoutManager(getFragmentActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        mProgressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.VISIBLE);
        errorLayout = (RelativeLayout) rootView.findViewById(R.id.errorLayout);
        try {
            FinanceUtils.makeLoanListRequest(FinanceUtils.LoanList.COMPLETED, 1, this, selectedMonth, selectedYear);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_pending, container, false);
        return rootView;
    }

    @Override
    public void success(final FinanceLoanCasesListModel loanCaseModel, Response response) {
        if ("T".equalsIgnoreCase(loanCaseModel.getStatus())) {
            if (loanCaseModel.getTotalRecords() > 0) {
                mProgressBar.setVisibility(View.GONE);
                if (pageNumber == 1) {
                    ApplicationController.getEventBus().post(new SetTabTextEvent("Completed("+Integer.toString(loanCaseModel.getTotalRecords())+")", Constants.COMPLETED_LOAN_STATUS));
                    loanCases = loanCaseModel.getLoanCasesList();
                    loanCasesAdapter = new FinanceLoanCasesListAdapter(getFragmentActivity(), loanCases);
                    recyclerView.setAdapter(loanCasesAdapter);
                    recyclerView.setOnScrollListener(new RecyclerViewEndlessScrollListener(
                            layoutManager,
                            loanCaseModel.getTotalRecords()
                    ) {
                        @Override
                        public void onLoadMore(int nextPageNo) {
                            try {
                                pageNumber = nextPageNo;
                                if (loanCaseModel.isHasNext()) {
                                    FinanceUtils.makeLoanListRequest(FinanceUtils.LoanList.COMPLETED, pageNumber, PendingFragment.this, selectedMonth, selectedYear);
                                }
                            } catch (Exception e) {
                                GCLog.e("exception: " + e.getMessage());
                            }
                        }
                    });
                } else {
                    loanCases.addAll(loanCaseModel.getLoanCasesList());
                    loanCasesAdapter.notifyDataSetChanged();
                }
                GCLog.e("pageNumber " + pageNumber);
            } else {
                mProgressBar.setVisibility(View.GONE);
            }


            if (onLoanCaseContentAvailable != null) {
                onLoanCaseContentAvailable.onContentAvailable(loanCaseModel, Constants.COMPLETED_LOAN_STATUS);
            }
        }
    }

    @Override
    public void failure(RetrofitError error) {
        CommonUtils.showErrorToast(getFragmentActivity(), error, Toast.LENGTH_SHORT);
    }
}
