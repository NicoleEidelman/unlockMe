# AndroidMultiStepAuthenticationProcess

A futuristic Android application that guides users through a **multi-step authentication process** using real-time checks and intuitive interactions. Each step is visualized through circular indicators and animated feedback. Once all checks are passed, a dynamic unlocking animation is triggered.

---

## Features
 **WiFi Checker**  
Verifies the device is connected to a secure WiFi network (not public or guest).

 **Time-Based Condition**  
Approves access if the current time satisfies specific rules (odd digit sum or minute).

 **Motion Detection**  
Detects shake movement using the device’s accelerometer.

 **2-Factor Authentication (2FA)**  
Sends a code via SMS. If auto-detection fails, prompts user to enter their number manually.

 **Charging Status**  
Checks if the device is currently charging or fully charged.

 **Voice Command Recognition**  
Listens for the voice command `"open"` and fills the final authentication circle.

 **Visual Completion Animation**  
All six circles animate outwards before revealing a lock and success message.

---


##  Getting Started

### Prerequisites
- Android Studio
- Git
- Physical or emulated Android device with:
    - Microphone access
    - SMS permission (for 2FA)
    - Accelerometer sensor (for shake detection)

### Installation
1. Clone this repository:
```bash
git clone https://github.com/NicoleEidelman/unlockMe.git
```

2. Open the project in Android Studio
3. Run the app on your device/emulator
4. Grant the requested permissions on first run

---

##  Permissions Required

- `RECORD_AUDIO` – for voice recognition
- `SEND_SMS`, `READ_PHONE_NUMBERS`, `READ_PHONE_STATE` – for 2FA
- `ACCESS_FINE_LOCATION`, `ACCESS_WIFI_STATE` – for WiFi validation
- `VIBRATE` – for feedback upon repeated clicks
- `ACCESS_NETWORK_STATE`, `ACCESS_COARSE_LOCATION` – supporting checks

---

##  Project Structure Overview
MainActivity.java – Central activity and UI logic

CircleManager.java – UI state + animation manager

VoiceCommandRecognizer.java – Speech recognition logic

WifiChecker.java, TimeChecker.java, ShakeDetector.java, ChargingStatusChecker.java – Logic modules

TwoFactorAuthenticator.java – Handles SMS-based verification

---

##  Future Ideas

- Save authentication state in SharedPreferences
- Add biometric (fingerprint) as an optional extra
- Visual feedback while voice recording
- Localization support (English / Hebrew)

---
