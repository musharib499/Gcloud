package com.gcloud.gaadi.rsa.RSAAdapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.gcloud.gaadi.ApplicationController;
import com.gcloud.gaadi.R;
import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.rsa.RSACarSelectedEvent;
import com.gcloud.gaadi.rsa.RSAModel.RSACarDetailsModel;
import com.gcloud.gaadi.utils.CommonUtils;
import com.gcloud.gaadi.utils.GCLog;

import java.util.ArrayList;

/**
 * Created by ankitgarg on 21/05/15.
 */
public class RSACarsAdapter extends RecyclerView.Adapter<RSACarsAdapter.RSACarsViewHolder> {

    private Context mContext;
    private ArrayList<RSACarDetailsModel> mItems;
    private LayoutInflater mInflater;

    public RSACarsAdapter(Context context, ArrayList<RSACarDetailsModel> items) {
        mContext = context;
        mItems = items;
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public RSACarsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = mInflater.inflate(R.layout.layout_rsa_list_item, parent, false);
        RSACarsViewHolder viewHolder = new RSACarsViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RSACarsViewHolder holder, int position) {

        RSACarDetailsModel carData = mItems.get(position);

        if (ApplicationController.makeLogoMap.get(Integer.parseInt(carData.getMakeid())) != 0) {
            holder.makeLogo.setVisibility(View.VISIBLE);
            holder.makeLogo.setImageDrawable(
                    mContext.getResources().getDrawable(
                            ApplicationController.makeLogoMap.get(Integer.parseInt(carData.getMakeid()))));
        } else {
            holder.makeLogo.setVisibility(View.INVISIBLE);
        }

        holder.modelVersion.setText(carData.getModelName() + " " + carData.getCarVersion());

        holder.stockPrice.setText(Constants.RUPEES_SYMBOL + " " + carData.getStockPrice());

        holder.regNo.setText(CommonUtils.getReplacementString(
                mContext,
                R.string.registrationNumber,
                carData.getRegistrationNumber()));
        holder.kmsDriven.setText(carData.getKms() + " kms");
        holder.modelYear.setText(carData.getModelYear());
        holder.fuelType.setText(carData.getFuelType());

        if (carData.getImageIcon() != null && !carData.getImageIcon().isEmpty()) {
            Glide.with(mContext)
                    .load(carData.getImageIcon())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .centerCrop()
                    .placeholder(R.drawable.image_load_default_small)
                    .into(holder.stockImage);
        } else {
            Glide.with(mContext)
                    .load(R.drawable.no_image_default_small)
                    .centerCrop()
                    .into(holder.stockImage);
        }

        holder.item = mItems.get(position);

        GradientDrawable bgShape = (GradientDrawable) holder.color.getBackground();
        int colorCode = Color.parseColor("#FFFFFF");
        if (mItems.get(position).getHexCode() != null && !mItems.get(position).getHexCode().isEmpty()) {
            try {
                GCLog.e("color: " + mItems.get(position).getHexCode());
                colorCode = Color.parseColor(mItems.get(position).getHexCode());
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } finally {
                bgShape.setColor(colorCode);
            }
        } else {
            bgShape.setColor(colorCode);
        }
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public class RSACarsViewHolder extends RecyclerView.ViewHolder {

        public RelativeLayout viewStockListItem, imageLayout;
        public TextView stockPrice;
        public ImageView stockImage;
        public TextView kmsDriven;
        public TextView modelYear;
        public TextView fuelType;
        public ImageView makeLogo, color;
        public LinearLayout leadsLayout;
        public TextView modelVersion;
        public TextView colorValue;
        public ImageView trustMark;
        public TextView regNo;

        private RSACarDetailsModel item;

        public RSACarsViewHolder(View itemView) {
            super(itemView);

            viewStockListItem = (RelativeLayout) itemView.findViewById(R.id.view_stock_list_item);
            stockImage = (ImageView) itemView.findViewById(R.id.stockImage);
            color = (ImageView) itemView.findViewById(R.id.color);
            imageLayout = (RelativeLayout) itemView.findViewById(R.id.imageLayout);
            stockPrice = (TextView) itemView.findViewById(R.id.stockPrice);
            makeLogo = (ImageView) itemView.findViewById(R.id.makeLogo);
            modelVersion = (TextView) itemView.findViewById(R.id.stockModelVersion);
            //moreOptions = (ImageView) itemView.findViewById(R.id.moreOptions);
            modelYear = (TextView) itemView.findViewById(R.id.modelYear);
            fuelType = (TextView) itemView.findViewById(R.id.fuelType);
            colorValue = (TextView) itemView.findViewById(R.id.colorValue);
            regNo = (TextView) itemView.findViewById(R.id.reg_no);
            kmsDriven = (TextView) itemView.findViewById(R.id.kmsDriven);
            leadsLayout = (LinearLayout) itemView.findViewById(R.id.leadsLayout);
            trustMark = (ImageView) itemView.findViewById(R.id.trustmark);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GCLog.e("view clicked: " + item.getStockId());
                    ApplicationController.getEventBus().post(new RSACarSelectedEvent(item));

                    //ApplicationController.getEventBus().post();
                    /*Intent intent = new Intent(mContext, LoanOffersListActivity.class);
                    intent.putExtra(Constants.MODEL_DATA, item);
                    mContext.startActivity(intent);*/
                }
            });
        }
    }
}
