package com.gcloud.gaadi.insurance;

import com.gcloud.gaadi.model.GeneralResponse;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Gaurav on 21-07-2015.
 */
public class InsuranceDashboardModel extends GeneralResponse {

    @SerializedName("data")
    private Dashboard data;

    @SerializedName("insurance_knowlarity")
    private String helplineNumber;

    @SerializedName("monthwise")
    private ArrayList<Dashboard> monthwise;

    @SerializedName("pending_case")
    private ArrayList<PendingInsuranceCaseModel> pendingInsuranceCaseModels;

    public String getHelplineNumber() {
        return helplineNumber;
    }

    public void setHelplineNumber(String helplineNumber) {
        this.helplineNumber = helplineNumber;
    }

    public ArrayList<Dashboard> getMonthwise() {
        return monthwise;
    }

    public void setMonthwise(ArrayList<Dashboard> monthwise) {
        this.monthwise = monthwise;
    }

    public Dashboard getData() {
        return data;
    }

    public void setData(Dashboard data) {
        this.data = data;
    }

    public ArrayList<PendingInsuranceCaseModel> getPendingInsuranceCaseModels() {
        return pendingInsuranceCaseModels;
    }

    public void setPendingInsuranceCaseModels(ArrayList<PendingInsuranceCaseModel> pendingInsuranceCaseModels) {
        this.pendingInsuranceCaseModels = pendingInsuranceCaseModels;
    }

    public class Dashboard implements Serializable {

        @SerializedName("all")
        private String allCount;

        @SerializedName("booked")
        private String bookedCount;

        @SerializedName("unbooked")
        private String unbookedCount;

        @SerializedName("cancelled")
        private String cancelledCount;

        @SerializedName("final_od_sum")
        private String totalOdPremium;

        @SerializedName("final_od_com_sum")
        private String totalCommissionEarned;

        @SerializedName("month")
        private String month;

        @SerializedName("year")
        private String year;

        public String getMonth() {
            return month;
        }

        public void setMonth(String month) {
            this.month = month;
        }

        public String getYear() {
            return year;
        }

        public void setYear(String year) {
            this.year = year;
        }

        public String getTotalCommissionEarned() {
            return totalCommissionEarned;
        }

        public void setTotalCommissionEarned(String totalCommissionEarned) {
            this.totalCommissionEarned = totalCommissionEarned;
        }

        public String getAllCount() {
            return allCount;
        }

        public void setAllCount(String allCount) {
            this.allCount = allCount;
        }

        public String getBookedCount() {
            return bookedCount;
        }

        public void setBookedCount(String bookedCount) {
            this.bookedCount = bookedCount;
        }

        public String getUnbookedCount() {
            return unbookedCount;
        }

        public void setUnbookedCount(String unbookedCount) {
            this.unbookedCount = unbookedCount;
        }

        public String getCancelledCount() {
            return cancelledCount;
        }

        public void setCancelledCount(String cancelledCount) {
            this.cancelledCount = cancelledCount;
        }

        public String getTotalOdPremium() {
            return totalOdPremium;
        }

        public void setTotalOdPremium(String totalOdPremium) {
            this.totalOdPremium = totalOdPremium;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("Dashboard{");
            sb.append("allCount='").append(allCount).append('\'');
            sb.append(", bookedCount='").append(bookedCount).append('\'');
            sb.append(", unbookedCount='").append(unbookedCount).append('\'');
            sb.append(", cancelledCount='").append(cancelledCount).append('\'');
            sb.append(", totalOdPremium='").append(totalOdPremium).append('\'');
            sb.append(", totalCommissionEarned='").append(totalCommissionEarned).append('\'');
            sb.append(", month='").append(month).append('\'');
            sb.append(", year='").append(year).append('\'');
            sb.append('}');
            return sb.toString();
        }
    }

    public class PendingInsuranceCaseModel implements Serializable {

        @SerializedName("process_id")
        private String processId;

        @SerializedName("request_id")
        private String requestId;

        @SerializedName("request_date")
        private String requestDate;

        @SerializedName("car_id")
        private String carId;

        @SerializedName("insurer")
        private String insurer;

        @SerializedName("regNo")
        private String regNo;

        @SerializedName("make")
        private String make;

        @SerializedName("model")
        private String model;

        @SerializedName("version")
        private String version;

        @SerializedName("net_premium")
        private String netPremium;

        @SerializedName("reg_date")
        private String regDate;

        @SerializedName("reg_month")
        private String regMonth;

        @SerializedName("reg_year")
        private String regYear;

        @SerializedName("make_id")
        private int makeId;

        @SerializedName("pending_reason")
        private String pendingReason;

        @SerializedName("car_type")
        private String carType;

        @SerializedName("pending_documents")
        private ArrayList<String> pendingDocuments;

        @SerializedName("form_30_url")
        private ArrayList<String> form30;

        public ArrayList<String> getForm30() {
            return form30;
        }

        public void setForm30(ArrayList<String> form30) {
            this.form30 = form30;
        }

        public String getProcessId() {
            return processId;
        }

        public void setProcessId(String processId) {
            this.processId = processId;
        }

        public String getRequestId() {
            return requestId;
        }

        public void setRequestId(String requestId) {
            this.requestId = requestId;
        }

        public String getRequestDate() {
            return requestDate;
        }

        public void setRequestDate(String requestDate) {
            this.requestDate = requestDate;
        }

        public String getCarId() {
            return carId;
        }

        public void setCarId(String carId) {
            this.carId = carId;
        }

        public String getInsurer() {
            return insurer;
        }

        public void setInsurer(String insurer) {
            this.insurer = insurer;
        }

        public String getRegNo() {
            return regNo;
        }

        public void setRegNo(String regNo) {
            this.regNo = regNo;
        }

        public String getMake() {
            return make;
        }

        public void setMake(String make) {
            this.make = make;
        }

        public String getModel() {
            return model;
        }

        public void setModel(String model) {
            this.model = model;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public String getNetPremium() {
            return netPremium;
        }

        public void setNetPremium(String netPremium) {
            this.netPremium = netPremium;
        }

        public String getRegDate() {
            return regDate;
        }

        public void setRegDate(String regDate) {
            this.regDate = regDate;
        }

        public String getRegMonth() {
            return regMonth;
        }

        public void setRegMonth(String regMonth) {
            this.regMonth = regMonth;
        }

        public String getRegYear() {
            return regYear;
        }

        public void setRegYear(String regYear) {
            this.regYear = regYear;
        }

        public int getMakeId() {
            return makeId;
        }

        public void setMakeId(int makeId) {
            this.makeId = makeId;
        }

        public String getPendingReason() {
            return pendingReason;
        }

        public void setPendingReason(String pendingReason) {
            this.pendingReason = pendingReason;
        }

        public String getCarType() {
            return carType;
        }

        public void setCarType(String carType) {
            this.carType = carType;
        }

        public ArrayList<String> getPendingDocuments() {
            return pendingDocuments;
        }

        public void setPendingDocuments(ArrayList<String> pendingDocuments) {
            this.pendingDocuments = pendingDocuments;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("PendingInsuranceCaseModel{");
            sb.append("processId='").append(processId).append('\'');
            sb.append(", requestId='").append(requestId).append('\'');
            sb.append(", requestDate='").append(requestDate).append('\'');
            sb.append(", carId='").append(carId).append('\'');
            sb.append(", insurer='").append(insurer).append('\'');
            sb.append(", regNo='").append(regNo).append('\'');
            sb.append(", make='").append(make).append('\'');
            sb.append(", model='").append(model).append('\'');
            sb.append(", version='").append(version).append('\'');
            sb.append(", netPremium='").append(netPremium).append('\'');
            sb.append(", regDate='").append(regDate).append('\'');
            sb.append(", regMonth='").append(regMonth).append('\'');
            sb.append(", regYear='").append(regYear).append('\'');
            sb.append(", makeId=").append(makeId);
            sb.append(", pendingReason='").append(pendingReason).append('\'');
            sb.append(", carType='").append(carType).append('\'');
            sb.append(", pendingDocuments=").append(pendingDocuments);
            sb.append('}');
            return sb.toString();
        }
    }
}
