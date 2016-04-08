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
import com.gcloud.gaadi.model.Finance.ProfessionTypeDetails;
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
public class FinanceProfessionTypeFragment extends Fragment implements View.OnClickListener, SlideDateTimeListener, AdapterView.OnItemClickListener {

    Activity mActivity;
    CityAdapter cityAdapter;
    private MaterialTextView etStartDate;
    private MaterialEditText etLastYearPAT, etTotalExistingEMI;
    private MaterialAutoCompleteTextView mactvBankName;
    private String professionType = null, officeTypeStr;
    private Spinner mSpinnerProfession, officeSetUpType;
    private ArrayList<String> mProfessionType;
    private ArrayAdapter mProfessionAdapter;
    private ProfessionTypeListener mProfessionTypeListener;
    private ProfessionTypeDetails mProfessionTypeDetails;
    private MaterialAutoCompleteTextView workCity;
    private FilterQueryProvider cityFilterQueryProvider = new FilterQueryProvider() {
        @Override
        public Cursor runQuery(CharSequence constraint) {
            return getCityCursor(constraint);
        }
    };

    public static FinanceProfessionTypeFragment getInstance(ProfessionTypeListener listener) {
        FinanceProfessionTypeFragment fragment = new FinanceProfessionTypeFragment();
        fragment.mProfessionTypeListener = listener;
        fragment.mProfessionTypeDetails = new ProfessionTypeDetails();
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

    private Activity getFragmentActivity() {
        return mActivity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = LayoutInflater.from(getFragmentActivity()).inflate(R.layout.finance_profess_layout, null);
        initializeWidgets(view);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bNext:
                if(validate())
                    mProfessionTypeListener.onProfessionTypeDetailsCompleted(mProfessionTypeDetails);
                break;
            case  R.id.start_date:
                onDailogs();
                break;
        }
    }

    public void onDailogs() {
        new SlideDateTimePicker.Builder(getFragmentManager())
                .setShowTimeTab(false)
                .setListener(this)

                .setMaxDate(new Date())

                .build().show();
    }

    /* @Subscribe
     public void salaryTypeEvent(FinanceSalaryTypeEvents event) {
         if(event .getmSalaryType().equals(Constants.FINANCE_EMP_TYPE_PROFESS)) {
             if(validate())
                 mProfessionTypeListener.onProfessionTypeDetailsCompleted(mProfessionTypeDetails);
         }

     }*/
    private void initializeWidgets(View view)
    {
        etLastYearPAT = (MaterialEditText) view.findViewById(R.id.last_year_income);
        etTotalExistingEMI = (MaterialEditText) view.findViewById(R.id.emi);
        mactvBankName = (MaterialAutoCompleteTextView) view.findViewById(R.id.mactv_bankName);
        etStartDate = (MaterialTextView) view.findViewById(R.id.start_date);
        etStartDate.setOnClickListener(this);
        //etStartDate.setMaxDate(new Date());
        //etStartDate.setmActivity(getActivity());
        workCity = (MaterialAutoCompleteTextView) view.findViewById(R.id.workCity);
        cityAdapter = new CityAdapter(mActivity, null);
        workCity.setAdapter(cityAdapter);
        workCity.setThreshold(1);
        workCity.setOnItemClickListener(this);
        cityAdapter.setFilterQueryProvider(cityFilterQueryProvider);
        mProfessionType = FinanceDBHelper.getProfession(Constants.FINANCE_EMP_TYPE_PROFESS_SERVER);
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
        mSpinnerProfession = (Spinner) view.findViewById(R.id.profession_Type);
        view.findViewById(R.id.bNext).setOnClickListener(this);
        mProfessionAdapter = new ArrayAdapter(getFragmentActivity(), android.R.layout.simple_spinner_dropdown_item, mProfessionType);
        mProfessionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerProfession.setAdapter(mProfessionAdapter);
        mSpinnerProfession.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mSpinnerProfession.setSelection(position);
                if (position != 0)
                    professionType = mSpinnerProfession.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

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

        CustomSpinnerAdapter adapter = new CustomSpinnerAdapter(FinanceDB.BANK_NAME, mactvBankName, getFragmentActivity(), R.layout.spinner_dropdown_item,
                null, new String[]{FinanceDB.AUTOINCREMENT_ID, FinanceDB.BANK_NAME}, new int[]{R.id.tvSpinnerItemID, R.id.tvSpinnerItem}, 0);
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
                if (s.toString().equals("")) return;
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
    /*@Override
    public void onResume() {
        super.onResume();
        ApplicationController.getEventBus().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        ApplicationController.getEventBus().unregister(this);
    }*/

    public boolean validate()
    {
        mProfessionTypeDetails.setProfessionType(professionType);
        mProfessionTypeDetails.setOfficeSetUpType(officeTypeStr);
        if (!etStartDate.getText().toString().trim().equals("")) {
            mProfessionTypeDetails.setStartDate(etStartDate.getText().toString().trim());
        }
        else
        {
            //etStartDate.setError("Enter date");
            //return false;
        }
        if(!etLastYearPAT.getText().toString().trim().equals(""))
        {
            mProfessionTypeDetails.setLastYearPAT(etLastYearPAT.getText().toString().trim().replace(",", ""));
        }
        else
        {
            //etLastYearPAT.setError("Enter last year PAT");
            //return false;
        }
        if(!etTotalExistingEMI.getText().toString().trim().equals(""))
        {
            mProfessionTypeDetails.setTotalExistingEMI(etTotalExistingEMI.getText().toString().trim().replace(",", ""));

        }
        else
        {
            //etTotalExistingEMI.setError("Enter total existing EMI");
            //return false;
        }
        if(!mactvBankName.getText().toString().trim().equals(""))
        {
            String bankId = FinanceDBHelper.getBankId(mactvBankName.getText().toString());
            mProfessionTypeDetails.setBankName(bankId);
        }
        else
        {
            //mactvBankName.setError("Enter bank name");
            //return false;
        }
        if (!workCity.getText().toString().trim().equals("")) {
            mProfessionTypeDetails.setWorkCity(workCity.getText().toString().trim());
        }
        return true;
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

    public interface ProfessionTypeListener {
        void onProfessionTypeDetailsCompleted(ProfessionTypeDetails professionTypeDetails);
    }
}
