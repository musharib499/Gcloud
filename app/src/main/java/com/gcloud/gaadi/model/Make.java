package com.gcloud.gaadi.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by ankit on 3/12/14.
 */
public class Make implements Serializable {

    @SerializedName("count")
    private int count;

    @SerializedName("make")
    private ArrayList<MakeObject> makeObjects;


    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public ArrayList<MakeObject> getMakeObjects() {
        return makeObjects;
    }

    public void setMakeObjects(ArrayList<MakeObject> makeObjects) {
        this.makeObjects = makeObjects;
    }

    @Override
    public String toString() {
        return "Make{" +
                "count=" + count +
                ", makeObjects=" + makeObjects +
                '}';
    }

    /*public class Models implements Serializable {

        @SerializedName("total_models")
        private int totalModels;

        @SerializedName("models")
        private ArrayList<Model> models;

        public int getTotalModels() {
            return totalModels;
        }

        public void setTotalModels(int totalModels) {
            this.totalModels = totalModels;
        }

        public ArrayList<Model> getModels() {
            return models;
        }

        public void setModels(ArrayList<Model> models) {
            this.models = models;
        }

        @Override
        public String toString() {
            return "Models{" +
                    "totalModels=" + totalModels +
                    ", models=" + models +
                    '}';
        }
    }*/
}
