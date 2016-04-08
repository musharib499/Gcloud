package com.gcloud.gaadi.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Priya on 14-07-2015.
 */
public class InsurancesCasesModel extends GeneralResponse {

    @SerializedName("data")
    private InsuranceCasesCount insurancesCasesCount;

    public InsuranceCasesCount getInsurancesCasesCount() {
        return insurancesCasesCount;
    }

    public void setInsurancesCasesCount(InsuranceCasesCount insurancesCasesCount) {
        this.insurancesCasesCount = insurancesCasesCount;
    }

    class InsuranceCasesCount {
        @SerializedName("all")
        private String all;

        @SerializedName("unbooked")
        private String unbooked;

        @SerializedName("booked")
        private String booked;

        @SerializedName("cancelled")
        private String cancelled;

        public String getAll() {
            return all;
        }

        public void setAll(String all) {
            this.all = all;
        }

        public String getUnbooked() {
            return unbooked;
        }

        public void setUnbooked(String unbooked) {
            this.unbooked = unbooked;
        }

        public String getBooked() {
            return booked;
        }

        public void setBooked(String booked) {
            this.booked = booked;
        }

        public String getCancelled() {
            return cancelled;
        }

        public void setCancelled(String cancelled) {
            this.cancelled = cancelled;
        }
    }
}
