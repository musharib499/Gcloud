package com.gcloud.gaadi.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;

import com.gcloud.gaadi.R;

/**
 * Created by Priya on 23-06-2015.
 */
public class AddLeadOptionFragment extends DialogFragment {
    Activity activity;
    private static AddLeadOptionFragment mInstance;
    private boolean shown;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.activity_leadadd_option, null, false);

        AlertDialog.Builder builder = new AlertDialog.Builder(getFragmentActivity());
        builder.setView(view);
        builder.setTitle("Add Lead");


        return builder.create();
    }

    private Activity getFragmentActivity() {
        return activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();

    }

    public static AddLeadOptionFragment getInstance() {
        if (mInstance == null) {
            mInstance = new AddLeadOptionFragment();
        }
        return mInstance;
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        if (shown) return;

        super.show(manager, tag);
        shown = true;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        shown = false;
        super.onDismiss(dialog);
    }

    public boolean isShowing() {
        return shown;
    }

}
