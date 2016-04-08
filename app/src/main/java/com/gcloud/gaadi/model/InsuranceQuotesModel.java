package com.gcloud.gaadi.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Ambujesh on 5/27/2015.
 */
public class InsuranceQuotesModel implements Serializable {


    @SerializedName("status")
    private String status;

    @SerializedName("msg")
    private String message;

    @SerializedName("total_records")
    private int totalRecords;

    @SerializedName("error")
    private String error;

    @SerializedName("car_id")
    private String carId;

    @SerializedName("process_id")
    private String processId;

    @SerializedName("idv_range")
    private String idvRange;

    @SerializedName("idv_slider_steps")
    private String sliderSteps;

    @SerializedName("insurance_case_id")
    private String insuranceCaseId;


    @SerializedName("quotes")
    private ArrayList<QuoteDetails> quoteList;


    @SerializedName("hasNext")
    private int hasNext;

    public String getCarId() {
        return carId;
    }

    public void setCarId(String carId) {
        this.carId = carId;
    }

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public int getHasNext() {
        return hasNext;
    }

    public void setHasNext(int hasNext) {
        this.hasNext = hasNext;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getTotalRecords() {
        return totalRecords;
    }

    public void setTotalRecords(int totalRecords) {
        this.totalRecords = totalRecords;
    }

    public ArrayList<QuoteDetails> getQuoteList() {
        return quoteList;
    }

    public void setQuoteList(ArrayList<QuoteDetails> quoteList) {
        this.quoteList = quoteList;
    }

    public String getIdvRange() {
        return idvRange;
    }

    public void setIdvRange(String idvRange) {
        this.idvRange = idvRange;
    }

    public String getSliderSteps() {
        return sliderSteps;
    }

    public void setSliderSteps(String sliderSteps) {
        this.sliderSteps = sliderSteps;
    }

    public String getInsuranceCaseId() {
        return insuranceCaseId;
    }

    public void setInsuranceCaseId(String insuranceCaseId) {
        this.insuranceCaseId = insuranceCaseId;
    }
}
