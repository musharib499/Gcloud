package com.gcloud.gaadi.model;

import com.google.gson.annotations.SerializedName;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by ankit on 6/1/15.
 */
public class LeadDetailModel implements Serializable {

    @SerializedName("dealerID")
    private String dealerID;
    @SerializedName("car_id")
    private String car_id;
    @SerializedName("year")
    private String year;
    @SerializedName("color")
    private String color;
    @SerializedName("color_hex")
    private String colorHexCode;
    @SerializedName("km")
    private String km;
    @SerializedName("imageIcon")
    private String imageIcon;
    @SerializedName("price")
    private String price;
    @SerializedName("comments")
    private String comments;
    @SerializedName("emailID")
    private String emailID;
    @SerializedName("followDate")
    private String followDate;
    @SerializedName("name")
    private String name;
    @SerializedName("number")
    private String number;
    @SerializedName("budgetfrom")
    private String budgetfrom;
    @SerializedName("budgetto")
    private String budgetto;
    @SerializedName("budget")
    private String budget;
    @SerializedName("makeID")
    private int makeId;
    @SerializedName("make")
    private String makename;
    @SerializedName("model")
    private String model;
    @SerializedName("modelname")
    private String modelName;
    @SerializedName("source")
    private String source;
    @SerializedName("lead_status")
    private String leadStatus;
    @SerializedName("dateTime")
    private String dateTime;
    @SerializedName("leadID")
    private String leadId;
    @SerializedName("changetime")
    private String changeTime;
    @SerializedName("verified")
    private String verified;

    @SerializedName("car_list")
    private ArrayList<SellerLeadsCarModel> carsList;
    @SerializedName("comment")
    private ArrayList<Comments> commentsArrayList;

    public String getColorHexCode() {
        return colorHexCode;
    }

    public void setColorHexCode(String colorHexCode) {
        this.colorHexCode = colorHexCode;
    }

    public ArrayList<SellerLeadsCarModel> getCarsList() {
        return carsList;
    }

    public void setCarsList(ArrayList<SellerLeadsCarModel> carsList) {
        this.carsList = carsList;
    }

    public ArrayList<Comments> getCommentsArrayList() {
        return commentsArrayList;
    }

    public void setCommentsArrayList(ArrayList<Comments> commentsArrayList) {
        this.commentsArrayList = commentsArrayList;
    }

    public String getCar_id() {
        return car_id;
    }

    public void setCar_id(String car_id) {
        this.car_id = car_id;
    }

    public String getKm() {
        return km;
    }

    public void setKm(String km) {
        this.km = km;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getCarID() {
        return car_id;
    }

    public void setCarID(String car_id) {
        this.car_id = car_id;
    }

    public String getImageIcon() {
        return imageIcon;
    }

    public void setImageIcon(String imageIcon) {
        this.imageIcon = imageIcon;
    }

    public String getVerified() {
        return verified;
    }

    public void setVerified(String verified) {
        this.verified = verified;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }


    public String getEmailID() {
        return emailID;
    }

    public void setEmailID(String emailID) {
        this.emailID = emailID;
    }

    public String getDealerID() {
        return dealerID;
    }

    public void setDealerID(String dealerID) {
        this.dealerID = dealerID;
    }

    public String getformattedfollowDate() {

        DateTimeFormatter dtf = DateTimeFormat
                .forPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDate = "";
        StringBuilder timeStringBuilder = new StringBuilder();
        if (!followDate.trim().equals("")) {

            if (followDate.equals("0000-00-00 00:00:00")) {
                formattedDate = "";
            } else {
                verifyTimeFormat(followDate);
                DateTime date = dtf.parseDateTime(followDate);
                int minuteOfHour = Integer.parseInt(date.minuteOfHour().getAsString());
                timeStringBuilder.append((minuteOfHour < 10) ? ("0" + minuteOfHour) : minuteOfHour);
                formattedDate = date.getDayOfMonth() + "/"
                        + date.monthOfYear().getAsShortText(Locale.ENGLISH) + "/"
                        + date.year().getAsShortText(Locale.ENGLISH) + " "
                        + date.hourOfDay().getAsShortText(Locale.ENGLISH) + ":"
                        + timeStringBuilder;
            }
        }

        return formattedDate;
    }

    private void verifyTimeFormat(String followDate) {
        String[] dateTime = followDate.split(" ");
        if (dateTime.length > 1) {
            String[] times = dateTime[1].split(":");
            if (times.length < 3) {
                this.followDate += ":00";
            }
        } else {
            this.followDate += " 00:00:00";
        }
    }

    public String getformattedfollowUPDate() {
        String formattedDate = "";
        if (followDate.equals("0000-00-00 00:00:00")) {
            formattedDate = "";
        } else {
            formattedDate = followDate;
        }

        return formattedDate;
    }

    public String getFollowDate() {

        /*DateTimeFormatter dtf = DateTimeFormat
                .forPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDate = "";
        if (!followDate.trim().equals("")) {

            if (followDate.equals("0000-00-00 00:00:00")) {
                formattedDate = "";
            } else {
                DateTime date = dtf.parseDateTime(followDate);

                formattedDate = date.getDayOfMonth() + " "
                        + date.monthOfYear().getAsShortText(Locale.ENGLISH) + ", "
                        + date.year().getAsShortText(Locale.ENGLISH) + " at "
                        + date.hourOfDay().getAsShortText(Locale.ENGLISH) + ":"
                        + date.minuteOfHour().getAsShortText(Locale.ENGLISH);
            }
        }*/

        return followDate;

    }

    public void setFollowDate(String followDate) {
        this.followDate = followDate;
    }

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

    public String getMakename() {
        return makename;
    }

    public void setMakename(String makename) {
        this.makename = makename;
    }

    public int getMakeId() {
        return makeId;
    }

    public void setMakeId(int makeId) {
        this.makeId = makeId;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getBudget() {
        return budget;
    }

    public void setBudget(String budget) {
        this.budget = budget;
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

    public String getDateTime() {
        DateTimeFormatter dtf = DateTimeFormat
                .forPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDate = "";
        if (!dateTime.trim().equals("") && !dateTime.trim().equals("0000-00-00 00:00:00")) {
            DateTime date = dtf.parseDateTime(dateTime);
            int minute = Integer.parseInt(date.minuteOfHour().getAsShortText(Locale.ENGLISH));
            StringBuilder timeStringBuilder = new StringBuilder();
            timeStringBuilder.append((minute < 10) ? ("0" + minute) : minute);
            formattedDate = date.getDayOfMonth() + " "
                    + date.monthOfYear().getAsShortText(Locale.ENGLISH) + ", "
                    + date.year().getAsShortText(Locale.ENGLISH);
        }

        return formattedDate;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getDateTimeUnFormatted() {
        return dateTime;
    }

    public String getLeadId() {
        return leadId;
    }

    public void setLeadId(String leadId) {
        this.leadId = leadId;
    }

    public String getChangeTime() {
        return changeTime;
    }

    public void setChangeTime(String changeTime) {
        this.changeTime = changeTime;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("LeadDetailModel{");
        sb.append("dealerID='").append(dealerID).append('\'');
        sb.append(", car_id='").append(car_id).append('\'');
        sb.append(", year='").append(year).append('\'');
        sb.append(", color='").append(color).append('\'');
        sb.append(", colorHexCode='").append(colorHexCode).append('\'');
        sb.append(", km='").append(km).append('\'');
        sb.append(", imageIcon='").append(imageIcon).append('\'');
        sb.append(", price='").append(price).append('\'');
        sb.append(", comments='").append(comments).append('\'');
        sb.append(", emailID='").append(emailID).append('\'');
        sb.append(", followDate='").append(followDate).append('\'');
        sb.append(", name='").append(name).append('\'');
        sb.append(", number='").append(number).append('\'');
        sb.append(", budgetfrom='").append(budgetfrom).append('\'');
        sb.append(", budgetto='").append(budgetto).append('\'');
        sb.append(", budget='").append(budget).append('\'');
        sb.append(", makeId=").append(makeId);
        sb.append(", makename='").append(makename).append('\'');
        sb.append(", model='").append(model).append('\'');
        sb.append(", modelName='").append(modelName).append('\'');
        sb.append(", source='").append(source).append('\'');
        sb.append(", leadStatus='").append(leadStatus).append('\'');
        sb.append(", dateTime='").append(dateTime).append('\'');
        sb.append(", leadId='").append(leadId).append('\'');
        sb.append(", changeTime='").append(changeTime).append('\'');
        sb.append(", verified='").append(verified).append('\'');
        sb.append(", carsList=").append(carsList);
        sb.append(", commentsArrayList=").append(commentsArrayList);
        sb.append('}');
        return sb.toString();
    }
}
