package com.gcloud.gaadi.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gcloud.gaadi.R;
import com.gcloud.gaadi.model.BasicListItemModel;

import java.util.ArrayList;

/**
 * Created by Priya on 30-04-2015.
 */
public class ColorAdapter extends ArrayAdapter<BasicListItemModel> {
    private Context mContext;
    private ArrayList<BasicListItemModel> mItems;
    private LayoutInflater mInflater;
    private ColorItemsHolder mHolder;

    public ColorAdapter(Context context, ArrayList<BasicListItemModel> items) {
        super(context, 0);

        mContext = context;
        mItems = items;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        if (mItems != null)
            return mItems.size();
        else
            return 0;
    }

    @Override
    public BasicListItemModel getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.color_layout_row, parent, false);

            mHolder = new ColorItemsHolder();
            mHolder.text = (TextView) convertView.findViewById(R.id.colorValue);
            mHolder.text.setTextAppearance(mContext, R.style.textStyleHeading2);
            mHolder.iv_color = (ImageView) convertView.findViewById(R.id.color);

            convertView.setTag(mHolder);

        } else {
            mHolder = (ColorItemsHolder) convertView.getTag();

        }

        mHolder.text.setText(mItems.get(position).getValue());
        if (mItems.get(position).getId() != null && !mItems.get(position).getId().trim().equals("")) {
            if (!mItems.get(position).getId().trim().equalsIgnoreCase("Other")) {
                GradientDrawable bgShape = (GradientDrawable) mHolder.iv_color.getBackground();
                bgShape.setColor(Color.parseColor(mItems.get(position).getId()));
            } else {
                GradientDrawable bgShape = (GradientDrawable) mHolder.iv_color.getBackground();
                bgShape.setColor(Color.parseColor("#ffffff"));
            }


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

    private class ColorItemsHolder {
        TextView text;
        ImageView iv_color;
    }
}
