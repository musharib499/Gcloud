package com.gcloud.gaadi.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.gcloud.gaadi.ApplicationController;
import com.gcloud.gaadi.R;
import com.gcloud.gaadi.adapter.FinanceSelectedOffersAdapter;
import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.model.CarItemModel;
import com.gcloud.gaadi.model.DocumentCategories;
import com.gcloud.gaadi.model.Finance.FInanceOfferSelectedRequestModel;
import com.gcloud.gaadi.model.Finance.FinanceOfferSelectedModel;
import com.gcloud.gaadi.model.Finance.UpdateFinanceLeadModel;
import com.gcloud.gaadi.retrofit.RetrofitRequest;
import com.gcloud.gaadi.ui.Finance.AvailableCarsActivity;
import com.gcloud.gaadi.ui.Finance.FinanceCollectImagesActivity;
import com.gcloud.gaadi.ui.Finance.FinanceFormsActivity;
import com.gcloud.gaadi.ui.Finance.FinanceOffersActivity;
import com.gcloud.gaadi.ui.GCProgressDialog;
import com.gcloud.gaadi.utils.CommonUtils;
import com.gcloud.gaadi.utils.GAHelper;
import com.gcloud.gaadi.utils.GCLog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Lakshay on 09-09-2015.
 */
public class FinanceSelectedOfferFragment extends Fragment implements View.OnClickListener {

    ImageView  ivMakeLogo;
    TextView tvApplicationNo;
    RecyclerView mRecyclerView;
    FinanceSelectedOffersAdapter mAdapter;
    ArrayList<DocumentCategories> documentCategoriesArrayList = new ArrayList<>();
    GCProgressDialog gcProgressDialog;
    private Activity mActivity;
    private ArrayList<FInanceOfferSelectedRequestModel> mFInanceOfferSelectedRequestModel = new ArrayList<>();
    private CarItemModel carItemModel;
    private ProgressBar progressBar;

    public static FinanceSelectedOfferFragment newInstance(Activity mActivity,
                                                           ArrayList<FInanceOfferSelectedRequestModel> requestModel,
                                                           CarItemModel model) {
        FinanceSelectedOfferFragment fragment = new FinanceSelectedOfferFragment();
        fragment.mFInanceOfferSelectedRequestModel = requestModel;
        fragment.carItemModel = model;
        fragment.mActivity = mActivity;
       // this.bankLogoUrl = requestModel.getBank_URL();
        return fragment;
    }


    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        //mActivity = getActivity();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
    }

    private Activity getFragmentActivity() {
        return mActivity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = LayoutInflater.from(getFragmentActivity()).inflate(R.layout.finance_selected_offer_fragment, null);

        progressBar = (ProgressBar)view.findViewById(R.id.progressBar);
        if(progressBar != null && progressBar.isShown())
            progressBar.setVisibility(View.GONE);

        if(savedInstanceState != null)
        {
            mFInanceOfferSelectedRequestModel = (ArrayList<FInanceOfferSelectedRequestModel>) savedInstanceState.getSerializable("financeOfferModel");
            carItemModel = (CarItemModel) savedInstanceState.getSerializable("carItemModel");
        }
        for(FInanceOfferSelectedRequestModel obj:mFInanceOfferSelectedRequestModel) {
            obj.setFinance_lead_id(CommonUtils.getStringSharedPreference(getFragmentActivity(), Constants.FINANCE_APP_ID, ""));
        }
        Button mUploadDocument = (Button) view.findViewById(R.id.bUploadDocument);
        tvApplicationNo=(TextView) view.findViewById(R.id.tvApplicationNo);
        tvApplicationNo.setText(CommonUtils.getStringSharedPreference(mActivity, Constants.FINANCE_APP_ID, ""));
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new FinanceSelectedOffersAdapter(mActivity,mFInanceOfferSelectedRequestModel,carItemModel);
        mRecyclerView.setAdapter(mAdapter);


        //mFInanceOfferSelectedRequestModel.setBank_id("1");

        ((TextView)view.findViewById(R.id.tvMakeModel)).setText(carItemModel.getMake() + " " + carItemModel.getModel());
        ((TextView)view.findViewById(R.id.tvRegNo)).setText("Reg No. " + carItemModel.getRegno());
        ivMakeLogo = (ImageView) view.findViewById(R.id.makeLogo);
        ivMakeLogo.setImageResource(ApplicationController.makeLogoMap.get(carItemModel.getMake_id()));
       /* switch (Integer.parseInt(mFInanceOfferSelectedRequestModel.getBank_id()))
        {
            case 1:
                ivBankLogo.setImageResource(R.drawable.hdfc);
                break;
            case 2:
                ivBankLogo.setImageResource(R.drawable.axis_finance);
                break;
            case 3:
                ivBankLogo.setImageResource(R.drawable.mahindra_finance);
                break;
        }*/

            /*mHolder.stockImage.setDefaultImageResId(R.drawable.no_image_default_small);*/


        mUploadDocument.setOnClickListener(this);
        GCLog.e(Constants.TAG, "Selected Offers fragment on create view");

        postAppliedLoanData();

        Calendar calendar = Calendar.getInstance();
        long timePhase1 = calendar.getTimeInMillis() -
        ((AvailableCarsActivity.totalTimeOnAvailableCarsActivity == 0) ? AvailableCarsActivity.startTime : AvailableCarsActivity.totalTimeOnAvailableCarsActivity)
        + ((FinanceFormsActivity.totalTimeOnFinanceFormsActivity == 0) ? FinanceFormsActivity.startTime : FinanceFormsActivity.totalTimeOnFinanceFormsActivity)
        + ((FinanceOffersActivity.totalTimeOnFinanceOffersActivity == 0) ? FinanceOffersActivity.startTime : FinanceOffersActivity.totalTimeOnFinanceOffersActivity);

        ApplicationController.getInstance().getGAHelper().sendScreenName(GAHelper.TrackerName.APP_TRACKER, Constants.CATEGORY_FINANCE_SELECTED_LOAN);
        ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER, Constants.CATEGORY_FINANCE_SELECTED_LOAN, Constants.CATEGORY_FINANCE_SELECTED_LOAN,
                Constants.ACTION_FINANCE_PHASE1, Constants.TIME_PHASE_1, timePhase1/1000);
        ApplicationController.getInstance().getGAHelper().sendUserTimings(GAHelper.TrackerName.APP_TRACKER, Constants.EVENT_FINANCE_DETAILS, timePhase1, Constants.CATEGORY_FINANCE_INSPECTED_CARS, Constants.CATEGORY_FINANCE_INSPECTED_CARS);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bUploadDocument:
                progressBar.setVisibility(View.VISIBLE);
                Intent intent = new Intent(mActivity, FinanceCollectImagesActivity.class);
                intent.putExtra(Constants.CAR_MODEL, carItemModel);
                intent.putExtra(Constants.DOCUMENT_CATEGORIES, documentCategoriesArrayList);
                mActivity.startActivity(intent);
                break;
        }
    }

    private void postAppliedLoanData()
    {
        String path = "updateFinanceLead";
        Gson gson = new Gson();
        gcProgressDialog = new GCProgressDialog(mActivity, mActivity, getString(R.string.please_wait));
        gcProgressDialog.show();
        gcProgressDialog.setCancelable(false);
        ArrayList<FinanceOfferSelectedModel> list=new ArrayList<>();
        for(FInanceOfferSelectedRequestModel model:mFInanceOfferSelectedRequestModel) {
            FinanceOfferSelectedModel financeObj = new FinanceOfferSelectedModel();
            financeObj.setApplied_roi(model.getApplied_roi().split("%")[0]);
            financeObj.setApplied_tenure(model.getApplied_tenure());
            financeObj.setBank_id(model.getBank_id());
            financeObj.setApplied_loan_amount(model.getApplied_loan_amount().trim().equals("") ? "" :
                    (model.getApplied_loan_amount().trim().replace(",", "")).replace(Constants.RUPEES_SYMBOL, "").trim());
            financeObj.setApplied_emi(model.getApplied_emi().trim().equals("") ? "" : (model.getApplied_emi().trim().replace(",", "")).replace(Constants.RUPEES_SYMBOL, "").trim());
            financeObj.setFinance_lead_id(model.getFinance_lead_id());
            list.add(financeObj);
        }
        Type typeOfSrc = new TypeToken<ArrayList<FinanceOfferSelectedModel>>(){}.getType();
        String string= gson.toJson(list,typeOfSrc);
        RetrofitRequest.postAppliedLoanData(path, string, new Callback<UpdateFinanceLeadModel>() {
            @Override
            public void success(UpdateFinanceLeadModel updateFinanceLeadModelResponse, Response response) {
                if (gcProgressDialog != null && gcProgressDialog.isShowing()) {
                    gcProgressDialog.dismiss();
                }
                documentCategoriesArrayList.addAll(updateFinanceLeadModelResponse.getDocumentCategoriesArrayList());
            }

            @Override
            public void failure(RetrofitError error) {
                //error.printStackTrace();
                if (gcProgressDialog != null && gcProgressDialog.isShowing()) {
                    gcProgressDialog.dismiss();
                }

            }
        });

    }

    @Override
    public void onResume() {
        if(progressBar != null && progressBar.getVisibility() == View.VISIBLE)
        {
            progressBar.setVisibility(View.GONE);
        }
        super.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("financeOfferModel", mFInanceOfferSelectedRequestModel);
        outState.putSerializable("carItemModel", carItemModel);
        super.onSaveInstanceState(outState);
    }


}
