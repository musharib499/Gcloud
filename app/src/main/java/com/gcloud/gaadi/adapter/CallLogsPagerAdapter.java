package com.gcloud.gaadi.adapter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.provider.CallLog;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gcloud.gaadi.R;
import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.model.CallLogViewHolder;
import com.gcloud.gaadi.ui.LeadAddActivity;

import org.joda.time.DateTime;

import java.util.Locale;

/**
 * Created by ankurjha on 23/9/15.
 */
public class CallLogsPagerAdapter extends CursorAdapter {
    private Context mContext;
    private Cursor mCursor;
    private LayoutInflater mInflater;
    private CallLogViewHolder mViewHolder;

    int[] callTypeImages = {
            R.drawable.call_recieve,
            R.drawable.call_outgoing,
            R.drawable.call_missed
    };

    public CallLogsPagerAdapter(Context context, Cursor c) {
        super(context, c, false);
        mContext = context;
        mCursor = c;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        //Log.i("ankur", "newView cursor.getPosition() = "+cursor.getPosition());
        View view = mInflater.inflate(R.layout.call_log_pager_list_item, parent, false);
        mViewHolder = new CallLogViewHolder();
        mViewHolder.parentView = view;
        mViewHolder.name = (TextView) view.findViewById(R.id.name);
        mViewHolder.time = (TextView) view.findViewById(R.id.time);
        mViewHolder.header = (TextView) view.findViewById(R.id.header);
        mViewHolder.separator = (LinearLayout) view.findViewById(R.id.listSeparator);
        mViewHolder.addLead = (ImageView) view.findViewById(R.id.addLeadButton);
        mViewHolder.callType = (ImageView) view.findViewById(R.id.callType);


        view.setTag(mViewHolder);

        return view;
    }

    /*@Override
    public boolean areAllItemsEnabled() {
        return true;
    }

    @Override
    public boolean isEnabled(int position) {
        if(position % 4 == 0)
            return false;
        else
            return true;
    }*/

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //Log.i("ankur", "getView cursor.getPosition() = "+position);
        return super.getView(position, convertView, parent);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        //Log.i("ankur", "bindView cursor.getPosition() = "+cursor.getPosition());
        mViewHolder = (CallLogViewHolder) view.getTag();

        final String name = cursor.getString(cursor.getColumnIndex(CallLog.Calls.CACHED_NAME));
        final String number = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER));
        final String correctNumber = number.length() > 10 ? number.substring(number.length() - 10) : number;
        String type = cursor.getString(cursor.getColumnIndex(CallLog.Calls.TYPE));
        String date = cursor.getString(cursor.getColumnIndex(CallLog.Calls.DATE));
        String time = cursor.getString(cursor.getColumnIndex(CallLog.Calls.DURATION));
        //String currentDate = date;
        // Log.i("ankur", "bindView currentDate = "+currentDate);


       /* if(cursor.getPosition() % 4 == 0) {
            //Log.i("ankur", "cursor.getPosition() = "+cursor.getPosition());
            mViewHolder.separator.setVisibility(View.VISIBLE);
        }*/

        DateTime dateTime = new DateTime(Long.valueOf(date));

        String currentDate = dateTimeFormatter(dateTime);
        /*Log.i("ankur", "bindView currentDate = "+currentDate);
        Log.i("ankur", "bindView Yesterday = "+DateTimeFormatter(dateTime.minusDays(1)));
        Log.i("ankur", "bindView aaj ka din = "+DateTimeFormatter(dateTimeSystem));*/

        /*DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        dateFormat.format(cal.getTime());
        Log.i("ankur", "bindView currentDate calender= " + dateFormat.format(dateTime));*/

        /*Period period = new Period(dateTime, new DateTime());
        int days = period.getDays();
        int months = period.getMonths();

        if (++days < 7 && (months == 0)) {
            date = days + " days ago";
        } else if (months > 0) {
            date = months + " month ago";
        }
        GCLog.e(Constants.TAG, "period between : " + date);*/

        mViewHolder.parentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("ankur", "setOnClickListener name = " + name);
                Intent intent = new Intent(mContext,
                        LeadAddActivity.class);
                intent.putExtra(Constants.LEAD_MOBILE, correctNumber);
                intent.putExtra(Constants.LEAD_NAME, name);
                intent.putExtra(Constants.CALL_SOURCE, "RC");
                mContext.startActivity(intent);
            }
        });

        StringBuilder timeStringBuilder = new StringBuilder();
        int hourOfDay = Integer.parseInt(dateTime.hourOfDay().getAsString());
        int minuteOfHour = Integer.parseInt(dateTime.minuteOfHour().getAsString());

        timeStringBuilder.append((hourOfDay > 12) ? (hourOfDay - 12) : hourOfDay);
        timeStringBuilder.append(":");
        timeStringBuilder.append((minuteOfHour < 10) ? ("0" + minuteOfHour) : minuteOfHour);
        timeStringBuilder.append(" ");
        timeStringBuilder.append(hourOfDay >= 12 ? "PM" : "AM");
        time = timeStringBuilder.toString();

        mViewHolder.name.setText((name != null && !name.isEmpty()) ? name : correctNumber);
        //mViewHolder.number.setText(number);
        mViewHolder.header.setText(headerDate(dateTime));
        mViewHolder.time.setText(time);
        if (Integer.parseInt(type) < 4) {
            mViewHolder.callType.setImageResource(callTypeImages[Integer.parseInt(type) - 1]);
        } else {
            mViewHolder.callType.setImageResource(0);
        }

        String lastDate = "";
        int position = cursor.getPosition();
        if (position != 0) {
            cursor.moveToPosition(position - 1);
            lastDate = cursor.getString(cursor.getColumnIndex(CallLog.Calls.DATE));
            DateTime dateTime1 = new DateTime(Long.valueOf(lastDate));
            lastDate = dateTimeFormatter(dateTime1);
            //Log.i("ankur", "bindView lastDate = "+lastDate);
            cursor.moveToPosition(position);
        }
        if (position == 0 || !currentDate.equals(lastDate)) {
            mViewHolder.separator.setVisibility(View.VISIBLE);
            //mViewHolder.hea.setText(date);
        } else {
            mViewHolder.separator.setVisibility(View.GONE);
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

    public String headerDate(DateTime dateTime) {
        DateTime dateTimeSystem = new DateTime();
        String today = dateTimeFormatter(dateTimeSystem);
        String yesterday = dateTimeFormatter(dateTimeSystem.minusDays(1));
        String callDate = dateTimeFormatter(dateTime);

        if (callDate.equals(today)) {
            return "Today";
        } else if (callDate.equals(yesterday)) {
            return "Yesterday";
        } else {
            return callDate;
        }


    }
}

