package com.gcloud.gaadi.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.gcloud.gaadi.R;
import com.gcloud.gaadi.model.BasicListItemModel;
import com.gcloud.gaadi.model.BasicListViewItemHolder;

import java.util.ArrayList;

/**
 * Created by ankit on 5/1/15.
 */
public class KmDrivenAdapter extends ArrayAdapter<BasicListItemModel> {

    private Context mContext;
    private ArrayList<BasicListItemModel> mItems;
    private LayoutInflater mInflater;
    private BasicListViewItemHolder mHolder;

    public KmDrivenAdapter(Context context, ArrayList<BasicListItemModel> items) {
        super(context, 0);

        mContext = context;
        mItems = items;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
            convertView = mInflater.inflate(android.R.layout.simple_dropdown_item_1line, parent, false);
            mHolder = new BasicListViewItemHolder();
            mHolder.text = (TextView) convertView.findViewById(android.R.id.text1);
            mHolder.text.setTextAppearance(mContext, R.style.textStyleHeading2);

            convertView.setTag(mHolder);

        } else {
            mHolder = (BasicListViewItemHolder) convertView.getTag();

        }

        mHolder.text.setText(mItems.get(position).getValue());
        return convertView;
    }
}
