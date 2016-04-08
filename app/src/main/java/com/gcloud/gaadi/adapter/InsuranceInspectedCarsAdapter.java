package com.gcloud.gaadi.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
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
import com.gcloud.gaadi.events.InsuranceInspectedCarSelectedEvent;
import com.gcloud.gaadi.model.InsuranceInspectedCarData;
import com.gcloud.gaadi.utils.GCLog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Priya on 25-05-2015.
 */
public class InsuranceInspectedCarsAdapter extends RecyclerView.Adapter<InsuranceInspectedCarsAdapter.InspectedCarsViewHolder> {

    private List<InsuranceInspectedCarData> insuranceInspectedCars;
    private LayoutInflater inflater;
    private Context context;
    private ArrayList<String> mInsuranceCities;
    private String mAgentId = "";

    public InsuranceInspectedCarsAdapter(Context context, List<InsuranceInspectedCarData> insuranceInspectedCars,
                                         ArrayList<String> insuranceCities, String agentId) {
        this.context = context;
        this.insuranceInspectedCars = insuranceInspectedCars;
        inflater = LayoutInflater.from(context);
        this.mInsuranceCities = insuranceCities;
        this.mAgentId = agentId;
    }


    @Override
    public int getItemCount() {
        return insuranceInspectedCars.size();
    }

    @Override
    public void onBindViewHolder(InspectedCarsViewHolder holder, int pos) {


        holder.tv_regNo.setText("Reg. No. " + insuranceInspectedCars.get(pos).getRegNo());
        holder.tv_regYear.setText(insuranceInspectedCars.get(pos).getRegYear());
        holder.tv_makeModel.setText(insuranceInspectedCars.get(pos).getModel()
                + " " + insuranceInspectedCars.get(pos).getVersion());

        holder.tv_Price.setText(insuranceInspectedCars.get(pos).getPrice());
        holder.tv_kmsDriven.setText(insuranceInspectedCars.get(pos).getKmsDriven() + " kms");
        holder.tv_fuelType.setText(insuranceInspectedCars.get(pos).getFuelType());
        holder.item = insuranceInspectedCars.get(pos);
        holder.makeLogo.setImageResource(ApplicationController.makeLogoMap.get(insuranceInspectedCars.get(pos).getMakeId()));

        if ((insuranceInspectedCars.get(pos).getImage() != null) && !insuranceInspectedCars.get(pos).getImage().isEmpty()) {
            Glide.with(context)
                    .load(insuranceInspectedCars.get(pos).getImage())
                    .placeholder(R.drawable.image_load_default_small)
                    .crossFade()
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.inspectedCarImage);

        } else {

            Glide.with(context)
                    .load(R.drawable.no_image_default_small)
                    .centerCrop()
                    .into(holder.inspectedCarImage);

        }
        if (insuranceInspectedCars.get(pos).getColour() != null && !insuranceInspectedCars.get(pos).getColour().equals("")) {
            GradientDrawable bgShape = (GradientDrawable) holder.iv_color.getBackground();
            bgShape.setColor(Color.parseColor(insuranceInspectedCars.get(pos).getColour()));
        } else {
            GradientDrawable bgShape = (GradientDrawable) holder.iv_color.getBackground();
            bgShape.setColor(Color.parseColor("#FFFFFF"));
        }
    }

    @Override
    public InspectedCarsViewHolder onCreateViewHolder(ViewGroup viewGroup, final int viewType) {

        View view = inflater.inflate(R.layout.inspected_cars_card_layout, viewGroup, false);
        InspectedCarsViewHolder holder = new InspectedCarsViewHolder(view);

        return holder;

    }


    class InspectedCarsViewHolder extends RecyclerView.ViewHolder {


        public TextView tv_makeModel, tv_regNo, tv_regYear, tv_fuelType, tv_Price, tv_kmsDriven;
        public InsuranceInspectedCarData item;
        ImageView inspectedCarImage, iv_color, makeLogo;

        public InspectedCarsViewHolder(final View viewItem) {
            super(viewItem);

            tv_makeModel = (TextView) viewItem.findViewById(R.id.tv_makeModel);
            tv_regNo = (TextView) viewItem.findViewById(R.id.tv_regNo);
            tv_regYear = (TextView) viewItem.findViewById(R.id.tv_regYear);
            inspectedCarImage = (ImageView) viewItem.findViewById(R.id.stockImage);
            tv_fuelType = (TextView) viewItem.findViewById(R.id.tv_fuelType);
            tv_Price = (TextView) viewItem.findViewById(R.id.tv_Price);
            tv_kmsDriven = (TextView) viewItem.findViewById(R.id.tv_kmsDriven);
            iv_color = (ImageView) viewItem.findViewById(R.id.iv_color);
            makeLogo = (ImageView) viewItem.findViewById(R.id.makeLogo);
            viewItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GCLog.e("item clicked: " + item.toString());
                    ApplicationController.getEventBus().post(new InsuranceInspectedCarSelectedEvent(
                            item, mAgentId
                    ));

                }
            });
        }


    }


}
