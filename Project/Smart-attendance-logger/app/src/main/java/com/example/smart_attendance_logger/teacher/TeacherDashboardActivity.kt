package com.example.smart_attendance_logger.teacher

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.smart_attendance_logger.auth.LoginActivity
import com.example.smart_attendance_logger.databinding.ActivityTeacherDashboardBinding

class TeacherDashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTeacherDashboardBinding
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTeacherDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Show teacher's name
        val uid = auth.currentUser?.uid ?: return
        db.collection("users").document(uid).get()
            .addOnSuccessListener { doc ->
                val name = doc.getString("name") ?: "Teacher"
                binding.tvWelcome.text = "Welcome, $name"
            }

        binding.btnGenerateQR.setOnClickListener {
            startActivity(Intent(this, GenerateQRActivity::class.java))
        }

        binding.btnViewAttendance.setOnClickListener {
            startActivity(Intent(this, AttendanceListActivity::class.java))
        }

        binding.btnLogout.setOnClickListener {
            auth.signOut()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }
}