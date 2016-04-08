package com.gcloud.gaadi.events;

import com.gcloud.gaadi.model.InsuranceInspectedCarData;
import com.gcloud.gaadi.model.QuoteDetails;

import java.io.Serializable;

/**
 * Created by ankitgarg on 28/05/15.
 */
public class InsuranceInspectedCarSelectedEvent implements Serializable {

    private InsuranceInspectedCarData inspectedCarData;

    private String agentId;
    QuoteDetails quoteDetails;

    public QuoteDetails getQuoteDetails() {
        return quoteDetails;
    }

    public void setQuoteDetails(QuoteDetails quoteDetails) {
        this.quoteDetails = quoteDetails;
    }

    public InsuranceInspectedCarSelectedEvent(InsuranceInspectedCarData inspectedCarData, String agentId) {
        this.inspectedCarData = inspectedCarData;

        this.agentId = agentId;
    }

    public InsuranceInspectedCarSelectedEvent(QuoteDetails quoteDetails, String agentId) {
        this.quoteDetails = quoteDetails;

        this.agentId = agentId;
    }

    public InsuranceInspectedCarData getInspectedCarData() {
        return inspectedCarData;
    }

    public void setInspectedCarData(InsuranceInspectedCarData inspectedCarData) {
        this.inspectedCarData = inspectedCarData;
    }


    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("InsuranceInspectedCarSelectedEvent{");
        sb.append("inspectedCarData=").append(inspectedCarData);

        sb.append(", agentId='").append(agentId).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
