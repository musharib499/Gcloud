package com.gcloud.gaadi.lms;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
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

import java.util.Calendar;

/**
 * Created by Gaurav on 30-06-2015.
 */
public class WalkInNo extends DialogFragment implements View.OnClickListener, DialogInterface.OnKeyListener {

    private static final String TAG = "WalkInNo";
    private LeadData leadData;
    private Activity activity;
    private LMSInterface listener;
    private int nextKey = Constants.OUTSIDE_TOUCHED;

    public Activity getFragmentActivity() {
        return activity;
    }

    public static WalkInNo getInstance(Bundle data,
                                       Activity activity,
                                       LMSInterface listener) {
        WalkInNo fragment = new WalkInNo();
        fragment.leadData = (LeadData) data.get(Constants.MODEL_DATA);
        fragment.activity = activity;
        fragment.listener = listener;
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = (LayoutInflater) getFragmentActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogLayout = inflater.inflate(R.layout.lms_walkin_no, null);
        ((TextView) dialogLayout.findViewById(R.id.title)).setText(R.string.notYetCalled_callSuccess_yes);
        dialogLayout.findViewById(R.id.markClosed).setOnClickListener(this);
        dialogLayout.findViewById(R.id.scheduleWalkIn).setOnClickListener(this);
        dialogLayout.findViewById(R.id.call).setOnClickListener(this);
        AlertDialog dialog = new AlertDialog.Builder(getFragmentActivity())
                //.setTitle(CommonUtils.getReplacementString(getFragmentActivity(), R.string.notYetCalled_callSuccess_yes, leadData.getName()))
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
        Intent intent;
//        Calendar calendar = Calendar.getInstance();
        long notificationTime = Calendar.getInstance().getTimeInMillis();
        leadData.setStatus(LeadFollowUpActivity.WALK_IN_SCHEDULE);
        switch (view.getId()) {
            case R.id.call:
                String number = leadData.getNumber();
                if ((number != null) && !number.isEmpty() && !"null".equalsIgnoreCase(number)) {
                    CommonUtils.activateLeadCallStateListener(getFragmentActivity(),
                            LeadFollowUpActivity.class,
                            "+91" + number,
                            Constants.VALUE_VIEWLEAD,
                            Constants.NOT_YET_CALLED_FRAG_NO,
                            leadData);
                    intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse("tel:+91" + number));

                    startActivity(intent);
                }
                dismiss();
                break;

            case R.id.markClosed:
                nextKey = Constants.MARK_CLOSED;
                dismiss();
                break;

            case R.id.scheduleWalkIn:
                nextKey = 111;
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
