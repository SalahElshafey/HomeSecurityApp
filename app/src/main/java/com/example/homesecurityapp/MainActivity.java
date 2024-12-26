package com.example.homesecurityapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private static final String ACCEPTED_USERNAME = "admin";
    private static final String ACCEPTED_PASSWORD = "password";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        usernameEditText = findViewById(R.id.txt1);
        passwordEditText = findViewById(R.id.txt2);
        loginButton = findViewById(R.id.but1);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                if (username.equals(ACCEPTED_USERNAME) && password.equals(ACCEPTED_PASSWORD)) {
                    Intent intent = new Intent(MainActivity.this,MainActivity2.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this, "Invalid username or password", Toast.LENGTH_SHORT).show();

                }

            }
        });

    }
}