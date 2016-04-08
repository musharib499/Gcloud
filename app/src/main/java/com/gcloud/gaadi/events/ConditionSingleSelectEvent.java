package com.gcloud.gaadi.events;

import com.gcloud.gaadi.constants.ConditionType;
import com.gcloud.gaadi.model.BasicListItemModel;

import java.io.Serializable;

/**
 * Created by ankit on 15/1/15.
 */
public class ConditionSingleSelectEvent implements Serializable {
    private String id;
    private BasicListItemModel item;
    private ConditionType conditionType;

    public ConditionSingleSelectEvent(String id, BasicListItemModel item, ConditionType conditionType) {
        this.id = id;
        this.item = item;
        this.conditionType = conditionType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public BasicListItemModel getItem() {
        return item;
    }

    public void setItem(BasicListItemModel value) {
        this.item = value;
    }

    public ConditionType getConditionType() {
        return conditionType;
    }

    public void setConditionType(ConditionType conditionType) {
        this.conditionType = conditionType;
    }

    @Override
    public String toString() {
        return "ConditionSingleSelectEvent{" +
                "id='" + id + '\'' +
                ", value='" + item + '\'' +
                ", conditionType=" + conditionType +
                '}';
    }
}
