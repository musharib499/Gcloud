package com.gcloud.gaadi.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Lakshay on 27-05-2015.
 */
public class SubCategory implements Serializable {

    @SerializedName("id")
    int id;

    @SerializedName("category_name")
    String categoryName;

    @SerializedName("parent_id")
    String parentId;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    @Override
    public String toString() {
        return "SubCategory{" +
                "id=" + id +
                ", categoryName='" + categoryName + '\'' +
                ", parentId='" + parentId + '\'' +
                '}';
    }
}
