package com.example.smart_attendance_logger.student

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.smart_attendance_logger.databinding.ActivityScanQrBinding
import com.example.smart_attendance_logger.models.AttendanceRecord
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions
import java.util.UUID

class ScanQRActivity : AppCompatActivity() {

    private lateinit var binding: ActivityScanQrBinding
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    // This launcher opens the QR scanner and gets the result back
    private val scanLauncher = registerForActivityResult(ScanContract()) { result: ScanIntentResult ->
        if (result.contents != null) {
            // QR scanned successfully — now verify location
            handleQRResult(result.contents)
        } else {
            showStatus("❌ Scan cancelled", isError = true)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScanQrBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnOpenScanner.setOnClickListener {
            startQRScan()
        }
    }

    private fun startQRScan() {
        val options = ScanOptions().apply {
            setPrompt("Scan the teacher's QR code")
            setBeepEnabled(true)
            setOrientationLocked(false)
            setBarcodeImageEnabled(false)
        }
        scanLauncher.launch(options)
    }

    private fun handleQRResult(qrContent: String) {
        // QR format is: "sessionId|token"
        val parts = qrContent.split("|")
        if (parts.size != 2) {
            showStatus("❌ Invalid QR code. Please try again.", isError = true)
            return
        }

        val sessionId = parts[0]
        val token = parts[1]

        showStatus("✅ QR scanned! Verifying your location...", isError = false)
        binding.progressBar.visibility = View.VISIBLE

        // Step 1: verify the session exists in Firestore and token matches
        db.collection("sessions").document(sessionId).get()
            .addOnSuccessListener { doc ->
                if (!doc.exists()) {
                    binding.progressBar.visibility = View.GONE
                    showStatus("❌ Session not found. Ask your teacher to regenerate.", isError = true)
                    return@addOnSuccessListener
                }

                val savedToken = doc.getString("token") ?: ""
                if (savedToken != token) {
                    binding.progressBar.visibility = View.GONE
                    showStatus("❌ Invalid QR code token.", isError = true)
                    return@addOnSuccessListener
                }

                // Token matches — now get GPS location
                val classLat = doc.getDouble("latitude") ?: 0.0
                val classLng = doc.getDouble("longitude") ?: 0.0
                val radius = doc.getDouble("radiusMeters") ?: 50.0
                val className = doc.getString("className") ?: "Class"

                getCurrentLocation { studentLocation ->
                    binding.progressBar.visibility = View.GONE

                    if (studentLocation == null) {
                        showStatus("❌ Could not get your location. Make sure GPS is on.", isError = true)
                        return@getCurrentLocation
                    }

                    // Step 2: calculate distance between student and classroom
                    val distanceResults = FloatArray(1)
                    Location.distanceBetween(
                        studentLocation.latitude, studentLocation.longitude,
                        classLat, classLng,
                        distanceResults
                    )
                    val distanceMeters = distanceResults[0]

                    if (distanceMeters <= radius) {
                        // Within range — record attendance
                        recordAttendance(
                            sessionId = sessionId,
                            className = className,
                            studentLat = studentLocation.latitude,
                            studentLng = studentLocation.longitude,
                            distance = distanceMeters
                        )
                    } else {
                        showStatus(
                            "❌ You are too far from the classroom!\n" +
                                    "Distance: %.0fm (allowed: %.0fm)\n" +
                                    "Please be physically present.".format(distanceMeters, radius),
                            isError = true
                        )
                    }
                }
            }
            .addOnFailureListener {
                binding.progressBar.visibility = View.GONE
                showStatus("❌ Error: ${it.message}", isError = true)
            }
    }

    private fun getCurrentLocation(callback: (Location?) -> Unit) {
        // Check permission first
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                200
            )
            callback(null)
            return
        }

        val fusedClient = LocationServices.getFusedLocationProviderClient(this)
        val cancellationToken = CancellationTokenSource()

        // getCurrentLocation is more accurate than lastLocation
        fusedClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cancellationToken.token)
            .addOnSuccessListener { location ->
                callback(location)
            }
            .addOnFailureListener {
                // Fallback to last known location
                fusedClient.lastLocation.addOnSuccessListener { lastLocation ->
                    callback(lastLocation)
                }
            }
    }

    private fun recordAttendance(
        sessionId: String,
        className: String,
        studentLat: Double,
        studentLng: Double,
        distance: Float
    ) {
        val uid = auth.currentUser?.uid ?: return

        // Check if student already marked attendance for this session
        db.collection("attendance")
            .whereEqualTo("sessionId", sessionId)
            .whereEqualTo("studentId", uid)
            .get()
            .addOnSuccessListener { existing ->
                if (!existing.isEmpty) {
                    showStatus("⚠️ You already marked attendance for this session!", isError = false)
                    return@addOnSuccessListener
                }

                // Get student name then save the record
                db.collection("users").document(uid).get()
                    .addOnSuccessListener { userDoc ->
                        val studentName = userDoc.getString("name") ?: "Unknown"
                        val recordId = UUID.randomUUID().toString()

                        val record = AttendanceRecord(
                            recordId = recordId,
                            sessionId = sessionId,
                            studentId = uid,
                            studentName = studentName,
                            timestamp = System.currentTimeMillis(),
                            latitude = studentLat,
                            longitude = studentLng,
                            verified = true
                        )

                        db.collection("attendance").document(recordId).set(record)
                            .addOnSuccessListener {
                                showStatus(
                                    "✅ Attendance marked successfully!\n" +
                                            "Class: $className\n" +
                                            "Distance from class: %.0fm".format(distance),
                                    isError = false
                                )
                                binding.btnOpenScanner.isEnabled = false
                                binding.btnOpenScanner.text = "Attendance Recorded ✓"
                            }
                            .addOnFailureListener {
                                showStatus("❌ Failed to save: ${it.message}", isError = true)
                            }
                    }
            }
            .addOnFailureListener {
                showStatus("❌ Error checking records: ${it.message}", isError = true)
            }
    }

    private fun showStatus(message: String, isError: Boolean) {
        binding.tvStatus.text = message
        binding.tvStatus.setTextColor(
            if (isError) getColor(android.R.color.holo_red_dark)
            else getColor(android.R.color.holo_green_dark)
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 200 && grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            showStatus("Location permission granted. Please scan again.", isError = false)
        } else {
            showStatus("❌ Location permission is required to verify attendance.", isError = true)
        }
    }
}