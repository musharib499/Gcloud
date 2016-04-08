package com.gcloud.gaadi.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.widget.CursorAdapter;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.gcloud.gaadi.model.GeneralResponse;
import com.gcloud.gaadi.model.StockDetailModel;
import com.gcloud.gaadi.model.StockItemViewHolder;
import com.gcloud.gaadi.model.UpdatePriceModel;
import com.gcloud.gaadi.retrofit.RetrofitRequest;
import com.gcloud.gaadi.ui.CarLeadsActivity;
import com.gcloud.gaadi.ui.StockAddActivity;
import com.gcloud.gaadi.utils.CommonUtils;
import com.gcloud.gaadi.utils.GCLog;

import java.net.UnknownHostException;
import java.util.HashMap;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static com.gcloud.gaadi.providers.ViewStockProvider.CONTENT_URI;

/**
 * Created by alokmishra on 26/2/16.
 */
public class RemovedStocksAdapter extends CursorAdapter implements  View.OnClickListener{
    private final LayoutInflater cursorInflater;
    private Context mContext;
    private String shareTextString;
    private String stockIdString;
    private String modelVersionString;
    private int makeString;
    private String urlString;
    private StockDetailModel stockDetailModel2;
    private StockItemViewHolder holder;

    public RemovedStocksAdapter(Context activity, Cursor cursor) {
        super(activity,cursor);
        mContext = activity;
        cursorInflater = (LayoutInflater) activity.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View convertView = cursorInflater.inflate(R.layout.removed_stocks_row_layout, parent, false);

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
        holder.edit = (RelativeLayout) convertView.findViewById(R.id.edit);
        holder.add = (RelativeLayout) convertView.findViewById(R.id.add);
        holder.stockLeads = (TextView) convertView.findViewById(R.id.stockLeads);
        holder.kmsDriven = (TextView) convertView.findViewById(R.id.kmsDriven);
        holder.leadsLayout = (LinearLayout) convertView.findViewById(R.id.leadsLayout);
        holder.trustMark = (ImageView) convertView.findViewById(R.id.trustmark);
        holder.fuelType = (TextView) convertView.findViewById(R.id.fuelType);

        holder.edit.setOnClickListener(this);
        holder.add.setOnClickListener(this);
        holder.moreOptions.setOnClickListener(this);
        holder.imageLayout.setOnClickListener(this);

        convertView.setTag(holder);

        return convertView;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        holder = (StockItemViewHolder) view.getTag();
        if(cursor != null) {
            stockIdString = cursor.getString(cursor.getColumnIndex(ViewStockModel.STOCK_ID));
            modelVersionString = cursor.getString(cursor.getColumnIndex(ViewStockModel.MODEL_VERSION));
            makeString = cursor.getInt(cursor.getColumnIndex(ViewStockModel.MAKE));
            String modelYearText = cursor.getString(cursor.getColumnIndex(ViewStockModel.MODEL_YEAR));
            String kmsText = cursor.getString(cursor.getColumnIndex(ViewStockModel.KMS));
            urlString = cursor.getString(cursor.getColumnIndex(ViewStockModel.IMAGE_ICON));
            String totalLeadsString = cursor.getString(cursor.getColumnIndex(ViewStockModel.TOTAL_LEADS));
            String stockPriceText = cursor.getString(cursor.getColumnIndex(ViewStockModel.STOCK_PRICE));
            String colourText = cursor.getString(cursor.getColumnIndex(ViewStockModel.COLOR));
            shareTextString = cursor.getString(cursor.getColumnIndex(ViewStockModel.SHARE_TEXT));
            String hexCodeText = cursor.getString(cursor.getColumnIndex(ViewStockModel.HEX_CODE));

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
            //viewStockListItem.setTag(position);
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

            holder.kmsDriven.setText(kmsText);
            holder.fuelType.setText(cursor.getString(cursor.getColumnIndex(ViewStockModel.FUEL_TYPE)));

            holder.modelYear.setText(modelYearText);
            holder.modelVersion.setText(modelVersionString);
            holder.make.setImageResource(ApplicationController.makeLogoMap.get(makeString));

//            mHolder.make.setImageResource(ApplicationController.makeLogoMap.get(mItems.get(position).getMake()));
            int curPosition = cursor.getPosition();
            holder.moreOptions.setTag(curPosition);
            holder.colorValue.setText(colourText);

            holder.add.setTag(curPosition);
            holder.edit.setTag(curPosition);

            holder.imageLayout.setTag(curPosition);

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
        }
    }

    @Override
    public void onClick(View v) {
        int position = (int) v.getTag();
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
                ApplicationController.getEventBus().post(new OpenListItemEvent(position, Constants.REMOVED_STOCKS));

                GCLog.e("position clicked: " + position);
                break;
            case R.id.sms:
                ApplicationController.getEventBus().post(new OpenListItemEvent(position, Constants.REMOVED_STOCKS));
                ApplicationController.getEventBus().post(new ShareTypeEvent(ShareType.SMS, shareTextString, stockIdString, Constants.STOCKS));
                GCLog.e("position clicked for sms: " + position);
                break;

            case R.id.email:
                ApplicationController.getEventBus().post(new OpenListItemEvent(position, Constants.REMOVED_STOCKS));
                ShareTypeEvent shareTypeEvent = new ShareTypeEvent(ShareType.EMAIL,shareTextString,stockIdString, Constants.STOCKS);
                shareTypeEvent.setExtraText(stockIdString);
                ApplicationController.getEventBus().post(shareTypeEvent);
                GCLog.e("position clicked for email: " + position);
                break;

            case R.id.whatsapp:
                String url = "";
                if (urlString!= null && !urlString.trim().isEmpty())
                    url = urlString;
                ApplicationController.getEventBus().post(new OpenListItemEvent(position, Constants.REMOVED_STOCKS));
                ApplicationController.getEventBus().post(new ShareTypeEvent(ShareType.WHATSAPP, shareTextString,stockIdString, url, Constants.STOCKS));
                break;

            case R.id.bringToTop:
                ApplicationController.getEventBus().post(new OpenListItemEvent(position, Constants.REMOVED_STOCKS));
                ApplicationController.getEventBus().post(new ActionEvent(ActionType.BRING_TOP, DBFunction.getSingleStockModel(cursr)));
                break;

            case R.id.add:
                ApplicationController.getEventBus().post(new OpenListItemEvent(position, Constants.REMOVED_STOCKS));
                makeAddToStockRequest(stockIdString);
                break;

            case R.id.edit:
                ApplicationController.getEventBus().post(new OpenListItemEvent(position, Constants.REMOVED_STOCKS));
                editStock(stockIdString);
                break;
        }
    }


    private void editStock(String position) {

        makeStockDetailRequest(position);
    }

    private void makeStockDetailRequest(String carId) {

        //resetAllViews();
        //showProgressBar();
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(Constants.DEALER_USERNAME, CommonUtils.getStringSharedPreference(mContext, Constants.UC_DEALER_USERNAME, ""));
        params.put(Constants.UCDID, String.valueOf(CommonUtils.getIntSharedPreference(mContext, Constants.UC_DEALER_ID, -1)));
        params.put(Constants.CAR_ID, carId);
        params.put(Constants.METHOD_LABEL, Constants.STOCK_DETAIL_METHOD);
        params.put(Constants.API_RESPONSE_FORMAT_LABEL, Constants.API_RESPONSE_FORMAT);
        params.put(Constants.API_KEY_LABEL, Constants.API_KEY);

        RetrofitRequest.stockViewRequest(params, new Callback<StockDetailModel>() {
            @Override
            public void success(StockDetailModel stockDetailModel, Response response) {
                stockDetailModel2 = stockDetailModel;
                boolean canEdit = "0".equals(stockDetailModel.getEditable());
//        GCLog.e(Constants.TAG, "Stock detail model: " + stockDetailModel.toString());
                if (canEdit) {

                    Intent intent = new Intent(mContext, StockAddActivity.class);
                    Bundle args = new Bundle();
                    args.putString(Constants.COMING_FROM, "SV");
                    args.putBoolean(Constants.CAN_EDIT, canEdit);
                    args.putString(Constants.STOCK_ID, stockDetailModel.getStockId());
                    args.putString(Constants.CITY_NAME, stockDetailModel.getRegistrationPlace());
                    args.putString(Constants.REGISTRATION_NUMBER, stockDetailModel.getRegistrationNumber());
                    args.putString(Constants.NUM_OWNERS, stockDetailModel.getOwnerNumber());
                    args.putString(Constants.INSURANCE_TYPE, stockDetailModel.getInsurance());
                    args.putString(Constants.TAX_TYPE, stockDetailModel.getTax());
                    args.putString(Constants.SELL_TO_DEALER, stockDetailModel.getSellToDealer());
                    args.putString(Constants.DEALER_PRICE, stockDetailModel.getEditDealerPrice());
                    args.putString(Constants.STOCK_MONTH, stockDetailModel.getModelMonth());
                    args.putString(Constants.STOCK_YEAR, stockDetailModel.getModelYear());
                    args.putString(Constants.MAKE, stockDetailModel.getMake());
                    args.putString(Constants.MODEL, stockDetailModel.getModel());
                    args.putString(Constants.VERSION, stockDetailModel.getVersion());
                    args.putString(Constants.KMS_DRIVEN, stockDetailModel.getEditKm());
                    args.putString(Constants.STOCK_PRICE, stockDetailModel.getEditPrice());
                    args.putString(Constants.STOCK_COLOR, stockDetailModel.getColorValue());
                    args.putString(Constants.INSURANCE_MONTH, stockDetailModel.getInsuranceMonth());
                    args.putString(Constants.INSURANCE_YEAR, stockDetailModel.getYear());
                    args.putString(Constants.CNG_FITTED, stockDetailModel.getCngFitted());
                    args.putString(Constants.SHOWROOM_NAME, stockDetailModel.getShowroom());
                    args.putString(Constants.HEXCODE, stockDetailModel.getHexCode());
                    args.putString(Constants.SHOWROOM_ID, stockDetailModel.getShowroomId());
                    args.putBoolean("fromRemoved", true);
                    args.putStringArrayList(Constants.STOCK_IMAGES, stockDetailModel.getArrayImages());
                    intent.putExtras(args);

                    mContext.startActivity(intent);

                } else {

                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    View view = ((Activity) mContext).getLayoutInflater().inflate(R.layout.layout_update_price_dialog, null);
                    TextView textView = (TextView) view.findViewById(R.id.text);
                    final EditText editText = (EditText) view.findViewById(R.id.editText);
                    editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                    textView.setText("This stock has been marked certified and cannot be edited. You can however update the price.\n\nCurrent Price: " + stockDetailModel.getStockPrice());
                    builder.setView(view);
                    builder.setPositiveButton(R.string.done_label, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String updatedPrice = editText.getText().toString().trim();
                            if (updatedPrice.length() < 4) {
                                CommonUtils.showToast(mContext, "Please enter valid amount.", Toast.LENGTH_SHORT);
                            } else {
                                if (Integer.parseInt(updatedPrice) > Integer.parseInt(stockDetailModel2.getEditDealerPrice())) {
                                    makeUpdatePriceRequest(updatedPrice);
                                } else {
                                    CommonUtils.showToast(mContext, "Car price should be greater than dealer price", Toast.LENGTH_SHORT);
                                }
                            }
                        }
                    });
                    builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });


                    AlertDialog dialog = builder.create();

                    if (!((Activity) mContext).isFinishing() && !dialog.isShowing()) {
                        dialog.show();
                    }

                    //CommonUtils.showToast(this, "Stock cannot be edited.", Toast.LENGTH_SHORT);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                if (error.getCause() instanceof UnknownHostException) {
                    CommonUtils.showToast(mContext, mContext.getString(R.string.network_connection_error), Toast.LENGTH_SHORT);
                } else {
                    CommonUtils.showToast(mContext, mContext.getString(R.string.server_error_message), Toast.LENGTH_SHORT);
                }
            }
        });
    }

    private void makeUpdatePriceRequest(String updatedPrice) {
        HashMap<String, String> params = new HashMap<>();
        params.put(Constants.CAR_ID, stockDetailModel2.getStockId());
        params.put(Constants.UPDATED_STOCK_PRICE, updatedPrice);
        params.put(Constants.API_KEY_LABEL, Constants.API_KEY);
        params.put(Constants.API_RESPONSE_FORMAT_LABEL, Constants.API_RESPONSE_FORMAT);
        params.put(Constants.METHOD_LABEL, Constants.UPDATE_PRICE_METHOD);
        params.put(Constants.DEALER_USERNAME, CommonUtils.getStringSharedPreference(mContext, Constants.UC_DEALER_USERNAME, ""));
        RetrofitRequest.EditPriceRequest(params, new Callback<UpdatePriceModel>() {
            @Override
            public void success(UpdatePriceModel updatePriceModel, Response response) {
                if ("T".equalsIgnoreCase(updatePriceModel.getStatus())) {

                    CommonUtils.showToast(mContext, updatePriceModel.getMessage(), Toast.LENGTH_SHORT);
                    ApplicationController.getEventBus().post("refresh");
                          /*  if (fromRemoved) {
                                onActivityResult(Constants.ACTION_EDIT, Activity.RESULT_OK, null);
                            } else {
                                makeStockDetailRequest(false);
                            }*/
                } else {
                    CommonUtils.showToast(mContext, updatePriceModel.getErrorMessage(), Toast.LENGTH_SHORT);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                if(error.getCause()!= null) {
                    if (error.getCause() instanceof UnknownHostException) {
                        CommonUtils.showToast(mContext, mContext.getString(R.string.network_connection_error), Toast.LENGTH_SHORT);
                    } else {
                        CommonUtils.showToast(mContext, mContext.getString(R.string.server_error_message), Toast.LENGTH_SHORT);
                    }
                }
            }
        });
    }

    private void makeAddToStockRequest(String carId) {
        ApplicationController.getEventBus().post("call progress dialog");
        HashMap<String, String> params = new HashMap<>();
        params.put(Constants.CAR_ID, carId);
        params.put(Constants.METHOD_LABEL, Constants.METHOD_ADD_TO_STOCK);

        RetrofitRequest.addToStock(params, new Callback<GeneralResponse>() {
            @Override
            public void success(GeneralResponse generalResponse, Response response) {
                if ("T".equalsIgnoreCase(generalResponse.getStatus())) {
                    ApplicationController.getEventBus().post("refresh");
                } else {
                    ApplicationController.getEventBus().post("dismiss progress dialog");
                }
            }

            @Override
            public void failure(RetrofitError error) {
                ApplicationController.getEventBus().post("dismiss progress dialog");
                GCLog.e(error.getMessage());
                if(error.getCause() != null) {
                    if (error.getCause() instanceof UnknownHostException) {
                        CommonUtils.showToast(mContext, mContext.getString(R.string.network_connection_error), Toast.LENGTH_SHORT);
                    } else {
                        CommonUtils.showToast(mContext, mContext.getString(R.string.server_error_message), Toast.LENGTH_SHORT);
                    }
                }
            }
        });
    }

}
