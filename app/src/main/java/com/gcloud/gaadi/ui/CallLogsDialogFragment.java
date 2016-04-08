package com.gcloud.gaadi.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.CallLog;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.gcloud.gaadi.ApplicationController;
import com.gcloud.gaadi.R;
import com.gcloud.gaadi.adapter.CallLogsAdapter;
import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.constants.ShareType;
import com.gcloud.gaadi.events.CallLogItemSelectedEvent;
import com.gcloud.gaadi.interfaces.CallLogsItemClickInterface;
import com.gcloud.gaadi.model.CallLogItem;
import com.gcloud.gaadi.utils.GCLog;

/**
 * Created by ankit on 2/12/14.
 */
public class CallLogsDialogFragment extends DialogFragment
        implements LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemClickListener {

    private static CallLogsDialogFragment mInstance;
    private CallLogsAdapter callLogAdapter;
    private Cursor mCursor;
    private ListView listView;
    private CallLogsItemClickInterface mListener;
    private Activity activity;
    private String shareText;
    private ShareType shareType;
    private String carId;
    private String imageUrl;
    private boolean shown;

    private String[] mProjection = new String[]{
            CallLog.Calls.NUMBER,
            CallLog.Calls.DURATION,
            CallLog.Calls.TYPE,
            CallLog.Calls.CACHED_NAME,
            CallLog.Calls.DATE,
            CallLog.Calls._ID
    };
    private String callSource = "";

    public CallLogsDialogFragment() {

    }

    public static CallLogsDialogFragment getInstance() {
        if (mInstance == null) {
            mInstance = new CallLogsDialogFragment();
        }
        return mInstance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        ApplicationController.getEventBus().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ApplicationController.getEventBus().unregister(this);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity != null) {
            getLoaderManager().initLoader(Constants.CALL_LOGS_LOADER, null, this);
        }

    }

    private Activity getFragmentActivity() {
        return activity;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.call_log, null, false);
        listView = (ListView) view.findViewById(R.id.logsList);
        mCursor = getCallLogsCursor();
        callLogAdapter = new CallLogsAdapter(getActivity(), mCursor);
        listView.setAdapter(callLogAdapter);
        listView.setOnItemClickListener(this);

        Bundle args = getArguments();
        if (args != null) {
            if (args.containsKey(Constants.SHARE_TEXT) && args.containsKey(Constants.SHARE_TYPE)) {
                shareText = args.getString(Constants.SHARE_TEXT);
                shareType = (ShareType) args.getSerializable(Constants.SHARE_TYPE);
                carId = args.getString(Constants.CAR_ID);
                imageUrl = args.getString(Constants.IMAGE_URL);
            } else if (args.containsKey(Constants.CALL_SOURCE)) {
                callSource = args.getString(Constants.CALL_SOURCE);
            }
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getFragmentActivity());
        builder.setView(view);
        builder.setTitle("Recent Calls");

        if (getFragmentActivity() instanceof CallLogsItemClickInterface) {
            mListener = (CallLogsItemClickInterface) getFragmentActivity();
        }

        return builder.create();
    }

    private Cursor getCallLogsCursor() {

        Cursor cursor = getActivity().getContentResolver().query(CallLog.Calls.CONTENT_URI,
                mProjection, null, null, CallLog.Calls.DEFAULT_SORT_ORDER);

        cursor.moveToFirst();

        return cursor;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        CursorLoader cursorLoader = new CursorLoader(getActivity(),
                CallLog.Calls.CONTENT_URI, mProjection, null, null, CallLog.Calls.DEFAULT_SORT_ORDER);

        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        callLogAdapter.changeCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        callLogAdapter.swapCursor(null);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Cursor cursor = ((CallLogsAdapter) listView.getAdapter()).getCursor();
        cursor.moveToPosition(position);

        CallLogItem callLogItem = new CallLogItem(
                cursor.getString(cursor.getColumnIndex(CallLog.Calls.CACHED_NAME)),
                cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER)),
                cursor.getString(cursor.getColumnIndex(CallLog.Calls.TYPE)),
                callSource);

        GCLog.e("phone number: " + callLogItem.getGaadiFormatNumber() + ", type = " + callLogItem.getType());
        dismiss();

        if (mListener != null) {
            mListener.onCallLogSelected(callLogItem, shareType, shareText, carId, imageUrl);
        }

        if (getFragmentActivity() instanceof DealerPlatformActivity) {
            ApplicationController.getEventBus().post(new CallLogItemSelectedEvent(callLogItem, shareType, shareText, carId));
        }

    }

    @Override
    public void show(FragmentManager manager, String tag) {
        if (shown) return;

        super.show(manager, tag);
        shown = true;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        shown = false;
        super.onDismiss(dialog);
    }

    public boolean isShowing() {
        return shown;
    }
}
