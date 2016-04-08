package com.gcloud.gaadi.chat.model;

import com.gcloud.gaadi.chat.parser.DealerChatHistoryParser;

public class DealerChatModel extends Model {

	/*private String lastchattime ,message,type, offerprice, conversationid, price, chatsessionid,
            imageurl, modelyear, variantId, km,messagesendtime,messageId;*/

    //private SenderModel sender;

    DealerChatHistoryParser dealerChat;

    public DealerChatHistoryParser getChatItem() {
        return dealerChat;
    }

    public void setChatItem(DealerChatHistoryParser dealerChat) {
        this.dealerChat = dealerChat;
    }
}
