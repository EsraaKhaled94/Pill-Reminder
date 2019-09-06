package com.esraakhaled.apps.pillreminder.services;

import android.app.IntentService;
import android.content.Intent;
import android.widget.Toast;

import com.esraakhaled.apps.pillreminder.R;
import com.esraakhaled.apps.pillreminder.database.AppExecutors;
import com.esraakhaled.apps.pillreminder.database.MedicationDatabase;
import com.esraakhaled.apps.pillreminder.model.Medicine;
import com.esraakhaled.apps.pillreminder.utils.SharedPrefrencesUtil;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import java.util.List;

public class BackUpMedicineIntentService extends IntentService {
    private MedicationDatabase database;
    private String userId;
    private String medicineString;

    public BackUpMedicineIntentService() {
        super("BackUpMedicineIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        database = MedicationDatabase.getInstance(this);
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference medicine = firebaseDatabase.getReference("medicine");
        userId = SharedPrefrencesUtil.getUserId(this);
        AppExecutors.getInstance().diskIO().execute(() -> {
            List<Medicine> medicineList = database.medicationDao().listAllMedicine();
            medicineString = new Gson().toJson(medicineList);
            addToFireBase(medicine);
        });
    }

    private void addToFireBase(DatabaseReference medicine) {
        medicine.child(userId).setValue(medicineString)
                .addOnCompleteListener(task -> Toast.makeText(BackUpMedicineIntentService.this,
                        getString(R.string.data_synced_successfully), Toast.LENGTH_SHORT).show());
    }
}
