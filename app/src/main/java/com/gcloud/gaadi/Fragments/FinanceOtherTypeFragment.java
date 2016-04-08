package com.gcloud.gaadi.Fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.gcloud.gaadi.R;
import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.db.FinanceDBHelper;
import com.gcloud.gaadi.model.Finance.OtherTypeDetails;

import java.util.ArrayList;

/**
 * Created by Manish on 9/21/2015.
 */
public class FinanceOtherTypeFragment extends Fragment implements View.OnClickListener {

    private Activity mActivity;
    private RadioGroup rbgResidenceProof, rbgTakenLoan, rbgRepaying;
    private RadioButton rbResidenceProofYes, rbResidenceProofNo, rbTakenLoanYes, rbTakenLoanNo, rbRepaying12To24, rbRepayingLessThan12, rbRepayingMoreThan24;
    private Spinner mSpinnerEmploymentStatus;
    private String employmentStatus = null;
    private ArrayList<String> mEmploymentStatusType;
    private ArrayAdapter mEmploymentStatusAdapter;
    private OtherTypeListener mOtherTypeListener;
    private OtherTypeDetails mOtherTypeDetails;
    TextView loanTenureRepaidTxt;


    public interface OtherTypeListener {
        public void onOtherTypeDetailsCompleted(OtherTypeDetails otherTypeDetails);
    }

    public static FinanceOtherTypeFragment getInstance(OtherTypeListener otherTypeListener) {
        FinanceOtherTypeFragment fragment = new FinanceOtherTypeFragment();
        fragment.mOtherTypeDetails = new OtherTypeDetails();
        fragment.mOtherTypeListener = otherTypeListener;
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
        View view = LayoutInflater.from(getFragmentActivity()).inflate(R.layout.finance_other_layout, null);
        initializeWidgets(view);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bNext:
                validate();
                mOtherTypeListener.onOtherTypeDetailsCompleted(mOtherTypeDetails);
                break;
        }
    }

   /* @Override
    public void onResume() {
        super.onResume();
        ApplicationController.getEventBus().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        ApplicationController.getEventBus().unregister(this);
    }*/
   /* @Subscribe
    public void salaryTypeEvent(FinanceSalaryTypeEvents event) {
        if(event .getmSalaryType().equals(Constants.FINANCE_EMP_TYPE_OTHERS)) {
            validate();
            mOtherTypeListener.onOtherTypeDetailsCompleted(mOtherTypeDetails);
        }

    }*/
    public void validate() {
        mOtherTypeDetails.setEmploymentStatus(employmentStatus);

        String selectedResidenceProof = null;
        rbResidenceProofYes = (RadioButton) getView().findViewById(R.id.rbResidenceProofYes);
        rbResidenceProofNo = (RadioButton) getView().findViewById(R.id.rbResidenceProofNo);
        if(rbResidenceProofYes.isChecked()) {

            selectedResidenceProof = "Yes";
        }
        else if(rbResidenceProofNo.isChecked()) {
            selectedResidenceProof = "No";
        }
        mOtherTypeDetails.setResidenceProof(selectedResidenceProof);

        String selectedTakenLoan = null;

        if(rbTakenLoanYes.isChecked()) {

            selectedTakenLoan = "Yes";
        }
        else if(rbTakenLoanNo.isChecked()) {
            selectedTakenLoan = "No";

        }
        mOtherTypeDetails.setTakenLoan(selectedTakenLoan);

        String selectedRepaying = null;
        rbRepaying12To24 = (RadioButton)getView().findViewById(R.id.rbMonthRepaying12To24);
        rbRepayingLessThan12 = (RadioButton)getView().findViewById(R.id.rbMonthRepayingLessThan12);
        rbRepayingMoreThan24 = (RadioButton)getView().findViewById(R.id.rbMonthRepayingMoreThan24);
        if(rbRepayingLessThan12.isChecked())
            selectedRepaying = "12";
        else if(rbRepaying12To24.isChecked())
            selectedRepaying = "24";
        else if(rbRepayingMoreThan24.isChecked())
            selectedRepaying = "24+";
        mOtherTypeDetails.setRepaying(selectedRepaying);

    }

    private void initializeWidgets(View view) {
        rbgResidenceProof = (RadioGroup) view.findViewById(R.id.rbgResidenceProof);
        rbgRepaying = (RadioGroup) view.findViewById(R.id.rbgMonthRepaying);
        loanTenureRepaidTxt = (TextView) view.findViewById(R.id.loanTenureRepaidTxt);
        rbTakenLoanYes = (RadioButton)view.findViewById(R.id.rbTakenLoanYes);
        rbTakenLoanNo = (RadioButton)view.findViewById(R.id.rbTakenLoanNo);
        rbgTakenLoan = (RadioGroup) view.findViewById(R.id.rbgTakenLoan);
        ((Button) view.findViewById(R.id.bNext)).setOnClickListener(this);
        mEmploymentStatusType = FinanceDBHelper.getProfession(Constants.FINANCE_EMP_TYPE_OTHERS_SERVER);
        mSpinnerEmploymentStatus = (Spinner) view.findViewById(R.id.employment_Type);
        mEmploymentStatusAdapter = new ArrayAdapter(getFragmentActivity(), android.R.layout.simple_spinner_dropdown_item, mEmploymentStatusType);
        mEmploymentStatusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerEmploymentStatus.setAdapter(mEmploymentStatusAdapter);
        mSpinnerEmploymentStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mSpinnerEmploymentStatus.setSelection(position);
                if (position != 0)
                    employmentStatus = mSpinnerEmploymentStatus.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        rbgTakenLoan.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId)
                {
                    case R.id.rbTakenLoanYes:
                        loanTenureRepaidTxt.setVisibility(View.VISIBLE);
                        rbgRepaying.setVisibility(View.VISIBLE);

                        break;
                    case R.id.rbTakenLoanNo:
                        loanTenureRepaidTxt.setVisibility(View.GONE);
                        rbgRepaying.setVisibility(View.GONE);
                        rbgRepaying.clearCheck();
                        break;
                }
            }
        });
    }


}
