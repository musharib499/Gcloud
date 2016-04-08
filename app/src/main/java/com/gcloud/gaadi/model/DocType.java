package com.gcloud.gaadi.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Lakshay on 26-06-2015.
 */
public class DocType implements Serializable {

    String docType;
    String tag;
    String parent;
    ArrayList<String> imagesSelected;

    public ArrayList<String> getImagesSelected() {
        return imagesSelected;
    }

    public void setImagesSelected(ArrayList<String> imagesSelected) {
        this.imagesSelected = imagesSelected;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public String getDocType() {
        return docType;
    }

    public void setDocType(String docType) {
        this.docType = docType;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
