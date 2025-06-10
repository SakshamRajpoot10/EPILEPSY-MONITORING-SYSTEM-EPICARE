package com.epicare;
import android.os.Bundle;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class PatientHistoryActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_history);

        TextView tvHistory = findViewById(R.id.tvHistory);
        String patientName = getIntent().getStringExtra("PATIENT_NAME");

        tvHistory.setText("History data for: " + patientName);
    }
}
