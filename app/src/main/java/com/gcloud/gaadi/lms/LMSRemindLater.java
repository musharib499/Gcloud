package com.gcloud.gaadi.lms;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.gcloud.gaadi.utils.CommonUtils;
import com.gcloud.gaadi.utils.GCLog;

import java.util.Calendar;

/**
 * Created by Gaurav on 30-06-2015.
 */
public class LMSRemindLater extends DialogFragment implements View.OnClickListener, DialogInterface.OnKeyListener {

    private static final String TAG = "LMSRemindLater";
    private LeadData leadData;
    private Activity activity;
    private LMSInterface listener;
    private int nextKey = Constants.OUTSIDE_TOUCHED;
    private String scheduleStatus;

    public Activity getFragmentActivity() {
        return activity;
    }

    public static LMSRemindLater getInstance(Bundle data,
                                             Activity activity,
                                             LMSInterface listener,
                                             String scheduleStatus) {
        LMSRemindLater fragment = new LMSRemindLater();
        fragment.leadData = (LeadData) data.get(LeadManageIntentService.LEAD_DATA);
        fragment.activity = activity;
        fragment.listener = listener;
        fragment.scheduleStatus = scheduleStatus;
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = (LayoutInflater) getFragmentActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogLayout = inflater.inflate(R.layout.lms_remind_later, null);
        ((TextView) dialogLayout.findViewById(R.id.title)).setText(CommonUtils.getReplacementString(getFragmentActivity(),
                R.string.remind_later,
                leadData.getName()));
        dialogLayout.findViewById(R.id.after1hour).setOnClickListener(this);
        dialogLayout.findViewById(R.id.after2hour).setOnClickListener(this);
        dialogLayout.findViewById(R.id.after4hour).setOnClickListener(this);
        dialogLayout.findViewById(R.id.dontRemind).setOnClickListener(this);
        AlertDialog dialog = new AlertDialog.Builder(getFragmentActivity())
                //.setTitle(CommonUtils.getReplacementString(getFragmentActivity(), R.string.remind_later, leadData.getName()))
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
        leadData.setStatus(scheduleStatus);
        switch (view.getId()) {
            case R.id.after1hour:
                notificationTime += (long) 60 * 60 * 1000;
                intent = new Intent(getActivity(), LeadManageIntentService.class);
                intent.putExtra(LeadManageIntentService.LEAD_DATA, leadData);
                intent.putExtra(LeadManageIntentService.ACTION_ID, LeadManageIntentService.ACTION.UPDATE);
                intent.putExtra(LeadManageIntentService.NOTIFICATION_TIME, notificationTime);
                if (getActivity().startService(intent) == null)
                    GCLog.w(TAG, "start Service failed");
                dismiss();
                break;

            case R.id.after2hour:
                notificationTime += (long) 2 * 60 * 60 * 1000;
                intent = new Intent(getActivity(), LeadManageIntentService.class);
                intent.putExtra(LeadManageIntentService.LEAD_DATA, leadData);
                intent.putExtra(LeadManageIntentService.ACTION_ID, LeadManageIntentService.ACTION.UPDATE);
                intent.putExtra(LeadManageIntentService.NOTIFICATION_TIME, notificationTime);
                if (getActivity().startService(intent) == null)
                    GCLog.w(TAG, "start Service failed");
                dismiss();
                break;

            case R.id.after4hour:
                notificationTime += (long) 4 * 60 * 60 * 1000;
                intent = new Intent(getActivity(), LeadManageIntentService.class);
                intent.putExtra(LeadManageIntentService.LEAD_DATA, leadData);
                intent.putExtra(LeadManageIntentService.ACTION_ID, LeadManageIntentService.ACTION.UPDATE);
                intent.putExtra(LeadManageIntentService.NOTIFICATION_TIME, notificationTime);
                if (getActivity().startService(intent) == null)
                    GCLog.w(TAG, "start Service failed");
                dismiss();
                break;

            case R.id.dontRemind:
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
