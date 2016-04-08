package com.gcloud.gaadi.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gcloud.gaadi.R;
import com.gcloud.gaadi.model.InsuranceCaseStatusData;
import com.gcloud.gaadi.utils.CommonUtils;

import java.util.List;

/**
 * Created by Priya on 25-05-2015.
 */
public class InsuranceStatusAdapter extends RecyclerView.Adapter<InsuranceStatusAdapter.InspectedCarsViewHolder> {

    LayoutInflater inflater;
    Context context;
    int position;
    private List<InsuranceCaseStatusData> mInsuranceCases;

    public InsuranceStatusAdapter(Context context, List<InsuranceCaseStatusData> insuranceCases) {
        this.context = context;
        this.mInsuranceCases = insuranceCases;
        inflater = LayoutInflater.from(context);
    }


    @Override
    public int getItemCount() {
        return mInsuranceCases.size();
    }

    @Override
    public void onBindViewHolder(InspectedCarsViewHolder holder, int pos) {

        holder.tv_requestId.setText(CommonUtils.getReplacementString(context,
                R.string.request_no, String.valueOf(mInsuranceCases.get(pos).getRequestNo())));

        holder.tv_requestDate.setText(CommonUtils.getReplacementString(context,
                R.string.request_date, mInsuranceCases.get(pos).getRequestDate()));

        holder.tv_insurerName.setText(mInsuranceCases.get(pos).getInsurer());

        holder.tv_premiumAmount.setText(CommonUtils.getReplacementString(context,
                R.string.premium_amount, mInsuranceCases.get(pos).getPremiumAmount()));

        holder.tv_carType.setText(CommonUtils.getReplacementString(context,
                R.string.car_type, mInsuranceCases.get(pos).getInspectionType()));

        holder.tv_status.setText(mInsuranceCases.get(pos).getInsuranceStatus());

        holder.tv_vehicleName.setText(mInsuranceCases.get(pos).getModel()
                + " " + mInsuranceCases.get(pos).getVersion());

    }

    @Override
    public InspectedCarsViewHolder onCreateViewHolder(ViewGroup viewGroup, final int viewType) {

        View view = inflater.inflate(R.layout.insurance_status_card_layout, viewGroup, false);
        InspectedCarsViewHolder holder = new InspectedCarsViewHolder(view);

        return holder;


    }


    public class InspectedCarsViewHolder extends RecyclerView.ViewHolder {

        public TextView tv_requestId,
                tv_vehicleName,
                tv_requestDate,
                tv_insurerName,
                tv_status,
                tv_carType,
                tv_premiumAmount;

        public InsuranceCaseStatusData item;

        public InspectedCarsViewHolder(final View viewItem) {
            super(viewItem);

            tv_requestId = (TextView) viewItem.findViewById(R.id.tv_requestId);
            tv_vehicleName = (TextView) viewItem.findViewById(R.id.tv_vehicleName);
            tv_requestDate = (TextView) viewItem.findViewById(R.id.tv_requestDate);
            tv_insurerName = (TextView) viewItem.findViewById(R.id.tv_insurerName);
            tv_status = (TextView) viewItem.findViewById(R.id.tv_status);
            tv_carType = (TextView) viewItem.findViewById(R.id.tv_carType);
            tv_premiumAmount = (TextView) viewItem.findViewById(R.id.tv_premiumAmount);


            viewItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                }
            });
        }

    }


}
