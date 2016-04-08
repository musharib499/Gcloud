package com.gcloud.gaadi.chat.parser;

import android.annotation.SuppressLint;

import com.gcloud.gaadi.chat.model.DealerChatHistoryModel;
import com.gcloud.gaadi.chat.model.DealerChatMEssageReceivedModel;
import com.gcloud.gaadi.chat.model.DealerChatModel;
import com.gcloud.gaadi.chat.model.Model;
import com.gcloud.gaadi.chat.model.OfflineModel;
import com.gcloud.gaadi.chat.model.OnlineModel;
import com.gcloud.gaadi.chat.model.ServerReceivedMessageModel;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class ChatParser implements Parser {

    @SuppressLint("NewApi")
    @Override
    public Model parse(JSONObject json) throws JSONException {
        // TODO Auto-generated method stub
        Gson gson = new Gson();
        if (json != null && json.has("type") && json.getString("type").equalsIgnoreCase("DEALER_CHAT_HISTORY")) {
            DealerChatHistoryModel mChatHistory = null;
            JSONObject chatdataobj = json.getJSONObject("data");
            if (chatdataobj != null) {
                mChatHistory = gson.fromJson(chatdataobj.toString(), DealerChatHistoryModel.class);
                LinkedHashMap<Long, DealerChatHistoryParser> mChatItems = new LinkedHashMap<Long, DealerChatHistoryParser>();
                JSONArray chathistory = chatdataobj.getJSONArray("history");
                for (int i = 0; i < chathistory.length(); i++) {
                    JSONObject chatlistobj = chathistory.getJSONObject(i);
                    if (chatlistobj != null) {
                        DealerChatHistoryParser parser = gson.fromJson(chatlistobj.toString(), DealerChatHistoryParser.class);
                        if (parser != null) {
                            if (parser.getMessagesendtime() == 0)
                                parser.setMessagesendtime(System.currentTimeMillis());

                            if (parser.isIsdelivered())
                                parser.setIsMessageDelivered(2);
                            else
                                parser.setIsMessageDelivered(1);

                            if (mChatHistory.getChatWith().getUserchatid().equals(parser.getSender().getUserchatid()))
                                parser.setSentMessage(true);
                            else
                                parser.setSentMessage(false);

							/*if (localJSONObject2.has("phoneNumber"))
                                phoneNumber = localJSONObject2.getString("phoneNumber");*/


                            mChatItems.put(parser.getMessagesendtime(), parser);
                        }
                    }
                }
                mChatHistory.setChatItems(mChatItems);
            }

            return mChatHistory;
        } else if (json != null && json.has("type") && json.getString("type").equalsIgnoreCase("ONLINE")) {
            OnlineModel online = null;
            JSONObject chatdataobj = json.getJSONObject("data");
            if (chatdataobj != null) {
                online = gson.fromJson(chatdataobj.toString(), OnlineModel.class);
            }
            return online;
        } else if (json != null && json.has("type") && json.getString("type").equalsIgnoreCase("OFFLINE")) {
            OfflineModel offline = null;
            JSONObject chatdataobj = json.getJSONObject("data");
            if (chatdataobj != null) {
                offline = gson.fromJson(chatdataobj.toString(), OfflineModel.class);
            }
            return offline;
        } else if (json != null && json.has("type") && json.getString("type").equalsIgnoreCase("SERVER_RECEIVED_MESSAGE")) {
            ServerReceivedMessageModel serverModel = null;
            JSONObject chatdataobj = json.getJSONObject("data");
            if (chatdataobj != null) {
                serverModel = gson.fromJson(chatdataobj.toString(), ServerReceivedMessageModel.class);
            }
            return serverModel;
        } else if (json != null && json.has("type") && json.getString("type").equalsIgnoreCase("DEALER_CHAT_MESSAGE_RECEIVED")) {
            DealerChatMEssageReceivedModel message = null;
            JSONObject chatdataobj = json.getJSONObject("data");
            if (chatdataobj != null) {
                message = gson.fromJson(chatdataobj.toString(), DealerChatMEssageReceivedModel.class);
            }
            return message;
        } else if (json != null && json.has("type") && json.getString("type").equalsIgnoreCase("DEALER_CHAT")) {
            DealerChatModel message = null;
            JSONObject chatdataobj = json.getJSONObject("data");
            if (chatdataobj != null) {
                DealerChatHistoryParser parser = gson.fromJson(chatdataobj.toString(), DealerChatHistoryParser.class);
                if (parser != null) {
                    message = new DealerChatModel();
                    if (parser.getSender() != null && parser.getSender().getUsertype().equals("enduser"))
                        parser.setSentMessage(false);
                    else
                        parser.setSentMessage(true);

                    parser.setTime(parser.getLastchattime());
                    message.setChatItem(parser);
                }
            }
            return message;
        }


        return null;
    }

    @Override
    public ArrayList parse(String resp) throws JSONException {
        // TODO Auto-generated method stub
        return null;
    }

}