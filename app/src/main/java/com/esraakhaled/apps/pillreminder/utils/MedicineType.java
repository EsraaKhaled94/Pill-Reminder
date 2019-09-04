package com.esraakhaled.apps.pillreminder.utils;

import com.esraakhaled.apps.pillreminder.R;
import com.esraakhaled.apps.pillreminder.model.Medicine;

public enum MedicineType {
    SYRUP("syrup", R.drawable.syrup),
    SYRINGE("syringe", R.drawable.syringe),
    PILL("pill", R.drawable.pills),
    EFFERVESCENT("effervescent", R.drawable.effervescent),
    UNKNOWN("", R.drawable.ic_action_add);

    private String type;
    private int resource;

    MedicineType(String type, int icon) {
        this.type = type;
        this.resource = icon;
    }

    public String getType() {
        return type;
    }

    public int getResource() {
        return resource;
    }

    public static MedicineType getMedicineType(Medicine medicine) {
        for (MedicineType type : MedicineType.values()) {
            if (type.getType().equals(medicine.getType().toLowerCase())) {
                return type;
            }
        }
        return UNKNOWN;
    }
}
