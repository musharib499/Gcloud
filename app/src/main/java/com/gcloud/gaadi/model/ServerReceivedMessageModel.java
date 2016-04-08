package com.gcloud.gaadi.model;


public class ServerReceivedMessageModel extends Model {
    String conversationid, messagesendtime;

    public String getConversationid() {
        return conversationid;
    }

    public void setConversationid(String conversationid) {
        this.conversationid = conversationid;
    }

    public String getMessagesendtime() {
        return messagesendtime;
    }

    public void setMessagesendtime(String messagesendtime) {
        this.messagesendtime = messagesendtime;
    }
}
