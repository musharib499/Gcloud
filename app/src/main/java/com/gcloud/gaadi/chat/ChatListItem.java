package com.gcloud.gaadi.chat;

public class ChatListItem {
    private String conversationid, chat_with, id, messagesendtime, userchatid, timestamp;

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getUserchatid() {
        return userchatid;
    }

    public void setUserchatid(String userchatid) {
        this.userchatid = userchatid;
    }

    public String getMessagesendtime() {
        return messagesendtime;
    }

    public void setMessagesendtime(String messagesendtime) {
        this.messagesendtime = messagesendtime;
    }

    public String getChat_with() {
        return chat_with;
    }

    public void setChat_with(String chat_with) {
        this.chat_with = chat_with;
    }

    public String getid() {
        return id;
    }

    public void setid(String id) {
        this.id = id;
    }

    public String getConversationid() {
        return conversationid;
    }

    public void setConversationid(String conversationid) {
        this.conversationid = conversationid;
    }

}
