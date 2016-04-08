package com.gcloud.gaadi.chat;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings.Secure;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.gcloud.gaadi.R;
import com.gcloud.gaadi.chat.Utility.CHATLIST_MESSAGE_TYPE;
import com.gcloud.gaadi.ui.BaseActivity;

import org.java_websocket.client.WebSocketClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.channels.NotYetConnectedException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;

public class DealerChatListActivity extends BaseActivity implements
        OnItemClickListener, WebSocketCallback {

    ProgressDialog progressdialog = null;
    String deviceId = null;
    private ListView mChatList;
    private ChatListAdapter mAdapter;
    private WebSocketClient mWebSocketClient;
    private SharedPreferences prefs;
    private ChatList mList;
    private LinkedHashMap<String, ChatListItem> mChatListItems;
    //	private ProgressBar mProgressBar;
    private String dealerID = "";
    private String mLastId = "0";
    private boolean loadMoreHistory = false, isHistory;

    // CommanMethods commanMethods;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.chat_list, frameLayout);
        mChatList = (ListView) findViewById(R.id.chat_listview);
        mChatList.setOnItemClickListener(this);
        mChatList.setDivider(getResources().getDrawable(R.drawable.separator_chat));
//		mProgressBar = (ProgressBar) findViewById(R.id.chatlist_progressBar);
        mLastId = "0";

        LayoutInflater inflater = getLayoutInflater();
        ViewGroup header = (ViewGroup) inflater.inflate(R.layout.header_view,
                mChatList, false);
        mChatList.addHeaderView(header);
        deviceId = Secure.getString(getContentResolver(), Secure.ANDROID_ID);
        mChatList.setOnScrollListener(new OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                // TODO Auto-generated method stub
                if (firstVisibleItem + visibleItemCount == totalItemCount) {
                    Log.e(" onScroll ", " mLastId loadMoreHistory " + loadMoreHistory);

                    if (mAdapter != null && mAdapter.getCount() != 0 && loadMoreHistory/* && !isHistory */) {
                        mLastId = "" + mAdapter.getmMaxId();/*mAdapter.getCount()-1).getid()*/
                        ;
                        Log.e(" onScroll ", " mLastId " + mLastId);
                        Log.e(" onScroll ", " mLastId " + mLastId);
                        Log.e(" onScroll ", " mLastId " + mLastId);

                        if (mLastId != null && !mLastId.isEmpty()) {
                            loadMoreHistory = false;
                            isHistory = true;
                            sendRequest();
                        }
                    }
                }
            }
        });
        if (Utility.isNetworkAvailable(this)) {
            progressdialog = new ProgressDialog(DealerChatListActivity.this);
            progressdialog.setMessage("Please wait Loading...");
            progressdialog.setCancelable(false);
            progressdialog.show();
        }

    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        // connectWebSocket();
        Log.e(" onResume ", " onResume ");
        Log.e(" onResume ", " onResume ");
        Log.e(" onResume ", " onResume ");
        PreferenceSettings.openDataBase(this);
        dealerID = PreferenceSettings.getDealerId();//"183675"; //prefs.getString(UtilityConstants.DEALER_ID, null);
        ConnectionManager.getInstance().connect(DealerChatListActivity.this);
        if (Utility.isNetworkAvailable(this)) {
            /*progressdialog = new ProgressDialog(DealerChatListActivity.this);
            progressdialog.setMessage("Please wait Loading...");
			progressdialog.setCancelable(true);
    		progressdialog.show();*/
            if (ConnectionManager.getInstance().isConnected()) {
                sendRequest();
            }
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (progressdialog != null && progressdialog.isShowing()) {

                        progressdialog.dismiss();
                        progressdialog = null;
                        Toast.makeText(getApplicationContext(), "Unable to create connection with server, Please Try Again", Toast.LENGTH_LONG).show();
                        finish();

                    }
                }
            }, 30000);
        } else {
            showNointernetToast();
        }
    }

    private void sendRequest() {
        // TODO Auto-generated method stub
        JSONObject mainJsonObject = getChatJSONMessage("", "dealer", "", "",
                dealerID);
        try {
            if (ConnectionManager.getInstance().isConnected()) {
                JSONObject request = CommanMethods.getInstance().online(dealerID, deviceId);

                Log.e(" ", "sendRequest status request 1 " + request.toString());
                Log.e(" ", "sendRequest status request 1 " + request.toString());

                //CommanMethods.getInstance().WriteSdCard(" ChatList Request : "+mainJsonObject.toString());
                ConnectionManager.getInstance().sendMessage(mainJsonObject, false);
                ConnectionManager.getInstance().sendMessage(request, false);

            } else
                ConnectionManager.getInstance().connect(this);
        } catch (NotYetConnectedException e) {
            e.printStackTrace();

        }
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();

        try {
            if (progressdialog != null && progressdialog.isShowing()) {

                progressdialog.dismiss();
                progressdialog = null;
            }
            if (mAdapter != null) {
                mAdapter.setmMaxId(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (ConnectionManager.getInstance().isConnected()) {
            ConnectionManager.getInstance().sendMessage(CommanMethods.getInstance().offline(dealerID, deviceId), false);
            ConnectionManager.getInstance().closeSocketConnection();
        }
        ConnectionManager.getInstance().setSocketNull();
    }

    private void parseData(String message) {
        // TODO Auto-generated method stub
        ChatListParser mParser = new ChatListParser();
        try {
            ChatList mList = (ChatList) mParser.parse(new JSONObject(message));
            setDataInList(mList, message);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
//		mProgressBar.setVisibility(View.GONE);
        finish();
    }

    private void setDataInList(ChatList mList, String msg) {
        // TODO Auto-generated method stub

//		mProgressBar.setVisibility(View.GONE);
        if (mList.getType() != null
                && mList.getType().equals(
                CHATLIST_MESSAGE_TYPE.CONVERSATION_LIST)) {
            try {
                if (progressdialog != null && progressdialog.isShowing()) {

                    progressdialog.dismiss();
                    progressdialog = null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            // mAdapter = null;
            if (isHistory) {
                LinkedHashMap<String, ChatListItem> temp = mList.getmChatListItems();
                mChatListItems.putAll(mList.getmChatListItems());
                isHistory = false;
                if (!temp.isEmpty()) {
                    loadMoreHistory = true;
                } else {
                    loadMoreHistory = false;
                }
            } else {
                mChatListItems = mList.getmChatListItems();
                PreferenceSettings.openDataBase(this);
                if (mChatListItems.isEmpty() && mLastId.equals("0")) {
                    String response = PreferenceSettings.getList();
                    if (response == null)
                        Toast.makeText(this, "No one in your chat list", Toast.LENGTH_LONG).show();
                    else {
                        try {
                            ChatListParser mParser = new ChatListParser();
                            mList = (ChatList) mParser.parse(new JSONObject(response));
                            mChatListItems = mList.getmChatListItems();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    if (mLastId.equals("0"))
                        PreferenceSettings.setList(msg);
                }
                if (!mChatListItems.isEmpty()) {
                    loadMoreHistory = true;
                } else {
                    loadMoreHistory = false;
                }
            }
            ArrayList<ChatListItem> temp = new ArrayList<ChatListItem>(
                    mChatListItems.values());
            Log.e(" List ", "temp Size " + temp.size());
            Collections.sort(temp, new Comparator<ChatListItem>() {
                @Override
                public int compare(ChatListItem item1, ChatListItem item2) {
                    long temp1 = Long.parseLong(item1.getTimestamp());
                    long temp2 = Long.parseLong(item2.getTimestamp());
                    return (temp2 < temp1) ? -1 : ((temp2 == temp1) ? 0 : 1);
                }
            });
            if (mAdapter == null) {
                mAdapter = new ChatListAdapter(this,
                        new ArrayList<ChatListItem>(temp));
                mChatList.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
            } else {
                mAdapter.setDataInList(new ArrayList<ChatListItem>(temp));
                mAdapter.notifyDataSetChanged();
            }
//			mAdapter = new ChatListAdapter(this,
//					new ArrayList<ChatListItem>(mChatListItems.values()));
//			mChatList.setAdapter(mAdapter);
//			mAdapter.notifyDataSetChanged();
        } else if (mList.getType() != null && (mList.getType().equals(CHATLIST_MESSAGE_TYPE.DEALER_CHAT) || mList
                .getType().equals(CHATLIST_MESSAGE_TYPE.EXPERT_CHAT))) {
            try {
                if (progressdialog != null && progressdialog.isShowing()) {

                    progressdialog.dismiss();
                    progressdialog = null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            ArrayList<ChatListItem> temp = new ArrayList<ChatListItem>(mList
                    .getmChatListItems().values());
            ChatListItem mItem = temp.get(0);
            if (mItem != null) {
                sendReadChatMessage(mItem.getUserchatid(), mItem.getMessagesendtime(), mItem.getConversationid());
            }
            if (mChatListItems != null) {
                mChatListItems.remove(mItem.getConversationid());
                mChatListItems.put(mItem.getConversationid(), mItem);
                ArrayList<ChatListItem> temp1 = new ArrayList<ChatListItem>(mChatListItems.values());
                Collections.reverse(temp1);
                mAdapter.setDataInList(temp1);
                mAdapter.notifyDataSetChanged();
            }

        }

    }

    private void sendReadChatMessage(String userchatid, String time, String conversationid) {
        JSONObject request = null;
        request = CommanMethods.getInstance().dealerChatMessageRead(userchatid, conversationid, time, "list");
        if (ConnectionManager.getInstance().isConnected() && request != null && Utility.isNetworkAvailable(this)) {
            ConnectionManager.getInstance().sendMessage(request, false);
        }
    }

    @SuppressLint("NewApi")
    private JSONObject getChatJSONMessage(/*
										 * String userchatid, String userId,
										 * String mobileno
										 */String userchatid, String usertype,
                                          String userId, String mobileno, String dealerId) {
        JSONObject jsonObjectMain = new JSONObject();
        JSONObject dataJSONJsonObject = new JSONObject();
        JSONObject senderJSONObject = new JSONObject();
        try {
            if (/*
				 * userId != null && !userId.isEmpty() && mobileno != null &&
				 * !mobileno.isEmpty()
				 */true) {
                senderJSONObject.put("userchatid", "");
                senderJSONObject.put("userId", userId);
                senderJSONObject.put("mobileno", mobileno);
                senderJSONObject.put("usertype", "dealer");
                dataJSONJsonObject.put("sender", senderJSONObject);
                dataJSONJsonObject.put("dealerId", dealerId);
                dataJSONJsonObject.put("conversationid", "");
                dataJSONJsonObject.put("chatsessionid", "");
                dataJSONJsonObject.put("id", mLastId != null ? mLastId : "0");
                dataJSONJsonObject.put("deviceId", deviceId);
                jsonObjectMain.put("data", dataJSONJsonObject);
                jsonObjectMain.put("type", "CONVERSATION_LIST");
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            // return null;
        }

        return jsonObjectMain;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        // TODO Auto-generated method stub
        Log.e("onItemClick ", "position " + position);
        Log.e("onItemClick ", "mAdapter " + mAdapter);

        if (mAdapter != null) {
            if (mAdapter.getCount() > 0) {
                ChatListItem item = mAdapter.getListItem(position <= 0 ? 0 : position - 1);
                ExpertChatListItem item1 = null;
                DealerChatListItem item2 = null;

                Log.e("onItemClick ", "item " + item);
		
				/*if (item instanceof ExpertChatListItem)
					item1 = (ExpertChatListItem) item;
				else*/
                if (item instanceof DealerChatListItem)
                    item2 = (DealerChatListItem) item;

                if (item1 != null) {
					/*
					 * Intent intent = new Intent(this,ChatAcitivity.class);
					 * intent.putExtra("chatsessionid", item1.getChatsessionid());
					 * intent.putExtra("conversationid", item1.getConversationid());
					 * intent.putExtra("chat_with", item1.getChat_with());
					 * intent.putExtra("consumerid", item1.getEnduserchatid());
					 * intent.putExtra("consumername", item1.getEndusername());
					 * intent.putExtra("Supportroomname", item1.getSupportroomname());
					 * intent.putExtra("supportname", item1.getName());
					 * intent.putExtra("supportid", item1.getUserchatid());
					 * intent.putExtra("lastchattime", item1.getLastchattime());
					 * intent.putExtra("lastchatmsg", item1.getLastchattime());
					 * startActivity(intent); //mWebSocketClient.close(); finish();
					 */
                } else if (item2 != null) {
                    Intent intent = new Intent(this, DealerChatActivity.class);
                    intent.putExtra("lastchatmsg", item2.getLastchatmsg());
                    intent.putExtra("conversationid", item2.getConversationid());
                    intent.putExtra("chat_with", item2.getChat_with());
                    intent.putExtra("variantId", item2.getVariantId());
                    intent.putExtra("km", item2.getKm());
                    intent.putExtra("modelyear", item2.getModelyear());

                    intent.putExtra("lastchattime", item2.getLastchattime());
                    intent.putExtra("offerprice", item2.getOfferprice());
                    intent.putExtra("userchatid", item2.getUserchatid());
                    intent.putExtra("price", item2.getPrice());
                    intent.putExtra("name", item2.getName());
                    intent.putExtra("chatsessionid", item2.getChatsessionid());
                    intent.putExtra("imageurl", item2.getImageurl());
                    startActivity(intent);
                    // mAdapter=null;
                    // mWebSocketClient.close();
                    // finish();
                }
            }

        }
    }

    @Override
    public void parse(final String msg) {
        // TODO Auto-generated method stub
        Log.e("  ", " mLastId parse " + msg);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //CommanMethods.getInstance().WriteSdCard(" ChatList Response : "+msg);

                // stuff that updates ui
                parseData(msg);

            }
        });
    }

    @Override
    public void onClose(String messgae) {
        // TODO Auto-generated method stub
        //showErrorCloseToast();
    }

    @Override
    public void onError(Exception e) {
        // TODO Auto-generated method stub
        //showErrorCloseToast();

    }

    @Override
    public void onConnect() {
        // TODO Auto-generated method stub
        Log.e("onConnect ", "onConnect ");
        Log.e("onConnect ", "onConnect ");
        Log.e("onConnect ", "onConnect ");
        Log.e("onConnect ", "onConnect ");

        //if(mAdapter!=null && mAdapter.getCount()==0)
        //{
        mLastId = "0";
        sendRequest();
        //}
    }


    @Override
    public void onConnectivityStatusChanged(boolean connectivityFlag) {
        // TODO Auto-generated method stub
        if (!connectivityFlag) {
            Toast.makeText(this,
                    getResources().getString(R.string.no_internet_msg),
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void updateMessageOnUI(boolean status, boolean isChatMessage) {
        // TODO Auto-generated method stub

    }

    @Override
    public void sendMessage(String message, boolean isChatMessage) {
        // TODO Auto-generated method stub

    }

    public void showNointernetToast() {
        try {
            if (progressdialog != null && progressdialog.isShowing()) {

                progressdialog.dismiss();
                progressdialog = null;
            }
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_internet_msg), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showErrorCloseToast() {
        try {
            if (progressdialog != null && progressdialog.isShowing()) {

                progressdialog.dismiss();
                progressdialog = null;
            }
            //Toast.makeText(getApplicationContext(),getResources().getString(R.string.error_msg), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
