package com.gcloud.gaadi.Fragments;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FilterQueryProvider;
import android.widget.Spinner;

import com.gcloud.gaadi.ApplicationController;
import com.gcloud.gaadi.R;
import com.gcloud.gaadi.adapter.CityAdapter;
import com.gcloud.gaadi.adapter.CustomSpinnerAdapter;
import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.db.FinanceDB;
import com.gcloud.gaadi.db.FinanceDBHelper;
import com.gcloud.gaadi.db.MakeModelVersionDB;
import com.gcloud.gaadi.model.CityData;
import com.gcloud.gaadi.model.Finance.BusinessTypeDetails;
import com.gcloud.gaadi.utils.CommonUtils;
import com.gcloud.gaadi.utils.GCLog;
import com.github.jjobes.slidedatetimepicker.SlideDateTimeListener;
import com.github.jjobes.slidedatetimepicker.SlideDateTimePicker;
import com.rengwuxian.materialedittext.MaterialAutoCompleteTextView;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.rengwuxian.materialedittext.MaterialTextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Lakshay on 08-09-2015.
 */
public class FinanceBusinessTypeFragment extends Fragment implements View.OnClickListener, SlideDateTimeListener, AdapterView.OnItemClickListener {

    Activity mActivity;
    CityAdapter cityAdapter;
    private MaterialTextView etStartDate;
    private MaterialEditText etLastYearPAT, etTotalExistingEMI;
    private Spinner mSpinnerApplicant, officeSetUpType;
    private Spinner mIndustryType;
    private String applicantType, industryTypeValue, officeTypeStr;
    private ArrayList<String> mApplicantType;
    private ArrayList<String> industriesList;
    private ArrayAdapter mApplicantAdapter, mIndustriesAdapter;
    private BusinessTypeListener mBusinessTypeListener;
    private MaterialAutoCompleteTextView mactvBankName;
    private BusinessTypeDetails mBusinessTypeDetails;
    private MaterialAutoCompleteTextView workCity;
    private FilterQueryProvider cityFilterQueryProvider = new FilterQueryProvider() {
        @Override
        public Cursor runQuery(CharSequence constraint) {
            return getCityCursor(constraint);
        }
    };

    public static FinanceBusinessTypeFragment getInstance(BusinessTypeListener listener) {
        FinanceBusinessTypeFragment fragment = new FinanceBusinessTypeFragment();
        fragment.mBusinessTypeDetails = new BusinessTypeDetails();
        fragment.mBusinessTypeListener = listener;
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
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        //mActivity = activity;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = LayoutInflater.from(getFragmentActivity()).inflate(R.layout.finance_business_layout, null);
        initializeWidgets(view);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bNext:
                if(validate())
                    mBusinessTypeListener.onBusinessTypeDetailsCompleted(mBusinessTypeDetails);
                break;
            case  R.id.start_date:
                onDailogs();
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

    private void initializeWidgets(View view) {
        etStartDate = (MaterialTextView) view.findViewById(R.id.start_date);
        etStartDate.setOnClickListener(this);
       // etStartDate.setMaxDate(new Date());
       // etStartDate.setmActivity(getFragmentActivity());
        etLastYearPAT = (MaterialEditText) view.findViewById(R.id.last_year_income);
        etTotalExistingEMI = (MaterialEditText) view.findViewById(R.id.emi);

        mactvBankName = (MaterialAutoCompleteTextView) view.findViewById(R.id.mactv_bankName);
        mApplicantType = FinanceDBHelper.getProfession(Constants.FINANCE_EMP_TYPE_BUSINESS_SERVER);
        industriesList = FinanceDBHelper.getIndustries();
        view.findViewById(R.id.bNext).setOnClickListener(this);
        mSpinnerApplicant = (Spinner) view.findViewById(R.id.applicant_Type);
        mIndustryType = (Spinner) view.findViewById(R.id.industryType);
        mApplicantAdapter = new ArrayAdapter(getFragmentActivity(), android.R.layout.simple_spinner_dropdown_item, mApplicantType);
        mIndustriesAdapter = new ArrayAdapter(getFragmentActivity(), android.R.layout.simple_spinner_dropdown_item, industriesList);
        mApplicantAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerApplicant.setAdapter(mApplicantAdapter);
        mIndustriesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mIndustryType.setAdapter(mIndustriesAdapter);
        officeSetUpType = (Spinner) view.findViewById(R.id.officeSetUpType);
        ArrayList<String> officeTypeList = CommonUtils.getOfficeStUpType();
        ArrayAdapter mAdapter = new ArrayAdapter(getFragmentActivity(), android.R.layout.simple_spinner_dropdown_item, officeTypeList);
        mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        officeSetUpType.setAdapter(mAdapter);
        officeSetUpType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                officeSetUpType.setSelection(position);
                if (position != 0)
                    officeTypeStr = officeSetUpType.getSelectedItem().toString();

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
        etLastYearPAT.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                CommonUtils.insertCommaIntoNumber(etLastYearPAT, s, "##,##,###");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etTotalExistingEMI.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                CommonUtils.insertCommaIntoNumber(etTotalExistingEMI, s, "##,##,###");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mSpinnerApplicant.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mSpinnerApplicant.setSelection(position);
                if (position != 0)
                    applicantType = mSpinnerApplicant.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }


        });

        mIndustryType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mIndustryType.setSelection(position);
                if (position != 0)
                    industryTypeValue = mIndustryType.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }


        });

        CustomSpinnerAdapter adapter = new CustomSpinnerAdapter(FinanceDB.BANK_NAME, mactvBankName, getFragmentActivity(), R.layout.spinner_dropdown_item, null,
                new String[]{FinanceDB.BANK_NAME, FinanceDB.AUTOINCREMENT_ID}, new int[]{R.id.tvSpinnerItem, R.id.tvSpinnerItemID}, 0);
        mactvBankName.setThreshold(1);
        mactvBankName.setAdapter(adapter);

        etStartDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().equals("")) return;
                if (Integer.parseInt(s.toString().split("/")[2]) == Calendar.getInstance().get(Calendar.YEAR)) {
                    etLastYearPAT.setKeyListener(null);
                    etLastYearPAT.setCursorVisible(false);
                    etLastYearPAT.setPressed(false);
                    etLastYearPAT.setFocusable(false);
                    etLastYearPAT.setText("");
                } else {
                    etLastYearPAT.setInputType(InputType.TYPE_CLASS_NUMBER);
                    etLastYearPAT.setEnabled(true);
                    etLastYearPAT.setCursorVisible(true);
                    etLastYearPAT.setPressed(true);
                    etLastYearPAT.setFocusable(true);
                    etLastYearPAT.setFocusableInTouchMode(true);
                }
            }
        });
    }

    private Activity getFragmentActivity() {
        return mActivity;
    }

    public boolean validate()
    {
        mBusinessTypeDetails.setApplicantType(applicantType);
        mBusinessTypeDetails.setIndustryType(industryTypeValue);
        mBusinessTypeDetails.setOfficeSetUpType(officeTypeStr);
        if(!etStartDate.getText().toString().trim().equals(""))
        {
            mBusinessTypeDetails.setStartDate(etStartDate.getText().toString().trim());
        }
        else
        {
            //etStartDate.setError("Enter date");
            //return false;
        }
        if(!etLastYearPAT.getText().toString().trim().equals(""))
        {
            mBusinessTypeDetails.setLastYearPAT(etLastYearPAT.getText().toString().trim().replace(",", ""));
        }
        else
        {
            //etLastYearPAT.setError("Enter last year PAT");
            //return false;
        }
        if(!etTotalExistingEMI.getText().toString().trim().equals(""))
        {
            mBusinessTypeDetails.setTotalExistingEMI(etTotalExistingEMI.getText().toString().trim().replace(",", ""));
        }
        else
        {
            //etTotalExistingEMI.setError("Enter total existing EMI");
            //return false;
        }
        if(!mactvBankName.getText().toString().trim().equals(""))
        {
            String bankId = FinanceDBHelper.getBankId(mactvBankName.getText().toString());
            mBusinessTypeDetails.setBankName(bankId);
        }
        else
        {
            //mactvBankName.setError("Enter bank name");
            //return false;
        }
        if (!workCity.getText().toString().trim().equals("")) {
            mBusinessTypeDetails.setWorkCity(workCity.getText().toString().trim());
        }
    return true;
    }

   /* @Subscribe
    public void salaryTypeEvent(FinanceSalaryTypeEvents event) {
        if(event .getmSalaryType().equals(Constants.FINANCE_EMP_TYPE_BUSINESS)) {
            if(validate())
                mBusinessTypeListener.onBusinessTypeDetailsCompleted(mBusinessTypeDetails);
        }

    }*/

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
        etStartDate.setText(stringDate);
    }

    @Override
    public void onDateTimeCancel() {

    }

    public interface BusinessTypeListener {
        void onBusinessTypeDetailsCompleted(BusinessTypeDetails businessTypeDetails);
    }
}