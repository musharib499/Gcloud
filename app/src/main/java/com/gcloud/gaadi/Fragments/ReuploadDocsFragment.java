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
import android.widget.ExpandableListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.gcloud.gaadi.R;
import com.gcloud.gaadi.adapter.FinanceReuploadDocsAdapter;
import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.model.DocumentCategories;
import com.gcloud.gaadi.model.DocumentInfo;
import com.gcloud.gaadi.model.Finance.GetReuploadItemsResponse;
import com.gcloud.gaadi.retrofit.RetrofitRequest;
import com.gcloud.gaadi.ui.Finance.FinanceCollectReuploadImagesActivity;
import com.gcloud.gaadi.utils.CommonUtils;
import com.gcloud.gaadi.utils.GCLog;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by lakshaygirdhar on 16/10/15.
 */
public class ReuploadDocsFragment extends Fragment implements ExpandableListView.OnGroupExpandListener, FinanceReuploadDocsAdapter.ReuploadListener {

    public static String mFinanceId;
    public int sizeOfDocumentImages;
    ArrayList<DocumentCategories> documentCategoriesArrayList;
    private Activity mActivity;
    private ExpandableListView mElvDocuments;
    private FinanceReuploadDocsAdapter mReviewDocsAdapter;
    private ProgressBar mProgressBar;
    private String mActiveCategory;
    private int mActivePosition;

    public static ReuploadDocsFragment getInstance(String financeId) {
        ReuploadDocsFragment fragment = new ReuploadDocsFragment();
        mFinanceId = financeId;
        return fragment;
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
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
        View view = LayoutInflater.from(getFragmentActivity()).inflate(R.layout.finance_reupload_docs_fragment, null);
        mElvDocuments = (ExpandableListView) view.findViewById(R.id.elvFinanceDocs);
        mElvDocuments.setVisibility(View.GONE);
        mProgressBar = (ProgressBar) view.findViewById(R.id.pbContent);
        mProgressBar.setVisibility(View.VISIBLE);
        mElvDocuments.setOnGroupExpandListener(this);

        RetrofitRequest.getReuploadCategories(mFinanceId, new Callback<GetReuploadItemsResponse>() {

            @Override
            public void success(GetReuploadItemsResponse getReuploadItemsResponse, Response response) {
                GCLog.e(Constants.TAG, getReuploadItemsResponse.getImageData().size() + "");
                mProgressBar.setVisibility(View.GONE);
                mElvDocuments.setVisibility(View.VISIBLE);
                documentCategoriesArrayList = new ArrayList<DocumentCategories>();
                documentCategoriesArrayList.addAll(getReuploadItemsResponse.getDocumentCategoriesArrayList());
                //delete this comment--setting the listview -->not to be checked
                mReviewDocsAdapter = new FinanceReuploadDocsAdapter(getFragmentActivity(), ReuploadDocsFragment.this, getReuploadItemsResponse.getImageData(), documentCategoriesArrayList);
                mElvDocuments.setAdapter(mReviewDocsAdapter);
            }

            @Override
            public void failure(RetrofitError error) {
//                Toast.makeText(mActivity, "Some Error", Toast.LENGTH_SHORT).show();
                CommonUtils.showErrorToast(mActivity,error,Toast.LENGTH_SHORT);
                mProgressBar.setVisibility(View.GONE);
            }
        });
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        GCLog.e(Constants.TAG, "Fragment OnActivity Result");
        if (requestCode == FinanceReuploadDocsAdapter.GET_IMAGES) {
            GCLog.e(Constants.TAG, "Fragment OnActivity Request Code Get Images");
            if (resultCode == Activity.RESULT_OK) {
                GCLog.e(Constants.TAG, "Fragment OnActivity Result Code Ok");
                DocumentInfo info = (DocumentInfo) data.getExtras().getSerializable(Constants.RESULT_IMAGES);
                sizeOfDocumentImages=info.getImages().size();
                GCLog.e(Constants.TAG, "No Of Images Received : " + sizeOfDocumentImages  + "");
                DocumentInfo alreadyImagesInfo = FinanceReuploadDocsAdapter.mImages.get(mActiveCategory).get(info.getTag()+"");

                if(alreadyImagesInfo==null || alreadyImagesInfo.getImages().size()==0){
                    HashMap<String,DocumentInfo> docsMap = FinanceReuploadDocsAdapter.mImages.get(mActiveCategory);
                    docsMap.put(info.getTag()+"",info);
                    FinanceReuploadDocsAdapter.mImages.put(mActiveCategory, docsMap);
                } else {
                    alreadyImagesInfo.getImages().addAll(info.getImages());
                    HashMap<String,DocumentInfo> docsMap = FinanceReuploadDocsAdapter.mImages.get(mActiveCategory);
                    docsMap.put(info.getTag()+"",alreadyImagesInfo);
                    FinanceReuploadDocsAdapter.mImages.put(mActiveCategory, docsMap);
                }
                mElvDocuments.expandGroup(mActivePosition);
                mReviewDocsAdapter.notifyDataSetChanged();
            }
        }
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
    public void onGroupClick(int position) {
        if (mElvDocuments.isGroupExpanded(position)) {
            mElvDocuments.collapseGroup(position);
        } else {
            mElvDocuments.expandGroup(position);
        }
    }

    @Override
    public void onReuploadClick(String activeCategory,int position) {
        mActiveCategory = activeCategory;
        mActivePosition = position;
        Intent intent = new Intent(mActivity, FinanceCollectReuploadImagesActivity.class);
        intent.putExtra(Constants.ACTIVE_CATEGORY, activeCategory);
        intent.putExtra(Constants.DOCUMENT_CATEGORIES, documentCategoriesArrayList);
        startActivityForResult(intent, FinanceReuploadDocsAdapter.GET_IMAGES);
    }
}
