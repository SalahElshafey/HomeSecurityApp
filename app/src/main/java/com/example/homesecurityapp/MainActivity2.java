package com.example.homesecurityapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.*;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Collections;

public class MainActivity2 extends AppCompatActivity {

    private static final int CAMERA_REQUEST_CODE = 100;
    private static final String TAG = "MainActivity2";

    private TextureView textureView;
    private CameraDevice cameraDevice;
    private CameraCaptureSession cameraCaptureSession;
    private CaptureRequest.Builder captureRequestBuilder;

    private Button lockerButton;
    private ImageView alertButton, homeButton, historyButton;
    private TextView welcomeMessage;
    private boolean isLockerOpen = false;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        initializeViews();
        displayWelcomeMessage();
        setupListeners();
        checkCameraPermission();

        databaseReference = FirebaseDatabase.getInstance().getReference("locker_history");
    }

    private void initializeViews() {
        textureView = findViewById(R.id.live_stream);
        lockerButton = findViewById(R.id.locker_button);
        historyButton = findViewById(R.id.history_button);
        alertButton = findViewById(R.id.alert_button);
        homeButton = findViewById(R.id.home_button);
        welcomeMessage = findViewById(R.id.welcome_message);

        ProgressBar progressSecurity = findViewById(R.id.progress_security);
        ProgressBar progressSystem = findViewById(R.id.progress_system);

        progressSecurity.setProgress(85);
        progressSystem.setProgress(55);
    }

    private void displayWelcomeMessage() {
        Intent intent = getIntent();
        String username = intent.getStringExtra("USERNAME");
        welcomeMessage.setText(username != null && !username.isEmpty()
                ? "Welcome back, " + username + "!"
                : "Welcome back, User!");
    }

    private void setupListeners() {
        homeButton.setOnClickListener(v -> navigateTo(MainActivity5.class));
        historyButton.setOnClickListener(v -> navigateTo(MainActivity3.class));
        alertButton.setOnClickListener(v -> {
            Toast.makeText(this, "Alert is ON", Toast.LENGTH_SHORT).show();
            navigateTo(MainActivity4.class);
        });
        lockerButton.setOnClickListener(v -> toggleLocker());
    }

    private void navigateTo(Class<?> activityClass) {
        Intent intent = new Intent(MainActivity2.this, activityClass);
        startActivity(intent);
    }

    private void toggleLocker() {
        isLockerOpen = !isLockerOpen;

        String lockStatus = isLockerOpen ? "Unlocked" : "Locked";
        String safetyStatus = isLockerOpen ? "Not Safe" : "Safe";
        long timestamp = System.currentTimeMillis();

        runOnUiThread(() -> {
            lockerButton.setText(isLockerOpen ? "Locker Open" : "Locker Closed");
            Toast.makeText(this, isLockerOpen ? "Locker is now Open!" : "Locker is now Closed!", Toast.LENGTH_SHORT).show();
        });

        HistoryItem historyItem = new HistoryItem(lockStatus, safetyStatus, timestamp);
        databaseReference.push().setValue(historyItem)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Locker state saved successfully"))
                .addOnFailureListener(e -> Log.e(TAG, "Failed to save locker state", e));
    }

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
        } else {
            initializeLiveStream();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initializeLiveStream();
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void initializeLiveStream() {
        textureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surface, int width, int height) {
                openCamera();
            }

            @Override
            public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surface, int width, int height) {}

            @Override
            public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surface) {
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surface) {}
        });
    }

    private void openCamera() {
        try {
            CameraManager cameraManager = (CameraManager) getSystemService(CAMERA_SERVICE);
            String cameraId = cameraManager.getCameraIdList()[0];
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                cameraManager.openCamera(cameraId, new CameraDevice.StateCallback() {
                    @Override
                    public void onOpened(@NonNull CameraDevice camera) {
                        cameraDevice = camera;
                        createCameraPreviewSession();
                    }

                    @Override
                    public void onDisconnected(@NonNull CameraDevice camera) {
                        camera.close();
                        cameraDevice = null;
                    }

                    @Override
                    public void onError(@NonNull CameraDevice camera, int error) {
                        camera.close();
                        cameraDevice = null;
                        Log.e(TAG, "Camera error: " + error);
                    }
                }, null);
            } else {
                Toast.makeText(this, "Camera permission not granted", Toast.LENGTH_SHORT).show();
            }
        } catch (CameraAccessException e) {
            Log.e(TAG, "Camera access exception: " + e.getMessage());
        }
    }

    private void createCameraPreviewSession() {
        try {
            SurfaceTexture surfaceTexture = textureView.getSurfaceTexture();
            if (surfaceTexture == null) return;

            surfaceTexture.setDefaultBufferSize(1920, 1080);
            Surface surface = new Surface(surfaceTexture);

            captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            captureRequestBuilder.addTarget(surface);

            cameraDevice.createCaptureSession(Collections.singletonList(surface), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession session) {
                    cameraCaptureSession = session;
                    try {
                        cameraCaptureSession.setRepeatingRequest(captureRequestBuilder.build(), null, null);
                    } catch (CameraAccessException e) {
                        Log.e(TAG, "Capture session error: " + e.getMessage());
                    }
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                    Log.e(TAG, "Camera configuration failed");
                }
            }, null);
        } catch (CameraAccessException e) {
            Log.e(TAG, "Error creating capture session: " + e.getMessage());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cameraDevice != null) {
            cameraDevice.close();
        }
        if (cameraCaptureSession != null) {
            cameraCaptureSession.close();
        }
    }
}
