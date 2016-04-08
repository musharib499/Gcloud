package com.gcloud.gaadi.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import com.gcloud.gaadi.R;
import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.model.BasicListItemModel;
import com.gcloud.gaadi.utils.CommonUtils;
import com.gcloud.gaadi.utils.GCLog;

import java.util.ArrayList;

/**
 * Created by ankit on 28/2/15.
 */
public class URLPickerFragment extends DialogFragment {

    private static final String ARG_TITLE = "ARG_TITLE";
    private static final String ARG_ITEMS = "ARG_ITEMS";
    private static final String ARG_SELECTED_INDEX = "ARG_SELECTED_INDEX";
    private Activity mActivity;
    private int mTitle;
    private int selectedIndex;
    private ArrayList<BasicListItemModel> urls = new ArrayList<>();

    public static URLPickerFragment newInstance(int title, int selectedIndex) {
        Bundle args = new Bundle();
        args.putInt(ARG_TITLE, title);
        //args.putSerializable(ARG_ITEMS, getHostURLs());
        args.putInt(ARG_SELECTED_INDEX, selectedIndex);

        URLPickerFragment urlPickerFragment = new URLPickerFragment();
        urlPickerFragment.setArguments(args);
        return urlPickerFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
    }

    private Activity getFragmentActivity() {
        return mActivity;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        setRetainInstance(true);
        Bundle args = getArguments();
        getHostURLs();
        if (args != null) {
            mTitle = args.getInt(ARG_TITLE, R.string.change_host_url);
            //dealers = (ArrayList<DealerData>) args.getSerializable(ARG_ITEMS);
            selectedIndex = args.getInt(ARG_SELECTED_INDEX, -1);
        }

        if (savedInstanceState != null) {
            selectedIndex = savedInstanceState.getInt(ARG_SELECTED_INDEX, selectedIndex);
        }

        final String[] urlsArray = getHostURLsArray();

        AlertDialog.Builder builder = new AlertDialog.Builder(getFragmentActivity());
        builder.setTitle(mTitle)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        GCLog.e("current url: " + urlsArray[selectedIndex]);
                        CommonUtils.setIntSharedPreference(getFragmentActivity(), Constants.SELECTED_URL_INDEX, selectedIndex);
                        CommonUtils.setStringSharedPreference(getFragmentActivity(), Constants.REST_HOST, urlsArray[selectedIndex]);

                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setSingleChoiceItems(urlsArray, selectedIndex, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selectedIndex = which;
                    }
                });

        return builder.create();

    }

    private void getHostURLs() {
        urls = new ArrayList<>();
        urls.add(new BasicListItemModel("Beta", getFragmentActivity().getResources().getString(R.string.REST_HOST_BETA)));
        urls.add(new BasicListItemModel("Live", getFragmentActivity().getResources().getString(R.string.REST_HOST_LIVE)));

        urls.trimToSize();


    }

    private String[] getHostURLsArray() {
        int itemCount = urls.size();
        String[] urls = new String[itemCount];
        //int i = 0;

        urls[0] = getFragmentActivity().getResources().getString(R.string.REST_HOST_BETA);
        urls[1] = getFragmentActivity().getResources().getString(R.string.REST_HOST_LIVE);

        return urls;

    }
}
