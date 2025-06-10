package com.epicare;
import android.os.Bundle;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SeizureDetailsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seizure_details);

        TextView tvPatientName = findViewById(R.id.tvPatientName);
        TextView tvDateTime = findViewById(R.id.tvDateTime);

        String name = getIntent().getStringExtra("PATIENT_NAME");
        String dateTime = getIntent().getStringExtra("DATETIME");

        tvPatientName.setText(name);
        tvDateTime.setText(dateTime);
    }
}
