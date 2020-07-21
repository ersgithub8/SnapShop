package com.fyp.snapshop.notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.fyp.snapshop.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    SharedPreferences.Editor editor;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        sendNotification(remoteMessage.getNotification());
    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.e("FirebaseToken : ", s);

        SharedPreferences prefs = getSharedPreferences("firebase_token", Context.MODE_PRIVATE);
        editor = prefs.edit();
        editor.putString("token", s);
        editor.apply();

        Log.e("TokenOne", prefs.getString("token",""));


    }

    private void sendNotification(RemoteMessage.Notification notification) {
        String CHANNEL_ID = "my_channel_02";// The id of the channel.
        NotificationChannel mChannel = null;
        CharSequence name = "PlanBeauty Notifications";// The user-visible name of the channel.
        int importance = NotificationManager.IMPORTANCE_HIGH;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
        }


        long[] pattern = {500, 500, 500, 500, 500, 500, 500, 500, 500};

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.snapshop_logo)
                .setContentTitle(notification.getTitle())
                .setContentText(notification.getBody())
                .setAutoCancel(true)
                .setLights(Color.BLUE, 500, 500)
                .setVibrate(pattern)
                .setSound(defaultSoundUri)
                .setChannelId(CHANNEL_ID);

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mNotificationManager.createNotificationChannel(mChannel);
        }

        mNotificationManager.notify(0, notificationBuilder.build());
    }
}
