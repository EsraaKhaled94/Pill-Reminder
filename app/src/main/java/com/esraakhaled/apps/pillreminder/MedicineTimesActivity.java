package com.esraakhaled.apps.pillreminder;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.esraakhaled.apps.pillreminder.database.AppExecutors;
import com.esraakhaled.apps.pillreminder.database.MedicationDatabase;
import com.esraakhaled.apps.pillreminder.model.Medicine;
import com.esraakhaled.apps.pillreminder.utils.DateUtil;
import com.esraakhaled.apps.pillreminder.utils.MedicineType;
import com.esraakhaled.apps.pillreminder.utils.NotificationsUtil;
import com.esraakhaled.apps.pillreminder.utils.WidgetUpdateNotifierUtil;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MedicineTimesActivity extends AppCompatActivity {

    private static final String EXTRA_MEDICINE_ID = "EXTRA_MEDICINE_ID";
    private static String EXTRA_MEDICINE = "EXTRA_MEDICINE";

    private MedicationDatabase database;
    private Medicine medicine;

    @BindView(R.id.medicine_type_image)
    ImageView medicineType;

    @BindView(R.id.medicine_last_taken_time)
    TextView lastTakenTime;

    @BindView(R.id.medicine_next_taken_time)
    TextView nextTakenTime;

    @BindView(R.id.medicine_name)
    TextView medicineName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine_times);

        ButterKnife.bind(this);

        database = MedicationDatabase.getInstance(this);
        if (getIntent().hasExtra(EXTRA_MEDICINE)) {
            medicine = getIntent().getExtras().getParcelable(EXTRA_MEDICINE);
            fillMedicineData();
        } else if (getIntent().hasExtra(EXTRA_MEDICINE_ID)) {
            long medicineId = getIntent().getExtras().getLong(EXTRA_MEDICINE_ID);
            AppExecutors.getInstance().diskIO().execute(() -> {
                medicine = database.medicationDao().getMedicineById(medicineId);
                runOnUiThread(() -> fillMedicineData());
            });
        } else {
            Toast.makeText(this, getString(R.string.error_receiving_medicine), Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @OnClick(R.id.finished_course_button)
    void onFinishedCourseClick() {
        AppExecutors.getInstance().diskIO().execute(() -> {
            database.medicationDao().removeMedicine(medicine);
            runOnUiThread(() -> {
                Toast.makeText(MedicineTimesActivity.this,
                        getString(R.string.medicine_was_removed), Toast.LENGTH_SHORT).show();
                finish();
                WidgetUpdateNotifierUtil.notifyWidget(this);
            });
        });
    }

    @OnClick(R.id.take_medicine_button)
    void onTakeMedicineClick() {
        Date newDate = new Date();
        medicine.setLastTakenTime(newDate.getTime());
        AppExecutors.getInstance().diskIO().execute(() -> {
            database.medicationDao().updateMedicine(medicine);
            runOnUiThread(() -> {
                fillMedicineData();
                WidgetUpdateNotifierUtil.notifyWidget(this);
                NotificationsUtil.addNotification(MedicineTimesActivity.this, medicine.getName(), medicine.getId(),
                        DateUtil.getNotificationTime(newDate, DateUtil.HOURS_PER_DAY / medicine.getTimesPerDay()));
            });
        });
    }

    private void fillMedicineData() {
        medicineName.setText(medicine.getName());
        medicineType.setImageResource(MedicineType.getMedicineType(medicine).getResource());
        Date lastTakenDate = new Date(medicine.getLastTakenTime());
        lastTakenTime.setText(DateUtil.getHoursAMPM(lastTakenDate));
        Date notificationDate = DateUtil.getNotificationTime(lastTakenDate,
                DateUtil.HOURS_PER_DAY / medicine.getTimesPerDay());
        nextTakenTime.setText(DateUtil.getHoursAMPM(notificationDate));
    }

    public static Intent getStartIntent(Context context, Medicine medicine) {
        Intent intent = new Intent(context, MedicineTimesActivity.class);
        intent.putExtra(EXTRA_MEDICINE, medicine);
        return intent;
    }

    public static Intent getStartIntent(Context context, long medicineId) {
        Intent intent = new Intent(context, MedicineTimesActivity.class);
        intent.putExtra(EXTRA_MEDICINE_ID, medicineId);
        return intent;
    }
}
