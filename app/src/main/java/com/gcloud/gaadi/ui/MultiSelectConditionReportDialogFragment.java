package com.gcloud.gaadi.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import com.gcloud.gaadi.ApplicationController;
import com.gcloud.gaadi.R;
import com.gcloud.gaadi.adapter.ListItemAdapter;
import com.gcloud.gaadi.constants.ConditionType;
import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.events.ConditionSelectedEvent;
import com.gcloud.gaadi.model.BasicListItemModel;

import java.util.ArrayList;

/**
 * Created by ankit on 14/1/15.
 */
public class MultiSelectConditionReportDialogFragment extends DialogFragment
        implements DialogInterface.OnClickListener {

    private Activity activity;
    private ArrayList<BasicListItemModel> mItems;
    private ListItemAdapter mAdapter;
    private ConditionType mConditionType;
    private int title;
    private String selectedIds = "";

    public MultiSelectConditionReportDialogFragment() {

    }

    private Activity getFragmentActivity() {
        return activity;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(Constants.DIALOG_TITLE, title);
        outState.putSerializable(Constants.LIST_ITEMS, mItems);
        outState.putSerializable(Constants.CONDITION_TYPE, mConditionType);
        outState.putSerializable(Constants.SELECTED_IDS, selectedIds);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        setRetainInstance(true);

        Bundle args = getArguments();
        if (args != null) {
            title = args.getInt(Constants.DIALOG_TITLE);
            mItems = (ArrayList<BasicListItemModel>) args.getSerializable(Constants.LIST_ITEMS);
            mConditionType = (ConditionType) args.getSerializable(Constants.CONDITION_TYPE);
            if (args.containsKey(Constants.SELECTED_IDS) && !args.getString(Constants.SELECTED_IDS).isEmpty()) {
                selectedIds = args.getString(Constants.SELECTED_IDS);
                setItems(selectedIds);
            }

        } else if (savedInstanceState != null) {
            title = savedInstanceState.getInt(Constants.DIALOG_TITLE);
            mItems = (ArrayList<BasicListItemModel>) savedInstanceState.getSerializable(Constants.LIST_ITEMS);
            mConditionType = (ConditionType) savedInstanceState.getSerializable(Constants.CONDITION_TYPE);
            selectedIds = savedInstanceState.getString(Constants.SELECTED_IDS);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getFragmentActivity());
        builder.setTitle(title);

        //builder.setView(getFragmentActivity().getLayoutInflater().inflate(R.layout.layout_brands, null));
        mAdapter = new ListItemAdapter(getFragmentActivity(), mItems, mConditionType);
        builder.setAdapter(mAdapter, this);


        builder.setPositiveButton(R.string.done_label, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String ids = mAdapter.getSelectedItemsIds();
                String values = mAdapter.getSelectedItemsValues();
                int count = mAdapter.getSelectedItemsCount();
                ApplicationController.getEventBus().post(new ConditionSelectedEvent(ids, values, count, mConditionType));
            }
        });


        return builder.create();
    }

    private void setItems(String items) {
        String[] itemIds = items.split(",");
        for (int i = 0; i < itemIds.length; ++i) {
            for (int j = 0; j < mItems.size(); ++j) {
                if (mItems.get(j).getId().equals(itemIds[i])) {
                    mItems.get(j).setChecked(true);
                }
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();

        //generate();
    }

    @Override
    public void onResume() {
        super.onResume();
        ApplicationController.getEventBus().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        ApplicationController.getEventBus().unregister(this);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {

    }
}
