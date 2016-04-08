package com.gcloud.gaadi.model;

import java.io.Serializable;

public class PhotoInfo implements Serializable {
    private static final long serialVersionUID = -272916521021943068L;
    private String path;
    private int orientation;
    private String pid;
    private String propId;

    public PhotoInfo() {
    }

    public PhotoInfo(String path) {
        this.path = path;
    }

    public PhotoInfo(String path, int orientation) {
        this.path = path;
        this.orientation = orientation;
    }

    public int getOrientation() {
        return orientation;
    }

    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public boolean equals(Object o) {
        boolean equals = super.equals(o);
        if (equals) {
            return true;
        }
        if (o instanceof PhotoInfo) {
            if (this.path.equals(((PhotoInfo) o).getPath())) {
                return true;
            }
        }
        return equals;
    }


    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getPropId() {
        return propId;
    }

    public void setPropId(String propId) {
        this.propId = propId;
    }

    @Override
    public String toString() {
        return "PhotoInfo [path=" + path + ", orientation=" + orientation + "]";
    }

}
