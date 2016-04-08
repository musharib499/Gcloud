package com.gcloud.gaadi.adapter;

import android.app.Activity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.gcloud.gaadi.R;
import com.gcloud.gaadi.model.Finance.BankOffers;
import com.gcloud.gaadi.model.Finance.FInanceOfferSelectedRequestModel;
import com.gcloud.gaadi.model.Finance._12M_tenure;
import com.gcloud.gaadi.model.Finance._24M_tenure;
import com.gcloud.gaadi.model.Finance._36M_tenure;
import com.gcloud.gaadi.model.Finance._48M_tenure;
import com.gcloud.gaadi.model.Finance._60M_tenure;
import com.gcloud.gaadi.utils.CommonUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * Created by Lakshay on 08-09-2015.
 */
public class FinanceOffersAdapter extends RecyclerView.Adapter<FinanceOffersAdapter.CustomViewHolder>  {


    public LinkedList<BankOffers> mList;
    public HashMap<String, FInanceOfferSelectedRequestModel> modelList = new HashMap<>();
    public ArrayList<FInanceOfferSelectedRequestModel> list=new ArrayList<>();
    int currentTennure;
    int principal, code;
    int amount;
    double roi, emi;
    CustomViewHolder holder;
    _12M_tenure _12monthTenure;
    _24M_tenure _24monthTenure;
    _36M_tenure _36monthTenure;
    _48M_tenure _48monthTenure;
    _60M_tenure _60monthTenure;
    private Activity mActivity;


    public FinanceOffersAdapter(Activity fragmentActivity, LinkedList<BankOffers> mBankOfferses, int currentTennure, int principal, int code) {
        this.mActivity=fragmentActivity;
        this.mList=mBankOfferses;
        this.currentTennure=currentTennure;
        this.principal=principal;
        this.code=code;
    }


    @Override

    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mActivity).inflate(R.layout.finance_offers_from_multiple_bank_tuple, null);
        CustomViewHolder viewholder = new CustomViewHolder(view);
        return viewholder;
    }

    @Override
    public void onBindViewHolder(final CustomViewHolder holder, final int position) {
        if ((mList.get(position).getBank_logo()!= null) && !mList.get(position).getBank_logo().trim().isEmpty()) {
            holder.imgBankView.setVisibility(View.VISIBLE);
            Glide.with(mActivity).load(mList.get(position).getBank_logo()).crossFade().diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.imgBankView);
        } else {
            holder.imgBankView.setVisibility(View.INVISIBLE);
        }
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mList.get(position).setIsChecked(true);
                } else {
                    mList.get(position).setIsChecked(false);
                }

            }
        });

        if(mList.get(position).isChecked()){
            holder.checkBox.setChecked(true);
        }
        else{
            holder.checkBox.setChecked(false);
        }



        _12monthTenure = mList.get(position).get_12month_tenure();
        _24monthTenure = mList.get(position).get_24month_tenure();
        _36monthTenure = mList.get(position).get_36month_tenure();
        _48monthTenure = mList.get(position).get_48month_tenure();
        _60monthTenure = mList.get(position).get_60month_tenure();



        switch (currentTennure) {

            case 2:
                if(_24monthTenure!=null) {
                    if(code==0) {
                        principal = Integer.parseInt(mList.get(position).get_24month_tenure().getMax_eligibility());
                    }
                    holder.greyedoutView.setVisibility(View.GONE);
                    roi = Double.parseDouble(mList.get(position).get_24month_tenure().getFixed_roi());
                    holder.tvRoi.setText(mList.get(position).get_24month_tenure().getFixed_roi() + "%");
                    emi = Math.floor(CommonUtils.calculateLoanEmi(principal, roi, 2 * 12));
                    CommonUtils.insertCommaIntoNumber(holder.tvEmi, new DecimalFormat("#").format(emi), "##,##,###");
                    CommonUtils.insertCommaIntoNumber(holder.tvMax, _24monthTenure.getMax_eligibility(), "##,##,###");
                    if (principal > Integer.parseInt(_24monthTenure.getMax_eligibility())) {
                        holder.greyedoutView.setVisibility(View.VISIBLE);

                    }
                }else{
                    holder.greyedoutView.setVisibility(View.VISIBLE);
                    if(holder.checkBox.isChecked()) {
                        holder.checkBox.setChecked(false);
                    }
                    holder.tvRoi.setText("0%");
                    CommonUtils.insertCommaIntoNumber(holder.tvEmi, new DecimalFormat("#").format(0), "##,##,###");
                    CommonUtils.insertCommaIntoNumber(holder.tvMax, "0", "##,##,###");
                }
                break;

            case 4:
                if(_48monthTenure!=null) {
                    if(code==0) {
                        principal = Integer.parseInt(mList.get(position).get_48month_tenure().getMax_eligibility());
                    }
                    holder.greyedoutView.setVisibility(View.GONE);
                    roi = Double.parseDouble(mList.get(position).get_48month_tenure().getFixed_roi());
                    holder.tvRoi.setText(mList.get(position).get_48month_tenure().getFixed_roi()+"%");
                    emi = Math.floor(CommonUtils.calculateLoanEmi(principal, roi, 4 * 12));
                    CommonUtils.insertCommaIntoNumber(holder.tvEmi, new DecimalFormat("#").format(emi), "##,##,###");
                    CommonUtils.insertCommaIntoNumber(holder.tvMax, _48monthTenure.getMax_eligibility(), "##,##,###");
                    if (principal > Integer.parseInt(_48monthTenure.getMax_eligibility())) {
                        holder.greyedoutView.setVisibility(View.VISIBLE);

                    }


                }else{
                    holder.greyedoutView.setVisibility(View.VISIBLE);
                    if(holder.checkBox.isChecked()) {
                        holder.checkBox.setChecked(false);
                    }
                    holder.tvRoi.setText("0%");
                    CommonUtils.insertCommaIntoNumber(holder.tvEmi, new DecimalFormat("#").format(0), "##,##,###");
                    CommonUtils.insertCommaIntoNumber(holder.tvMax, "0", "##,##,###");
                }
                break;

            case 3:
                if(_36monthTenure!=null) {
                    if(code==0) {
                        principal = Integer.parseInt(mList.get(position).get_36month_tenure().getMax_eligibility());
                    }
                    holder.greyedoutView.setVisibility(View.GONE);
                    roi = Double.parseDouble(mList.get(position).get_36month_tenure().getFixed_roi());
                    holder.tvRoi.setText(mList.get(position).get_36month_tenure().getFixed_roi()+"%");
                    emi = Math.floor(CommonUtils.calculateLoanEmi(principal, roi, 3 * 12));
                    CommonUtils.insertCommaIntoNumber(holder.tvEmi, new DecimalFormat("#").format(emi), "##,##,###");
                    CommonUtils.insertCommaIntoNumber(holder.tvMax, _36monthTenure.getMax_eligibility(), "##,##,###");
                    if (principal > Integer.parseInt(_36monthTenure.getMax_eligibility())) {
                        holder.greyedoutView.setVisibility(View.VISIBLE);

                    }
                }else{
                    holder.greyedoutView.setVisibility(View.VISIBLE);
                    if(holder.checkBox.isChecked()) {
                        holder.checkBox.setChecked(false);
                    }
                    holder.tvRoi.setText("0%");
                    CommonUtils.insertCommaIntoNumber(holder.tvEmi, new DecimalFormat("#").format(0), "##,##,###");
                    CommonUtils.insertCommaIntoNumber(holder.tvMax, "0", "##,##,###");
                }
                break;
            case 5:
                if(_60monthTenure!=null) {
                    if(code==0) {
                        principal = Integer.parseInt(mList.get(position).get_60month_tenure().getMax_eligibility());
                    }
                    holder.greyedoutView.setVisibility(View.GONE);
                    roi = Double.parseDouble(mList.get(position).get_60month_tenure().getFixed_roi());
                    holder.tvRoi.setText(mList.get(position).get_60month_tenure().getFixed_roi()+"%");
                    emi = Math.floor(CommonUtils.calculateLoanEmi(principal, roi, 5 * 12));
                    CommonUtils.insertCommaIntoNumber(holder.tvEmi, new DecimalFormat("#").format(emi), "##,##,###");
                    CommonUtils.insertCommaIntoNumber(holder.tvMax, mList.get(position).get_60month_tenure().getMax_eligibility(), "##,##,###");
                    if (principal > Integer.parseInt(_60monthTenure.getMax_eligibility())) {
                        holder.greyedoutView.setVisibility(View.VISIBLE);

                    }
                }else{
                    holder.greyedoutView.setVisibility(View.VISIBLE);
                    if(holder.checkBox.isChecked()) {
                        holder.checkBox.setChecked(false);
                    }
                    holder.tvRoi.setText("0%");
                    CommonUtils.insertCommaIntoNumber(holder.tvEmi, new DecimalFormat("#").format(0), "##,##,###");
                    CommonUtils.insertCommaIntoNumber(holder.tvMax, "0", "##,##,###");
                }
                break;
            case 1:
                if(_12monthTenure!=null) {
                    if (code == 0){
                        principal = Integer.parseInt(mList.get(position).get_12month_tenure().getMax_eligibility());
                    }
                    holder.greyedoutView.setVisibility(View.GONE);
                    roi = Double.parseDouble(mList.get(position).get_12month_tenure().getFixed_roi());
                    holder.tvRoi.setText(mList.get(position).get_12month_tenure().getFixed_roi()+"%");
                    emi = Math.floor(CommonUtils.calculateLoanEmi(principal, roi, 1 * 12));
                    CommonUtils.insertCommaIntoNumber(holder.tvEmi, new DecimalFormat("#").format(emi), "##,##,###");
                    CommonUtils.insertCommaIntoNumber(holder.tvMax, _12monthTenure.getMax_eligibility(), "##,##,###");
                    if (principal > Integer.parseInt(_12monthTenure.getMax_eligibility())) {
                        holder.greyedoutView.setVisibility(View.VISIBLE);

                    }
                }else{
                    holder.greyedoutView.setVisibility(View.VISIBLE);
                    if(holder.checkBox.isChecked()) {
                        holder.checkBox.setChecked(false);
                    }
                    holder.tvRoi.setText("0%");
                    CommonUtils.insertCommaIntoNumber(holder.tvEmi, new DecimalFormat("#").format(0), "##,##,###");
                    CommonUtils.insertCommaIntoNumber(holder.tvMax, "0", "##,##,###");
                }
                break;
        }
        FInanceOfferSelectedRequestModel object=new FInanceOfferSelectedRequestModel();
        object.setApplied_emi(holder.tvEmi.getText().toString());
        object.setApplied_loan_amount(String.valueOf(principal));
        object.setApplied_roi(holder.tvRoi.getText().toString());
        object.setApplied_tenure(String.valueOf(currentTennure));
        object.setBank_id(mList.get(position).getBank_id());
        object.setBank_URL(mList.get(position).getBank_logo());
        if(modelList.containsKey(mList.get(position).getBank_id())){
            modelList.remove(mList.get(position).getBank_id());
        }
        modelList.put(mList.get(position).getBank_id(), object);


    }


    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void updateData(int currentTennure, int principal, int code, LinkedList<BankOffers> linkedListOfBankOffers) {
        this.currentTennure = currentTennure;
        this.principal = principal;
        this.code = code;
        list.clear();
        this.mList = linkedListOfBankOffers;
        notifyDataSetChanged();

    }

    class  CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        protected ImageView imgBankView;
        protected TextView tvMax, tvRoi,  tvEmi;
        CheckBox checkBox;
        CardView cardView;
        LinearLayout greyedoutView;
        RelativeLayout clickableView;

        public CustomViewHolder(View view) {
            super(view);
            imgBankView = (ImageView) view.findViewById(R.id.img_bank);
            tvMax = (TextView) view.findViewById(R.id.maxEligibilityVal);
            tvRoi = (TextView) view.findViewById(R.id.roiVal);
            tvEmi = (TextView) view.findViewById(R.id.emiVal);
            checkBox = (CheckBox) view.findViewById(R.id.checkBox);
            cardView = (CardView) view.findViewById(R.id.card_view);
            greyedoutView=(LinearLayout)view.findViewById(R.id.overlayView);
            clickableView=(RelativeLayout)view.findViewById(R.id.clickableView);
            greyedoutView.setOnClickListener(this);
            clickableView.setOnClickListener(this);

        }


        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.clickableView:
                    if(checkBox.isChecked()){
                        checkBox.setChecked(false);
                    }else{
                        checkBox.setChecked(true);
                    }
                    break;
                case R.id.overlayView:
                    if(checkBox.isChecked()){
                        checkBox.setChecked(false);
                    }
                    Toast.makeText(mActivity, "This bank doesn't provide any offer for the selected tenure", Toast.LENGTH_SHORT).show();
                    break;
            }



        }
    }



}