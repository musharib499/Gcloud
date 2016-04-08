package com.gcloud.gaadi.events;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by Lakshay on 20-01-2015.
 */

public class FilterEvent implements Serializable {

    private HashMap<String, String> params = new HashMap<String, String>();
    private int currentItem;

    public FilterEvent(HashMap<String, String> params, int currentItem) {
        this.currentItem = currentItem;
        this.params = params;
    }

    public int getCurrentItem() {
        return currentItem;
    }

    public void setCurrentItem(int currentItem) {
        this.currentItem = currentItem;
    }

    public HashMap<String, String> getParams() {
        return params;
    }

    public void setParams(HashMap<String, String> params) {
        this.params = params;
    }

}
