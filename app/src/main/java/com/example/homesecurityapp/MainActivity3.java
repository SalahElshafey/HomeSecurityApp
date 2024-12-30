package com.example.homesecurityapp;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class MainActivity3 extends AppCompatActivity {

    private static final String LOCKER_HISTORY_REFERENCE = "locker_history";
    private static final String TAG = "MainActivity3";

    private ListView historyListView;
    private HistoryAdapter historyAdapter;
    private List<HistoryItem> historyList;
    private DatabaseReference databaseReference;
    private ChildEventListener childEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        initializeViews();
        setupFirebase();
        attachFirebaseListener();
    }

    /**
     * Initialize the UI components
     */
    private void initializeViews() {
        historyListView = findViewById(R.id.history_list_view);
        historyList = new ArrayList<>();
        historyAdapter = new HistoryAdapter(this, historyList);
        historyListView.setAdapter(historyAdapter);
    }

    /**
     * Setup Firebase Database reference
     */
    private void setupFirebase() {
        databaseReference = FirebaseDatabase.getInstance().getReference(LOCKER_HISTORY_REFERENCE);
    }

    /**
     * Attach Firebase listener to fetch data
     */
    private void attachFirebaseListener() {
        if (childEventListener == null) {
            childEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot snapshot, String previousChildName) {
                    try {
                        String lockStatus = snapshot.child("lockStatus").getValue(String.class);
                        String safetyStatus = snapshot.child("safetyStatus").getValue(String.class);
                        Long timestamp = snapshot.child("timestamp").getValue(Long.class);

                        if (lockStatus != null && safetyStatus != null && timestamp != null) {
                            historyList.add(new HistoryItem(lockStatus, safetyStatus, timestamp));
                            runOnUiThread(() -> historyAdapter.notifyDataSetChanged());
                        } else {
                            Log.w(TAG, "Incomplete data in snapshot: " + snapshot.getKey());
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error processing child data: " + e.getMessage(), e);
                    }
                }

                @Override
                public void onChildChanged(DataSnapshot snapshot, String previousChildName) {
                    // Handle changes if needed
                }

                @Override
                public void onChildRemoved(DataSnapshot snapshot) {
                    // Handle removals if needed
                }

                @Override
                public void onChildMoved(DataSnapshot snapshot, String previousChildName) {
                    // Handle moves if needed
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    Log.e(TAG, "Firebase listener cancelled: " + error.getMessage(), error.toException());
                    Toast.makeText(MainActivity3.this, "Error loading history: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            };
            databaseReference.addChildEventListener(childEventListener);
        }
    }

    /**
     * Detach Firebase listener to prevent memory leaks
     */
    private void detachFirebaseListener() {
        if (databaseReference != null && childEventListener != null) {
            databaseReference.removeEventListener(childEventListener);
            childEventListener = null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        detachFirebaseListener(); // Detach listener when leaving activity
    }

    @Override
    protected void onResume() {
        super.onResume();
        attachFirebaseListener(); // Re-attach listener when returning to activity
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        detachFirebaseListener(); // Ensure listener is detached on activity destroy
    }
}
