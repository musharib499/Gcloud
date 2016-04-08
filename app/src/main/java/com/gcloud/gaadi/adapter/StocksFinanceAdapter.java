package com.gcloud.gaadi.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.gcloud.gaadi.ApplicationController;
import com.gcloud.gaadi.R;
import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.model.CarItemModel;
import com.gcloud.gaadi.ui.Finance.FinanceFormsActivity;
import com.gcloud.gaadi.utils.CommonUtils;
import com.gcloud.gaadi.utils.GCLog;

import java.util.ArrayList;

/**
 * Created by ankitgarg on 13/05/15.
 */
public class StocksFinanceAdapter extends RecyclerView.Adapter<StocksFinanceAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<CarItemModel> mItems;

    public StocksFinanceAdapter(ArrayList<CarItemModel> items) {
        mItems = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View v = LayoutInflater.from(mContext).inflate(R.layout.layout_stock_finance_list_item, parent, false);

        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        if ("1".equals(mItems.get(position).getCertification_status())) {
            holder.trustMark.setVisibility(View.VISIBLE);
        } else {
            holder.trustMark.setVisibility(View.GONE);
        }

        if ((mItems.get(position).getImageIcon() != null) && !mItems.get(position).getImageIcon().trim().isEmpty()) {
            Glide.with(mContext)
                    .load(mItems.get(position).getImageIcon())
                    .placeholder(R.drawable.gcloud_placeholder)
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.stockImage);
        } else {
            Glide.with(mContext)
                    .load(R.drawable.no_image_default_small)
                    .centerCrop()
                    .into(holder.stockImage);
        }

        CarItemModel itemModel = mItems.get(position);
        holder.kmsDriven.setText(itemModel.getKm() + " kms");
        holder.tvModelYear.setText(itemModel.getMyear());
        holder.modelVersion.setText(itemModel.getModel());
        holder.make.setImageResource(ApplicationController.makeLogoMap.get(itemModel.getMake_id()));
       holder.FuleType.setText(itemModel.getFuel_type());
        holder.color.setBackgroundColor(Color.parseColor(itemModel.getColour()));
        holder.tvRegNo.setText(itemModel.getRegno());
       // holder.stockPrice.setText("₹ " + mItems.get(position).getPricefrom());
        CommonUtils.insertCommaIntoNumber(holder.stockPrice, mItems.get(position).getPricefrom(), "##,##,###");
        holder.item = mItems.get(position);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout viewStockListItem;
        public TextView stockPrice;
        public TextView tvRegNo;
        public ImageView stockImage;
        public TextView kmsDriven, leads;
        public TextView tvModelYear;
        public ImageView make, color;
        public TextView modelVersion;
        public ImageView trustMark;
        public CarItemModel item;
        public TextView FuleType;

        public ViewHolder(final View itemView) {
            super(itemView);
            viewStockListItem = (LinearLayout) itemView.findViewById(R.id.view_stock_list_item);
            stockImage = (ImageView) itemView.findViewById(R.id.ivStockImage);
            color = (ImageView) itemView.findViewById(R.id.color);
            stockPrice = (TextView) itemView.findViewById(R.id.tvStockPrice);
            make = (ImageView) itemView.findViewById(R.id.ivMakeLogo);
            modelVersion = (TextView) itemView.findViewById(R.id.stockModelVersion);
            //moreOptions = (ImageView) itemView.findViewById(R.id.moreOptions);
            tvModelYear = (TextView) itemView.findViewById(R.id.tvModelYear);
            kmsDriven = (TextView) itemView.findViewById(R.id.tvKmDriven);
            trustMark = (ImageView) itemView.findViewById(R.id.trustmark);
            tvRegNo = (TextView) itemView.findViewById(R.id.tvRegNo);
            FuleType=(TextView)itemView.findViewById(R.id.fuletype);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GCLog.e("view clicked: " + item.getUsedCarID());
                    //ApplicationController.getEventBus().post();

                    if (true/*item.getLoanStatus() == null || "".equals(item.getLoanStatus())*/) {
//                        Intent intent = new Intent(mContext, LoanOffersListActivity.class);
                        Intent intent = new Intent(mContext, FinanceFormsActivity.class);
                        intent.putExtra(Constants.MODEL_DATA, item);

                        mContext.startActivity(intent);
                    } else {
                        new AlertDialog.Builder(mContext)
                                .setTitle("Loan Already Applied on this Car")
                                .setMessage("To know the status please select it a day later")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // continue with delete
                                        dialog.dismiss();
                                    }
                                })
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                    }
                }
            });
        }
    }
}
