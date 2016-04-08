package com.gallerylib;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Priya on 20-03-2015.
 */
public class ParamsToDisplay implements Serializable {
    private ArrayList<String> imagesUrlList;
    private String makeModelVersion;
    private int imgSelectedPosition;

    public String getMakeModelVersion() {
        return makeModelVersion;
    }

    public void setMakeModelVersion(String makeModelVersion) {
        this.makeModelVersion = makeModelVersion;
    }

    public ArrayList<String> getImagesUrlList() {
        return imagesUrlList;
    }

    public void setImagesUrlList(ArrayList<String> imagesUrlList) {
        this.imagesUrlList = imagesUrlList;
    }


    public int getImgSelectedPosition() {
        return imgSelectedPosition;
    }

    public void setImgSelectedPosition(int imgSelectedPosition) {
        this.imgSelectedPosition = imgSelectedPosition;
    }
}
