package com.esraakhaled.apps.pillreminder.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.util.Log;

import com.esraakhaled.apps.pillreminder.model.Medicine;

@Database(entities = {Medicine.class}, version = 1, exportSchema = false)
public abstract class MedicationDatabase extends RoomDatabase {

    private static final String LOG_TAG = MedicationDatabase.class.getSimpleName();
    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = "medicationsList";
    private static MedicationDatabase mInstance;

    public static MedicationDatabase getInstance(Context context) {
        if (mInstance == null) {
            synchronized (LOCK) {
                Log.d(LOG_TAG, "creating a new database instance");
                mInstance = Room.databaseBuilder(context.getApplicationContext(),
                        MedicationDatabase.class, DATABASE_NAME)
                        .build();
            }
        }
        Log.d(LOG_TAG, "getting the database instance");
        return mInstance;
    }

    public abstract MedicationDao medicationDao();
}
