# 📋 Smart Attendance Logger

> A mobile application that modernizes classroom attendance management using **QR code scanning** and **GPS location verification** — built with Kotlin and Firebase.

---

## 🎯 Project Aim

Traditional classroom attendance is slow, error-prone, and easy to fake. Students can sign for absent classmates, and teachers waste valuable class time calling out names.

**Smart Attendance Logger** solves this by turning attendance into a two-factor verification process:

1. **QR Code** — the teacher generates a unique, time-limited code for each class session
2. **GPS** — the student's phone must be physically within 50 metres of the classroom to check in

This ensures attendance can only be marked by a student who is **genuinely present** in the room, at the **right time**, with a **valid code** — all in under 10 seconds.

---

## 📱 Screenshots

| Login | Teacher Dashboard | QR Code | Student Scan | Attendance List |
|-------|------------------|---------|--------------|-----------------|
| *Login screen* | *Teacher home* | *Generated QR* | *Scan result* | *Records list* |

---

## ✨ Features

### 👨‍🏫 Teacher Features
- **Secure login** with email and password
- **Start a class session** by entering a class name and capturing classroom GPS location
- **Generate a QR code** unique to each session — displayed full-screen for students to scan
- **5-minute countdown timer** on the QR code — automatically expires to prevent sharing
- **View attendance records** — see every student who checked in, with their name, time, and GPS verification status
- **Export to CSV** — download a spreadsheet of attendance records shareable via email, Drive, or WhatsApp

### 👨‍🎓 Student Features
- **Secure login** with email and password
- **Scan QR code** using the phone camera — opens a full-screen scanner
- **Automatic GPS verification** — checks that the student is within 50 metres of the classroom
- **Instant feedback** — success message with class name and distance, or a clear error if rejected
- **Attendance history** — view a personal log of every session attended with exact scan times
- **Duplicate prevention** — cannot mark attendance twice for the same session

### 🔒 Security Features
- **Token verification** — QR code contains a secret token matched against Firestore to prevent forged codes
- **Expiry check** — QR codes expire after 5 minutes and are rejected after that
- **GPS radius check** — students outside the 50m classroom boundary are denied
- **Firebase Authentication** — all data access requires a valid logged-in account
- **Firestore security rules** — users can only access their own data

---

## 🏗️ Architecture

```
Smart-attendance-logger/
├── app/src/main/java/com/example/smart_attendance_logger/
│   ├── auth/
│   │   ├── LoginActivity.kt          # Login screen + role-based routing
│   │   └── RegisterActivity.kt       # New account creation with role selection
│   ├── teacher/
│   │   ├── TeacherDashboardActivity.kt   # Teacher home screen
│   │   ├── GenerateQRActivity.kt         # GPS capture + QR generation + timer
│   │   └── AttendanceListActivity.kt     # View records + CSV export
│   ├── student/
│   │   ├── StudentDashboardActivity.kt   # Student home screen
│   │   ├── ScanQRActivity.kt             # QR scan + GPS verify + record attendance
│   │   └── StudentHistoryActivity.kt     # Personal attendance history
│   └── models/
│       ├── User.kt                   # User data class
│       ├── ClassSession.kt           # Session data class
│       └── AttendanceRecord.kt       # Attendance record data class
├── app/src/main/res/
│   ├── layout/                       # XML screen layouts
│   └── xml/file_paths.xml            # FileProvider paths for CSV export
└── app/google-services.json          # Firebase configuration
```

---

## 🔄 How It Works

### Attendance Flow

```
TEACHER                                    STUDENT
   │                                          │
   │  1. Opens app, logs in as Teacher        │
   │  2. Enters class name                    │
   │  3. Captures classroom GPS location      │
   │  4. Taps "Generate QR Code"              │
   │  5. QR displayed on screen ──────────►  │
   │     (expires in 5 minutes)               │  6. Opens app, logs in as Student
   │                                          │  7. Taps "Scan QR Code"
   │                                          │  8. Points camera at QR ✓
   │                                          │  9. App checks token ✓
   │                                          │ 10. App checks expiry ✓
   │                                          │ 11. App gets student GPS ✓
   │                                          │ 12. Distance < 50m ✓
   │                                          │ 13. Checks no duplicate ✓
   │  14. Record appears in list ◄──────────  │ 14. "Attendance marked!" ✓
   │  15. Teacher can export CSV              │ 15. Appears in history
```

### GPS Verification Logic

```
Student location ──► distanceBetween() ──► distance in metres
                                                    │
                              ┌─────────────────────┴──────────────────────┐
                              │                                             │
                         ≤ 50 metres                                  > 50 metres
                              │                                             │
                    ✅ Attendance recorded                     ❌ Rejected with distance shown
```

---

## 🛠️ Tech Stack

| Technology | Usage |
|------------|-------|
| **Kotlin** | Primary programming language |
| **Android Studio** | IDE |
| **Firebase Authentication** | User login and registration |
| **Firebase Firestore** | Cloud database for all app data |
| **Google Play Services Location** | GPS / FusedLocationProviderClient |
| **ZXing (core)** | QR code generation (bitmap encoding) |
| **ZXing Android Embedded** | Camera-based QR code scanning |
| **RecyclerView** | Scrollable lists for records and history |
| **CardView** | Card-style UI components |
| **ViewBinding** | Type-safe view access |
| **FileProvider** | Secure CSV file sharing |

---

## 🚀 Getting Started

### Prerequisites

- Android Studio (latest stable version)
- A Firebase account (free)
- An Android device or emulator running API 26+

### 1. Clone the repository

```bash
git clone https://github.com/your-username/smart-attendance-logger.git
cd smart-attendance-logger
```

### 2. Set up Firebase

1. Go to [console.firebase.google.com](https://console.firebase.google.com) and create a new project
2. Add an Android app with package name `com.example.smart_attendance_logger`
3. Download `google-services.json` and place it in the `app/` directory
4. In the Firebase console, enable:
   - **Authentication → Sign-in method → Email/Password**
   - **Firestore Database → Create database → Start in test mode**

### 3. Open in Android Studio

Open the project folder in Android Studio and wait for Gradle to sync.

### 4. Run the app

Connect a physical Android device or start an emulator, then click **Run ▶**

> ⚠️ GPS verification requires a **real physical device**. The emulator's GPS is simulated and will not accurately reflect classroom proximity.

---

## 📦 Building the APK

To generate an installable APK:

1. In Android Studio: **Build → Build Bundle(s) / APK(s) → Build APK(s)**
2. Once complete, click **"locate"** in the notification
3. The APK is at: `app/build/outputs/apk/debug/app-debug.apk`
4. Transfer to your phone and install (enable "Install from unknown sources" if prompted)

---

## 🗄️ Firestore Data Structure

```
Firestore
├── users/{uid}
│   ├── uid: string
│   ├── name: string
│   ├── email: string
│   └── role: "teacher" | "student"
│
├── sessions/{sessionId}
│   ├── sessionId: string
│   ├── teacherId: string
│   ├── className: string
│   ├── latitude: number
│   ├── longitude: number
│   ├── radiusMeters: number        (default: 50.0)
│   ├── timestamp: number           (session creation time)
│   ├── token: string               (8-char security token)
│   └── expiryTime: number          (creation + 5 minutes)
│
└── attendance/{recordId}
    ├── recordId: string
    ├── sessionId: string
    ├── studentId: string
    ├── studentName: string
    ├── timestamp: number           (exact scan time)
    ├── latitude: number            (student location)
    ├── longitude: number
    └── verified: boolean
```

---

## 🔐 Firestore Security Rules

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /users/{userId} {
      allow read, write: if request.auth.uid == userId;
    }
    match /sessions/{sessionId} {
      allow read, write: if request.auth != null;
    }
    match /attendance/{recordId} {
      allow read, write: if request.auth != null;
    }
  }
}
```

---

## 📋 Attendance Verification Steps

When a student scans a QR code, the app runs **9 sequential checks** before recording attendance:

| Step | Check | Failure message |
|------|-------|----------------|
| 1 | QR format is valid (`sessionId\|token`) | "Invalid QR code" |
| 2 | Session exists in Firestore | "Session not found" |
| 3 | Token matches Firestore record | "Invalid QR code token" |
| 4 | QR code has not expired | "QR code has expired" |
| 5 | Location permission is granted | "Permission required" |
| 6 | GPS location was obtained | "Could not get location" |
| 7 | Student is within 50m of classroom | "You are too far from the classroom" |
| 8 | Student hasn't already checked in | "Already marked attendance" |
| 9 | Record saved to Firestore | "Attendance marked successfully ✅" |

---

## 🤝 Contributing

Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

---

## 📄 License

This project was developed as an academic assessment for an Android App Development course.

---



---

<div align="center">
  Built with ❤️ using Kotlin and Firebase
</div>
