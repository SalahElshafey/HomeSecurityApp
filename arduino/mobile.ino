#include <WiFi.h>
#include <FirebaseESP32.h>
#include <Servo.h>

// WiFi credentials
#define WIFI_SSID "YOUR_WIFI_SSID"
#define WIFI_PASSWORD "YOUR_WIFI_PASSWORD"

// Firebase credentials
#define FIREBASE_HOST https://homesecurityapp-730f2-default-rtdb.firebaseio.com/
#define FIREBASE_AUTH KXgKBStEgD5CnL7ki8RHESVQcMaTwaVQCYueMYsF

// Firebase paths
#define LOCKER_STATUS_PATH "/locker/lockStatus"
#define SAFETY_STATUS_PATH "/locker/safetyStatus"
#define ALERT_STATUS_PATH "/alert/status"

// Pin configuration
#define PIR_SENSOR_PIN 27
#define BUZZER_PIN 26
#define SERVO_PIN 25

// Firebase and Servo objects
FirebaseData firebaseData;
Servo servo;

// Variables
bool isLockerOpen = false;

void setup() {
  // Initialize Serial Monitor
  Serial.begin(115200);

  // Connect to Wi-Fi
  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
  Serial.print("Connecting to Wi-Fi");
  while (WiFi.status() != WL_CONNECTED) {
    delay(1000);
    Serial.print(".");
  }
  Serial.println("\nConnected to Wi-Fi");

  // Initialize Firebase
  Firebase.begin(FIREBASE_HOST, FIREBASE_AUTH);
  Firebase.reconnectWiFi(true);
  Serial.println("Connected to Firebase");

  // Pin modes
  pinMode(PIR_SENSOR_PIN, INPUT);
  pinMode(BUZZER_PIN, OUTPUT);
  servo.attach(SERVO_PIN);

  // Initial servo position
  servo.write(0); // Locked position
}

void loop() {
  // Check motion with PIR sensor
  int motionDetected = digitalRead(PIR_SENSOR_PIN);

  // Update safety status in Firebase
  if (motionDetected) {
    Firebase.setString(firebaseData, SAFETY_STATUS_PATH, "Not Safe");
  } else {
    Firebase.setString(firebaseData, SAFETY_STATUS_PATH, "Safe");
  }

  // Fetch locker status from Firebase
  if (Firebase.getString(firebaseData, LOCKER_STATUS_PATH)) {
    String lockStatus = firebaseData.stringData();

    if (lockStatus == "Unlocked" && !isLockerOpen) {
      servo.write(90); // Open position
      isLockerOpen = true;
      Serial.println("Locker Unlocked");
    } else if (lockStatus == "Locked" && isLockerOpen) {
      servo.write(0); // Locked position
      isLockerOpen = false;
      Serial.println("Locker Locked");
    }
  } else {
    Serial.println("Failed to get locker status from Firebase");
  }

  // Fetch alert status from Firebase
  if (Firebase.getString(firebaseData, ALERT_STATUS_PATH)) {
    String alertStatus = firebaseData.stringData();

    if (alertStatus == "ON") {
      digitalWrite(BUZZER_PIN, HIGH); // Turn buzzer ON
      delay(200); // Short beep
      digitalWrite(BUZZER_PIN, LOW);  // Turn buzzer OFF
      delay(200);
    }
  } else {
    Serial.println("Failed to get alert status from Firebase");
  }

  // Add a short delay to reduce network traffic
  delay(1000);
}
