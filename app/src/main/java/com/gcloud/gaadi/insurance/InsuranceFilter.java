package com.gcloud.gaadi.insurance;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.gcloud.gaadi.R;
import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.ui.BaseActivity;
import com.gcloud.gaadi.utils.OverFlowMenu;

import java.text.DecimalFormat;

public class InsuranceFilter extends BaseActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    private TextView tv_max, tv_min;
    //  private RadioGroup dp_radioGroup;
    // private RadioButton dp_checkButton;
    private CheckBox dp_check;
    private LinearLayout sub_check_zero_dep;
    private int minValue, diffValue;
    private long slideValue = 0;
    private MySeekBar discreteSeekBar1;
    private DecimalFormat df;
    private int steps = 0, dp_zero, dp_amount;
    private boolean checkValue = false, progressChanged = false, checkChangeListenerCalled = false;
    //private   CheckBox checkButton1,checkButton2,checkButton3,checkButton4;
    private RadioGroup radioGroup;
    private int check_id_item = 0, previousCheckedId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_incurance_filter, frameLayout);
        TextView lay_cancel = (TextView) findViewById(R.id.lay_cancel);
        TextView lay_apply = (TextView) findViewById(R.id.lay_apply);
        lay_apply.setOnClickListener(this);
        lay_cancel.setOnClickListener(this);
        steps = DealerQuoteScreen.steps;

        radioGroup = (RadioGroup) findViewById(R.id.radioGroup_deductible_amount);
        SeekBarInitialize();
        invalidateOptionsMenu();
    }

    public void radioButtonClicked(View view) {
        if (!checkChangeListenerCalled && previousCheckedId == view.getId()) {
            radioGroup.clearCheck();
            previousCheckedId = -1;

            dp_amount = 0;
            check_id_item = 0;
        } else {
            checkChangeListenerCalled = false;
        }
    }

    public void SeekBarInitialize() {
        tv_max = (TextView) findViewById(R.id.tv_max_idv);
        tv_min = (TextView) findViewById(R.id.tv_min_idv);
        //   dp_radioGroup = (RadioGroup) findViewById(R.id.dp_radio_group);
        /*checkButton1 = (CheckBox) findViewById(R.id.check_dp1);
        checkButton2 = (CheckBox) findViewById(R.id.check_dp2);
        checkButton3 = (CheckBox) findViewById(R.id.check_dp3);
        checkButton4 = (CheckBox) findViewById(R.id.check_dp4);
        checkButton1.setOnCheckedChangeListener(this);
        checkButton2.setOnCheckedChangeListener(this);
        checkButton3.setOnCheckedChangeListener(this);
        checkButton4.setOnCheckedChangeListener(this);
        checkButton1.setText(Constants.RUPEES_SYMBOL + " " + customFormat(2500));
        checkButton2.setText(Constants.RUPEES_SYMBOL + " " + customFormat(5000));
        checkButton3.setText(Constants.RUPEES_SYMBOL + " " + customFormat(7500));
        checkButton4.setText(Constants.RUPEES_SYMBOL + " " + customFormat(15000));*/
        TextView tv_more = (TextView) findViewById(R.id.tv_more);
        TextView tv_zero_more = (TextView) findViewById(R.id.tv_zero_more);
        tv_more.setOnClickListener(this);

        tv_zero_more.setOnClickListener(this);

        dp_check = (CheckBox) findViewById(R.id.check_zero_dep);
        dp_check.setOnCheckedChangeListener(this);
        sub_check_zero_dep = (LinearLayout) findViewById(R.id.sub_check_zero_dep);
        sub_check_zero_dep.setVisibility(View.GONE);
        tv_max.setText(Constants.RUPEES_SYMBOL + " " + customFormat((double) DealerQuoteScreen.maxValue));
        tv_min.setText(Constants.RUPEES_SYMBOL + " " + customFormat((double) DealerQuoteScreen.minValue));
        discreteSeekBar1 = (MySeekBar) findViewById(R.id.seeb_bar);
        discreteSeekBar1.setMin(0);
        discreteSeekBar1.setMax(DealerQuoteScreen.idvSteps);
        slideValue = 0;
        discreteSeekBar1.showFloater();
        diffValue = (DealerQuoteScreen.maxValue - DealerQuoteScreen.minValue) / DealerQuoteScreen.idvSteps;
        if (DealerQuoteScreen.checkStaps) {
            if (!checkValue) {

                MySeekBar.firstTime = true;
                discreteSeekBar1.setProgress(steps, true);
                discreteSeekBar1.setPressed(true);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        MySeekBar.firstTime = false;
                    }
                }, 100);
            }
        }
        discreteSeekBar1.setOnProgressChangeListener(new MySeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(MySeekBar seekBar, int value, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(MySeekBar seekBar) {
                progressChanged = true;
            }

            @Override
            public void onStopTrackingTouch(MySeekBar seekBar) {

            }
        });
        discreteSeekBar1.setNumericTransformer(new MySeekBar.NumericTransformer() {
            @Override
            public int transform(int value) {
                int totalvalue;
                totalvalue = (value == DealerQuoteScreen.idvSteps) ? DealerQuoteScreen.maxValue : DealerQuoteScreen.minValue + value * diffValue;
                slideValue = totalvalue;
                steps = value;
                return totalvalue;
            }
        });
//        if (check_id_item != 0) {
//            checkButton rb = (checkButton) dp_radioGroup.findViewById(check_id_item);
//            dp_radioGroup.check(check_id_item);
//            rb.setTextColor(getResources().getColor(R.color.white));
//        }
        if (!checkValue) {
            dp_check.setChecked(DealerQuoteScreen.checkZeroDp);
            CheckChangecheckButton(DealerQuoteScreen.checkItemPosition);
        } else {
            dp_amount = 0;
            if (dp_check.isChecked()) {
                dp_check.setChecked(false);
            }
            CheckChangecheckButton(0);
        }

        if (DealerQuoteScreen.isZeroDep == 1) {
            ((CheckBox) findViewById(R.id.consumable)).setChecked(DealerQuoteScreen.isZeroDep_C == 1);
            ((CheckBox) findViewById(R.id.engine_protection)).setChecked(DealerQuoteScreen.isZeroDep_H == 1);
        }


    }

    public void dialogsInsuranceMessage(String st_title, String st_message) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //before

        //setting custom layout to dialog
        dialog.setContentView(R.layout.insurance_filter_dialogs);
        dialog.setTitle("Custom Dialog");

        //adding text dynamically
        TextView tv_title = (TextView) dialog.findViewById(R.id.tv_title);
        tv_title.setText(st_title);
        TextView tv_message = (TextView) dialog.findViewById(R.id.tv_message);
        tv_message.setText(st_message);


        //adding button click event
        LinearLayout dismissLayout = (LinearLayout) dialog.findViewById(R.id.lay_dismiss);
        dismissLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    public void CheckChangecheckButton(int checkId) {


        /*if (checkId==checkButton1.getId())
        {
            if (!checkButton1.isChecked()) {
                checkButton1.setChecked(true);
                dp_amount = 2500;
            }

        }else
        if (checkId==checkButton2.getId())
        {
            if (!checkButton2.isChecked()) {
                checkButton2.setChecked(true);
                dp_amount = 5000;
            }

        }else
        if (checkId==checkButton3.getId())
        {
            if (!checkButton3.isChecked()) {
                checkButton3.setChecked(true);
                dp_amount = 7500;
            }

        }else
        if (checkId==checkButton4.getId())
        {
            if (!checkButton4.isChecked()) {
                checkButton4.setChecked(true);
                dp_amount = 15000;
            }

        }else if (checkId==0)
        {
            checkButton2.setChecked(false);
            checkButton3.setChecked(false);
            checkButton4.setChecked(false);
            checkButton1.setChecked(false);
            dp_amount =0;
        }*/

    }

    private String customFormat(double value) {
        DecimalFormat myFormatter = new DecimalFormat("##,##,###");
        return myFormatter.format(value);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        OverFlowMenu.OverFlowMenuText(this, "Reset", 16, menu);

        OverFlowMenu.tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                steps = 0;
                discreteSeekBar1.setProgress(0);
                checkValue = true;
                //discreteSeekBar1.hideFloater();
                discreteSeekBar1.setPressed(false);

                radioGroup.clearCheck();
                dp_check.setChecked(false);
                SeekBarInitialize();
                slideValue = 0;
                check_id_item = 0;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
           /* case R.id.action_reset:

                slideValue = 0;
                steps = 0;
                discreteSeekBar1.setProgress(0);
                checkValue=true;
                //discreteSeekBar1.hideFloater();
                discreteSeekBar1.setPressed(false);

                radioGroup.clearCheck();
                SeekBarInitialize();
                check_id_item =0;
                break;*/
            case android.R.id.home:
                onClick(findViewById(R.id.lay_cancel));
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.lay_apply:
                DealerQuoteScreen.isZeroDep_C = ((CheckBox) findViewById(R.id.consumable)).isChecked() ? 1 : 0;
                DealerQuoteScreen.isZeroDep_H = ((CheckBox) findViewById(R.id.engine_protection)).isChecked() ? 1 : 0;

                if (progressChanged) {
                    if (steps == 0) {
                        if (checkValue) {
                            intent.putExtra("selectedIdv", "" + 0);
                            DealerQuoteScreen.steps = 0;
                            DealerQuoteScreen.checkStaps = false;
                        } else {
                            intent.putExtra("selectedIdv", "" + slideValue);
                            DealerQuoteScreen.steps = steps;
                            DealerQuoteScreen.checkStaps = true;
                        }
                    } else {
                        intent.putExtra("selectedIdv", "" + slideValue);
                        DealerQuoteScreen.steps = steps;
                        DealerQuoteScreen.checkStaps = true;
                    }
                    if (dp_check.isChecked()) {
                        DealerQuoteScreen.checkZeroDp = true;
                        DealerQuoteScreen.isZeroDep = 1;
                    } else {
                        DealerQuoteScreen.checkZeroDp = false;
                        DealerQuoteScreen.isZeroDep = 0;
                    }
                    DealerQuoteScreen.checkItemPosition = check_id_item;
                    DealerQuoteScreen.deductible_premiums = dp_amount;
                    setResult(RESULT_OK, intent);
                } else {
                    if (checkValue) {
                        intent.putExtra("selectedIdv", String.valueOf(slideValue));
                        if (dp_check.isChecked()) {
                            DealerQuoteScreen.checkZeroDp = true;
                            DealerQuoteScreen.isZeroDep = 1;
                        } else {
                            DealerQuoteScreen.checkZeroDp = false;
                            DealerQuoteScreen.isZeroDep = 0;
                        }

                        DealerQuoteScreen.deductible_premiums = dp_amount;
                        DealerQuoteScreen.checkItemPosition = check_id_item;
                        DealerQuoteScreen.steps = 0;
                        DealerQuoteScreen.checkStaps = false;
                        setResult(RESULT_OK, intent);
                    } else {
                        if (dp_check.isChecked() || dp_amount != 0 || !progressChanged) {

                            if (dp_check.isChecked()) {
                                DealerQuoteScreen.isZeroDep = 1;
                                DealerQuoteScreen.checkZeroDp = true;
                            } else {
                                DealerQuoteScreen.isZeroDep = 0;
                                DealerQuoteScreen.checkZeroDp = false;
                            }

                            DealerQuoteScreen.deductible_premiums = dp_amount;
                            DealerQuoteScreen.checkItemPosition = check_id_item;
                            intent.putExtra("selectedIdv", String.valueOf(DealerQuoteScreen.checkStaps ? slideValue : 0));
                            setResult(RESULT_OK, intent);
                        }
                    }
                }
            case R.id.lay_cancel:
                finish();
                break;
            case R.id.tv_more:
                dialogsInsuranceMessage(getResources().getString(R.string.voluntary_title), getResources().getString(R.string.voluntary_details));
                break;
            case R.id.tv_zero_more:
                dialogsInsuranceMessage(getResources().getString(R.string.zero_depth_title), getResources().getString(R.string.zero_depth));
                break;

        }

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        switch (buttonView.getId()) {
            case R.id.check_zero_dep:
                if (isChecked) {
                    sub_check_zero_dep.setVisibility(View.VISIBLE);
                } else {
                    sub_check_zero_dep.setVisibility(View.GONE);
                    ((CheckBox) findViewById(R.id.consumable)).setChecked(false);
                    ((CheckBox) findViewById(R.id.engine_protection)).setChecked(false);
                }
        }


            /*switch(buttonView.getId())
            {
                case R.id.check_dp1:
                    checkButton2.setChecked(false);
                    checkButton3.setChecked(false);
                    checkButton4.setChecked(false);
                  //  checkButton1.setChecked(true);
                    check_id_item =checkButton1.getId();
                    dp_amount = 2500;

                    if (!checkButton1.isChecked())
                    {
                        check_id_item = 0;
                        dp_amount = 0;
                    }



                    break;
                case R.id.check_dp2:

                 //   checkButton2.setChecked(true);
                    checkButton1.setChecked(false);
                    checkButton3.setChecked(false);
                    checkButton4.setChecked(false);
                    check_id_item =checkButton2.getId();
                    dp_amount = 5000;
                    if (!checkButton2.isChecked())
                    {

                        check_id_item = 0;
                        dp_amount = 0;
                    }




                    break;
                case R.id.check_dp3:
                  //  checkButton3.setChecked(true);
                    checkButton1.setChecked(false);
                    checkButton2.setChecked(false);
                    checkButton4.setChecked(false);
                    check_id_item =checkButton3.getId();
                    dp_amount = 7500;
                    if (!checkButton3.isChecked())
                    {
                      //  checkButton3.setChecked(false);
                        check_id_item = 0;
                        dp_amount = 0;
                    }




                    break;
                case R.id.check_dp4:

                   // checkButton4.setChecked(true);
                    checkButton1.setChecked(false);
                    checkButton2.setChecked(false);
                    checkButton3.setChecked(false);
                    check_id_item =checkButton4.getId();
                    dp_amount = 15000;
                    if (!checkButton4.isChecked())
                    {

                        check_id_item = 0;
                        dp_amount = 0;

                    }
                    break;

        }*/

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();

        if (DealerQuoteScreen.checkItemPosition != 0) {
            check_id_item = DealerQuoteScreen.checkItemPosition;
            ((RadioButton) radioGroup.getChildAt(check_id_item - 1)).setChecked(true);
            previousCheckedId = radioGroup.getCheckedRadioButtonId();

            dp_amount = Integer.parseInt((String) radioGroup.getChildAt(check_id_item - 1).getTag());
        }
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId > -1) {
                    checkChangeListenerCalled = true;
                    previousCheckedId = checkedId;
                    // Mod is used because checkId changes each time we open this Activity
                    // radiogroup's childcount is added in Ids
                    check_id_item = checkedId % radioGroup.getChildCount();
                    if (check_id_item == 0) {
                        check_id_item = 4;
                    }
                    dp_amount = Integer.parseInt((String) group.findViewById(checkedId).getTag());
                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        /*checkButton2.setChecked(false);
        checkButton3.setChecked(false);
        checkButton4.setChecked(false);
        checkButton1.setChecked(false);*/
        dp_check.setChecked(false);
    }
}
