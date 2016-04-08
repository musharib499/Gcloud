package com.gcloud.gaadi.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Lakshay on 26-06-2015.
 */
public class ReuploadItems implements Serializable {

    ArrayList<String> selectedPhotos;

    String name;
    String selectedTag;
    ArrayList<DocType> docTypes;

    public String getSelectedTag() {
        return selectedTag;
    }

    public void setSelectedTag(String selectedTag) {
        this.selectedTag = selectedTag;
    }

    public ArrayList<String> getSelectedPhotos() {
        return selectedPhotos;
    }

    public void setSelectedPhotos(ArrayList<String> selectedPhotos) {
        this.selectedPhotos = selectedPhotos;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<DocType> getDocTypes() {
        return docTypes;
    }

    public void setDocTypes(ArrayList<DocType> docTypes) {
        this.docTypes = docTypes;
    }

    @Override
    public String toString() {
        return "ReuploadItems{" +
                "selectedPhotos=" + selectedPhotos +
                ", name='" + name + '\'' +
                ", selectedTag='" + selectedTag + '\'' +
                ", docTypes=" + docTypes +
                '}';
    }
}
