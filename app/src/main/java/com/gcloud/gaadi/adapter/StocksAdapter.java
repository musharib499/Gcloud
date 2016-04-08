package com.gcloud.gaadi.adapter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.gcloud.gaadi.ApplicationController;
import com.gcloud.gaadi.R;
import com.gcloud.gaadi.constants.ActionType;
import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.constants.ShareType;
import com.gcloud.gaadi.db.DBFunction;
import com.gcloud.gaadi.db.ViewStockModel;
import com.gcloud.gaadi.events.ActionEvent;
import com.gcloud.gaadi.events.OpenListItemEvent;
import com.gcloud.gaadi.events.ShareTypeEvent;
import com.gcloud.gaadi.model.StockItemViewHolder;
import com.gcloud.gaadi.ui.CarLeadsActivity;
import com.gcloud.gaadi.utils.GCLog;

/**
 * Created by alokmishra on 26/2/16.
 */
public class StocksAdapter extends CursorAdapter implements  View.OnClickListener{
    private LayoutInflater cursorInflater;
    private Context mContext;
    private String shareTextString;
    private String stockIdString;
    private String modelVersionString;
    private int makeString;
    private String urlString;
    private StockItemViewHolder holder;

    public StocksAdapter(Context activity, Cursor cursor) {
        super(activity,cursor);
        mContext = activity;
        cursorInflater = (LayoutInflater) mContext.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View convertView = cursorInflater.inflate(R.layout.layout_stocks_adapter, parent, false);
        holder = new StockItemViewHolder();
        holder.viewStockListItem = (RelativeLayout) convertView.findViewById(R.id.view_stock_list_item);
        holder.stockImage = (ImageView) convertView.findViewById(R.id.stockImage);
        holder.color = (ImageView) convertView.findViewById(R.id.color);
        holder.imageLayout = (RelativeLayout) convertView.findViewById(R.id.imageLayout);
        holder.stockPrice = (TextView) convertView.findViewById(R.id.stockPrice);
        holder.make = (ImageView) convertView.findViewById(R.id.makeLogo);
        holder.modelVersion = (TextView) convertView.findViewById(R.id.stockModelVersion);
        holder.moreOptions = (ImageView) convertView.findViewById(R.id.moreOptions);
        holder.modelYear = (TextView) convertView.findViewById(R.id.modelYear);
        holder.colorValue = (TextView) convertView.findViewById(R.id.colorValue);
        holder.sms = (RelativeLayout) convertView.findViewById(R.id.sms);
        holder.email = (RelativeLayout) convertView.findViewById(R.id.email);
        holder.stockLeads = (TextView) convertView.findViewById(R.id.stockLeads);
        holder.whatsapp = (RelativeLayout) convertView.findViewById(R.id.whatsapp);
        holder.bringToTop = (RelativeLayout) convertView.findViewById(R.id.bringToTop);
        holder.kmsDriven = (TextView) convertView.findViewById(R.id.kmsDriven);
        holder.leadsLayout = (LinearLayout) convertView.findViewById(R.id.leadsLayout);
        holder.trustMark = (ImageView) convertView.findViewById(R.id.trustmark);
        holder.fuelType = (TextView) convertView.findViewById(R.id.fuelType);
        holder.sms.setOnClickListener(this);
        holder.moreOptions.setOnClickListener(this);
        holder.imageLayout.setOnClickListener(this);
        holder.email.setOnClickListener(this);
        holder.whatsapp.setOnClickListener(this);
        holder.bringToTop.setOnClickListener(this);
        convertView.setTag(holder);
        return convertView;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        holder = (StockItemViewHolder) view.getTag();
        stockIdString = getCursor().getString(cursor.getColumnIndex(ViewStockModel.STOCK_ID));
        modelVersionString = getCursor().getString(cursor.getColumnIndex(ViewStockModel.MODEL_VERSION));
        makeString = getCursor().getInt(cursor.getColumnIndex(ViewStockModel.MAKE));
        String modelYearText = getCursor().getString(cursor.getColumnIndex(ViewStockModel.MODEL_YEAR));
        String kmsText = getCursor().getString(cursor.getColumnIndex(ViewStockModel.KMS));
        urlString = getCursor().getString(cursor.getColumnIndex(ViewStockModel.IMAGE_ICON));
        String totalLeadsString = getCursor().getString(cursor.getColumnIndex(ViewStockModel.TOTAL_LEADS));
        String stockPriceText = getCursor().getString(cursor.getColumnIndex(ViewStockModel.STOCK_PRICE));
        String colourText = getCursor().getString(cursor.getColumnIndex(ViewStockModel.COLOR));
        shareTextString = getCursor().getString(cursor.getColumnIndex(ViewStockModel.SHARE_TEXT));
        String hexCodeText = getCursor().getString(cursor.getColumnIndex(ViewStockModel.HEX_CODE));

        GradientDrawable bgShape = (GradientDrawable) holder.color.getBackground();
        if (hexCodeText != null && !hexCodeText.equals("")) {
            bgShape.setColor(Color.parseColor(hexCodeText));
        } else {
            bgShape.setColor(Color.parseColor("#FFFFFF"));
        }
        //trustMark.setVisibility(View.VISIBLE);
        if ("1".equals(cursor.getString(cursor.getColumnIndex(ViewStockModel.TRUST_MARK_CERTIFY)))) {
            holder.trustMark.setVisibility(View.VISIBLE);
        } else {
            holder.trustMark.setVisibility(View.GONE);
        }
        if ((urlString != null) && !urlString.trim().isEmpty()) {
            Glide.with(mContext)
                    .load(urlString)
                    .placeholder(R.drawable.no_image_default_small)
                    .crossFade()
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.stockImage);
        } else {
            Glide.with(mContext)
                    .load(R.drawable.no_image_default_small)
                    .centerCrop()
                    .into(holder.stockImage);

        }
        int curPosition=cursor.getPosition();

        holder.kmsDriven.setText(kmsText);
        holder.fuelType.setText(cursor.getString(cursor.getColumnIndex(ViewStockModel.FUEL_TYPE)));

        holder.modelYear.setText(modelYearText);
        holder.modelVersion.setText(modelVersionString);
        holder.make.setImageResource(ApplicationController.makeLogoMap.get(makeString));
        holder.moreOptions.setTag(curPosition);
        holder.colorValue.setText(colourText);
        holder.sms.setTag(curPosition);
        holder.email.setTag(curPosition);
        holder.imageLayout.setTag(curPosition);
        holder.whatsapp.setTag(curPosition);
        int totalLeads = Integer.parseInt(totalLeadsString);
        String totalLeadsText = "";
        if (totalLeads == 0) {
            holder.leadsLayout.setVisibility(View.GONE);
            holder.imageLayout.setClickable(false);
            holder.imageLayout.setEnabled(false);
        } else if (totalLeads == 1) {
            holder.leadsLayout.setVisibility(View.VISIBLE);
            totalLeadsText = "1 Lead";
            holder.imageLayout.setClickable(true);
            holder.imageLayout.setEnabled(true);
        } else if (totalLeads < 99) {
            holder.leadsLayout.setVisibility(View.VISIBLE);
            totalLeadsText = totalLeads + " Leads";
            holder.imageLayout.setClickable(true);
            holder.imageLayout.setEnabled(true);
        } else if (totalLeads > 99) {
            holder.leadsLayout.setVisibility(View.VISIBLE);
            totalLeadsText = "99+ Leads";
            holder.imageLayout.setClickable(true);
            holder.imageLayout.setEnabled(true);
        }
        holder.stockLeads.setText(totalLeadsText);
        holder.stockPrice.setText(Constants.RUPEES_SYMBOL + " " + stockPriceText);
        holder.bringToTop.setTag(curPosition);
    }

    @Override
    public void onClick(View v) {
        int position = (Integer) v.getTag();
        Cursor cursr = (Cursor)getItem(position);
        String stockIdString = cursr.getString(cursr.getColumnIndex(ViewStockModel.CAR_ID));
        String shareTextString = cursr.getString(cursr.getColumnIndex(ViewStockModel.SHARE_TEXT));
        String urlString = cursr.getString(cursr.getColumnIndex(ViewStockModel.IMAGE_ICON));
        switch (v.getId()) {

            case R.id.imageLayout:
                Intent intent = new Intent(mContext, CarLeadsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString(Constants.CAR_ID, stockIdString);
                bundle.putInt(Constants.MAKE,cursr.getInt(cursr.getColumnIndex(ViewStockModel.MAKE)));
                bundle.putString(Constants.MODELVERSION, cursr.getString(cursr.getColumnIndex(ViewStockModel.MODEL_VERSION)));
                intent.putExtras(bundle);
                mContext.startActivity(intent);
                break;
            case R.id.moreOptions:
                ApplicationController.getEventBus().post(new OpenListItemEvent(position, Constants.AVAILABLE_STOCKS));

                GCLog.e("position clicked: " + position);
                break;

            case R.id.sms:
                ApplicationController.getEventBus().post(new OpenListItemEvent(position, Constants.AVAILABLE_STOCKS));
                ApplicationController.getEventBus().post(new ShareTypeEvent(ShareType.SMS, shareTextString,stockIdString, Constants.STOCKS));
                GCLog.e("position clicked for sms: " + position);
                break;

            case R.id.email:
                ApplicationController.getEventBus().post(new OpenListItemEvent(position, Constants.AVAILABLE_STOCKS));
                ShareTypeEvent shareTypeEvent = new ShareTypeEvent(ShareType.EMAIL, shareTextString,stockIdString, Constants.STOCKS);
                shareTypeEvent.setExtraText(stockIdString);
                ApplicationController.getEventBus().post(shareTypeEvent);
                GCLog.e("position clicked for email: " + position);
                break;

            case R.id.whatsapp:
                String url = "";
                if (urlString != null && !urlString.trim().isEmpty())
                    url = urlString;
                ApplicationController.getEventBus().post(new OpenListItemEvent(position, Constants.AVAILABLE_STOCKS));
                ApplicationController.getEventBus().post(new ShareTypeEvent(ShareType.WHATSAPP, shareTextString,stockIdString, url, Constants.STOCKS));
                break;

            case R.id.bringToTop:
                ApplicationController.getEventBus().post(new OpenListItemEvent(position, Constants.AVAILABLE_STOCKS));
                ApplicationController.getEventBus().post(new ActionEvent(ActionType.BRING_TOP, DBFunction.getSingleStockModel(cursr)));
                break;
        }
    }

}
