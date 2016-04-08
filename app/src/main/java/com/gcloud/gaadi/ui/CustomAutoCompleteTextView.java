package com.gcloud.gaadi.ui;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AutoCompleteTextView;

import com.gcloud.gaadi.ApplicationController;
import com.gcloud.gaadi.R;
import com.gcloud.gaadi.constants.MakeModelType;
import com.gcloud.gaadi.events.CancelMakeEvent;
import com.gcloud.gaadi.events.CancelModelEvent;
import com.gcloud.gaadi.events.ClearDealershipListEvent;
import com.gcloud.gaadi.utils.GCLog;

/**
 * Created by ankit on 27/12/14.
 */
public class CustomAutoCompleteTextView extends AutoCompleteTextView {

    Context context;
    private Drawable imgCloseButton = getResources().getDrawable(R.drawable.close_layer_dark);
    private MakeModelType type;

    public CustomAutoCompleteTextView(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public CustomAutoCompleteTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomAutoCompleteTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void setType(MakeModelType type) {
        this.type = type;
    }

    private void init() {
        // Set bounds of the Clear button so it will look ok
        imgCloseButton.setBounds(0, 0, imgCloseButton.getIntrinsicWidth(), imgCloseButton.getIntrinsicHeight());

        // There may be initial text in the field, so we may need to display the  button
        handleClearButton();

        //if the Close image is displayed and the user remove his finger from the button, clear it. Otherwise do nothing
        this.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                CustomAutoCompleteTextView et = CustomAutoCompleteTextView.this;

                if (et.getCompoundDrawables()[2] == null)
                    return false;

                if (event.getAction() != MotionEvent.ACTION_UP)
                    return false;

                if (event.getX() > et.getWidth() - et.getPaddingRight() - imgCloseButton.getIntrinsicWidth()) {
                    et.setText("");
                    CustomAutoCompleteTextView.this.handleClearButton();
                }
                return false;
            }
        });

        //if text changes, take care of the button
        this.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                CustomAutoCompleteTextView.this.handleClearButton();
            }

            @Override
            public void afterTextChanged(Editable arg0) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
        });
    }

    void handleClearButton() {
        if (this.type != null) {
            switch (this.type) {
                case MAKE:
                    if (this.getText().toString().equals("")) {
                        // add the clear button
                        this.setCompoundDrawables(null, this.getCompoundDrawables()[1], null, this.getCompoundDrawables()[3]);

                        ApplicationController.getEventBus().post(new CancelMakeEvent());
                        GCLog.e("FIREDEVENT ");
                    } else {
                        //remove clear button
                        this.setCompoundDrawables(this.getCompoundDrawables()[0], this.getCompoundDrawables()[1], imgCloseButton, this.getCompoundDrawables()[3]);
                    }
                    break;

                case MODEL:
                    if (this.getText().toString().trim().equals("")) {
                        this.setCompoundDrawables(null, this.getCompoundDrawables()[1], null, this.getCompoundDrawables()[3]);
                        ApplicationController.getEventBus().post(new CancelModelEvent());
                        GCLog.e("FIREDEVENT ");
                    } else {
                        //remove clear button
                        this.setCompoundDrawables(this.getCompoundDrawables()[0], this.getCompoundDrawables()[1], imgCloseButton, this.getCompoundDrawables()[3]);
                    }
                    break;
                case CITY:
                    if (this.getText().toString().equals("")) {
                        // add the clear button
                        ApplicationController.getEventBus().post(new ClearDealershipListEvent());
                    } else {
                        //remove clear button
                        this.setCompoundDrawables(this.getCompoundDrawables()[0], this.getCompoundDrawables()[1], imgCloseButton, this.getCompoundDrawables()[3]);
                    }
                    break;
            }
        } else {
            if (this.getText().toString().equals("")) {
                // add the clear button

                this.setCompoundDrawables(null, this.getCompoundDrawables()[1], null, this.getCompoundDrawables()[3]);
            } else {
                //remove clear button
                this.setCompoundDrawables(this.getCompoundDrawables()[0], this.getCompoundDrawables()[1], imgCloseButton, this.getCompoundDrawables()[3]);
            }
        }
    }
}
