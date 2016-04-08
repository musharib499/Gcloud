package com.gcloud.gaadi.chat.model;


public class OnlineModel extends Model {
    String userchatid, conversationid, name;

    public String getUserchatid() {
        return userchatid;
    }

    public void setUserchatid(String userchatid) {
        this.userchatid = userchatid;
    }

    public String getConversationid() {
        return conversationid;
    }

    public void setConversationid(String conversationid) {
        this.conversationid = conversationid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
