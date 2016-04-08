package com.gcloud.gaadi.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.gallerylib.ParamsToDisplay;
import com.gallerylib.VwPagerActivity;
import com.gcloud.gaadi.R;
import com.gcloud.gaadi.utils.GCLog;

import java.util.ArrayList;

/**
 * Created by ankit on 30/12/14.
 */
public class StockImagesAdapter extends PagerAdapter {

    String modelVersion;
    private ArrayList<String> urls;
    private Context mContext;
    private LayoutInflater mInflater;

    public StockImagesAdapter(Context context, ArrayList<String> urls) {
        super();
        mContext = context;
        this.urls = urls;

        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public StockImagesAdapter(Context context, ArrayList<String> urls, String modelVersion) {
        super();
        mContext = context;
        this.urls = urls;
        this.modelVersion = modelVersion;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View view = mInflater.inflate(R.layout.network_image_view, container, false);
        ImageView imageView = (ImageView) view.findViewById(R.id.imageView);

        if (urls.size() == 0) {
            Glide.with(mContext)
                    .load(R.drawable.no_image_default_big)
//                    .placeholder(R.drawable.no_image_default_big)
                    .crossFade()
                    .centerCrop()
                    .into(imageView);

        } else {
            GCLog.e(urls.size() + "");

            Glide.with(mContext)
                    .load(urls.get(position))
                    .placeholder(R.drawable.image_load_default_big)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .crossFade()
                    .centerCrop()
                    .into(imageView);

        }
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParamsToDisplay params = new ParamsToDisplay();
                params.setImagesUrlList(urls);
                params.setMakeModelVersion(modelVersion);
                params.setImgSelectedPosition(position);
                Intent intent = VwPagerActivity.makeNewInstance(params, (Activity) mContext);
                if (urls.size() > 0) {
                    mContext.startActivity(intent);
                    ((Activity) mContext).overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
                }

            }
        });
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object o) {
        return (view == o);
    }

    @Override
    public int getCount() {
        return (urls.size() == 0) ? 1 : urls.size();
    }
}
