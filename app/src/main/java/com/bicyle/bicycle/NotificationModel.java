package com.bicyle.bicycle;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class NotificationModel {

    private static final String CHANNEL_ID = "channel_id";
    private final static String TAG = "NotificationModel";
    private final Context mContext;

    public NotificationModel(Context context) {
        mContext = context;
        init();
    }

    private void init() {

        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "My Channel", importance);
        channel.setDescription("Reminders");
        NotificationManager mNotificationManager =
                (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.createNotificationChannel(channel);

    }

    public void createNotification(String title, String message, String senderId) {
        Log.d(TAG, "createNotification");

        Intent intent = new Intent(mContext, MakeFriend.class);
        intent.putExtra("myNick", message);
        intent.putExtra("senderUid", senderId);
        intent.putExtra("frdNick", title);
        int requestID = (int) System.currentTimeMillis();
        int flags = PendingIntent.FLAG_CANCEL_CURRENT;
        PendingIntent pIntent = PendingIntent.getActivity(mContext, requestID, intent, flags);


        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(mContext, CHANNEL_ID)
                        .setContentTitle(title)
//                        .addAction(R.drawable.fui_ic_github_white_24dp, "Yes", pIntent)
//                        .addAction(R.drawable.fui_ic_github_white_24dp, "No", pIntent)
                        .setSmallIcon(R.drawable.fui_ic_github_white_24dp)
                        .setAutoCancel(true)
                        .setContentText(message)
                        .setContentIntent(pIntent);
        mBuilder.build().flags |= Notification.FLAG_AUTO_CANCEL;
        NotificationManager mNotificationManager =
                (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(0, mBuilder.build());
    }

}