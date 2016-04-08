package com.gcloud.gaadi.events;

import java.io.Serializable;

/**
 * Created by shivani on 18/3/16.
 */
public class SetMonthYearForPicker implements Serializable {
    private int currentItem;
    private String text;

    public SetMonthYearForPicker(String text, int currentItem) {
        this.text = text;
        this.currentItem = currentItem;
    }

    public int getCurrentItem() {
        return currentItem;
    }

    public void setCurrentItem(int currentItem) {
        this.currentItem = currentItem;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
