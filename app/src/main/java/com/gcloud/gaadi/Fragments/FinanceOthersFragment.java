package com.gcloud.gaadi.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gcloud.gaadi.R;
import com.gcloud.gaadi.ui.Finance.GaadiFinanceActivity;

/**
 * Created by lakshaygirdhar on 19/10/15.
 */
public class FinanceOthersFragment extends Fragment {

    private Activity mActivity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
    }

    private Activity getFragmentActivity() {
        return mActivity;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        //mActivity = getFragmentActivity();
    }

    public static FinanceOthersFragment getInstance(){
        FinanceOthersFragment fragment = new FinanceOthersFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.others_fragment,null);
        view.findViewById(R.id.tvGoToMain).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getFragmentActivity(), GaadiFinanceActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                getFragmentActivity().finish();
            }
        });
        return view;
    }
}
