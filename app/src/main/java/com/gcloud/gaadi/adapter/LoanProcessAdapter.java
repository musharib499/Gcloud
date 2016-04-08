package com.gcloud.gaadi.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gcloud.gaadi.R;

import java.util.List;

/**
 * Created by Mushareb Ali on 02-09-2015.
 */
public class LoanProcessAdapter extends RecyclerView.Adapter<LoanProcessAdapter.CustomViewHolder> {

    private Context context;
    private List<String> stringList;

    public LoanProcessAdapter(Context context, List<String> stringList) {

        this.context = context;
        this.stringList = stringList;

    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.loan_process_item, null);
        CustomViewHolder customViewHolder = new CustomViewHolder(view);
        return customViewHolder;
    }

    @Override
    public void onBindViewHolder(final CustomViewHolder holder, int position) {

        holder.tvMax.setText(stringList.get(position).toString());

        holder.lay_check_emi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.lay_check_emi.setVisibility(View.GONE);
                holder.lay_year.setVisibility(View.GONE);
                holder.lay_seek_detail.setVisibility(View.VISIBLE);

            }
        });
        holder.lay_approve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.lay_seek_detail.setVisibility(View.GONE);
                holder.lay_year.setVisibility(View.VISIBLE);
                holder.lay_check_emi.setVisibility(View.VISIBLE);

            }
        });

    }


    @Override
    public int getItemCount() {
        return (null != stringList ? stringList.size() : 0);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        protected ImageView imgBankView;
        protected TextView tvMax, tvRoi, tv2Year, tv3Year, tv5Year, tvAmount, tvEmi, tvEmiTime, tvSubject;
        protected RelativeLayout lay_year, lay_seek_detail, lay_check_emi, lay_approve;

        public CustomViewHolder(View view) {
            super(view);
            this.imgBankView = (ImageView) view.findViewById(R.id.img_bank);
            this.tvMax = (TextView) view.findViewById(R.id.tv_mx);
            this.tvRoi = (TextView) view.findViewById(R.id.tv_roi);
            this.tv2Year = (TextView) view.findViewById(R.id.tv_2year);
            this.tv3Year = (TextView) view.findViewById(R.id.tv_3year);
            this.tv5Year = (TextView) view.findViewById(R.id.tv_5year);
            this.tvAmount = (TextView) view.findViewById(R.id.tv_amount);
            //this.tvAmount=(TextView)view.findViewById(R.id.tv_amount);
            this.tvEmi = (TextView) view.findViewById(R.id.tv_emi);
            this.tvSubject = (TextView) view.findViewById(R.id.tv_subject);
            this.lay_year = (RelativeLayout) view.findViewById(R.id.lay_year);
            this.lay_seek_detail = (RelativeLayout) view.findViewById(R.id.lay_seek_detail);
            this.lay_check_emi = (RelativeLayout) view.findViewById(R.id.lay_check_emi);
            this.lay_approve = (RelativeLayout) view.findViewById(R.id.lay_approve);


            //this.imageView = (ImageView) view.findViewById(R.id.thumbnail);
            //this.textView = (TextView) view.findViewById(R.id.title);
        }
    }

}
