package com.gcloud.gaadi.adapter;

/**
 * Created by lakshaygirdhar on 16/10/15.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.gcloud.gaadi.R;
import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.model.DocumentCategories;
import com.gcloud.gaadi.model.DocumentInfo;
import com.gcloud.gaadi.model.Finance.ImageData;
import com.gcloud.gaadi.utils.CommonUtils;
import com.gcloud.gaadi.utils.FinanceUtils;
import com.gcloud.gaadi.utils.GCLog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class FinanceReuploadDocsAdapter extends BaseExpandableListAdapter implements View.OnClickListener {

    public static final int GET_IMAGES = 10;
    public static HashMap<String, HashMap<String, DocumentInfo>> mImages;
    private Activity mActivity;
    private ArrayList<String> mCategories;
    private HashMap<String, Integer> mIconsMap;
    private HashSet<String> mReuploadItems = new HashSet<>();
    private ReuploadListener mReuploadListener;

    public FinanceReuploadDocsAdapter(Activity activity, ReuploadListener listener, ArrayList<ImageData> reuploadItems, ArrayList<DocumentCategories> documentCategoriesArrayList) {
        mActivity = activity;
        // mCategories = FinanceDB.getCategories();
        mCategories = FinanceUtils.getDocumentCategories(documentCategoriesArrayList);
//        mCategories = FinanceUtils.getDocumentCategories();
        mReuploadListener = listener;

        rearrangeCategories();
        HashMap<String, DocumentCategories> documentCategoriesMap = FinanceUtils.getDocumentCategoriesMap(documentCategoriesArrayList);
        HashMap<String, DocumentCategories> docCategoriesIDMap = FinanceUtils.getDocCategoriesIDMap(documentCategoriesArrayList);
        mImages = new HashMap<>();
        for (String category : mCategories) {
//            ArrayList<String> images = ApplicationController.getFinanceDB().getImagesByCategory(category);
//            GCLog.e(Constants.TAG, "category : " + category + " no of images : " + images);
            // ArrayList<DocumentInfo> documentInfos = ApplicationController.getFinanceDB().getCategoryDocs(category);
            ArrayList<DocumentInfo> documentInfos = FinanceUtils.getCategoriesDoc(category, documentCategoriesMap.get(category));
            for (DocumentInfo info : documentInfos) {
                HashMap<String, DocumentInfo> infoHashMap = new HashMap<String, DocumentInfo>();
//                infoHashMap.put(info.getTag()+"",new DocumentInfo());
                infoHashMap.put(info.getTag() + "", info);
                mImages.put(category, infoHashMap);
            }
        }

        mIconsMap = CommonUtils.getFinanceReviewIconsMap();

        for (ImageData imageData:reuploadItems){
            // String parentName = FinanceDBHelper.getParentCategoryName(imageData.getParent_cat_id());
            if (null != docCategoriesIDMap.get(imageData.getParent_cat_id())) {
                String parentName = docCategoriesIDMap.get(imageData.getParent_cat_id()).getCategoryName();
                GCLog.e(Constants.TAG, "Parent Name " + parentName);
                mReuploadItems.add(parentName);
            }
        }
    }

    public static boolean checkIfAnyImagePresent(HashMap<String, DocumentInfo> documentInfoHashMap) {
        if (null != documentInfoHashMap)
        for (Map.Entry<String, DocumentInfo> entry : documentInfoHashMap.entrySet()) {
            if (entry.getValue().getImages().size() > 0)
                return true;
        }
        return false;
    }

    private void rearrangeCategories() {
        if(mCategories.size()>0) {
            int position = mCategories.indexOf(Constants.FINANCE_APPLICATION_FORM);
            mCategories.remove(position);
            mCategories.add(0, Constants.FINANCE_APPLICATION_FORM);

            int positionOthers = mCategories.indexOf(Constants.OTHER_DOCS);
            mCategories.remove(positionOthers);
            mCategories.add(Constants.OTHER_DOCS);
        }
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
        return mImages.get(mCategories.get(groupPosition));
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
    public View getGroupView(final int groupPosition, final boolean isExpanded, View convertView, final ViewGroup parent) {
        View view = LayoutInflater.from(mActivity).inflate(R.layout.proof_tuple_reupload, null);
        ImageView ivProofIcon = (ImageView) view.findViewById(R.id.ivProofIcon);
        final ImageView ivDone = (ImageView) view.findViewById(R.id.ivDone);
        final ImageView ivGrpExpandable = (ImageView) view.findViewById(R.id.ivGrpExpandable);
        final ImageView ivGroupIndicator = (ImageView) view.findViewById(R.id.ivGroupIndicator);
        ivProofIcon.setImageResource(mIconsMap.get(mCategories.get(groupPosition)));
        TextView proofName = (TextView) view.findViewById(R.id.tvProofText);
        proofName.setText(mCategories.get(groupPosition));
        view.setTag(groupPosition);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ivGroupIndicator.getVisibility() == View.VISIBLE) {
                    //mReuploadListener.onGroupClick(groupPosition);
                    mReuploadListener.onReuploadClick((String)ivGroupIndicator.getTag(),groupPosition);

                }
                else if(ivGrpExpandable.getVisibility() == View.VISIBLE)
                {
                    if(isExpanded)
                        ((ExpandableListView)parent).collapseGroup(groupPosition);
                    else
                        ((ExpandableListView)parent).expandGroup(groupPosition);
                }
                else
                {
                   showDialogToReuploadImages((String)ivDone.getTag(), groupPosition);
                }
            }
        });

        if(mReuploadItems.contains(proofName.getText().toString())){
            if (checkIfAnyImagePresent(mImages.get(mCategories.get(groupPosition))))
            {
                ivGroupIndicator.setVisibility(View.GONE);
                ivDone.setVisibility(View.GONE);
                ivGrpExpandable.setTag(proofName.getText().toString());
                ivGrpExpandable.setVisibility(View.VISIBLE);
            }
            else {
                ivGroupIndicator.setVisibility(View.VISIBLE);
                ivGroupIndicator.setTag(proofName.getText().toString());
                ivGroupIndicator.setOnClickListener(this);
                ivDone.setVisibility(View.GONE);
                ivGrpExpandable.setVisibility(View.GONE);
            }
        } else {
            if (checkIfAnyImagePresent(mImages.get(mCategories.get(groupPosition))))
            {
                ivGroupIndicator.setVisibility(View.GONE);
                ivDone.setVisibility(View.GONE);
                ivGrpExpandable.setTag(proofName.getText().toString());
                ivGrpExpandable.setVisibility(View.VISIBLE);
            }
            else {
                ivGroupIndicator.setVisibility(View.GONE);
                ivDone.setVisibility(View.VISIBLE);
                ivDone.setTag(proofName.getText().toString());
                ivGrpExpandable.setVisibility(View.GONE);
            }
        }
        if(isExpanded && ivGrpExpandable.getVisibility() == View.VISIBLE)
        {
            ivGrpExpandable.setImageResource(R.drawable.up_arrow);
        }
        else
        {
            ivGrpExpandable.setImageResource(R.drawable.down_arrow);
        }
        return view;
    }

    private void showDialogToReuploadImages(final String activeCategory, final int groupPosition) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setTitle(R.string.reupload_images);
        builder.setMessage(R.string.confirm_reupload_of_images);
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
              mReuploadListener.onReuploadClick(activeCategory, groupPosition);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
             dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();

        if (!mActivity.isFinishing() && !dialog.isShowing()) {
            dialog.show();
        }
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(mActivity).inflate(R.layout.review_doc_child, null);
        RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.rcvDocs);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(layoutManager);
        ArrayList<String> images = getAllImagesInACategory(mImages.get(mCategories.get(groupPosition)));
        CollectedDocsAdapter adapter = new CollectedDocsAdapter(images, mActivity, mReuploadListener, mCategories.get(groupPosition), groupPosition);
        mRecyclerView.setAdapter(adapter);
        return view;
    }

    public ArrayList<String> getAllImagesInACategory(HashMap<String, DocumentInfo> map) {
        ArrayList<String> allImages = new ArrayList<>();
        for (Map.Entry<String, DocumentInfo> entry : map.entrySet()) {
            allImages.addAll(entry.getValue().getImages());
        }
        return allImages;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.ivGroupIndicator:
                String tag = (String) v.getTag();
                ViewGroup viewGroup = (ViewGroup) v.getParent();
                int position = (int) viewGroup.getTag();
                mReuploadListener.onReuploadClick(tag,position);
                break;
        }

    }

    public interface ReuploadListener {
        void onGroupClick(int position);

        //active Main category & postion required to expand the group on adding images
        void onReuploadClick(String activeCategory, int position);
    }

}


