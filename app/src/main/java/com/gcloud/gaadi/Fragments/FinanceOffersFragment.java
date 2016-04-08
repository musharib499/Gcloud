package com.gcloud.gaadi.Fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.gcloud.gaadi.R;
import com.gcloud.gaadi.adapter.FinanceOffersAdapter;
import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.model.CarItemModel;
import com.gcloud.gaadi.model.Finance.BankOffers;
import com.gcloud.gaadi.model.Finance.FInanceOfferSelectedRequestModel;
import com.gcloud.gaadi.utils.CommonUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * Created by Lakshay on 08-09-2015.
 */
public class FinanceOffersFragment extends Fragment implements Button.OnClickListener{

    TextView tvTenure, tvLoanAmount;
    SeekBar seekBar;
    Button btnPlus,btnMinus,btnApplyNow;
    int currentTennure, principal, code=0;
    RadioGroup radioGroup;
    RadioButton rbMaxAmount,rbCustom;
    RelativeLayout relativeLayout;
    LinkedList<BankOffers> linkedListOfBankOffers;
    private Activity mActivity;
    private RecyclerView mRecyclerView;
    private FinanceOffersAdapter mAdapter;
    private ArrayList<BankOffers> mBankOfferses;
    private CarItemModel mCarItemModel;
    private HashMap<Integer, Integer> mapOfMaxEligibilityForATenure;

    public static FinanceOffersFragment getInstance(ArrayList<BankOffers> bankOfferses, CarItemModel model) {
        FinanceOffersFragment fragment = new FinanceOffersFragment();
        fragment.mBankOfferses = bankOfferses;
        fragment.mCarItemModel = model;
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
    }

    private Activity getFragmentActivity() {
        return mActivity;
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = LayoutInflater.from(getFragmentActivity()).inflate(R.layout.finance_offers_from_multiple_bank_layout, null);
        if(savedInstanceState != null)
        {
            mBankOfferses = (ArrayList<BankOffers>) savedInstanceState.getSerializable("bankOffersList");
            mCarItemModel = (CarItemModel) savedInstanceState.getSerializable("carModel");

        }

        tvTenure=(TextView)view.findViewById(R.id.tenure);
        tvLoanAmount=(TextView)view.findViewById(R.id.maxLoanAmount);
        seekBar=(SeekBar)view.findViewById(R.id.seek_bar);
        btnPlus=(Button)view.findViewById(R.id.plusButton);
        btnMinus=(Button)view.findViewById(R.id.minuButton);
        radioGroup=(RadioGroup) view.findViewById(R.id.radioGroup);
        btnApplyNow=(Button)view.findViewById(R.id.applyNowButton);
        rbMaxAmount=(RadioButton) view.findViewById(R.id.rbMaxAmount);
        rbCustom=(RadioButton) view.findViewById(R.id.rbCustom);
        relativeLayout=(RelativeLayout) view.findViewById(R.id.relativeLayout);
        currentTennure=Integer.parseInt(tvTenure.getText().toString().trim());
        btnPlus.setOnClickListener(this);
        btnMinus.setOnClickListener(this);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getFragmentActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        rearrangeBankData();
        mAdapter = new FinanceOffersAdapter(getFragmentActivity(), linkedListOfBankOffers, currentTennure, 0, code);
        mRecyclerView.setAdapter(mAdapter);
        btnApplyNow.setOnClickListener(this);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.rbMaxAmount:
                        relativeLayout.setVisibility(View.GONE);
                        code=0;
                        rearrangeBankData();
                        mAdapter.updateData(currentTennure, principal, code, linkedListOfBankOffers);
                        break;
                    case R.id.rbCustom:
                        mapOfMaxEligibilityForATenure = new HashMap<Integer, Integer>();
                        mapOfMaxEligibilityForATenure.put(currentTennure, getMaxOfAll(currentTennure, 0));
                        principal = getMaxOfAll(currentTennure, 0);
                        mapOfMaxEligibilityForATenure.put(currentTennure, principal);
                        relativeLayout.setVisibility(View.VISIBLE);
                        code=1;
                        seekBar.setMax(principal);
                        seekBar.setProgress(principal);
                        CommonUtils.insertCommaIntoNumber(tvLoanAmount, String.valueOf(principal), "##,##,###");
                        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                                CommonUtils.insertCommaIntoNumber(tvLoanAmount, progress + "", "##,##,###");
                                principal = Integer.parseInt(CommonUtils.convertCommaIntoNumber(tvLoanAmount.getText().toString().split(Constants.RUPEES_SYMBOL)[1].trim(), "#######"));
                                rearrangeBankData();
                                mAdapter.updateData(currentTennure, principal, code, linkedListOfBankOffers);
                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {

                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {

                            }
                        });
                        rearrangeBankData();
                        mAdapter.updateData(currentTennure, principal, code, linkedListOfBankOffers);
                        break;
                }
            }
        });
        return view;
    }

    private void rearrangeBankData() {
        linkedListOfBankOffers = new LinkedList<>();

        for (int i = 0; i < mBankOfferses.size(); i++) {
            if (currentTennure == 1 && mBankOfferses.get(i).get_12month_tenure() != null && Integer.parseInt(mBankOfferses.get(i).get_12month_tenure().getMax_eligibility()) >= principal) {
                linkedListOfBankOffers.add(mBankOfferses.get(i));
            } else if (currentTennure == 2 && mBankOfferses.get(i).get_24month_tenure() != null && Integer.parseInt(mBankOfferses.get(i).get_24month_tenure().getMax_eligibility()) >= principal) {
                linkedListOfBankOffers.add(mBankOfferses.get(i));
            } else if (currentTennure == 3 && mBankOfferses.get(i).get_36month_tenure() != null && Integer.parseInt(mBankOfferses.get(i).get_36month_tenure().getMax_eligibility()) >= principal) {
                linkedListOfBankOffers.add(mBankOfferses.get(i));

            } else if (currentTennure == 4 && mBankOfferses.get(i).get_48month_tenure() != null && Integer.parseInt(mBankOfferses.get(i).get_48month_tenure().getMax_eligibility()) >= principal) {
                linkedListOfBankOffers.add(mBankOfferses.get(i));

            } else if (currentTennure == 5 && mBankOfferses.get(i).get_60month_tenure() != null && Integer.parseInt(mBankOfferses.get(i).get_60month_tenure().getMax_eligibility()) >= principal) {
                linkedListOfBankOffers.add(mBankOfferses.get(i));

            }


        }
        for (int i = 0; i < mBankOfferses.size(); i++) {
            if ((currentTennure == 1 && mBankOfferses.get(i).get_12month_tenure() != null)) {
                if (Integer.parseInt(mBankOfferses.get(i).get_12month_tenure().getMax_eligibility()) < principal) {
                    linkedListOfBankOffers.add(mBankOfferses.get(i));
                }
            } else if ((currentTennure == 2 && mBankOfferses.get(i).get_24month_tenure() != null)) {
                if (Integer.parseInt(mBankOfferses.get(i).get_24month_tenure().getMax_eligibility()) < principal) {
                    linkedListOfBankOffers.add(mBankOfferses.get(i));
                }
            } else if ((currentTennure == 3 && mBankOfferses.get(i).get_36month_tenure() != null)) {
                if (Integer.parseInt(mBankOfferses.get(i).get_36month_tenure().getMax_eligibility()) < principal) {
                    linkedListOfBankOffers.add(mBankOfferses.get(i));
                }

            } else if ((currentTennure == 4 && mBankOfferses.get(i).get_48month_tenure() != null)) {
                if (Integer.parseInt(mBankOfferses.get(i).get_48month_tenure().getMax_eligibility()) < principal) {
                    linkedListOfBankOffers.add(mBankOfferses.get(i));
                }

            } else if ((currentTennure == 5 && mBankOfferses.get(i).get_60month_tenure() != null)) {
                if (Integer.parseInt(mBankOfferses.get(i).get_60month_tenure().getMax_eligibility()) < principal) {
                    linkedListOfBankOffers.add(mBankOfferses.get(i));
                }

            }
        }


        for (int i = 0; i < mBankOfferses.size(); i++) {
            if ((currentTennure == 1 && mBankOfferses.get(i).get_12month_tenure() == null)) {
                linkedListOfBankOffers.add(mBankOfferses.get(i));
            } else if ((currentTennure == 2 && mBankOfferses.get(i).get_24month_tenure() == null)) {
                linkedListOfBankOffers.add(mBankOfferses.get(i));
            } else if ((currentTennure == 3 && mBankOfferses.get(i).get_36month_tenure() == null)) {
                linkedListOfBankOffers.add(mBankOfferses.get(i));

            } else if ((currentTennure == 4 && mBankOfferses.get(i).get_48month_tenure() == null)) {
                linkedListOfBankOffers.add(mBankOfferses.get(i));

            } else if ((currentTennure == 5 && mBankOfferses.get(i).get_60month_tenure() == null)) {
                linkedListOfBankOffers.add(mBankOfferses.get(i));

            }

        }
    }


    private int getMaxOfAll(int currentTennure, int max) {
        for (BankOffers bankOffersObj : mBankOfferses) {
            switch (currentTennure){
                case 1:
                    if(bankOffersObj.get_12month_tenure()!=null && max<Integer.parseInt(bankOffersObj.get_12month_tenure().getMax_eligibility())){
                        max = Integer.parseInt(bankOffersObj.get_12month_tenure().getMax_eligibility());
                    }
                    break;
                case 2:
                    if (bankOffersObj.get_24month_tenure() != null && max < Integer.parseInt(bankOffersObj.get_24month_tenure().getMax_eligibility())) {
                        max = Integer.parseInt(bankOffersObj.get_24month_tenure().getMax_eligibility());
                    }
                    break;
                case 3:
                    if(bankOffersObj.get_36month_tenure()!=null && max<Integer.parseInt(bankOffersObj.get_36month_tenure().getMax_eligibility())){
                        max=Integer.parseInt(bankOffersObj.get_36month_tenure().getMax_eligibility());
                    }
                    break;
                case 4:
                    if(bankOffersObj.get_48month_tenure()!=null && max<Integer.parseInt(bankOffersObj.get_48month_tenure().getMax_eligibility())){
                        max=Integer.parseInt(bankOffersObj.get_48month_tenure().getMax_eligibility());
                    }
                    break;
                case 5:
                    if(bankOffersObj.get_60month_tenure()!=null && max<Integer.parseInt(bankOffersObj.get_60month_tenure().getMax_eligibility())){
                        max=Integer.parseInt(bankOffersObj.get_60month_tenure().getMax_eligibility());
                    }
                    break;
            }

        }
        return max;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("bankOffersList", mBankOfferses);
        outState.putSerializable("carModel",mCarItemModel);
        super.onSaveInstanceState(outState);
    }


    @Override
    public void onClick(View v) {
        currentTennure=Integer.parseInt(tvTenure.getText().toString());
        switch (v.getId()){
            case R.id.applyNowButton:
                ArrayList<FInanceOfferSelectedRequestModel> list=new ArrayList<>();
                for(BankOffers mlistObj: mAdapter.mList){
                    if(mlistObj.isChecked()){
                        if(mAdapter.modelList.containsKey(mlistObj.getBank_id())) {
                            list.add(mAdapter.modelList.get(mlistObj.getBank_id()));

                        }
                    }

                }
                if(list.size()!=0 ) {
                    Fragment fragment = FinanceSelectedOfferFragment.newInstance(mActivity, list, mCarItemModel);
                    getFragmentManager().beginTransaction().replace(R.id.flLoanOffers, fragment).commit();

                }else{
                    Toast.makeText(mActivity.getApplicationContext(),"Please select atleast one offer to proceed",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.plusButton:

                if(currentTennure<5) {
                    currentTennure=currentTennure+1;
                }else{
                    currentTennure=5;
                }
                tvTenure.setText(String.valueOf(currentTennure));
                if(code==1) {
                    mapOfMaxEligibilityForATenure.put(currentTennure, getMaxOfAll(currentTennure, 0));
                    if (mapOfMaxEligibilityForATenure.get(currentTennure).equals(0)) {
                        seekBar.setMax(mapOfMaxEligibilityForATenure.get(currentTennure - 1));
                        seekBar.setProgress(Integer.parseInt(CommonUtils.convertCommaIntoNumber(tvLoanAmount.getText().toString().split(Constants.RUPEES_SYMBOL)[1].trim(), "#######")));
                    } else {
                        seekBar.setMax(getMaxOfAll(currentTennure, 0));
                        seekBar.setProgress(Integer.parseInt(CommonUtils.convertCommaIntoNumber(tvLoanAmount.getText().toString().split(Constants.RUPEES_SYMBOL)[1].trim(), "#######")));
                    }
                }
                else{
                    principal=0;
                }
                rearrangeBankData();
                mAdapter.updateData(currentTennure, principal, code, linkedListOfBankOffers);
                break;
            case R.id.minuButton:

                if (currentTennure > 1) {
                    currentTennure = currentTennure - 1;

                }else{
                    currentTennure = 1;
                }
                tvTenure.setText(String.valueOf(currentTennure));
                if (code == 1) {
                    mapOfMaxEligibilityForATenure.put(currentTennure, getMaxOfAll(currentTennure, 0));
                    if (mapOfMaxEligibilityForATenure.get(currentTennure).equals(0)) {
                        seekBar.setMax(mapOfMaxEligibilityForATenure.get(currentTennure + 1));
                        seekBar.setProgress(Integer.parseInt(CommonUtils.convertCommaIntoNumber(tvLoanAmount.getText().toString().split(Constants.RUPEES_SYMBOL)[1].trim(), "#######")));
                    } else {
                        seekBar.setMax(getMaxOfAll(currentTennure, 0));
                        seekBar.setProgress(Integer.parseInt(CommonUtils.convertCommaIntoNumber(tvLoanAmount.getText().toString().split(Constants.RUPEES_SYMBOL)[1].trim(), "#######")));
                    }

                }else{
                    principal = 0;
                }
                rearrangeBankData();
                mAdapter.updateData(currentTennure, principal, code, linkedListOfBankOffers);
                break;
        }

    }

}
