package com.gcloud.gaadi.events;

import java.io.Serializable;

/**
 * Created by ankit on 22/1/15.
 */
public class MakeOfferEvent implements Serializable {

    private String otherDealerMobile;
    private String otherDealerEmail;
    private String dealerEmail;
    private String dealerMobile;

    public MakeOfferEvent(String otherDealerMobile, String otherDealerEmail, String dealerEmail, String dealerMobile) {
        this.otherDealerMobile = otherDealerMobile;
        this.otherDealerEmail = otherDealerEmail;
        this.dealerEmail = dealerEmail;
        this.dealerMobile = dealerMobile;
    }

    public String getOtherDealerMobile() {
        return otherDealerMobile;
    }

    public void setOtherDealerMobile(String otherDealerMobile) {
        this.otherDealerMobile = otherDealerMobile;
    }

    public String getOtherDealerEmail() {
        return otherDealerEmail;
    }

    public void setOtherDealerEmail(String otherDealerEmail) {
        this.otherDealerEmail = otherDealerEmail;
    }

    public String getDealerEmail() {
        return dealerEmail;
    }

    public void setDealerEmail(String dealerEmail) {
        this.dealerEmail = dealerEmail;
    }

    public String getDealerMobile() {
        return dealerMobile;
    }

    public void setDealerMobile(String dealerMobile) {
        this.dealerMobile = dealerMobile;
    }

    @Override
    public String toString() {
        return "MakeOfferEvent{" +
                "otherDealerEmail='" + otherDealerEmail + '\'' +
                ", dealerEmail='" + dealerEmail + '\'' +
                ", dealerMobile='" + dealerMobile + '\'' +
                '}';
    }
}
