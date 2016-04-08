package com.gcloud.gaadi.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.gcloud.gaadi.R;

/**
 * Created by ankit on 18/11/14.
 */
public class GCProgressDialog extends Dialog {

    private Activity activity;
    private FragmentActivity fragmentActivity;
    public GCProgressDialog(Context context, Activity activity, String showMessage) {
        super(context, R.style.progressDialog);
        setContentView(R.layout.progressdialog_layout);

        this.activity = activity;
        (findViewById(R.id.progressMessage)).setVisibility(View.VISIBLE);
        if (showMessage != null) {
            ((TextView) findViewById(R.id.progressMessage)).setText(showMessage);
        }

    }

    public GCProgressDialog(Context context) {
        super(context, R.style.progressDialog);
        setContentView(R.layout.progressdialog_layout);

    }

    public GCProgressDialog(Context context, Activity activity) {
        super(context, R.style.progressDialog);
        setContentView(R.layout.progressdialog_layout);
        this.activity = activity;

    }
    public GCProgressDialog(Context context, Activity activity, String showMessage,int drawable_image) {
        super(context,R.style.full_screen_dialog);
        setContentView(R.layout.progress_with_image_dialog);
        this.activity = activity;
        (findViewById(R.id.progressMessage)).setVisibility(View.VISIBLE);
        if (showMessage != null) {
            ((TextView) findViewById(R.id.progressMessage)).setText(showMessage);
        }
        ImageView imageView= (ImageView) findViewById(R.id.img_pro_bg);
        imageView.setBackgroundResource(drawable_image);
    }
    public GCProgressDialog(Context context, Activity activity, String showMessage, String url_image) {
        super(context,android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        setContentView(R.layout.progress_with_image_dialog);
        this.activity = activity;
        (findViewById(R.id.progressMessage)).setVisibility(View.VISIBLE);
        if (showMessage != null) {
            ((TextView) findViewById(R.id.progressMessage)).setText(showMessage);
        }
        ImageView imageView= (ImageView) findViewById(R.id.img_pro_bg);
        Glide.with(context)
                .load(url_image)
                .placeholder(R.drawable.insurance_loading_bg)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .crossFade()
                .fitCenter()
                .into(imageView);
    }
   /* public void ShowImageWithProgress()
    {
       // (findViewById(R.id.lay_container)).setBackgroundColor(Color.GRAY);
        TextView textView= (TextView) findViewById(R.id.progressMessage);
        textView.setTextColor(Color.BLACK);
        findViewById(R.id.pro_back_image).setVisibility(View.VISIBLE);

    }*/

    public GCProgressDialog(Context context, FragmentActivity fragmentActivity) {
        super(context, R.style.progressDialog);
        setContentView(R.layout.progressdialog_layout);
        this.fragmentActivity = fragmentActivity;

    }



    /* (non-Javadoc)
     * @see android.app.Dialog#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    /* (non-Javadoc)
     * @see android.app.Dialog#show()
     */
    @Override
    public void show() {

        if (activity != null) {
            if (!activity.isFinishing() && !this.isShowing()) {
                super.show();

            }
        } else if (fragmentActivity != null) {
            if (!fragmentActivity.isFinishing() && !this.isShowing()) {
                super.show();

            }
        }

    }

    /* (non-Javadoc)
     * @see android.app.Dialog#setCancelable(boolean)
     */
    @Override
    public void setCancelable(boolean flag) {
        super.setCancelable(flag);
    }

    /* (non-Javadoc)
     * @see android.app.Dialog#setCanceledOnTouchOutside(boolean)
     */
    @Override
    public void setCanceledOnTouchOutside(boolean cancel) {
        super.setCanceledOnTouchOutside(false);
    }

    @Override
    public void dismiss() {
        if (activity != null) {
            if (!activity.isFinishing() && this.isShowing()) {
                super.dismiss();

            }
        } else if (fragmentActivity != null) {
            if (!fragmentActivity.isFinishing() && this.isShowing()) {
                super.dismiss();

            }
        }
    }
}
