package com.gcloud.gaadi.chat;

import android.annotation.SuppressLint;

import com.gcloud.gaadi.chat.Utility.CHATLIST_MESSAGE_TYPE;
import com.gcloud.gaadi.chat.model.Model;
import com.gcloud.gaadi.chat.parser.Parser;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;


public class ChatListParser implements Parser {

    @SuppressLint("NewApi")
    @Override
    public Model parse(JSONObject json) throws JSONException {
        // TODO Auto-generated method stub
        ChatList mChatList = new ChatList();
        Gson gson = new Gson();
        LinkedHashMap<String, ChatListItem> mChatListItems = new LinkedHashMap<String, ChatListItem>();
        if (json != null && !json.isNull("type") && json.getString("type").equalsIgnoreCase("CONVERSATION_LIST")) {
            JSONObject chatlistdataobj = json.getJSONObject("data");
//		ArrayList<DealerChatListItem> mDealerChatListItems = new ArrayList<DealerChatListItem>();
//		ArrayList<ExpertChatListItem> mExpertChatListItems = new ArrayList<ExpertChatListItem>();
            if (chatlistdataobj != null) {
                mChatList.setType(CHATLIST_MESSAGE_TYPE.CONVERSATION_LIST);
                JSONArray chatlist = chatlistdataobj.getJSONArray("conversation_list");
                for (int i = 0; i < chatlist.length(); i++) {
                    JSONObject chatlistobj = chatlist.getJSONObject(i);
                    if (chatlistobj != null && !chatlistobj.toString().isEmpty() && !chatlistobj.isNull("chat_with") && chatlistobj.get("chat_with").equals("suppportuser")) {
                        ExpertChatListItem expertchatitem = new ExpertChatListItem();
                        expertchatitem = gson.fromJson(chatlistobj.toString(), ExpertChatListItem.class);
//					mExpertChatListItems.add(expertchatitem);
                        mChatListItems.put(expertchatitem.getConversationid(), expertchatitem);
                    } else if (chatlistobj != null && !chatlistobj.toString().isEmpty() && !chatlistobj.isNull("chat_with") && chatlistobj.getString("chat_with").equals("dealer")) {
                        DealerChatListItem dealerchatitem = new DealerChatListItem();
                        dealerchatitem = gson.fromJson(chatlistobj.toString(), DealerChatListItem.class);
//					mDealerChatListItems.add(dealerchatitem);
                        mChatListItems.put(dealerchatitem.getConversationid(), dealerchatitem);
                    }
                }
            }
//		mChatList.setmDealerChatListItems(mDealerChatListItems);
//		mChatList.setmExpertChatListItems(mExpertChatListItems);
        } else if (json != null && !json.isNull("type")
                && json.getString("type").equalsIgnoreCase("DEALER_CHAT")) {
            if (!json.isNull("data") && !json.getString("data").isEmpty()) {
                JSONObject dealerobj = json.getJSONObject("data");
                if (!dealerobj.isNull("conversation_list")
                        && !dealerobj.getString("conversation_list").isEmpty()) {
                    DealerChatListItem dealerchatitem = new DealerChatListItem();

                    dealerchatitem = gson.fromJson(
                            dealerobj.getString("conversation_list"),
                            DealerChatListItem.class);
                    if (dealerobj.has("timestamp")) {
                        dealerchatitem.setTimestamp(dealerobj.getString("timestamp"));
                    }

                    if (dealerobj.has("sender")) {
                        JSONObject senderJSONObject = dealerobj.getJSONObject("sender");
                        if (senderJSONObject.has("userchatid"))
                            dealerchatitem.setUserchatid(senderJSONObject.getString("userchatid"));
                    }

                    mChatListItems.put(dealerchatitem.getConversationid(),
                            dealerchatitem);
                    mChatList.setType(CHATLIST_MESSAGE_TYPE.DEALER_CHAT);
                }
            }
        } else if (json != null && !json.isNull("type") && json.getString("type").equalsIgnoreCase("CHAT")) {
            if (!json.isNull("conversation_list") && !json.getString("conversation_list").isEmpty()) {
                ExpertChatListItem expertchatitem = new ExpertChatListItem();
                expertchatitem = gson.fromJson(json.getString("conversation_list"), ExpertChatListItem.class);
                mChatListItems.put(expertchatitem.getConversationid(), expertchatitem);
                mChatList.setType(CHATLIST_MESSAGE_TYPE.EXPERT_CHAT);
            }
        }
        mChatList.setmChatListItems(mChatListItems);
        return mChatList;
    }

    @Override
    public ArrayList parse(String resp) throws JSONException {
        // TODO Auto-generated method stub
        return null;
    }

}
