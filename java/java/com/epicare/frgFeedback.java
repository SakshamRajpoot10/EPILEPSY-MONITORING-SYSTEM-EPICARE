package com.epicare;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;

public class frgFeedback extends Fragment {
    ProgressDialog progressDialog;
    FirebaseAuth firebaseAuth;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frg_feedback, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Feedback");

        init(view);

        view.findViewById(R.id.btnSendReview).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendReview(view);
            }
        });
    }

    private void sendReview(View view) {
        EditText editTextTitle = view.findViewById(R.id.editTextTitle);
        EditText editTextDesc = view.findViewById(R.id.editTextDesc);

        if (TextUtils.isEmpty(editTextTitle.getText().toString().trim())) {
            editTextTitle.setError("Should be value");
            return;
        }
        if (TextUtils.isEmpty(editTextDesc.getText().toString().trim())) {
            editTextDesc.setError("Description of reviews should be String");
            return;
        }

        HashMap<String, Object> feedbackData = new HashMap<>();
        feedbackData.put("Title", editTextTitle.getText().toString().trim());
        feedbackData.put("Review", editTextDesc.getText().toString().trim());
        feedbackData.put("DateTime", Calendar.getInstance().getTime().toString());

        addData(feedbackData, view);
    }

    public void addData(final HashMap<String, Object> object, View view) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String feedbackId = db.collection("feedback").document().getId();

        final ProgressDialog progressDialog = ProgressDialog.show(getContext(), "Saving Data", "Please wait while we save the data...", true);

        DocumentReference feedbackRef = db.collection("feedback").document(feedbackId);

        feedbackRef.set(object)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        ((EditText) view.findViewById(R.id.editTextTitle)).setText("");
                        ((EditText) view.findViewById(R.id.editTextDesc)).setText("");
                        progressDialog.dismiss();

                        new AlertDialog.Builder(getContext())
                                .setTitle("Success")
                                .setMessage("Feedback saved successfully\nThank you")
                                .setPositiveButton("OK", null)
                                .show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        new AlertDialog.Builder(getContext())
                                .setTitle("Error")
                                .setMessage(e.getMessage())
                                .setPositiveButton("OK", null)
                                .show();
                        Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void init(View view) {
        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(getContext());
        if (firebaseAuth.getCurrentUser() == null) {
            getActivity().finish();
            startActivity(new Intent(getContext(), LoginActivity.class));
        }
    }
}