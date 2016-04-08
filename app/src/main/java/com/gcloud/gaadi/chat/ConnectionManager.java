package com.gcloud.gaadi.chat;

import android.content.Context;

import com.gcloud.gaadi.ApplicationController;
import com.gcloud.gaadi.utils.GCLog;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;

public class ConnectionManager {

    private static ConnectionManager mInstance = null;
    private static String host = "chatapi.cardekho.com:8080";
    public static String mUri = "ws://" + host + "/CarDekhoChat/chatwebsocket";
    //	private static String host = "chatapi.cardekho.com:7070";
    //private static String host = "192.168.32.32:8081";
    //private static String host = "192.168.33.30:8081";
    private WebSocketClient mWebSocketClient = null;
    private WebSocketCallback mCallback;

    private boolean isConnected = false;

    private ConnectionManager() {
    }

    public static ConnectionManager getInstance() {
        GCLog.e("getInstance " + mInstance);
        if (mInstance == null) {
            mInstance = new ConnectionManager();
        }
        return mInstance;
    }

    public void connect(WebSocketCallback callback) {
        mCallback = callback;

        GCLog.e("  connect mWebSocketClient " + mWebSocketClient);
        try {

            if (mWebSocketClient == null) {
                connectWebSocket();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void connectWebSocket(/* WebSocketCallback callback */) {

        // TODO Auto-generated method stub

        URI uri;
        try {
            uri = new URI(mUri);
            GCLog.e(mUri);

        } catch (URISyntaxException e) {
            e.printStackTrace();

            GCLog.e(e.toString() + "");

            return;
        }

        try {
            mWebSocketClient = new WebSocketClient(uri, new Draft_17()) {

                @Override
                public void connect() {
                    // TODO Auto-generated method stub
                    super.connect();
                    GCLog.e("connect");

                }

                @Override
                public void onOpen(ServerHandshake serverHandshake) {
                    GCLog.e("Opened");
                    isConnected = true;
                    mCallback.onConnect();
                }

                @Override
                public void onMessage(String s) {
                    final String message = s;
                    GCLog.e(" chat " + s);
                    mCallback.parse(s);
                    // runOnUiThread(new Runnable() {
                    // @Override
                    // public void run() {
                    //
                    // parseData(message);
                    // Log.e("onMessage", message);
                    // }
                    // });
                }

                @Override
                public void onClose(int i, String s, boolean b) {
                    GCLog.i("Closed " + s);
                    mWebSocketClient = null;
                    isConnected = false;
                    mCallback.onClose(s);
                }

                @Override
                public void onError(Exception e) {
                    GCLog.i("Error " + e.toString());
                    mWebSocketClient = null;

                    isConnected = false;
                    mCallback.onError(e);
                }

            };

            mWebSocketClient.connect();
        } catch (Exception e) {

            GCLog.e(" mWebSocketClient " + e.toString());
        }
    }

    public boolean isConnected() {
        GCLog.i(" isConnected()  isConnected" + isConnected);
        GCLog.i(" isConnected()  mWebSocketClient" + mWebSocketClient);


        return isConnected && mWebSocketClient != null;
    }

    public void sendMessage(JSONObject mainJsonObject, boolean isChatMessage) {
        try {
            if (Utility.isNetworkAvailable(ApplicationController.getInstance()) && isConnected()) {
                // CommanMethods.getInstance().WriteSdCard(" Dealer Chat Request : "+mainJsonObject.toString());
                GCLog.e(" chat sendMessage " + mainJsonObject.toString());

                mWebSocketClient.send(mainJsonObject.toString());
                mCallback.updateMessageOnUI(true, isChatMessage);
            } else {
                mCallback.updateMessageOnUI(false, isChatMessage);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            mCallback.updateMessageOnUI(false, isChatMessage);

        }
    }


    public void closeSocketConnection() {
        try {
            if (mWebSocketClient != null) {
                this.mWebSocketClient.close();
                mWebSocketClient = null;
                isConnected = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void onConnectivityStatusChanged(Context context) {
        boolean temp = Utility.isNetworkAvailable(context);
        if (mCallback != null) {
            if (temp) {
                connectWebSocket();
            } else {
                isConnected = false;
                mWebSocketClient = null;
            }
            mCallback.onConnectivityStatusChanged(temp);
        }
    }

    public void setSocketNull() {
        isConnected = false;
        mWebSocketClient = null;
    }
}
