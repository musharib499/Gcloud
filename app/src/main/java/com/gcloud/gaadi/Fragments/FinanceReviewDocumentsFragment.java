package com.gcloud.gaadi.Fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.gcloud.gaadi.ApplicationController;
import com.gcloud.gaadi.R;
import com.gcloud.gaadi.adapter.FinanceReviewDocsAdapter;
import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.model.DocumentCategories;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

/**
 * Created by Lakshay on 09-09-2015.
 */
public class FinanceReviewDocumentsFragment extends Fragment implements ExpandableListView.OnGroupExpandListener {

    ArrayList<DocumentCategories> mDocCategoriesList;
    TextView textVw;
    private Activity mActivity;
    private ExpandableListView mElvDocuments;
    private FinanceReviewDocsAdapter mReviewDocsAdapter;

    public static FinanceReviewDocumentsFragment getInstance(ArrayList<DocumentCategories> mDocCategoriesList) {
        FinanceReviewDocumentsFragment fragment = new FinanceReviewDocumentsFragment();
        Bundle extras = new Bundle();
        extras.putSerializable(Constants.DOCUMENT_CATEGORIES, mDocCategoriesList);
        fragment.setArguments(extras);
        return fragment;
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        //mActivity = getActivity();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
    }

    private Activity getFragmentActivity() {
        return mActivity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = LayoutInflater.from(getFragmentActivity()).inflate(R.layout.finance_review_documents_fragment, null);
        ApplicationController.getEventBus().register(this);
        mDocCategoriesList = (ArrayList<DocumentCategories>) getArguments().getSerializable(Constants.DOCUMENT_CATEGORIES);
        mElvDocuments = (ExpandableListView) view.findViewById(R.id.elvFinanceDocs);
        textVw = (TextView) view.findViewById(R.id.profileCompletionText);
        mElvDocuments.setOnGroupExpandListener(this);
        mReviewDocsAdapter = new FinanceReviewDocsAdapter(getFragmentActivity(), mDocCategoriesList);
        mElvDocuments.setAdapter(mReviewDocsAdapter);
        return view;
    }

    @Override
    public void onGroupExpand(int groupPosition) {
        int len = mReviewDocsAdapter.getGroupCount();

        for (int i = 0; i < len; i++) {
            if (i != groupPosition) {
                mElvDocuments.collapseGroup(i);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        ApplicationController.getEventBus().unregister(this);

    }

    @Subscribe
    public void getProfileCompletionText(String text) {
        textVw.setText(text + "% Complete");
    }
}
