package com.gcloud.gaadi.ui;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;

/**
 * Created by ankit on 24/11/14.
 */
public class DealerSearchPopupWindow extends PopupWindow {

    private View mContentView;

    public DealerSearchPopupWindow(Context context, int resourceId) {
        super(context);
        mContentView = LayoutInflater.from(context).inflate(resourceId, null);
        setContentView(mContentView);
        setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        setWidth(WindowManager.LayoutParams.WRAP_CONTENT);

        setOutsideTouchable(true);
        setFocusable(true);
        setBackgroundDrawable(new BitmapDrawable());

    }

    public void show(final View actionBarView) {
        final int x = actionBarView.getMeasuredHeight() - mContentView.getMeasuredHeight();

        Runnable showRunnable = new Runnable() {
            @Override
            public void run() {
                showAsDropDown(actionBarView, x, 0);
            }
        };
        showRunnable.run();

    }

    public View getView() {
        return mContentView;
    }
}
