package com.gcloud.gaadi.ui.customviews;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;

import com.gcloud.gaadi.ApplicationController;
import com.github.jjobes.slidedatetimepicker.SlideDateTimeListener;
import com.github.jjobes.slidedatetimepicker.SlideDateTimePicker;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by lakshaygirdhar on 5/10/15.
 */
public class CustomCalender extends EditText implements SlideDateTimeListener,View.OnClickListener {

    private Activity mActivity;
    private Date minDate;
    private Date maxDate;
    private Date initialDate;
    private String simpleDateFormate = "dd/MM/yyyy";
    AfterChnageOnDateSet afterChnageOnDateSet = null,
            afterDismissOnDateSet = null;
    public void setSimpleDateFormate(String simpleDateFormate) {
        this.simpleDateFormate = simpleDateFormate;
    }

    public void setMinDate(Date minDate) {
        this.minDate = minDate;
    }


    public void setMaxDate(Date maxDate) {
        this.maxDate = maxDate;
    }

    public void setInitialDate(Date initialDate) {
        this.initialDate = initialDate;
    }

    public CustomCalender(Context context) {
        super(context);

    }
   /* public CustomCalender(Context context, AfterChnageOnDateSet afterChnageOnDateSet) {
        super(context);
        this.afterChnageOnDateSet = afterChnageOnDateSet;
    }*/
    public void afterchnage(AfterChnageOnDateSet afterChnageOnDateSet)
    {
        this.afterChnageOnDateSet = afterChnageOnDateSet;
    }
    public void afterdismiss(AfterChnageOnDateSet afterChnageOnDateSet)
    {
        this.afterDismissOnDateSet = afterDismissOnDateSet;
    }


    public CustomCalender(Context context, AttributeSet attrs) {
        super(context, attrs);

        setOnClickListener(this);

    }



    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
    }

    public Activity getmActivity() {
        return mActivity;
    }

    public void setmActivity(Activity mActivity) {
        this.mActivity = mActivity;
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
    }
    public interface AfterChnageOnDateSet{

        public abstract void onAfterChange();
        public abstract void onAfterDismiss();

    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        super.setOnClickListener(l);


    }



    @Override
    public void onDateTimeSet(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(simpleDateFormate);
            setText(dateFormat.format(date));
        if (afterChnageOnDateSet != null) {
            afterChnageOnDateSet.onAfterChange();
        }
            clearFocus();

    }

    @Override
    public void onDateTimeCancel() {

        if (afterDismissOnDateSet != null) {
            afterDismissOnDateSet.onAfterDismiss();
        }
        clearFocus();
    }
    public void onDailogs()
    {
        new SlideDateTimePicker.Builder(((AppCompatActivity) mActivity).getSupportFragmentManager())
                .setShowTimeTab(false)
                .setMinDate(minDate)
                .setMaxDate(maxDate)
                .setInitialDate(initialDate)
                .setListener(CustomCalender.this)
                .build().show();
    }

    @Override
    public void onClick(View v) {
        new SlideDateTimePicker.Builder(((AppCompatActivity) mActivity).getSupportFragmentManager())
                .setShowTimeTab(false)
                .setMinDate(minDate)
                .setMaxDate(maxDate)
                .setInitialDate(initialDate)
                .setListener(CustomCalender.this)
                .build().show();
    }
}
