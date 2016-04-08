package com.gcloud.gaadi.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.gcloud.gaadi.ApplicationController;
import com.gcloud.gaadi.R;
import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.model.Finance.Utility;
import com.gcloud.gaadi.model.LoanApplication;
import com.gcloud.gaadi.ui.Finance.FinanceReuploadActivity;

import java.util.ArrayList;

/**
 * Created by priyarawat on 2/2/16.
 */
public class FinanceLoanCasesListAdapter extends RecyclerView.Adapter<FinanceLoanCasesListAdapter.ViewHolder> {

    private ArrayList<LoanApplication> mItems;
    private Context mContext;
    private LayoutInflater mInflater;

    public FinanceLoanCasesListAdapter(Context context, ArrayList<LoanApplication> items) {
        if(context == null)
            mContext = ApplicationController.getInstance();
        else
            mContext = context;
        mItems = items;
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.finance_loan_cases_row_item, parent, false);
        CardView cardView = (CardView) view;
        ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.cardView = cardView;
        viewHolder.tvLoanId = (TextView) view.findViewById(R.id.tvLoanId);
        viewHolder.tvCustomarName = (TextView) view.findViewById(R.id.tvCustomerName);
        viewHolder.tvMakeModel = (TextView) view.findViewById(R.id.tvCarName);
        viewHolder.tvRegistrationNum=(TextView) view.findViewById(R.id.tvRegNum);
        viewHolder.expandableListView = (ExpandableListView) view.findViewById(R.id.expandableListView);
        viewHolder.imageView = (ImageView) view.findViewById(R.id.imageView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {


        final LoanApplication loanCase = mItems.get(position);

        holder.tvLoanId.setText(loanCase.getApplicationId());
        holder.tvMakeModel.setText(loanCase.getMakeValue()+" "+loanCase.getModel());
        holder.tvRegistrationNum.setText(loanCase.getRegNo());
        holder.expandableListView.setAdapter(new LoanCasesExpandableListAdptr(mContext, loanCase.getBankInfoList()));
        Utility.setListViewHeightBasedOnChildren(holder.expandableListView);
        if(loanCase.isExpanded())
        {
            holder.imageView.setImageResource(R.drawable.up_arrow);
            for (int i = 0; i < loanCase.getBankInfoList().size(); i++) {
                holder.expandableListView.expandGroup(i);
            }
        }
        else
        {
            holder.imageView.setImageResource(R.drawable.down_arrow);
            for (int i = 0; i < loanCase.getBankInfoList().size(); i++) {
                holder.expandableListView.collapseGroup(i);
            }
        }
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(loanCase.getBankInfoList().size() == 0)
                {
                    return;
                }
                if(!loanCase.isExpanded()) {
                    loanCase.setIsExpanded(true);
                    holder.imageView.setImageResource(R.drawable.up_arrow);
                    for (int i = 0; i < loanCase.getBankInfoList().size(); i++) {
                        holder.expandableListView.expandGroup(i);
                    }
                }
                else
                {
                    loanCase.setIsExpanded(false);
                    holder.imageView.setImageResource(R.drawable.down_arrow);
                    for(int i=0 ; i< loanCase.getBankInfoList().size() ; i++)
                    {
                        holder.expandableListView.collapseGroup(i);
                    }
                }

            }
        });

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
        holder.expandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
            @Override
            public void onGroupCollapse(int groupPosition) {
                Utility.setListViewHeightBasedOnChildren(holder.expandableListView);
            }
        });
        holder.expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
                                                               @Override
                                                               public void onGroupExpand(int groupPosition) {
                                                                   int totalHeight = 0;
                                                                   int desiredWidth = View.MeasureSpec.makeMeasureSpec(holder.expandableListView.getWidth(), View.MeasureSpec.UNSPECIFIED);
                                                                   for (int i = 0; i < holder.expandableListView.getAdapter().getCount(); i++) {
                                                                       View listItem = holder.expandableListView.getAdapter().getView(i, null, holder.expandableListView);
                                                                       listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
                                                                       totalHeight += listItem.getMeasuredHeight();
                                                                   }

                                                                   ViewGroup.LayoutParams params = holder.expandableListView.getLayoutParams();
                                                                   params.height = totalHeight + (holder.expandableListView.getDividerHeight() * (holder.expandableListView.getAdapter().getCount() - 1));
                                                                   holder.expandableListView.setLayoutParams(params);
                                                                   holder.expandableListView.requestLayout();
                                                               }
                                                           }
        );
        holder.expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                return true;
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

        ImageView imageView;
        TextView tvMakeModel, tvRegistrationNum, tvCustomarName, tvLoanId;
        ExpandableListView expandableListView;
        CardView cardView;
    }
}

