package com.gcloud.gaadi.Fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Spinner;

import com.gcloud.gaadi.R;
import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.model.Finance.BusinessTypeDetails;
import com.gcloud.gaadi.model.Finance.OtherTypeDetails;
import com.gcloud.gaadi.model.Finance.ProfessionTypeDetails;
import com.gcloud.gaadi.model.Finance.SalariedTypeDetail;
import com.gcloud.gaadi.model.FinanceIncomeDetailsObj;
import com.gcloud.gaadi.utils.CommonUtils;

import java.util.ArrayList;

/**
 * Created by Lakshay on 08-09-2015.
 */
public class FinanceIncomeInformationFragment extends Fragment implements
        FinanceSalariedTypeFragment.SalariedTypeListener,
        FinanceBusinessTypeFragment.BusinessTypeListener,
        FinanceProfessionTypeFragment.ProfessionTypeListener,
        FinanceOtherTypeFragment.OtherTypeListener {

    public static String selectedEmployment;
    public FinanceIncomeDetailsObj mIncomeDetailsObj;
    private Activity mActivity;
    private FrameLayout mContentFrame;
    private Spinner mSpinnerEmployment;
    private ArrayList<String> mEmploymentType;
    private ArrayAdapter mEmploymentAdapter;
    private FinanceIncomeActionsListener mListener;
    private Button next;

    public static FinanceIncomeInformationFragment getInstance(FinanceIncomeActionsListener listener) {
        FinanceIncomeInformationFragment fragment = new FinanceIncomeInformationFragment();
        fragment.mListener = listener;
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
    }

    private Activity getFragmentActivity() {
        return mActivity;
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        //mActivity = getFragmentActivity();
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = LayoutInflater.from(getFragmentActivity()).inflate(R.layout.finance_income_details_layout, null);
        getFragmentActivity().setTitle(Constants.FINANCE_FORMS);
        mContentFrame = (FrameLayout) view.findViewById(R.id.framelayout);
        mSpinnerEmployment = (Spinner) view.findViewById(R.id.employment_Type);
        next = (Button) view.findViewById(R.id.bNext);
      /*  next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ApplicationController.getEventBus().post(new FinanceSalaryTypeEvents(String.valueOf(selectedEmployment)));
            }
        });*/
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mEmploymentType = CommonUtils.getFinanceEmploymentType();
        mEmploymentAdapter = new ArrayAdapter(mActivity, android.R.layout.simple_spinner_dropdown_item, mEmploymentType);
        mEmploymentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerEmployment.setAdapter(mEmploymentAdapter);
        mSpinnerEmployment.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                OnEmploymentSelected(position);
                selectedEmployment = mSpinnerEmployment.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        mIncomeDetailsObj = new FinanceIncomeDetailsObj();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void OnEmploymentSelected(int position) {
        FragmentManager mFragmentManager = ((AppCompatActivity) mActivity).getSupportFragmentManager();

        switch (position) {
            case 0:
                mContentFrame.setVisibility(View.GONE);
                next.setVisibility(View.VISIBLE);
                next.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.grey));
                break;
            case 1:
                Fragment fragment = FinanceSalariedTypeFragment.getInstance(this);
                next.setVisibility(View.GONE);
                mContentFrame.setVisibility(View.VISIBLE);
                mFragmentManager.beginTransaction().replace(R.id.framelayout, fragment).commit();

                break;
            case 2:
                Fragment fragment2 = FinanceBusinessTypeFragment.getInstance(this);
                next.setVisibility(View.GONE);
                mContentFrame.setVisibility(View.VISIBLE);
                mFragmentManager.beginTransaction().replace(R.id.framelayout, fragment2).commit();

                break;
            case 3:
                Fragment fragment3 = FinanceProfessionTypeFragment.getInstance(this);
                next.setVisibility(View.GONE);
                mContentFrame.setVisibility(View.VISIBLE);
                mFragmentManager.beginTransaction().replace(R.id.framelayout, fragment3).commit();
                break;
            case 4:
                Fragment fragment4 = FinanceOtherTypeFragment.getInstance(this);
                next.setVisibility(View.GONE);
                mContentFrame.setVisibility(View.VISIBLE);
                mFragmentManager.beginTransaction().replace(R.id.framelayout, fragment4).commit();
                break;
        }
    }

    @Override
    public void onSalariedTypeDetailsCompleted(SalariedTypeDetail salariedTypeDetail) {
        //Snackbar.make(getFragmentActivity().findViewById(android.R.id.content), salariedTypeDetail.getCompany(), Snackbar.LENGTH_SHORT).show();
        if (selectedEmployment == null || selectedEmployment == "") {
            Snackbar.make(getFragmentActivity().findViewById(android.R.id.content), "Select Employment Type", Snackbar.LENGTH_SHORT).show();
            return;
        }
        mIncomeDetailsObj.setmSalariedTypeDetail(salariedTypeDetail);
        mIncomeDetailsObj.setEmploymentType(selectedEmployment);
        mListener.onIncomeFilled(mIncomeDetailsObj);
    }

    @Override
    public void onBusinessTypeDetailsCompleted(BusinessTypeDetails businessTypeDetails) {
        if(selectedEmployment == null || selectedEmployment == "") {
            Snackbar.make(getFragmentActivity().findViewById(android.R.id.content), "Select Employment Type", Snackbar.LENGTH_SHORT).show();
            return;
        }
        mIncomeDetailsObj.setmBusinessTypeDetails(businessTypeDetails);
        mIncomeDetailsObj.setEmploymentType("business");
        mListener.onIncomeFilled(mIncomeDetailsObj);
    }

    @Override
    public void onProfessionTypeDetailsCompleted(ProfessionTypeDetails professionTypeDetails) {
        if(selectedEmployment == null || selectedEmployment == "") {
            Snackbar.make(getFragmentActivity().findViewById(android.R.id.content), "Select Employment Type", Snackbar.LENGTH_SHORT).show();
            return;
        }
        mIncomeDetailsObj.setmProfessionTypeDetails(professionTypeDetails);
        mIncomeDetailsObj.setEmploymentType(Constants.FINANCE_EMP_TYPE_PROFESS_SERVER);
        mListener.onIncomeFilled(mIncomeDetailsObj);
    }

    @Override
    public void onOtherTypeDetailsCompleted(OtherTypeDetails otherTypeDetails) {
        mIncomeDetailsObj.setmOtherTypeDetails(otherTypeDetails);
        mIncomeDetailsObj.setEmploymentType(Constants.FINANCE_EMP_TYPE_OTHERS_SERVER);
        mListener.onIncomeFilled(mIncomeDetailsObj);
    }

    public interface FinanceIncomeActionsListener {
        void onIncomeFilled(FinanceIncomeDetailsObj obj);
    }
}
