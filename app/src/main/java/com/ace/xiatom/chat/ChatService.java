package com.ace.xiatom.chat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

public class ChatService extends Service {
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("Service","onCreated execute");
        Intent intent = new Intent(this, ChatActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this,0,intent,0);
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O) {
            CharSequence name = "C1";
            String description = "desc1";
            String channelId = "channelId1";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel mChannel = new NotificationChannel(channelId, name, importance);
            mChannel.setDescription(description);
            mChannel.enableLights(true);
            mChannel.enableVibration(true);
            NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(mChannel);
            Notification.Builder builder = new Notification.Builder(this, channelId);
            builder.setSmallIcon(R.drawable.ic_launcher_background)
                    .setContentTitle("情侣空间")
                    .setContentText("对话")
                    .setSmallIcon(R.drawable.ic_bigicon)
                    .setWhen(System.currentTimeMillis())
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher_foreground))
                    .setContentIntent(pi);
            Notification notification = builder.build();
            startForeground(1,notification);
            Log.i("msgs","here");
        }

    }

    public ChatService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
