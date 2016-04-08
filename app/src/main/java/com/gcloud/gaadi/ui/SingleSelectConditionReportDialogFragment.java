package com.gcloud.gaadi.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import com.gcloud.gaadi.ApplicationController;
import com.gcloud.gaadi.constants.ConditionType;
import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.events.ConditionSingleSelectEvent;
import com.gcloud.gaadi.model.BasicListItemModel;

import java.util.ArrayList;

/**
 * Created by ankit on 15/1/15.
 */
public class SingleSelectConditionReportDialogFragment extends DialogFragment {

    private Activity activity;
    private ArrayList<BasicListItemModel> mItems;
    private ConditionType mConditionType;
    private int title;
    private int selectedIndex;

    public SingleSelectConditionReportDialogFragment() {

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
        outState.putSerializable(Constants.LIST_ITEMS, mItems);
        outState.putInt(Constants.DIALOG_TITLE, title);
        outState.putSerializable(Constants.CONDITION_TYPE, mConditionType);
        outState.putInt(Constants.SELECTED_CONDITION_TYPE_INDEX, selectedIndex);
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

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        setRetainInstance(true);
        Bundle args = getArguments();
        if (args != null) {
            mConditionType = (ConditionType) args.getSerializable(Constants.CONDITION_TYPE);
            title = args.getInt(Constants.DIALOG_TITLE);
            mItems = (ArrayList<BasicListItemModel>) args.getSerializable(Constants.LIST_ITEMS);
            selectedIndex = args.getInt(Constants.SELECTED_CONDITION_TYPE_INDEX);

        } else if (savedInstanceState != null) {
            mConditionType = (ConditionType) savedInstanceState.getSerializable(Constants.CONDITION_TYPE);
            title = savedInstanceState.getInt(Constants.DIALOG_TITLE);
            mItems = (ArrayList<BasicListItemModel>) savedInstanceState.getSerializable(Constants.LIST_ITEMS);
            selectedIndex = savedInstanceState.getInt(Constants.SELECTED_CONDITION_TYPE_INDEX);

        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getFragmentActivity());

        builder.setTitle(title);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (0 <= selectedIndex && selectedIndex < mItems.size()) {
                    ApplicationController.getEventBus().post(new ConditionSingleSelectEvent(String.valueOf(selectedIndex), mItems.get(selectedIndex), mConditionType));
                }
            }
        });

        builder.setSingleChoiceItems(getItemsArray(), selectedIndex, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selectedIndex = which;
            }
        });

        return builder.create();
    }

    private String[] getItemsArray() {
        int count = mItems.size();
        String[] itemsArray = new String[count];
        for (int i = 0; i < count; ++i) {
            itemsArray[i] = mItems.get(i).getValue();
        }
        return itemsArray;
    }
}
