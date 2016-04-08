package com.gcloud.gaadi.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.gcloud.gaadi.ApplicationController;
import com.gcloud.gaadi.R;
import com.gcloud.gaadi.model.CallLogViewHolder;
import com.gcloud.gaadi.model.Comments;
import com.gcloud.gaadi.utils.CommonUtils;

import java.util.ArrayList;

public class ViewCommentsAdapter extends ArrayAdapter<Comments> {

    private ArrayList<Comments> mItems;
    private LayoutInflater mInflater;
    private Context mContext;

    public ViewCommentsAdapter(Context context, ArrayList<Comments> items) {
        super(context, 0);
        mContext = context;
        mItems = items;
        mInflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        ApplicationController.getEventBus().register(this);

    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Comments getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        CallLogViewHolder mViewHolder;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.commentslistitem, parent, false);
            mViewHolder = new CallLogViewHolder();
            mViewHolder.name = (TextView) convertView.findViewById(R.id.name);
            mViewHolder.date = (TextView) convertView.findViewById(R.id.date);
            mViewHolder.time = (TextView) convertView.findViewById(R.id.time);
            convertView.setTag(mViewHolder);

        } else {
            mViewHolder = (CallLogViewHolder) convertView.getTag();
        }

        mViewHolder.name.setText(mItems.get(position).getCmnts());
        if (CommonUtils.isValidField(mItems.get(position).getDate())) {
            mViewHolder.date.setText(mItems.get(position).getDate().split(" ")[0]);
            String time = mItems.get(position).getDate().split(" ")[1];
            String hour = time.split(":")[0].trim();
            String min = time.split(":")[1].trim();
            StringBuilder timeStringBuilder = new StringBuilder();
            int hourOfDay = Integer.parseInt(hour);
            int minuteOfHour = Integer.parseInt(min);

            timeStringBuilder.append((hourOfDay > 12) ? (hourOfDay - 12)
                    : hourOfDay);
            timeStringBuilder.append(":");
            timeStringBuilder.append((minuteOfHour < 10) ? ("0" + minuteOfHour)
                    : minuteOfHour);
            timeStringBuilder.append(" ");
            timeStringBuilder.append(hourOfDay >= 12 ? "PM" : "AM");
            time = timeStringBuilder.toString();
            mViewHolder.time.setText(time);
        } else if (CommonUtils.isValidField(mItems.get(position).getCommentDate())) {
            mViewHolder.date.setText(mItems.get(position).getCommentDate().split(" ")[0]);
            String time = mItems.get(position).getCommentDate().split(" ")[1];
            String hour = time.split(":")[0].trim();
            String min = time.split(":")[1].trim();
            StringBuilder timeStringBuilder = new StringBuilder();
            int hourOfDay = Integer.parseInt(hour);
            int minuteOfHour = Integer.parseInt(min);

            timeStringBuilder.append((hourOfDay > 12) ? (hourOfDay - 12)
                    : hourOfDay);
            timeStringBuilder.append(":");
            timeStringBuilder.append((minuteOfHour < 10) ? ("0" + minuteOfHour)
                    : minuteOfHour);
            timeStringBuilder.append(" ");
            timeStringBuilder.append(hourOfDay >= 12 ? "PM" : "AM");
            time = timeStringBuilder.toString();
            mViewHolder.time.setText(time);
        }

        return convertView;
    }

}
