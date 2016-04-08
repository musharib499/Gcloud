package com.github.jjobes.slidedatetimepicker;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.NumberPicker;

import java.lang.reflect.Field;

/**
 * A subclass of {@link android.widget.DatePicker} that uses
 * reflection to allow for customization of the default blue
 * dividers.
 *
 * @author jjobes
 */
public class CustomDatePicker extends DatePicker {
    private static final String TAG = "CustomDatePicker";

    public CustomDatePicker(Context context, AttributeSet attrs) {
        super(context, attrs);

        Class<?> idClass = null;
        Class<?> numberPickerClass = null;
        Field selectionDividerField = null;
        Field dayField = null;
        Field monthField = null;
        Field yearField = null;
        NumberPicker dayNumberPicker = null;
        NumberPicker monthNumberPicker = null;
        NumberPicker yearNumberPicker = null;

        try {
            // Create an instance of the id class
            idClass = Class.forName("com.android.internal.R$id");

            // Get the fields that store the resource IDs for the month, day and year NumberPickers
            dayField = idClass.getField("day");
            monthField = idClass.getField("month");
            yearField = idClass.getField("year");

            // Use the resource IDs to get references to the month, day and year NumberPickers
            dayNumberPicker = (NumberPicker) findViewById(dayField.getInt(null));
            monthNumberPicker = (NumberPicker) findViewById(monthField.getInt(null));
            yearNumberPicker = (NumberPicker) findViewById(yearField.getInt(null));

            numberPickerClass = Class.forName("android.widget.NumberPicker");

            // Set the value of the mSelectionDivider field in the month, day and year NumberPickers
            // to refer to our custom drawables
            selectionDividerField = numberPickerClass.getDeclaredField("mSelectionDivider");
            selectionDividerField.setAccessible(true);
            selectionDividerField.set(dayNumberPicker, getResources().getDrawable(R.drawable.selection_divider));
            selectionDividerField.set(monthNumberPicker, getResources().getDrawable(R.drawable.selection_divider));
            selectionDividerField.set(yearNumberPicker, getResources().getDrawable(R.drawable.selection_divider));
        } catch (ClassNotFoundException e) {
            Log.e(TAG, "ClassNotFoundException in CustomDatePicker", e);
        } catch (NoSuchFieldException e) {
            Log.e(TAG, "NoSuchFieldException in CustomDatePicker", e);
        } catch (IllegalAccessException e) {
            Log.e(TAG, "IllegalAccessException in CustomDatePicker", e);
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "IllegalArgumentException in CustomDatePicker", e);
        }
    }
}
