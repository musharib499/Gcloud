package com.gcloud.gaadi.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Priya on 30-04-2015.
 */
public class ColorModel implements Serializable {


   /* private String name;
    private boolean hexCode;

    public BasicListItemModel(String name, String hexcode) {
        this.id = id;
        this.value = value;
    }*/

    @SerializedName("name")
    private String name;
    @SerializedName("hexCode")
    private String hexCode;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHexCode() {
        return hexCode;
    }

    public void setHexCode(String hexCode) {
        this.hexCode = hexCode;
    }

}
