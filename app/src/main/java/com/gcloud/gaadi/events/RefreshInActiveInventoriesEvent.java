package com.gcloud.gaadi.events;

import java.io.Serializable;

/**
 * Created by ankit on 21/1/15.
 */
public class RefreshInActiveInventoriesEvent implements Serializable {
    private boolean refresh;

    public RefreshInActiveInventoriesEvent(boolean refresh) {
        this.refresh = refresh;
    }

    public boolean isRefresh() {
        return refresh;
    }

    public void setRefresh(boolean refresh) {
        this.refresh = refresh;
    }
}
