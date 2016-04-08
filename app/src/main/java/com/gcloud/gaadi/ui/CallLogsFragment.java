package com.gcloud.gaadi.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.CallLog;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.gcloud.gaadi.R;
import com.gcloud.gaadi.adapter.CallLogsPagerAdapter;
import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.utils.CommonUtils;

/**
 * Created by ankurjha on 23/9/15.
 */
public class CallLogsFragment extends Fragment {

    ListView listView;
    CallLogsPagerAdapter callLogAdapter;
    private Cursor mCursor;
    private Context context;
    private String[] mProjection = new String[]{
            CallLog.Calls.NUMBER,
            CallLog.Calls.DURATION,
            CallLog.Calls.TYPE,
            CallLog.Calls.CACHED_NAME,
            CallLog.Calls.DATE,
            CallLog.Calls._ID
    };

    public static CallLogsFragment getInstance(Context context) {
        CallLogsFragment fragment = new CallLogsFragment();
        fragment.context = context;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater i, ViewGroup container, Bundle savedInstanceState) {
        LayoutInflater inflat = getActivity().getLayoutInflater();
        View view = inflat.inflate(R.layout.call_log, container, false);
        listView = (ListView) view.findViewById(R.id.logsList);
        mCursor = getCallLogsCursor();
        callLogAdapter = new CallLogsPagerAdapter(getActivity(), mCursor);
        listView.setAdapter(callLogAdapter);
        //listView.setOnItemClickListener(this);
        return view;
    }

    private Cursor getCallLogsCursor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && !CommonUtils.checkForPermission(context,
                new String[]{Manifest.permission.READ_CALL_LOG},
                Constants.REQUEST_PERMISSION_READ_CALL_LOG, "Phone")) {
            return null;
        }

        Cursor cursor = getActivity().getContentResolver().query(CallLog.Calls.CONTENT_URI,
                mProjection, null, null, CallLog.Calls.DEFAULT_SORT_ORDER);

        if (cursor != null) {
            cursor.moveToFirst();
        }

        return cursor;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCursor = getCallLogsCursor();
        callLogAdapter.swapCursor(mCursor);
    }
}
