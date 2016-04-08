package com.gcloud.gaadi.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.gcloud.gaadi.ApplicationController;
import com.gcloud.gaadi.R;
import com.gcloud.gaadi.interfaces.ResetFragment;
import com.gcloud.gaadi.utils.CommonUtils;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by @author gauravkumar on 1/10/15.
 */
public class FilterFragment extends ListFragment implements ResetFragment {

    private ArrayList<String> list, data;
    private LinkedHashMap<String, String> valueMap;
    private boolean filterOptions, sendKey, withEditText = false;
    private View previousView;
    private ArrayList<Integer> selectedPositions;
    private String regNoTempData = "", hint = "By Reg No",
            filterParam = "reg no";
    private int inputType = InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS;
    private ResetFragment resetFragment = this;

    public FilterFragment() {
        this.selectedPositions = new ArrayList<>();
    }

    public static FilterFragment getInstance(boolean withEditText, String hint,
                                             String filterParam, int inputType) {
        FilterFragment fragment = FilterFragment.getInstance(withEditText, hint);
        fragment.filterParam = filterParam;
        fragment.inputType = inputType;
        return fragment;
    }

    public static FilterFragment getInstance(boolean withEditText, String hint) {
        FilterFragment fragment = new FilterFragment();
        fragment.filterOptions = false;
        fragment.withEditText = withEditText;
        fragment.hint = hint;
        return fragment;
    }

    public static FilterFragment getInstance(boolean filterOptions, ArrayList<String> list) {
        FilterFragment fragment = new FilterFragment();
        fragment.list = list;
        fragment.filterOptions = filterOptions;
        return fragment;
    }

    public static FilterFragment getInstance(boolean filterOptions,
                                             LinkedHashMap<String, String> valueMap,
                                             boolean sendKey) {
        FilterFragment fragment = new FilterFragment();
        fragment.valueMap = valueMap;
        fragment.filterOptions = filterOptions;
        fragment.sendKey = sendKey;
        return fragment;
    }

    public ResetFragment getResetFragment() {
        return resetFragment;
    }

    public ArrayList<Integer> getSelectedPositions() {
        return selectedPositions;
    }

    public void setSelectedPositions(ArrayList<Integer> selectedPositions) {
        this.selectedPositions = selectedPositions;
        ((FilterDataAdapter) getListAdapter()).notifyDataSetChanged();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Context context = view.getContext();
        if (!withEditText) {
            getListView().setDivider(new ColorDrawable(ContextCompat.getColor(context,
                    filterOptions ? R.color.white : R.color.insurance_tuple_divider)));
            getListView().setDividerHeight(2);
        } else {
            getListView().setDividerHeight(0);
        }
        getListView().setBackgroundColor(ContextCompat.getColor(context,
                !filterOptions ? R.color.white : R.color.insurance_tuple_divider));
        if (withEditText) {
            data = new ArrayList<>();
            data.add("Edittext");
        } else if (list == null) {
            data = new ArrayList<>();
            if (valueMap != null) {
                for (Map.Entry entry : valueMap.entrySet()) {
                    data.add((String) entry.getKey());
                }
            }
        } else {
            data = list;
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.list_filter_option_row, R.id.data, data);
        setListAdapter(filterOptions ? adapter : new FilterDataAdapter(data));
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        Context context = l.getContext();
        CommonUtils.hideKeyboard(l.getContext(), v);
        if (withEditText) {
            getListView().getChildAt(0).findViewById(R.id.reg_no).requestFocus();
            return;
        }
        Intent intent = new Intent(filterOptions ? FilterActivity.FILTER_OPTION_CLICKED : "");
        intent.putExtra(FilterActivity.MAP_KEY, l.getAdapter().getItem(position).toString().toLowerCase());
        if (filterOptions) {
            if (previousView != null) {
                previousView.setBackgroundColor(ContextCompat.getColor(context, R.color.insurance_tuple_divider));
                ((TextView) previousView.findViewById(R.id.data)).setTextColor(ContextCompat.getColor(context, R.color.lms_dark_gray));
            }
            ((TextView) v.findViewById(R.id.data)).setTextColor(ContextCompat.getColor(context, R.color.orange));
            v.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
            previousView = v;
            getListView().smoothScrollToPosition(position);
        } else {
            ((TextView) v.findViewById(R.id.data)).setTextColor(ContextCompat.getColor(context,
                    selectedPositions.contains(position) ? android.R.color.black : R.color.orange));
            /*((TextView) v.findViewById(R.id.data_tick)).setTextColor(ContextCompat.getColor(context, 
                    selectedPositions.contains(position) ? android.R.color.white : R.color.orange));*/
            v.findViewById(R.id.data_tick)
                    .setVisibility(selectedPositions.contains(position) ? View.GONE : View.VISIBLE);
            updateSelectedState(position);
            intent.putExtra(FilterActivity.DATA_KEY, list == null
                    ? (sendKey ? valueMap.get(data.get(position)) : data.get(position))
                    : data.get(position));
        }
        ApplicationController.getEventBus().post(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        ApplicationController.getEventBus().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ApplicationController.getEventBus().unregister(this);
    }

    @Subscribe
    public void resetCommandReceived(Intent intent) {
        if (filterOptions) {
            Bundle extras = intent.getExtras();
            int position;
            switch (intent.getAction()) {
                case FilterActivity.CHANGE_LIST_ITEM:
                    //GCLog.e("change item");
                    position = extras.getInt("itemPosition");
                    if (getListView().performItemClick(getListView().getChildAt(position),
                            position, getListAdapter().getItemId(position))) {
                    }
                    break;

                case FilterActivity.UPDATE_COUNT:
                    position = extras.getInt("itemPosition");
                    int count = extras.getInt("count");
                    TextView dataCount = (TextView) getListView().getChildAt(position).findViewById(R.id.data_count);
                    if (count > 0) {
                        if (dataCount.getVisibility() != View.VISIBLE) {
                            dataCount.setVisibility(View.VISIBLE);
                        }
                        dataCount.setText(String.valueOf(count));
                        selectedPositions.add(position);
                    } else {
                        if (dataCount.getVisibility() == View.VISIBLE) {
                            dataCount.setVisibility(View.GONE);
                        }
                        selectedPositions.remove((Integer) position);
                    }
                    StocksActivity.filterOptionCount.put(position, count);
                    break;

                case FilterActivity.RESET_COMMAND:
                    for (Integer entry : selectedPositions) {
                        getListView().getChildAt(entry).findViewById(R.id.data_count).setVisibility(View.GONE);
                    }
                    selectedPositions.clear();
                    StocksActivity.filterOptionCount.clear();
                    break;

                case FilterActivity.REFRESH_MODEL_COUNT:
                    int modelIndex = FilterActivity.list.indexOf("Model");
                    getListView().getChildAt(modelIndex)
                            .findViewById(R.id.data_count).setVisibility(View.GONE);
                    StocksActivity.filterOptionCount.put(modelIndex, 0);
                    break;
            }
            return;
        }
        switch (intent.getAction()) {
            case FilterActivity.RESET_COMMAND:
                selectedPositions.clear();
                if (withEditText) {
                    regNoTempData = "";
                }
                if (getListAdapter() != null) {
                    ((FilterDataAdapter) getListAdapter()).notifyDataSetChanged();
                }
                break;
        }

    }

    public void refreshList(ArrayList<String> makeIds) {
        //GCLog.e("refresh list");
        if (data == null) {
            data = new ArrayList<>();
        } else {
            data.clear();
        }
        if (withEditText) {
            data.add("Edittext");
        } else if (list == null) {
            //data = new ArrayList<>();
            for (Map.Entry entry : valueMap.entrySet()) {
                if (makeIds != null && makeIds.size() > 0) {
                    if (makeIds.contains(entry.getValue())) {
                        data.add((String) entry.getKey());
                    }
                } else {
                    data.add((String) entry.getKey());
                }
            }
        }
        if (getListAdapter() != null) {
            ((FilterDataAdapter) getListAdapter()).notifyDataSetChanged();
        }
    }

    /*@Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("selectedPositions", selectedPositions);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.containsKey("selectedPositions")) {
            this.selectedPositions = (ArrayList<Integer>) savedInstanceState.getSerializable("selectedPositions");
        }
    }*/

    public void clearSelection() {
        selectedPositions.clear();
    }

    public void updateView() {
        if (filterOptions) {
            for (Map.Entry entry : StocksActivity.filterOptionCount.entrySet()) {
                if ((int) entry.getValue() > 0) {
                    TextView dataCount = (TextView) getListView().getChildAt((int) entry.getKey()).findViewById(R.id.data_count);
                    dataCount.setVisibility(View.VISIBLE);
                    dataCount.setText(String.valueOf((int) entry.getValue()));
                    selectedPositions.add((int) entry.getKey());
                }
            }
        } else {
            ((FilterDataAdapter) getListAdapter()).notifyDataSetChanged();
        }
    }

    public void clearSelection(String dataKey) {
        selectedPositions.clear();
        selectedPositions.add(Integer.valueOf(dataKey) - 1);
        updateView();
    }

    @Override
    public void updateOnReset() {
        selectedPositions.clear();
        if (withEditText) {
            regNoTempData = "";
        }
        if (getListAdapter() != null) {
            ((FilterDataAdapter) getListAdapter()).notifyDataSetChanged();
        }
    }

    public void updateSelectedState(int position) {
        if (!selectedPositions.contains(position)) {
            selectedPositions.add(position);
        } else {
            selectedPositions.remove((Integer) position);
        }
    }

    private class FilterDataAdapter extends BaseAdapter {

        private ArrayList<String> data;

        public FilterDataAdapter(ArrayList<String> data) {
            this.data = data;
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(
                        withEditText ? R.layout.list_regno_filter_row : R.layout.list_filter_row,
                        parent, false);
            }
            if (!withEditText) {
                TextView textView = (TextView) convertView.findViewById(R.id.data);
                textView.setText(data.get(position));
                textView.setTextColor(ContextCompat.getColor(parent.getContext(),
                        selectedPositions.contains(position) ? R.color.orange : R.color.black));
                /*((TextView) convertView.findViewById(R.id.data_tick))
                        .setTextColor(ContextCompat.getColor(context, selectedPositions.contains(position) ? R.color.orange : android.R.color.white));*/
                convertView.findViewById(R.id.data_tick)
                        .setVisibility(selectedPositions.contains(position) ? View.VISIBLE : View.GONE);
            } else {
                EditText regNoField = (EditText) convertView.findViewById(R.id.reg_no);
                regNoField.setHint(hint);
                regNoField.setInputType(inputType);
                if (StocksActivity.filterParams.get(filterParam) != null) {
                    String regNo = StocksActivity.filterParams.get(filterParam).get(0);
                    if (!regNo.isEmpty()) {
                        regNoField.setText(regNo);
                    }
                } else if (!regNoTempData.isEmpty()) {
                    regNoField.setText(regNoTempData);
                } else {
                    regNoField.setText("");
                }
                regNoField.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        regNoTempData = s.toString();
                    }
                });
            }
            return convertView;
        }
    }
}
