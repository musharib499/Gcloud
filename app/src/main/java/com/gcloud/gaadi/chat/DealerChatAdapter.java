package com.gcloud.gaadi.chat;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gcloud.gaadi.R;
import com.gcloud.gaadi.chat.parser.DealerChatHistoryParser;

import java.util.LinkedHashMap;
import java.util.Vector;

public class DealerChatAdapter extends BaseAdapter {
    Context context;
    LayoutInflater inflater;
    private Vector<DealerChatHistoryParser> chatMessageList = new Vector<DealerChatHistoryParser>();
    private LinkedHashMap<Long, DealerChatHistoryParser> chatMessageMap = new LinkedHashMap<Long, DealerChatHistoryParser>();

    public DealerChatAdapter(Context paramContext) {
        this.context = paramContext;
        this.inflater = ((LayoutInflater) this.context
                .getSystemService("layout_inflater"));
    }

    public void add(DealerChatHistoryParser paramChatMessageParser) {
        Log.e(" add ", "chatMessageList size  " + chatMessageList.size());

        if (chatMessageList == null)
            chatMessageList = new Vector<DealerChatHistoryParser>();

        this.chatMessageList.add(paramChatMessageParser);
        chatMessageMap.put(paramChatMessageParser.getMessagesendtime(), paramChatMessageParser);
        notifyDataSetChanged();

    }

    public void addAll(/*Vector<DealerChatHistoryParser> paramVector*/LinkedHashMap<Long, DealerChatHistoryParser> localVector) {
//		Log.e(" addAll ", "paramVector " +paramVector.size());
        Log.e(" addAll ", "chatMessageList size  " + chatMessageList.size());
        Log.e(" addAll ", "chatMessageList   " + chatMessageList);
        LinkedHashMap<Long, DealerChatHistoryParser> newMap = new LinkedHashMap<Long, DealerChatHistoryParser>(localVector);
        newMap.putAll(chatMessageMap);
//		chatMessageMap.putAll(localVector);
//		chatMessageMap = newMap;
        chatMessageMap = new LinkedHashMap<Long, DealerChatHistoryParser>(newMap);
        newMap.clear();
        if (chatMessageList == null)
            chatMessageList = new Vector<DealerChatHistoryParser>();

        chatMessageList.addAll(0, new Vector<DealerChatHistoryParser>(localVector.values()));
        notifyDataSetChanged();
    }

    public void setServerReadRecipt(long key, String conversationId) {
        //check for conversation id
        if (chatMessageMap.containsKey(key)) {
            chatMessageMap.get(key).setIsMessageDelivered(1);
            chatMessageList = new Vector<DealerChatHistoryParser>(chatMessageMap.values());
            notifyDataSetChanged();
        }
    }

    public void setEndUserReadRecipt(long key, String conversationId) {
        if (chatMessageMap.containsKey(key)) {
            chatMessageMap.get(key).setIsMessageDelivered(2);
            chatMessageList = new Vector<DealerChatHistoryParser>(chatMessageMap.values());
            notifyDataSetChanged();
        }
    }

    public int getCount() {
        return this.chatMessageList.size();
    }

    public DealerChatHistoryParser getItem(int paramInt) {
        return (DealerChatHistoryParser) this.chatMessageList.get(paramInt);
    }

    public long getItemId(int paramInt) {
        return paramInt;
    }

    public int getItemViewType(int paramInt) {
        if (chatMessageList.get(paramInt).isSentMessage()) {
            return 1;
        }
        return 0;
    }

    public View getView(int paramInt, View paramView, ViewGroup paramViewGroup) {
        int i = getItemViewType(paramInt);
        //Log.e("getView " + paramInt, " " + paramView + " type = " + i);
        DealerChatHistoryParser localChatMessageParser = getItem(paramInt);
        ViewHolder localViewHolder;
        if (paramView == null) {
            localViewHolder = new ViewHolder();
            switch (i) {
                case 0:
                    paramView = this.inflater.inflate(R.layout.consumer_chat_support_dealer_row, null);
                    localViewHolder.expertOrDealerContainer = ((RelativeLayout) paramView
                            .findViewById(R.id.singleMessageContainerSupport));
                    localViewHolder.expertOrDealerTextView = ((TextView) paramView
                            .findViewById(R.id.expertOrDealerTextView));
                    localViewHolder.expertOrDealerSupportNameTextView = ((TextView) paramView
                            .findViewById(R.id.expertOrDealerSupportNameTextView));
                    localViewHolder.expertOrDealerSupportDateTimeNameTextView = ((TextView) paramView
                            .findViewById(R.id.expertOrDealerSupportDateTimeNameTextView));
                    localViewHolder.expertOrDealerMessageTextView = ((TextView) paramView
                            .findViewById(R.id.expertOrDealerMessageTextView));
                    localViewHolder.expertOrDealerTextView.setText("User - ");

                    break;
                case 1:
                    paramView = this.inflater.inflate(R.layout.consumer_chat_user_row, null);
                    localViewHolder.expertOrDealerContainer = ((RelativeLayout) paramView
                            .findViewById(R.id.singleMessageContainerConsumer));
                    localViewHolder.expertOrDealerTextView = ((TextView) paramView
                            .findViewById(R.id.consumerTextView));
                    localViewHolder.expertOrDealerSupportNameTextView = ((TextView) paramView
                            .findViewById(R.id.consumerNameTextView));
                    localViewHolder.expertOrDealerSupportDateTimeNameTextView = ((TextView) paramView
                            .findViewById(R.id.consumerDateTimeNameTextView));
                    localViewHolder.expertOrDealerMessageTextView = ((TextView) paramView
                            .findViewById(R.id.consumerMessageTextView));
                    localViewHolder.imageViewTic = ((ImageView) paramView
                            .findViewById(R.id.imageViewTic));
                    break;
            }
            paramView.setTag(localViewHolder);

        } else
            localViewHolder = (ViewHolder) paramView.getTag();

        Log.e("getView", "getMessage " + localChatMessageParser.getMessage());

        localViewHolder.expertOrDealerMessageTextView.setText(localChatMessageParser.getMessage());

        localViewHolder.expertOrDealerSupportDateTimeNameTextView.setVisibility(View.VISIBLE);

        if (localChatMessageParser.getTime() == null || localChatMessageParser.getTime().isEmpty())
            localViewHolder.expertOrDealerSupportDateTimeNameTextView.setText(CommanMethods.getTime12(System.currentTimeMillis()));
        else
            localViewHolder.expertOrDealerSupportDateTimeNameTextView.setText(localChatMessageParser.getTime());

        if (i == 0)
            localViewHolder.expertOrDealerSupportNameTextView.setText(localChatMessageParser.getSender().getName());
        else {
            localViewHolder.expertOrDealerSupportNameTextView.setText("");
            if (localViewHolder.imageViewTic != null)
                localViewHolder.imageViewTic.setVisibility(View.VISIBLE);
            localViewHolder.expertOrDealerTextView.setText("me");
            if (localViewHolder.imageViewTic != null && localChatMessageParser.getMessage().indexOf("Type your message below to start chatting with the buyer") != -1) {
                Log.e("getView", " Inside Welcome message ");
                localViewHolder.expertOrDealerTextView.setText("");
                localViewHolder.imageViewTic.setVisibility(View.INVISIBLE);
            } else if (localViewHolder.imageViewTic != null) {
                localViewHolder.imageViewTic.setVisibility(View.VISIBLE);
                if (localChatMessageParser.getIsMessageDelivered() == 0)
                    localViewHolder.imageViewTic.setImageResource(R.drawable.chat_processing);
                else if (localChatMessageParser.getIsMessageDelivered() == 1)
                    localViewHolder.imageViewTic.setImageResource(R.drawable.chat_check);
                else if (localChatMessageParser.getIsMessageDelivered() == 2)
                    localViewHolder.imageViewTic.setImageResource(R.drawable.double_check);
            }

        }


        //Log.e("getView", "Message " + localChatMessageParser.getMessage());
        //Log.e("getView", "Name " + localChatMessageParser.getName());
        //Log.e("getView", "Time " + localChatMessageParser.getTime());


        return paramView;

    }

    public int getViewTypeCount() {
        return 2;
    }

    public Vector<DealerChatHistoryParser> getList() {
        return chatMessageList;
    }

    public void setList(Vector<DealerChatHistoryParser> list) {
        chatMessageList = list;
        notifyDataSetChanged();
    }

    private static class ViewHolder {
        private RelativeLayout expertOrDealerContainer;
        private TextView expertOrDealerMessageTextView;
        private TextView expertOrDealerSupportDateTimeNameTextView;
        private TextView expertOrDealerSupportNameTextView;
        private TextView expertOrDealerTextView;
        private ImageView imageViewTic;
    }
}
