package com.gcloud.gaadi.model;

import java.io.Serializable;

/**
 * Created by Lakshay on 27-05-2015.
 */
public class DocumentInfos implements Serializable {

    DocumentCategories documentCategories;

    public DocumentCategories getDocumentCategories() {
        return documentCategories;
    }

    public void setDocumentCategories(DocumentCategories documentCategories) {
        this.documentCategories = documentCategories;
    }
}
