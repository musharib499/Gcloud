package com.gallerylib.adapters;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.gallerylib.R;
import com.gallerylib.TouchImageView;

import java.util.ArrayList;

/**
 * Created by Priya on 20-03-2015.
 */
public class VwPagerAdapter extends PagerAdapter {


    private ArrayList<String> urls;
    private Context mContext;
    private LayoutInflater mInflater;


    public VwPagerAdapter(Context context, ArrayList<String> urls) {
        super();
        mContext = context;
        this.urls = urls;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
      /*  View view = mInflater.inflate(R.layout.network_image_view, container, false);
        ImageView imageView = (ImageView) view.findViewById(R.id.imageView);*/
        TouchImageView imageView = new TouchImageView(container.getContext());


        if (urls.size() == 0) {
            Glide.with(mContext)
                    .load(R.drawable.no_image_default_big)
//                    .placeholder(R.drawable.no_image_default_big)
                    .crossFade()
                    .centerCrop()
                    .into(imageView);

        } else {
            Log.e("Url at position  : ", "" + urls.get(position));

            Glide.with(mContext)
                    .load(urls.get(position))
                    .placeholder(R.drawable.image_load_default_small)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .crossFade()
                    .fitCenter()
                    .into(imageView);

        }
        // container.addView(view);
        container.addView(imageView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        return imageView;
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
