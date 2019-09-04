package com.esraakhaled.apps.pillreminder.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.esraakhaled.apps.pillreminder.R;
import com.esraakhaled.apps.pillreminder.listeners.OnMedicineSelectedListener;
import com.esraakhaled.apps.pillreminder.model.Medicine;
import com.esraakhaled.apps.pillreminder.utils.MedicineType;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MedicineAdapter extends RecyclerView.Adapter<MedicineAdapter.MedicineViewHolder> {
    private List<Medicine> list;
    private OnMedicineSelectedListener listener;

    public MedicineAdapter(OnMedicineSelectedListener listener) {
        list = new ArrayList<>();
        this.listener = listener;
    }

    @NonNull
    @Override
    public MedicineViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_medicine, viewGroup, false);
        return new MedicineViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MedicineViewHolder holder, int i) {
        holder.onBind(list.get(i));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void reset() {
        list = new ArrayList<>();
        notifyDataSetChanged();
    }

    public void addItems(List<Medicine> medicineList) {
        list = medicineList;
        notifyDataSetChanged();
    }

    class MedicineViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.medicine_type_image)
        ImageView type;
        @BindView(R.id.medicine_name)
        TextView name;
        private Medicine medicine;

        public MedicineViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        public void onBind(Medicine medicine) {
            this.medicine = medicine;
            name.setText(medicine.getName());
            type.setImageResource(MedicineType.getMedicineType(medicine).getResource());
        }

        @Override
        public void onClick(View v) {
            if (listener != null) {
                listener.onMedicineSelected(medicine);
            }
        }
    }
}
