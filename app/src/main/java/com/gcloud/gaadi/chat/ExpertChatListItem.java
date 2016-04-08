package com.gcloud.gaadi.chat;

public class ExpertChatListItem extends ChatListItem {
    private String lastchattime, /*conversationid,*/
            userchatid, endusername,
            enduserchatid,/* chat_with,*/
            lastchatmsg, name, chatsessionid,
            unreadmsgcount, supportroomname;

    public String getLastchattime() {
        return lastchattime;
    }

    public void setLastchattime(String lastchattime) {
        this.lastchattime = lastchattime;
    }

//public String getConversationid() {
//return conversationid;
//}
//
//public void setConversationid(String conversationid) {
//this.conversationid = conversationid;
//}

    public String getUserchatid() {
        return userchatid;
    }

    public void setUserchatid(String userchatid) {
        this.userchatid = userchatid;
    }

    public String getEndusername() {
        return endusername;
    }

    public void setEndusername(String endusername) {
        this.endusername = endusername;
    }

    public String getEnduserchatid() {
        return enduserchatid;
    }

    public void setEnduserchatid(String enduserchatid) {
        this.enduserchatid = enduserchatid;
    }

    /*public String getChat_with() {
    return chat_with;
    }

    public void setChat_with(String chat_with) {
    this.chat_with = chat_with;
    }
    */
    public String getLastchatmsg() {
        return lastchatmsg;
    }

    public void setLastchatmsg(String lastchatmsg) {
        this.lastchatmsg = lastchatmsg;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getChatsessionid() {
        return chatsessionid;
    }

    public void setChatsessionid(String chatsessionid) {
        this.chatsessionid = chatsessionid;
    }

    public String getUnreadmsgcount() {
        return unreadmsgcount;
    }

    public void setUnreadmsgcount(String unreadmsgcount) {
        this.unreadmsgcount = unreadmsgcount;
    }

    public String getSupportroomname() {
        return supportroomname;
    }

    public void setSupportroomname(String supportroomname) {
        this.supportroomname = supportroomname;
    }
}
