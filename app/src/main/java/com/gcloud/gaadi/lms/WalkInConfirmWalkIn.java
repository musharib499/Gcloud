package com.gcloud.gaadi.lms;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.gcloud.gaadi.R;
import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.db.ManageLeadDB;
import com.gcloud.gaadi.model.LeadData;
import com.gcloud.gaadi.ui.LeadFollowUpActivity;
import com.gcloud.gaadi.utils.CommonUtils;
import com.gcloud.gaadi.utils.GCLog;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Created by Gaurav on 29-06-2015.
 */
public class WalkInConfirmWalkIn extends DialogFragment implements DialogInterface.OnKeyListener, View.OnClickListener, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private static final String TAG = "WalkInConfirmWalkIn";
    private final Uri LMS_URI = Uri.parse(new StringBuilder().append("content://")
            .append(Constants.LMS_CONTENT_AUTHORITY).append("/").append(ManageLeadDB.TABLE_NAME).toString());
    private LeadData leadData;
    private Activity activity;
    private LMSInterface listener;
    private int nextKey = Constants.OUTSIDE_TOUCHED;
    //private DatePicker datePicker;
    //private TimePicker timePicker;
    private TextView /*previousTime,*/ noTimeSet;
    private View dialogLayout;
    private LinearLayout dateTimeLayout, noTimeSetLayout;
    private DateTime dateTime;
    private DateTimeFormatter fmt;
//    private String scheduleStatus;

    private Activity getFragmentActivity() {
        return activity;
    }

    public static WalkInConfirmWalkIn getInstance(Bundle data,
                                                  Activity activity,
                                                  LMSInterface listener) {
        WalkInConfirmWalkIn fragment = new WalkInConfirmWalkIn();
        fragment.leadData = (LeadData) data.get(Constants.MODEL_DATA);
        fragment.activity = activity;
        fragment.listener = listener;
//        fragment.scheduleStatus = scheduleStatus;
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = (LayoutInflater) getFragmentActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        dialogLayout = inflater.inflate(R.layout.lms_walk_in_confirm_walk_in, null);
        ((TextView) dialogLayout.findViewById(R.id.title)).setText(CommonUtils.getReplacementString(getActivity(),
                R.string.notYetCalled_callUnsuccess_callback_schedule,
                leadData.getName()));
        noTimeSet = (TextView) dialogLayout.findViewById(R.id.noTimeSet);
        dateTimeLayout = (LinearLayout) dialogLayout.findViewById(R.id.dateTimeLayout);
        noTimeSetLayout = (LinearLayout) dialogLayout.findViewById(R.id.notTimeSetLayout);
        //previousTime = (TextView) dialogLayout.findViewById(R.id.previousTime);
        dateTime = DateTime.now();
        long notificationTime = getNotificationTime(leadData.getNumber());

        if (notificationTime != 0) {
            dateTime = dateTime.withMillis(notificationTime);
        /*Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(ApplicationController.getManageLeadDB().getNotificationTime(leadData.getLeadId()));*/
            fmt = DateTimeFormat.forPattern("hh:mm a");
            /*previousTime.setText("Date: " + dateTime.dayOfMonth().getAsText() + "-" + (dateTime.monthOfYear().getAsShortText()) + "-" + dateTime.year().getAsText()
                    + " Time: " + fmt.print(dateTime));*/

            ((TextView) dialogLayout.findViewById(R.id.date)).setText(dateTime.dayOfMonth().getAsText());
            ((TextView) dialogLayout.findViewById(R.id.month)).setText(dateTime.monthOfYear().getAsShortText());
            ((TextView) dialogLayout.findViewById(R.id.year)).setText(dateTime.year().getAsText());
            ((TextView) dialogLayout.findViewById(R.id.time)).setText(CommonUtils.getReplacementString(getFragmentActivity(),
                    R.string.time, fmt.print(dateTime)));
            //datePicker = (DatePicker) dialogLayout.findViewById(R.id.date); //calendar.getTimeInMillis(); //minDate + (long) 60 * 24 * 60 * 60 * 1000; //Adding 60 days as max date
            //fmt = DateTimeFormat.forPattern("h:m a");

            dialogLayout.findViewById(R.id.dateScheduled).setOnClickListener(this);
            dialogLayout.findViewById(R.id.timeScheduled).setOnClickListener(this);
            dialogLayout.findViewById(R.id.modify).setVisibility(View.VISIBLE);
            dialogLayout.findViewById(R.id.modify).setOnClickListener(this);
        } else {
            dateTimeLayout.setVisibility(View.GONE);
            noTimeSetLayout.setVisibility(View.VISIBLE);
        }
        ((TextView) dialogLayout.findViewById(R.id.confirm)).setText("CANCEL");
        dialogLayout.findViewById(R.id.confirm).setOnClickListener(this);
        /*datePicker.setMinDate(minDate);
        datePicker.setMaxDate(maxDate);*/
        //timePicker = (TimePicker) dialogLayout.findViewById(R.id.time);
        AlertDialog dialog = new AlertDialog.Builder(getFragmentActivity())
                //.setTitle(CommonUtils.getReplacementString(getActivity(), R.string.notYetCalled_callUnsuccess_callback_schedule, leadData.getName()))
                .setView(dialogLayout)
                        //.setPositiveButton("Cancel", null)
                        //.setNegativeButton(notificationTime == 0 ? "" :"Modify", null)
                .create();
        dialog.getWindow().getAttributes().windowAnimations = R.style.AnimationDialog;
        dialog.setOnKeyListener(this);
        return dialog;
    }

    private long getNotificationTime(String number) {
        long result = 0;
        Cursor cursor = getFragmentActivity().getContentResolver().query(LMS_URI,
                new String[]{ManageLeadDB.NOTIFICATION_TIME},
                ManageLeadDB.NUMBER + " = ?",
                new String[]{number}, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                result = Long.valueOf(cursor.getString(cursor.getColumnIndex(ManageLeadDB.NOTIFICATION_TIME)));
                cursor.close();
            }
        }
        return result;
    }

    @Override
    public void onResume() {
        super.onResume();
        /*final AlertDialog dialog = (AlertDialog) getDialog();
        Button modifyButton = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        if (!modifyButton.getText().toString().isEmpty()) {
            modifyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (dateTime.isBeforeNow()) {
                        CommonUtils.showToast(getFragmentActivity(), "Past time", Toast.LENGTH_SHORT);
                    } else {
                        leadData.setStatus(LeadFollowUpActivity.WALK_IN_SCHEDULE);
                        Intent intent = new Intent(getActivity(), LeadManageIntentService.class);
                        intent.putExtra(LeadManageIntentService.LEAD_DATA, leadData);
                        intent.putExtra(LeadManageIntentService.ACTION_ID, LeadManageIntentService.ACTION.UPDATE);
                        intent.putExtra(LeadManageIntentService.NOTIFICATION_TIME, dateTime.getMillis());
                        if (getActivity().startService(intent) == null)
                            GCLog.w(TAG, "start Service failed");
                        CommonUtils.showToast(getFragmentActivity(),
                                "Walk-in confirmed for " + leadData.getName() + " at "
                                        + fmt.print(dateTime),
                                Toast.LENGTH_LONG);
                        dialog.dismiss();
                    }
                }
            });
        }
        Button okButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });*/
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        listener.onFragmentDismiss(nextKey);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dateScheduled:
                DatePickerDialog datePickerDialog = new DatePickerDialog(getFragmentActivity(), /*android.R.style.Theme_Material_Dialog,*/
                        this, dateTime.getYear(), dateTime.getMonthOfYear() - 1, dateTime.getDayOfMonth());
                /*minDate = DateTime.now().getMillis() - 10000;
                maxDate = DateTime.now().plusDays(60).getMillis();*/
                datePickerDialog.getDatePicker().setMaxDate(DateTime.now().plusDays(60).getMillis());
                datePickerDialog.getDatePicker().setMinDate(DateTime.now().getMillis() - 10000);

                if (!getFragmentActivity().isFinishing() && !datePickerDialog.isShowing()) {
                    datePickerDialog.show();
                }
                break;

            case R.id.timeScheduled:
                TimePickerDialog timePickerDialog = new TimePickerDialog(getFragmentActivity(), /*android.R.style.Theme_Material_Dialog,*/
                        this, dateTime.getHourOfDay(), dateTime.getMinuteOfHour(), false);

                if (!getFragmentActivity().isFinishing() && !timePickerDialog.isShowing()) {
                    timePickerDialog.show();
                }
                break;

            case R.id.modify:
                if (dateTime.isBefore(DateTime.now().withSecondOfMinute(59).withMillisOfSecond(999))) {
                    CommonUtils.showToast(getFragmentActivity(), "You can not select Past time", Toast.LENGTH_SHORT);
                } else {
                    leadData.setStatus(LeadFollowUpActivity.WALK_IN_SCHEDULE);
                    Intent intent = new Intent(getActivity(), LeadManageIntentService.class);
                    intent.putExtra(LeadManageIntentService.LEAD_DATA, leadData);
                    intent.putExtra(LeadManageIntentService.ACTION_ID, LeadManageIntentService.ACTION.UPDATE);
                    intent.putExtra(LeadManageIntentService.NOTIFICATION_TIME, dateTime.getMillis());
                    if (getActivity().startService(intent) == null)
                        GCLog.w(TAG, "start Service failed");
                    CommonUtils.showToast(getFragmentActivity(),
                            "Walk-in confirmed for " + leadData.getName() + " at " + dateTime.dayOfMonth().getAsText()
                                    + " " + dateTime.monthOfYear().getAsShortText() + ", "
                                    + fmt.print(dateTime),
                            Toast.LENGTH_LONG);
                    getDialog().dismiss();
                }
                break;

            case R.id.confirm:
                getDialog().dismiss();
                break;
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

        dateTime = dateTime.withDate(year, monthOfYear + 1, dayOfMonth);

        ((TextView) dialogLayout.findViewById(R.id.date)).setText(dateTime.dayOfMonth().getAsText());
        ((TextView) dialogLayout.findViewById(R.id.month)).setText(dateTime.monthOfYear().getAsShortText());
        ((TextView) dialogLayout.findViewById(R.id.year)).setText(dateTime.year().getAsText());

    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

        dateTime = dateTime.withTime(hourOfDay, minute, DateTime.now().getSecondOfMinute(), DateTime.now().getMillisOfSecond());

        ((TextView) dialogLayout.findViewById(R.id.time)).setText(CommonUtils.getReplacementString(getFragmentActivity(),
                R.string.time, fmt.print(dateTime)));

    }
}
