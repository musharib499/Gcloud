package com.gcloud.gaadi.events;

import java.io.Serializable;

/**
 * Created by ankitgarg on 30/05/15.
 */
public class UpdateScreenEvent implements Serializable {

    private SCREEN_TYPE screenType;

    public UpdateScreenEvent(SCREEN_TYPE screenType) {
        this.screenType = screenType;
    }

    public SCREEN_TYPE getScreenType() {
        return screenType;
    }

    public void setScreenType(SCREEN_TYPE screenType) {
        this.screenType = screenType;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("UpdateScreenEvent{");
        sb.append("screenType=").append(screenType);
        sb.append('}');
        return sb.toString();
    }

    public enum SCREEN_TYPE {
        INSURANCE_INSPECTED_CARS,
        INSURACE_CASES
    }
}
