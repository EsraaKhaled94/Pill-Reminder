package com.esraakhaled.apps.pillreminder.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {
    private final static String fullDateFormat = "dd-MM-yyyy HH:mm:ss";
    private final static String dateFormat = "dd-MM-yyyy";
    private final static String hours12Format = "hh:mm aa";
    public final static int HOURS_PER_DAY = 24;

    private static Date getDate(String sDate) {
        SimpleDateFormat df = new SimpleDateFormat(fullDateFormat);
        Date date = null;
        try {
            date = df.parse(sDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static String getHoursAMPM(String date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(hours12Format);
        return simpleDateFormat.format(getDate(date));
    }

    public static String getHoursAMPM(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(hours12Format);
        return simpleDateFormat.format(date);
    }

    public static long getDateTime(String date) {
        return getDate(date).getTime();
    }

    public static Date getNotificationTime(String sDate, int hours) {
        Date date = getDate(sDate);
        Date notificationDate = new Date();
        notificationDate.setTime(date.getTime() + (hours * 60 * 60 * 1000));
        return notificationDate;
    }

    public static Date getNotificationTime(Date date, int hours) {
        Date notificationDate = new Date();
        notificationDate.setTime(date.getTime() + (hours * 60 * 60 * 1000));

        return notificationDate;
    }

    public static boolean isAfterNow(Date date) {
        Date nowDate = new Date();
        return date.after(nowDate);
    }

    public static String getDateFormatted(long time) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(fullDateFormat);
        return simpleDateFormat.format(new Date(time));
    }

    public static String getTodayDate() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
        return simpleDateFormat.format(new Date());
    }
}
