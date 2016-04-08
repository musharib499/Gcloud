package com.gcloud.gaadi.model.Finance;

import com.gcloud.gaadi.model.DocumentCategories;
import com.gcloud.gaadi.model.GeneralResponse;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by priyarawat on 3/3/16.
 */
public class UpdateFinanceLeadModel extends GeneralResponse {
    @SerializedName("documents")
    private ArrayList<DocumentCategories> documentCategoriesArrayList;

    public ArrayList<DocumentCategories> getDocumentCategoriesArrayList() {
        return documentCategoriesArrayList;
    }

    public void setDocumentCategoriesArrayList(ArrayList<DocumentCategories> documentCategoriesArrayList) {
        this.documentCategoriesArrayList = documentCategoriesArrayList;
    }
}
