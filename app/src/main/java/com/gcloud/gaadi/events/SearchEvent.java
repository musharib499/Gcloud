package com.gcloud.gaadi.events;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by Seema Pandit on 04-03-2015.
 */
public class SearchEvent implements Serializable {

    private HashMap<String, String> params = new HashMap<String, String>();
    private int currentItem;

    public SearchEvent(HashMap<String, String> params, int currentItem) {
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
