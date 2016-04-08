package com.gcloud.gaadi.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.gcloud.gaadi.ApplicationController;
import com.gcloud.gaadi.R;
import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.events.AddOnD2DEvent;
import com.gcloud.gaadi.events.EditInactiveInventoryPriceEvent;
import com.gcloud.gaadi.events.OpenListItemEvent;
import com.gcloud.gaadi.interfaces.MoreActionsClickListener;
import com.gcloud.gaadi.model.InactiveInventoriesViewHolder;
import com.gcloud.gaadi.model.InventoryModel;
import com.gcloud.gaadi.ui.swipelistview.SwipeListView;
import com.gcloud.gaadi.utils.GAHelper;

import java.util.ArrayList;

/**
 * Created by ankit on 9/1/15.
 */
public class InActiveInventoriesAdapter extends ArrayAdapter<InventoryModel> implements View.OnClickListener {

    private Context mContext;
    private ArrayList<InventoryModel> mItems;
    private LayoutInflater mInflater;
    private InactiveInventoriesViewHolder mHolder;
    private MoreActionsClickListener moreActionsClickListener;
//  private GAHelper mGAHelper;

    public InActiveInventoriesAdapter(Context context, ArrayList<InventoryModel> items, MoreActionsClickListener moreActionsClickListener) {
        super(context, 0);
        mContext = context;
//    mGAHelper = new GAHelper(mContext);
        mItems = items;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.moreActionsClickListener = moreActionsClickListener;
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public InventoryModel getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.layout_inactive_inventories, parent, false);
            mHolder = new InactiveInventoriesViewHolder();
            mHolder.edit = (RelativeLayout) convertView.findViewById(R.id.edit);
            mHolder.make = (ImageView) convertView.findViewById(R.id.makeLogo);
            mHolder.modelYear = (TextView) convertView.findViewById(R.id.modelYear);
            mHolder.modelVersion = (TextView) convertView.findViewById(R.id.stockModelVersion);
            mHolder.moreOptions = (ImageView) convertView.findViewById(R.id.moreOptions);
            mHolder.kmsDriven = (TextView) convertView.findViewById(R.id.kmsDriven);
            mHolder.color = (ImageView) convertView.findViewById(R.id.color);
            mHolder.stockPrice = (TextView) convertView.findViewById(R.id.stockPrice);
            mHolder.colorValue = (TextView) convertView.findViewById(R.id.colorValue);
            mHolder.stockImage = (ImageView) convertView.findViewById(R.id.stockImage);
            mHolder.modelYear = (TextView) convertView.findViewById(R.id.modelYear);
            mHolder.add = (RelativeLayout) convertView.findViewById(R.id.add);
            mHolder.trustMark = (ImageView) convertView.findViewById(R.id.trustmark);
            mHolder.leadsLayout = (LinearLayout) convertView.findViewById(R.id.leadsLayout);
            mHolder.fuelType = (TextView) convertView.findViewById(R.id.fuelType);


            convertView.setTag(mHolder);

        } else {
            mHolder = (InactiveInventoriesViewHolder) convertView.getTag();

        }

        ((SwipeListView) parent).recycle(convertView, position);

        mHolder.kmsDriven.setText(mItems.get(position).getKmsDriven());
        mHolder.leadsLayout.setVisibility(View.GONE);//Till further modification

        if ("1".equals(mItems.get(position).getTrusmarkCertified())) {
            mHolder.trustMark.setVisibility(View.VISIBLE);
        } else {
            mHolder.trustMark.setVisibility(View.GONE);
        }

        if ((mItems.get(position).getImageIcon() != null) && !mItems.get(position).getImageIcon().isEmpty()) {
            Glide.with(mContext)
                    .load(mItems.get(position).getImageIcon())
                    .placeholder(R.drawable.image_load_default_small)
                    .crossFade()
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(mHolder.stockImage);

        } else {
            Glide.with(mContext)
                    .load(R.drawable.no_image_default_small)
                    .centerCrop()
                    .into(mHolder.stockImage);

        }
        if (mItems.get(position).getHexCode() != null && !mItems.get(position).getHexCode().equals("")) {
            GradientDrawable bgShape = (GradientDrawable) mHolder.color.getBackground();
            bgShape.setColor(Color.parseColor(mItems.get(position).getHexCode()));
        } else {
            GradientDrawable bgShape = (GradientDrawable) mHolder.color.getBackground();
            bgShape.setColor(Color.parseColor("#FFFFFF"));
        }
        mHolder.modelVersion.setText(mItems.get(position).getModelVersion());
        mHolder.modelYear.setText(mItems.get(position).getYear());
        mHolder.make.setImageResource(ApplicationController.makeLogoMap.get(mItems.get(position).getMake()));
        mHolder.moreOptions.setTag(position);
        mHolder.moreOptions.setOnClickListener(this);
        mHolder.colorValue.setText(mItems.get(position).getColor());
        mHolder.edit.setTag(position);
        mHolder.edit.setOnClickListener(this);
        mHolder.add.setTag(position);
        mHolder.add.setOnClickListener(this);
        //mHolder.stockLeads.setText(mItems.get(position).getTotalLeads());
        mHolder.stockPrice.setText(Constants.RUPEES_SYMBOL + " " + mItems.get(position).getDealerPrice());
        mHolder.fuelType.setText(mItems.get(position).getFuel_type());

        return convertView;
    }

    @Override
    public void onClick(View v) {
        int position = (Integer) v.getTag();
        switch (v.getId()) {
            case R.id.edit:
                ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                        Constants.CATEGORY_DEALER_PLATFORM,
                        Constants.CATEGORY_DEALER_PLATFORM,
                        Constants.ACTION_TAP,
                        Constants.LABEL_D2D_INACTIVE_EDIT,
                        0);
                ApplicationController.getEventBus().post(new OpenListItemEvent(position, Constants.CLOSELIST_DEALERINACTIVE));
                ApplicationController.getEventBus().post(new EditInactiveInventoryPriceEvent(mItems.get(position).getId(), mItems.get(position).getDealerPrice(), "UpdatePrice"));
                break;

            case R.id.add:
                ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                        Constants.CATEGORY_DEALER_PLATFORM,
                        Constants.CATEGORY_DEALER_PLATFORM,
                        Constants.ACTION_TAP,
                        Constants.LABEL_ADD_D2D,
                        0);
                ApplicationController.getEventBus().post(new OpenListItemEvent(position, Constants.CLOSELIST_DEALERINACTIVE));
                if (mItems.get(position).getDealerPrice().trim().equalsIgnoreCase("N/A")
                        || mItems.get(position).getDealerPrice().trim().isEmpty()
                        || mItems.get(position).getDealerPrice().trim().equalsIgnoreCase("0")) {
                    ApplicationController.getEventBus().post(new EditInactiveInventoryPriceEvent(mItems.get(position).getId(), mItems.get(position).getDealerPrice(), "UpdatePriceAndAdd"));

                } else {
                    ApplicationController.getEventBus().post(new AddOnD2DEvent(mItems.get(position).getId()));
                }
                break;

            case R.id.moreOptions:
                ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                        Constants.CATEGORY_DEALER_PLATFORM,
                        Constants.CATEGORY_DEALER_PLATFORM,
                        Constants.ACTION_TAP,
                        Constants.LABEL_THREE_DOTS,
                        0);
                this.moreActionsClickListener.clickMoreOptions(position);
                break;
        }
    }
}
