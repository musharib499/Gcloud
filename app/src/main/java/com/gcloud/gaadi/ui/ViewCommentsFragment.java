package com.gcloud.gaadi.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.gcloud.gaadi.R;
import com.gcloud.gaadi.adapter.ViewCommentsAdapter;
import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.model.CommentsModel;

public class ViewCommentsFragment extends DialogFragment {

    private static ViewCommentsFragment mInstance;
    CommentsModel commentsModel;
    private ViewCommentsAdapter callTrackAdapter;
    private ListView listView;
    private Activity activity;


    public ViewCommentsFragment() {

    }

    public static ViewCommentsFragment getInstance() {
        if (mInstance == null) {
            mInstance = new ViewCommentsFragment();
        }

        return mInstance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        outState.putSerializable(Constants.MODEL_DATA, commentsModel);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity != null) {
//			getLoaderManager().initLoader(Constants.CALL_LOGS_LOADER, null,
//					this);
        }

    }

    private Activity getFragmentActivity() {
        return activity;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.comments_loglayout, null, false);
        listView = (ListView) view.findViewById(R.id.logsList);
        LinearLayout footerLayout = (LinearLayout) view.findViewById(R.id.footerLayout);
        footerLayout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                dismiss();

            }
        });
        //mCursor = getCallLogsCursor();
//		callTrackAdapter = new CallTrackAdapter(getActivity(), mCursor);
//		
        Bundle args = getArguments();
        if (args != null) {
            commentsModel = (CommentsModel)
                    args.getSerializable(Constants.MODEL_DATA);


        }

        if (savedInstanceState != null) {
            commentsModel = (CommentsModel) savedInstanceState.getSerializable(Constants.MODEL_DATA);
        }

        callTrackAdapter = new ViewCommentsAdapter(getActivity(), commentsModel.getComments());
        listView.setAdapter(callTrackAdapter);

        AlertDialog.Builder builder = new AlertDialog.Builder(
                getFragmentActivity());
        builder.setView(view);
        builder.setTitle("Comments");

//		if (getFragmentActivity() instanceof CallLogsItemClickInterface) {
//			mListener = (CallTrackClickInterface) getFragmentActivity();
//		}

        return builder.create();
    }


}
