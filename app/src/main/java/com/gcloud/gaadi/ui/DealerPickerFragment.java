package com.gcloud.gaadi.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;

import com.gcloud.gaadi.ApplicationController;
import com.gcloud.gaadi.R;
import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.db.DBFunction;
import com.gcloud.gaadi.db.InsuranceDB;
import com.gcloud.gaadi.db.LeadsOfflineDB;
import com.gcloud.gaadi.db.MakeModelVersionDB;
import com.gcloud.gaadi.db.ManageLeadDB;
import com.gcloud.gaadi.model.DealerData;
import com.gcloud.gaadi.utils.CommonUtils;
import com.gcloud.gaadi.utils.GCLog;

import java.util.ArrayList;

/**
 * Created by ankit on 29/12/14.
 */
public class DealerPickerFragment extends DialogFragment {


    private static final String ARG_TITLE = "ARG_TITLE";
    private static final String ARG_ITEMS = "ARG_ITEMS";
    private static final String ARG_SELECTED_INDEX = "ARG_SELECTED_INDEX";
    private Activity activity;
    private String title;
    private ArrayList<DealerData> dealers;
    private int selectedIndex;

    /**
     * Constructor
     */
    public DealerPickerFragment() {
    }

    /**
     * Create a new instance of ItemPickerDialogFragment with specified arguments
     *
     * @param title         Dialog title text
     * @param selectedIndex initial selection index, or -1 if no item should be pre-selected
     * @return ItemPickerDialogFragment
     */
    public static DealerPickerFragment newInstance(String title, int selectedIndex) {
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putSerializable(ARG_ITEMS, ApplicationController.getMakeModelVersionDB().getDealers());
        args.putInt(ARG_SELECTED_INDEX, selectedIndex);

        DealerPickerFragment fragment = new DealerPickerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public void show(FragmentManager fragmentManager, String s) {
        super.show(fragmentManager, s);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    private Activity getFragmentActivity() {
        return activity;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(ARG_SELECTED_INDEX, selectedIndex);
        outState.putSerializable(ARG_ITEMS, dealers);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        setRetainInstance(true);
        Bundle args = getArguments();
        if (args != null) {
            title = args.getString(ARG_TITLE, "Dialog");
            dealers = (ArrayList<DealerData>) args.getSerializable(ARG_ITEMS);
            selectedIndex = args.getInt(ARG_SELECTED_INDEX, -1);
        }

        //dealers = ApplicationController.getMakeModelVersionDB().getDealers();

        if (savedInstanceState != null) {
            selectedIndex = savedInstanceState.getInt(ARG_SELECTED_INDEX, selectedIndex);
            dealers = (ArrayList<DealerData>) savedInstanceState.getSerializable(ARG_ITEMS);
        }

        String[] dealersArray = getDealersArray();

        AlertDialog.Builder builder = new AlertDialog.Builder(getFragmentActivity());
        builder.setTitle(title)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        GCLog.e("OK button clicked");
                        Constants.isDealerChanged = true;

                        if (getFragmentActivity() instanceof OnDealerSelectedListener) {
                            if (0 <= selectedIndex && selectedIndex < dealers.size()) {
                                DealerData dealer = dealers.get(selectedIndex);
                                GCLog.e("dealer: " + dealer.toString());
                                OnDealerSelectedListener listener = (OnDealerSelectedListener) getFragmentActivity();
                                CommonUtils.setIntSharedPreference(getFragmentActivity(), Constants.SELECTED_DEALER_INDEX, selectedIndex);
                                CommonUtils.setIntSharedPreference(getFragmentActivity(), Constants.DEALER_ID, Integer.parseInt(dealer.getDealerId()));
                                CommonUtils.setIntSharedPreference(getFragmentActivity(), Constants.UC_DEALER_ID, Integer.parseInt(dealer.getUCdid()));
                                CommonUtils.setStringSharedPreference(getFragmentActivity(), Constants.UC_DEALER_PASSWORD, dealer.getDealerPassword());
                                CommonUtils.setStringSharedPreference(getFragmentActivity(), Constants.UC_DEALER_EMAIL, dealer.getmEmail());
                                CommonUtils.setStringSharedPreference(getFragmentActivity(), Constants.UC_DEALER_MOBILE, dealer.getMobileSms());
                                CommonUtils.setStringSharedPreference(getFragmentActivity(), Constants.UC_DEALER_USERNAME, dealer.getmUsername());
                                CommonUtils.setStringSharedPreference(getFragmentActivity(), Constants.UC_WARRANTY_ONLY_DEALER, dealer.getWarrantOnlyDealer());
                                CommonUtils.setStringSharedPreference(getFragmentActivity(), Constants.UC_CARDEKHO_INVENTORY, dealer.getCardekhoInventory());
                                CommonUtils.setIntSharedPreference(getFragmentActivity(), Constants.IS_SELLER, 0);
                                CommonUtils.setIntSharedPreference(getFragmentActivity(), Constants.IS_LMS, 0);
                                CommonUtils.removeSharedPreference(getFragmentActivity(), Constants.LMS_LAST_SYNCED_TIME);
                                CommonUtils.removeSharedPreference(getFragmentActivity(), Constants.LEADS_LAST_SYNCED_TIME);
                                CommonUtils.removeSharedPreference(getFragmentActivity(), Constants.INSURANCE_DASHBOARD_OFFLINE_DATA);
                                CommonUtils.removeSharedPreference(getFragmentActivity(), Constants.STOCK_CHANGE_TIME);
                                CommonUtils.removeSharedPreference(getFragmentActivity(), Constants.IS_ACTIVE_RUNNING_STOCK_SERVICE);
                                CommonUtils.removeSharedPreference(getFragmentActivity(), Constants.IS_INACTIVE_RUNNING_STOCK_SERVICE);
                                CommonUtils.removeSharedPreference(getFragmentActivity(), Constants.IS_ERROR_STOCK_SERVICE);

                                /*remove this before making live.
                                CommonUtils.setBooleanSharedPreference(context, Constants.RE_SYNCED, false);*/
                                ApplicationController.getManageLeadDB().getWritableDatabase().delete(ManageLeadDB.TABLE_NAME, null, null);
                                ApplicationController.getLeadsOfflineDB().getWritableDatabase().delete(LeadsOfflineDB.TABLE_NAME, null, null);
                                ApplicationController.getMakeModelVersionDB().getWritableDatabase().delete(MakeModelVersionDB.TABLE_NOTIFICATION, null, null);
                                ApplicationController.getInsuranceDB().getWritableDatabase().delete(InsuranceDB.TABLE_IMAGES, null, null);
                                DBFunction.clearStocksTable();

                                // for bug when changing dealer, previously saved form data on add stock doesn't get clear.
                                ApplicationController.getStocksDB().deletePreviousData();

                                if (listener != null) {
                                    listener.onDealerSelected(DealerPickerFragment.this, dealer, selectedIndex);

                                }
                            }
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        GCLog.d("Cancel button clicked");

                        // OK, just let the dialog be closed
                    }
                })
                .setSingleChoiceItems(dealersArray, selectedIndex, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        GCLog.d("User clicked item with index " + which);
                        selectedIndex = which;
                    }
                });

        return builder.create();
    }

    private String[] getDealersArray() {
        final int itemCount = dealers.size();
        String[] dealersArray = new String[itemCount];
        for (int i = 0; i < itemCount; ++i) {
            dealersArray[i] = dealers.get(i).getOrganization();
        }
        return dealersArray;
    }

    /**
     * Interface for notification of item selection
     * <p/>
     * If the owning Activity implements this interface, then the fragment will
     * invoke its onItemSelected() method when the user clicks the OK button.
     */
    public interface OnDealerSelectedListener {
        void onDealerSelected(DealerPickerFragment fragment, DealerData dealer, int index);
    }

}
