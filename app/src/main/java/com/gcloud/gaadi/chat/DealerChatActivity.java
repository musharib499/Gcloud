package com.gcloud.gaadi.chat;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.provider.Settings.Secure;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gcloud.gaadi.R;
import com.gcloud.gaadi.chat.SwipeRefreshLayout.OnRefreshListener;
import com.gcloud.gaadi.chat.model.DealerChatHistoryModel;
import com.gcloud.gaadi.chat.model.DealerChatMEssageReceivedModel;
import com.gcloud.gaadi.chat.model.DealerChatModel;
import com.gcloud.gaadi.chat.model.OfflineModel;
import com.gcloud.gaadi.chat.model.OnlineModel;
import com.gcloud.gaadi.chat.model.SenderModel;
import com.gcloud.gaadi.chat.model.ServerReceivedMessageModel;
import com.gcloud.gaadi.chat.parser.ChatParser;
import com.gcloud.gaadi.chat.parser.DealerChatHistoryParser;
import com.gcloud.gaadi.ui.BaseActivity;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import org.java_websocket.client.WebSocketClient;
import org.json.JSONException;
import org.json.JSONObject;

public class DealerChatActivity extends BaseActivity implements WebSocketCallback, OnRefreshListener, OnItemClickListener {
    RatingBar carRatingBar;
    Intent intent;
    ImageView chatCarImageView;
    TextView kmTextView, modalOfYearTextView;
    RelativeLayout chatWithDealerRelativeLayout, chatWithDealerKMAndYearRelativeLayout, typeOfChatRelativeLayout;
    //private String name = "hello1234567";
    String DEALER_TYPE = "dealer";
    String lastMessageId = "0";
    boolean isUiOpened = false;
    String previuosMessageId = null;
    JavaScriptInterface displayNotification;
    ImageView callImageView, contactImageView;
    //private String name = "hello1234567";
    ProgressDialog progressdialog = null;
    String lastchatmsg, conversationid, chat_with, variantId, km, lastchattime, offerprice, dealerId = null, price,
            chatsessionid, imageurl, consumerId = null, consumername, modelyear, userId, dealerName, usertype;
    String previousConversationId = null;
    boolean isHistory = false;
    TextView offOnTextView;
    ImageView offOnImageView;
    boolean currentStatus = false;
    String deviceId = null;
    long sentMesageTime = 0;
    ChatParser mParser;
    String phoneNumber = null;
    private String START_CHAT_WITH_DEALER = "START_CHAT_WITH_DEALER";
    private String START_CHAT_WITH_EXPERT = "START_CHAT_WITH_EXPERT";
    private TextView carChatExpertOrDealerTextView;
    private TextView carchatCostTextView;
    private TextView carchatNameTextView;
    private TextView carchatOfferPriceTextView;
    private DealerChatAdapter chatArrayAdapter;
    private EditText chatEditText;
    private ListView chatListView;
    private WebSocketClient mWebSocketClient;
    private TextView textViewTying;
    private Button sendButton;
    private boolean side = false;
    private DisplayImageOptions options;
    private ImageLoader imageLoader;
    private String DEALERID = "";
    private SharedPreferences prefs;
    private SwipeRefreshLayout mSwipeLayout;

    public static DisplayImageOptions getDisplayOption() {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.default_img)
                .cacheInMemory(true).cacheOnDisk(true)
                .showImageForEmptyUri(R.drawable.default_img)
                .showImageOnFail(R.drawable.default_img)
                .resetViewBeforeLoading(true)
                .bitmapConfig(Bitmap.Config.RGB_565).considerExifParams(true)
                .build();

        return options;
    }

    private void parseData(String paramString) {
        try {

            JSONObject localJSONObject1 = new JSONObject(paramString);
            if (localJSONObject1.has("type")) {
                String str = localJSONObject1.getString("type").trim();
                Log.e("parseData", "type " + str);
                if (str.equals("DEALER_CHAT_HISTORY")) {
                    try {
                        if (progressdialog != null && progressdialog.isShowing()) {

                            progressdialog.dismiss();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    DealerChatHistoryModel mList = null;
                    try {
                        mList = (DealerChatHistoryModel) mParser.parse(localJSONObject1);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (mList != null) {
                        if (mList.getChatWith() != null) {
                            dealerId = mList.getChatWith().getUserchatid();
                            consumerId = mList.getChatWith().getUserId();
                            dealerName = mList.getChatWith().getDealerName();
                            consumername = mList.getChatWith().getName();
                        }
                        conversationid = mList.getConversationid();
                        chatsessionid = mList.getChatsessionid();
                        offerprice = mList.getOfferprice();
                        phoneNumber = mList.getPhoneNumber();

                        if (phoneNumber != null && phoneNumber.length() != 0) {
                            callImageView.setVisibility(View.VISIBLE);
                            contactImageView.setVisibility(View.VISIBLE);
                        } else {
                            callImageView.setVisibility(View.GONE);
                            contactImageView.setVisibility(View.GONE);
                        }

                        if (offerprice != null) {
                            if (offerprice.toLowerCase().startsWith("rs")) {
                                carchatOfferPriceTextView.setText(offerprice);
                            } else {
                                carchatOfferPriceTextView.setText("Rs. " + offerprice);
                            }
                        } else
                            carchatOfferPriceTextView.setText("");

                        price = mList.getPrice();

                        if (price != null) {
                            if (price.toLowerCase().startsWith("rs"))
                                carchatCostTextView.setText(price);
                            else
                                carchatCostTextView.setText("Rs. " + price);
                        } else
                            carchatCostTextView.setText("");

                        imageurl = mList.getImageurl();

                        if (imageurl != null && !imageurl.isEmpty())
                            imageLoader.displayImage(imageurl, chatCarImageView, options);
                        else
                            chatCarImageView.setImageResource(R.drawable.default_img);


                        modelyear = mList.getModelyear();

                        if (modelyear != null)
                            modalOfYearTextView.setText(modelyear);
                        else
                            modalOfYearTextView.setText("");


                        variantId = mList.getVariantId();
                        if (variantId != null)
                            carchatNameTextView.setText(variantId);
                        else
                            carchatNameTextView.setText("");

                        km = mList.getKm();
                        if (km != null)
                            kmTextView.setText(km);
                        else
                            kmTextView.setText("");
                        chatArrayAdapter.addAll(mList.getChatItems());
                    }
                    if (isHistory)
                        chatListView.smoothScrollToPosition(0);
                    else
                        chatListView.smoothScrollToPosition(-1 + chatArrayAdapter.getCount());
                    previousConversationId = conversationid;
                    isHistory = false;
                    //chatEditText.setHint("TYPE MESSAGE TO CHAT...");
                    //textViewTying.setVisibility(View.GONE);
                    isUiOpened = true;
                    mSwipeLayout.setRefreshing(false);
                }    //
                else if (str.equals("DEALER_CHAT")) {
                    Log.e("DEALER_CHAT", "DEALER_CHAT ");
                    Log.e("DEALER_CHAT", "DEALER_CHAT ");
                    Log.e("DEALER_CHAT", "DEALER_CHAT ");
                    try {
                        if (progressdialog != null && progressdialog.isShowing()) {

                            progressdialog.dismiss();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    DealerChatModel mListModel = null;
                    DealerChatHistoryParser mList = null;
                    try {
                        mListModel = (DealerChatModel) mParser.parse(localJSONObject1);
                        if (mListModel != null)
                            mList = mListModel.getChatItem();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (mList != null) {
                        if (!previousConversationId.equals(mList.getConversationid())) {
                            JavaScriptInterface display = new JavaScriptInterface(this);
                            display.displayNotification(mList.getSender().getName(), mList);
                            return;
                        } else
                            previousConversationId = mList.getConversationid();

                        if (mList.getSender() != null) {
                            /*if(mList.getSender().getUsertype()!=null && mList.getSender().getUsertype().equalsIgnoreCase("enduser"))
                            {
								consumerId=localJSONObject3.getString("userchatid");
								consumername=localJSONObject3.getString("name");
							}
							else if(mList.getSender().getUsertype()!=null && mList.getSender().getUsertype().equalsIgnoreCase("dealer"))
							{

							}*/
                            chatsessionid = mList.getChatsessionid();
                            offerprice = mList.getOfferprice();

                            phoneNumber = mList.getPhoneNumber();

                            if (phoneNumber != null && phoneNumber.length() != 0) {
                                callImageView.setVisibility(View.VISIBLE);
                                contactImageView.setVisibility(View.VISIBLE);
                            } else {
                                callImageView.setVisibility(View.GONE);
                                contactImageView.setVisibility(View.GONE);
                            }
                            km = mList.getKm();
                            if (km != null)
                                kmTextView.setText(km);
                            else
                                kmTextView.setText("");

                            variantId = mList.getVariantId();
                            if (variantId != null)
                                carchatNameTextView.setText(variantId);
                            else
                                carchatNameTextView.setText("");


                            price = mList.getPrice();
                            if (price != null) {
                                if (price.toLowerCase().startsWith("rs"))
                                    carchatCostTextView.setText(price);
                                else
                                    carchatCostTextView.setText("Rs. " + price);
                            } else
                                carchatCostTextView.setText("");


                            if (imageurl == null || imageurl.isEmpty()) {
                                imageurl = mList.getImageurl();

                                if (imageurl != null || !imageurl.isEmpty())
                                    imageLoader.displayImage(imageurl, chatCarImageView, options);
                                else
                                    chatCarImageView.setImageResource(R.drawable.default_img);
                            }

                            modelyear = mList.getModelyear();
                            if (modelyear != null)
                                modalOfYearTextView.setText(modelyear);
                            else
                                modalOfYearTextView.setText("");
                            if (mList.getMessage().length() != 0) {
                                if (previuosMessageId != null && previuosMessageId.equals(mList.getMessageId()))
                                    return;
                                chatArrayAdapter.add(mList);
                            }
                            isHistory = false;
                            //sendChatMessage(false,false);
                            chatListView.smoothScrollToPosition(-1 + chatArrayAdapter.getCount());
                            isUiOpened = true;
                            sendReadChatMessage("" + mList.getMessagesendtime());

                        }
                    }
                } else if (str.equals("DEALER_TYPING")) {/*
					JSONObject localJSONObject2 = localJSONObject1.getJSONObject("data");


					if (localJSONObject2.has("conversationid"))

						if(previousConversationId.equals(localJSONObject2.getString("conversationid")))
							if(localJSONObject2.has("message"))
								textViewTying.setVisibility(View.VISIBLE);

					textViewTying.setText(localJSONObject2.getString("message"));
				*/
                } else if (str.equals("DEALER_NOT_TYPING")) {
                    //textViewTying.setVisibility(View.GONE);

                } else if (str.equalsIgnoreCase("ONLINE")) {
                    OnlineModel onlineModel = null;
                    try {
                        onlineModel = (OnlineModel) mParser.parse(localJSONObject1);
                        if (onlineModel != null) {
                            if (onlineModel.getConversationid() != null && onlineModel.getConversationid().equals(conversationid)) {
                                currentStatus = true;
                                offOnTextView.setText("Online");
                                offOnImageView.setImageResource(R.drawable.on);
                                textViewTying.setVisibility(View.GONE);
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        if (progressdialog != null && progressdialog.isShowing()) {

                            progressdialog.dismiss();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (str.equalsIgnoreCase("OFFLINE")) {
                    OfflineModel OfflineModel = null;
                    try {
                        OfflineModel = (OfflineModel) mParser.parse(localJSONObject1);
                        if (OfflineModel != null) {
                            if (OfflineModel.getConversationid() != null && OfflineModel.getConversationid().equals(conversationid)) {
                                currentStatus = false;
                                offOnTextView.setText("Offline");
                                offOnImageView.setImageResource(R.drawable.off);
                                textViewTying.setVisibility(View.VISIBLE);
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (str.equalsIgnoreCase("SERVER_RECEIVED_MESSAGE")) {
                    //{"data":{�messagesendtime�:�4454545454�,conversationid:"jdfg87dhf89fh8dshf8hds89fdhsfd8s"},"
                    //type":"SERVER_RECEIVED_MESSAGE"}
                    ServerReceivedMessageModel receivedMessage = null;
                    try {
                        receivedMessage = (ServerReceivedMessageModel) mParser.parse(localJSONObject1);
                        if (receivedMessage != null) {
                            if (receivedMessage.getConversationid() != null && receivedMessage.getMessagesendtime() != null && receivedMessage.getConversationid().equals(conversationid)) {
                                chatArrayAdapter.setServerReadRecipt(Long.parseLong(receivedMessage.getMessagesendtime()), receivedMessage.getConversationid());
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (str.equalsIgnoreCase("DEALER_CHAT_MESSAGE_RECEIVED")) {
                    DealerChatMEssageReceivedModel receivedMessage = null;
                    try {
                        receivedMessage = (DealerChatMEssageReceivedModel) mParser.parse(localJSONObject1);
                        if (receivedMessage != null) {
                            if (receivedMessage.getConversationid() != null && receivedMessage.getMessagesendtime() != null && receivedMessage.getConversationid().equals(conversationid)) {
                                chatArrayAdapter.setEndUserReadRecipt(Long.parseLong(receivedMessage.getMessagesendtime()), receivedMessage.getConversationid());
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    JSONObject localJSONObject2 = localJSONObject1.getJSONObject("data");
                    if (localJSONObject2.has("conversationid")) {
                        String lcoalMessagesendTime = null, lcoalConversationId = null;
                        lcoalConversationId = localJSONObject2.getString("conversationid");
                        if (localJSONObject2.has("messagesendtime"))
                            lcoalMessagesendTime = localJSONObject2.getString("messagesendtime");
                        if (lcoalConversationId != null && lcoalMessagesendTime != null && conversationid.equals(conversationid)) {
                            chatArrayAdapter.setEndUserReadRecipt(Long.parseLong(lcoalMessagesendTime), lcoalConversationId);
                        }
                    }
                } else {
                    Log.e("parseData", "type " + str);
                }
            }
            return;
        } catch (Exception localException) {
            localException.printStackTrace();
        }
    }

    private DealerChatHistoryParser returnChatMessageObject(String paramString, String id, String name, String conversationid, String chatsessionid) {
        DealerChatHistoryParser localChatMessageParser = new DealerChatHistoryParser();
        localChatMessageParser.setMessage(paramString);
        localChatMessageParser.setName(name);
        localChatMessageParser.setChatsessionid(chatsessionid);
        //localChatMessageParser.setConversationid(conversationid);
        localChatMessageParser.setTime(CommanMethods.getTime12(System.currentTimeMillis()));
        localChatMessageParser.setDate(CommanMethods.getDate12(System.currentTimeMillis()));
        localChatMessageParser.setMessagesendtime(sentMesageTime);
        localChatMessageParser.setIsMessageDelivered(0);
        localChatMessageParser.setSentMessage(true);

        SenderModel send = new SenderModel();
        send.setUserchatid(id);
        localChatMessageParser.setSender(send);
        return localChatMessageParser;
    }

    private boolean sendChatMessage(boolean isTyping, boolean isNotTyping) {
        // Send Typing and NotTyping status
        if (isTyping) {
            JSONObject request = null;
            if (isNotTyping)
                request = CommanMethods.getInstance().dealerNotTyping(dealerId, dealerName, conversationid, chatsessionid);///(dealerId,consumername);
            else
                request = CommanMethods.getInstance().dealerTyping(dealerId, dealerName, conversationid, chatsessionid);//(dealerId,consumername);
            Log.e("sendChatMessage ", " isTyping request " + request);

            if (request != null) {
                try {
                    if (ConnectionManager.getInstance().isConnected()) {
                        ConnectionManager.getInstance().sendMessage(request, false);
                        //this.dealerchatAdapter.add(returnChatMessageObjectDealer(chatEditText.getText().toString(), consumername, conversationid, chatsessionid), false, null,isHistory);
                        //this.chatEditText.setText("");
                    } else {
                        ConnectionManager.getInstance().connect(this);
                        showNointernetToast();
                        Toast.makeText(DealerChatActivity.this, "WebSocket Disconnected", Toast.LENGTH_LONG).show();
                    }

                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

        } else {
            String message = chatEditText.getText().toString().trim();

            JSONObject request = null;

            if (message.length() == 0) return true;
            sentMesageTime = System.currentTimeMillis();
            request = CommanMethods.getInstance().dealerSentMessage(this, message, dealerId, dealerName, conversationid, chatsessionid, "", DEALER_TYPE, currentStatus, dealerId, "" + sentMesageTime);

            Log.e("Dealer sendChatMessage", " request " + request.toString());

            if (ConnectionManager.getInstance().isConnected() && request != null && Utility.isNetworkAvailable(DealerChatActivity.this)) {
                ConnectionManager.getInstance().sendMessage(request, true);


            } else {
                ConnectionManager.getInstance().connect(this);
                //this.chatEditText.setText("");
                //this.chatEditText.setHint("TYPE MESSAGE TO CHAT...");
                showNointernetToast();
            }
        }

        return true;
    }

    private void sendReadChatMessage(String time) {
        JSONObject request = null;
        request = CommanMethods.getInstance().dealerChatMessageRead(dealerId, conversationid, time, "chat");
        if (ConnectionManager.getInstance().isConnected() && request != null && Utility.isNetworkAvailable(DealerChatActivity.this)) {
            ConnectionManager.getInstance().sendMessage(request, false);
        }
    }

    public void onBackPressed() {
        if (progressdialog != null && progressdialog.isShowing()) {

            progressdialog.dismiss();
        }
        finish();
    }

    public void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        getLayoutInflater().inflate(R.layout.consumer_chat_activity, frameLayout);

        Log.e("Dealer onCreate", "onCreate");

        sendButton = ((Button) findViewById(R.id.sendChatMessageButton));
        chatListView = (ListView) findViewById(R.id.carChatListView);
        chatEditText = (EditText) findViewById(R.id.chatMessageEditText);
        carchatNameTextView = ((TextView) findViewById(R.id.chatCarNameTextView));
        carchatCostTextView = ((TextView) findViewById(R.id.chatCarCostTextView));
        carchatOfferPriceTextView = (TextView) findViewById(R.id.chatWithDealerOfferPrice);
        carChatExpertOrDealerTextView = (TextView) findViewById(R.id.chatExpertOrDealerTextView);
        chatWithDealerRelativeLayout = (RelativeLayout) findViewById(R.id.chatActivtyChatWithDealerRelativeLayout);
        chatWithDealerKMAndYearRelativeLayout = (RelativeLayout) findViewById(R.id.chatActivtyDealerChatKmAndYearRelativeLayout);
        typeOfChatRelativeLayout = (RelativeLayout) findViewById(R.id.chatActivtyTypeOfChatRelativeLayout1);
        kmTextView = (TextView) findViewById(R.id.chatActivtyDealerChatKmTextView);
        modalOfYearTextView = (TextView) findViewById(R.id.chatActivtyDealerChatModalOfYearTextView);
        chatCarImageView = (ImageView) findViewById(R.id.chatCarImageView);
        carRatingBar = (RatingBar) findViewById(R.id.chatCarRatingBar);
        //cancelButton=(Button)findViewById(R.id.buttonDealerCancelOffer);
        textViewTying = (TextView) findViewById(R.id.textViewTying);
        offOnTextView = (TextView) findViewById(R.id.textViewOnlineOffline);
        offOnImageView = (ImageView) findViewById(R.id.onlineOfflineimageview);
        mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        mSwipeLayout.setOnRefreshListener(this);
        mSwipeLayout.setColorScheme(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        deviceId = Secure.getString(getContentResolver(), Secure.ANDROID_ID);
        chatListView.setOnItemClickListener(this);

        chatListView.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(chatEditText.getApplicationWindowToken(), 0);
                return false;
            }
        });
        callImageView = (ImageView) findViewById(R.id.imageViewChatCall);
        contactImageView = (ImageView) findViewById(R.id.imageViewChatContact);

        contactImageView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                AddToContact();
            }
        });
        callImageView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                CallFunction();
            }
        });

    }

    protected void onPause() {
        super.onPause();
        try {
            try {
                if (progressdialog != null && progressdialog.isShowing()) {

                    progressdialog.dismiss();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            isUiOpened = false;

            if (ConnectionManager.getInstance().isConnected()) {

                ConnectionManager.getInstance().sendMessage(CommanMethods.getInstance().offline(DEALERID, deviceId), false);
                ConnectionManager.getInstance().closeSocketConnection();
            }
            ConnectionManager.getInstance().setSocketNull();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void onResume() {
        super.onResume();
        //connectWebSocket();

        try {
            Log.e(" onResume ", " onResume ");
            Log.e(" onResume ", " onResume ");
            Log.e(" onResume ", " onResume ");

            lastMessageId = "0";
            mParser = new ChatParser();
            prefs = PreferenceManager.getDefaultSharedPreferences(this);
            PreferenceSettings.openDataBase(this);
            DEALERID = PreferenceSettings.getDealerId();

			/*chatListView.setOnScrollListener(new OnScrollListener() {

				@Override
				public void onScrollStateChanged(AbsListView view, int scrollState)
				{
					// TODO Auto-generated method stub
					Log.e(" onScrollStateChanged ", "scrollState "+scrollState+" visibleItemCount ");
				}

				@Override
				public void onScroll(AbsListView view, int firstVisibleItem,
						int visibleItemCount, int totalItemCount) {
					// TODO Auto-generated method stub
					if(firstVisibleItem==0 && totalItemCount!=0)
					{
						Log.e(" onScroll ", "dealerchatAdapter "+chatArrayAdapter);
						Log.e(" onScroll ", " isHistory "+isHistory);
						Log.e(" onScroll ", " isMoreHistoryRequest "+isMoreHistoryRequest);

						if(chatArrayAdapter!=null  && !isHistory && isMoreHistoryRequest)
						{
							lastMessageId=chatArrayAdapter.getItem(firstVisibleItem).getMessageId();
							Log.e(" onScroll ", "dealerchatAdapter getMessageId "+lastMessageId);
							isHistory=true;
							isMoreHistoryRequest=false;
							if(!lastMessageId.isEmpty())
							sendRequest();
						}


					}
				}
			});*/
            options = getDisplayOption();
            imageLoader = ImageLoader.getInstance();
            ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                    this).threadPriority(Thread.NORM_PRIORITY - 2)
                    .denyCacheImageMultipleSizesInMemory()
                    .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                    .diskCacheSize(50 * 1024 * 1024)
                            // 50 Mb
                    .tasksProcessingOrder(QueueProcessingType.LIFO).build();
            // Initialize ImageLoader with configuration.
            imageLoader.init(config);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            Bundle dataFromIntent = getIntent().getExtras();
            if (dataFromIntent != null) {

                if (dataFromIntent.containsKey("lastchatmsg"))
                    lastchatmsg = dataFromIntent.getString("lastchatmsg", null);

                if (dataFromIntent.containsKey("conversationid"))
                    conversationid = dataFromIntent.getString("conversationid", null);

                if (dataFromIntent.containsKey("chat_with"))
                    chat_with = dataFromIntent.getString("chat_with", null);

                if (dataFromIntent.containsKey("variantId"))
                    variantId = dataFromIntent.getString("variantId", null);

                if (dataFromIntent.containsKey("km"))
                    km = dataFromIntent.getString("km", null);

                if (dataFromIntent.containsKey("lastchattime"))
                    lastchattime = dataFromIntent.getString("lastchattime", null);

                if (dataFromIntent.containsKey("offerprice"))
                    offerprice = dataFromIntent.getString("offerprice", null);

                if (dataFromIntent.containsKey("userchatid"))
                    consumerId = dataFromIntent.getString("userchatid", null);

                if (dataFromIntent.containsKey("price"))
                    price = dataFromIntent.getString("price", null);

                if (dataFromIntent.containsKey("name"))
                    consumername = dataFromIntent.getString("name", null);

                if (dataFromIntent.containsKey("chatsessionid"))
                    chatsessionid = dataFromIntent.getString("chatsessionid", null);

                if (dataFromIntent.containsKey("imageurl"))
                    imageurl = dataFromIntent.getString("imageurl", null);

                typeOfChatRelativeLayout.setBackgroundColor(Color.rgb(241, 91, 41));
                carChatExpertOrDealerTextView.setText("You&apos;re chatting with Buyer");
                chatWithDealerKMAndYearRelativeLayout.setVisibility(View.VISIBLE);
                carRatingBar.setVisibility(View.GONE);


                carChatExpertOrDealerTextView.setText("Price Offered");
                carChatExpertOrDealerTextView.setPadding(10, 10, 10, 10);
                carChatExpertOrDealerTextView.setTextColor(Color.WHITE);
                carchatOfferPriceTextView.setVisibility(View.VISIBLE);

                carchatOfferPriceTextView.setTextColor(Color.WHITE);

                if (variantId != null)
                    carchatNameTextView.setText(variantId);
                else
                    carchatNameTextView.setText("");

                if (price != null) {
                    if (price.toLowerCase().startsWith("rs"))
                        carchatCostTextView.setText(price);
                    else
                        carchatCostTextView.setText("Rs. " + price);
                } else
                    carchatCostTextView.setText("");

                if (km != null)
                    kmTextView.setText(km);
                else
                    kmTextView.setText("");

                if (modelyear != null)
                    modalOfYearTextView.setText(modelyear);
                else
                    modalOfYearTextView.setText("");

                if (offerprice != null)
                    carchatOfferPriceTextView.setText("Rs. " + offerprice + " Lac*");
                else
                    carchatOfferPriceTextView.setText("");


                if (imageurl != null)
                    imageLoader.displayImage(imageurl, chatCarImageView, options);
                else
                    chatCarImageView.setImageResource(R.drawable.default_img);
            } else
                return;

        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            Log.e("onResume ", " Showing Dailog ");
            displayNotification = new JavaScriptInterface(this);
            if (Utility.isNetworkAvailable(this)) {
                progressdialog = new ProgressDialog(DealerChatActivity.this);
                progressdialog.setMessage("Please wait Loading...");
                progressdialog.setCancelable(false);
                progressdialog.show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (progressdialog != null && progressdialog.isShowing()) {

                            progressdialog.dismiss();
                            progressdialog = null;
                            if (chatArrayAdapter != null && chatArrayAdapter.getCount() == 0) {
                                Toast.makeText(getApplicationContext(), "Unable to create connection with server, Please Try Again", Toast.LENGTH_LONG).show();
                                finish();
                            }
                        }
                    }
                }, 30000);

                ConnectionManager.getInstance().connect(DealerChatActivity.this);
                if (ConnectionManager.getInstance().isConnected()) {
                    sendRequest();
                }
            } else {
                showNointernetToast();
            }
        } catch (Exception localException) {
            localException.printStackTrace();
        }


        chatArrayAdapter = new DealerChatAdapter(this);
        chatListView.setAdapter(chatArrayAdapter);
        chatEditText.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View paramAnonymousView,
                                 int paramAnonymousInt, KeyEvent paramAnonymousKeyEvent) {
                if ((paramAnonymousKeyEvent.getAction() == 0)
                        && (paramAnonymousInt == 66)) {
                    return sendChatMessage(false, false);
                }
                return false;
            }
        });
        sendButton.setOnClickListener(new OnClickListener() {
            public void onClick(View paramAnonymousView) {
                if (Utility.isNetworkAvailable(getApplicationContext())) {
                    sendChatMessage(false, false);
                } else {
                    ConnectionManager.getInstance().setSocketNull();
                    //showNointernetToast();
                }
            }
        });
        //chatListView.setTranscriptMode(2);
	/*	chatArrayAdapter.registerDataSetObserver(new DataSetObserver()
		{
			public void onChanged()
			{
				super.onChanged();
			}
		});*/
		/*chatEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count)
			{
				// TODO Auto-generated method stub
				Log.e("onTextChanged", "onTextChanged");
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				Log.e("beforeTextChanged", "beforeTextChanged");
				//sendChatMessage(true,false);

			}

			@Override
			public void afterTextChanged(Editable s)
			{
				// TODO Auto-generated method stub
				Log.e("afterTextChanged", " afterTextChanged s.length() "+s.length());

				if(s.length()==0)
				{
					//sendChatMessage(true,true);
				}
				else if(s.length()<=5)
				{
					//tyingStatusSend=true;
					//sendChatMessage(true,false);
				}
			}
		});*/
    }

    private void sendRequest() {
        // TODO Auto-generated method stub
        try {
            JSONObject request = CommanMethods.getInstance().dealerChatHistory(consumerId, conversationid, chatsessionid, DEALER_TYPE, lastMessageId, deviceId);
            if (ConnectionManager.getInstance().isConnected() && Utility.isNetworkAvailable(DealerChatActivity.this)) {
                //CommanMethods.getInstance().WriteSdCard(" Chat Request : "+request.toString());
                ConnectionManager.getInstance().sendMessage(request, false);
                ConnectionManager.getInstance().sendMessage(CommanMethods.getInstance().online(DEALERID, deviceId), false);
            } else {
                ConnectionManager.getInstance().connect(this);
                //showNointernetToast();
            }
        } catch (Exception localException) {
            localException.printStackTrace();
        }
    }

    @Override
    public void parse(final String msg) {
        // TODO Auto-generated method stub
        //CommanMethods.getInstance().WriteSdCard(" Chat Response : "+msg);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                //stuff that updates ui
                parseData(msg);

            }
        });
    }

    @Override
    public void onClose(String messgae) {
        // TODO Auto-generated method stub
        try {
            showErrorCloseToast();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    @Override
    public void onError(Exception e) {
        // TODO Auto-generated method stub
        try {
            showNointernetToast();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    @Override
    public void onConnect() {
        // TODO Auto-generated method stub
        if (chatArrayAdapter != null) {
            if (chatArrayAdapter.getCount() == 0)
                sendRequest();
        }
    }

    @Override
    public void onConnectivityStatusChanged(boolean connectivityFlag) {
        // TODO Auto-generated method stub
        if (!connectivityFlag) {
//			Toast.makeText(getActivity(), "Please check your internet connection", Toast.LENGTH_LONG).show();
            showNointernetToast();
        }
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

    @Override
    public void onRefresh() {
        // TODO Auto-generated method stub
        Log.e(" onRefresh ", " Sending History Request ");

        if (Utility.isNetworkAvailable(this)) {
            if (chatArrayAdapter != null && chatArrayAdapter.getCount() > 0) {
                lastMessageId = chatArrayAdapter.getItem(/*firstVisibleItem*/0).getId();
                Log.e(" onRefresh ", " chatArrayAdapter getMessageId " + lastMessageId);
                isHistory = true;
                if (lastMessageId != null && !lastMessageId.isEmpty()) {
                    sendRequest();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mSwipeLayout.setRefreshing(false);
                        }
                    }, 20000);
                }
            } else {
                mSwipeLayout.setRefreshing(false);
            }
        } else {
            mSwipeLayout.setRefreshing(false);
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_internet_msg), Toast.LENGTH_SHORT).show();

        }

    }

    @Override
    public void updateMessageOnUI(boolean status, boolean isChatMessage) {
        // TODO Auto-generated method stub
        if (status && isChatMessage) {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    chatArrayAdapter.add(returnChatMessageObject(chatEditText
                            .getText().toString(), dealerId, dealerName, conversationid, chatsessionid));
                    chatListView.smoothScrollToPosition(-1 + chatArrayAdapter.getCount());
                    chatEditText.setText("");
                    chatEditText.setHint("Type here...");
                }
            });
        }
    }

    @Override
    public void sendMessage(String message, boolean isChatMessage) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        // TODO Auto-generated method stub
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(chatEditText.getApplicationWindowToken(), 0);
    }

    void CallFunction() {
        if (phoneNumber != null && phoneNumber.length() != 0) {
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:" + phoneNumber));
            startActivity(intent);
        }
    }

    void AddToContact() {
        if (phoneNumber != null && phoneNumber.length() != 0) {
            Intent intent = new Intent(Intent.ACTION_INSERT);
            intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
            intent.putExtra(ContactsContract.Intents.Insert.NAME, consumername);
            intent.putExtra(ContactsContract.Intents.Insert.PHONE, phoneNumber);
            startActivityForResult(intent, 100);
        }
    }
}
