package com.epicare;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.epicare.adapter.MyPatientAdapter;

import java.util.ArrayList;
import java.util.List;

public class frgMyPatient_list extends Fragment {
    private RecyclerView recyclerView;
    private MyPatientAdapter adapter;
    private List<String> patientList;
    private List<String> filteredList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);  // Enable search bar in menu
        return inflater.inflate(R.layout.frg_mypatient_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("My Patient List");

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Sample Patient List
        patientList = new ArrayList<>();
        patientList.add("John Doe");
        patientList.add("Jane Smith");
        patientList.add("Alice Brown");

        filteredList = new ArrayList<>(patientList); // Copy original list

        adapter = new MyPatientAdapter(getContext(), filteredList);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.search_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        if (searchView != null) {
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    filterPatients(query);
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    filterPatients(newText);
                    return true;
                }
            });
        }
    }

    private void filterPatients(String query) {
        filteredList.clear();
        if (query.isEmpty()) {
            filteredList.addAll(patientList);
        } else {
            for (String patient : patientList) {
                if (patient.toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(patient);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }
}
