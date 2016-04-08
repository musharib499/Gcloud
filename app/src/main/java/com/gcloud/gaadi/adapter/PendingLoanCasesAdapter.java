package com.gcloud.gaadi.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.gcloud.gaadi.ApplicationController;
import com.gcloud.gaadi.R;
import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.model.LoanApplication;
import com.gcloud.gaadi.ui.Finance.FinanceReuploadActivity;
import com.gcloud.gaadi.utils.CommonUtils;

import java.util.ArrayList;



public class PendingLoanCasesAdapter extends RecyclerView.Adapter<PendingLoanCasesAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<LoanApplication> mItems;
    private LayoutInflater mInflater;

    public PendingLoanCasesAdapter(Context context, ArrayList<LoanApplication> items) {
        mContext = context;
        mItems = items;
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public PendingLoanCasesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.layout_loan_statuses_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.cardView = (CardView) view;
       // viewHolder.tvKmDriven = (TextView) view.findViewById(R.id.kmsDriven);
       // viewHolder.tvYear = (TextView) view.findViewById(R.id.modelYear);
        viewHolder.tvStockPrice = (TextView) view.findViewById(R.id.tv22);
        viewHolder.lbStockPrice = (TextView) view.findViewById(R.id.tv_label22);
        viewHolder.tvMakeModel = (TextView) view.findViewById(R.id.tv12);
        viewHolder.lbMakeModel = (TextView) view.findViewById(R.id.tv_label11);
        //viewHolder.ivStockImage = (ImageView) view.findViewById(R.id.stockImage);
        viewHolder.tvPriceText = (TextView) view.findViewById(R.id.tv22);
        viewHolder.tvStatusTag = (TextView) view.findViewById(R.id.tv02);
        viewHolder.ivBankLogo = (ImageView) view.findViewById(R.id.ivBankLogo);
        viewHolder.tvStatusTag.setVisibility(View.VISIBLE);

        //viewHolder.make = (ImageView) view.findViewById(R.id.makeLogo);
        //viewHolder.modelVersion = (TextView) view.findViewById(R.id.stockModelVersion);
        //viewHolder.trustMark = (ImageView) view.findViewById(R.id.trustmark);
        viewHolder.tvCustomarName = (TextView) view.findViewById(R.id.tv11);
        viewHolder.lbCustomarName = (TextView) view.findViewById(R.id.tv_label11);
        viewHolder.tvApplicationId = (TextView) view.findViewById(R.id.tv01);
        viewHolder.lbApplicationId = (TextView) view.findViewById(R.id.tv_label01);
        //viewHolder.tvRegNo = (TextView) view.findViewById(R.id.tvTenure);
        //  view.findViewById(R.id.llTenure).setVisibility(View.VISIBLE);
        viewHolder.tvRegNo = (TextView) view.findViewById(R.id.tv21);
        viewHolder.lbRegNo = (TextView) view.findViewById(R.id.tv_label21);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(PendingLoanCasesAdapter.ViewHolder holder, int position) {

        final LoanApplication loanCase = mItems.get(position);
        //holder.tvStockPrice.setText(Constants.RUPEES_SYMBOL +loanCase.getStockPrice());
        CommonUtils.insertCommaIntoNumber(holder.tvStockPrice, loanCase.getStockPrice(), "##,##,###");
        holder.lbStockPrice.setText(Constants.LOAN_AMOUNT_CAP);
//        holder.tvYear.setText(loanCase.getYear());
        //  holder.tvKmDriven.setText(loanCase.getKmDriven());

        // holder.make.setImageResource(ApplicationController.makeLogoMap.get(mItems.get(position).getMake()));
        holder.tvMakeModel.setText(loanCase.getMakeValue()+" "+loanCase.getModel());
        holder.lbMakeModel.setText(Constants.CAR_MODEL);
        // holder.tvPriceText.setText("Car Price : ");
        holder.tvRegNo.setText(loanCase.getRegNo());
        holder.lbRegNo.setText(Constants.REGISTRATION);

        holder.tvApplicationId.setText(loanCase.getApplicationId());
        if (loanCase.getCustomarName() != null) {
            holder.tvCustomarName.setText(loanCase.getCustomarName());
            holder.lbCustomarName.setText(Constants.NAME);
        }
        //  holder.trustMark.setVisibility(View.GONE);
        Drawable tempDrawable = ((Activity) mContext).getResources().getDrawable(R.drawable.round_text_view);
        LayerDrawable bubble = (LayerDrawable) tempDrawable;
        GradientDrawable background = (GradientDrawable) bubble.findDrawableByLayerId(R.id.round_text_background);

       /* if(loanCase.getBank_id() != null) {
            holder.ivBankLogo.setVisibility(View.VISIBLE);
            switch (Integer.parseInt(loanCase.getBank_id())) {
                case 1:
                    holder.ivBankLogo.setImageResource(R.drawable.hdfc);
                    break;
                case 2:
                    holder.ivBankLogo.setImageResource(R.drawable.axis_finance);
                    break;
                case 3:
                    holder.ivBankLogo.setImageResource(R.drawable.mahindra_finance);
                    break;
            }
        }
        else
        {
            holder.ivBankLogo.setVisibility(View.INVISIBLE);
        }*/

        if ((loanCase.getBank_logo()!= null) && !loanCase.getBank_logo().trim().isEmpty()) {
            holder.ivBankLogo.setVisibility(View.VISIBLE);
            Glide.with(mContext)
                    .load(loanCase.getBank_logo())

                    .crossFade()

                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.ivBankLogo);
            /*mHolder.stockImage.setDefaultImageResId(R.drawable.image_load_default_small);
            mHolder.stockImage.setErrorImageResId(R.drawable.no_image_default_small);
            mHolder.stockImage.setImageUrl(mItems.get(position).getImageIcon(), mImageLoader);*/

        } else {
            holder.ivBankLogo.setVisibility(View.INVISIBLE);
            /*mHolder.stockImage.setDefaultImageResId(R.drawable.no_image_default_small);*/

        }

       /* if (Constants.PENDING_FROM_OPS.equalsIgnoreCase(loanCase.getPendingStatus())) {
//            holder.ivStatusTag.setImageDrawable(mContext.getResources().getDrawable(R.drawable.inprocess));
            holder.tvStatusTag.setText("In Process");
//            background.setColor(((Activity) mContext).getResources().getColor(android.R.color.transparent));
            holder.tvStatusTag.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.rounded_borders_text_view_yellow));

            holder.tvStatusTag.setTextColor(mContext.getResources().getColor(R.color.Yellow));
           // holder.tvStatusTag.setBackgroundColor(mContext.getResources().getColor(R.color.finance_yellow));
        } else if (Constants.PENDING_FROM_USER.equalsIgnoreCase(loanCase.getPendingStatus())) {
            holder.tvStatusTag.setText("Action Required");
//            background.setColor(((Activity) mContext).getResources().getColor(android.R.color.transparent));
            holder.tvStatusTag.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.rounded_border_text_view));
//            holder.tvStatusTag.setBackground(background);
            holder.tvStatusTag.setTextColor(mContext.getResources().getColor(R.color.dark_orange));
           // holder.tvStatusTag.setBackgroundColor(mContext.getResources().getColor(R.color.orange));
        } else if (Constants.PENDING_FROM_BANK.equalsIgnoreCase(loanCase.getPendingStatus())) {
            holder.tvStatusTag.setText("Pending Approval");
            background.setColor(((Activity) mContext).getResources().getColor(android.R.color.transparent));
            holder.tvStatusTag.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.rounded_borders_text_view_blue));
            holder.tvStatusTag.setTextColor(mContext.getResources().getColor(R.color.Blue));
           // holder.tvStatusTag.setBackgroundColor(mContext.getResources().getColor(R.color.finance_blue));


            //yellow in process
            //action required is redb

        } else {
            GCLog.e("NOTHING PENDING STATUS");
        }*/

        switch (Integer.parseInt(loanCase.getLoanApprovalStatus()))
        {
            case 0:

                holder.tvStatusTag.setText(ApplicationController.loanStatus.get(0));
//            background.setColor(((Activity) mContext).getResources().getColor(android.R.color.transparent));
                holder.tvStatusTag.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.rounded_borders_text_view_yellow));

                holder.tvStatusTag.setTextColor(mContext.getResources().getColor(R.color.Yellow));
                // holder.tvStatusTag.setBackgroundColor(mContext.getResources().getColor(R.color.finance_yellow));
                break;
            case 1:
                holder.tvStatusTag.setText(ApplicationController.loanStatus.get(1));
                background.setColor(((Activity) mContext).getResources().getColor(android.R.color.transparent));
                holder.tvStatusTag.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.rounded_border_text_view));
                holder.tvStatusTag.setTextColor(mContext.getResources().getColor(R.color.dark_orange));
                // holder.tvStatusTag.setBackgroundColor(mContext.getResources().getColor(R.color.finance_blue));
                break;
            case -2:
                holder.tvStatusTag.setText(ApplicationController.loanStatus.get(-2));
//            background.setColor(((Activity) mContext).getResources().getColor(android.R.color.transparent));
                holder.tvStatusTag.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.rounded_border_wd_red_color));
//            holder.tvStatusTag.setBackground(background);
                holder.tvStatusTag.setTextColor(mContext.getResources().getColor(R.color.Action_red));
                // holder.tvStatusTag.setBackgroundColor(mContext.getResources().getColor(R.color.orange));
                break;
        }

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, FinanceReuploadActivity.class);
                Activity activity = (Activity) mContext;
                intent.putExtra(Constants.FINANCE_APP_ID, loanCase.getApplicationId());
                intent.putExtra(Constants.CUSTOMER_ID, loanCase.getCustomarId());
                intent.putExtra(Constants.FINANCE_CAR_ID,loanCase.getCarId());
                activity.startActivity(intent);
            }
        });

       /* if ((mItems.get(position).getImageIcon() != null) && !mItems.get(position).getImageIcon().trim().isEmpty()) {
            Glide.with(mContext)
                    .load(mItems.get(position).getImageIcon())
                    .placeholder(R.drawable.image_load_default_small)
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.ivStockImage);
        } else {
            Glide.with(mContext)
                    .load(R.drawable.no_image_default_small)
                    .centerCrop()
                    .into(holder.ivStockImage);
        }*/
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
        }

        //ImageView ivStockImage, make, trustMark;
        TextView tvStatusTag, tvRegNo, lbRegNo;
        TextView tvMakeModel, lbMakeModel, tvYear, tvKmDriven, tvStockPrice, lbStockPrice, modelVersion, tvCustomarName, lbCustomarName, tvPriceText, lbPriceText, tvApplicationId, lbApplicationId;
        CardView cardView;
        ImageView ivBankLogo;
    }
}
