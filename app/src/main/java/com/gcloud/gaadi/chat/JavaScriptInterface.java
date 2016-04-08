package com.gcloud.gaadi.chat;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.gcloud.gaadi.R;
import com.gcloud.gaadi.chat.parser.DealerChatHistoryParser;

public class JavaScriptInterface {

    private Context context;

    public JavaScriptInterface(Context con) {
        this.context = con;
    }

    public void displayNotification(String message, DealerChatHistoryParser localChatMessageParser1) {
        String title = "You have received a message";
        Log.i("Start", "notification");
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.chat_notification_icon)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true);
        mBuilder.setSound(uri);


        Intent intent1 = new Intent(context, DealerChatActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("conversationid", localChatMessageParser1.getConversationid());
        bundle.putString("chat_with", "dealer");
        bundle.putString("variantId", localChatMessageParser1.getVariantId());
        bundle.putString("km", localChatMessageParser1.getKm());
        bundle.putString("modelyear", localChatMessageParser1.getModelyear());

        bundle.putString("offerprice", localChatMessageParser1.getOfferprice());
        bundle.putString("userchatid", localChatMessageParser1.getSender().getUserchatid());
        bundle.putString("price", localChatMessageParser1.getPrice());
        bundle.putString("name", message);
        bundle.putString("chatsessionid", localChatMessageParser1.getChatsessionid());
        bundle.putString("imageurl", localChatMessageParser1.getImageurl());
        intent1.putExtras(bundle);
        intent1.setAction("com.girnar.cardekho.activity" + System.currentTimeMillis());

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                intent1, PendingIntent.FLAG_UPDATE_CURRENT);
        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        mBuilder.setContentIntent(pendingIntent);


        NotificationManager nm = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(localChatMessageParser1.getConversationid(), 100, mBuilder.build());

    }

}
