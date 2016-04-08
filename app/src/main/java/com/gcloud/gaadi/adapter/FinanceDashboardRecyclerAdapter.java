package com.gcloud.gaadi.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gcloud.gaadi.ApplicationController;
import com.gcloud.gaadi.R;
import com.gcloud.gaadi.model.Finance.MonthWiseCount;

import java.util.ArrayList;

/**
 * Created by priyarawat on 27/1/16.
 */
public class FinanceDashboardRecyclerAdapter extends RecyclerView.Adapter<FinanceDashboardRecyclerAdapter.ViewHolder> {
    Context context;
    private LayoutInflater inflater;
    ArrayList<MonthWiseCount> monthYrList;

    public FinanceDashboardRecyclerAdapter(Context context, ArrayList<MonthWiseCount> monthYrList) {
        this.context = context;
        this.monthYrList = monthYrList;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.dashboard_recycler_rowitem, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if(!monthYrList.get(position).getYear().equals("") && !monthYrList.get(position).getYear().equals("0")) {
            holder.monthName.setVisibility(View.VISIBLE);
            holder.year.setText(monthYrList.get(position).getYear() + "");
            holder.monthName.setText(ApplicationController.monthShortMap.get(monthYrList.get(position).getMonth()));
        }
        else if (monthYrList.get(position).getYear().equals("0"))
        {
            holder.year.setText("All");
            holder.monthName.setVisibility(View.GONE);
        }
        else {
            holder.monthName.setVisibility(View.VISIBLE);
            holder.year.setText("");
            holder.monthName.setText("");
        }
    }

    @Override
    public int getItemCount() {
        return monthYrList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView monthName, year;

        public ViewHolder(View itemView) {
            super(itemView);
            year = (TextView) itemView.findViewById(R.id.tvYear);
            monthName = (TextView) itemView.findViewById(R.id.tvMonthName);
        }
    }

}
