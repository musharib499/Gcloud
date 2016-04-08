package com.gcloud.gaadi.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.gcloud.gaadi.R;
import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.utils.CommonUtils;

/**
 * Created by Priya on 24-08-2015.
 */
public class LeadsDialogFragment extends DialogFragment implements View.OnClickListener {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.leads_dialog_fragment_layout, container,
                false);

        getDialog().setTitle("Leads");
        Button btn_buyerLeads = (Button) rootView.findViewById(R.id.btn_buyerLeads);
        Button btn_sellerLeads = (Button) rootView.findViewById(R.id.btn_sellerLeads);
        btn_buyerLeads.setOnClickListener(this);
        btn_sellerLeads.setOnClickListener(this);


        return rootView;
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {

            case R.id.btn_buyerLeads:
                if (CommonUtils.getStringSharedPreference(getActivity(), Constants.UC_WARRANTY_ONLY_DEALER, "0").equals("0")) {
                    intent = new Intent(getActivity(), LeadsManageActivity.class);
                    startActivity(intent);
                    CommonUtils.startActivityTransition(getActivity(), Constants.TRANSITION_LEFT);
                } else {
                    CommonUtils.showToast(getActivity(), getActivity().getString(R.string.warranty_only_dealer_message), Toast.LENGTH_SHORT);
                }
                break;

            case R.id.btn_sellerLeads:
                if (CommonUtils.getIntSharedPreference(getActivity(), Constants.IS_SELLER, 0) == 1) {
                    intent = new Intent(getActivity(), SellerLeadsActivity.class);
                    startActivity(intent);
                    CommonUtils.startActivityTransition(getActivity(), Constants.TRANSITION_LEFT);
                } else {
                    CommonUtils.showToast(getActivity(), getActivity().getString(R.string.warranty_only_dealer_message), Toast.LENGTH_SHORT);
                }
                break;

        }
        this.dismiss();
    }

 /*   @Override
    public void onResume() {

        super.onResume();
        Window window = getDialog().getWindow();
        window.setLayout(500, 500);
        window.setGravity(Gravity.CENTER);
    }*/
}