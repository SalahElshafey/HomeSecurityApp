package com.example.homesecurityapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.*;
import android.os.Bundle;
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

import java.util.Arrays;

public class MainActivity2 extends AppCompatActivity {

    private static final int CAMERA_REQUEST_CODE = 100;

    private TextureView textureView;
    private CameraDevice cameraDevice;
    private CameraCaptureSession cameraCaptureSession;
    private CaptureRequest.Builder captureRequestBuilder;

    private Button lockerButton, alertButton, homeButton;
    private ImageView historyImage;
    private TextView welcomeMessage;
    private boolean isLockerOpen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        initializeViews();
        displayWelcomeMessage();
        setupListeners();
        checkCameraPermission();
    }

    private void initializeViews() {
        textureView = findViewById(R.id.live_stream);
        lockerButton = findViewById(R.id.locker_button);
        historyImage = findViewById(R.id.history_button);
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
        welcomeMessage.setText(username != null && !username.isEmpty() ? "Welcome back, " + username + "!" : "Welcome back, User!");
    }

    private void setupListeners() {
        homeButton.setOnClickListener(v -> navigateTo(MainActivity5.class));
        historyImage.setOnClickListener(v -> navigateTo(MainActivity3.class));
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
        if (isLockerOpen) {
            lockerButton.setText("Locker Closed");
            lockerButton.setBackgroundColor(getResources().getColor(android.R.color.holo_red_dark));
            Toast.makeText(this, "Locker is now Closed!", Toast.LENGTH_SHORT).show();
            isLockerOpen = false;
        } else {
            lockerButton.setText("Locker Open");
            lockerButton.setBackgroundColor(getResources().getColor(android.R.color.holo_green_dark));
            Toast.makeText(this, "Locker is now Open!", Toast.LENGTH_SHORT).show();
            isLockerOpen = true;
        }
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
                Toast.makeText(this, "Camera permission granted", Toast.LENGTH_SHORT).show();
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
            String cameraId = cameraManager.getCameraIdList()[0]; // Rear camera
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
                    Toast.makeText(MainActivity2.this, "Camera error: " + error, Toast.LENGTH_SHORT).show();
                }
            }, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void createCameraPreviewSession() {
        try {
            SurfaceTexture surfaceTexture = textureView.getSurfaceTexture();
            surfaceTexture.setDefaultBufferSize(1920, 1080);
            Surface surface = new Surface(surfaceTexture);

            captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            captureRequestBuilder.addTarget(surface);

            cameraDevice.createCaptureSession(Arrays.asList(surface), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession session) {
                    cameraCaptureSession = session;
                    try {
                        cameraCaptureSession.setRepeatingRequest(captureRequestBuilder.build(), null, null);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                    Toast.makeText(MainActivity2.this, "Camera configuration failed!", Toast.LENGTH_SHORT).show();
                }
            }, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
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
