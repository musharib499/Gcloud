package com.gcloud.gaadi.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by ankit on 7/12/14.
 */
public class ModelObject implements Serializable {

    private int id;

    @SerializedName("model_id")
    private String modelId;

    @SerializedName("model")
    private String modelName;

    @SerializedName("make_id")
    private String makeId;

    @SerializedName("makeModel")
    private String makeModel;

    @SerializedName("makename")
    private String makename;

    @SerializedName("rank")
    private String rank;

    @SerializedName("parent_model_id")
    private String parentModelId;

    public String getParentModelId() {
        return parentModelId;
    }

    public void setParentModelId(String parentModelId) {
        this.parentModelId = parentModelId;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public String getMakeModel() {
        return makeModel;
    }

    public void setMakeModel(String makeModel) {
        this.makeModel = makeModel;
    }

    public String getMakename() {
        return makename;
    }

    public void setMakename(String makename) {
        this.makename = makename;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getModelId() {
        return modelId;
    }

    public void setModelId(String modelId) {
        this.modelId = modelId;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getMakeId() {
        return makeId;
    }

    public void setMakeId(String makeId) {
        this.makeId = makeId;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("ModelObject{");
        sb.append("id=").append(id);
        sb.append(", modelId='").append(modelId).append('\'');
        sb.append(", modelName='").append(modelName).append('\'');
        sb.append(", makeId='").append(makeId).append('\'');
        sb.append(", makeModel='").append(makeModel).append('\'');
        sb.append(", makename='").append(makename).append('\'');
        sb.append(", rank='").append(rank).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
