package com.gcloud.gaadi.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.gcloud.gaadi.R;
import com.gcloud.gaadi.utils.CommonUtils;

/**
 * Created by Gaurav on 04-05-2015.
 */
public class ListPopupWindowAdapter extends ArrayAdapter<Integer> {

    private Context context;
    private Integer[] data;
    private boolean isMonth;

    public ListPopupWindowAdapter(Context context, Integer[] objects, boolean isMonth) {
        super(context, 0);
        this.context = context;
        this.data = objects;
        this.isMonth = isMonth;
    }

    @Override
    public int getCount() {
        if (data != null)
            return data.length;
        else
            return 0;
    }

    @Override
    public Integer getItem(int position) {
        return data[position];
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(android.R.layout.simple_dropdown_item_1line, parent, false);
            convertView.setBackgroundColor(context.getResources().getColor(R.color.white));
        }
        if (!isMonth) {
            //  GCLog.e("yearList", data[position]+"");
            ((TextView) convertView.findViewById(android.R.id.text1)).setText(data[position].toString());
        } else
            ((TextView) convertView.findViewById(android.R.id.text1)).setText(CommonUtils.getMonthShortForm(data[position]));
        ((TextView) convertView.findViewById(android.R.id.text1)).setTextAppearance(context, R.style.textStyleHeading2);
        return convertView;
    }
}
