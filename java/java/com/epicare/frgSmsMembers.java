package com.epicare;

import static com.epicare.utils.MyConstants.PREF_NAME_GLOBAL;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.telephony.SmsManager;
public class frgSmsMembers extends Fragment {
    TextInputEditText txt_pnumber1,  txt_pnumber2, txt_pnumber3, txt_pnumber4;
    TextInputEditText txt_name1,txt_name2,txt_name3,txt_name4;
    EditText txt_msg;
    Button btnsaveMember,btnTestsms;
    FusedLocationProviderClient fusedLocationProviderClient;

    private ActivityResultLauncher<String[]> permissionLauncher;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frg_sms_members, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("SMS Members");
        txt_msg = view.findViewById(R.id.txt_sms);
        txt_pnumber1 = view.findViewById(R.id.txt_phone_number1);
        txt_pnumber2 = view.findViewById(R.id.txt_phone_number2);
        txt_pnumber3 = view.findViewById(R.id.txt_phone_number3);
        txt_pnumber4 = view.findViewById(R.id.txt_phone_number4);
        txt_name1=view.findViewById(R.id.txt_name1);
        txt_name2=view.findViewById(R.id.txt_name2);
        txt_name3=view.findViewById(R.id.txt_name3);
        txt_name4=view.findViewById(R.id.txt_name4);

        btnsaveMember = view.findViewById(R.id.btnSaveMember);
        btnTestsms=view.findViewById(R.id.btnTestsms);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        SharedPreferences getShared = requireActivity().getSharedPreferences(PREF_NAME_GLOBAL, Context.MODE_PRIVATE);
        txt_pnumber1.setText(getShared.getString("phone1", ""));
        txt_pnumber2.setText(getShared.getString("phone2", ""));
        txt_pnumber3.setText(getShared.getString("phone3", ""));
        txt_pnumber4.setText(getShared.getString("phone4", ""));
        txt_name1.setText(getShared.getString("name1", ""));
        txt_name2.setText(getShared.getString("name2", ""));
        txt_name3.setText(getShared.getString("name3", ""));
        txt_name4.setText(getShared.getString("name4", ""));
        txt_msg.setText(getShared.getString("msg", "I am in danger, please come fast..."));

        btnsaveMember.setOnClickListener(v -> {

            SharedPreferences.Editor editor = getShared.edit();
            editor.putString("phone1", txt_pnumber1.getText().toString());
            editor.putString("phone2", txt_pnumber2.getText().toString());
            editor.putString("phone3", txt_pnumber3.getText().toString());
            editor.putString("phone4", txt_pnumber4.getText().toString());
            editor.putString("name1", txt_name1.getText().toString());
            editor.putString("name2", txt_name2.getText().toString());
            editor.putString("name3", txt_name3.getText().toString());
            editor.putString("name4", txt_name4.getText().toString());
            editor.putString("msg", txt_msg.getText().toString());
            editor.apply();
            Toast.makeText(requireActivity(), "Saved...", Toast.LENGTH_SHORT).show();
        });
        btnTestsms.setOnClickListener(v -> {
            Toast.makeText(requireActivity(), "sending....", Toast.LENGTH_SHORT).show();
            tryIt();
            Toast.makeText(requireActivity(), "Successfully sent test sms", Toast.LENGTH_SHORT).show();
        });
    }


    private void tryIt() {
//        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED &&
//                ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            SendLocationMessage();

//        } else {
//            // Request both SMS and location permissions
//            permissionLauncher.launch(new String[]{
//                    Manifest.permission.SEND_SMS,
//                    Manifest.permission.ACCESS_FINE_LOCATION
//            });
//        }
    }
    private void SendLocationMessage() {
//        if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            return;
//        }
        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(task -> {
            Location location = task.getResult();
            String Message = txt_msg.getText().toString().trim();
            if (location != null) {
                try {
                    Geocoder geocoder = new Geocoder(requireActivity(), Locale.getDefault());
                    List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    Message += "I am at " + addresses.get(0).getLatitude() + "," + addresses.get(0).getLongitude() + ", " + addresses.get(0).getCountryName() + "," + addresses.get(0).getLocality() + ", " + addresses.get(0).getAddressLine(0);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            sendSMS(Message);
        });
    }

    private void sendSMS(String message) {
        SmsManager smsManager = SmsManager.getDefault();
        if (!txt_pnumber1.getText().toString().isEmpty()) {
            smsManager.sendTextMessage(txt_pnumber1.getText().toString(), null, message, null, null);
        }
        if (!txt_pnumber2.getText().toString().isEmpty()) {
            smsManager.sendTextMessage(txt_pnumber2.getText().toString(), null, message, null, null);
        }
        if (!txt_pnumber3.getText().toString().isEmpty()) {
            smsManager.sendTextMessage(txt_pnumber3.getText().toString(), null, message, null, null);
        }
        if (!txt_pnumber4.getText().toString().isEmpty()) {
            smsManager.sendTextMessage(txt_pnumber4.getText().toString(), null, message, null, null);
        }
        Toast.makeText(requireActivity(), "Message sent...", Toast.LENGTH_SHORT).show();
    }
}
