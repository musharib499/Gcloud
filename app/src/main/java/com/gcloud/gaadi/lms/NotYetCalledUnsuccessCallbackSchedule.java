package com.gcloud.gaadi.lms;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
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
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.gcloud.gaadi.R;
import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.model.LeadData;
import com.gcloud.gaadi.utils.CommonUtils;
import com.gcloud.gaadi.utils.GCLog;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Calendar;

/**
 * Created by Gaurav on 23-06-2015.
 */
public class NotYetCalledUnsuccessCallbackSchedule extends DialogFragment implements DialogInterface.OnKeyListener,
        View.OnClickListener, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private static final String TAG = "NotYetCalledSuccess";
    private LeadData leadData;
    private Activity activity;
    private LMSInterface listener;
    private int nextKey = Constants.OUTSIDE_TOUCHED;
    //private DatePicker datePicker;
    //private TimePicker timePicker;
    private String scheduleStatus;
    private DateTime dateTime;
    private DateTimeFormatter fmt;
    private long maxDate, minDate;
    private View dialogLayout, clicked;

    private Activity getFragmentActivity() {
        return activity;
    }

    public static NotYetCalledUnsuccessCallbackSchedule getInstance(Bundle data,
                                                                    Activity activity,
                                                                    LMSInterface listener,
                                                                    String scheduleStatus) {
        NotYetCalledUnsuccessCallbackSchedule fragment = new NotYetCalledUnsuccessCallbackSchedule();
        fragment.leadData = (data.containsKey(Constants.MODEL_DATA)) ?
                (LeadData) data.get(Constants.MODEL_DATA) : (LeadData) data.get(LeadManageIntentService.LEAD_DATA);
        fragment.activity = activity;
        fragment.listener = listener;
        fragment.scheduleStatus = scheduleStatus;
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        minDate = Calendar.getInstance().getTimeInMillis();
        LayoutInflater inflater = (LayoutInflater) getFragmentActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        dialogLayout = inflater.inflate(R.layout.lms_not_yet_called_unsuccess_callback_schedule, null);
        ((TextView) dialogLayout.findViewById(R.id.title)).setText(CommonUtils.getReplacementString(getActivity(),
                R.string.notYetCalled_callUnsuccess_callback_schedule,
                leadData.getName()));
        dialogLayout.findViewById(R.id.dateScheduled).setOnClickListener(this);
        dialogLayout.findViewById(R.id.timeScheduled).setOnClickListener(this);
        dialogLayout.findViewById(R.id.confirm).setOnClickListener(this);
        dateTime = DateTime.now();
        GCLog.e(dateTime.toString());

        ((TextView) dialogLayout.findViewById(R.id.date)).setText(dateTime.dayOfMonth().getAsText());
        ((TextView) dialogLayout.findViewById(R.id.month)).setText(dateTime.monthOfYear().getAsShortText());
        ((TextView) dialogLayout.findViewById(R.id.year)).setText(dateTime.year().getAsText());

        fmt = DateTimeFormat.forPattern("hh:mm a");


        ((TextView) dialogLayout.findViewById(R.id.time)).setText(CommonUtils.getReplacementString(getFragmentActivity(),
                R.string.time, fmt.print(dateTime)));

        //datePicker = (DatePicker) dialogLayout.findViewById(R.id.date);
        maxDate = minDate + (long) 60 * 24 * 60 * 60 * 1000; //Adding 60 days as max date
        minDate -= 10000;
        /*datePicker.setMinDate(minDate);
        datePicker.setMaxDate(maxDate);
        timePicker = (TimePicker) dialogLayout.findViewById(R.id.time);*/
        AlertDialog dialog = new AlertDialog.Builder(getFragmentActivity())
                //.setTitle(CommonUtils.getReplacementString(getActivity(), R.string.notYetCalled_callUnsuccess_callback_schedule, leadData.getName()))
                .setView(dialogLayout)
                        //.setPositiveButton("Confirm", null)
                .create();
        dialog.getWindow().getAttributes().windowAnimations = R.style.AnimationDialog;
        dialog.setOnKeyListener(this);
        return dialog;
    }

    @Override
    public void onResume() {
        super.onResume();
        /*final AlertDialog dialog = (AlertDialog) getDialog();
        Button okButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                GCLog.e(dateTime.toString());

                    if (dateTime.isBeforeNow()) {
                        CommonUtils.showToast(getFragmentActivity(), "Past time", Toast.LENGTH_SHORT);

                    } else {

                        leadData.setStatus(scheduleStatus);
                        Intent intent = new Intent(getActivity(), LeadManageIntentService.class);
                        intent.putExtra(LeadManageIntentService.LEAD_DATA, leadData);
                        intent.putExtra(LeadManageIntentService.ACTION_ID, LeadManageIntentService.ACTION.UPDATE);
                        intent.putExtra(LeadManageIntentService.NOTIFICATION_TIME, dateTime.getMillis());
                        if (getFragmentActivity().startService(intent) == null)
                            GCLog.w(TAG, "start Service failed");
                        CommonUtils.showToast(getFragmentActivity(),
                                leadData.getName() + " scheduled for " + dateTime.dayOfMonth().getAsText()
                                + " " + dateTime.monthOfYear().getAsShortText() + ", "
                                + dateTime.dayOfMonth().getAsText() + " "
                                + fmt.print(dateTime),
                                Toast.LENGTH_LONG);
                        dialog.dismiss();
                    }
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
        v.setEnabled(false);
        clicked = v;
        switch (v.getId()) {
            case R.id.confirm:
                GCLog.e(dateTime.toString());

                if (dateTime.isBefore(DateTime.now().withSecondOfMinute(59).withMillisOfSecond(999))) {
                    CommonUtils.showToast(getFragmentActivity(), "You can not select Past time", Toast.LENGTH_SHORT);
                    v.setEnabled(true);
                } else {
                    leadData.setStatus(scheduleStatus);
                    Intent intent = new Intent(getActivity(), LeadManageIntentService.class);
                    intent.putExtra(LeadManageIntentService.LEAD_DATA, leadData);
                    intent.putExtra(LeadManageIntentService.ACTION_ID, LeadManageIntentService.ACTION.UPDATE);
                    intent.putExtra(LeadManageIntentService.NOTIFICATION_TIME, dateTime.getMillis());
                    if (getFragmentActivity().startService(intent) == null)
                        GCLog.w(TAG, "start Service failed");
                    CommonUtils.showToast(getFragmentActivity(),
                            leadData.getName() + " scheduled for " + dateTime.dayOfMonth().getAsText()
                                    + " " + dateTime.monthOfYear().getAsShortText() + ", "
                                    + fmt.print(dateTime),
                            Toast.LENGTH_LONG);
                    getDialog().dismiss();
                }
                break;

            case R.id.dateScheduled:
                GCLog.e("Date scheduled");
                DatePickerDialog datePickerDialog = new DatePickerDialog(getFragmentActivity()/*, android.R.style.Theme_Material_Dialog*/,
                        this, dateTime.getYear(), dateTime.getMonthOfYear() - 1, dateTime.getDayOfMonth());

                datePickerDialog.getDatePicker().setMinDate(minDate);
                datePickerDialog.getDatePicker().setMaxDate(maxDate);

                datePickerDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        clicked.setEnabled(true);
                    }
                });
                datePickerDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        clicked.setEnabled(true);
                    }
                });

                if (!getFragmentActivity().isFinishing() && !datePickerDialog.isShowing()) {
                    datePickerDialog.show();
                } else {
                    v.setEnabled(true);
                }

                break;

            case R.id.timeScheduled:
                GCLog.e("Time scheduled");
                TimePickerDialog timePickerDialog = new TimePickerDialog(getFragmentActivity()/*, android.R.style.Theme_Material_Dialog*/,
                        this, dateTime.getHourOfDay(), dateTime.getMinuteOfHour(), false);

                timePickerDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        clicked.setEnabled(true);
                    }
                });
                timePickerDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        clicked.setEnabled(true);
                    }
                });

                if (!getFragmentActivity().isFinishing() && !timePickerDialog.isShowing()) {
                    timePickerDialog.show();
                } else {
                    v.setEnabled(true);
                }

                break;
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

        GCLog.e("year: " + year + ", month: " + monthOfYear + ", day of Month: " + dayOfMonth);

        dateTime = dateTime.withDate(year, monthOfYear + 1, dayOfMonth);

        ((TextView) dialogLayout.findViewById(R.id.date)).setText(dateTime.dayOfMonth().getAsText());
        ((TextView) dialogLayout.findViewById(R.id.month)).setText(dateTime.monthOfYear().getAsShortText());
        ((TextView) dialogLayout.findViewById(R.id.year)).setText(dateTime.year().getAsText());
        clicked.setEnabled(true);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

        GCLog.e("hour of day: " + hourOfDay + ", minute: " + minute);

        dateTime = dateTime.withTime(hourOfDay, minute, DateTime.now().getSecondOfMinute(), DateTime.now().getMillisOfSecond());


        ((TextView) dialogLayout.findViewById(R.id.time)).setText(CommonUtils.getReplacementString(getFragmentActivity(),
                R.string.time, fmt.print(dateTime)));

        clicked.setEnabled(true);
    }
}
