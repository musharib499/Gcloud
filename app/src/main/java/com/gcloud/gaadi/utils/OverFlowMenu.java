package com.gcloud.gaadi.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gcloud.gaadi.R;

/**
 * Created by musharebali on 12/1/16.
 */
public class OverFlowMenu {
    public static TextView tv;
    public static void OverFlowMenuText(Context context, String st, int testSize, Menu menu)
    {
         int testsize1=16;
        testsize1=testSize;
        tv = new TextView(context);
        tv.setText(st);
        tv.setBackgroundColor(context.getResources().getColor(R.color.orange));
        tv.setTextColor(context.getResources().getColor(R.color.white));
        tv.setPadding(0, 0, 20, 0);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 10, 0);

        tv.setLayoutParams(params);
        tv.setTypeface(null, Typeface.NORMAL);
        tv.setTextSize(testsize1);
        menu.add(0, 0, 1, st).setActionView(tv).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);



    }
}
