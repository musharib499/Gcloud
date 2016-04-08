package com.gcloud.gaadi.events;

/**
 * Created by ankit on 10/12/14.
 */
public class InitialRequestCompleteEvent {
    private int counter;
    private RequestType requestType;

    public InitialRequestCompleteEvent(int counter, RequestType requestType) {
        this.counter = counter;
        this.requestType = requestType;
    }

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

    public RequestType getRequestType() {
        return requestType;
    }

    public void setRequestType(RequestType requestType) {
        this.requestType = requestType;
    }

    @Override
    public String toString() {
        return "InitialRequestCompleteEvent{" +
                "counter=" + counter +
                ", requestType=" + requestType +
                '}';
    }

    public enum RequestType {
        MAKE_REQUEST,
        MODEL_REQUEST,
        VERSION_REQUEST,
        CITY_REQUEST,
        REMOVE_INVENTORY,
        MARK_SOLD_INVENTORY,
        SOLD_WITHOUT_WARRANTY,
        SOLD_WITH_WARRANTY,
        ISSUE_WARRANTY,
        MODEL_SYNC,
        DEALER_TO_DEALER_MODEL_NO_CHANGE_REQ
    }
}
