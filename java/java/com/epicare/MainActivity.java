package com.epicare;
import static com.epicare.utils.MyConstants.PREF_NAME_GLOBAL;

import android.app.ActivityManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.PermissionRequest;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.epicare.service.BluetoothForegroundService;
import com.epicare.service.LocationForegroundService;
import com.epicare.utils.LocationUtils;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.io.File;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final int REQUEST_LOCATION_PERMISSIONS = 101;
    private static final int REQUEST_BLUETOOTH_PERMISSIONS = 102;
    private SharedPreferences sharedPreferences;
    private Fragment fragment = null;
    TableLayout tableLayout =null;
    // Get the directory path for Documents folder
    File documentsDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
    File inspecteaseDirectory = new File(documentsDirectory, "EpicCare");
    LinearLayout dlayout;
    FrameLayout frameLayout;
    private TextView locationTextView,txt_drname;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    private Button btnBluetoothConnect;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        dlayout = findViewById(R.id.dashboard_content);
        locationTextView = findViewById(R.id.locationTextView);
        txt_drname=findViewById(R.id.txt_drname);
        btnBluetoothConnect = findViewById(R.id.btnBluetoothConnect);
        requestPermissionsIfNeeded();
        requestLocationPermissions(); // for location
        requestBluetoothPermissions(); // ✅ Request Bluetooth Permissions
        updateBluetoothButton();
        btnBluetoothConnect.setOnClickListener(v -> {
            if (isBluetoothServiceRunning()) {
                stopBluetoothService();
            } else {
                startBluetoothService();
            }
            updateBluetoothButton(); // Update button state after action
        });

        if (!inspecteaseDirectory.exists()) {
            if (inspecteaseDirectory.mkdirs()) {
                Toast.makeText(getApplicationContext(), "EpicCare directory created successfully", Toast.LENGTH_LONG).show();
                Log.d("CreateDirectory", "EpicCare directory created successfully");
            } else {
                Toast.makeText(getApplicationContext(), "Failed to create EpicCare directory", Toast.LENGTH_LONG).show();
                Log.e("CreateDirectory", "Failed to create EpicCare directory");
                return;
            }
        }
        sharedPreferences = getSharedPreferences(PREF_NAME_GLOBAL, MODE_PRIVATE);
        frameLayout = findViewById(R.id.content_frame);
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
                startActivity(new Intent(MainActivity.this, LoginActivity.class));

            }

            View mHeaderView;
            mHeaderView = navigationView.getHeaderView(0);
            ((TextView) mHeaderView.findViewById(R.id.nav_header_subtitle)).setText(firebaseAuth.getCurrentUser().getEmail());

        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), (firebaseAuth.getCurrentUser().getEmail() + "\n" + ex.toString()), Toast.LENGTH_SHORT).show();
        }

        //  Get last saved location on launch
        updateLocationText(LocationUtils.getLastKnownLocation(this));
        //  Get Contacts
        getFaimelydata();

    }
    // ✅ Check if BluetoothForegroundService is running
    private boolean isBluetoothServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (BluetoothForegroundService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
    // ✅ Start Bluetooth Service
    private void startBluetoothService() {
        Intent serviceIntent = new Intent(this, BluetoothForegroundService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent);
        } else {
            startService(serviceIntent);
        }
        Toast.makeText(this, "Bluetooth Service Started", Toast.LENGTH_SHORT).show();
    }
    // ✅ Stop Bluetooth Service
    private void stopBluetoothService() {
        Intent serviceIntent = new Intent(this, BluetoothForegroundService.class);
        stopService(serviceIntent);
        Toast.makeText(this, "Bluetooth Service Stopped", Toast.LENGTH_SHORT).show();
    }

    // ✅ Update Button State Based on Bluetooth Connection
    private void updateBluetoothButton() {
        if (isBluetoothServiceRunning()) {
            btnBluetoothConnect.setText("Disconnect Bluetooth");
            btnBluetoothConnect.setEnabled(true);
        } else {
            btnBluetoothConnect.setText("Connect Bluetooth");
            btnBluetoothConnect.setEnabled(true);
        }
    }
    private void updateLocationText(String location) {
        locationTextView.setText(location);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(new LocationReceiver()); // Prevent crash if already unregistered
        } catch (IllegalArgumentException e) {
            Log.w("MainActivity", "Receiver not registered: " + e.getMessage());
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_LOCATION_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // ✅ Permission Granted → Start Service
                startLocationService();
            } else {
                // ❌ Permission Denied → Show Message
                Toast.makeText(this, "Location permission is required for tracking.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void startLocationService() {
        Intent serviceIntent = new Intent(this, LocationForegroundService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) { // Android 14+
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.FOREGROUND_SERVICE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                startService(serviceIntent);
            } else {
                Log.e("MainActivity", "Missing permissions, service not started.");
            }
        } else {
            startService(serviceIntent);
        }
    }


    private void requestPermissionsIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) { // Android 14+
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.FOREGROUND_SERVICE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.FOREGROUND_SERVICE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION
                }, REQUEST_LOCATION_PERMISSIONS);
            }
        }
    }
    private void requestBluetoothPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) { // Android 12+
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.FOREGROUND_SERVICE_CONNECTED_DEVICE) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.FOREGROUND_SERVICE_CONNECTED_DEVICE,
                        Manifest.permission.BLUETOOTH_CONNECT,
                        Manifest.permission.BLUETOOTH_SCAN
                }, REQUEST_BLUETOOTH_PERMISSIONS);
            }
        }
    }
    private void requestLocationPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) { // Android 14+
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.FOREGROUND_SERVICE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                // ✅ Request Permissions at Runtime
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.FOREGROUND_SERVICE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION
                }, REQUEST_LOCATION_PERMISSIONS);
            } else {
                // ✅ Permissions already granted → Start Service
                startLocationService();
            }
        } else {
            // ✅ For Android 13 and below → Start Service Directly
            startLocationService();
        }
    }


    public class LocationReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.hasExtra("latitude") && intent.hasExtra("longitude")) {
                double lat = intent.getDoubleExtra("latitude", 0);
                double lon = intent.getDoubleExtra("longitude", 0);
                long time = intent.getLongExtra("timestamp", 0);

                String updatedLocation = "Lat: " + lat + ", Lon: " + lon + ", Time: " + time;
                updateLocationText(updatedLocation);
            }
        }
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

    private void copyToClipboard(Context context, String text) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        clipboard.setPrimaryClip(android.content.ClipData.newPlainText("Copied.", text));
    }
    private String formatTimestamp(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }
    private void shareApp() {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Share EpiCare");
        sharingIntent.putExtra(Intent.EXTRA_TEXT, "Check this out: ");
        startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_frame, fragment);
        ft.commit();
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

        } else if (id == R.id.nav_addMembers) {
            dlayout.setVisibility(View.GONE);
            frameLayout.setVisibility(View.VISIBLE);
            setTitle("Add Members");
            fragment = new frgSmsMembers();

        } else if (id == R.id.nav_profile) {
            dlayout.setVisibility(View.GONE);
            frameLayout.setVisibility(View.VISIBLE);
            setTitle("Your Profile");
            fragment = new frgProfile();
        } else if (id == R.id.nav_testDemoData) {
            startActivity(new Intent(getApplicationContext(), ActivityDemoData.class));

        } else if (id == R.id.nav_logout) {
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
public void getFaimelydata()
{
    tableLayout = findViewById(R.id.tableLayout);
    if (tableLayout == null) {
        Toast.makeText(this, "TableLayout is null", Toast.LENGTH_SHORT).show();
        return;
    }
    // In your onCreate or relevant method:
    SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME_GLOBAL, MODE_PRIVATE);

// Retrieve values from SharedPreferences
    String name1 = sharedPreferences.getString("name1", ""); // Default value is empty string if not found
    String name2 = sharedPreferences.getString("name2", "");
    String name3 = sharedPreferences.getString("name3", "");
    String name4 = sharedPreferences.getString("name4", "");

    String phone1 = sharedPreferences.getString("phone1", "");
    String phone2 = sharedPreferences.getString("phone2", "");
    String phone3 = sharedPreferences.getString("phone3", "");
    String phone4 = sharedPreferences.getString("phone4", "");


// Find TableRows by ID instead of getChildAt
    TableRow row1 = findViewById(R.id.row1);
    TableRow row2 = findViewById(R.id.row2);
    TableRow row3 = findViewById(R.id.row3);
    TableRow row4 = findViewById(R.id.row4);

    if (row1 != null && row2 != null && row3 != null && row4 != null) {
        ((TextView) row1.getChildAt(0)).setText(name1);
        ((TextView) row1.getChildAt(1)).setText(phone1);

        ((TextView) row2.getChildAt(0)).setText(name2);
        ((TextView) row2.getChildAt(1)).setText(phone2);

        ((TextView) row3.getChildAt(0)).setText(name3);
        ((TextView) row3.getChildAt(1)).setText(phone3);

        ((TextView) row4.getChildAt(0)).setText(name4);
        ((TextView) row4.getChildAt(1)).setText(phone4);
    } else {
        Toast.makeText(this,"One or more TableRows are null",Toast.LENGTH_SHORT).show();
    }
    txt_drname.setText(name4);

}
}
