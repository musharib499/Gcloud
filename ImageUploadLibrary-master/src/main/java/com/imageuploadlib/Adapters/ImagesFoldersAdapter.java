package com.imageuploadlib.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.imageuploadlib.R;
import com.imageuploadlib.Utils.FileInfo;

import java.util.ArrayList;

/**
 * Created by Lakshay on 27-02-2015.
 */
public class ImagesFoldersAdapter extends BaseAdapter {

    Context context;
    ArrayList<FileInfo> folders;

    public ImagesFoldersAdapter(Context context, ArrayList<FileInfo> files) {

        this.context = context;
        this.folders = files;
    }

    @Override
    public int getCount() {
        return folders.size();
    }

    @Override
    public Object getItem(int position) {
        return folders.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        FolderHolder holder;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.gallery_folder_layout, null);

            holder = new FolderHolder();
            holder.imageView = (ImageView) convertView.findViewById(R.id.ivFolderThumbnail);
            holder.countFiles = (TextView) convertView.findViewById(R.id.tvCount);
            holder.FolderName = (TextView) convertView.findViewById(R.id.tvFolderName);
            convertView.setTag(holder);
        }

        holder = (FolderHolder) convertView.getTag();
        FileInfo fileInfo = folders.get(position);
        if (fileInfo.getFileCount() > 0) {
            holder.countFiles.setText(fileInfo.getFileCount() + "");
            holder.countFiles.setVisibility(View.VISIBLE);
        } else {
            holder.countFiles.setVisibility(View.INVISIBLE);
        }
        holder.FolderName.setText(folders.get(position).getDisplayName().toString());

        Glide.with(context)
                .load("file://" + folders.get(position).getFilePath())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.default_placeholder)
                .centerCrop()
                .into(holder.imageView);
        return convertView;
    }
}
