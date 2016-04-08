package com.imageuploadlib.Utils;

import java.io.Serializable;

/**
 * Created by Lakshay on 04-09-2015.
 */
public class ActiveDocumentMetrics implements Serializable {

    int maxNumberOfPhotos;   //0 means no limit

    int minNumberOfTypes;   //atleast this much types of subcategory pictures is required


    public ActiveDocumentMetrics(int maxNumberOfPhotos, int minNumberOfTypes) {
        this.maxNumberOfPhotos = maxNumberOfPhotos;
        this.minNumberOfTypes = minNumberOfTypes;
    }

    public int getMaxNumberOfPhotos() {

        return maxNumberOfPhotos;
    }

    public void setMaxNumberOfPhotos(int maxNumberOfPhotos) {
        this.maxNumberOfPhotos = maxNumberOfPhotos;
    }

    public int getMinNumberOfTypes() {
        return minNumberOfTypes;
    }

    public void setMinNumberOfTypes(int minNumberOfTypes) {
        this.minNumberOfTypes = minNumberOfTypes;
    }
}
