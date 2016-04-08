package com.gcloud.gaadi.insurance;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.gcloud.gaadi.ApplicationController;
import com.gcloud.gaadi.R;
import com.gcloud.gaadi.model.FormItem;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Gaurav on 20-07-2015.
 */
public class InsuranceForm30Adapter extends BaseAdapter implements View.OnClickListener {

    private Context context;
    private int viewNumber;
    private HashMap<Integer, FormItem> imagePathMap;
    private AllImagesLoaded listener;

    public InsuranceForm30Adapter(Context context,
                                  int viewNumber,
                                  AllImagesLoaded listener) {
        this.context = context;
        this.viewNumber = viewNumber;
        this.listener = listener;
        imagePathMap = new HashMap<>();
    }

    @Override
    public int getCount() {
        return viewNumber;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.insurance_form30_image_layout, viewGroup, false);
        }

        ImageView image = (ImageView) view.findViewById(R.id.form_30_image);
        Glide.with(ApplicationController.getInstance())
                .load(android.R.color.transparent)
                .into(image);
        ImageView delete = (ImageView) view.findViewById(R.id.form_30_image_delete);
        delete.setVisibility(View.GONE);
        TextView retry = (TextView) view.findViewById(R.id.form_30_image_retry);
        retry.setVisibility(View.GONE);
        ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.form_30_image_progressbar);
        progressBar.setVisibility(View.GONE);
        TextView imageName = (TextView) view.findViewById(R.id.image_name);
        imageName.setText("Page " + (i + 1));

        if (imagePathMap.get(i) != null) {
            FormItem item = imagePathMap.get(i);
            Glide.with(ApplicationController.getInstance())
                    .load(item.getImagePath().startsWith("http")? item.getImagePath(): "file://" + item.getImagePath())
                    .into(image);
            if (!item.getImagePath().startsWith("http") && item.getShowRetry())
                retry.setVisibility(View.VISIBLE);
            if (!item.getImagePath().startsWith("http") && item.getShowProgressBar())
                progressBar.setVisibility(View.VISIBLE);
            item.setRetry(retry);
            item.setImageProgress(progressBar);
            if (!item.getImagePath().startsWith("http")) {
                delete.setVisibility(View.VISIBLE);
                delete.setTag(i);
                delete.setOnClickListener(this);
            }
        }
        return view;
    }

    public void setImagePathMap(int index, String path) {
        FormItem item = new FormItem();
        item.setImagePath(path);
        imagePathMap.put(index, item);
        notifyDataSetChanged();
        if (imagePathMap.size() < viewNumber)
            listener.onAllImagesLoaded(R.drawable.tick);
        else
            listener.onAllImagesLoaded(R.drawable.tick_active);
    }

    public ArrayList<FormItem> getImagePathMap() {
        ArrayList<FormItem> pathList = new ArrayList<>();
        for (int i = 0; i < viewNumber; i++) {
            if (imagePathMap.get(i) != null
                    && !imagePathMap.get(i).getImagePath().startsWith("http")) {
                pathList.add(imagePathMap.get(i));
            } else {
                pathList.add(null);
            }
        }
        return pathList;
    }

    public int getImagePathMapSize() {
        return imagePathMap.size();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.form_30_image_delete:
                int index = (int) view.getTag();
                imagePathMap.remove(index);
                listener.onAllImagesLoaded(R.drawable.tick);
                notifyDataSetChanged();
                break;
        }
    }

    public interface AllImagesLoaded {
        void onAllImagesLoaded(int resId);
    }

    /*private int getExifRotation(File pictureFile) {
        ExifInterface ei = null;
        try {
            ei = new ExifInterface(pictureFile.getPath());
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    return 90;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    return 180;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    return 270;
                case ExifInterface.ORIENTATION_UNDEFINED:
                    return 90;
                default:
                    return 0;
                // etc.
            }
        } catch (IOException e) {
            Log.e(Constants.TAG, "Exception Message : " + e.getMessage());
        }
        return 0;
    }*/
}
