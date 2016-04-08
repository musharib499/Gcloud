package com.gcloud.gaadi.model.Finance;

import com.gcloud.gaadi.model.DocumentCategories;
import com.gcloud.gaadi.model.GeneralResponse;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by lakshaygirdhar on 17/10/15.
 */
public class GetReuploadItemsResponse extends GeneralResponse implements Serializable {


    ArrayList<ImageData> imageData;
    @SerializedName("documents")
    private ArrayList<DocumentCategories> documentCategoriesArrayList;

    public ArrayList<DocumentCategories> getDocumentCategoriesArrayList() {
        return documentCategoriesArrayList;
    }

    public void setDocumentCategoriesArrayList(ArrayList<DocumentCategories> documentCategoriesArrayList) {
        this.documentCategoriesArrayList = documentCategoriesArrayList;
    }

    public ArrayList<ImageData> getImageData() {
        return imageData;
    }

    public void setImageData(ArrayList<ImageData> imageData) {
        this.imageData = imageData;
    }
}
