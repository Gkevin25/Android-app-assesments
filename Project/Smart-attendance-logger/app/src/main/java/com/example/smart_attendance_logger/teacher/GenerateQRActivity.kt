package com.example.smart_attendance_logger.teacher

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.example.smart_attendance_logger.databinding.ActivityGenerateQrBinding
import com.example.smart_attendance_logger.models.ClassSession
import java.util.UUID

class GenerateQRActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGenerateQrBinding
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private var classLatitude = 0.0
    private var classLongitude = 0.0
    private var locationCaptured = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGenerateQrBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnGetLocation.setOnClickListener {
            captureLocation()
        }

        binding.btnGenerateQR.setOnClickListener {
            val className = binding.etClassName.text.toString().trim()

            if (className.isEmpty()) {
                Toast.makeText(this, "Please enter a class name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (!locationCaptured) {
                Toast.makeText(this, "Please capture your location first", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            createSessionAndShowQR(className)
        }
    }

    private fun captureLocation() {
        // Check permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                100
            )
            return
        }

        val fusedClient = LocationServices.getFusedLocationProviderClient(this)
        fusedClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                classLatitude = location.latitude
                classLongitude = location.longitude
                locationCaptured = true
                binding.tvLocationStatus.text =
                    "✅ Location captured: %.4f, %.4f".format(classLatitude, classLongitude)
                binding.tvLocationStatus.setTextColor(getColor(android.R.color.holo_green_dark))
            } else {
                Toast.makeText(this, "Could not get location. Make sure GPS is on.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun createSessionAndShowQR(className: String) {
        val teacherId = auth.currentUser?.uid ?: return
        val sessionId = UUID.randomUUID().toString()
        val token = UUID.randomUUID().toString().take(8)
        val expiryTime = System.currentTimeMillis() + (5 * 60 * 1000) // 5 minutes from now

        val session = ClassSession(
            sessionId = sessionId,
            teacherId = teacherId,
            className = className,
            latitude = classLatitude,
            longitude = classLongitude,
            radiusMeters = 50.0,
            timestamp = System.currentTimeMillis(),
            token = token,
            expiryTime = expiryTime          // ← new field
        )

        db.collection("sessions").document(sessionId).set(session)
            .addOnSuccessListener {
                val qrContent = "$sessionId|$token"
                showQRCode(qrContent, className, sessionId, expiryTime)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to create session: ${it.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun showQRCode(content: String, className: String, sessionId: String, expiryTime: Long) {
        try {
            val encoder = BarcodeEncoder()
            val bitmap: Bitmap = encoder.encodeBitmap(content, BarcodeFormat.QR_CODE, 600, 600)

            binding.ivQRCode.setImageBitmap(bitmap)
            binding.ivQRCode.visibility = View.VISIBLE
            binding.tvSessionInfo.visibility = View.VISIBLE

            Toast.makeText(this, "QR Code ready! Show it to students.", Toast.LENGTH_LONG).show()

            // Countdown timer — updates every second
            object : android.os.CountDownTimer(expiryTime - System.currentTimeMillis(), 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    val minutes = millisUntilFinished / 1000 / 60
                    val seconds = (millisUntilFinished / 1000) % 60
                    binding.tvSessionInfo.text =
                        "Class: $className\n" +
                                "Session: ${sessionId.take(8)}...\n" +
                                "⏱ Expires in: %02d:%02d".format(minutes, seconds)
                }

                override fun onFinish() {
                    // Grey out the QR code when expired
                    binding.ivQRCode.alpha = 0.3f
                    binding.tvSessionInfo.text = "⛔ QR Code expired!\nGenerate a new one."
                    binding.tvSessionInfo.setTextColor(getColor(android.R.color.holo_red_dark))
                    binding.btnGenerateQR.isEnabled = true
                    binding.btnGenerateQR.text = "Generate New QR Code"
                }
            }.start()

        } catch (e: com.google.zxing.WriterException) {
            Toast.makeText(this, "Failed to generate QR: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100 && grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            captureLocation()
        } else {
            Toast.makeText(this, "Location permission is required", Toast.LENGTH_SHORT).show()
        }
    }
}