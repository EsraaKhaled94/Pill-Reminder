package com.esraakhaled.apps.pillreminder.utils;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;

import com.esraakhaled.apps.pillreminder.widget.PillsWidget;
import com.esraakhaled.apps.pillreminder.R;

public class WidgetUpdateNotifierUtil {
    public static void notifyWidget(Context context) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, PillsWidget.class));
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_grid_view);
    }
}
