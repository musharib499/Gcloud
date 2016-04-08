package com.gcloud.gaadi.events;

/**
 * Created by ankit on 18/11/14.
 */
public class AppInitializationEvent {
    private String message;
    private int logoTop;

    public AppInitializationEvent(String message, int logoTop) {
        this.message = message;
        this.logoTop = logoTop;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getLogoTop() {
        return logoTop;
    }

    public void setLogoTop(int logoTop) {
        this.logoTop = logoTop;
    }
}
