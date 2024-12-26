package com.example.homesecurityapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest; // Import this!
import android.os.Bundle;
import android.view.Surface;
import android.view.TextureView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity2 extends AppCompatActivity {

    private TextureView textureView;
    private CameraDevice cameraDevice;
    private CameraCaptureSession cameraCaptureSession;
    private CaptureRequest.Builder captureRequestBuilder;
    private Button lockerButton;
    private ImageView historyImage;
    private DatabaseReference databaseReference;
    private Timer timer;

    private boolean isLockerOpen = false; // Locker state

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        textureView = findViewById(R.id.tv);
        lockerButton = findViewById(R.id.locker_button);
        historyImage = findViewById(R.id.history_image);

        // Initialize Firebase reference
        databaseReference = FirebaseDatabase.getInstance().getReference("actions");

        // Start live stream
        textureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surface, int width, int height) {
                startLiveStream(surface);
            }

            @Override
            public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surface, int width, int height) {
            }

            @Override
            public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surface) {
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surface) {
            }
        });

        // Handle locker button click
        lockerButton.setOnClickListener(v -> toggleLocker());

        // Handle history image click
        historyImage.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity2.this, MainActivity3.class);
            startActivity(intent);
        });

        // Start sending "Safe" status to Firebase every 10 seconds
        startSafeStatusUpdates();
    }

    private void startLiveStream(SurfaceTexture surfaceTexture) {
        try {
            CameraManager cameraManager = (CameraManager) getSystemService(CAMERA_SERVICE);
            String cameraId = cameraManager.getCameraIdList()[0]; // Use the back camera

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 101);
                return;
            }

            cameraManager.openCamera(cameraId, new CameraDevice.StateCallback() {
                @Override
                public void onOpened(@NonNull CameraDevice camera) {
                    cameraDevice = camera;
                    try {
                        Surface surface = new Surface(surfaceTexture);
                        textureView.getSurfaceTexture().setDefaultBufferSize(textureView.getWidth(), textureView.getHeight());
                        surface = new Surface(textureView.getSurfaceTexture());

                        captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
                        captureRequestBuilder.addTarget(surface);

                        cameraDevice.createCaptureSession(Collections.singletonList(surface), new CameraCaptureSession.StateCallback() {
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
                                Toast.makeText(MainActivity2.this, "Failed to configure live stream", Toast.LENGTH_SHORT).show();
                            }
                        }, null);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
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
                }
            }, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void startSafeStatusUpdates() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
                HistoryItem historyItem = new HistoryItem("Safe", timestamp);
                databaseReference.push().setValue(historyItem);
            }
        }, 0, 10000); // Send every 10 seconds
    }

    private void toggleLocker() {
        if (isLockerOpen) {
            closeLocker();
        } else {
            openLocker();
        }
    }

    private void openLocker() {
        lockerButton.setBackgroundColor(getResources().getColor(android.R.color.holo_green_dark));
        lockerButton.setText("Locker Open");
        Toast.makeText(this, "Locker Opened!", Toast.LENGTH_SHORT).show();
        isLockerOpen = true;
    }

    private void closeLocker() {
        lockerButton.setBackgroundColor(getResources().getColor(android.R.color.holo_red_dark));
        lockerButton.setText("Locker Closed");
        Toast.makeText(this, "Locker Closed!", Toast.LENGTH_SHORT).show();
        isLockerOpen = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cameraDevice != null) {
            cameraDevice.close();
        }
        if (timer != null) {
            timer.cancel();
        }
    }
}
