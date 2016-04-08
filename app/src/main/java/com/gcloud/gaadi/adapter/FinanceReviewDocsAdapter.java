package com.gcloud.gaadi.adapter;

import android.app.Activity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gcloud.gaadi.ApplicationController;
import com.gcloud.gaadi.R;
import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.model.DocumentCategories;
import com.gcloud.gaadi.utils.CommonUtils;
import com.gcloud.gaadi.utils.FinanceUtils;
import com.gcloud.gaadi.utils.GCLog;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Lakshay on 09-09-2015.
 */
public class FinanceReviewDocsAdapter extends BaseExpandableListAdapter {

    float totalDocumentsCount, providedDocumentsCount;
    private Activity mActivity;
    private ArrayList<String> mCategories;
    private HashMap<String, ArrayList<String>> mImages;
    private HashMap<String, Integer> mIconsMap;
    private HashMap<String, DocumentCategories> documentCategoriesMap;


    public FinanceReviewDocsAdapter(Activity activity, ArrayList<DocumentCategories> mDocCategories) {
        mActivity = activity;
        //mCategories = ApplicationController.getFinanceDB().getCategories();
        //mCategories = FinanceUtils.getDocumentCategories();

        mCategories = FinanceUtils.getDocumentCategories(mDocCategories);
        totalDocumentsCount = mCategories.size();
        documentCategoriesMap = FinanceUtils.getDocumentCategoriesMap(mDocCategories);
        rearrangeCategories();

//        ArrayList<FinanceData> financeDatas = ApplicationController.getFinanceDB().getImagesInDb();

        mImages = new HashMap<>();
        for (String category : mCategories) {
            ArrayList<String> images = ApplicationController.getFinanceDB().getImagesByCategory(activity, category, documentCategoriesMap);
            GCLog.e(Constants.TAG, "category : " + category + " no of images : " + images);
            if (images.size() > 0 && !category.equalsIgnoreCase("Others")) {
                providedDocumentsCount += 1;
            } else if (category.equalsIgnoreCase("Others")) {
                totalDocumentsCount = totalDocumentsCount - 1;
            }
            mImages.put(category, images);
        }
        int profileCompletionPercent = (int) ((providedDocumentsCount / totalDocumentsCount) * 100);
        GCLog.e("DocsProvided = " + providedDocumentsCount, "TotalDocCount = " + totalDocumentsCount);
        ApplicationController.getEventBus().post(String.valueOf(profileCompletionPercent));
        mIconsMap = CommonUtils.getFinanceReviewIconsMap();

    }

    private void rearrangeCategories() {
        int position = mCategories.indexOf(Constants.FINANCE_APPLICATION_FORM);
        mCategories.remove(position);
        mCategories.add(0, Constants.FINANCE_APPLICATION_FORM);

        int positionOthers = mCategories.indexOf(Constants.OTHER_DOCS);
        mCategories.remove(positionOthers);
        mCategories.add(Constants.OTHER_DOCS);
    }


    @Override
    public int getGroupCount() {
        return mCategories.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
//        return mDocumentInfos.get(mCategories.get(groupPosition)).size();
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mImages.get(mCategories.get(groupPosition));
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mImages.get(mCategories.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(mActivity).inflate(R.layout.proof_tuple, null);
        ImageView ivProofIcon = (ImageView) view.findViewById(R.id.ivProofIcon);
        ImageView ivGroupIndicator = (ImageView) view.findViewById(R.id.ivGroupIndicator);
        ivProofIcon.setImageResource(mIconsMap.get(mCategories.get(groupPosition)));
        TextView proofName = (TextView) view.findViewById(R.id.tvProofText);
        proofName.setText(mCategories.get(groupPosition));
       /* if (isExpanded) {
            ivGroupIndicator.setRotation(180.0f);
        }*/
        if (null != mImages.get(mCategories.get(groupPosition)) && mImages.get(mCategories.get(groupPosition)).size() > 0) {
            ivGroupIndicator.setImageResource(R.drawable.finance_done);
        } else {
            ivGroupIndicator.setImageResource(R.drawable.cross);
        }
        return view;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(mActivity).inflate(R.layout.review_doc_child, null);
        RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.rcvDocs);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(layoutManager);
        ArrayList<String> images = mImages.get(mCategories.get(groupPosition));
        CollectedDocsAdapter adapter = new CollectedDocsAdapter(images, mActivity);
        mRecyclerView.setAdapter(adapter);
        return view;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}


