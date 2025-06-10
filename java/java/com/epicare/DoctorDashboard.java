package com.epicare;

import static com.epicare.utils.MyConstants.PREF_NAME_GLOBAL;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.epicare.adapter.PatientEventAdapter;
import com.epicare.model.Patient;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class DoctorDashboard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private SharedPreferences sharedPreferences;
    private Fragment fragment = null;
    FrameLayout frameLayout;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    private TextView txt_name;
    LinearLayout dlayout;
    private RecyclerView recyclerView;
    private PatientEventAdapter adapter;
    private List<Patient> patientList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_doctor_dashboard);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Doctor Dashboard");
        setSupportActionBar(toolbar);
        sharedPreferences = getSharedPreferences(PREF_NAME_GLOBAL, MODE_PRIVATE);
        frameLayout = findViewById(R.id.content_frame);
        dlayout = findViewById(R.id.dashboard_content_dr);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        // do not remove it will be used when live code
        try {
            firebaseAuth = FirebaseAuth.getInstance();
            user = firebaseAuth.getCurrentUser();

            if (user == null) {
                //login checked activity here
                finish();
                startActivity(new Intent(DoctorDashboard.this, LoginActivity.class));

            }

            View mHeaderView;
            mHeaderView = navigationView.getHeaderView(0);
            ((TextView) mHeaderView.findViewById(R.id.nav_header_subtitle)).setText(firebaseAuth.getCurrentUser().getEmail());

        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), (firebaseAuth.getCurrentUser().getEmail() + "\n" + ex.toString()), Toast.LENGTH_SHORT).show();
        }
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Sample data - Replace with your data from Firebase
        patientList = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            patientList.add(new Patient("Patient " + i, "2025-02-28 10:0" + i));
        }

        adapter = new PatientEventAdapter(this, patientList);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
    final void setFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_frame, fragment);
        ft.commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_home) {
            fragment = null;
            frameLayout.setVisibility(View.GONE);
            dlayout.setVisibility(View.VISIBLE);
            setTitle("EpiCare App");

        }   else if (id == R.id.nav_patient_list) {
            dlayout.setVisibility(View.GONE);
            frameLayout.setVisibility(View.VISIBLE);
            setTitle("My Patient List");
            fragment = new frgMyPatient_list();

        }   else if (id == R.id.nav_patient_history) {
            dlayout.setVisibility(View.GONE);
            frameLayout.setVisibility(View.VISIBLE);
            setTitle("My Patient's Data History");
            fragment = new frgNewLead();

        }  else if (id == R.id.nav_profile) {
            dlayout.setVisibility(View.GONE);
            frameLayout.setVisibility(View.VISIBLE);
            setTitle("Your Profile");
            fragment = new frgProfile();
        }  else if (id == R.id.nav_logout) {
            FirebaseAuth.getInstance().signOut();
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("isLoggedIn", false);
            editor.apply();

            startActivity(new Intent(getApplicationContext(), LoginActivity.class));

        } else if (id == R.id.nav_contact) {
            dlayout.setVisibility(View.GONE);
            frameLayout.setVisibility(View.VISIBLE);
            fragment = new Contactus();

        }
        else if (id == R.id.nav_settings) {
            dlayout.setVisibility(View.GONE);
            frameLayout.setVisibility(View.VISIBLE);
            setTitle("Settings");
            fragment = new frgSettings();

        }

        else if (id == R.id.nav_info) {
            dlayout.setVisibility(View.GONE);
            frameLayout.setVisibility(View.VISIBLE);
            setTitle("About");
            fragment = new frgAbout();

        } else if (id == R.id.nav_url) {
            dlayout.setVisibility(View.GONE);
            frameLayout.setVisibility(View.VISIBLE);
            setTitle("Base Url");
            fragment = new frmAddUrl();
        } else if (id == R.id.nav_feedback) {
            dlayout.setVisibility(View.GONE);
            frameLayout.setVisibility(View.VISIBLE);
            setTitle("Feedback Us");
            fragment = new frgFeedback();
        } else if (id == R.id.nav_share) {
            Toast.makeText(this, "Under Construction", Toast.LENGTH_LONG).show();
            shareApp();
            return true;
        }
        if (fragment != null) {
            setFragment();
        }

        DrawerLayout drawer =  findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    private void shareApp() {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Share EpiCare");
        sharingIntent.putExtra(Intent.EXTRA_TEXT, "Check this out: ");
        startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }
}