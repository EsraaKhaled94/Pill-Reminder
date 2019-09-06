package com.esraakhaled.apps.pillreminder.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.esraakhaled.apps.pillreminder.MainActivity;
import com.esraakhaled.apps.pillreminder.MedicineTimesActivity;
import com.esraakhaled.apps.pillreminder.R;
import com.esraakhaled.apps.pillreminder.utils.NotificationID;

public class NotificationServiceReceiver extends BroadcastReceiver {
    public static final String ID = "ID";
    public static String NAME = "NAME";
    private String CHANNEL_ID = "channel";

    private void createNotificationChannel(Context context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = context.getString(R.string.channel_name);
            String description = context.getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setShowBadge(true);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        createNotificationChannel(context);
//todo
        String medicineName = "";
        long medicineId = 0;
        if (intent.hasExtra(NAME)) {
            medicineName = intent.getExtras().getString(NAME);
        }
        if (intent.hasExtra(ID)) {
            medicineId = intent.getExtras().getLong(ID);
        }

        Intent actionIntent = new Intent(context, MainActivity.class);
        actionIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, actionIntent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID);

        Notification notification = builder.setContentTitle(context.getString(R.string.app_name))
                .setContentText(context.getString(R.string.time_to_take, medicineName))
                .setTicker("New Message Alert!")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .build();


        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(NotificationID.getID(), notification);
    }
}
