package com.gcloud.gaadi.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.gcloud.gaadi.ApplicationController;
import com.gcloud.gaadi.R;
import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.model.SellerLeadsCarModel;
import com.gcloud.gaadi.utils.CommonUtils;

import java.util.ArrayList;

/**
 * Created by Priya on 26-08-2015.
 */
public class SellerLeadsRecyclerAdapter extends RecyclerView.Adapter<SellerLeadsRecyclerAdapter.ViewHolder> {

    Context context;
    private LayoutInflater inflater;
    ArrayList<SellerLeadsCarModel> mList;

    public SellerLeadsRecyclerAdapter(Context context, ArrayList<SellerLeadsCarModel> mList) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.mList = mList;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = inflater.inflate(R.layout.seller_leads_car_detail_list_rowitem, viewGroup, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int pos) {

        Display display = ((Activity) context).getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float density = ((Activity) context).getResources().getDisplayMetrics().density;
        float dpHeight = outMetrics.heightPixels / density;
        float dpWidth = outMetrics.widthPixels / density;
        ViewGroup.LayoutParams params = holder.rowParentLayout.getLayoutParams();
        params.width = outMetrics.widthPixels - 70;
        // holder.rowParentLayout.setMinimumWidth((int) dpWidth);
        holder.tv_modelYear.setText(mList.get(pos).getYear());
        StringBuilder makeModelVariant = new StringBuilder();
        makeModelVariant.append(mList.get(pos).getModel());
        if (mList.get(pos).getVariant() != null && !mList.get(pos).getVariant().trim().isEmpty()) {
            makeModelVariant.append(mList.get(pos).getVariant());
        }
        holder.tv_makeModelVariant.setText(makeModelVariant.toString());

        if (!mList.get(pos).getPrice().equals("") && !mList.get(pos).getPrice().equals("0")) {
            holder.tv_price.setVisibility(View.VISIBLE);
            holder.tv_price.setText(CommonUtils.getReplacementString(context, R.string.inr, mList.get(pos).getPrice()));
        } else {
            holder.tv_price.setVisibility(View.GONE);
        }
        holder.tv_kmsDriven.setText(mList.get(pos).getKmsDriven() + " kms");
        holder.tv_fuelType.setText(mList.get(pos).getFuel_type());
        // holder.item = mList.get(pos);
        holder.iv_makeLogo.setImageResource(ApplicationController.makeLogoMap.get(mList.get(pos).getMakeID()));


        GradientDrawable bgShape = (GradientDrawable) holder.iv_color.getBackground();
        int colorResource = Color.parseColor("#FFFFFF");
        if (mList.get(pos).getColorHex() != null && !mList.get(pos).getColorHex().isEmpty() && !mList.get(pos).getColorHex().equals("#")) {
            try {
                colorResource = Color.parseColor(mList.get(pos).getColor());
            } catch (IllegalArgumentException e) {
                //Crashlytics.logException(e);
                try {
                    colorResource = Color.parseColor(mList.get(pos).getColorHex());
                } catch (IllegalArgumentException ex) {
                    Crashlytics.logException(ex);
                } catch (Exception exp) {
                    Crashlytics.setUserEmail(CommonUtils.getStringSharedPreference(context, Constants.UC_DEALER_EMAIL, ""));
                    Crashlytics.logException(exp.getCause());
                }
            }
        }
        bgShape.setColor(colorResource);

        if (pos == mList.size() - 1) {
            holder.divider.setVisibility(View.GONE);
        } else {
            holder.divider.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {

        TextView tv_makeModelVariant, tv_kmsDriven, tv_modelYear, tv_fuelType, tv_price;
        ImageView iv_color, iv_makeLogo;
        RelativeLayout rowParentLayout;
        View divider;

        ViewHolder(View viewItem) {
            super(viewItem);
            tv_makeModelVariant = (TextView) viewItem.findViewById(R.id.tv_makeModelVariant);
            rowParentLayout = (RelativeLayout) viewItem.findViewById(R.id.parentLayout);
            tv_modelYear = (TextView) viewItem.findViewById(R.id.tv_regYear);

            tv_fuelType = (TextView) viewItem.findViewById(R.id.tv_fuelType);
            tv_price = (TextView) viewItem.findViewById(R.id.tv_Price);
            tv_kmsDriven = (TextView) viewItem.findViewById(R.id.tv_kmsDriven);
            iv_color = (ImageView) viewItem.findViewById(R.id.iv_color);
            iv_makeLogo = (ImageView) viewItem.findViewById(R.id.iv_makeLogo);
            divider = (View) viewItem.findViewById(R.id.iv_divider);

        }


    }
}
