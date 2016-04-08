package com.gcloud.gaadi.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Lakshay on 27-05-2015.
 */
public class DocumentCategories implements Serializable {

    @SerializedName("id")
    String id;

    @SerializedName("category_name")
    String categoryName;

    @SerializedName("child")
    ArrayList<SubCategory> subCategories;

    @SerializedName("parent_id")
    String parentID;

    public String getParentID() {
        return parentID;
    }

    public void setParentID(String parentID) {
        this.parentID = parentID;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public ArrayList<SubCategory> getSubCategories() {
        return subCategories;
    }

    public void setSubCategories(ArrayList<SubCategory> subCategories) {
        this.subCategories = subCategories;
    }

    @Override
    public String toString() {
        return "DocumentCategories{" +
                "id='" + id + '\'' +
                ", categoryName='" + categoryName + '\'' +
                ", subCategories=" + subCategories +
                ", parentID='" + parentID + '\'' +
                '}';
    }
}
