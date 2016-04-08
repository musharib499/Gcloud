package com.gcloud.gaadi.adapter;

import android.content.Context;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.gcloud.gaadi.ApplicationController;
import com.gcloud.gaadi.R;
import com.gcloud.gaadi.model.BankInfo;
import com.gcloud.gaadi.utils.CommonUtils;

import java.util.ArrayList;

/**
 * Created by priyarawat on 2/2/16.
 */
public class LoanCasesExpandableListAdptr extends BaseExpandableListAdapter {

    ArrayList<BankInfo> bankInfoArrayList;
    StateListDrawable drawable;
    private Context _context;

    public LoanCasesExpandableListAdptr(Context context, ArrayList<BankInfo> bankInfoArrayList) {
        this._context = context;
        this.bankInfoArrayList = bankInfoArrayList;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return bankInfoArrayList.get(groupPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {



        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.finance_expandble_list_child_item, null);
        }

        if(!bankInfoArrayList.get(groupPosition).getLoanCaseStatus().equals("3")) {
            convertView.findViewById(R.id.childLinearLayout).setVisibility(View.VISIBLE);
            TextView tvloanAmount = (TextView) convertView.findViewById(R.id.tvloanAmount);
            TextView tvLoanTenure = (TextView) convertView.findViewById(R.id.tvLoanTenure);
            TextView tvEmi = (TextView) convertView.findViewById(R.id.tvEmi);


            tvEmi.setText(bankInfoArrayList.get(groupPosition).getAppliedEmi());
            tvloanAmount.setText(bankInfoArrayList.get(groupPosition).getLoanAmount());
            tvLoanTenure.setText(bankInfoArrayList.get(groupPosition).getAppliedTenure());
            switch (Integer.parseInt(bankInfoArrayList.get(groupPosition).getLoanCaseStatus())) {
                case 0:
                case 1:
                case 3:
                    CommonUtils.insertCommaIntoNumber(tvEmi, bankInfoArrayList.get(groupPosition).getAppliedEmi(), "##,##,###");
                    CommonUtils.insertCommaIntoNumber(tvloanAmount, bankInfoArrayList.get(groupPosition).getLoanAmount(), "##,##,###");
                    tvLoanTenure.setText(bankInfoArrayList.get(groupPosition).getAppliedTenure());
                    break;
            /*case 1:
                tvEmi.setText(bankInfoArrayList.get(groupPosition).getAppliedEmi());
                tvloanAmount.setText(bankInfoArrayList.get(groupPosition).getLoanAmount());
                tvLoanTenure.setText(bankInfoArrayList.get(groupPosition).getAppliedTenure());
                break;*/

                case 2:
                    CommonUtils.insertCommaIntoNumber(tvEmi, bankInfoArrayList.get(groupPosition).getApproved_emi(), "##,##,###");
                    CommonUtils.insertCommaIntoNumber(tvloanAmount, bankInfoArrayList.get(groupPosition).getApproved_amount(), "##,##,###");
                    tvLoanTenure.setText(bankInfoArrayList.get(groupPosition).getApproved_tenure());
                    break;
          /*  case 3:
                tvEmi.setText(bankInfoArrayList.get(groupPosition).getAppliedEmi());
                tvloanAmount.setText(bankInfoArrayList.get(groupPosition).getLoanAmount());
                tvLoanTenure.setText(bankInfoArrayList.get(groupPosition).getAppliedTenure());
                break;*/
                case 4:
                    CommonUtils.insertCommaIntoNumber(tvEmi, bankInfoArrayList.get(groupPosition).getDisbursed_emi(), "##,##,###");
                    CommonUtils.insertCommaIntoNumber(tvloanAmount, bankInfoArrayList.get(groupPosition).getDisbursed_amount(), "##,##,###");
                    tvLoanTenure.setText(bankInfoArrayList.get(groupPosition).getDisbursed_tenure());
                    break;

            }
        }
        else
        {
            TextView rejectionReasonTxt = (TextView) convertView.findViewById(R.id.rejectionReasonText);
            convertView.findViewById(R.id.rejectionReasonLayout).setVisibility(View.VISIBLE);
            convertView.findViewById(R.id.childLinearLayout).setVisibility(View.GONE);
            rejectionReasonTxt.setText(bankInfoArrayList.get(groupPosition).getRejectionReason());
            rejectionReasonTxt.invalidate();
        }
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return bankInfoArrayList.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return bankInfoArrayList.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.finance_expandable_list_grp_item, null);
        }

        TextView tvDate = (TextView) convertView.findViewById(R.id.tvDate);
        ImageView bankLogo = (ImageView) convertView.findViewById(R.id.bankLogo);
        TextView tvLoanCaseStatus = (TextView) convertView.findViewById(R.id.tvLoanCaseStatus);
        TextView dateLabelTxt = (TextView) convertView.findViewById(R.id.dateLabelTxt);
        tvLoanCaseStatus.setText(bankInfoArrayList.get(groupPosition).getLoanCaseStatus());

        switch (Integer.parseInt(bankInfoArrayList.get(groupPosition).getLoanCaseStatus()))
        {
            case 0:
                dateLabelTxt.setText("PICKUP DATE");
                tvLoanCaseStatus.setText(ApplicationController.loanStatus.get(0));
                tvDate.setText(bankInfoArrayList.get(groupPosition).getFileDate());
                drawable = (StateListDrawable) tvLoanCaseStatus.getBackground();
                if (android.os.Build.VERSION.SDK_INT >= 21) {
                    drawable.getCurrent().setTint(ContextCompat.getColor(_context, R.color.yellow_light));
                } else {
                    ((VectorDrawableCompat) drawable.getCurrent()).setTint(ContextCompat.getColor(_context, R.color.yellow_light));
                }
                //drawable.setColor(ContextCompat.getColor(_context, R.color.yellow_light));
                break;
            case 1:
                dateLabelTxt.setText("FILED DATE");
                tvLoanCaseStatus.setText(ApplicationController.loanStatus.get(1));
                tvDate.setText(bankInfoArrayList.get(groupPosition).getFileDate());
                drawable = (StateListDrawable) tvLoanCaseStatus.getBackground();
                if (android.os.Build.VERSION.SDK_INT >= 21) {
                    drawable.getCurrent().setTint(ContextCompat.getColor(_context, R.color.pendingCasesBackground));
                } else {
                    ((VectorDrawableCompat) drawable.getCurrent()).setTint(ContextCompat.getColor(_context, R.color.pendingCasesBackground));
                }
                //drawable.setColor(ContextCompat.getColor(_context, R.color.pendingCasesBackground));
                break;

            case 2:
                dateLabelTxt.setText("APPROVED DATE");
                tvLoanCaseStatus.setText(ApplicationController.loanStatus.get(2));
                tvDate.setText(bankInfoArrayList.get(groupPosition).getApproval_date());
                drawable = (StateListDrawable) tvLoanCaseStatus.getBackground();
                if (android.os.Build.VERSION.SDK_INT >= 21) {
                    drawable.getCurrent().setTint(ContextCompat.getColor(_context, R.color.Approved_green));
                } else {
                    ((VectorDrawableCompat) drawable.getCurrent()).setTint(ContextCompat.getColor(_context, R.color.Approved_green));
                }
                //drawable.setColor(ContextCompat.getColor(_context, R.color.Approved_green));
                break;
            case 3:
                dateLabelTxt.setText("REJECTED DATE");
                tvLoanCaseStatus.setText(ApplicationController.loanStatus.get(3));
                tvDate.setText(bankInfoArrayList.get(groupPosition).getFileDate());
                drawable = (StateListDrawable) tvLoanCaseStatus.getBackground();
                if (android.os.Build.VERSION.SDK_INT >= 21) {
                    drawable.getCurrent().setTint(ContextCompat.getColor(_context, R.color.rejectedCasesBackground));
                } else {
                    ((VectorDrawableCompat) drawable.getCurrent()).setTint(ContextCompat.getColor(_context, R.color.rejectedCasesBackground));

                }//drawable.setColor(ContextCompat.getColor(_context, R.color.rejectedCasesBackground));
                break;
            case 4:
                dateLabelTxt.setText("DISBURSED DATE");
                tvLoanCaseStatus.setText(ApplicationController.loanStatus.get(4));
                tvDate.setText(bankInfoArrayList.get(groupPosition).getDisbursed_date());
                drawable = (StateListDrawable) tvLoanCaseStatus.getBackground();
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    drawable.getCurrent().setTint(ContextCompat.getColor(_context, R.color.disbursed_color));
                } else {
                    ((VectorDrawableCompat) drawable.getCurrent()).setTint(ContextCompat.getColor(_context, R.color.disbursed_color));
                }
                //drawable.setColor(ContextCompat.getColor(_context, R.color.disbursed_color));
                break;
         /*   case 5:
                dateLabelTxt.setText("WASHOUT DATE");
                tvLoanCaseStatus.setText(ApplicationController.loanStatus.get(5));
                drawable = (GradientDrawable) tvLoanCaseStatus.getBackground();
                drawable.setColor(ContextCompat.getColor(_context, R.color.finance_blue));
                break;

            case 6:
                dateLabelTxt.setText("CLOSED DATE");
                tvLoanCaseStatus.setText(ApplicationController.loanStatus.get(6));
                drawable = (GradientDrawable) tvLoanCaseStatus.getBackground();
                drawable.setColor(ContextCompat.getColor(_context, R.color.finance_yellow));
                break;
            case -2:
                tvLoanCaseStatus.setText(ApplicationController.loanStatus.get(-2));
                drawable = (GradientDrawable) tvLoanCaseStatus.getBackground();
                drawable.setColor(ContextCompat.getColor(_context, R.color.orange));
                break;*/
        }
        if ((bankInfoArrayList.get(groupPosition).getBankLogo()!= null) && !bankInfoArrayList.get(groupPosition).getBankLogo().trim().isEmpty()) {
            bankLogo.setVisibility(View.VISIBLE);
            Glide.with(_context)
                    .load(bankInfoArrayList.get(groupPosition).getBankLogo())

                    .crossFade()

                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(bankLogo);
            /*mHolder.stockImage.setDefaultImageResId(R.drawable.image_load_default_small);
            mHolder.stockImage.setErrorImageResId(R.drawable.no_image_default_small);
            mHolder.stockImage.setImageUrl(mItems.get(position).getImageIcon(), mImageLoader);*/

        } else {
            bankLogo.setVisibility(View.INVISIBLE);
            /*mHolder.stockImage.setDefaultImageResId(R.drawable.no_image_default_small);*/

        }

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
