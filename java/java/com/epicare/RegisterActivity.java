package com.epicare;

import static com.epicare.utils.Validation.isValidEmail;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    Button buttonRegister;
    EditText editTextEmail, editTextEmpId, editTextPassword, editTextName;
    TextView textViewSignin;
    ProgressDialog progressDialog;
    FirebaseAuth firebaseAuth;
    FirebaseAuth.AuthStateListener mAuth;
    Spinner spinnerUserType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        init();
        mAuth = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                FirebaseAuth.getInstance().signOut();
                                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                finish();
                            } else {
                                overridePendingTransition(0, 0);
                                finish();
                                overridePendingTransition(0, 0);
                                startActivity(getIntent());
                            }
                        }
                    });
                } else {

                }
            }
        };
        buttonRegister.setOnClickListener(this);
        textViewSignin.setOnClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(mAuth);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (firebaseAuth != null && mAuth != null) {
            firebaseAuth.removeAuthStateListener(mAuth);
        }

    }

    private void init() {
        firebaseAuth = FirebaseAuth.getInstance();
        editTextEmpId = findViewById(R.id.editTextEmpId);
        editTextName = findViewById(R.id.editTextName);
        buttonRegister = findViewById(R.id.buttonRegister);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        textViewSignin = findViewById(R.id.textViewSignin);
        spinnerUserType = findViewById(R.id.spinnerUserType);
        progressDialog = new ProgressDialog(this);
        if (firebaseAuth.getCurrentUser() != null) {
            //profile activity here
            finish();
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            return;
        }
    }

    @Override
    public void onClick(View v) {
        if (v == buttonRegister) {
            registerUser();
        }
        if (v == textViewSignin) {
            signin();
        }
    }

    private void signin() {
        finish();
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
    }

    private void registerUser() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String emp_id = editTextEmpId.getText().toString().trim();
        String emp_name = editTextName.getText().toString().trim();
        String userType = spinnerUserType.getSelectedItem().toString();

        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches() || TextUtils.isEmpty(emp_name)) {
            editTextEmail.setError("Email address is not valid");
            return;
        }
        if (!isValidEmail(email)) {
            editTextEmail.setError("Email address is not valid");
            return;
        }
        if (TextUtils.isEmpty(password) || password.length() < 6) {
            editTextPassword.setError("Password must be at least 6 digits long");
            return;
        }
        if (TextUtils.isEmpty(emp_id) || emp_id.length() < 4 || emp_id.length() > 12) {
            editTextEmpId.setError("Patient ID must be between 4 and 12 digits");
            return;
        }

        progressDialog.setMessage("Registering User....");
        progressDialog.show();
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressDialog.dismiss();
                if (task.isSuccessful()) {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user != null) {
                        String userId = user.getUid();
                        String name = editTextName.getText().toString().trim();
                        String userType = spinnerUserType.getSelectedItem().toString(); // replace with the user's actual user type
                        String employeeId = editTextEmpId.getText().toString().trim();

                        Map<String, Object> userData = new HashMap<>();
                        userData.put("name", name);
                        userData.put("userType", userType);
                        userData.put("emp_id", employeeId);
                        userData.put("gender", "NA");
                        userData.put("img_url", "NA");
                        userData.put("status", "Register");

                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        db.collection("users").document(userId).set(userData)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(RegisterActivity.this, "Email verification sent to your inbox", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception ex) {
                                        Toast.makeText(RegisterActivity.this, "Could not register. Please try again\n" + ex.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        Toast.makeText(RegisterActivity.this, "Could not register. Please try again", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(RegisterActivity.this, "Could not register. Please try again", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

}