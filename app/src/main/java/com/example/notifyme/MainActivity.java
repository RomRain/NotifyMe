package com.example.notifyme;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button button_notify;
    private Button button_cancel;
    private Button button_update;
    private static final String PRIMARY_CHANNEL_ID = "primary_notification_channel";
    private NotificationManager mNotifyManager;
    private static final int NOTIFICATION_ID = 0;
    private static final String ACTION_UPDATE_NOTIFICATION = "com.example.android.notifyme.ACTION_UPDATE_NOTIFICATION";
    private static final String ACTION_SWIPE_NOTIFICATION = "com.example.android.notifyme.ACTION_SWIPE_NOTIFICATION";
    private NotificationReceiver mReceiver = new NotificationReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button_notify = findViewById(R.id.notify);
        button_cancel = findViewById(R.id.cancel);
        button_update = findViewById(R.id.update);

        setNotificationButtonState(true, false, false);

        button_notify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sendNotification();
            }
        });

        button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                cancelNotification();
            }
        });

        button_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                updateNotification();
            }
        });


        createNotificationChannel();
        registerReceiver(mReceiver, new IntentFilter(ACTION_UPDATE_NOTIFICATION));
        registerReceiver(mReceiver, new IntentFilter(ACTION_SWIPE_NOTIFICATION));
    }

    public void sendNotification() {

        Intent updateIntent = new Intent(ACTION_UPDATE_NOTIFICATION);
        PendingIntent updatePendingIntent = PendingIntent.getBroadcast(this, NOTIFICATION_ID, updateIntent, PendingIntent.FLAG_ONE_SHOT);

        Intent swipeIntent = new Intent(ACTION_SWIPE_NOTIFICATION);
        PendingIntent swipePendingIntent = PendingIntent.getBroadcast(this, NOTIFICATION_ID, swipeIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder notifyBuilder = getNotificationBuilder();
        notifyBuilder.addAction(R.drawable.ic_update, "Update Notification", updatePendingIntent)
                .setDeleteIntent(swipePendingIntent);
        mNotifyManager.notify(NOTIFICATION_ID, notifyBuilder.build());
        setNotificationButtonState(false, true, true);
    }

    public void cancelNotification() {

        mNotifyManager.cancel(NOTIFICATION_ID);
        setNotificationButtonState(true, false, false);
    }

    public void updateNotification() {

        Bitmap androidImage = BitmapFactory.decodeResource(getResources(), R.drawable.mascot_1);

        NotificationCompat.Builder notifyBuilder = getNotificationBuilder();
        notifyBuilder.setStyle(new NotificationCompat.BigPictureStyle()
                .bigPicture(androidImage)
                .setBigContentTitle("Notification Updated"));

        mNotifyManager.notify(NOTIFICATION_ID, notifyBuilder.build());

        setNotificationButtonState(false, false, true);
    }

    public void createNotificationChannel() {

        mNotifyManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel notificationChannel = new NotificationChannel(PRIMARY_CHANNEL_ID, "Mascot Notification", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setDescription("Notification From Mascot");
            mNotifyManager.createNotificationChannel(notificationChannel);
        }
    }

    private NotificationCompat.Builder getNotificationBuilder() {

        Intent notificationIntent = new Intent(this, MainActivity.class);

        PendingIntent notificationPendingIntent = PendingIntent.getActivity(this, NOTIFICATION_ID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent swipeIntent = new Intent(ACTION_SWIPE_NOTIFICATION);
        PendingIntent swipePendingIntent = PendingIntent.getBroadcast(this, NOTIFICATION_ID, swipeIntent, PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder notifyBuilder = new NotificationCompat.Builder(this, PRIMARY_CHANNEL_ID)
                .setContentTitle("You've been notified")
                .setContentText("This is your notification text.")
                .setSmallIcon(R.drawable.ic_android)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setContentIntent(notificationPendingIntent)
                .setDeleteIntent(swipePendingIntent)
                .setAutoCancel(true);

        return notifyBuilder;

    }

    private void setNotificationButtonState(boolean isNotifyEnabled, boolean isUpdateEnabled, boolean isCancelEnabled) {

        button_notify.setEnabled(isNotifyEnabled);
        button_update.setEnabled(isUpdateEnabled);
        button_cancel.setEnabled(isCancelEnabled);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    public class NotificationReceiver extends BroadcastReceiver {

        public NotificationReceiver() {

        }

        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(ACTION_UPDATE_NOTIFICATION)) {

                updateNotification();
            }

            if (intent.getAction().equals(ACTION_SWIPE_NOTIFICATION)) {

                cancelNotification();

                setNotificationButtonState(true, false, false);
            }
        }
    }
}
