package com.gcloud.gaadi.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.gcloud.gaadi.R;
import com.gcloud.gaadi.model.FormItem;
import com.gcloud.gaadi.ui.customviews.ImageUploadImageView;
import com.gcloud.gaadi.utils.GCLog;
import com.imageuploadlib.PhotosLibrary;
import com.imageuploadlib.Utils.PhotoParams;

import java.util.ArrayList;

/**
 * Created by Lakshay on 26-05-2015.
 */
public class SelectedPhotosAdapter extends BaseAdapter {

    ArrayList<FormItem> mItems;
    Context mContext;

    public SelectedPhotosAdapter(Context context, ArrayList<FormItem> mItems) {
        this.mItems = mItems;
        mContext = context;
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.selected_item, null);
        ImageUploadImageView ivPhoto = (ImageUploadImageView) convertView.findViewById(R.id.ivSelectedPhoto);
        ProgressBar progressBar = (ProgressBar) convertView.findViewById(R.id.pbSelectedPhoto);
        FormItem item = mItems.get(position);
        GCLog.e(item.getImagePath() + "");
        if (item.getImagePath() != null) {
            GCLog.e("getview ImagePath : " + item.getImagePath());
            Glide.with(mContext).load("file://" + item.getImagePath()).diskCacheStrategy(DiskCacheStrategy.NONE).into(ivPhoto);
        } else {
            ImageView imageViewAddMore = (ImageView) convertView.findViewById(R.id.ivAddMore);
            imageViewAddMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PhotoParams params = new PhotoParams();
                    params.setMode(PhotoParams.MODE.CAMERA_PRIORITY);
                    PhotosLibrary.collectPhotos(mContext, params);
                }
            });
            Glide.with(mContext).load(R.drawable.add).into(imageViewAddMore);
        }
//        ivPhoto.setImageURI(Uri.parse(item.getImagePath()));

        item.setImageProgress(progressBar);
        item.setIvIcon(ivPhoto);

        return convertView;
    }
}
