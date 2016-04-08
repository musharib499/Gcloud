package com.gcloud.gaadi.chat.parser;

import com.gcloud.gaadi.chat.model.SenderModel;

public class DealerChatHistoryParser {
    String message, name, is_notification, chatsessionid, date, id, time;
    long messagesendtime;
    private String phoneNumber;
    private String lastchattime;
    private boolean isdelivered;
    private int isMessageDelivered;// 0 for Progress image , 1 for single tic image , 2 for double tic

    private String type, offerprice, conversationid, price,
            imageurl, modelyear, variantId, km, messageId;
    private SenderModel sender;

    private boolean isSentMessage = false;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIs_notification() {
        return is_notification;
    }

    public void setIs_notification(String is_notification) {
        this.is_notification = is_notification;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getIsMessageDelivered() {
        return isMessageDelivered;
    }

    public void setIsMessageDelivered(int isMessageDelivered) {
        this.isMessageDelivered = isMessageDelivered;
    }

    public boolean isIsdelivered() {
        return isdelivered;
    }

    public void setIsdelivered(boolean isdelivered) {
        this.isdelivered = isdelivered;
    }

    public String getLastchattime() {
        return lastchattime;
    }

    public void setLastchattime(String lastchattime) {
        this.lastchattime = lastchattime;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getMessagesendtime() {
        return messagesendtime;
    }

    public void setMessagesendtime(long messagesendtime) {
        this.messagesendtime = messagesendtime;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getOfferprice() {
        return offerprice;
    }

    public void setOfferprice(String offerprice) {
        this.offerprice = offerprice;
    }

    public String getConversationid() {
        return conversationid;
    }

    public void setConversationid(String conversationid) {
        this.conversationid = conversationid;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getChatsessionid() {
        return chatsessionid;
    }

    public void setChatsessionid(String chatsessionid) {
        this.chatsessionid = chatsessionid;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getModelyear() {
        return modelyear;
    }

    public void setModelyear(String modelyear) {
        this.modelyear = modelyear;
    }

    public String getVariantId() {
        return variantId;
    }

    public void setVariantId(String variantId) {
        this.variantId = variantId;
    }

    public String getKm() {
        return km;
    }

    public void setKm(String km) {
        this.km = km;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public SenderModel getSender() {
        return sender;
    }

    public void setSender(SenderModel sender) {
        this.sender = sender;
    }

    public boolean isSentMessage() {
        return isSentMessage;
    }

    public void setSentMessage(boolean isSentMessage) {
        this.isSentMessage = isSentMessage;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
