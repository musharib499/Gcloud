package com.gcloud.gaadi.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.gcloud.gaadi.R;
import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.model.PhotoInfo;
import com.gcloud.gaadi.ui.PhotoUploadActivity;
import com.gcloud.gaadi.ui.StockAddActivity;
import com.gcloud.gaadi.utils.GCLog;

import java.util.ArrayList;
import java.util.HashMap;


public class UploadPhotosGridViewAdapter extends BaseAdapter implements OnClickListener {

    private LayoutInflater mInfalter;
    private Activity mContext;
    private ArrayList<PhotoInfo> mList = new ArrayList<PhotoInfo>();
    private Matrix mMatrix = new Matrix();
    private ArrayList<String> uploadedImages;
    private String stockId;


    public UploadPhotosGridViewAdapter(Activity c, ArrayAdapter<PhotoInfo> list) {
        mInfalter = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = c;

    }


    public UploadPhotosGridViewAdapter(Activity c) {
        this(c, null);
        mContext = c;
        PhotoUploadActivity activity = (PhotoUploadActivity) c;
        this.uploadedImages = StockAddActivity.imagesList;
        this.stockId = activity.propId;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public PhotoInfo getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public ArrayList<PhotoInfo> getList() {
        return mList;
    }

    public boolean contains(PhotoInfo photoInfo) {

        if (photoInfo.getPath().startsWith("http://")) {
            return false;
        }
        return mList.contains(photoInfo);
    }

    public void addAll(ArrayList<PhotoInfo> files) {

        try {
            this.mList.clear();
            this.mList.addAll(files);
        } catch (Exception e) {
            e.printStackTrace();
        }

        notifyDataSetChanged();
    }

    public void add(PhotoInfo photoInfo) {
        try {
            mList.add(photoInfo);
        } catch (Exception e) {
        }
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final MyViewHolder holder;
        if (convertView == null) {

            convertView = mInfalter.inflate(R.layout.add_photos_imagegrid, null);
            holder = new MyViewHolder();
            holder.imgView = (RelativeLayout) convertView.findViewById(R.id.container);
            holder.imgQueue = (ImageView) convertView.findViewById(R.id.im_photo);
            //holder.progressPhotoImage = (ProgressBar) convertView.findViewById(R.id.progressPhotoImage);
            holder.rmvBt = (ImageButton) convertView.findViewById(R.id.rmvbt);
            holder.rmvBt.setOnClickListener(this);
            convertView.setTag(holder);

        } else {
            holder = (MyViewHolder) convertView.getTag();
        }

        PhotoInfo photoInfo = mList.get(position);
        /*holder.imgView.setTag(position);
        holder.progressPhotoImage.setTag(position);*/
        holder.rmvBt.setTag(position);
        //holder.imgQueue.setTag(position);

        PhotoInfo prePhotoInfo = (PhotoInfo) holder.imgView.findViewById(R.id.im_photo).getTag(R.id.TAG_PHOTO_INFO);

        if (prePhotoInfo != null && prePhotoInfo.equals(photoInfo))
            return convertView;

        holder.imgView.findViewById(R.id.im_photo).setTag(R.id.TAG_PHOTO_INFO, photoInfo);

        if (photoInfo.getPath().startsWith("http://")) {
            Glide.with(mContext)
                    .load(photoInfo.getPath())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.image_load_default_small)
                    .crossFade()
                    .into(holder.imgQueue);

        } else if (photoInfo.getPath().startsWith("https://")) {
            Glide.with(mContext)
                    .load(photoInfo.getPath())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.image_load_default_small)
                    .crossFade()
                    .into(holder.imgQueue);

        } else {
            Glide.with(mContext)
                    .load("file://" + photoInfo.getPath())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.image_load_default_small)
                    .crossFade()
                    .into(holder.imgQueue);
        }

        return convertView;
    }

    public Bitmap rotateBitmap(Bitmap source, float angle) {
        mMatrix.setRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), mMatrix, true);
    }

    public void clear() {
        mList.clear();
        notifyDataSetChanged();
    }

    public void remove(int pos) {

        if ((mList != null) && !mList.isEmpty()) {
            if ((getItem(pos) != null) && (mList.get(pos) != null)) {
                mList.remove(pos);
                notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rmvbt:
                final int pos = Integer.parseInt(v.getTag().toString());
                final AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
                dialog.setTitle("Alert");
                dialog.setMessage("Are you sure you want to delete this photo?");
                dialog.setCancelable(false);

                dialog.setPositiveButton("Delete", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        final String imagePath = mList.get(pos).getPath();
                        GCLog.e(imagePath);
                        String propId = mList.get(pos).getPropId();
                        mList.remove(pos);
                        GCLog.e(propId);

                        HashMap<String, String> params = new HashMap<String, String>();
                        params.put(Constants.CAR_ID, propId);
                        params.put(Constants.IMAGE_NAME, imagePath);
                        if ((StockAddActivity.imagesList != null) && StockAddActivity.imagesList.contains(imagePath)) {
                            GCLog.e("Make A request to delete photo");
                          /*  DeletePhotoRequest photoRequest = new DeletePhotoRequest(
                                    mContext,
                                    Request.Method.POST, Constants.getWebServiceURL(mContext),
                                    new Response.Listener<InventoryImageDeleteResponse>() {
                                        @Override
                                        public void onResponse(InventoryImageDeleteResponse response) {

                                            if (response != null && response.getResponses().size() > 0) {
                                                //GCLog.e(response.toString());
                                                for (GeneralResponse res : response.getResponses()) {
                                                    if ("T".equals(res.getStatus())) {
                                                        StockAddActivity.imagesList.remove(res.getMessage());
                                                    }
                                                }
                                            } else {
                                                GCLog.e("Response Null");
                                            }
                                        }
                                    },
                                    new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            GCLog.e("Photo Delete Error");
                                        }
                                    }, params);
                            ApplicationController.getInstance().addToRequestQueue(photoRequest);*/
                        }
                        notifyDataSetChanged();
                        return;
                    }
                });

                dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.dismiss();
                        return;
                    }
                });

                dialog.show();

                break;


        }
    }

    public class MyViewHolder {
        RelativeLayout imgView;
        ImageView imgQueue;
        ImageButton rmvBt;
    }
}
