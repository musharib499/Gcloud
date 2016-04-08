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
import com.gcloud.gaadi.utils.GCLog;

import java.util.Calendar;

/**
 * Created by Gaurav on 23-06-2015.
 */
public class NotYetCalledUnsuccessCallback extends DialogFragment implements View.OnClickListener, DialogInterface.OnKeyListener {

    private static final String TAG = "NotYetCalledUnsuccessCallback";
    private LeadData leadData;
    private Activity activity;
    private LMSInterface listener;
    private int nextKey = Constants.OUTSIDE_TOUCHED;
    private String scheduleStatus;

    public Activity getFragmentActivity() {
        return activity;
    }

    public static NotYetCalledUnsuccessCallback getInstance(Bundle data,
                                                            Activity activity,
                                                            LMSInterface listener,
                                                            String scheduleStatus) {
        NotYetCalledUnsuccessCallback fragment = new NotYetCalledUnsuccessCallback();
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
        View dialogLayout = inflater.inflate(R.layout.lms_not_yet_called_unsuccess_callback, null);
        ((TextView) dialogLayout.findViewById(R.id.title)).setText(CommonUtils.getReplacementString(getFragmentActivity(),
                R.string.notYetCalled_callUnsuccess_callback,
                leadData.getName()));
        dialogLayout.findViewById(R.id.now).setOnClickListener(this);
        dialogLayout.findViewById(R.id.after1hour).setOnClickListener(this);
        dialogLayout.findViewById(R.id.after2hour).setOnClickListener(this);
        dialogLayout.findViewById(R.id.schedule).setOnClickListener(this);
        AlertDialog dialog = new AlertDialog.Builder(getFragmentActivity())
                //.setTitle(CommonUtils.getReplacementString(getFragmentActivity(), R.string.notYetCalled_callUnsuccess_callback, leadData.getName()))
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
            case R.id.now:
                CommonUtils.activateLeadCallStateListener(getFragmentActivity(),
                        LeadFollowUpActivity.class,
                        "+91" + leadData.getNumber(),
                        Constants.VALUE_VIEWLEAD,
                        Constants.NOT_YET_CALLED_FRAG_NO,
                        leadData);
                intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:+91" + leadData.getNumber()));
                startActivity(intent);
                dismiss();
                break;

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

            case R.id.schedule:
                if (scheduleStatus.equals(LeadFollowUpActivity.FOLLOW_UP_SCHEDULE)) {
                    nextKey = 23;
                } else if (scheduleStatus.equals(LeadFollowUpActivity.WALK_IN_SCHEDULE)) {
                    nextKey = 44;
                } else {
                    nextKey = 6;
                }
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
