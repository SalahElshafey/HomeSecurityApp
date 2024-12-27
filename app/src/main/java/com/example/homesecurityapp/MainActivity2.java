package com.example.homesecurityapp;

import android.content.Intent;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.*;
import android.os.Bundle;
import android.view.Surface;
import android.view.TextureView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Collections;

public class MainActivity2 extends AppCompatActivity {

    private TextureView textureView;
    private CameraDevice cameraDevice;
    private Button lockerButton, alertButton, homeButton;
    private ImageView historyImage;
    private TextView welcomeMessage; // TextView for the welcome message
    private boolean isLockerOpen = false; // Locker state flag

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        initializeViews(); // Initialize views
        displayWelcomeMessage(); // Display the username in the welcome message
        setupListeners(); // Setup listeners for buttons
        initializeLiveStream(); // Initialize the live stream
    }

    /**
     * Initializes all views.
     */
    private void initializeViews() {
        textureView = findViewById(R.id.tv);
        lockerButton = findViewById(R.id.locker_button);
        historyImage = findViewById(R.id.history_image);
        alertButton = findViewById(R.id.alert_button);
        homeButton = findViewById(R.id.home_button);
        welcomeMessage = findViewById(R.id.welcome_message); // Initialize the welcome message TextView
    }

    /**
     * Displays the welcome message with the username.
     */
    private void displayWelcomeMessage() {
        Intent intent = getIntent();
        String username = intent.getStringExtra("USERNAME"); // Retrieve the username from the Intent
        if (username != null && !username.isEmpty()) {
            welcomeMessage.setText("Welcome back, " + username + "!");
        } else {
            welcomeMessage.setText("Welcome back, User!");
        }
    }

    /**
     * Sets up listeners for buttons and other interactive components.
     */
    private void setupListeners() {
        // Home button
        homeButton.setOnClickListener(v -> navigateTo(MainActivity5.class));

        // History button
        historyImage.setOnClickListener(v -> navigateTo(MainActivity3.class));

        // Alert button
        alertButton.setOnClickListener(v -> {
            Toast.makeText(this, "Alert is ON", Toast.LENGTH_SHORT).show();
            navigateTo(MainActivity4.class);
        });

        // Locker button
        lockerButton.setOnClickListener(v -> toggleLocker());
    }

    /**
     * Navigates to the specified activity.
     *
     * @param activityClass The target activity class.
     */
    private void navigateTo(Class<?> activityClass) {
        Intent intent = new Intent(MainActivity2.this, activityClass);
        startActivity(intent);
    }

    /**
     * Toggles the locker state between open and closed.
     */
    private void toggleLocker() {
        if (isLockerOpen) {
            closeLocker();
        } else {
            openLocker();
        }
    }

    /**
     * Opens the locker and updates the UI.
     */
    private void openLocker() {
        lockerButton.setBackgroundColor(getResources().getColor(android.R.color.holo_green_dark));
        lockerButton.setText("Locker Open");
        Toast.makeText(this, "Locker Opened!", Toast.LENGTH_SHORT).show();
        isLockerOpen = true;
    }

    /**
     * Closes the locker and updates the UI.
     */
    private void closeLocker() {
        lockerButton.setBackgroundColor(getResources().getColor(android.R.color.holo_red_dark));
        lockerButton.setText("Locker Closed");
        Toast.makeText(this, "Locker Closed!", Toast.LENGTH_SHORT).show();
        isLockerOpen = false;
    }

    /**
     * Initializes the live camera stream.
     */
    private void initializeLiveStream() {
        textureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surface, int width, int height) {
                startLiveStream(surface);
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

    /**
     * Starts the live camera stream.
     *
     * @param surfaceTexture The surface texture for the stream.
     */
    private void startLiveStream(SurfaceTexture surfaceTexture) {
        try {
            CameraManager cameraManager = (CameraManager) getSystemService(CAMERA_SERVICE);
            String cameraId = cameraManager.getCameraIdList()[0]; // Back camera
            cameraManager.openCamera(cameraId, new CameraDevice.StateCallback() {
                @Override
                public void onOpened(@NonNull CameraDevice camera) {
                    cameraDevice = camera;
                    setupCaptureSession(surfaceTexture);
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

    /**
     * Sets up the camera capture session.
     *
     * @param surfaceTexture The surface texture for the stream.
     */
    private void setupCaptureSession(SurfaceTexture surfaceTexture) {
        try {
            Surface surface = new Surface(surfaceTexture);
            CaptureRequest.Builder captureRequestBuilder =
                    cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            captureRequestBuilder.addTarget(surface);

            cameraDevice.createCaptureSession(Collections.singletonList(surface),
                    new CameraCaptureSession.StateCallback() {
                        @Override
                        public void onConfigured(@NonNull CameraCaptureSession session) {
                            try {
                                session.setRepeatingRequest(captureRequestBuilder.build(), null, null);
                            } catch (CameraAccessException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                            Toast.makeText(MainActivity2.this, "Live stream failed to configure", Toast.LENGTH_SHORT).show();
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
    }
}
