package com.esraakhaled.apps.pillreminder.utils;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

public class SharedPrefrencesUtil {
    private static final int SHARED_PREF_MODE = MODE_PRIVATE;
    private static final String SHARED_PREF_NAME = "PREF";
    private static final String USER_ID_KEY = "USER_ID";

    private static SharedPreferences getSharedPreference(Context context) {
        return context.getSharedPreferences(SHARED_PREF_NAME, SHARED_PREF_MODE);
    }

    public static void saveUserId(Context context, String userId) {
        SharedPreferences.Editor editor = getSharedPreference(context).edit();
        editor.putString(USER_ID_KEY, userId);
        editor.apply();
    }

    public static String getUserId(Context context) {
        SharedPreferences prefs = getSharedPreference(context);
        String userId = prefs.getString(USER_ID_KEY, null);
        return userId;
    }
}
