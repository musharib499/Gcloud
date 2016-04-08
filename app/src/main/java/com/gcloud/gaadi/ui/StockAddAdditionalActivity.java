package com.gcloud.gaadi.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.gcloud.gaadi.ApplicationController;
import com.gcloud.gaadi.R;
import com.gcloud.gaadi.constants.ConditionType;
import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.events.ConditionSelectedEvent;
import com.gcloud.gaadi.events.ConditionSingleSelectEvent;
import com.gcloud.gaadi.model.BasicListItemModel;
import com.gcloud.gaadi.utils.CommonUtils;
import com.gcloud.gaadi.utils.GAHelper;
import com.gcloud.gaadi.utils.GCLog;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by ankit on 21/11/14.
 */
public class StockAddAdditionalActivity extends ActionBarActivity
        implements OnClickListener {
    private TextView overallCondition;
    private TextView exteriors;
    private TextView engineTransmissionClutch;
    private TextView bodyFrame;
    private TextView interiors;
    private TextView suspensionSteering;
    private TextView electronics;
    private TextView tyres;
    private TextView brakes;
    private TextView battery;
    private TextView acHeater;

    private ActionBar mActionBar;
    private ArrayList<BasicListItemModel> overallConditionList,
            exteriorList, interiorList, tyresList, brakesList,
            etcList, ssList, bodyFrameList, electricalList, acheaterList, batteryList;

    private String selectedExteriors = "", selectedInteriors = "", selectedETC = "",
            selectedBrakes = "", selectedACHeater = "", selectedBodyFrames = "",
            selectedTyres = "", selectedSuspension = "";

    private int selectedOverallIndex = -1, selectedAcHeaterIndex = -1, selectedElectricalsIndex = -1,
            selectedBatteryIndex = -1;


    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        finishActivity(Constants.STOCK_ADD_ADDITIONAL);
        super.onBackPressed();
    }

    @Override
    protected void onStart() {
        super.onStart();
        ApplicationController.getInstance().getGAHelper().sendScreenName(GAHelper.TrackerName.APP_TRACKER, Constants.CATEGORY_ADD_ADDITIONAL_STOCK);

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_stock_additional);
        mActionBar = getSupportActionBar();



        /*mActionBar.setDisplayHomeAsUpEnabled(true);*/
        formLists();
        initializeView();


    }

    private void formLists() {
        overallConditionList = new ArrayList<>(ApplicationController.overallConditionMap.size());
        for (Map.Entry<String, String> entry : ApplicationController.overallConditionMap.entrySet()) {
            BasicListItemModel item = new BasicListItemModel(entry.getKey(), entry.getValue());
            overallConditionList.add(item);
        }

        exteriorList = new ArrayList<>(ApplicationController.exteriorMap.size());
        for (Map.Entry<String, String> entry : ApplicationController.exteriorMap.entrySet()) {
            BasicListItemModel item = new BasicListItemModel(entry.getKey(), entry.getValue());
            exteriorList.add(item);
        }

        interiorList = new ArrayList<>(ApplicationController.interiorMap.size());
        for (Map.Entry<String, String> entry : ApplicationController.interiorMap.entrySet()) {
            BasicListItemModel item = new BasicListItemModel(entry.getKey(), entry.getValue());
            interiorList.add(item);
        }

        tyresList = new ArrayList<>(ApplicationController.tyresMap.size());
        for (Map.Entry<String, String> entry : ApplicationController.tyresMap.entrySet()) {
            BasicListItemModel item = new BasicListItemModel(entry.getKey(), entry.getValue());
            tyresList.add(item);
        }

        brakesList = new ArrayList<>(ApplicationController.brakesMap.size());
        for (Map.Entry<String, String> entry : ApplicationController.brakesMap.entrySet()) {
            BasicListItemModel item = new BasicListItemModel(entry.getKey(), entry.getValue());
            brakesList.add(item);
        }

        etcList = new ArrayList<>(ApplicationController.etcMap.size());
        for (Map.Entry<String, String> entry : ApplicationController.etcMap.entrySet()) {
            BasicListItemModel item = new BasicListItemModel(entry.getKey(), entry.getValue());
            etcList.add(item);
        }

        ssList = new ArrayList<>(ApplicationController.ssMap.size());
        for (Map.Entry<String, String> entry : ApplicationController.ssMap.entrySet()) {
            BasicListItemModel item = new BasicListItemModel(entry.getKey(), entry.getValue());
            ssList.add(item);
        }

        bodyFrameList = new ArrayList<>(ApplicationController.bodyFrameMap.size());
        for (Map.Entry<String, String> entry : ApplicationController.ssMap.entrySet()) {
            BasicListItemModel item = new BasicListItemModel(entry.getKey(), entry.getValue());
            bodyFrameList.add(item);
        }

        electricalList = new ArrayList<>(ApplicationController.electricalsMap.size());
        for (Map.Entry<String, String> entry : ApplicationController.electricalsMap.entrySet()) {
            BasicListItemModel item = new BasicListItemModel(entry.getKey(), entry.getValue());
            electricalList.add(item);
        }

        acheaterList = new ArrayList<>(ApplicationController.acheaterMap.size());
        for (Map.Entry<String, String> entry : ApplicationController.acheaterMap.entrySet()) {
            BasicListItemModel item = new BasicListItemModel(entry.getKey(), entry.getValue());
            acheaterList.add(item);
        }

        batteryList = new ArrayList<>(ApplicationController.batteryMap.size());
        for (Map.Entry<String, String> entry : ApplicationController.batteryMap.entrySet()) {
            BasicListItemModel item = new BasicListItemModel(entry.getKey(), entry.getValue());
            batteryList.add(item);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initializeView() {
        overallCondition = (TextView) findViewById(R.id.overallCondition);
        overallCondition.setOnClickListener(this);

        exteriors = (TextView) findViewById(R.id.exterior);
        exteriors.setOnClickListener(this);

        engineTransmissionClutch = (TextView) findViewById(R.id.engineTransmission);
        engineTransmissionClutch.setOnClickListener(this);

        suspensionSteering = (TextView) findViewById(R.id.suspensionSteering);
        suspensionSteering.setOnClickListener(this);

        tyres = (TextView) findViewById(R.id.tyres);
        tyres.setOnClickListener(this);

        brakes = (TextView) findViewById(R.id.brakes);
        brakes.setOnClickListener(this);

        bodyFrame = (TextView) findViewById(R.id.bodyFrame);
        bodyFrame.setOnClickListener(this);

        interiors = (TextView) findViewById(R.id.interior);
        interiors.setOnClickListener(this);

        electronics = (TextView) findViewById(R.id.electronicsElectricals);
        electronics.setOnClickListener(this);

        battery = (TextView) findViewById(R.id.battery);
        battery.setOnClickListener(this);

        acHeater = (TextView) findViewById(R.id.acHeater);
        acHeater.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        MultiSelectConditionReportDialogFragment dialogFragment;
        SingleSelectConditionReportDialogFragment singleSelectDialogFragment;
        Bundle bundle = new Bundle();

        switch (v.getId()) {
            case R.id.overallCondition:
                singleSelectDialogFragment = new SingleSelectConditionReportDialogFragment();
                bundle.putSerializable(Constants.LIST_ITEMS, overallConditionList);
                bundle.putInt(Constants.DIALOG_TITLE, R.string.select_overall_condition);
                bundle.putSerializable(Constants.CONDITION_TYPE, ConditionType.OVERALL);
                bundle.putInt(Constants.SELECTED_CONDITION_TYPE_INDEX, selectedOverallIndex);
                singleSelectDialogFragment.setArguments(bundle);
                singleSelectDialogFragment.show(getSupportFragmentManager(), "single-select-dialog");
                break;

            case R.id.battery:
                if (selectedOverallIndex != -1) {
                    singleSelectDialogFragment = new SingleSelectConditionReportDialogFragment();
                    bundle.putSerializable(Constants.LIST_ITEMS, batteryList);
                    bundle.putInt(Constants.DIALOG_TITLE, R.string.select_battery);
                    bundle.putSerializable(Constants.CONDITION_TYPE, ConditionType.BATTERY);
                    bundle.putInt(Constants.SELECTED_CONDITION_TYPE_INDEX, selectedBatteryIndex);
                    singleSelectDialogFragment.setArguments(bundle);
                    singleSelectDialogFragment.show(getSupportFragmentManager(), "single-select-dialog");
                } else {
                    CommonUtils.showToast(this, getString(R.string.error_select_overall_condition), Toast.LENGTH_SHORT);
                }
                break;

            case R.id.electronicsElectricals:
                if (selectedOverallIndex != -1) {
                    singleSelectDialogFragment = new SingleSelectConditionReportDialogFragment();
                    bundle.putSerializable(Constants.LIST_ITEMS, electricalList);
                    bundle.putInt(Constants.DIALOG_TITLE, R.string.select_electricals_electronics);
                    bundle.putSerializable(Constants.CONDITION_TYPE, ConditionType.ELECTRICALS);
                    bundle.putInt(Constants.SELECTED_CONDITION_TYPE_INDEX, selectedElectricalsIndex);
                    singleSelectDialogFragment.setArguments(bundle);
                    singleSelectDialogFragment.show(getSupportFragmentManager(), "single-select-dialog");
                } else {
                    CommonUtils.showToast(this, getString(R.string.error_select_overall_condition), Toast.LENGTH_SHORT);
                }
                break;

            case R.id.acHeater:
                if (selectedOverallIndex != -1) {
                    singleSelectDialogFragment = new SingleSelectConditionReportDialogFragment();
                    bundle.putSerializable(Constants.LIST_ITEMS, acheaterList);
                    bundle.putInt(Constants.DIALOG_TITLE, R.string.select_acheater);
                    bundle.putSerializable(Constants.CONDITION_TYPE, ConditionType.AC_HEATER);
                    bundle.putInt(Constants.SELECTED_CONDITION_TYPE_INDEX, selectedAcHeaterIndex);
                    singleSelectDialogFragment.setArguments(bundle);
                    singleSelectDialogFragment.show(getSupportFragmentManager(), "single-select-dialog");
                } else {
                    CommonUtils.showToast(this, getString(R.string.error_select_overall_condition), Toast.LENGTH_SHORT);
                }
                break;

            case R.id.exterior:
                if (selectedOverallIndex != -1) {
                    dialogFragment = new MultiSelectConditionReportDialogFragment();
                    bundle.putString(Constants.SELECTED_IDS, selectedExteriors);
                    bundle.putSerializable(Constants.LIST_ITEMS, exteriorList);
                    bundle.putInt(Constants.DIALOG_TITLE, R.string.select_exteriors);
                    bundle.putSerializable(Constants.CONDITION_TYPE, ConditionType.EXTERIOR);
                    dialogFragment.setArguments(bundle);
                    dialogFragment.show(getSupportFragmentManager(), "condition-type-dialog");
                } else {
                    CommonUtils.showToast(this, getString(R.string.error_select_overall_condition), Toast.LENGTH_SHORT);
                }
                break;

            case R.id.engineTransmission:
                if (selectedOverallIndex != -1) {
                    dialogFragment = new MultiSelectConditionReportDialogFragment();
                    bundle.putSerializable(Constants.LIST_ITEMS, etcList);
                    bundle.putInt(Constants.DIALOG_TITLE, R.string.select_engine_transmission_clutch);
                    bundle.putString(Constants.SELECTED_IDS, selectedETC);
                    bundle.putSerializable(Constants.CONDITION_TYPE, ConditionType.ETC);
                    dialogFragment.setArguments(bundle);
                    dialogFragment.show(getSupportFragmentManager(), "condition-type-dialog");
                } else {
                    CommonUtils.showToast(this, getString(R.string.error_select_overall_condition), Toast.LENGTH_SHORT);
                }
                break;

            case R.id.interior:
                if (selectedOverallIndex != -1) {
                    dialogFragment = new MultiSelectConditionReportDialogFragment();
                    bundle.putSerializable(Constants.LIST_ITEMS, interiorList);
                    bundle.putInt(Constants.DIALOG_TITLE, R.string.select_interiors);
                    bundle.putString(Constants.SELECTED_IDS, selectedInteriors);
                    bundle.putSerializable(Constants.CONDITION_TYPE, ConditionType.INTERIOR);
                    dialogFragment.setArguments(bundle);
                    dialogFragment.show(getSupportFragmentManager(), "condition-type-dialog");
                } else {
                    CommonUtils.showToast(this, getString(R.string.error_select_overall_condition), Toast.LENGTH_SHORT);
                }
                break;

            case R.id.bodyFrame:
                if (selectedOverallIndex != -1) {
                    dialogFragment = new MultiSelectConditionReportDialogFragment();
                    bundle.putSerializable(Constants.LIST_ITEMS, bodyFrameList);
                    bundle.putInt(Constants.DIALOG_TITLE, R.string.select_body_frame);
                    bundle.putString(Constants.SELECTED_IDS, selectedBodyFrames);
                    bundle.putSerializable(Constants.CONDITION_TYPE, ConditionType.BODY_FRAME);
                    dialogFragment.setArguments(bundle);
                    dialogFragment.show(getSupportFragmentManager(), "condition-type-dialog");
                } else {
                    CommonUtils.showToast(this, getString(R.string.error_select_overall_condition), Toast.LENGTH_SHORT);
                }
                break;

            case R.id.brakes:
                if (selectedOverallIndex != -1) {
                    dialogFragment = new MultiSelectConditionReportDialogFragment();
                    bundle.putSerializable(Constants.LIST_ITEMS, brakesList);
                    bundle.putInt(Constants.DIALOG_TITLE, R.string.select_brakes);
                    bundle.putString(Constants.SELECTED_IDS, selectedBrakes);
                    bundle.putSerializable(Constants.CONDITION_TYPE, ConditionType.BRAKE);
                    dialogFragment.setArguments(bundle);
                    dialogFragment.show(getSupportFragmentManager(), "condition-type-dialog");
                } else {
                    CommonUtils.showToast(this, getString(R.string.error_select_overall_condition), Toast.LENGTH_SHORT);
                }
                break;

            case R.id.suspensionSteering:
                if (selectedOverallIndex != -1) {
                    dialogFragment = new MultiSelectConditionReportDialogFragment();
                    bundle.putSerializable(Constants.LIST_ITEMS, ssList);
                    bundle.putInt(Constants.DIALOG_TITLE, R.string.select_suspension_steering);
                    bundle.putString(Constants.SELECTED_IDS, selectedSuspension);
                    bundle.putSerializable(Constants.CONDITION_TYPE, ConditionType.SUSPENSION);
                    dialogFragment.setArguments(bundle);
                    dialogFragment.show(getSupportFragmentManager(), "condition-type-dialog");
                } else {
                    CommonUtils.showToast(this, getString(R.string.error_select_overall_condition), Toast.LENGTH_SHORT);
                }
                break;

            case R.id.tyres:
                if (selectedOverallIndex != -1) {
                    dialogFragment = new MultiSelectConditionReportDialogFragment();
                    bundle.putSerializable(Constants.LIST_ITEMS, tyresList);
                    bundle.putInt(Constants.DIALOG_TITLE, R.string.select_tyres);
                    bundle.putString(Constants.SELECTED_IDS, selectedTyres);
                    bundle.putSerializable(Constants.CONDITION_TYPE, ConditionType.TYRE);
                    dialogFragment.setArguments(bundle);
                    dialogFragment.show(getSupportFragmentManager(), "condition-type-dialog");
                } else {
                    CommonUtils.showToast(this, getString(R.string.error_select_overall_condition), Toast.LENGTH_SHORT);
                }
                break;


            default:
                break;
        }
    }

    @Subscribe
    public void onConditionTypeEvent(ConditionSelectedEvent event) {
        GCLog.e(event.toString());
        switch (event.getConditionType()) {
            case EXTERIOR:
                exteriors.setText(getString(R.string.select_exteriors) + " (" + event.getCount() + ")");
                selectedExteriors = event.getIds();
                break;

            case INTERIOR:
                interiors.setText(getString(R.string.select_interiors) + " (" + event.getCount() + ")");
                selectedInteriors = event.getIds();
                break;

            case BRAKE:
                brakes.setText(getString(R.string.select_brakes) + " (" + event.getCount() + ")");
                selectedBrakes = event.getIds();
                break;

            case TYRE:
                tyres.setText(getString(R.string.select_tyres) + " (" + event.getCount() + ")");
                selectedTyres = event.getIds();
                break;

            case SUSPENSION:
                suspensionSteering.setText(getString(R.string.select_suspension_steering) + " (" + event.getCount() + ")");
                selectedSuspension = event.getIds();
                break;

            case BODY_FRAME:
                bodyFrame.setText(getString(R.string.select_body_frame) + " (" + event.getCount() + ")");
                selectedBodyFrames = event.getIds();
                break;

            case ETC:
                engineTransmissionClutch.setText(getString(R.string.select_engine_transmission_clutch) + " (" + event.getCount() + ")");
                selectedETC = event.getIds();
                break;


        }
    }

    @Subscribe
    public void onConditionTypeEvent(ConditionSingleSelectEvent event) {
        GCLog.e(event.toString());
        switch (event.getConditionType()) {
            case OVERALL:

                overallCondition.setText(event.getItem().getValue());
                selectedOverallIndex = Integer.parseInt(event.getId());
                String condition = event.getItem().getId();
                if ("1".equals(condition)) {
                    presetETC("1,2,3");
                    presetInteriors("1");
                    presetElectricals(0);
                    presetBrakes("1");
                    presetBodyFrame("1");
                    presetBattery(0);
                    presetExteriors("1,2");
                    presetSuspension("1");
                    presetACHeater(0);
                    presetTyres("1");

                } else if ("2".equals(condition)) {
                    presetETC("3,4,5");
                    presetInteriors("2,3");
                    presetElectricals(1);
                    presetBrakes("2");
                    presetBodyFrame("2,5");
                    presetBattery(1);
                    presetExteriors("3");
                    presetSuspension("1");
                    presetACHeater(0);
                    presetTyres("2");

                } else if ("3".equals(condition)) {
                    presetETC("4,7");
                    presetInteriors("2,3");
                    presetElectricals(2);
                    presetBrakes("3");
                    presetBodyFrame("3,6");
                    presetBattery(2);
                    presetExteriors("4,7");
                    presetSuspension("2,4");
                    presetACHeater(1);
                    presetTyres("4");

                } else if ("4".equals(condition)) {
                    presetETC("6,8,9");
                    presetInteriors("4,5,6,7");
                    presetElectricals(4);
                    presetBrakes("4");
                    presetBodyFrame("4,7");
                    presetBattery(3);
                    presetExteriors("5");
                    presetSuspension("3,5");
                    presetACHeater(2);
                    presetTyres("5");

                }

                findViewById(R.id.otherConditionLayout).setVisibility(View.VISIBLE);
                break;

            case BATTERY:
                battery.setText(event.getItem().getValue());
                selectedBatteryIndex = Integer.parseInt(event.getId());
                break;

            case ELECTRICALS:
                electronics.setText(event.getItem().getValue());
                selectedElectricalsIndex = Integer.parseInt(event.getId());
                break;

            case AC_HEATER:
                acHeater.setText(event.getItem().getValue());
                selectedAcHeaterIndex = Integer.parseInt(event.getId());
                break;
        }
    }

    private void presetTyres(String values) {
        int count = values.split(",").length;
        selectedTyres = values;
        for (BasicListItemModel item : tyresList) {
            item.setChecked(false);
        }
        tyres.setText(getString(R.string.select_tyres) + " (" + count + ")");
    }

    private void presetACHeater(int index) {
        selectedAcHeaterIndex = index;
        acHeater.setText(acheaterList.get(index).getValue());
    }

    private void presetSuspension(String values) {
        int count = values.split(",").length;
        selectedSuspension = values;
        for (BasicListItemModel item : ssList) {
            item.setChecked(false);
        }
        suspensionSteering.setText(getString(R.string.select_suspension_steering) + " (" + count + ")");
    }

    private void presetExteriors(String values) {
        int count = values.split(",").length;
        for (BasicListItemModel item : exteriorList) {
            item.setChecked(false);
        }
        selectedExteriors = values;
        exteriors.setText(getString(R.string.select_exteriors) + " (" + count + ")");
    }

    private void presetBattery(int index) {
        selectedBatteryIndex = index;
        battery.setText(batteryList.get(index).getValue());
    }

    private void presetBodyFrame(String values) {
        int count = values.split(",").length;
        for (BasicListItemModel item : bodyFrameList) {
            item.setChecked(false);
        }
        selectedBodyFrames = values;
        bodyFrame.setText(getString(R.string.select_body_frame) + " (" + count + ")");
    }

    private void presetBrakes(String values) {
        int count = values.split(",").length;
        for (BasicListItemModel item : brakesList) {
            item.setChecked(false);
        }
        selectedBrakes = values;
        brakes.setText(getString(R.string.select_brakes) + " (" + count + ")");
    }

    private void presetElectricals(int index) {
        selectedElectricalsIndex = index;
        electronics.setText(electricalList.get(index).getValue());
    }

    private void presetInteriors(String values) {
        int count = values.split(",").length;
        for (BasicListItemModel item : interiorList) {
            item.setChecked(false);
        }
        selectedInteriors = values;
        interiors.setText(getString(R.string.select_interiors) + " (" + count + ")");
    }

    private void presetETC(String values) {
        int count = values.split(",").length;
        for (BasicListItemModel item : etcList) {
            item.setChecked(false);
        }
        selectedETC = values;
        engineTransmissionClutch.setText(getString(R.string.select_engine_transmission_clutch) + " (" + count + ")");
    }

    @Override
    protected void onResume() {
        super.onResume();
        ApplicationController.getEventBus().register(this);

    }

    @Override
    protected void onPause() {
        super.onPause();
        ApplicationController.getEventBus().unregister(this);
    }
}
