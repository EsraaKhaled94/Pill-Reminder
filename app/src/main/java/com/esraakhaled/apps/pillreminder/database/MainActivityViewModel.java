package com.esraakhaled.apps.pillreminder.database;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.esraakhaled.apps.pillreminder.model.Medicine;

import java.util.List;

public class MainActivityViewModel extends AndroidViewModel {

    private LiveData<List<Medicine>> medicationListLiveData;

    public MainActivityViewModel(@NonNull Application application) {
        super(application);
        MedicationDatabase medicationDatabase = MedicationDatabase.getInstance(this.getApplication());
        medicationListLiveData = medicationDatabase.medicationDao().getAllMedicine();
    }

    public LiveData<List<Medicine>> getMedicationListLiveData() {
        return medicationListLiveData;
    }
}
