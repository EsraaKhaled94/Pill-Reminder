package com.esraakhaled.apps.pillreminder.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.esraakhaled.apps.pillreminder.model.Medicine;

import java.util.List;

@Dao
public interface MedicationDao {

    @Query("SELECT * FROM medicine")
    LiveData<List<Medicine>> getAllMedicine();

    @Query("SELECT * FROM medicine")
    List<Medicine> listAllMedicine();

    @Query("SELECT * FROM medicine WHERE id = :itemId")
    Medicine getMedicineById(long itemId);

    @Insert
    long addMedicine(Medicine medicine);

    @Update
    void updateMedicine(Medicine medicines);

    @Delete
    void removeMedicine(Medicine medicine);
}
