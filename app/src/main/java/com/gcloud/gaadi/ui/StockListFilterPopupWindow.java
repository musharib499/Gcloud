package com.gcloud.gaadi.ui;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;

import com.gcloud.gaadi.interfaces.OnApplyFilterListener;

/**
 * Created by ankit on 31/12/14.
 */
public class StockListFilterPopupWindow extends PopupWindow {

    private View mContentView;
    private OnApplyFilterListener mListener;

    public StockListFilterPopupWindow(Context context, int resourceId) {
        super(context);
        mContentView = LayoutInflater.from(context).inflate(resourceId, null);
        setContentView(mContentView);
        setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        setWidth(WindowManager.LayoutParams.WRAP_CONTENT);

        setOutsideTouchable(true);
        setFocusable(true);
        setBackgroundDrawable(new BitmapDrawable());
    }

    public void show(View actionBarView) {
        int x = actionBarView.getMeasuredHeight() - mContentView.getMeasuredHeight();
        showAsDropDown(actionBarView, x, 0);
    }

    public View getView() {
        return mContentView;
    }

    public void setFilterListener(OnApplyFilterListener listener) {
        mListener = listener;
    }

}
