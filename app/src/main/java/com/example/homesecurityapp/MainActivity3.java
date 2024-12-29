package com.example.homesecurityapp;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity3 extends AppCompatActivity {

    private ListView historyListView;
    private HistoryAdapter historyAdapter;
    private List<HistoryItem> historyList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        initializeViews();
        loadHistoryData();
    }

    private void initializeViews() {
        historyListView = findViewById(R.id.history_list_view);
        historyList = new ArrayList<>();
        historyAdapter = new HistoryAdapter(this, historyList);
        historyListView.setAdapter(historyAdapter);
    }

    private void loadHistoryData() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("locker_history");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                historyList.clear();
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    String lockStatus = childSnapshot.child("lockStatus").getValue(String.class);
                    String safetyStatus = childSnapshot.child("safetyStatus").getValue(String.class);
                    Long timestamp = childSnapshot.child("timestamp").getValue(Long.class);

                    if (lockStatus != null && safetyStatus != null && timestamp != null) {
                        historyList.add(new HistoryItem(lockStatus, safetyStatus, timestamp));
                    }
                }
                historyAdapter.notifyDataSetChanged(); // Update the UI
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(MainActivity3.this, "Failed to load data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
