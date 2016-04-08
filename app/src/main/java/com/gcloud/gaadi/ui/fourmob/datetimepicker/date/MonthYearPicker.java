package com.gcloud.gaadi.ui.fourmob.datetimepicker.date;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

import com.gcloud.gaadi.ApplicationController;
import com.gcloud.gaadi.R;
import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.events.SetMonthYearForPicker;
import com.gcloud.gaadi.ui.StockAddActivity;

import java.util.Calendar;

public class MonthYearPicker extends DialogFragment {

    public String monthYearStr;
    Calendar cal;
    NumberPicker monthPicker;
    NumberPicker yearPicker;
    DatePickerDialog.OnDateSetListener listener;
    int code = 1;
    String[] str = new String[]{"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
    private StockAddActivity stockAddActivity;

    public MonthYearPicker(int code) {
        this.code = code;
    }

    public void setListener(DatePickerDialog.OnDateSetListener listener) {
        this.listener = listener;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        cal = Calendar.getInstance();
        View view = inflater.inflate(R.layout.month_year_picker, null);
        monthPicker = (NumberPicker) view.findViewById(R.id.picker_month);
        yearPicker = (NumberPicker) view.findViewById(R.id.picker_year);
        yearPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        monthPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        monthPicker.setDisplayedValues(null);
        if (code == Constants.CODE_FOR_MFG_PICKER_DIALOG || code == Constants.CODE_FOR_RSA_PICKER_DIALOG) {
            if (cal.get(Calendar.MONTH) == 0) {
                yearPicker.setMaxValue(cal.get(Calendar.YEAR) - 1);
                yearPicker.setValue(cal.get(Calendar.YEAR) - 1);
            } else {
                yearPicker.setMaxValue(cal.get(Calendar.YEAR));
                yearPicker.setValue(cal.get(Calendar.YEAR));
            }
            if (code == Constants.CODE_FOR_MFG_PICKER_DIALOG) {
                yearPicker.setMinValue(cal.get(Calendar.YEAR) - 15);
            } else {
                yearPicker.setMinValue(cal.get(Calendar.YEAR) - 10);
            }

            monthPicker.setMinValue(1);
            monthPicker.setDisplayedValues(str);
            monthPicker.setMaxValue((cal.get(Calendar.MONTH) == 0) ? 12 : cal.get(Calendar.MONTH));
            monthPicker.setValue((cal.get(Calendar.MONTH) == 0) ? 12 : cal.get(Calendar.MONTH));
        }
        if (code == Constants.CODE_FOR_INSURANCE_PICKER_DIALOG) {
            String[] arr = new String[(12 - cal.get(Calendar.MONTH))];
            yearPicker.setMinValue(cal.get(Calendar.YEAR));
            yearPicker.setMaxValue(cal.get(Calendar.YEAR) + 1);
            yearPicker.setValue(cal.get(Calendar.YEAR));
            for (int i = (cal.get(Calendar.MONTH)); i < 12; i++) {
                arr[i - (cal.get(Calendar.MONTH))] = str[i];
            }
            monthPicker.setMinValue(cal.get(Calendar.MONTH) + 1);
            monthPicker.setDisplayedValues(arr);
            monthPicker.setMaxValue(12);
            //monthPicker.setValue(cal.get(Calendar.MONTH) + 1);

        }
        yearPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {

                refreshValuesOfMonthAndYear(newVal);
            }
        });
        monthPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                refreshValuesOfMonthAndYear(yearPicker.getValue());
            }
        });

        if (code == Constants.CODE_FOR_MFG_PICKER_DIALOG) {
            builder.setTitle("Select the month and year of manufacture");
        } else if (code == Constants.CODE_FOR_INSURANCE_PICKER_DIALOG) {
            builder.setTitle("Select the month and year of Insurance");
        } else {
            builder.setTitle("Select the month and year of Registration");
        }
        builder.setView(view)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        listener.onDateSet(null, 0, monthPicker.getValue(), yearPicker.getValue());
                        monthYearStr = ApplicationController.monthShortMap.get(monthPicker.getValue() + "") + "/" + yearPicker.getValue();
                        if (code == Constants.CODE_FOR_MFG_PICKER_DIALOG) {
                            ApplicationController.getEventBus().post(new SetMonthYearForPicker(monthYearStr, 1));
                        } else if (code == Constants.CODE_FOR_INSURANCE_PICKER_DIALOG) {
                            ApplicationController.getEventBus().post(new SetMonthYearForPicker(monthYearStr, 2));
                        } else {
                            ApplicationController.getEventBus().post(new SetMonthYearForPicker(monthYearStr, 3));
                        }

                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        MonthYearPicker.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }

    private void refreshValuesOfMonthAndYear(int yearVal) {
        int curr_year = cal.get(Calendar.YEAR);
        int curr_month = cal.get(Calendar.MONTH);
        monthPicker.setDisplayedValues(null);
        switch (code) {
            case Constants.CODE_FOR_RSA_PICKER_DIALOG:
            case Constants.CODE_FOR_MFG_PICKER_DIALOG:
                if (curr_month == 0) {
                    curr_year--;
                    curr_month = 12;
                }
                if (yearVal == curr_year) {
                    String[] arr = new String[curr_month];
                    yearPicker.setValue(curr_year);
                    for (int i = 0; i < curr_month; i++) {
                        arr[i] = str[i];

                    }
                    monthPicker.setMaxValue(arr.length);
                    monthPicker.setDisplayedValues(arr);

                } else {
                    monthPicker.setMaxValue(str.length);
                    monthPicker.setDisplayedValues(str);
                    yearPicker.setMaxValue(curr_year);

                }
                break;
            case Constants.CODE_FOR_INSURANCE_PICKER_DIALOG:
                String[] arr = new String[(12 - cal.get(Calendar.MONTH))];
                if (yearVal != curr_year) {
                    yearPicker.setValue(yearVal);
                    monthPicker.setMinValue(1);
                    monthPicker.setMaxValue(str.length);
                    monthPicker.setDisplayedValues(str);
                } else {
                    yearPicker.setValue(cal.get(Calendar.YEAR));
                    //monthPicker.setDisplayedValues(null);
                    for (int i = (cal.get(Calendar.MONTH)); i < 12; i++) {
                        arr[i - cal.get(Calendar.MONTH)] = str[i];
                    }
                    monthPicker.setMinValue(cal.get(Calendar.MONTH) + 1);
                    monthPicker.setDisplayedValues(arr);
                    // monthPicker.setMaxValue(12 );
                    //monthPicker.setValue(cal.get(Calendar.MONTH));
                }
        }


    }


}
