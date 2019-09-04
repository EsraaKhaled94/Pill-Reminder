package com.esraakhaled.apps.pillreminder;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.Toolbar;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.esraakhaled.apps.pillreminder.database.AppExecutors;
import com.esraakhaled.apps.pillreminder.database.MedicationDatabase;
import com.esraakhaled.apps.pillreminder.model.Medicine;
import com.esraakhaled.apps.pillreminder.utils.DateUtil;
import com.esraakhaled.apps.pillreminder.utils.NotificationsUtil;
import com.esraakhaled.apps.pillreminder.utils.TextValidationUtil;
import com.esraakhaled.apps.pillreminder.utils.WidgetUpdateNotifierUtil;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MedicineDetailsActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.medicine_type)
    Spinner type;
    @BindView(R.id.medicine_name_input_layout)
    TextInputLayout nameInputLayout;
    @BindView(R.id.medicine_name)
    TextInputEditText name;
    @BindView(R.id.medicine_times_per_day_input_layout)
    TextInputLayout timesInputLayout;
    @BindView(R.id.medicine_times_per_day)
    TextInputEditText times;
    @BindView(R.id.medicine_last_taken_time_input_layout)
    TextInputLayout lastTakenTimeInput;
    @BindView(R.id.medicine_last_taken_time)
    TextInputEditText lastTakenTime;
    @BindView(R.id.finished)
    AppCompatCheckBox finishedCheckBox;
    @BindView(R.id.fab)
    FloatingActionButton fab;

    private Medicine medicine;
    private MedicationDatabase database;
    private String datePrefix;
    private Date nextNotificationTime;


    public static Intent getStartIntent(Context context) {
        return new Intent(context, MedicineDetailsActivity.class);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine_details);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        database = MedicationDatabase.getInstance(this);
        setupArrayAdapter();

        setTitle(R.string.add_new_medicine);
    }

    private void setupArrayAdapter() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.medicine_types));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        type.setAdapter(adapter);
    }

    @OnClick(R.id.medicine_last_taken_time_iv)
    void onLastTakenTimeClicked() {
        times.requestFocus();
        Calendar calendar = Calendar.getInstance();
        datePrefix = DateUtil.getTodayDate();

        TimePickerDialog dialog = TimePickerDialog.newInstance(this::onTimeSet,
                calendar.get(Calendar.HOUR),
                calendar.get(Calendar.MINUTE),
                false);
        dialog.show(getFragmentManager(), "Datepickerdialog");
    }

    @OnClick(R.id.fab)
    void addMedicine() {
        if (isAllInputsValid()) {
            int separatorHours = DateUtil.HOURS_PER_DAY / Integer.parseInt(times.getText().toString());
            nextNotificationTime = DateUtil.getNotificationTime(datePrefix, separatorHours);
            if (DateUtil.isAfterNow(nextNotificationTime)) {
                lastTakenTimeInput.setError(null);
                medicine = new Medicine();
                medicine.setName(name.getText().toString());
                medicine.setTimesPerDay(Integer.parseInt(times.getText().toString()));
                medicine.setLastTakenTime(DateUtil.getDateTime(datePrefix));
                medicine.setFinished(finishedCheckBox.isChecked());
                medicine.setType(type.getSelectedItem().toString());

                AppExecutors.getInstance().diskIO().execute(() -> {
                    long id = database.medicationDao().addMedicine(medicine);
                    runOnUiThread(() -> {
                        NotificationsUtil.addNotification(MedicineDetailsActivity.this, medicine.getName(), id, nextNotificationTime);
                        finish();
                        WidgetUpdateNotifierUtil.notifyWidget(this);
                    });
                });
            } else {
                lastTakenTimeInput.setError(getString(R.string.medicine_time_passed_error));
            }
        }
    }

    private boolean isAllInputsValid() {
        if (TextValidationUtil.isEmptyText(name)) {
            nameInputLayout.setError(getString(R.string.name_cant_be_empty));
            return false;
        } else if (TextValidationUtil.isEmptyText(times)) {
            timesInputLayout.setError(getString(R.string.times_per_day_cant_be_empty));
            return false;
        } else if (TextValidationUtil.isEmptyText(lastTakenTime)) {
            lastTakenTimeInput.setError(getString(R.string.last_taken_time_cant_be_empty));
            return false;
        } else if (type.getSelectedItemPosition() == 0) {
            Toast.makeText(this, getString(R.string.please_select_medicine_type), Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
        datePrefix += " " + hourOfDay + ":" + minute + ":" + second;
        lastTakenTime.setText(DateUtil.getHoursAMPM(datePrefix));
    }
}