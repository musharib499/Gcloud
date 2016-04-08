package com.gcloud.gaadi.model;

import java.io.Serializable;

/**
 * Created by dipanshugarg on 20/1/16.
 */
public class LeadFilterModel implements Serializable {

    private String budgetfrom = "";

    private String budgetto = "";

    private String source = "";

    private String leadStatus = "";

    private int sourceId = 0;

    private int statusId = 0;

    public String getBudgetfrom() {
        return budgetfrom;
    }

    public void setBudgetfrom(String budgetfrom) {
        this.budgetfrom = budgetfrom;
    }

    public String getBudgetto() {
        return budgetto;
    }

    public void setBudgetto(String budgetto) {
        this.budgetto = budgetto;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getLeadStatus() {
        return leadStatus;
    }

    public void setLeadStatus(String leadStatus) {
        this.leadStatus = leadStatus;
    }

    public int getSourceId() {
        return sourceId;
    }

    public void setSourceId(int sourceId) {
        this.sourceId = sourceId;
    }

    public int getStatusId() {
        return statusId;
    }

    public void setStatusId(int statusId) {
        this.statusId = statusId;
    }


    public void reset() {
        budgetfrom = "";
        budgetto = "";
        source = "";
        leadStatus = "";
        sourceId = 0;
        statusId = 0;

    }

}
