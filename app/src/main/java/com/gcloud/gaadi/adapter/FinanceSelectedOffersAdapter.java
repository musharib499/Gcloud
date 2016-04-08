package com.gcloud.gaadi.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.gcloud.gaadi.R;
import com.gcloud.gaadi.model.CarItemModel;
import com.gcloud.gaadi.model.Finance.FInanceOfferSelectedRequestModel;
import com.gcloud.gaadi.utils.CommonUtils;

import java.util.ArrayList;

/**
 * Created by shivani on 5/2/16.
 */
public class FinanceSelectedOffersAdapter extends RecyclerView.Adapter<FinanceSelectedOffersAdapter.CustomViewHolder> {
    public ArrayList<FInanceOfferSelectedRequestModel> mFInanceOfferSelectedRequestModel=new ArrayList<>();
    CarItemModel carItemModel;
    Activity mActivity;

    public FinanceSelectedOffersAdapter(Activity mActivity, ArrayList<FInanceOfferSelectedRequestModel> FInanceOfferSelectedRequestModel,  CarItemModel carItemModel) {
        this.mActivity=mActivity;
        this.mFInanceOfferSelectedRequestModel=FInanceOfferSelectedRequestModel;
        this.carItemModel=carItemModel;



    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mActivity).inflate(R.layout.finance_selected_offer_tuple_layout, parent, false);
        CustomViewHolder viewholder = new CustomViewHolder(view);
        return viewholder;
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        if ((mFInanceOfferSelectedRequestModel.get(position).getBank_URL()!= null) && !mFInanceOfferSelectedRequestModel.get(position).getBank_URL().trim().isEmpty()) {
            holder.ivBankLogo.setVisibility(View.VISIBLE);
            Glide.with(mActivity)
                    .load(mFInanceOfferSelectedRequestModel.get(position).getBank_URL())

                    .crossFade()

                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.ivBankLogo);


        } else {
            holder.ivBankLogo.setVisibility(View.GONE);
            //mHolder.stockImage.setDefaultImageResId(R.drawable.no_image_default_small);

        }
        CommonUtils.insertCommaIntoNumber(holder.tvAppliedLoanAmount, mFInanceOfferSelectedRequestModel.get(position).getApplied_loan_amount(), "##,##,###");
        holder.tvAppliedLoanEmi.setText(mFInanceOfferSelectedRequestModel.get(position).getApplied_emi());
        holder.tvAppliedLoanYear.setText(mFInanceOfferSelectedRequestModel.get(position).getApplied_tenure());

    }

    @Override
    public int getItemCount() {
        return mFInanceOfferSelectedRequestModel.size();
    }

    class CustomViewHolder extends RecyclerView.ViewHolder{
        ImageView ivBankLogo;
        TextView  tvAppliedLoanAmount, tvAppliedLoanYear, tvAppliedLoanEmi;

        public CustomViewHolder(View itemView) {
            super(itemView);
            ivBankLogo=(ImageView) itemView.findViewById(R.id.ivBankLogo);
            tvAppliedLoanAmount=(TextView) itemView.findViewById(R.id.tvAppliedLoanAmount);
            tvAppliedLoanYear=(TextView) itemView.findViewById(R.id.tvAppliedLoanYear);
            tvAppliedLoanEmi=(TextView) itemView.findViewById(R.id.tvAppliedLoanEmi);



        }
    }

}
