package com.esraakhaled.apps.pillreminder.services;

import android.arch.lifecycle.LifecycleOwner;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.esraakhaled.apps.pillreminder.MedicineTimesActivity;
import com.esraakhaled.apps.pillreminder.R;
import com.esraakhaled.apps.pillreminder.database.MedicationDatabase;
import com.esraakhaled.apps.pillreminder.model.Medicine;
import com.esraakhaled.apps.pillreminder.utils.MedicineType;

import java.util.List;

public class GridWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new GridRemoteViewsFactory(this.getApplicationContext());
    }

    private class GridRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
        private Context mContext;
        private MedicationDatabase database;
        private List<Medicine> medicines;

        public GridRemoteViewsFactory(Context context) {
            mContext = context;
            database = MedicationDatabase.getInstance(context);
        }

        @Override
        public void onCreate() {

        }

        @Override
        public void onDataSetChanged() {
            medicines=database.medicationDao().listAllMedicine();
        }

        @Override
        public void onDestroy() {

        }

        @Override
        public int getCount() {
            return medicines == null ? 0 : medicines.size();
        }

        @Override
        public RemoteViews getViewAt(int position) {
            if (medicines == null || medicines.size() == 0)
                return null;

            Medicine medicine = medicines.get(position);
            RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.item_medicine_widget);
            views.setImageViewResource(R.id.medicine_type_image, MedicineType.getMedicineType(medicine).getResource());
            views.setTextViewText(R.id.medicine_name, medicine.getName());

            Intent fillInIntent = MedicineTimesActivity.getStartIntent(mContext, medicine);
            views.setOnClickFillInIntent(R.id.medicine_type_image, fillInIntent);

            return views;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int position) {
            return medicines.get(position).getId();
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }
    }
}
