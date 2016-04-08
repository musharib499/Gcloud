package com.imageuploadlib.Adapters;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.imageuploadlib.R;
import com.imageuploadlib.Utils.Constants;

/**
 * Created by ankitgarg on 09/04/15.
 */
public class CustomGalleryCursorAdapter extends CursorAdapter {

    private LayoutInflater mInflater;
    private Context mContext;
    private Cursor mCursor;
    private ViewHolder holder;

    public CustomGalleryCursorAdapter(Context context, Cursor c) {
        super(context, c);
        mContext = context;
        mCursor = c;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = mInflater.inflate(R.layout.select_files, parent, false);
        holder = new ViewHolder();

        holder.selectedImage = (ImageView) view.findViewById(R.id.imageSelected);
        holder.transparentLayer = (ImageView) view.findViewById(R.id.vTransparentLayer);
        holder.selection_view = (ImageView) view.findViewById(R.id.selection_view);

        view.setTag(holder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        holder = (ViewHolder) view.getTag();

        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        try {
            String bucket = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME));
            Log.e(Constants.TAG, "Bucket : " + bucket);
        } catch (Exception e) {
            Log.e(Constants.TAG, e.getMessage());
        }

        //Log.e(Constants.TAG, "photo path: " + path);

        Glide.with(mContext)
                .load(path)
                .placeholder(R.drawable.image_load_default_small)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .into(holder.selectedImage);

    }


    private class ViewHolder {
        ImageView selectedImage;
        ImageView transparentLayer;
        ImageView selection_view;
    }
}
