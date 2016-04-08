
package com.gcloud.gaadi.model.Finance;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class MonthWiseCount {

    @SerializedName("year")
    @Expose
    private String year = "";
    @SerializedName("month")
    @Expose
    private String month = "";
    @SerializedName("pending")
    @Expose
    private String pending;
    @SerializedName("approved")
    @Expose
    private String approved;
    @SerializedName("rejected")
    @Expose
    private String rejected;
    @SerializedName("disbursed")
    @Expose
    private String disbursed;
    @SerializedName("total")
    @Expose
    private Integer total;
    @SerializedName("disbursed_amount")
    @Expose
    private String disbursedAmount;

    /**
     * 
     * @return
     *     The year
     */
    public String getYear() {
        return year;
    }

    /**
     * 
     * @param year
     *     The year
     */
    public void setYear(String year) {
        this.year = year;
    }

    /**
     * 
     * @return
     *     The month
     */
    public String getMonth() {
        return month;
    }

    /**
     * 
     * @param month
     *     The month
     */
    public void setMonth(String month) {
        this.month = month;
    }

    /**
     * 
     * @return
     *     The pending
     */
    public String getPending() {
        return pending;
    }

    /**
     * 
     * @param pending
     *     The pending
     */
    public void setPending(String pending) {
        this.pending = pending;
    }

    /**
     * 
     * @return
     *     The approved
     */
    public String getApproved() {
        return approved;
    }

    /**
     * 
     * @param approved
     *     The approved
     */
    public void setApproved(String approved) {
        this.approved = approved;
    }

    /**
     * 
     * @return
     *     The rejected
     */
    public String getRejected() {
        return rejected;
    }

    /**
     * 
     * @param rejected
     *     The rejected
     */
    public void setRejected(String rejected) {
        this.rejected = rejected;
    }

    /**
     * 
     * @return
     *     The disbursed
     */
    public String getDisbursed() {
        return disbursed;
    }

    /**
     * 
     * @param disbursed
     *     The disbursed
     */
    public void setDisbursed(String disbursed) {
        this.disbursed = disbursed;
    }

    /**
     * 
     * @return
     *     The total
     */
    public Integer getTotal() {
        return total;
    }

    /**
     * 
     * @param total
     *     The total
     */
    public void setTotal(Integer total) {
        this.total = total;
    }

    /**
     * 
     * @return
     *     The disbursedAmount
     */
    public String getDisbursedAmount() {
        return disbursedAmount;
    }

    /**
     * 
     * @param disbursedAmount
     *     The disbursed_amount
     */
    public void setDisbursedAmount(String disbursedAmount) {
        this.disbursedAmount = disbursedAmount;
    }

    @Override
    public String toString() {
        return "MonthWiseCount{" +
                "year='" + year + '\'' +
                ", month='" + month + '\'' +
                ", pending='" + pending + '\'' +
                ", approved='" + approved + '\'' +
                ", rejected='" + rejected + '\'' +
                ", disbursed='" + disbursed + '\'' +
                ", total=" + total +
                ", disbursedAmount='" + disbursedAmount + '\'' +
                '}';
    }

}
