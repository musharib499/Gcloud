package com.gcloud.gaadi.events;

import java.io.Serializable;

/**
 * Created by Lakshay on 14-01-2015.
 */
public class OpenListItemEvent implements Serializable {
    int position;
    int source;

    public OpenListItemEvent(int position, int source) {
        this.position = position;
        this.source = source;
    }

    public int getSource() {
        return source;
    }

    public void setSource(int source) {
        this.source = source;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
