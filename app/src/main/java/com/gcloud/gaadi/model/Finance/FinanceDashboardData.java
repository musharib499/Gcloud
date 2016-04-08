
package com.gcloud.gaadi.model.Finance;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;


public class FinanceDashboardData {

    @SerializedName("total_leads_count")
    private TotalLeadsCount totalLeadsCount;
    @SerializedName("month_wise_count")
    private List<MonthWiseCount> monthWiseCount = new ArrayList<MonthWiseCount>();

    /**
     * 
     * @return
     *     The totalLeadsCount
     */
    public TotalLeadsCount getTotalLeadsCount() {
        return totalLeadsCount;
    }

    /**
     * 
     * @param totalLeadsCount
     *     The total_leads_count
     */
    public void setTotalLeadsCount(TotalLeadsCount totalLeadsCount) {
        this.totalLeadsCount = totalLeadsCount;
    }

    /**
     * 
     * @return
     *     The monthWiseCount
     */
    public List<MonthWiseCount> getMonthWiseCount() {
        return monthWiseCount;
    }

    /**
     * 
     * @param monthWiseCount
     *     The month_wise_count
     */
    public void setMonthWiseCount(List<MonthWiseCount> monthWiseCount) {
        this.monthWiseCount = monthWiseCount;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("FinanceDashboardData{");
        sb.append("totalLeadsCount=").append(totalLeadsCount);
        sb.append(", monthWiseCount=").append(monthWiseCount);
        sb.append('}');
        return sb.toString();
    }
}
