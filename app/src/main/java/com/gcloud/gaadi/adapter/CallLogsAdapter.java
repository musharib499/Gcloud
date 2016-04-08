package com.gcloud.gaadi.adapter;

import android.content.Context;
import android.database.Cursor;
import android.provider.CallLog;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gcloud.gaadi.R;
import com.gcloud.gaadi.model.CallLogViewHolder;

import org.joda.time.DateTime;

import java.util.Locale;

/**
 * Created by ankit on 2/12/14.
 */
public class CallLogsAdapter extends CursorAdapter {

    private Context mContext;
    private Cursor mCursor;
    private LayoutInflater mInflater;
    private CallLogViewHolder mViewHolder;

    public CallLogsAdapter(Context context, Cursor c) {
        super(context, c, false);
        mContext = context;
        mCursor = c;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        View view = mInflater.inflate(R.layout.call_log_list_item, parent, false);
        mViewHolder = new CallLogViewHolder();
        mViewHolder.name = (TextView) view.findViewById(R.id.name);
        mViewHolder.number = (TextView) view.findViewById(R.id.number);
        mViewHolder.date = (TextView) view.findViewById(R.id.date);
        mViewHolder.time = (TextView) view.findViewById(R.id.time);

        view.setTag(mViewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        mViewHolder = (CallLogViewHolder) view.getTag();

        String name = cursor.getString(cursor.getColumnIndex(CallLog.Calls.CACHED_NAME));
        String number = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER));
        String date = cursor.getString(cursor.getColumnIndex(CallLog.Calls.DATE));
        String time = cursor.getString(cursor.getColumnIndex(CallLog.Calls.DURATION));

        DateTime dateTime = new DateTime(Long.valueOf(date));
        /*Period period = new Period(dateTime, new DateTime());
        int days = period.getDays();
        int months = period.getMonths();

        if (++days < 7 && (months == 0)) {
            date = days + " days ago";
        } else if (months > 0) {
            date = months + " month ago";
        }
        GCLog.e(Constants.TAG, "period between : " + date);*/

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(dateTime.dayOfMonth().getAsString());
        stringBuilder.append(" ");
        stringBuilder.append(dateTime.monthOfYear().getAsShortText(Locale.ENGLISH));
        stringBuilder.append(" ");
        stringBuilder.append(dateTime.year().getAsString());
        date = stringBuilder.toString();

        StringBuilder timeStringBuilder = new StringBuilder();
        int hourOfDay = Integer.parseInt(dateTime.hourOfDay().getAsString());
        int minuteOfHour = Integer.parseInt(dateTime.minuteOfHour().getAsString());

        timeStringBuilder.append((hourOfDay > 12) ? (hourOfDay - 12) : hourOfDay);
        timeStringBuilder.append(":");
        timeStringBuilder.append((minuteOfHour < 10) ? ("0" + minuteOfHour) : minuteOfHour);
        timeStringBuilder.append(" ");
        timeStringBuilder.append(hourOfDay >= 12 ? "PM" : "AM");
        time = timeStringBuilder.toString();

        mViewHolder.name.setText((name != null && !name.isEmpty()) ? name : "<Unknown>");
        mViewHolder.number.setText(number);
        mViewHolder.date.setText(date);
        mViewHolder.time.setText(time);

    }


}
