package com.gcloud.gaadi.Fragments;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FilterQueryProvider;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.gcloud.gaadi.ApplicationController;
import com.gcloud.gaadi.R;
import com.gcloud.gaadi.adapter.CityAdapter;
import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.db.MakeModelVersionDB;
import com.gcloud.gaadi.model.CityData;
import com.gcloud.gaadi.model.FinancePersonalDetails;
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
import java.util.regex.Pattern;

/**
 * Created by Lakshay on 11-08-2015.
 */
public class PersonalDetailsFragment extends Fragment implements View.OnClickListener, SlideDateTimeListener, AdapterView.OnItemClickListener {

    CityAdapter cityAdapter;
    private Context mContext;
    private PersonalDetailsActionListener mPersonalDetailsActionListener;
    private FinancePersonalDetails mPersonalDetailsObj;
    private MaterialEditText etFirstName, etLastName, etMobile, etEmail;
    private RadioGroup rbgGender, rbgStayLength;
    private RadioButton rbMale, rbFemale, rbLessThanOne, rbOnwOrTwo, rbMOreThanTwo;
    private String selectedResidenceType = null;
    private Button btnNext;
    private MaterialTextView etBirthDate;
    private Spinner mSpinnerResidence;
    private ArrayList<String> mResidenceType;
    private ArrayAdapter mResidenceAdapter;
    private Date minDate, maxDate, initialDate;
    private Activity mActivity;
    private MaterialAutoCompleteTextView homeCity;
    private FilterQueryProvider cityFilterQueryProvider = new FilterQueryProvider() {
        @Override
        public Cursor runQuery(CharSequence constraint) {
            return getCityCursor(constraint);
        }
    };

    public static PersonalDetailsFragment getInstance(Context context, PersonalDetailsActionListener personalDetailsActionListener) {
        PersonalDetailsFragment fragment = new PersonalDetailsFragment();
        fragment.mContext = context;
        fragment.mPersonalDetailsActionListener = personalDetailsActionListener;
        fragment.mPersonalDetailsObj = new FinancePersonalDetails();
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

            homeCity.setText(selectedCity.getCityName());
            homeCity.setSelection(homeCity.getText().length());
            GCLog.e("selected City: " + selectedCity.toString());
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
    }

    private Activity getFragmentActivity() {
        return mActivity;
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(mContext == null || mPersonalDetailsActionListener == null)
        {
            mContext = getFragmentActivity();
            mPersonalDetailsActionListener = (PersonalDetailsActionListener) getFragmentActivity();
            mPersonalDetailsObj = new FinancePersonalDetails();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.finance_personal_form_layout, null);
        getFragmentActivity().setTitle(Constants.FINANCE_FORMS);
        initializeWidgets(view);
        view.findViewById(R.id.next).setOnClickListener(this);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void initializeWidgets(View view) {
        etFirstName = (MaterialEditText) view.findViewById(R.id.firstName);
        etLastName = (MaterialEditText) view.findViewById(R.id.lastName);
        etMobile = (MaterialEditText) view.findViewById(R.id.mobile_No);
        etEmail = (MaterialEditText) view.findViewById(R.id.email);
        btnNext = (Button) view.findViewById(R.id.next);
        homeCity = (MaterialAutoCompleteTextView) view.findViewById(R.id.homeCity);
        rbgGender = (RadioGroup) view.findViewById(R.id.radioGroup);
        rbgStayLength = (RadioGroup) view.findViewById(R.id.rbgStayLength);
        rbMale = (RadioButton) view.findViewById(R.id.rbMale);
        rbFemale = (RadioButton) view.findViewById(R.id.rbFemale);
        rbLessThanOne = (RadioButton) view.findViewById(R.id.rbLessThanOne);
        rbOnwOrTwo = (RadioButton) view.findViewById(R.id.rbOneOrTwo);
        rbMOreThanTwo = (RadioButton) view.findViewById(R.id.rbMOreThanTwo);
        etBirthDate = (MaterialTextView) view.findViewById(R.id.etDateOfBirth);
      //  setDatePickerValues(etBirthDate);
       // etBirthDate.setmActivity(getFragmentActivity());
        etBirthDate.setOnClickListener(this);
        mResidenceType = CommonUtils.getResidenceType();
        mSpinnerResidence = (Spinner) view.findViewById(R.id.residence_Type);
        mResidenceAdapter = new ArrayAdapter(getFragmentActivity(), android.R.layout.simple_spinner_dropdown_item, mResidenceType);
        mResidenceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerResidence.setAdapter(mResidenceAdapter);
        mSpinnerResidence.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mSpinnerResidence.setSelection(position);
                if (position != 0)
                    selectedResidenceType = mSpinnerResidence.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        cityAdapter = new CityAdapter(mContext, null);
        homeCity.setAdapter(cityAdapter);
        homeCity.setThreshold(1);
        homeCity.setOnItemClickListener(PersonalDetailsFragment.this);
        cityAdapter.setFilterQueryProvider(cityFilterQueryProvider);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.next:
                if (validate(this.getView()))
                    mPersonalDetailsActionListener.onPersonalDetailsFilled(mPersonalDetailsObj);
                break;
            case R.id.etDateOfBirth:
                setDatePickerValues(etBirthDate);
                break;

        }
    }

    private void setDatePickerValues(MaterialTextView etBirthDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -18);
        maxDate = calendar.getTime();
        SimpleDateFormat dateformat = new SimpleDateFormat("dd-MM-yyyy");
        String strdate = Constants.INITIAL_BIRTH_DATE;
        try {
            initialDate = dateformat.parse(strdate);
            minDate = dateformat.parse("01-01-1945");
        } catch (Exception e) {
            e.printStackTrace();
        }
       /* etBirthDate.setMinDate(minDate);
        etBirthDate.setMaxDate(maxDate);
        etBirthDate.setInitialDate(initialDate);*/
        onDailogs();
    }

    public void onDailogs()
    {
        new SlideDateTimePicker.Builder(getFragmentManager())
                .setShowTimeTab(false)
                .setListener(this)
                .setInitialDate(initialDate)
                .setMinDate(minDate)
                .setMaxDate(maxDate)

                .build().show();
    }

    private boolean validate(View view) {
        if (etFirstName.getText().toString().trim().equals("")) {
            etFirstName.setError("Enter first name");
            return false;
        } else {
            mPersonalDetailsObj.setFirstName(etFirstName.getText().toString().trim());
        }
        if (etLastName.getText().toString().trim().equals("")) {
            //etLastName.setError("Enter last name");
            //return false;
        } else {
            mPersonalDetailsObj.setLastName(etLastName.getText().toString().trim());
        }
        if (etMobile.getText().toString().trim().equals("")) {
            etMobile.setError("Enter mobile number");
            return false;
        }
        if (etMobile.getText().toString().trim().length() < 10)
        {
            etMobile.setError("Enter valid mobile number");
            return false;
        }
        if (!Pattern.compile("^[7-9]").matcher(etMobile.getText().toString().trim()).find()) {
            etMobile.setError("Enter valid mobile number");
            return false;
        } else {
            mPersonalDetailsObj.setPhone(etMobile.getText().toString().trim());
        }
        if (etEmail.getText().toString().trim().equals("")) {
            etEmail.setError("Enter Email ID");
            return false;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(etEmail.getText().toString().trim()).matches()) {
            etEmail.setError("Enter valid email ID");
            return false;
        } else {
            mPersonalDetailsObj.setEmail(etEmail.getText().toString().trim());
        }
        if (etBirthDate.getText().toString().trim().equals("")) {
            //etBirthDate.setError("Enter Date of Birth");
            //return false;
        } else {
            mPersonalDetailsObj.setDateOfBirth(etBirthDate.getText().toString().trim());
        }
        if (!homeCity.getText().toString().trim().equals("")) {
            mPersonalDetailsObj.setHomeCity(homeCity.getText().toString().trim());
        }
        int selectedGenderID = rbgGender.getCheckedRadioButtonId();
        String selectedGender = null;
        if (selectedGenderID == rbMale.getId()) selectedGender = "Male";
        if (selectedGenderID == rbFemale.getId()) selectedGender = "Female";
        mPersonalDetailsObj.setGender(selectedGender);

        mPersonalDetailsObj.setResidenceType(selectedResidenceType);
        String stayLength = null;
        int stayLengthID = rbgStayLength.getCheckedRadioButtonId();
        if (stayLengthID == rbLessThanOne.getId())
            stayLength = "1";
        if (stayLengthID == rbOnwOrTwo.getId())
            stayLength = "2";
        if (stayLengthID == rbMOreThanTwo.getId())
            stayLength = "3";
        mPersonalDetailsObj.setStayInYears(stayLength);
        return true;
    }

    @Override
    public void onDateTimeSet(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String stringDate = sdf.format(date);
        etBirthDate.setText(stringDate);
    }

    @Override
    public void onDateTimeCancel() {

    }

    private Cursor getCityCursor(CharSequence constraint) {
        return ApplicationController.getMakeModelVersionDB().getCityRecords(constraint);
    }

    public interface PersonalDetailsActionListener {
        void onPersonalDetailsFilled(FinancePersonalDetails detailsObj);
    }


}
