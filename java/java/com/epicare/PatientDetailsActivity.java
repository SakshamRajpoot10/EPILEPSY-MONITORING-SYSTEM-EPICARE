package com.epicare;

import android.os.Bundle;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class PatientDetailsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_details);

        TextView tvDetails = findViewById(R.id.tvDetails);
        String patientName = getIntent().getStringExtra("PATIENT_NAME");

        tvDetails.setText("Details for: " + patientName);
    }
}
