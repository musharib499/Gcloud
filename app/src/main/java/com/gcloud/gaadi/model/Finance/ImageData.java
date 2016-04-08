package com.gcloud.gaadi.model.Finance;

import java.io.Serializable;

/**
 * Created by lakshaygirdhar on 17/10/15.
 */
public class ImageData implements Serializable {

    String parent_cat_id;

    public String getParent_cat_id() {
        return parent_cat_id;
    }

    public void setParent_cat_id(String parent_cat_id) {
        this.parent_cat_id = parent_cat_id;
    }
}
