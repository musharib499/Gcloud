package com.gcloud.gaadi.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.gcloud.gaadi.ApplicationController;
import com.gcloud.gaadi.R;
import com.gcloud.gaadi.model.Comments;
import com.gcloud.gaadi.utils.CommonUtils;

import java.util.ArrayList;

/**
 * Created by Priya on 26-08-2015.
 */
public class SellerLeadsCommentsAdapter extends ArrayAdapter<Comments> {

    private ArrayList<Comments> mItems;
    private LayoutInflater mInflater;
    private Context mContext;

    private ViewHolder mViewHolder;

    public SellerLeadsCommentsAdapter(Context context, ArrayList<Comments> items) {
        super(context, 0);
        mContext = context;
        mItems = items;
        mInflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // ApplicationController.getEventBus().register(this);

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

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.seller_leads_comments_listitem, parent,
                    false);
            mViewHolder = new ViewHolder();
            mViewHolder.tv_comment = (TextView) convertView.findViewById(R.id.tv_comment);
            mViewHolder.tv_commentDate = (TextView) convertView.findViewById(R.id.tv_commentDate);
            mViewHolder.tv_commentTime = (TextView) convertView.findViewById(R.id.tv_commentTime);
            convertView.setTag(mViewHolder);

        } else {

            mViewHolder = (ViewHolder) convertView.getTag();

        }

        mViewHolder.tv_comment.setText(mItems.get(position).getCmnts());
        if(CommonUtils.isValidField(mItems.get(position).getCommentDate()))
        {
            String date[] = mItems.get(position).getCommentDate().trim().split(" ");
           // DateTime dateTime = new DateTime(CommonUtils.SQLTimeToMillis(mItems.get(position).getCommentDate()));
            mViewHolder.tv_commentDate.setText(date[0].split("-")[2]+ " " + ApplicationController.monthShortMap.get(date[0].split("-")[1])+"," + date[0].split("-")[0]);
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
            mViewHolder.tv_commentTime.setText(time);
            /*String str[] = mItems.get(position).getCommentDate().split(" ");
            mViewHolder.tv_commentDate.setText(str[0]);
            mViewHolder.tv_commentTime.setText(str[1]);*/
        }
      //  mViewHolder.tv_commentDate.setText(mItems.get(position).getCommentDate());

        return convertView;
    }

    class ViewHolder {
        TextView tv_comment, tv_commentDate, tv_commentTime;
    }
}
