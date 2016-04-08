package com.gcloud.gaadi.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by ankit on 3/12/14.
 */
public class Version implements Serializable {

    @SerializedName("count")
    private int count;

    @SerializedName("version")
    private ArrayList<VersionObject> versions;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public ArrayList<VersionObject> getVersions() {
        return versions;
    }

    public void setVersions(ArrayList<VersionObject> versions) {
        this.versions = versions;
    }

    @Override
    public String toString() {
        return "Version{" +
                "count=" + count +
                ", versions=" + versions +
                '}';
    }
}
