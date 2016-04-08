package com.gcloud.gaadi.ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListPopupWindow;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.gcloud.gaadi.R;
import com.gcloud.gaadi.constants.SortType;
import com.gcloud.gaadi.interfaces.OnSortTypeListener;
import com.gcloud.gaadi.utils.GCLog;

/**
 * Created by ankit on 31/12/14.
 */
public class StockListSortPopupWindow extends PopupWindow implements View.OnClickListener {

    static int lastSelectedPosition;
    private View mContentView;
    private Context mContext;
    private OnSortTypeListener mListener;
    private RelativeLayout sortPriceASC;
    private RelativeLayout sortPriceDESC;
    private RelativeLayout sortKmASC;
    private RelativeLayout sortKmDESC;
    private RelativeLayout sortYearASC;
    private RelativeLayout sortYearDESC;

    public StockListSortPopupWindow(Context context, int resourceId) {
        super(context);
        mContext = context;
        mContentView = LayoutInflater.from(context).inflate(resourceId, null);
        setContentView(mContentView);
        setHeight(ListPopupWindow.WRAP_CONTENT);
        setWidth(ListPopupWindow.WRAP_CONTENT);

        setOutsideTouchable(true);
        setFocusable(true);
        setBackgroundDrawable(new BitmapDrawable());


        sortPriceASC = (RelativeLayout) mContentView.findViewById(R.id.sort_by_price_asc);
        sortPriceASC.setOnClickListener(this);

        sortPriceDESC = (RelativeLayout) mContentView.findViewById(R.id.sort_by_price_desc);
        sortPriceDESC.setOnClickListener(this);

        sortKmASC = (RelativeLayout) mContentView.findViewById(R.id.sort_by_km_asc);
        sortKmASC.setOnClickListener(this);

        sortKmDESC = (RelativeLayout) mContentView.findViewById(R.id.sort_by_km_desc);
        sortKmDESC.setOnClickListener(this);

        sortYearASC = (RelativeLayout) mContentView.findViewById(R.id.sort_by_year_asc);
        sortYearASC.setOnClickListener(this);

        sortYearDESC = (RelativeLayout) mContentView.findViewById(R.id.sort_by_year_desc);
        sortYearDESC.setOnClickListener(this);
        GCLog.e("last selected pos" + lastSelectedPosition);
        if (lastSelectedPosition != 0)
            mContentView.findViewById(lastSelectedPosition).setBackgroundColor(Color.parseColor("#ffffff"));
    }

    public void show(View actionBarView) {
        int x = actionBarView.getMeasuredHeight() - mContentView.getMeasuredHeight();
        showAsDropDown(actionBarView, x, 0);

    }

    public View getView() {
        return mContentView;
    }

    public void setSortTypeListener(OnSortTypeListener listener) {
        mListener = listener;
    }

    @Override
    public void onClick(View v) {
        lastSelectedPosition = v.getId();

        switch (v.getId()) {
            case R.id.sort_by_price_asc:
                mListener.onSortType(SortType.PRICE_ASC);
                this.dismiss();
                break;

            case R.id.sort_by_price_desc:
                mListener.onSortType(SortType.PRICE_DESC);
                this.dismiss();
                break;

            case R.id.sort_by_km_asc:
                mListener.onSortType(SortType.KM_ASC);
                this.dismiss();
                break;

            case R.id.sort_by_km_desc:
                mListener.onSortType(SortType.KM_DESC);
                this.dismiss();
                break;

            case R.id.sort_by_year_asc:
                mListener.onSortType(SortType.YEAR_ASC);
                this.dismiss();
                break;

            case R.id.sort_by_year_desc:
                mListener.onSortType(SortType.YEAR_DESC);
                this.dismiss();
                break;
        }
    }
}
