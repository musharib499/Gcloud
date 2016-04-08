package com.gcloud.gaadi.chat;

import android.content.Context;
import android.util.Log;

import com.gcloud.gaadi.chat.parser.ChatMessageParser;
import com.gcloud.gaadi.chat.parser.DealerChatHistoryParser;
import com.gcloud.gaadi.constants.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Vector;

public class CommanMethods {
    static CommanMethods commanmethods = null;

    public static String getDate12(long paramLong) {
        return new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
                .format(Long.valueOf(paramLong));
    }

    public static String getTime12(long paramLong) {
        return new SimpleDateFormat("hh:mm aa", Locale.getDefault())
                .format(Long.valueOf(paramLong));
    }

    public static CommanMethods getInstance() {
        if (commanmethods == null)
            return commanmethods = new CommanMethods();
        else
            return commanmethods;
    }

    public String DisconnectChatRequest(String userchatid, String name, String conversationid, String chatsessionid, String supportroomname) {
        try {
            JSONObject localJSONObject1 = new JSONObject();
            JSONObject localJSONObject2 = new JSONObject();
            localJSONObject2.put("userchatid", userchatid);
            localJSONObject2.put("name", name);
            JSONObject localJSONObject3 = new JSONObject();
            localJSONObject3.put("sender", localJSONObject2);
            localJSONObject3.put("conversationid", conversationid);
            localJSONObject3.put("chatsessionid", chatsessionid);
            localJSONObject3.put("supportroomname", supportroomname);
            localJSONObject1.put("data", localJSONObject3);
            localJSONObject1.put("type", "CHAT_CLOSE");
            Log.e("DisconnectChatRequest", "  " + localJSONObject1.toString());
            String str = localJSONObject1.toString();
            return str;
        } catch (Exception localException) {
            localException.printStackTrace();
        }
        return "";
    }

    public String SentMessage(Context paramContext, String message, String userchatid, String name, String conversationid, String chatsessionid, String supportroomname) {
        try {
            Log.e("SentMessage", " msg " + message);
            JSONObject localJSONObject1 = new JSONObject();
            JSONObject localJSONObject2 = new JSONObject();
            localJSONObject2.put("userchatid", userchatid);
            localJSONObject2.put("name", name);
            localJSONObject2.put("usertype", "enduser");
            JSONObject localJSONObject3 = new JSONObject();
            localJSONObject3.put("message", message);
            localJSONObject3.put("sender", localJSONObject2);
            localJSONObject3.put("conversationid", conversationid);
            localJSONObject3.put("chatsessionid", chatsessionid);
            localJSONObject3.put("supportroomname", supportroomname);
            localJSONObject1.put("data", localJSONObject3);
            localJSONObject1.put("type", "CHAT");
            Log.e("SentMessage", "  " + localJSONObject1.toString());
            String str = localJSONObject1.toString();
            return str;
        } catch (Exception localException) {
            localException.printStackTrace();
        }
        return "";
    }

    public JSONObject StartWithExpert(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, boolean paramBoolean) {
        try {

            JSONObject localJSONObject1 = new JSONObject();
            JSONObject localJSONObject2 = new JSONObject();
            localJSONObject2.put("userId", paramString1);
            localJSONObject2.put("name", paramString2);
            localJSONObject2.put("usertype", "enduser");
            localJSONObject2.put("mobileno", "mobileNumber");
            JSONObject localJSONObject3 = new JSONObject();
            localJSONObject3.put("message", "");
            localJSONObject3.put("sender", localJSONObject2);
            localJSONObject3.put("source", "android");
            localJSONObject3.put("leadId", "leadId");
            localJSONObject3.put("supportroomname", paramString5);
            localJSONObject1.put("data", localJSONObject3);
            if (paramBoolean) {
                localJSONObject1.put("type", "START");
            } else
                localJSONObject1.put("type", "CHAT_HISTORY");

            return localJSONObject1;

        } catch (Exception localException) {
            localException.printStackTrace();
            return null;
        }
    }

    public ChatMessageParser parseChatMessage(String paramString1,
                                              String paramString2, String paramString3) {
        try {
            Log.e(" parseChatMessage", "  parseChatMessage ");
            Log.e(" ChatMessageParser", "  json " + paramString1);
            Log.e(" ChatMessageParser", "  consumerID " + paramString2);
            Log.e(" ChatMessageParser", "  type " + paramString3);
            ChatMessageParser localChatMessageParser = new ChatMessageParser();
            JSONObject localJSONObject1 = new JSONObject(paramString1)
                    .getJSONObject("data");
            if (localJSONObject1.has("message")) {
                localChatMessageParser.setMessage(localJSONObject1
                        .getString("message"));
                Log.e("parseChatMessage",
                        "message " + localJSONObject1.getString("message"));
            }
            JSONObject localJSONObject2 = localJSONObject1
                    .getJSONObject("sender");
            if (localJSONObject2.has("userchatid")) {
                localChatMessageParser.setUserchatid(localJSONObject2
                        .getString("userchatid"));
            }
            if (localJSONObject2.has("name")) {
                localChatMessageParser.setName(localJSONObject2
                        .getString("name"));
            }
            if (localJSONObject2.has("usertype")) {
                localChatMessageParser.setUsertype(localJSONObject2
                        .getString("usertype"));
            }
            if (localJSONObject1.has("conversationid")) {
                localChatMessageParser.setConversationid(localJSONObject1
                        .getString("conversationid"));
            }
            if (localJSONObject1.has("chatsessionid")) {
                localChatMessageParser.setChatsessionid(localJSONObject1
                        .getString("chatsessionid"));
            }
            if (localJSONObject1.has("supportroomname")) {
                localChatMessageParser.setSupportroomname(localJSONObject1
                        .getString("supportroomname"));
            }
            localChatMessageParser.setIncoming(true);
            if ((paramString2 != null)
                    && (paramString2.equals(localChatMessageParser
                    .getUserchatid()))) {
                boolean bool = paramString3.equals("CHAT");
                if (bool) {
                    localChatMessageParser = null;
                }
            }
            return localChatMessageParser;
        } catch (Exception localException) {
            Log.e(" parseChatMessage",
                    "  Exception " + localException.toString());
            localException.printStackTrace();
        }
        return null;
    }

    public LinkedHashMap<Long, DealerChatHistoryParser> parseDealerChatHistory(String paramString1, String dealerId, String conversationid) {/*
        try
		{
			Log.e(" parseDealerChatHistory", " parseDealerChatHistory ");
			JSONArray localJSONArray = new JSONObject(paramString1)
					.getJSONObject("data").getJSONArray("history");
			Log.e(" history.length()!=0 ", "history.length()!=0 "
					+ localJSONArray.length());
			if (localJSONArray.length() != 0)
			{
				LinkedHashMap<Long, DealerChatHistoryParser> historymap = new LinkedHashMap<Long, DealerChatHistoryParser>();
				///Vector<DealerChatHistoryParser> localVector = new Vector<DealerChatHistoryParser>();
				for (int i=0;i<localJSONArray.length();i++)
				{

					JSONObject localJSONObject1 = localJSONArray.getJSONObject(i);


					DealerChatHistoryParser dealerChat = new DealerChatHistoryParser();

					if (localJSONObject1.has("id"))
						dealerChat.setMessageId(localJSONObject1.getString("id"));

					dealerChat.setConversationid(conversationid);

					if (localJSONObject1.has("message"))
						dealerChat.setMessage(localJSONObject1.getString("message"));

					if (localJSONObject1.has("messagesendtime"))
					{
						dealerChat.setMessagesendtime(localJSONObject1.getString("messagesendtime"));
					}
					if (localJSONObject1.has("isdelivered"))
					{
						// 0 for Progress image , 1 for single tic image , 2 for double tic image
						if(localJSONObject1.getBoolean("isdelivered"))
						dealerChat.setIsDelivered(2);
						else
						dealerChat.setIsDelivered(1);
					}


					JSONObject localJSONObject2 = localJSONObject1.getJSONObject("sender");

					if (localJSONObject2.has("userchatid"))
					{
						dealerChat.setUserchatid(localJSONObject2.getString("userchatid"));
					}
					if (localJSONObject2.has("name"))
					{
						dealerChat.setName(localJSONObject2.getString("name"));
					}
					if (localJSONObject1.has("time"))
					{
						dealerChat.setTime(localJSONObject1.getString("time"));
						Log.e("", "time "+dealerChat.getTime());
					}
					if (localJSONObject1.has("is_notification"))
					{
						dealerChat.setIs_notification(localJSONObject1.getString("is_notification"));
					}
					if (localJSONObject1.has("chatsessionid"))
					{
						dealerChat.setChatsessionid(localJSONObject1.getString("chatsessionid"));
					}
					if (localJSONObject1.has("date"))
					{
						dealerChat.setDate(localJSONObject1.getString("date"));
					}
					if (dealerChat.getUserchatid().equalsIgnoreCase(dealerId))
					{
						dealerChat.setIncoming(true);
					}
					else
						dealerChat.setIncoming(false);

					historymap.put(Long.parseLong(dealerChat.getMessagesendtime()),dealerChat);
				}
                return historymap;
			}
			return null;
		} catch (Exception localException)
		{
			localException.printStackTrace();

		}
	*/
        return null;
    }

    public String startChatWithDealer(String userId, String name, String mobileno, String dealerId
            , String usedcarId, String lastactivedeviceId, boolean type) {
        //{"data":{"sender":{"userId":"9j9@gmail.com","name":"Zahid","usertype":"dealer",
        //"mobileno":"9782091210"},"dealerId":"183675","source":"android","usedcarId":"27052",
        //"lastactivedeviceId":"45862272"},"type":"START_DEALER"}

        try {
            JSONObject localJSONObject1 = new JSONObject();
            JSONObject localJSONObject2 = new JSONObject();
            localJSONObject2.put("userId", userId);
            localJSONObject2.put("name", name);
            localJSONObject2.put("usertype", "dealer");
            localJSONObject2.put("mobileno", mobileno);
            JSONObject localJSONObject3 = new JSONObject();
            localJSONObject3.put("sender", localJSONObject2);
            localJSONObject3.put("source", "android");
            localJSONObject3.put("dealerId", dealerId);
            localJSONObject3.put("usedcarId", usedcarId);
            localJSONObject3.put("lastactivedeviceId", lastactivedeviceId);

            localJSONObject1.put("data", localJSONObject3);
            if (type) {
                localJSONObject1.put("type", "START_DEALER");
            } else
                localJSONObject1.put("type", "DEALER_CHAT_HISTORY");

            return localJSONObject1.toString();

        } catch (Exception localException) {
            localException.printStackTrace();
            return null;
        }
    }

    public JSONObject dealerChatHistory(String userchatid, String conversationid, String chatsessionid, String usertype, String lastMessageId, String deviceId) {
        //{"data":{"sender":{"userchatid":"","usertype":"dealer"},"conversationid":
        //	"6bdc55d370094ac9acd16bb3e0ff9983","chatsessionid":""},"type":"DEALER_CHAT_HISTORY"}
        try {
            JSONObject localJSONObject1 = new JSONObject();
            JSONObject localJSONObject2 = new JSONObject();
            localJSONObject2.put("userchatid", userchatid == null ? "" : userchatid);
            localJSONObject2.put("usertype", usertype);
            JSONObject localJSONObject3 = new JSONObject();
            localJSONObject3.put("sender", localJSONObject2);
            localJSONObject3.put("conversationid", conversationid);
            localJSONObject3.put("chatsessionid", chatsessionid);
            localJSONObject3.put("source", "ANDROID");
            localJSONObject1.put("data", localJSONObject3);
            localJSONObject1.put("type", "DEALER_CHAT_HISTORY");
            localJSONObject3.put("id", lastMessageId);
            localJSONObject3.put("deviceId", deviceId);
            return localJSONObject1;
        } catch (Exception localException) {
            localException.printStackTrace();
            return null;
        }
    }

    public Vector<ChatMessageParser> parseHistoryChatMessage(String paramString1, String userchatid) {
        try {
            JSONArray localJSONArray = new JSONObject(paramString1).getJSONObject("data").getJSONArray("history");
            Log.e(Constants.TAG, " history length() =  " + localJSONArray.length());
            if (localJSONArray.length() != 0) {
                Vector<ChatMessageParser> localVector = new Vector<ChatMessageParser>();
                for (int i = 0; i < localJSONArray.length(); i++) {
                    JSONObject localJSONObject1 = localJSONArray.getJSONObject(i);
                    ChatMessageParser localChatMessageParser = new ChatMessageParser();
                    if (localJSONObject1.has("message")) {
                        localChatMessageParser.setMessage(localJSONObject1
                                .getString("message"));
                    }
                    JSONObject localJSONObject2 = localJSONObject1
                            .getJSONObject("sender");
                    if (localJSONObject2.has("userchatid")) {
                        localChatMessageParser.setUserchatid(localJSONObject2
                                .getString("userchatid"));
                    }
                    if (localJSONObject2.has("name")) {
                        localChatMessageParser.setName(localJSONObject2
                                .getString("name"));
                    }
                    if (localJSONObject1.has("chatsessionid")) {
                        localChatMessageParser.setChatsessionid(localJSONObject1
                                .getString("chatsessionid"));
                    }
                    if (localJSONObject1.has("supportroomname")) {
                        localChatMessageParser.setSupportroomname(localJSONObject1
                                .getString("supportroomname"));
                    }
                    if (localJSONObject1.has("time")) {
                        localChatMessageParser.setTime(localJSONObject1
                                .getString("time"));
                    }
                    if (localJSONObject1.has("date")) {
                        localChatMessageParser.setDate(localJSONObject1
                                .getString("date"));
                    }
                    if (localChatMessageParser.getUserchatid().equalsIgnoreCase(userchatid)) {
                        localChatMessageParser.setIncoming(false);
                    } else
                        localChatMessageParser.setIncoming(true);

                    localVector.add(localChatMessageParser);
                }
                return localVector;
            }
            return null;
        } catch (Exception localException) {
            localException.printStackTrace();
            return null;
        }
    }

    public JSONObject dealerSentMessage(Context paramContext, String message, String userchatid, String name, String conversationid, String chatsessionid, String usedcarId, String usertype, boolean currentStatus, String dealerId, String sentMesageTime) {
        try {
            //{"data":{"message":"hello","sender":{"userchatid":"da5c8d5c3cf64d148f6259635075f50b","name":"Mukesh Swami","usertype":"dealer"},
            //"conversationid":"6bdc55d370094ac9acd16bb3e0ff9983","usedcarId":"27052","chatsessionid":"36932436c0294d1e87b844d2f5f4b45c"},"type":"DEALER_CHAT"}

            Log.e("dealerSentMessage", " msg " + message);
            JSONObject localJSONObject1 = new JSONObject();
            JSONObject localJSONObject2 = new JSONObject();
            localJSONObject2.put("userchatid", userchatid);
            localJSONObject2.put("name", name);
            localJSONObject2.put("usertype", usertype);
            JSONObject localJSONObject3 = new JSONObject();
            localJSONObject3.put("message", message);
            localJSONObject3.put("messagesendtime", sentMesageTime);
            localJSONObject3.put("online_status", currentStatus ? 1 : 0);
            localJSONObject3.put("source", "ANDROID");
            localJSONObject3.put("sender", localJSONObject2);
            localJSONObject3.put("conversationid", conversationid);
            localJSONObject3.put("chatsessionid", chatsessionid);
            localJSONObject3.put("usedcarId", usedcarId);
            localJSONObject3.put("dealerId", dealerId);
            localJSONObject1.put("data", localJSONObject3);
            localJSONObject1.put("type", "DEALER_CHAT");
            Log.e("dealerSentMessage", "  " + localJSONObject1.toString());
            return localJSONObject1;
        } catch (Exception localException) {
            localException.printStackTrace();
            return null;

        }
    }

    public DealerChatHistoryParser parseConsumerChatMessage(String json) {/*
        try
		{
			//Log.e(" parseConsumerChatMessage", "  parseConsumerChatMessage ");
			//Log.e(" ChatMessageParser", "  json " + json);
			DealerChatHistoryParser localChatMessageParser = new DealerChatHistoryParser();
			JSONObject localJSONObject1 = new JSONObject(json).getJSONObject("data");

			if (localJSONObject1.has("message"))
			{
				localChatMessageParser.setMessage(localJSONObject1.getString("message"));
			}
			if (localJSONObject1.has("conversation_list"))
			{
				JSONObject conversation_listJSONObject1 = localJSONObject1.getJSONObject("conversation_list");

				if (conversation_listJSONObject1.has("messagesendtime"))
				{
					localChatMessageParser.setMessagesendtime(conversation_listJSONObject1.getString("messagesendtime"));
				}
			}




			if (localJSONObject1.has("messageId"))
			{
				localChatMessageParser.setMessageId(localJSONObject1.getString("messageId"));
			}

			if (localJSONObject1.has("lastchattime"))
			{
				localChatMessageParser.setTime(localJSONObject1.getString("lastchattime"));
			}
			JSONObject localJSONObject2 = localJSONObject1.getJSONObject("sender");
			if (localJSONObject2.has("userchatid"))
			{
				localChatMessageParser.setUserchatid(localJSONObject2.getString("userchatid"));
			}
			if (localJSONObject2.has("name"))
			{
				localChatMessageParser.setName(localJSONObject2.getString("name"));
			}
			if (localJSONObject1.has("conversationid"))
			{
				localChatMessageParser.setConversationid(localJSONObject1.getString("conversationid"));
			}
			if (localJSONObject1.has("chatsessionid"))
			{
				localChatMessageParser.setChatsessionid(localJSONObject1.getString("chatsessionid"));
			}
			localChatMessageParser.setIncoming(true);
			return localChatMessageParser;
		} catch (Exception localException) {
			Log.e(" parseChatMessage",
					"  Exception " + localException.toString());
			localException.printStackTrace();
		}

	*/
        return null;
    }

    public JSONObject dealerTyping(String userId, String name, String conversationid, String chatsessionid) {
        //{"data":{"message":"Mukesh is typing...","sender":{"userchatid":"","usertype":"dealer"},"
        //conversationid":"6bdc55d370094ac9acd16bb3e0ff9983","chatsessionid":""},"type":"DEALER_TYPING"}

        JSONObject localJSONObject1 = null;
        try {
            localJSONObject1 = new JSONObject();
            JSONObject sender = new JSONObject();
            sender.put("userchatid", userId);
            sender.put("usertype", "dealer");
            JSONObject localJSONObject3 = new JSONObject();
            localJSONObject3.put("message", name + " is typing...");
            localJSONObject3.put("sender", sender);
            localJSONObject3.put("conversationid", conversationid == null ? "" : conversationid);
            localJSONObject3.put("chatsessionid", chatsessionid == null ? "" : chatsessionid);
            localJSONObject1.put("data", localJSONObject3);
            localJSONObject1.put("type", "DEALER_TYPING");
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return localJSONObject1;

    }

    public JSONObject dealerNotTyping(String userId, String name, String conversationid, String chatsessionid) {
        JSONObject localJSONObject1 = null;
        try {
            localJSONObject1 = new JSONObject();
            JSONObject sender = new JSONObject();
            sender.put("userchatid", userId);
            sender.put("usertype", "dealer");
            JSONObject localJSONObject3 = new JSONObject();
            localJSONObject3.put("message", name + " is typing...");
            localJSONObject3.put("sender", sender);
            localJSONObject3.put("conversationid", conversationid == null ? "" : conversationid);
            localJSONObject3.put("chatsessionid", chatsessionid == null ? "" : chatsessionid);
            localJSONObject1.put("data", localJSONObject3);
            localJSONObject1.put("type", "DEALER_NOT_TYPING");
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return localJSONObject1;
    }

    public JSONObject online(String dealer_id, String deviceId) {

        JSONObject localJSONObject1 = null;
        try {
            localJSONObject1 = new JSONObject();
            JSONObject sender = new JSONObject();
            sender.put("dealer_id", dealer_id);
            sender.put("deviceId", deviceId);
            sender.put("usertype", "dealer");
            JSONObject localJSONObject3 = new JSONObject();
            localJSONObject3.put("sender", sender);
            localJSONObject1.put("data", localJSONObject3);
            localJSONObject1.put("type", "ONLINE");
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return localJSONObject1;
    }

    public JSONObject offline(String dealer_id, String deviceId) {
        JSONObject localJSONObject1 = null;
        try {
            localJSONObject1 = new JSONObject();
            JSONObject sender = new JSONObject();
            sender.put("dealer_id", dealer_id);
            sender.put("usertype", "dealer");
            sender.put("deviceId", deviceId);
            JSONObject localJSONObject3 = new JSONObject();
            localJSONObject3.put("sender", sender);
            localJSONObject1.put("data", localJSONObject3);
            localJSONObject1.put("type", "OFFLINE");
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return localJSONObject1;
    }

    public JSONObject dealerChatMessageRead(String userchatid, String conversationid, String time, String screen_name) {
        try {
            JSONObject localJSONObject1 = new JSONObject();
            JSONObject localJSONObject2 = new JSONObject();
            localJSONObject2.put("userchatid", userchatid);
            localJSONObject2.put("usertype", "dealer");
            JSONObject localJSONObject3 = new JSONObject();
            localJSONObject3.put("sender", localJSONObject2);
            localJSONObject3.put("conversationid", conversationid);
            localJSONObject3.put("messagesendtime", time);
            localJSONObject3.put("screen_name", screen_name);
            localJSONObject1.put("data", localJSONObject3);
            localJSONObject1.put("type", "DEALER_CHAT_MESSAGE_READ");
            Log.e("dealerChatMessageRead", "  " + localJSONObject1.toString());
            String str = localJSONObject1.toString();
            return localJSONObject1;
        } catch (Exception localException) {
            localException.printStackTrace();
        }
        return null;
    }
    /*public void WriteSdCard(String text)
	 {
		 try
		 {
			boolean isSDPresent = android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
			if(!isSDPresent)return;

			 String filename="CarDekhoDealerMartMChatLogs.txt";
			 DateFormat df1 = new SimpleDateFormat("hh:mm aa MMM dd yyyy");
			 String temp="\n"+df1.format(System.currentTimeMillis())+"\n"+text;
			 String Sdcard=Environment.getExternalStorageDirectory().getAbsolutePath();
			 File outfile=new File(Sdcard+File.separator+filename.trim());
			 FileOutputStream fos= new FileOutputStream(outfile, true);
			 fos.write(temp.getBytes());
			 fos.close();
		 }
		 catch(Exception e)
		 {
			 e.printStackTrace();
		 }
	 }*/
}
