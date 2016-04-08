package com.gcloud.gaadi.ui;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListPopupWindow;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.gcloud.gaadi.R;
import com.gcloud.gaadi.interfaces.OnApplyFilterListener;
import com.gcloud.gaadi.utils.CommonUtils;

/**
 * Created by ankit on 5/1/15.
 */
public class SharePopupWindow extends PopupWindow implements View.OnClickListener {

    RelativeLayout email, sms, whatsapp;
    String shareText;
    private View mContentView;
    private Context mContext;
    private OnApplyFilterListener mListener;

    public SharePopupWindow(Context context, int resourceId) {
        super(context);
        mContext = context;
        mContentView = LayoutInflater.from(context).inflate(resourceId, null);
        setContentView(mContentView);
        setHeight(ListPopupWindow.WRAP_CONTENT);
        setWidth(ListPopupWindow.WRAP_CONTENT);

        setOutsideTouchable(true);
        setFocusable(true);
        setBackgroundDrawable(new BitmapDrawable());

        email = (RelativeLayout) mContentView.findViewById(R.id.sendEmail);
        email.setOnClickListener(this);

        sms = (RelativeLayout) mContentView.findViewById(R.id.sendSMS);
        sms.setOnClickListener(this);

        whatsapp = (RelativeLayout) mContentView.findViewById(R.id.sendWhatsapp);
        whatsapp.setOnClickListener(this);

    }

    public void show(View actionBarView) {
        int x = actionBarView.getMeasuredHeight() - mContentView.getMeasuredHeight();
        showAsDropDown(actionBarView, x, 0);
    }

    public void setShareText(String shareText) {
        this.shareText = shareText;
    }

    public View getView() {
        return mContentView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sendEmail:
                CommonUtils.showToast(mContext, shareText, Toast.LENGTH_SHORT);
                this.dismiss();
                break;

            case R.id.sendSMS:
                CommonUtils.showToast(mContext, shareText, Toast.LENGTH_SHORT);
                this.dismiss();
                break;

            case R.id.sendWhatsapp:
                CommonUtils.showToast(mContext, shareText, Toast.LENGTH_SHORT);
                this.dismiss();
                break;
        }
    }
}
