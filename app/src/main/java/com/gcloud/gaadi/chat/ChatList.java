package com.gcloud.gaadi.chat;

import com.gcloud.gaadi.chat.Utility.CHATLIST_MESSAGE_TYPE;
import com.gcloud.gaadi.chat.model.Model;

import java.util.LinkedHashMap;


public class ChatList extends Model {

    private LinkedHashMap<String, ChatListItem> mChatListItems = new LinkedHashMap<String, ChatListItem>();
    private CHATLIST_MESSAGE_TYPE type;
//	private ArrayList<ExpertChatListItem> mExpertChatListItems = new ArrayList<ExpertChatListItem>();
//	public ArrayList<DealerChatListItem> getmDealerChatListItems() {
//		return mDealerChatListItems;
//	}
//	public void setmDealerChatListItems(
//			ArrayList<DealerChatListItem> mDealerChatListItems) {
//		this.mDealerChatListItems = mDealerChatListItems;
//	}
//	public ArrayList<ExpertChatListItem> getmExpertChatListItems() {
//		return mExpertChatListItems;
//	}
//	public void setmExpertChatListItems(
//			ArrayList<ExpertChatListItem> mExpertChatListItems) {
//		this.mExpertChatListItems = mExpertChatListItems;
//	}

    public LinkedHashMap<String, ChatListItem> getmChatListItems() {
        return mChatListItems;
    }

    public void setmChatListItems(LinkedHashMap<String, ChatListItem> mChatListItems) {
        this.mChatListItems = mChatListItems;
    }

    public CHATLIST_MESSAGE_TYPE getType() {
        return type;
    }

    public void setType(CHATLIST_MESSAGE_TYPE conversationList) {
        this.type = conversationList;
    }
}
