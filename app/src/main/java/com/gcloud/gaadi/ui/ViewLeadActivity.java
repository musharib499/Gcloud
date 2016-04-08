package com.gcloud.gaadi.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.gcloud.gaadi.ApplicationController;
import com.gcloud.gaadi.R;
import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.constants.DateType;
import com.gcloud.gaadi.model.FollowupDate;
import com.gcloud.gaadi.ui.fourmob.datetimepicker.date.DatePickerDialog;
import com.gcloud.gaadi.ui.fourmob.datetimepicker.date.DatePickerDialog.OnDateSetListener;
import com.gcloud.gaadi.utils.GAHelper;

import java.util.Calendar;

public class ViewLeadActivity extends ActionBarActivity implements OnClickListener, OnDateSetListener {
    public static final String DATEPICKER_TAG = "datepicker";
    TextView mDate;
    ImageView etfollowupdate;
    private DatePickerDialog datePickerDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_lead);
        final Calendar calendar = Calendar.getInstance();

        datePickerDialog = DatePickerDialog.newInstance(this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), false);

        initializeViews();
        setOnClicks();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        Intent intent = null;

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void setOnClicks() {
        //etfollowupdate.setOnClickListener(this);
        mDate.setOnClickListener(this);
    }

    private void initializeViews() {

		/*etfollowupdate = (ImageView)findViewById(R.id.followupdate);*/
        mDate = (TextView) findViewById(R.id.date);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        ApplicationController.getInstance().getGAHelper().sendScreenName(GAHelper.TrackerName.APP_TRACKER, Constants.CATEGORY_VIEW_LEAD);

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        /*case R.id.followupdate:
             showDatePickerDialog();
		break;*/
            case R.id.date:
                showDatePickerDialog();
                break;

            default:
                break;
        }

    }

    private void showDatePickerDialog() {
        datePickerDialog.setYearRange(Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.YEAR) + 1);
        datePickerDialog.setCloseOnSingleTapDay(false);
        datePickerDialog.setOnDateSetListener(this);
        datePickerDialog.show(getSupportFragmentManager(), DATEPICKER_TAG);

    }

    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int year,
                          int month, int day) {

        //Toast.makeText(this, "Date set to " + day + ", " + month + ", " + year, Toast.LENGTH_SHORT).show();

        // TODO apply logic to check whether date chosen is greater than today's date.
        FollowupDate followupDate = new FollowupDate(year, month, day);
        mDate.setText("Date: " + day + "/" + followupDate.getMonthName() + "/" + year);


    }

    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day, DateType dateType) {

    }


}
