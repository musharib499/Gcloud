package com.gcloud.gaadi.lms;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.gcloud.gaadi.R;
import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.model.LeadData;
import com.gcloud.gaadi.ui.LeadFollowUpActivity;
import com.gcloud.gaadi.utils.CommonUtils;

/**
 * Created by Gaurav on 23-06-2015.
 */
public class NotYetCalledSuccess extends DialogFragment implements View.OnClickListener, DialogInterface.OnKeyListener {

    private static final String TAG = "NotYetCalledSuccess";
    private LeadData leadData;
    private Activity activity;
    private LMSInterface listener;
    private int nextKey = Constants.OUTSIDE_TOUCHED;
    private String scheduleStatus;

    public Activity getFragmentActivity() {
        return activity;
    }

    public static NotYetCalledSuccess getInstance(Bundle data,
                                                  Activity activity,
                                                  LMSInterface listener,
                                                  String scheduleStatus) {
        NotYetCalledSuccess fragment = new NotYetCalledSuccess();
        fragment.leadData = (LeadData) data.get(Constants.MODEL_DATA);
        fragment.activity = activity;
        fragment.listener = listener;
        fragment.scheduleStatus = scheduleStatus;
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = (LayoutInflater) getFragmentActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogLayout = inflater.inflate(R.layout.lms_not_yet_called_success, null);
        ((TextView) dialogLayout.findViewById(R.id.title)).setText(CommonUtils.getReplacementString(getActivity(),
                R.string.notYetCalled_callSuccess,
                leadData.getName()));
        dialogLayout.findViewById(R.id.yes).setOnClickListener(this);
        dialogLayout.findViewById(R.id.no).setOnClickListener(this);
        AlertDialog dialog = new AlertDialog.Builder(getFragmentActivity())
                //.setTitle(CommonUtils.getReplacementString(getActivity(), R.string.notYetCalled_callSuccess, leadData.getName()))
                .setView(dialogLayout)
                .create();
        dialog.getWindow().getAttributes().windowAnimations = R.style.AnimationDialog;
        dialog.setOnKeyListener(this);
        return dialog;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        listener.onFragmentDismiss(nextKey);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.yes:
                switch (scheduleStatus) {
                    case LeadFollowUpActivity.NOT_YET_CALLED:
                        nextKey = 12;
                        break;
                    case LeadFollowUpActivity.FOLLOW_UP_SCHEDULE:
                        nextKey = 32;
                        break;
                }
                dismiss();
                break;

            case R.id.no:
                nextKey = Constants.MARK_CLOSED;
                dismiss();
                break;
        }
    }

    @Override
    public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
        if (i == KeyEvent.KEYCODE_BACK) {
            nextKey = Constants.BACK_PRESSED;
            dismiss();
            return true;
        } else {
            return false;
        }
    }
}
