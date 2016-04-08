package com.gcloud.gaadi.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class CommentsModel implements Serializable {
    @SerializedName("status")
    private String status;

    @SerializedName("msg")
    private String message;
    @SerializedName("error")
    private String errorMessage;
    @SerializedName("commentList")
    private ArrayList<Comments> comments;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public ArrayList<Comments> getComments() {
        return comments;
    }

    public void setComments(ArrayList<Comments> comments) {
        this.comments = comments;
    }
}
