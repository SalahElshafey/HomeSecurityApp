package com.example.homesecurityapp;

import android.content.Intent;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.os.Bundle;
import android.view.Surface;
import android.view.TextureView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Collections;

public class MainActivity2 extends AppCompatActivity {

    private TextureView textureView;
    private CameraDevice cameraDevice;
    private Button lockerButton, alertButton;
    private ImageView historyImage;
    private boolean isLockerOpen = false; // Locker state flag
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        textureView = findViewById(R.id.tv);
        lockerButton = findViewById(R.id.locker_button);
        historyImage = findViewById(R.id.history_image);
        alertButton = findViewById(R.id.alert_button);

        // History button navigation
        historyImage.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity2.this, MainActivity3.class);
            startActivity(intent);
        });

        // Alert button navigation
        alertButton.setOnClickListener(v -> {
            Toast.makeText(this, "Alert is ON", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MainActivity2.this, MainActivity4.class);
            startActivity(intent);
        });



        lockerButton.setOnClickListener(v -> toggleLocker());

        // Initialize Camera Manager and setup live stream
        initializeLiveStream();
    }

    // Toggle locker button state
    private void toggleLocker() {
        if (isLockerOpen) {
            closeLocker();
        } else {
            openLocker();
        }
    }

    // Open locker
    private void openLocker() {
        lockerButton.setBackgroundColor(getResources().getColor(android.R.color.holo_green_dark));
        lockerButton.setText("Locker Open");
        Toast.makeText(this, "Locker Opened!", Toast.LENGTH_SHORT).show();
        isLockerOpen = true;
    }

    // Close locker
    private void closeLocker() {
        lockerButton.setBackgroundColor(getResources().getColor(android.R.color.holo_red_dark));
        lockerButton.setText("Locker Closed");
        Toast.makeText(this, "Locker Closed!", Toast.LENGTH_SHORT).show();
        isLockerOpen = false;
    }

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

    private void startLiveStream(SurfaceTexture surfaceTexture) {
        try {
            CameraManager cameraManager = (CameraManager) getSystemService(CAMERA_SERVICE);
            String cameraId = cameraManager.getCameraIdList()[0]; // Back camera
            cameraManager.openCamera(cameraId, new CameraDevice.StateCallback() {
                @Override
                public void onOpened(@NonNull CameraDevice camera) {
                    cameraDevice = camera;
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


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cameraDevice != null) {
            cameraDevice.close();
        }
    }
}
