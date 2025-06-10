package com.epicare;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.epicare.api.ApiResponse;
import com.epicare.model.DemoData;
import com.epicare.api.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivityDemoData extends AppCompatActivity {

    private Button btnSendDemoData;
    private TextView tvResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo_data);

        // Initialize UI Components
        btnSendDemoData = findViewById(R.id.btnSendDemoData);
        tvResponse = findViewById(R.id.tvResponse);

        // Button Click Listener
        btnSendDemoData.setOnClickListener(v -> sendDemoData());
    }

    private void sendDemoData() {
        DemoData demoData = new DemoData("Demo Title", "This is a sample demo request.", 1);

        RetrofitClient.getApiService(this).sendDemoData(demoData).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse apiResponse = response.body();
                    String result = "Response ID: " + apiResponse.getId() + "\n" +
                            "Title: " + apiResponse.getTitle() + "\n" +
                            "Body: " + apiResponse.getBody();
                    tvResponse.setVisibility(View.VISIBLE);
                    tvResponse.setText(result);
                } else {
                    Toast.makeText(ActivityDemoData.this, "Response failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(ActivityDemoData.this, "API Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                tvResponse.setVisibility(View.VISIBLE);
                tvResponse.setText("API Call Failed!");
            }
        });
    }
}
