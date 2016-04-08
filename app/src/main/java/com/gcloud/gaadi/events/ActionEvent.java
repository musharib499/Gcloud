package com.gcloud.gaadi.events;

import com.gcloud.gaadi.constants.ActionType;
import com.gcloud.gaadi.model.StockItemModel;

import java.io.Serializable;

/**
 * Created by ankit on 19/1/15.
 */
public class ActionEvent implements Serializable {

    private ActionType actionType;
    private StockItemModel stockItem;

    public ActionEvent(ActionType actionType, StockItemModel item) {
        this.actionType = actionType;
        this.stockItem = item;
    }

    public ActionType getActionType() {
        return actionType;
    }

    public void setActionType(ActionType actionType) {
        this.actionType = actionType;
    }

    public StockItemModel getStockItem() {
        return stockItem;
    }

    public void setStockItem(StockItemModel stockItem) {
        this.stockItem = stockItem;
    }

    @Override
    public String toString() {
        return "ActionEvent{" +
                "actionType=" + actionType +
                ", stockItem=" + stockItem +
                '}';
    }
}
