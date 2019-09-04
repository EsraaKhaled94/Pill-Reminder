package com.esraakhaled.apps.pillreminder.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.esraakhaled.apps.pillreminder.services.NotificationServiceReceiver;

import java.util.Calendar;
import java.util.Date;

import static android.content.Context.ALARM_SERVICE;

public class NotificationsUtil {
    public static void addNotification(Context context, String name, long id, Date notification) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        Intent alarmIntent = new Intent(context, NotificationServiceReceiver.class);

        alarmIntent.putExtra(NotificationServiceReceiver.NAME, name);
        alarmIntent.putExtra(NotificationServiceReceiver.ID, id);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                0, alarmIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        alarmManager.cancel(pendingIntent);
        pendingIntent = PendingIntent.getBroadcast(context,
                0, alarmIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Calendar notificationTime = Calendar.getInstance();
        notificationTime.setTime(notification);
        Calendar alarmStartTime = Calendar.getInstance();
        /*Calendar now = Calendar.getInstance();*/
        alarmStartTime.set(Calendar.HOUR_OF_DAY, notificationTime.get(Calendar.HOUR_OF_DAY));
        alarmStartTime.set(Calendar.MINUTE, notificationTime.get(Calendar.MINUTE));
        alarmStartTime.set(Calendar.SECOND, notificationTime.get(Calendar.SECOND));

        if (android.os.Build.VERSION.SDK_INT > 23) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, notificationTime.getTimeInMillis(), pendingIntent);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, notificationTime.getTimeInMillis(), pendingIntent);
        }

        Log.d("Alarm", "Added a notification.");
    }

}
