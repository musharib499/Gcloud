package com.gcloud.gaadi.events;

import java.io.Serializable;

/**
 * Created by ankit on 23/1/15.
 */
public class MakeInsertEvent implements Serializable {
    private boolean error;
    private String message;

    public MakeInsertEvent(boolean error, String message) {
        this.error = error;
        this.message = message;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
