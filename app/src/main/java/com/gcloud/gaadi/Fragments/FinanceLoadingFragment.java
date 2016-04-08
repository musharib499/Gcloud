package com.gcloud.gaadi.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.gcloud.gaadi.R;
import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.model.CarItemModel;
import com.gcloud.gaadi.model.Finance.LoanOffersResponse;
import com.gcloud.gaadi.retrofit.RetrofitRequest;
import com.gcloud.gaadi.ui.Finance.FinanceFormsActivity;
import com.gcloud.gaadi.ui.Finance.FinanceOffersActivity;
import com.gcloud.gaadi.utils.CommonUtils;
import com.gcloud.gaadi.utils.GCLog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Lakshay on 08-09-2015.
 */
public class FinanceLoadingFragment extends Fragment {

    private Activity mActivity;
    private ProgressBar pbContent;
    private FinanceLoadingListener mFinanceLoadingListener;
    private TextView tvMessage;
    private CarItemModel carItemModel;
    private static FinanceLoadingFragment fragment;

    public interface FinanceLoadingListener {
        public void onOthersOffers();
    }

    public static FinanceLoadingFragment getInstance(FinanceLoadingListener listener,CarItemModel model) {
        if(FinanceLoadingFragment.fragment == null)
             fragment = new FinanceLoadingFragment();
        fragment.mFinanceLoadingListener = listener;
        fragment.carItemModel = model;
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        //mActivity = getFragmentActivity();
    }
    
    private Activity getFragmentActivity() {
        return mActivity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = LayoutInflater.from(getFragmentActivity()).inflate(R.layout.finance_loading_fragment, null);
        pbContent = (ProgressBar) view.findViewById(R.id.pbContent);
        tvMessage = (TextView) view.findViewById(R.id.tvMessage);
        GCLog.e(Constants.TAG, "Loading View Inflated");
        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(FinanceFormsActivity.mRequestData);
        System.out.println("Request Data : "+json);

        RetrofitRequest.postFinanceFormData(FinanceFormsActivity.mRequestData, new retrofit.Callback<LoanOffersResponse>() {

            @Override
            public void success(LoanOffersResponse loanOffersResponse, Response response) {
                pbContent.setVisibility(View.GONE);
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                String json = gson.toJson(loanOffersResponse);
                System.out.println("Response Data : "+json);
                if("T".equalsIgnoreCase(loanOffersResponse.getStatus())){
                    GCLog.e(Constants.TAG, loanOffersResponse.toString());
                    CommonUtils.setStringSharedPreference(getFragmentActivity(), Constants.FINANCE_APP_ID, loanOffersResponse.getFinance_lead_id());
                    CommonUtils.setStringSharedPreference(getFragmentActivity(),Constants.FINANCE_CUSTOMER_ID,loanOffersResponse.getCustomer_id());
                    Intent intent = new Intent(mActivity, FinanceOffersActivity.class);
                    intent.putExtra(Constants.BANK_OFFER,loanOffersResponse.getBank_offer());
                    intent.putExtra(Constants.CAR_MODEL,carItemModel);
                    mActivity.startActivity(intent);
                }else if("O".equalsIgnoreCase(loanOffersResponse.getStatus())){
                    mFinanceLoadingListener.onOthersOffers();
                }else {
                    tvMessage.setText(loanOffersResponse.getMessage());
//                    Toast.makeText(getFragmentActivity(),loanOffersResponse.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                GCLog.e(Constants.TAG, "Retrofit Error");
                pbContent.setVisibility(View.GONE);
                tvMessage.setText(CommonUtils.getErrorMessage(getFragmentActivity(),error));
            }
        });
    }
}
