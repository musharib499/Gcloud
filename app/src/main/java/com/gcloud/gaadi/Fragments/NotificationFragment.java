package com.gcloud.gaadi.Fragments;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.widget.ListViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gcloud.gaadi.R;
import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.db.MakeModelVersionDB;
import com.gcloud.gaadi.insurance.AllCasesActivity;
import com.gcloud.gaadi.model.InsuranceInspectedCarData;
import com.gcloud.gaadi.ui.LeadAddActivity;
import com.gcloud.gaadi.ui.NotificationActivity;
import com.gcloud.gaadi.ui.SellerLeadsDetailPageActivity;
import com.gcloud.gaadi.utils.GCLog;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Created by gaurav on 8/3/16.
 */
public class NotificationFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private MyCursorAdapter adapter;
    private NotificationActivity.UpdateTabText listener;

    public static NotificationFragment newInstance(int position, NotificationActivity.UpdateTabText listener) {
        NotificationFragment fragment = new NotificationFragment();
        fragment.listener = listener;

        Bundle args = new Bundle();
        args.putInt("position", position);

        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_notification, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getLoaderManager().initLoader(getArguments().getInt("position"), null, this);

        view.findViewById(R.id.checkconnection).setVisibility(View.GONE);
        view.findViewById(R.id.retry).setVisibility(View.GONE);
        ((ImageView) view.findViewById(R.id.no_internet_img)).setImageResource(R.drawable.no_notification);
        ((TextView) view.findViewById(R.id.errorMessage)).setText("No Notifications Found");

        adapter = new MyCursorAdapter(getActivity(), null, false);

        ListViewCompat listView = (ListViewCompat) view.findViewById(R.id.listView);
        listView.setAdapter(adapter);


    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(),
                Uri.parse("content://"
                        + Constants.NOTIFICATION_CONTENT_AUTHORITY + "/" + MakeModelVersionDB.TABLE_NOTIFICATION),
                null,
                MakeModelVersionDB.COLUMN_TYPE + " = ? AND " + MakeModelVersionDB.COLUMN_TIME + " > ?",
                new String[]{String.valueOf(getArguments().getInt("position")),
                        String.valueOf(new DateTime().withTimeAtStartOfDay().minusDays(15).minusMillis(1).getMillis())},
                MakeModelVersionDB.COLUMN_TIME + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
        getView().findViewById(R.id.errorLayout).setVisibility(adapter.getCount() > 0 ? View.GONE : View.VISIBLE);
        if (listener != null) {
            listener.updateTabText(getArguments().getInt("position"), adapter.getCount());
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }

    private class ViewHolder {

        public TextView title, message, time, date, download, share;
        public LinearLayout callNowLayout, insuranceLayout;
        public View parent;

        public ViewHolder(View view) {
            parent = view;
            title = (TextView) view.findViewById(R.id.title);
            message = (TextView) view.findViewById(R.id.message);
            time = (TextView) view.findViewById(R.id.time);
            date = (TextView) view.findViewById(R.id.date);
            download = (TextView) view.findViewById(R.id.download);
            share = (TextView) view.findViewById(R.id.share);
            callNowLayout = (LinearLayout) view.findViewById(R.id.callNowLayout);
            insuranceLayout = (LinearLayout) view.findViewById(R.id.insuranceLayout);
        }
    }

    private class NotificationModel {
        @SerializedName("title")
        private String title;

        @SerializedName("nType")
        private String nType;

        @SerializedName("time")
        private String time;

        @SerializedName("nID")
        private String nID;

        @SerializedName("leadId")
        private String leadId;

        @SerializedName("leadname")
        private String leadname;

        @SerializedName("id")
        private int id;

        @SerializedName("insurance_data")
        private InsuranceInspectedCarData insurance_data;

        @SerializedName("notificationMessage")
        private String message;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public int getnType() {

                return Integer.parseInt(nType);

        }

        public void setnType(String nType) {
            this.nType = nType;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getnID() {
            return nID;
        }

        public void setnID(String nID) {
            this.nID = nID;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getLeadId() {
            return leadId;
        }

        public void setLeadId(String leadId) {
            this.leadId = leadId;
        }

        public String getLeadname() {
            return leadname == null ? "" : leadname;
        }

        public void setLeadname(String leadname) {
            this.leadname = leadname;
        }

        public InsuranceInspectedCarData getInsurance_data() {
            return insurance_data;
        }

        public void setInsurance_data(InsuranceInspectedCarData insurance_data) {
            this.insurance_data = insurance_data;
        }
    }

    private class MyCursorAdapter extends CursorAdapter {

        private final DateTimeFormatter timeFormat = DateTimeFormat.forPattern("KK:mm a");
        private final DateTimeFormatter dateFormat = DateTimeFormat.forPattern("dd MMM yyyy");

        public MyCursorAdapter(Context context, Cursor c, boolean autoRequery) {
            super(context, c, autoRequery);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            View view = LayoutInflater.from(context).inflate(R.layout.notification_row, parent, false);
            view.setTag(new ViewHolder(view));
            return view;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            ViewHolder holder = (ViewHolder) view.getTag();
            holder.date.setVisibility(View.GONE);
            holder.callNowLayout.setVisibility(View.GONE);
            holder.insuranceLayout.setVisibility(View.GONE);


            GCLog.e("gaurav", "json: " + cursor.getString(cursor.getColumnIndex(MakeModelVersionDB.COLUMN_JSON)));
            final NotificationModel model =
                    new Gson().fromJson(cursor.getString(cursor.getColumnIndex(MakeModelVersionDB.COLUMN_JSON)),
                            NotificationModel.class);
            model.setId(cursor.getInt(cursor.getColumnIndex(MakeModelVersionDB.COLUMN_ID)));
            model.setTime(cursor.getString(cursor.getColumnIndex(MakeModelVersionDB.COLUMN_TIME)));
            //model.setnType(cursor.getString(cursor.getColumnIndex(MakeModelVersionDB.COLUMN_TYPE)));

            DateTime dbTime = new DateTime(Long.valueOf(model.getTime())).withTimeAtStartOfDay();

            if (cursor.getPosition() == 0) {
                holder.date.setText(getLabel(holder, dbTime));
            } else {
                cursor.moveToPosition(cursor.getPosition() - 1);
                DateTime previousTime =
                        new DateTime(Long.valueOf(cursor.getString(cursor.getColumnIndex(MakeModelVersionDB.COLUMN_TIME))))
                                .withTimeAtStartOfDay();
                if (!previousTime.isEqual(dbTime)) {
                    holder.date.setText(getLabel(holder, dbTime));
                }
            }

            holder.title.setText(model.getTitle());
            holder.time.setText(timeFormat.print(Long.valueOf(model.getTime())));
            holder.message.setText(model.getMessage());

            Intent intent = null;

            Bundle args = new Bundle();
            args.putBoolean(Constants.FROM_NOTIFICATION, true);
            args.putString("nID", model.getnID());

            switch (model.getnType()) {
                case 1:
                    intent = new Intent(getActivity(), LeadAddActivity.class);
                    args.putString(Constants.VIEW_LEAD, Constants.VALUE_VIEWLEAD);
                case 6:
                    if (intent == null) {
                        intent = new Intent(getActivity(), SellerLeadsDetailPageActivity.class);
                    }
                    args.putString("leadId", model.getLeadId());
                    args.putString("leadname", model.getLeadname());
                    holder.callNowLayout.setVisibility(View.VISIBLE);
                    break;

                case 7:
                    intent = new Intent(getActivity(), AllCasesActivity.class);
                    if (model.getInsurance_data() != null
                            && model.getInsurance_data().getPolicyDocUrl() != null
                            && !model.getInsurance_data().getPolicyDocUrl().isEmpty()) {
                        holder.insuranceLayout.setVisibility(View.VISIBLE);
                    }
                    break;

                default:
                    args = null;
            }

            if (intent != null) {
                if (getArguments().getInt("position") == 2) {
                    args.putBoolean("read", true);
                } else {
                    args.putLong("rowId", model.getId());
                }
                intent.putExtras(args);
            }

            final Intent finalIntent = intent;
            holder.parent.setOnClickListener(args == null ? null : new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(finalIntent);
                }
            });

            final Intent callIntent = finalIntent;
            if (holder.callNowLayout.getVisibility() == View.VISIBLE) {
                holder.callNowLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        callIntent.putExtra(Constants.PERFORM_CALL, true);
                        startActivity(callIntent);
                    }
                });
            }
            if (holder.insuranceLayout.getVisibility() == View.VISIBLE) {
                final Intent downloadIntent = finalIntent;
                holder.download.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        downloadIntent.putExtra(Constants.INSURANCE_INSPECTED_CAR_DATA,
                                model.getInsurance_data());
                        downloadIntent.putExtra(Constants.ACTION, "download");
                        startActivity(downloadIntent);
                    }
                });
                final Intent shareIntent = finalIntent;
                holder.share.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        shareIntent.putExtra(Constants.INSURANCE_INSPECTED_CAR_DATA,
                                model.getInsurance_data());
                        shareIntent.putExtra(Constants.ACTION, "share");
                        startActivity(shareIntent);
                    }
                });
            }
        }

        private String getLabel(ViewHolder holder, DateTime dbTime) {
            holder.date.setVisibility(View.VISIBLE);
            DateTime today = new DateTime().withTimeAtStartOfDay();
            GCLog.e("gaurav", "time: " + today.minusDays(1).toString() + " " + dbTime.toString() + " " + today.toString());
            if (dbTime.isEqual(today)) {
                // It is today
                return "Today";
            } else if (dbTime.isAfter(today.minusDays(1).minusMillis(1)) && dbTime.isBefore(today)) {
                // It is yesterday
                return "Yesterday";
            } else {
                return dateFormat.print(dbTime);
            }
        }
    }
}
