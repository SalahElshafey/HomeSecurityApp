package com.example.homesecurityapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

public class CoffeeMachineFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_coffee_machine, container, false);

        // Get reference to the "Start Brewing" button
        Button startBrewButton = view.findViewById(R.id.start_brew);

        // Set an OnClickListener for the button
        startBrewButton.setOnClickListener(v -> {
            // Show a Toast message
            Toast.makeText(getContext(), "Coffee Brewing Started ☕", Toast.LENGTH_SHORT).show();
        });

        return view;
    }
}
