package com.gcloud.gaadi.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gcloud.gaadi.ApplicationController;
import com.gcloud.gaadi.R;
import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.model.CallLogViewHolder;
import com.gcloud.gaadi.model.CustomerModel;
import com.gcloud.gaadi.ui.LeadAddActivity;
import com.gcloud.gaadi.utils.GCLog;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Locale;

public class CallTrackAdapter extends ArrayAdapter<CustomerModel> {

    private ArrayList<CustomerModel> mItems;
    private LayoutInflater mInflater;
    private Context mContext;

    private CallLogViewHolder mViewHolder;

    public CallTrackAdapter(Context context, ArrayList<CustomerModel> items) {
        super(context, 0);
        mContext = context;
        mItems = items;
        GCLog.e("Customers list: " + mItems.toString());
        mInflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        ApplicationController.getEventBus().register(this);

    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public CustomerModel getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.call_log_pager_list_item,
                    parent, false);
            mViewHolder = new CallLogViewHolder();

            mViewHolder.name = (TextView) convertView.findViewById(R.id.name);
            mViewHolder.time = (TextView) convertView.findViewById(R.id.time);
            mViewHolder.header = (TextView) convertView.findViewById(R.id.header);
            mViewHolder.separator = (LinearLayout) convertView.findViewById(R.id.listSeparator);
            mViewHolder.addLead = (ImageView) convertView.findViewById(R.id.addLeadButton);
            mViewHolder.callType = (ImageView) convertView.findViewById(R.id.callType);

            convertView.setTag(mViewHolder);
        } else {

            mViewHolder = (CallLogViewHolder) convertView.getTag();

        }

        final String name = mItems.get(position).getName();
        final String number = mItems.get(position).getMobile();
        final String correctNumber = number.length() > 10 ? number.substring(number.length() - 10) : number;
        mViewHolder.name.setText((name != null && !name.isEmpty()) ? name : correctNumber);

        if (mItems.get(position).getStatus().equals("M")) {
            mViewHolder.callType.setImageResource(R.drawable.call_missed);
        } else {
            mViewHolder.callType.setImageResource(R.drawable.call_recieve);
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("ankur", "setOnClickListener number = " + correctNumber);
                Intent intent = new Intent(mContext,
                        LeadAddActivity.class);
                intent.putExtra(Constants.LEAD_MOBILE, correctNumber);
                intent.putExtra(Constants.LEAD_NAME, name);
                intent.putExtra(Constants.CALL_SOURCE, "CT");
                mContext.startActivity(intent);
            }
        });

/*        mViewHolder.name
                .setText((mItems.get(position).getName() != null && !mItems
                        .get(position).getName().isEmpty()) ? mItems.get(
                        position).getName() : "<Unknown>");
        mViewHolder.number.setText(mItems.get(position).getMobile());*/
        /*mViewHolder.date
                .setText(mItems.get(position).getDate().split("~")[0]);*/

        mViewHolder.header.setText(headerDate(mItems.get(position).getTrackerDate()));

        Log.i("ankur", "mItems.get(position).getDate().split(\"~\")[0]=" + mItems.get(position).getTrackerDate());

        String time = mItems.get(position).getDate().split("~")[1];
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

        String lastDate = "";
        if (position != 0) {
            lastDate = mItems.get(position - 1).getTrackerDate();
            Log.i("ankur", "getView lastDate = " + lastDate);
        }
        if (position == 0 || !mItems.get(position).getTrackerDate().equals(lastDate)) {
            mViewHolder.separator.setVisibility(View.VISIBLE);
            //mViewHolder.hea.setText(date);
        } else {
            mViewHolder.separator.setVisibility(View.GONE);
        }

        return convertView;
    }

    public String headerDate(String callDate) {
        DateTime dateTimeSystem = new DateTime();
        String today = dateTimeFormatter(dateTimeSystem);
        String yesterday = dateTimeFormatter(dateTimeSystem.minusDays(1));

        if (callDate.equals(today)) {
            return "Today";
        } else if (callDate.equals(yesterday)) {
            return "Yesterday";
        } else {
            return callDate;
        }

    }

    public String dateTimeFormatter(DateTime dateTime) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(dateTime.dayOfMonth().getAsString());
        stringBuilder.append(" ");
        stringBuilder.append(dateTime.monthOfYear().getAsShortText(Locale.ENGLISH));
        stringBuilder.append(" ");
        stringBuilder.append(dateTime.year().getAsString());
        String date = stringBuilder.toString();
        return date;
    }

}
