package com.gcloud.gaadi.model;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Created by ankit on 5/1/15.
 */
public class BasicListItemModel implements Serializable {

    int[] arrayValue;
    private String id;
    private String value;
    private boolean checked;

    public BasicListItemModel(String id, String value) {
        this.id = id;
        this.value = value;
    }

    public BasicListItemModel(String id, int[] arrayValue) {
        this.id = id;
        this.arrayValue = arrayValue;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("BasicListItemModel{");
        sb.append("arrayValue=").append(Arrays.toString(arrayValue));
        sb.append(", id='").append(id).append('\'');
        sb.append(", value='").append(value).append('\'');
        sb.append(", checked=").append(checked);
        sb.append('}');
        return sb.toString();
    }
}
