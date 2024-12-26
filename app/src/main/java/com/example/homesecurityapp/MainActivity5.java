package com.example.homesecurityapp;

import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.cardview.widget.CardView; // Correct import for CardView

public class MainActivity5 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main5);

        CardView cardCoffee = findViewById(R.id.card_coffee);
        CardView cardLighting = findViewById(R.id.card_lighting);

        // Load the CoffeeMachineFragment by default
        loadFragment(new CoffeeMachineFragment());

        cardCoffee.setOnClickListener(v -> loadFragment(new CoffeeMachineFragment()));
        cardLighting.setOnClickListener(v -> loadFragment(new LightingFragment()));
    }

    private void loadFragment(Fragment fragment) {
        // Create a FragmentManager
        FragmentManager fragmentManager = getSupportFragmentManager();
        // Begin a transaction
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        // Replace the current fragment with the new one
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        // Commit the transaction
        fragmentTransaction.commit();
    }
}