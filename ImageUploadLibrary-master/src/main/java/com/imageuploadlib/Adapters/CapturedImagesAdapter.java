package com.imageuploadlib.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.imageuploadlib.R;
import com.imageuploadlib.Utils.FileInfo;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Lakshay on 19-02-2015.
 */

public class CapturedImagesAdapter extends BaseAdapter implements View.OnClickListener {
    Context mContext;
    ArrayList<FileInfo> imageInfoArrayList;
    private ImageDeleted imageDeletedListener = null;

    public CapturedImagesAdapter(Context context, ArrayList<FileInfo> list) {
        this.mContext = context;
        imageInfoArrayList = list;
    }

    public CapturedImagesAdapter(Context context, ArrayList<FileInfo> list, ImageDeleted imageDeletedListener) {
        this.mContext = context;
        imageInfoArrayList = list;
        this.imageDeletedListener = imageDeletedListener;
    }

    public ArrayList<FileInfo> getImageInfoArrayList() {
        return imageInfoArrayList;
    }

    public void setImageInfoArrayList(ArrayList<FileInfo> imageInfoArrayList) {
        this.imageInfoArrayList = imageInfoArrayList;
    }

    public void setImageDeletedListener(ImageDeleted listener) {
        this.imageDeletedListener = listener;
    }

    @Override
    public void onClick(View v) {

        int position = (int) v.getTag();
        if (v.getId() == R.id.ivRemoveImage) {
            Log.e("On Click Remove", position + "");
            File file = new File(imageInfoArrayList.get(position).getFilePath());
            file.delete();
            imageInfoArrayList.remove(position);
            notifyDataSetChanged();
        }
    }

    @Override
    public int getCount() {
        return imageInfoArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return imageInfoArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ImagesHolder holder;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.captured_images_overlay, null);
            holder = new ImagesHolder();
            holder.capturedImage = (ImageView) convertView.findViewById(R.id.ivCaptured);
            holder.ivRemoveImage = (ImageView) convertView.findViewById(R.id.ivRemoveImage);
            Log.e("Position getView", position + "");
            holder.ivRemoveImage.setTag(position);
            convertView.setTag(holder);
        } else {
            holder = (ImagesHolder) convertView.getTag();
        }

        holder.ivRemoveImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("On Click Remove", position + "");
                File file = new File(imageInfoArrayList.get(position).getFilePath());
                file.delete();
                imageInfoArrayList.remove(position);
                if (imageDeletedListener != null)
                    imageDeletedListener.onImageDeleted(imageInfoArrayList.size());
                notifyDataSetChanged();
            }
        });

        Glide.with(mContext).load("file://" + imageInfoArrayList.get(position).getFilePath())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .crossFade()
                .placeholder(R.drawable.image_load_default_small)
                .into(holder.capturedImage);
        return convertView;
    }

    public interface ImageDeleted {
        void onImageDeleted(int count);
    }

    public class ImagesHolder implements Serializable {

        ImageView ivRemoveImage;
        ImageView capturedImage;
    }
}
