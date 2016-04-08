package com.gcloud.gaadi.ui;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.LinearLayout;
import android.widget.ListPopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.gcloud.gaadi.ApplicationController;
import com.gcloud.gaadi.R;
import com.gcloud.gaadi.adapter.CityAdapter;
import com.gcloud.gaadi.adapter.DealershipAdapter;
import com.gcloud.gaadi.adapter.MakeAdapter;
import com.gcloud.gaadi.adapter.ModelAdapter;
import com.gcloud.gaadi.annotations.Required;
import com.gcloud.gaadi.annotations.rules.Rule;
import com.gcloud.gaadi.annotations.rules.Validator;
import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.constants.MakeModelType;
import com.gcloud.gaadi.db.MakeModelVersionDB;
import com.gcloud.gaadi.events.ClearDealershipListEvent;
import com.gcloud.gaadi.events.NetworkEvent;
import com.gcloud.gaadi.interfaces.OnNoInternetConnectionListener;
import com.gcloud.gaadi.model.CityData;
import com.gcloud.gaadi.model.ShowroomData;
import com.gcloud.gaadi.model.ShowroomModel;
import com.gcloud.gaadi.retrofit.RetrofitRequest;
import com.gcloud.gaadi.utils.CommonUtils;
import com.gcloud.gaadi.utils.GAHelper;
import com.gcloud.gaadi.utils.GCLog;
import com.squareup.otto.Subscribe;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;

import retrofit.Callback;
import retrofit.RetrofitError;

/**
 * Created by ankit on 9/1/15.
 */
public class DealerSearchFragment extends Fragment
        implements AdapterView.OnItemClickListener, OnNoInternetConnectionListener,
        View.OnClickListener, Validator.ValidationListener {

    TextView showrooms;
    EditText priceMax, priceMin;
    LinearLayout searchCars;
    String dealerShipId = "";
    int mPriceMax, mPriceMin;
    private Activity activity;
    private View rootView;
    private Validator mValidator;
    //    private GAHelper mGAHelper;
    private GCProgressDialog progressDialog;
    private ArrayList<CityData> cityList;
    private CityAdapter cityAdapter;
    @Required(order = 1, messageResId = R.string.error_field_required)
    private CustomAutoCompleteTextView city;
    @Required(order = 2, messageResId = R.string.error_field_required)
    private CustomAutoCompleteTextView make;
    @Required(order = 3, messageResId = R.string.error_field_required)
    private CustomAutoCompleteTextView model;
    private DealershipAdapter dealershipAdapter;
    private ShowroomData selectedShowroom;
    private ArrayList<ShowroomData> showroomList;
    private MakeAdapter makeAdapter;
    private ModelAdapter modelAdapter;
    private String makeId = "";
    private FilterQueryProvider cityFilterQueryProvider = new FilterQueryProvider() {
        @Override
        public Cursor runQuery(CharSequence constraint) {
            return getCityCursor(constraint);
        }
    };

    private FilterQueryProvider makeFilterQueryProvider = new FilterQueryProvider() {
        @Override
        public Cursor runQuery(CharSequence constraint) {
            return getMakeCursor(constraint);
        }
    };

    private FilterQueryProvider modelFilterQueryProvider = new FilterQueryProvider() {
        @Override
        public Cursor runQuery(CharSequence constraint) {
            return getModelCursor(constraint);
        }
    };

    public DealerSearchFragment() {

    }

    private Cursor getModelCursor(CharSequence constraint) {
        return ApplicationController.getMakeModelVersionDB().getModelRecords(constraint, makeId, true);
    }

    private Cursor getMakeCursor(CharSequence constraint) {
        return ApplicationController.getMakeModelVersionDB().getMakeRecords(constraint);
    }

    private Cursor getCityCursor(CharSequence constraint) {
        return ApplicationController.getMakeModelVersionDB().getCityRecords(constraint);
    }

    private String getCityId(String cityName) {
        return ApplicationController.getMakeModelVersionDB().getCityId(cityName);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
//        mGAHelper = new GAHelper(getFragmentActivity());
        //progressDialog = new GCProgressDialog(activity, activity);


    }

    private Activity getFragmentActivity() {
        return activity;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(Constants.LIST_ITEMS, cityList);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_dealer_search, container, false);
        setRetainInstance(true);

        if (savedInstanceState != null) {
            cityList = (ArrayList<CityData>) savedInstanceState.getSerializable(Constants.LIST_ITEMS);
        }
        mValidator = new Validator(this);
        mValidator.setValidationListener(this);
        city = (CustomAutoCompleteTextView) rootView.findViewById(R.id.city);
        city.setType(MakeModelType.CITY);
        // get dealerCityName and retrieve cityId on the basis of that from db
        String dealerCityName = CommonUtils.getStringSharedPreference(getActivity(), Constants.UC_DEALER_CITY, "");
        city.setText(dealerCityName);
        makeShowroomRequest(getCityId(dealerCityName), false);
        showrooms = (TextView) rootView.findViewById(R.id.showroom);
        priceMax = (EditText) rootView.findViewById(R.id.priceTo);
        priceMin = (EditText) rootView.findViewById(R.id.priceFrom);
        make = (CustomAutoCompleteTextView) rootView.findViewById(R.id.make);
        makeAdapter = new MakeAdapter(getFragmentActivity(), null);
        make.setAdapter(makeAdapter);
        make.setThreshold(1);
        make.setOnItemClickListener(this);
        makeAdapter.setFilterQueryProvider(makeFilterQueryProvider);
        searchCars = (LinearLayout) rootView.findViewById(R.id.searchCars);
        model = (CustomAutoCompleteTextView) rootView.findViewById(R.id.model);
        model.setEnabled(false);
        model.setClickable(false);

        searchCars.setOnClickListener(this);
        cityAdapter = new CityAdapter(getFragmentActivity(), null);
        city.setAdapter(cityAdapter);
        city.setThreshold(1);
        city.setOnItemClickListener(DealerSearchFragment.this);
        cityAdapter.setFilterQueryProvider(cityFilterQueryProvider);
        return rootView;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getAdapter() instanceof CityAdapter) {
            Cursor cursor = (Cursor) parent.getAdapter().getItem(position);
            String cityName = cursor.getString(cursor.getColumnIndex(MakeModelVersionDB.CITY_NAME));
            String cityId = cursor.getString(cursor.getColumnIndex(MakeModelVersionDB.CITY_ID));
            city.setText(cityName);


            makeShowroomRequest(cityId, false);
            GCLog.e("cursor selected: " + cursor.toString());
        } else if (parent.getAdapter() instanceof MakeAdapter) {
            Cursor cursor = (Cursor) parent.getAdapter().getItem(position);
            String makeName = cursor.getString(cursor.getColumnIndex(MakeModelVersionDB.MAKENAME));
            makeId = cursor.getString(cursor.getColumnIndex(MakeModelVersionDB.MAKEID));
            make.setText(makeName);
            model.setEnabled(true);
            model.setClickable(true);

            modelAdapter = new ModelAdapter(getFragmentActivity(), null);
            model.setAdapter(modelAdapter);
            model.setThreshold(1);
            model.setOnItemClickListener(this);
            modelAdapter.setFilterQueryProvider(modelFilterQueryProvider);

        } else if (parent.getAdapter() instanceof ModelAdapter) {
            Cursor cursor = (Cursor) parent.getAdapter().getItem(position);
            String modelName = cursor.getString(cursor.getColumnIndex(MakeModelVersionDB.MODELNAME));
            String modelId = cursor.getString(cursor.getColumnIndex(MakeModelVersionDB.MODELID));
            model.setText(modelName);

        }
    }

    private void makeShowroomRequest(String cityId, boolean showFullPageError) {

        //progressDialog.show();

        HashMap<String, String> params = new HashMap<>();
        params.put("cityID", cityId);
        params.put(Constants.DEALER_USERNAME, CommonUtils.getStringSharedPreference(getFragmentActivity(), Constants.UC_DEALER_USERNAME, ""));
        params.put(Constants.UCDID, String.valueOf(CommonUtils.getIntSharedPreference(getFragmentActivity(), Constants.UC_DEALER_ID, -1)));
        params.put(Constants.API_KEY_LABEL, Constants.API_KEY);
        params.put(Constants.API_RESPONSE_FORMAT_LABEL, Constants.API_RESPONSE_FORMAT);
        params.put(Constants.METHOD_LABEL, Constants.SHOWROOM_BY_CITY_METHOD);

        RetrofitRequest.showroomRequest(params, new Callback<ShowroomModel>() {
            @Override
            public void success(ShowroomModel showroomModel, retrofit.client.Response response) {
                if ("T".equalsIgnoreCase(showroomModel.getStatus())) {
                    showroomList = showroomModel.getShowroomList();

                    if (showroomList.size() == 0) {
                        showrooms.setHint("No Dealership Available");
                        dealerShipId = "";
                    } else if (showroomList.size() == 1) {
                        selectedShowroom = showroomList.get(0);
                        showrooms.setText("Dealership: " + showroomList.get(0).getShowroomName());
                        dealerShipId = showroomList.get(0).getShowroomId();
                        showrooms.setClickable(false);

                    } else {
                        dealershipAdapter = new DealershipAdapter(getFragmentActivity(), showroomList);
                        showrooms.setHint("Dealership");
                        //showrooms.setThreshold(1);
                        showrooms.setOnClickListener(DealerSearchFragment.this);
                    }
                } else {
                    if (showroomModel.getErrorMessage().equalsIgnoreCase("No records found.")) {
                        showrooms.setText("");
                        showrooms.setHint("No Dealership Available");
                    } else {
                        CommonUtils.showToast(getFragmentActivity(), "Dealership names cannot be fetched.", Toast.LENGTH_SHORT);
                    }
                }
            }

            @Override
            public void failure(RetrofitError error) {
                if (error.getCause() instanceof UnknownHostException) {

                    CommonUtils.showToast(getFragmentActivity(), getString(R.string.no_internet_connection), Toast.LENGTH_SHORT);
                } else {
                    CommonUtils.showToast(getFragmentActivity(), "Server Error. Dealerships cannot be fetched", Toast.LENGTH_SHORT);
                }
            }
        });
       /* ShowroomByCityRequest showroomRequest = new ShowroomByCityRequest(getFragmentActivity(),
                Request.Method.POST,
                Constants.getWebServiceURL(getFragmentActivity()),
                params,
                new Response.Listener<ShowroomModel>() {
                    @Override
                    public void onResponse(ShowroomModel response) {
                        //progressDialog.dismiss();
                        if ("T".equalsIgnoreCase(response.getStatus())) {
                            showroomList = response.getShowroomList();

                            if (showroomList.size() == 0) {
                                showrooms.setHint("No dealership available.");
                                dealerShipId = "";
                            } else if (showroomList.size() == 1) {
                                selectedShowroom = showroomList.get(0);
                                showrooms.setText("Dealership: " + showroomList.get(0).getShowroomName());
                                dealerShipId = showroomList.get(0).getShowroomId();
                                showrooms.setClickable(false);

                            } else {
                                dealershipAdapter = new DealershipAdapter(getFragmentActivity(), showroomList);
                                showrooms.setHint("Dealership");
                                //showrooms.setThreshold(1);
                                showrooms.setOnClickListener(DealerSearchFragment.this);
                            }
                        } else {
                            if (response.getErrorMessage().equalsIgnoreCase("No records found.")) {
                                showrooms.setText("");
                                showrooms.setHint("No dealership available.");
                            } else {
                                CommonUtils.showToast(getFragmentActivity(), "Dealership names cannot be fetched.", Toast.LENGTH_SHORT);
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.getCause() instanceof UnknownHostException) {

                            CommonUtils.showToast(getFragmentActivity(), "Network connection error. Dealerships cannot be fetched", Toast.LENGTH_SHORT);
                        } else {
                            CommonUtils.showToast(getFragmentActivity(), "Server Error. Dealerships cannot be fetched", Toast.LENGTH_SHORT);
                        }
                    }
                });

        ApplicationController.getInstance().addToRequestQueue(showroomRequest, Constants.TAG_SHOWROOM_REQUEST, showFullPageError, this);
   */ }

    @Override
    public void onNoInternetConnection(NetworkEvent networkEvent) {
        ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                Constants.CATEGORY_DEALER_PLATFORM,
                Constants.CATEGORY_DEALER_PLATFORM,
                Constants.ACTION_TAP,
                Constants.LABEL_NO_INTERNET,
                0);
        if (networkEvent.isShowFullPageError()) {

        } else {
      /*if (progressDialog != null) {
        progressDialog.dismiss();
      }*/
            CommonUtils.showToast(getFragmentActivity(), networkEvent.getNetworkError().name(), Toast.LENGTH_SHORT);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.showroom:
                final ListPopupWindow dealerShipPopupWindow = new ListPopupWindow(getFragmentActivity());

                dealerShipPopupWindow.setAdapter(dealershipAdapter);
                dealerShipPopupWindow.setModal(true);
                dealerShipPopupWindow.setBackgroundDrawable(getFragmentActivity().getResources().getDrawable(R.drawable.abc_popup_background_mtrl_mult));
                dealerShipPopupWindow.setAnchorView(rootView.findViewById(R.id.showroom));
                dealerShipPopupWindow.setWidth(ListPopupWindow.WRAP_CONTENT);
                dealerShipPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        selectedShowroom = (ShowroomData) parent.getAdapter().getItem(position);
                        showrooms.setText("Dealership: " + selectedShowroom.getShowroomName());
                        dealerShipId = selectedShowroom.getShowroomId();
                        GCLog.e("Dealership name: " + selectedShowroom.getShowroomName() + "\n Dealership ID " + dealerShipId);
                        dealerShipPopupWindow.dismiss();
                    }
                });
                showrooms.post(new Runnable() {
                    @Override
                    public void run() {
                        dealerShipPopupWindow.show();
                    }
                });
                break;

            case R.id.searchCars:
                if (!city.getText().toString().isEmpty()
                        && cityAdapter.getCursor() != null && !(cityAdapter.getCursor().getCount() > 0)) {
                    CommonUtils.showToast(getFragmentActivity(), getString(R.string.text_has_invalid_value, "City"), Toast.LENGTH_SHORT);
                    return;
                }

                if (!make.getText().toString().isEmpty()
                        && makeAdapter.getCursor() != null && !(makeAdapter.getCursor().getCount() > 0)) {
                    CommonUtils.showToast(getFragmentActivity(), getString(R.string.text_has_invalid_value, "Make"), Toast.LENGTH_SHORT);
                    return;
                }

                if (!model.getText().toString().isEmpty()
                        && modelAdapter.getCursor() != null && !(modelAdapter.getCursor().getCount() > 0)) {
                    CommonUtils.showToast(getFragmentActivity(), getString(R.string.text_has_invalid_value, "Model"), Toast.LENGTH_SHORT);
                    return;
                }


                mPriceMax = 0;
                mPriceMin = 0;
                ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                        Constants.CATEGORY_DEALER_PLATFORM,
                        Constants.CATEGORY_DEALER_PLATFORM,
                        Constants.ACTION_TAP,
                        Constants.LABEL_SEARCH_BUTTON,
                        0);
                if (priceMax.getText().toString().trim().length() > 0 && Integer.parseInt(priceMax.getText().toString().trim()) > 0)
                    mPriceMax = Integer.parseInt(priceMax.getText().toString().trim());
                if (priceMin.getText().toString().trim().length() > 0 && Integer.parseInt(priceMin.getText().toString().trim()) > 0)
                    mPriceMin = Integer.parseInt(priceMin.getText().toString().trim());
                if (mPriceMax != 0 && mPriceMin > mPriceMax) {
                    CommonUtils.showToast(getFragmentActivity(), "Minimum price should be less than maximum price.", Toast.LENGTH_SHORT);
                } else {
                    if(CommonUtils.isNetworkAvailable(getContext())) {
                        showSearchedCarsList();
                    }
                    else {
                        CommonUtils.showToast(getFragmentActivity(), getString(R.string.no_internet_connection), Toast.LENGTH_SHORT);
                    }
                }
                break;
        }
    }

    private void showSearchedCarsList() {
        Intent intent = new Intent(getActivity(), SearchCarsListActivity.class);
        Bundle args = new Bundle();
        args.putString(Constants.MAKE, make.getText().toString());
        args.putString(Constants.MODEL, model.getText().toString());
        args.putString(Constants.CITY_NAME, city.getText().toString());
        args.putString(Constants.DEALER_ID, dealerShipId);
        args.putString(Constants.PRICE_MAX, priceMax.getText().toString());
        args.putString(Constants.PRICE_MIN, priceMin.getText().toString());
        intent.putExtras(args);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }

    @Override
    public void onPause() {
        super.onPause();
        ApplicationController.getEventBus().unregister(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        ApplicationController.getEventBus().register(this);
    }

    @Override
    public void onValidationSucceeded() {

        //showSearchedCarsList();
    }

    @Override
    public void onValidationFailed(View failedView, Rule<?> failedRule) {
        //clearErrors();

        if (failedView instanceof CustomAutoCompleteTextView) {
            ((CustomAutoCompleteTextView) failedView).setError(failedRule.getFailureMessage());
            failedView.requestFocus();
        }


        CommonUtils.shakeView(getActivity(), failedView);
    }

    @Override
    public void onViewValidationSucceeded(View succeededView) {

    }

    public void clearDealerShipList() {

        if (showroomList != null) {
            showroomList.clear();
        }
        if (dealershipAdapter != null) {
            dealershipAdapter.notifyDataSetChanged();
        }
        showrooms.setText("");
        showrooms.setHint("Dealership (Please select city first)");

    }

    @Subscribe
    public void clearList(ClearDealershipListEvent clearDealershipListEvent) {
        dealerShipId = "";
        clearDealerShipList();
    }
}
