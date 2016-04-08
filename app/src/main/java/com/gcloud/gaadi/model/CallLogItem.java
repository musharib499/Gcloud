package com.gcloud.gaadi.model;

import java.io.Serializable;

/**
 * Created by ankit on 26/12/14.
 */
public class CallLogItem implements Serializable {

    private String name;
    private String number;
    private String type;
    private String callSource;


    public CallLogItem(String name, String number, String type, String callSource) {
        this.name = name;
        this.number = number;
        this.type = type;
        this.callSource = callSource;
    }

    public String getCallSource() {
        return callSource;
    }

    public void setCallSource(String callSource) {
        this.callSource = callSource;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getGaadiFormatNumber() {
        return number.substring(Math.max(0, number.length() - 10));
    }

    @Override
    public String toString() {
        return "CallLogItem{" +
                "name='" + name + '\'' +
                ", number='" + number + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
