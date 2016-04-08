package com.gcloud.gaadi.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gcloud.gaadi.R;
import com.gcloud.gaadi.model.DocumentInfo;
import com.gcloud.gaadi.ui.Finance.FinanceCollectImagesActivity;

import java.util.ArrayList;

/**
 * Created by Lakshay on 02-09-2015.
 */
public class DocInfoGridAdapter extends BaseAdapter implements View.OnClickListener {

    private ArrayList<DocumentInfo> mDocumentInfos;
    private Context mContext;
    private DocInfoTapListener mDocInfoTapListener;

    public interface DocInfoTapListener {
        public void onTap(DocumentInfo info);
    }

    public DocInfoGridAdapter(Context context, ArrayList<DocumentInfo> documentInfos, DocInfoTapListener docInfoTapListener) {
        mContext = context;
        mDocumentInfos = documentInfos;
        mDocInfoTapListener = docInfoTapListener;
    }

    public void setmDocumentInfos(ArrayList<DocumentInfo> mDocumentInfos) {
        this.mDocumentInfos = mDocumentInfos;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mDocumentInfos.size();
    }

    @Override
    public Object getItem(int position) {
        return mDocumentInfos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.doc_info_layout, null);
        LinearLayout linearLayout = (LinearLayout)view.findViewById(R.id.llDocInfo);
        TextView tvName = (TextView) view.findViewById(R.id.tvDocName);
        tvName.setText(mDocumentInfos.get(position).getDocName());
        view.setTag(mDocumentInfos.get(position));
        view.setOnClickListener(this);
        if(FinanceCollectImagesActivity.selectedTags.contains(((DocumentInfo)view.getTag()).getTag()+"")) {
            linearLayout.setBackgroundResource(R.drawable.selected_document);
        }
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.llDocInfo:
                notifyDataSetChanged();
                DocumentInfo info = (DocumentInfo) v.getTag();
                mDocInfoTapListener.onTap(info);
                break;
        }
    }

}
