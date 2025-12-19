package com.epicare.adapter;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.epicare.PatientDetailsActivity;
import com.epicare.PatientHistoryActivity;
import com.epicare.R;

import java.util.List;

public class MyPatientAdapter extends RecyclerView.Adapter<MyPatientAdapter.ViewHolder> {
    private List<String> patientList;
    private Context context;

    public MyPatientAdapter(Context context, List<String> patientList) {
        this.context = context;
        this.patientList = patientList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_patient, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String patientName = patientList.get(position);
        holder.tvPatientName.setText(patientName);

        holder.btnView.setOnClickListener(v -> {
            Intent intent = new Intent(context, PatientDetailsActivity.class);
            intent.putExtra("PATIENT_NAME", patientName);
            context.startActivity(intent);
        });

        holder.btnHistData.setOnClickListener(v -> {
            Intent intent = new Intent(context, PatientHistoryActivity.class);
            intent.putExtra("PATIENT_NAME", patientName);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return patientList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvPatientName;
        Button btnView, btnHistData;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPatientName = itemView.findViewById(R.id.tvPatientName);
            btnView = itemView.findViewById(R.id.btnView);
            btnHistData = itemView.findViewById(R.id.btnHistData);
        }
    }
}
