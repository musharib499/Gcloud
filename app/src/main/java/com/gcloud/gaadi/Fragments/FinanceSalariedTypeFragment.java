package com.gcloud.gaadi.Fragments;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FilterQueryProvider;
import android.widget.RadioButton;
import android.widget.Spinner;

import com.gcloud.gaadi.ApplicationController;
import com.gcloud.gaadi.R;
import com.gcloud.gaadi.adapter.CityAdapter;
import com.gcloud.gaadi.adapter.CustomSpinnerAdapter;
import com.gcloud.gaadi.db.FinanceDB;
import com.gcloud.gaadi.db.FinanceDBHelper;
import com.gcloud.gaadi.db.MakeModelVersionDB;
import com.gcloud.gaadi.model.CityData;
import com.gcloud.gaadi.model.Finance.SalariedTypeDetail;
import com.gcloud.gaadi.utils.CommonUtils;
import com.gcloud.gaadi.utils.GCLog;
import com.github.jjobes.slidedatetimepicker.SlideDateTimeListener;
import com.github.jjobes.slidedatetimepicker.SlideDateTimePicker;
import com.rengwuxian.materialedittext.MaterialAutoCompleteTextView;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.rengwuxian.materialedittext.MaterialTextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Lakshay on 08-09-2015.
 */
public class FinanceSalariedTypeFragment extends Fragment implements View.OnClickListener, SlideDateTimeListener, AdapterView.OnItemClickListener {

    Activity mActivity;
    Spinner salaryCreditType;
    CityAdapter cityAdapter;
    String salaryTypeStr = "";
    private MaterialTextView etJoinDate;
    private MaterialEditText etWorkExp, etGrossIncome, etExistingEMI;
    private SalariedTypeListener mSalariedTypeListener;
    private MaterialAutoCompleteTextView mactvBankName, actvCompany;
    private SalariedTypeDetail mSalariedTypeDetail;
    private String bankId ="";
    private MaterialAutoCompleteTextView workCity;
    private FilterQueryProvider cityFilterQueryProvider = new FilterQueryProvider() {
        @Override
        public Cursor runQuery(CharSequence constraint) {
            return getCityCursor(constraint);
        }
    };

    public static FinanceSalariedTypeFragment getInstance(SalariedTypeListener listener) {
        FinanceSalariedTypeFragment fragment = new FinanceSalariedTypeFragment();
        fragment.mSalariedTypeListener = listener;
        fragment.mSalariedTypeDetail = new SalariedTypeDetail();
        return fragment;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getAdapter() instanceof CityAdapter) {
            Cursor cursor = (Cursor) parent.getAdapter().getItem(position);
            CityData selectedCity = new CityData();
            selectedCity.setCityId(cursor.getString(cursor.getColumnIndex(MakeModelVersionDB.CITY_ID)));
            selectedCity.setCityName(cursor.getString(cursor.getColumnIndex(MakeModelVersionDB.CITY_NAME)));
            selectedCity.setStateId(cursor.getString(cursor.getColumnIndex(MakeModelVersionDB.STATE_ID)));
            selectedCity.setRegionId(cursor.getString(cursor.getColumnIndex(MakeModelVersionDB.REGION_ID)));

            workCity.setText(selectedCity.getCityName());
            workCity.setSelection(workCity.getText().length());
            GCLog.e("selected City: " + selectedCity.toString());
        }
    }

    private Cursor getCityCursor(CharSequence constraint) {
        return ApplicationController.getMakeModelVersionDB().getCityRecords(constraint);
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
        View view = LayoutInflater.from(getFragmentActivity()).inflate(R.layout.finance_salried_type, null);
        initializeWidgets(view);
        return view;
    }

    private void initializeWidgets(View view)
    {
        actvCompany = (MaterialAutoCompleteTextView) view.findViewById(R.id.company);
        etJoinDate = (MaterialTextView)view.findViewById(R.id.joiningdate);
        etJoinDate.setOnClickListener(this);
        // etJoinDate.setMaxDate(new Date());
        // etJoinDate.setmActivity(getActivity());
        etWorkExp = (MaterialEditText)view.findViewById(R.id.totalWorkExp);
        etGrossIncome = (MaterialEditText)view.findViewById(R.id.monthlyIncome);
        etExistingEMI = (MaterialEditText)view.findViewById(R.id.existingEMI);
        mactvBankName = (MaterialAutoCompleteTextView)view.findViewById(R.id.mactv_bankName);
        salaryCreditType = (Spinner) view.findViewById(R.id.salaryCreditType);
        ArrayList<String> msalaryType = CommonUtils.getSalaryCreditType();
        ArrayAdapter msalaryAdapter = new ArrayAdapter(mActivity, android.R.layout.simple_spinner_dropdown_item, msalaryType);
        msalaryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        salaryCreditType.setAdapter(msalaryAdapter);
        salaryCreditType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                salaryCreditType.setSelection(position);
                if (position != 0)
                    salaryTypeStr = salaryCreditType.getSelectedItem().toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        workCity = (MaterialAutoCompleteTextView) view.findViewById(R.id.workCity);
        cityAdapter = new CityAdapter(mActivity, null);
        workCity.setAdapter(cityAdapter);
        workCity.setThreshold(1);
        workCity.setOnItemClickListener(this);
        cityAdapter.setFilterQueryProvider(cityFilterQueryProvider);
        etGrossIncome.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                CommonUtils.insertCommaIntoNumber(etGrossIncome, s, "##,##,###");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etExistingEMI.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                CommonUtils.insertCommaIntoNumber(etExistingEMI, s, "##,##,###");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        view.findViewById(R.id.bNext).setOnClickListener(this);

        final CustomSpinnerAdapter adapter = new CustomSpinnerAdapter(FinanceDB.COMPANY_NAME,actvCompany,getFragmentActivity(),R.layout.spinner_dropdown_item,
                FinanceDBHelper.getCompanies(actvCompany.getText().toString()), new String[]{FinanceDB.COMPANY_NAME,FinanceDB.AUTOINCREMENT_ID},
                new int[]{R.id.tvSpinnerItem,R.id.tvSpinnerItemID},0);

        actvCompany.setThreshold(1);
        actvCompany.setAdapter(adapter);

        CustomSpinnerAdapter adapter1 = new CustomSpinnerAdapter(FinanceDB.BANK_NAME, mactvBankName,getFragmentActivity(),R.layout.spinner_dropdown_item,
                FinanceDBHelper.getBanks(mactvBankName.getText().toString()),new String[]{FinanceDB.BANK_NAME,FinanceDB.AUTOINCREMENT_ID},new int[]{R.id.tvSpinnerItem,R.id.tvSpinnerItemID},0);
        mactvBankName.setThreshold(1);
        mactvBankName.setAdapter(adapter1);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bNext:
                if(validate())
                    mSalariedTypeListener.onSalariedTypeDetailsCompleted(mSalariedTypeDetail);

                break;
            case R.id.joiningdate:
                onDailogs();
                break;
        }
    }

    public void onDailogs()
    {
        new SlideDateTimePicker.Builder(getFragmentManager())
                .setShowTimeTab(false)
                .setListener(this)

                .setMaxDate(new Date())

                .build().show();
    }

    @Override
    public void onDateTimeSet(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String stringDate = sdf.format(date);
        etJoinDate.setText(stringDate);
    }

    @Override
    public void onDateTimeCancel() {

    }

    private boolean validate() {
        if (!actvCompany.getText().toString().trim().equals("")) {
            mSalariedTypeDetail.setCompany(actvCompany.getText().toString().trim());
        } else {
            //actvCompany.setError("Enter company name");
            //return false;
        }
        if (!etJoinDate.getText().toString().trim().equals("")) {
            mSalariedTypeDetail.setJoinDate(etJoinDate.getText().toString().trim());
        } else {
            //etJoinDate.setError("Enter joining date");
            //return false;
        }
        if (!etWorkExp.getText().toString().trim().equals("")) {
            mSalariedTypeDetail.setWorkExp(etWorkExp.getText().toString().trim());
        } else {
            //etWorkExp.setError("Enter work experience");
            //return false;
        }
        if (!etGrossIncome.getText().toString().trim().equals("")) {
            mSalariedTypeDetail.setGrossIncome(etGrossIncome.getText().toString().trim().replace(",", ""));
        } else {
            //etGrossIncome.setError("Enter gross income");
            //return false;
        }
        if (!etExistingEMI.getText().toString().trim().equals("")) {
            mSalariedTypeDetail.setExistingEMI(etExistingEMI.getText().toString().trim().replace(",", ""));
        } else {
            //etExistingEMI.setError("Enter existing EMI");
            //return false;
        }
        if (!mactvBankName.getText().toString().trim().equals("")) {
            bankId = FinanceDBHelper.getBankId(mactvBankName.getText().toString());
            mSalariedTypeDetail.setBankName(bankId);
        } else {
            //mactvBankName.setError("Enter salaried bank name");
            //return false;
        }
        if (!workCity.getText().toString().trim().equals("")) {
            mSalariedTypeDetail.setWorkCity(workCity.getText().toString().trim());
        }
        if (!salaryTypeStr.trim().equals("")) {
            mSalariedTypeDetail.setSalaryType(salaryTypeStr.trim());
        } /*else {
            mSalariedTypeDetail.setSalaryType("");
        }*/
        String isNoticedOrEmployed = null;
        RadioButton rbServingNoticePeriodYes = (RadioButton) getView().findViewById(R.id.rbServingNoticePeriodYes);
        RadioButton rbServingNoticePeriodNo = (RadioButton) getView().findViewById(R.id.rbServingNoticePeriodNo);
        if (rbServingNoticePeriodYes.isChecked()) {

            isNoticedOrEmployed = "noticed";
        } else if (rbServingNoticePeriodNo.isChecked()) {
            isNoticedOrEmployed = "employed";
        }
        mSalariedTypeDetail.setCurrentJobStatus(isNoticedOrEmployed);

        return true;
    }
/*
    @Override
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
        if(event .getmSalaryType().equals(Constants.FINANCE_EMP_TYPE_SALARIED)) {
            if (validate())
                mSalariedTypeListener.onSalariedTypeDetailsCompleted(mSalariedTypeDetail);
        }

    }*/

    public interface SalariedTypeListener
    {
        void onSalariedTypeDetailsCompleted(SalariedTypeDetail salariedTypeDetail);
    }
}
