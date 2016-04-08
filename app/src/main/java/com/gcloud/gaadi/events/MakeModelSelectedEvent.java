package com.gcloud.gaadi.events;

import com.gcloud.gaadi.model.VersionObject;

import java.io.Serializable;

/**
 * Created by ankit on 29/12/14.
 */
public class MakeModelSelectedEvent implements Serializable {

    private VersionObject model;

    public MakeModelSelectedEvent(VersionObject model) {
        this.model = model;
    }

    public VersionObject getModel() {
        return model;
    }

    public void setModel(VersionObject model) {
        this.model = model;
    }

    @Override
    public String toString() {
        return "MakeModelSelectedEvent{" +
                "model=" + model +
                '}';
    }
}
