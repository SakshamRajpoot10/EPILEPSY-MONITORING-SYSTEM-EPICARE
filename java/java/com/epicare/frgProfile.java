package com.epicare;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.yalantis.ucrop.UCrop;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class frgProfile extends Fragment {
    private static final int PICK_IMAGE_REQUEST = 1;
    FirebaseAuth firebaseAuth;
    Button btnUpdateProfile;
    Spinner spinnerGender;
    private CircleImageView profileImage;
    private Uri imageUri;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frg_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Profile");

        // Initialize Views
        profileImage = view.findViewById(R.id.profileImage);
        TextView tvChangePhoto = view.findViewById(R.id.tvChangePhoto);
        btnUpdateProfile = view.findViewById(R.id.btnUpdateProfile);
        spinnerGender = view.findViewById(R.id.spinnerGender);
        firebaseAuth = FirebaseAuth.getInstance();

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getContext(),
                R.array.gender_array,
                android.R.layout.simple_spinner_dropdown_item
        );
        spinnerGender.setAdapter(adapter);

        try {
            ((TextView) view.findViewById(R.id.txtEmail)).setText(firebaseAuth.getCurrentUser().getEmail());
            fetchUserProfile(view);
        } catch (Exception ex) {
            Toast.makeText(getContext(), ex.toString(), Toast.LENGTH_SHORT).show();
        }

        // Change Profile Picture
        tvChangePhoto.setOnClickListener(v -> openFileChooser());

        // Update Profile
        btnUpdateProfile.setOnClickListener(v -> {
            String name = ((TextView) view.findViewById(R.id.txtName)).getText().toString().trim();
            String gender = spinnerGender.getSelectedItem().toString();

            if (!name.isEmpty() && !gender.isEmpty()) {
                updateUserProfile(name, gender);
            } else {
                Toast.makeText(getContext(), "Please enter all details", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            Uri sourceUri = data.getData();
            Uri destinationUri = Uri.fromFile(new File(getActivity().getCacheDir(), "cropped_image.jpg"));

            // Start UCrop Activity
            UCrop.of(sourceUri, destinationUri)
                    .withAspectRatio(1, 1) // Square Crop
                    .withMaxResultSize(200, 200)
                    .start(getContext(), this);
        }

        // Handle UCrop result
        if (requestCode == UCrop.REQUEST_CROP && resultCode == Activity.RESULT_OK) {
            Uri resultUri = UCrop.getOutput(data);
            if (resultUri != null) {
                imageUri = resultUri;
                Picasso.get().load(imageUri).into(profileImage); // Set Cropped Image to UI
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            Toast.makeText(getContext(), "Cropping failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchUserProfile(View view) {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference userRef = db.collection("users").document(userId);
            userRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        try {
                            ((TextView) view.findViewById(R.id.txtUserType)).setText(document.getString("userType"));
                            ((TextView) view.findViewById(R.id.txtName)).setText(document.getString("name"));
                            ((TextView) view.findViewById(R.id.txtUserStatus)).setText(document.getString("status"));
                            ((TextView) view.findViewById(R.id.editTextEmpId)).setText(document.getString("emp_id"));

                            // Set Gender in Spinner
                            String gender = document.getString("gender");
                            if (gender != null) {
                                ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) spinnerGender.getAdapter();
                                int genderPosition = adapter.getPosition(gender);
                                spinnerGender.setSelection(genderPosition);
                            }

                            // Load Profile Image if available
                            String imageUrl = document.getString("profileImageUrl");
                            if (imageUrl != null && !imageUrl.isEmpty()) {
                                Picasso.get().load(imageUrl).into(profileImage);
                            }
                        } catch (Exception ex) {
                            Toast.makeText(getContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(getContext(), "Failed to fetch user profile", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void updateUserProfile(String name, String gender) {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference userRef = db.collection("users").document(userId);
            Map<String, Object> updates = new HashMap<>();
            updates.put("gender", gender);
            updates.put("name", name);

            userRef.update(updates)
                    .addOnSuccessListener(aVoid ->
                            Toast.makeText(getContext(), "User profile Name & Gender updated successfully", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e ->
                            Toast.makeText(getContext(), "Failed to update user profile", Toast.LENGTH_SHORT).show());
        }
    }
}
