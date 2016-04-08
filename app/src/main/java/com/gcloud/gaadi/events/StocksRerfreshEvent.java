package com.gcloud.gaadi.events;

import java.util.HashMap;

/**
 * Created by priyarawat on 2/12/15.
 */
public class StocksRerfreshEvent {
    HashMap<String, String> params = new HashMap<>();
    int fragmentType = 2;

    public int getFragmentType() {
        return fragmentType;
    }

    public void setFragmentType(int fragmentType) {
        this.fragmentType = fragmentType;
    }

    public HashMap<String, String> getParams() {
        return params;
    }

    public void setParams(HashMap<String, String> params) {
        this.params = params;
    }

    public StocksRerfreshEvent(HashMap<String, String> params, int fragmentType) {
     this.params = params;
        this.fragmentType = fragmentType;
    }
}
