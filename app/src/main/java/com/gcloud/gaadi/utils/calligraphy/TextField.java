package com.gcloud.gaadi.utils.calligraphy;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.gcloud.gaadi.R;

/**
 * Created by chris on 17/03/15.
 * For Calligraphy.
 */
public class TextField extends TextView {

    public TextField(final Context context, final AttributeSet attrs) {
        super(context, attrs, R.attr.textFieldStyle);
    }

}
