package com.gcloud.gaadi.events;

import com.gcloud.gaadi.constants.ConditionType;

import java.io.Serializable;

/**
 * Created by ankit on 14/1/15.
 */
public class ConditionSelectedEvent implements Serializable {

    private String ids;
    private String values;
    private int count;
    private ConditionType conditionType;

    public ConditionSelectedEvent(String ids, String values, int count, ConditionType conditionType) {
        this.ids = ids;
        this.values = values;
        this.count = count;
        this.conditionType = conditionType;
    }

    public String getIds() {
        return ids;
    }

    public void setIds(String ids) {
        this.ids = ids;
    }

    public String getValues() {
        return values;
    }

    public void setValues(String values) {
        this.values = values;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public ConditionType getConditionType() {
        return conditionType;
    }

    public void setConditionType(ConditionType conditionType) {
        this.conditionType = conditionType;
    }

    @Override
    public String toString() {
        return "ConditionSelectedEvent{" +
                "ids='" + ids + '\'' +
                ", values='" + values + '\'' +
                ", count=" + count +
                ", conditionType=" + conditionType +
                '}';
    }
}
