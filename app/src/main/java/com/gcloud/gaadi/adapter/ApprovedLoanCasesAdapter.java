package com.gcloud.gaadi.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
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
import com.gcloud.gaadi.model.LoanApplication;
import com.gcloud.gaadi.ui.Finance.FinanceReuploadActivity;
import com.gcloud.gaadi.utils.CommonUtils;

import java.util.ArrayList;


public class ApprovedLoanCasesAdapter extends RecyclerView.Adapter<ApprovedLoanCasesAdapter.ViewHolder> {

    private ArrayList<LoanApplication> mItems;
    private Context mContext;
    private LayoutInflater mInflater;

    public ApprovedLoanCasesAdapter(Context context, ArrayList<LoanApplication> items) {
        if(context == null)
            mContext = ApplicationController.getInstance();
        else
            mContext = context;
        mItems = items;
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.layout_loan_statuses_item, parent, false);
        CardView cardView = (CardView) view;
        ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.cardView = cardView;
        viewHolder.tvApplicationId = (TextView) view.findViewById(R.id.tv01);
        viewHolder.lbApplicationId= (TextView) view.findViewById(R.id.tv_label01);
        viewHolder.lbCustomarName= (TextView) view.findViewById(R.id.tv_label11);
        viewHolder.tvCustomarName = (TextView) view.findViewById(R.id.tv11);
        viewHolder.tvStockPrice = (TextView) view.findViewById(R.id.tv12);
        viewHolder.lbPriceText = (TextView) view.findViewById(R.id.tv_label12);
        viewHolder.tvMakeModel = (TextView) view.findViewById(R.id.tv21);
        viewHolder.lbMakeModel=(TextView)view.findViewById(R.id.tv_label21);
        viewHolder.tvTenure = (TextView) view.findViewById(R.id.tv22);
        viewHolder.lbTenure= (TextView) view.findViewById(R.id.tv_label22);
        viewHolder.tvRegistration=(TextView)view.findViewById(R.id.tv31);
        viewHolder.lbRegistration=(TextView)view.findViewById(R.id.tv_label31);
        viewHolder.lbEMI= (TextView) view.findViewById(R.id.tv_label32);
        viewHolder.tvEMI= (TextView) view.findViewById(R.id.tv32);
        viewHolder.ivBankLogo = (ImageView) view.findViewById(R.id.ivBankLogo);
        viewHolder.tvStatusTag = (TextView) view.findViewById(R.id.tv02);
        viewHolder.tvStatusTag.setVisibility(View.VISIBLE);
        /*visible view in this list*/
        viewHolder.tvRegistration.setVisibility(View.VISIBLE);
        viewHolder.lbRegistration.setVisibility(View.VISIBLE);
        viewHolder.tvEMI.setVisibility(View.VISIBLE);
        viewHolder.lbEMI.setVisibility(View.VISIBLE);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // TODO fill in the values

        final LoanApplication loanCase = mItems.get(position);
       // holder.tvStockPrice.setText(Constants.RUPEES_SYMBOL +loanCase.getLoanAmount());
        CommonUtils.insertCommaIntoNumber(holder.tvStockPrice, loanCase.getLoanAmount(), "##,##,###");

        holder.lbApplicationId.setText(Constants.APPLICATION_ID);
        holder.lbCustomarName.setText(Constants.NAME);
        holder.lbMakeModel.setText(Constants.CAR_MODEL);

        holder.lbRegistration.setText(Constants.REGISTRATION);
        holder.lbPriceText.setText( Constants.LOAN_AMOUNT_CAP);
        holder.lbEMI.setText(Constants.EMI);
        holder.tvApplicationId.setText(loanCase.getApplicationId());
        holder.tvRegistration.setText(loanCase.getRegNo());
        holder.lbTenure.setText( Constants.LOAN_TENURE);

        holder.tvMakeModel.setText(loanCase.getMakeValue()+" "+loanCase.getModel());
        if(loanCase.getApprovedTenure()!=null)
        holder.tvTenure.setText(loanCase.getApprovedTenure());
        //holder.tvEMI.setText(Constants.RUPEES_SYMBOL+ loanCase.getApprovedEmi());
        CommonUtils.insertCommaIntoNumber(holder.tvEMI, loanCase.getApprovedEmi(), "##,##,###");
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

      /*  else
        {
            holder.ivBankLogo.setVisibility(View.INVISIBLE);
        }*/

        switch (Integer.parseInt(loanCase.getLoanApprovalStatus()))
        {
            case 2:

                holder.tvStatusTag.setText(ApplicationController.loanStatus.get(2));
//            background.setColor(((Activity) mContext).getResources().getColor(android.R.color.transparent));
                holder.tvStatusTag.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.rounded_border_wd_green_color));

                holder.tvStatusTag.setTextColor(mContext.getResources().getColor(R.color.Approved_green));
                // holder.tvStatusTag.setBackgroundColor(mContext.getResources().getColor(R.color.finance_yellow));
                break;
            case 4:
                holder.tvStatusTag.setText(ApplicationController.loanStatus.get(4));
      //          background.setColor(((Activity) mContext).getResources().getColor(android.R.color.transparent));
                holder.tvStatusTag.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.rounded_borders_text_view_blue));
                holder.tvStatusTag.setTextColor(mContext.getResources().getColor(R.color.Blue));
                // holder.tvStatusTag.setBackgroundColor(mContext.getResources().getColor(R.color.finance_blue));
                break;

        }

        if(loanCase.getCustomarName()!=null)
            holder.tvCustomarName.setText(loanCase.getCustomarName());

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, FinanceReuploadActivity.class);
                Activity activity = (Activity) mContext;
                intent.putExtra(Constants.FINANCE_APP_ID, loanCase.getApplicationId());
                intent.putExtra(Constants.FINANCE_CAR_ID,loanCase.getCarId());
                intent.putExtra(Constants.CUSTOMER_ID, loanCase.getCustomarId());
                activity.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);
        }

        ImageView ivStockImage,make,trustMark, ivBankLogo;
        TextView tvMakeModel,lbMakeModel,tvRegistration,tvEMI,lbEMI, lbRegistration, tvYear,lb , tvKmDriven, tvStockPrice, modelVersion , tvCustomarName,lbCustomarName, lbPriceText, tvApplicationId,lbApplicationId , tvTenure,lbTenure, tvRoi, tvRoiText, tvStatusTag;
        LinearLayout llTenure, llRoi , llKm;
        CardView cardView;
    }
 }
