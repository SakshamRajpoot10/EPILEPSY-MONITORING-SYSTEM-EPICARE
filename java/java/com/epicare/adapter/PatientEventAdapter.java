package com.epicare.adapter;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.epicare.R;
import com.epicare.SeizureDetailsActivity;
import com.epicare.model.Patient;

import java.util.List;

public class PatientEventAdapter extends RecyclerView.Adapter<PatientEventAdapter.ViewHolder> {
    private List<Patient> patientEventList;
    private Context context;

    public PatientEventAdapter(Context context, List<Patient> patientList) {
        this.context = context;
        this.patientEventList = patientList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.patient_event_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Patient patient = patientEventList.get(position);
        holder.tvPatientName.setText(patient.getName());
        holder.tvDateTime.setText(patient.getDateTime());

        // Handle row click
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, SeizureDetailsActivity.class);
            intent.putExtra("PATIENT_NAME", patient.getName());
            intent.putExtra("DATETIME", patient.getDateTime());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return patientEventList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvPatientName, tvDateTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPatientName = itemView.findViewById(R.id.tvPatientName);
            tvDateTime = itemView.findViewById(R.id.tvDateTime);
        }
    }
}

