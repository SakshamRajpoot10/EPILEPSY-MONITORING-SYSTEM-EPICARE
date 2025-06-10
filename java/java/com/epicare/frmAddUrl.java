package com.epicare;

import static android.content.Context.MODE_PRIVATE;
import static com.epicare.utils.MyConstants.PREF_NAME_GLOBAL;
import static com.epicare.utils.MyConstants.base_api_url;
import java.util.regex.Pattern;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;

public class frmAddUrl extends Fragment {

    private TextInputEditText urlEditText;
    private SharedPreferences sharedPreferences;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frg_add_url, container, false);

        urlEditText = view.findViewById(R.id.urlEditText);
        sharedPreferences = getActivity().getSharedPreferences(PREF_NAME_GLOBAL, MODE_PRIVATE);
        String url = sharedPreferences.getString("urls", "");

        if (url.trim().isEmpty()) {
            urlEditText.setText(base_api_url);
        } else {
            urlEditText.setText(url);
        }

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Base Url Settings");

        view.findViewById(R.id.saveButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSaveButtonClicked(v);
            }
        });
    }

    public void onSaveButtonClicked(View view) {
        String url = urlEditText.getText().toString();
        if (!isValidUrlOrIp(url)) {
            Toast.makeText(getActivity().getApplicationContext(), "Invalid URL or IP Address!", Toast.LENGTH_SHORT).show();
            return;
        }

        saveUrl(url);
    }

    private void saveUrl(String url) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("urls", url);
        editor.commit();

        Toast.makeText(getActivity().getApplicationContext(), "Saved Successful", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(getActivity().getApplicationContext(), MainActivity.class));
    }

    // Regex for IP Address and URL Validation
    private boolean isValidUrlOrIp(String url) {
        String ipRegex = "^((25[0-5]|2[0-4][0-9]|[0-1]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[0-1]?[0-9][0-9]?)$";
        String urlRegex = "^(https?://)?([\\w.-]+)\\.([a-z]{2,6})(:[0-9]{1,5})?(/.*)?$";

        return Pattern.matches(ipRegex, url) || Pattern.matches(urlRegex, url);
    }

}
