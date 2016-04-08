package com.gcloud.gaadi.chat.model;

import com.gcloud.gaadi.chat.parser.DealerChatHistoryParser;

import java.util.LinkedHashMap;


public class DealerChatHistoryModel extends Model {
    LinkedHashMap<Long, DealerChatHistoryParser> historymap = new LinkedHashMap<Long, DealerChatHistoryParser>();
    private String type, offerprice, conversationid, price, chatsessionid, imageurl, modelyear, variantId, km, phoneNumber;
    private ChatWithModel chat_with;

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

    public LinkedHashMap<Long, DealerChatHistoryParser> getChatItems() {
        return historymap;
    }

    public void setChatItems(LinkedHashMap<Long, DealerChatHistoryParser> mChatItems) {
        this.historymap = mChatItems;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ChatWithModel getChatWith() {
        return chat_with;
    }

    public void setChatWith(ChatWithModel chat_with) {
        this.chat_with = chat_with;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
