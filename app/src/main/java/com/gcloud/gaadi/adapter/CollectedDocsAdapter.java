package com.gcloud.gaadi.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.gcloud.gaadi.R;
import com.gcloud.gaadi.model.DocumentInfo;
import com.gcloud.gaadi.ui.Finance.FinanceReuploadActivity;
import com.gcloud.gaadi.utils.GCLog;
import com.imageuploadlib.Activity.ReviewImageActivity;
import com.imageuploadlib.Utils.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Lakshay on 10-09-2015.
 */
public class CollectedDocsAdapter extends RecyclerView.Adapter<CollectedDocsAdapter.Holder> {

    private static final int TYPE_ITEM = 1;
    private static final int TYPE_FOOTER = 2;
    ArrayList<String> mDocuments;
    Context mContext;
    String activeCategory;
    int grpPosition;
    private FinanceReuploadDocsAdapter.ReuploadListener mReuploadListener;

    public CollectedDocsAdapter(ArrayList<String> mDocuments,Context mContext,FinanceReuploadDocsAdapter.ReuploadListener mReuploadListener,String activeCategory, int grpPosition) {
        this.mDocuments = mDocuments;
        GCLog.e(Constants.TAG, "Documents Size : " + mDocuments.size());
        this.mContext = mContext;
        this.activeCategory = activeCategory;
        this.grpPosition = grpPosition;
        this.mReuploadListener = mReuploadListener;
    }

    public CollectedDocsAdapter(ArrayList<String> mDocuments,Context mContext) {
        this.mDocuments = mDocuments;
        GCLog.e(Constants.TAG, "Documents Size : " + mDocuments.size());
        this.mContext = mContext;
    }
    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
//        ImageView imageView = new ImageView(mContext);
        View view = LayoutInflater.from(mContext).inflate(R.layout.collected_doc_tuple, null);
        Holder holder = new Holder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(Holder holder, final int position) {
        if(holder.getItemViewType() != TYPE_FOOTER) {
            holder.ivDeleteImage.setVisibility(View.GONE);
            Glide.with(mContext).load("file://" + mDocuments.get(position)).into(holder.ivImage);
            holder.ivImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, ReviewImageActivity.class);
                    intent.putExtra(Constants.IMAGE_PATH, mDocuments.get(position));
                    intent.putExtra(Constants.FLAG, true);
                    mContext.startActivity(intent);
                }
            });
            if (mContext instanceof FinanceReuploadActivity) {
                holder.ivDeleteImage.setVisibility(View.VISIBLE);
                holder.ivDeleteImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        removeImage(mDocuments.get(position));
                        mDocuments.remove(position);
                        notifyDataSetChanged();
                    }
                });
            }
        }
        else
        {
            holder.ivDeleteImage.setVisibility(View.GONE);
            holder.ivImage.setImageResource(R.drawable.finance_add_images);
            holder.ivImage.setScaleType(ImageView.ScaleType.CENTER);
            holder.ivImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mReuploadListener.onReuploadClick(activeCategory, grpPosition);
                }
            });
        }
    }

    private void removeImage(String s) {
        HashMap<String, DocumentInfo> docMap = FinanceReuploadDocsAdapter.mImages.get(activeCategory);

        DocumentInfo info = null;
        String tag = "";
        for(Map.Entry<String,DocumentInfo> entry :docMap.entrySet()){
            tag = entry.getKey();
            info = entry.getValue();
            if(info.getImages().contains(s)){
                info.getImages().remove(s);
                entry.setValue(info);
                break;
            }
        }
        docMap.put(tag,info);
        FinanceReuploadDocsAdapter.mImages.put(activeCategory,docMap);
    }

    @Override
    public int getItemCount() {
        if(mContext instanceof FinanceReuploadActivity && mDocuments.size()>0)
        return mDocuments.size()+1;
        else
        {
            return mDocuments.size();
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(mContext instanceof FinanceReuploadActivity) {
            if (position == mDocuments.size())
                return TYPE_FOOTER;
            else
                return TYPE_ITEM;
        }
        else
            return super.getItemViewType(position);
    }

    public class Holder extends RecyclerView.ViewHolder {

        private ImageView ivImage, ivDeleteImage;

        public Holder(View itemView) {
            super(itemView);
            ivImage = (ImageView) itemView.findViewById(R.id.ivImage);
            ivDeleteImage = (ImageView) itemView.findViewById(R.id.ivDeleteImage);

        }
    }
}
