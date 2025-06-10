package com.epicare;

import static com.epicare.utils.MyConstants.PREF_NAME_GLOBAL;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    Button signin;
    EditText temail;
    EditText tpass;
    TextView treg;
    TextView forgot;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    private SharedPreferences sharedPreferences;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
        sharedPreferences = getSharedPreferences(PREF_NAME_GLOBAL, MODE_PRIVATE);
        signin.setOnClickListener(this);
        treg.setOnClickListener(this);
        findViewById(R.id.lblskip).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });
    }

    private void init() {
        temail = (EditText) findViewById(R.id.editTextEmail);
        tpass = (EditText) findViewById(R.id.editTextPassword);
        signin = (Button) findViewById(R.id.buttonSignin);
        treg = (TextView) findViewById(R.id.textViewSignup);
        forgot = (TextView) findViewById(R.id.forgotpassword);
        progressDialog = new ProgressDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();


        if (user != null && user.isEmailVerified()) {
            //profile activity here
            finish();
            startActivity(new Intent(LoginActivity.this, MainActivity.class));

        }
        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = temail.getText().toString().trim();
                if (TextUtils.isEmpty(email) || Patterns.EMAIL_ADDRESS.matcher(email).matches() == false) {
                    //Email is empty
                    //Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show();
                    temail.setError("Email address is not valid");
                    return;
                }
                firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Recovery email sent to your inbox", Toast.LENGTH_SHORT).show();
                        } else
                            Toast.makeText(LoginActivity.this, "Account does not exists. Please register", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });


    }

    @Override
    public void onClick(View v) {
        if (v == signin) {
            userlogin();
        }
        if (v == treg) {

            startActivity(new Intent(getApplicationContext(), RegisterActivity.class));

        }
    }

    private void userlogin() {
        String email = temail.getText().toString().trim();
        String pass = tpass.getText().toString().trim();
        if (TextUtils.isEmpty(email) || Patterns.EMAIL_ADDRESS.matcher(email).matches() == false) {
            //Email is empty
            //Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show();
            temail.setError("Email address is not valid");
            return;
        }
        if (TextUtils.isEmpty(pass) || pass.length() < 6) {
            //password is empty
            //Toast.makeText(this, "Please enter your password", Toast.LENGTH_SHORT).show();
            tpass.setError("Password must at least 6 digits long");
            return;
        }
        progressDialog.setMessage("Signing In User....");
        progressDialog.show();
        firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressDialog.dismiss();
                if (task.isSuccessful()) {
                    //start the profile activity

                    checkifemailverified();

                } else {
                    temail.setError("Email or password is wrong");
                    //tpass.setError("Email or password is wrong");

                    //Toast.makeText(signinactivity.this, "Account with given details not exists", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void checkifemailverified() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user.isEmailVerified())//|| user.getEmail().equals(KEY_EMAIL)
        {
            Toast.makeText(this, "Successfully signed in", Toast.LENGTH_SHORT).show();
            fetchUserProfile();

        } else {
            FirebaseAuth.getInstance().signOut();
            temail.setError("Email not verified");
        }

    }

    private void fetchUserProfile() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference userRef = db.collection("users").document(userId);
            userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
                            try {
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("gender", document.getString("gender"));
                                editor.putString("emp_id", document.getString("emp_id"));
                                editor.putString("name", document.getString("name"));
                                editor.putString("userType", document.getString("userType"));
                                editor.putString("status", document.getString("status"));
                                editor.putBoolean("isLoggedIn", true);
                                editor.apply();

                            } catch (Exception ex) {
                                Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                            if (Objects.requireNonNull(document.getString("userType")).equals("Doctor")) {
                                startActivity(new Intent(LoginActivity.this, DoctorDashboard.class));
                                finish();
                            } else if (Objects.requireNonNull(document.getString("userType")).equals("Patient")) { // Removed extra `)`
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                finish();
                            } else {
                                Toast.makeText(getApplicationContext(), "Failed to fetch user profile userType value", Toast.LENGTH_SHORT).show();

                            }
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Failed to fetch user profile", Toast.LENGTH_SHORT).show();

                    }
                }
            });
        }
    }
}
