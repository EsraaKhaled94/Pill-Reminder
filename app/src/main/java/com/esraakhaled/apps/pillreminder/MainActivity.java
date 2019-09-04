package com.esraakhaled.apps.pillreminder;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.esraakhaled.apps.pillreminder.adapter.MedicineAdapter;
import com.esraakhaled.apps.pillreminder.database.MainActivityViewModel;
import com.esraakhaled.apps.pillreminder.listeners.OnMedicineSelectedListener;
import com.esraakhaled.apps.pillreminder.model.Medicine;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements OnMedicineSelectedListener {

    @BindView(R.id.rv_medications)
    RecyclerView medicationsList;

    @BindView(R.id.no_data)
    TextView noData;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private List<Medicine> medicineList;
    private MedicineAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setRecyclerView();
        setSupportActionBar(toolbar);
        setTitle(R.string.my_medications);
    }

    private void setRecyclerView() {
        medicationsList.setLayoutManager(new GridLayoutManager(this, 3));
        adapter = new MedicineAdapter(this);
        medicationsList.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MainActivityViewModel viewModel = ViewModelProviders.of(this).get(MainActivityViewModel.class);
        viewModel.getMedicationListLiveData().observe(this, medicines -> {
            medicineList = medicines;
            if (medicineList == null || medicineList.size() == 0) {
                noData.setVisibility(View.VISIBLE);
            } else {
                noData.setVisibility(View.GONE);
            }

            runOnUiThread(() -> {
                adapter.reset();
                adapter.addItems(medicineList);
            });
        });
    }

    @OnClick(R.id.fab)
    void addNewMedicine() {
        startActivity(MedicineDetailsActivity.getStartIntent(this));
    }

    @Override
    public void onMedicineSelected(Medicine medicine) {
        startActivity(MedicineTimesActivity.getStartIntent(this, medicine));
    }
}
