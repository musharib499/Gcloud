package com.gcloud.gaadi.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Lakshay on 25-05-2015.
 */
public class DocumentInfo implements Serializable {

    private String docName;
    private int tag;
    protected int parentId;
    private String parentCatName;
    private ArrayList<String> images = new ArrayList<>();


    public ArrayList<String> getImages() {
        return images;
    }

    public void setImages(ArrayList<String> images) {
        this.images = images;
    }

    public String getParentCatName() {
        return parentCatName;
    }

    public void setParentCatName(String parentCatName) {
        this.parentCatName = parentCatName;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public String getDocName() {
        return docName;
    }

    public void setDocName(String docName) {
        this.docName = docName;
    }

    public int getTag() {
        return tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }
}
