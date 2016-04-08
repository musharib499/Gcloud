package com.gcloud.gaadi.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by ankit on 3/12/14.
 */
public class Model implements Serializable {

    @SerializedName("count")
    private int count;

    @SerializedName("model")
    private ArrayList<ModelObject> models;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public ArrayList<ModelObject> getModels() {
        return models;
    }

    public void setModels(ArrayList<ModelObject> models) {
        this.models = models;
    }

    @Override
    public String toString() {
        return "Model{" +
                "count=" + count +
                ", models=" + models +
                '}';
    }
}
