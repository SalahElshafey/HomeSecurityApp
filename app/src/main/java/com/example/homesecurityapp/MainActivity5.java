package com.example.homesecurityapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.cardview.widget.CardView;

public class MainActivity5 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main5);

        // Set up the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Enable the back button in the toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Home Security Features");
        }

        // Initialize CardViews
        CardView cardCoffee = findViewById(R.id.card_coffee);
        CardView cardLighting = findViewById(R.id.card_lighting);
        CardView cardThermostat = findViewById(R.id.card_thermostat);
        CardView cardMusic = findViewById(R.id.card_music);

        // Load the CoffeeMachineFragment by default
        loadFragment(new CoffeeMachineFragment());

        // Set click listeners for the cards
        cardCoffee.setOnClickListener(v -> loadFragment(new CoffeeMachineFragment()));
        cardLighting.setOnClickListener(v -> loadFragment(new LightingFragment()));
        cardThermostat.setOnClickListener(v -> loadFragment(new ThermostatFragment()));
        cardMusic.setOnClickListener(v -> loadFragment(new MusicFragment()));
    }

    private void loadFragment(Fragment fragment) {
        // Create a FragmentManager
        FragmentManager fragmentManager = getSupportFragmentManager();
        // Begin a transaction and add to the back stack
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.addToBackStack(null); // Add the transaction to the back stack
        fragmentTransaction.commit();
    }

    // Handle the toolbar back button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            handleBackNavigation();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Handle the physical back button
    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            // Pop the last fragment in the back stack
            getSupportFragmentManager().popBackStack();
        } else {
            // Navigate back to MainActivity2
            handleBackNavigation();
        }
    }

    private void handleBackNavigation() {
        Intent intent = new Intent(MainActivity5.this, MainActivity2.class);
        startActivity(intent);
        finish(); // Close the current activity
    }
}
