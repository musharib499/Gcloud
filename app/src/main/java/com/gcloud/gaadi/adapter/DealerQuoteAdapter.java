package com.gcloud.gaadi.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.gcloud.gaadi.ApplicationController;
import com.gcloud.gaadi.R;
import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.events.InsuranceInspectedCarSelectedEvent;
import com.gcloud.gaadi.insurance.DealerQuoteScreen;
import com.gcloud.gaadi.model.InsuranceInspectedCarData;
import com.gcloud.gaadi.model.QuoteDetails;

import java.util.ArrayList;

/**
 * Created by Ambujesh on 5/22/2015.
 */
public class DealerQuoteAdapter extends RecyclerView.Adapter<DealerQuoteAdapter.DealerViewHolder> {

    private Context mContext;
    private ArrayList<QuoteDetails> mQuouteList;

    private String mProcessId;

    private String mSelectedCase;
    private InsuranceInspectedCarData insuranceInspectedCarData;
    private String mAgentId;
    public static String insurance_case_id;

    public DealerQuoteAdapter(Context context) {
        this.mContext = context;
    }

    public DealerQuoteAdapter(Context context, ArrayList<QuoteDetails> quoteList, String mProcessId, String selectedCase, InsuranceInspectedCarData insuranceInspectedCarData, String mAgentId) {
        this.mContext = context;
        this.mQuouteList = quoteList;

        this.mProcessId = mProcessId;

        this.mSelectedCase = selectedCase;
        this.insuranceInspectedCarData = insuranceInspectedCarData;
        this.mAgentId = mAgentId;
    }


    @Override
    public DealerViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.quote_list_row_layout, viewGroup, false);
        DealerViewHolder pvh = new DealerViewHolder(v);
        return pvh;
    }

    @Override
    public int getItemCount() {
        if(mQuouteList!=null)
        return mQuouteList.size();
        else
            return 0;
    }

    @Override
    public void onBindViewHolder(DealerViewHolder dealerViewHolder, int i) {
        dealerViewHolder.insurerName.setText(mQuouteList.get(i).getInsurerNameTrimmed());
        dealerViewHolder.premiumValue.setText("" + mQuouteList.get(i).getNetPremium());
        dealerViewHolder.finalOD.setText(mQuouteList.get(i).getFinalOd());
        dealerViewHolder.thirdPartyAmount.setText(mQuouteList.get(i).getTotalTp());
        dealerViewHolder.serviceTax.setText(mQuouteList.get(i).getServiceTaxAmount());
        dealerViewHolder.idvValue.setText(mQuouteList.get(i).getIdvValue());
        dealerViewHolder.tvPriceRange.setText("IDV Range : " + mQuouteList.get(i).getIdv_range());
        if (DealerQuoteScreen.isZeroDep !=0 || DealerQuoteScreen.deductible_premiums !=0)
        {
          if ("0".equalsIgnoreCase(mQuouteList.get(i).getZeroDepPremium()))
            {



                dealerViewHolder.zeroDepAmountLayout.setVisibility(View.INVISIBLE);
            }
            else {
              dealerViewHolder.odLayout.setVisibility(View.VISIBLE);
              dealerViewHolder.zeroDepAmountLayout.setVisibility(View.VISIBLE);
              dealerViewHolder.zeroDepValue.setText(mQuouteList.get(i).getZeroDepPremium());
          }
            if ("0".equalsIgnoreCase(mQuouteList.get(i).getVoluntaryDeductiblePremium()))
            {
                dealerViewHolder.ncbLayout.setVisibility(View.GONE);
                dealerViewHolder.deductiblesValuesLayout.setVisibility(View.INVISIBLE);
            }
            else {
                dealerViewHolder.ncbLayout.setVisibility(View.VISIBLE);
                dealerViewHolder.deductiblesValuesLayout.setVisibility(View.VISIBLE);

                dealerViewHolder.deductiblesValues.setText(mQuouteList.get(i).getVoluntaryDeductiblePremium());
            }


        }
        insurance_case_id = mQuouteList.get(i).getInsurance_case_id();

        if (!mQuouteList.get(i).getOdDiscountPercent().equals("0")) {
            dealerViewHolder.odLayout.setVisibility(View.VISIBLE);
            dealerViewHolder.odDiscountPercent.setText(mQuouteList.get(i).getOdDiscountPercent());
            dealerViewHolder.odBasic.setText(mQuouteList.get(i).getOdDiscountPremium());
        } else {
            if ("0".equalsIgnoreCase(mQuouteList.get(i).getZeroDepPremium())) {
                dealerViewHolder.odLayout.setVisibility(View.GONE);
                if ("0".equalsIgnoreCase(mQuouteList.get(i).getVoluntaryDeductiblePremium())) {
                    dealerViewHolder.deductiblesValuesLayout.setVisibility(View.GONE);
                }else{
                    dealerViewHolder.ncbLayout.setVisibility(View.VISIBLE);
                    dealerViewHolder.deductiblesValuesLayout.setVisibility(View.VISIBLE);
                }

            }
            dealerViewHolder.oldLayoutOD.setVisibility(View.GONE);
            dealerViewHolder.oldLayoutODDiscount.setVisibility(View.GONE);
        }
        dealerViewHolder.quoteDetails = mQuouteList.get(i);


        if ((mQuouteList.get(i).getInsurerLogo() != null) && !mQuouteList.get(i).getInsurerLogo().isEmpty()) {
            Glide.with(mContext)
                    .load(mQuouteList.get(i).getInsurerLogo())
                    .placeholder(R.drawable.image_load_default_small)
                    .crossFade()
                    .fitCenter()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(dealerViewHolder.ivInsurerLogo);

        } else {

            Glide.with(mContext)
                    .load(R.drawable.default_img)
                    .fitCenter()
                    .into(dealerViewHolder.ivInsurerLogo);

        }

        if (mSelectedCase.equalsIgnoreCase(Constants.OTHER_CARS) && !mQuouteList.get(i).getNcbPercent().equals("0")) {
            dealerViewHolder.ncbLayout.setVisibility(View.VISIBLE);
            if ("0".equalsIgnoreCase(mQuouteList.get(i).getVoluntaryDeductiblePremium())) {
                dealerViewHolder.deductiblesValuesLayout.setVisibility(View.INVISIBLE);
            }else{

                dealerViewHolder.deductiblesValuesLayout.setVisibility(View.VISIBLE);
            }
            dealerViewHolder.ncbLayoutPercentage.setVisibility(View.VISIBLE);
            dealerViewHolder.ncbLayoutValue.setVisibility(View.VISIBLE);
            dealerViewHolder.ncbPercent.setText(mQuouteList.get(i).getNcbPercent());
            dealerViewHolder.ncbValue.setText(mQuouteList.get(i).getNcbAmount());

        } else {
            if ("0".equalsIgnoreCase(mQuouteList.get(i).getVoluntaryDeductiblePremium())) {
                dealerViewHolder.ncbLayout.setVisibility(View.GONE);
            }
            dealerViewHolder.ncbLayoutPercentage.setVisibility(View.GONE);
            dealerViewHolder.ncbLayoutValue.setVisibility(View.GONE);
        }

        if ("0".equalsIgnoreCase(mQuouteList.get(i).getOdDiscountPremium())
                || "0".equalsIgnoreCase(mQuouteList.get(i).getOdDiscountPercent())) {
            if ("0".equalsIgnoreCase(mQuouteList.get(i).getZeroDepPremium())) {
                dealerViewHolder.odLayout.setVisibility(View.GONE);
            }

        } else {
            dealerViewHolder.odLayout.setVisibility(View.VISIBLE);

        }

    }


    public class DealerViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView insurerName;
        TextView premiumValue;

        ImageView ivInsurerLogo;
        TextView finalOD, odDiscountPercent, odBasic;
        ImageView dropDownArrow;
        TextView thirdPartyAmount;
        TextView serviceTax, tvPriceRange;
        TextView idvValue, ncbPercent, ncbValue,zeroDepValue,deductiblesValues;
        public QuoteDetails quoteDetails;
        LinearLayout ncbLayout, odLayout,expand,ncbLayoutPercentage,ncbLayoutValue,zeroDepAmountLayout,
                deductiblesValuesLayout,oldLayoutOD,oldLayoutODDiscount;
        Button buyNow;

        DealerViewHolder(View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.cvDealerQuote);
            tvPriceRange = (TextView) itemView.findViewById(R.id.tv_price_range);
            insurerName = (TextView) itemView.findViewById(R.id.tvInsurerName);
            premiumValue = (TextView) itemView.findViewById(R.id.tvPremiumValue);
            finalOD = (TextView) itemView.findViewById(R.id.tvFinalOdValue);
            thirdPartyAmount = (TextView) itemView.findViewById(R.id.tvTotalTpValue);
            serviceTax = (TextView) itemView.findViewById(R.id.tvServiceTax);
            dropDownArrow=(ImageView)itemView.findViewById(R.id.dropDownArrow);
            ivInsurerLogo = (ImageView) itemView.findViewById(R.id.ivInsurerLogo);
            idvValue = (TextView) itemView.findViewById(R.id.tvIdvValue);
            ncbPercent = (TextView) itemView.findViewById(R.id.tvNCBPercent);
            ncbValue = (TextView) itemView.findViewById(R.id.tvNCBValue);
            zeroDepValue = (TextView) itemView.findViewById(R.id.zeroDepValue);
            deductiblesValues = (TextView) itemView.findViewById(R.id.deductiblesValues);
            odBasic = (TextView) itemView.findViewById(R.id.tvOdDiscountPremiumValue);
            odDiscountPercent = (TextView) itemView.findViewById(R.id.tvOdDiscountPercentValue);
            ncbLayout = (LinearLayout) itemView.findViewById(R.id.ncbLayout);
            ncbLayoutPercentage = (LinearLayout) itemView.findViewById(R.id.ncbLayoutPercentage);
            zeroDepAmountLayout = (LinearLayout) itemView.findViewById(R.id.zeroDepAmountLayout);
            oldLayoutOD = (LinearLayout) itemView.findViewById(R.id.oldLayoutOD);
            oldLayoutODDiscount = (LinearLayout) itemView.findViewById(R.id.oldLayoutODDiscount);
            deductiblesValuesLayout = (LinearLayout) itemView.findViewById(R.id.deductiblesValuesLayout);
            ncbLayoutValue = (LinearLayout) itemView.findViewById(R.id.ncbLayoutValue);
            buyNow = (Button)itemView.findViewById(R.id.buyNow);
            odLayout = (LinearLayout) itemView.findViewById(R.id.odLayout);
            expand = (LinearLayout)itemView.findViewById(R.id.expand);
            dropDownArrow.setTag(1);
            dropDownArrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Integer.parseInt(dropDownArrow.getTag().toString()) == 1) {
                        dropDownArrow.setImageResource(R.drawable.up_arrow);
                        expand.setVisibility(View.VISIBLE);
                        dropDownArrow.setTag(2);
                    } else {
                        dropDownArrow.setImageResource(R.drawable.down_arrow);
                        expand.setVisibility(View.GONE);
                        dropDownArrow.setTag(1);
                    }

                }
            });
            cv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (Integer.parseInt(dropDownArrow.getTag().toString()) == 1) {
                        dropDownArrow.setImageResource(R.drawable.up_arrow);
                        expand.setVisibility(View.VISIBLE);
                        dropDownArrow.setTag(2);
                    } else {
                        dropDownArrow.setImageResource(R.drawable.down_arrow);
                        expand.setVisibility(View.GONE);
                        dropDownArrow.setTag(1);
                    }
                }
            });

            buyNow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    ApplicationController.getEventBus().post(new InsuranceInspectedCarSelectedEvent(
                            quoteDetails, mAgentId
                    ));
                }
            });
        }

    }
}
