package com.gcloud.gaadi.chat.parser;

public class ChatMessageParser {
    private String chatsessionid;
    private String conversationid;
    private String date;
    private boolean isIncoming = true;
    private String message;
    private String name;
    private String supportroomname;
    private String time;
    private String type;
    private String userchatid;
    private String usertype;

    public String getChatsessionid() {
        return this.chatsessionid;
    }

    public void setChatsessionid(String paramString) {
        this.chatsessionid = paramString;
    }

    public String getConversationid() {
        return this.conversationid;
    }

    public void setConversationid(String paramString) {
        this.conversationid = paramString;
    }

    public String getDate() {
        return this.date;
    }

    public void setDate(String paramString) {
        this.date = paramString;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String paramString) {
        this.message = paramString;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String paramString) {
        this.name = paramString;
    }

    public String getSupportroomname() {
        return this.supportroomname;
    }

    public void setSupportroomname(String paramString) {
        this.supportroomname = paramString;
    }

    public String getTime() {
        return this.time;
    }

    public void setTime(String paramString) {
        this.time = paramString;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String paramString) {
        this.type = paramString;
    }

    public String getUserchatid() {
        return this.userchatid;
    }

    public void setUserchatid(String paramString) {
        this.userchatid = paramString;
    }

    public String getUsertype() {
        return this.usertype;
    }

    public void setUsertype(String paramString) {
        this.usertype = paramString;
    }

    public boolean isIncoming() {
        return this.isIncoming;
    }

    public void setIncoming(boolean paramBoolean) {
        this.isIncoming = paramBoolean;
    }
}
