package com.gcloud.gaadi.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.gcloud.gaadi.R;
import com.gcloud.gaadi.constants.ConditionType;
import com.gcloud.gaadi.model.BasicListItemModel;
import com.gcloud.gaadi.model.ListViewItemHolder;
import com.gcloud.gaadi.ui.CheckableRelativeLayout;
import com.gcloud.gaadi.utils.GCLog;

import java.util.ArrayList;

/**
 * Created by ankit on 14/1/15.
 */
public class ListItemAdapter extends ArrayAdapter<BasicListItemModel> implements View.OnClickListener {

    private Context mContext;
    private ArrayList<BasicListItemModel> mItems;
    private LayoutInflater mInflater;
    private ListViewItemHolder mHolder;
    private ConditionType mConditionType;

    public ListItemAdapter(Context context, ArrayList<BasicListItemModel> items, ConditionType conditionType) {
        super(context, 0);

        mContext = context;
        mItems = items;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mConditionType = conditionType;

    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public BasicListItemModel getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.multi_select_list_item, parent, false);
            mHolder = new ListViewItemHolder();
            mHolder.listItem = (CheckableRelativeLayout) convertView.findViewById(R.id.list_item);
            mHolder.text = (TextView) convertView.findViewById(R.id.text);
            /*mHolder.text.setTextAppearance(mContext, R.style.textStyleHeading2);*/
            mHolder.checkBox = (CheckBox) convertView.findViewById(R.id.checkbox);

            convertView.setTag(R.id.list_item, mHolder);

        } else {
            mHolder = (ListViewItemHolder) convertView.getTag(R.id.list_item);

        }

        mHolder.text.setText(mItems.get(position).getValue());
        mHolder.listItem.setTag(position);
        mHolder.listItem.setOnClickListener(this);

        if (mItems.get(position).isChecked()) {
            mHolder.checkBox.setChecked(true);
        } else {
            mHolder.checkBox.setChecked(false);
        }
        return convertView;
    }

    public String getSelectedItemsIds() {
        StringBuilder selectedItemsIds = new StringBuilder();
        for (int i = 0; i < mItems.size(); ++i) {
            if (mItems.get(i).isChecked()) {
                selectedItemsIds.append(mItems.get(i).getId());
                selectedItemsIds.append(",");
            }
        }

        return selectedItemsIds.toString().isEmpty() ? "" : selectedItemsIds.toString().substring(0, selectedItemsIds.length() - 1);
    }

    public int getSelectedItemsCount() {
        int count = 0;
        for (int i = 0; i < mItems.size(); ++i) {
            if (mItems.get(i).isChecked()) {
                ++count;
            }
        }

        return count;
    }

    public String getSelectedItemsValues() {
        StringBuilder selectedItemsValues = new StringBuilder();
        for (int i = 0; i < mItems.size(); ++i) {
            if (mItems.get(i).isChecked()) {
                selectedItemsValues.append(mItems.get(i).getValue());
                selectedItemsValues.append(",");
            }
        }
        return selectedItemsValues.toString().isEmpty() ? "" : selectedItemsValues.toString().toString().substring(0, selectedItemsValues.length() - 1);
    }

    @Override
    public void onClick(View v) {
        ListViewItemHolder holder = (ListViewItemHolder) v.getTag(R.id.list_item);
        int position = (Integer) holder.listItem.getTag();
        switch (v.getId()) {
            case R.id.list_item:
                GCLog.e("label " + mItems.get(position).getId());
                if (!mItems.get(position).isChecked()) {
                    holder.checkBox.setChecked(true);
                    holder.listItem.setChecked(true);
                    mItems.get(position).setChecked(true);
                } else {
                    holder.checkBox.setChecked(false);
                    mItems.get(position).setChecked(false);
                    holder.listItem.setChecked(false);
                }
                break;
        }
    }
}
