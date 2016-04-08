package com.gcloud.gaadi.chat;

public interface WebSocketCallback {

    public void sendMessage(String message, boolean isChatMessage);

    public void parse(String msg);

    public void onClose(String messgae);

    public void onError(Exception e);

    public void onConnect();

    public void onConnectivityStatusChanged(boolean connectivityFlag);

    public void updateMessageOnUI(boolean status, boolean isChatMessage);

}
