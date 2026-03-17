package com.example.smart_attendance_logger.teacher

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.smart_attendance_logger.databinding.ActivityAttendanceListBinding
import com.example.smart_attendance_logger.databinding.ItemAttendanceBinding
import com.example.smart_attendance_logger.models.AttendanceRecord
import java.text.SimpleDateFormat
import java.util.*

class AttendanceListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAttendanceListBinding
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val records = mutableListOf<AttendanceRecord>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAttendanceListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rvAttendance.layoutManager = LinearLayoutManager(this)
        binding.rvAttendance.adapter = AttendanceAdapter(records)

        loadAttendance()
    }

    private fun loadAttendance() {
        val teacherId = auth.currentUser?.uid ?: return

        // Get all sessions belonging to this teacher
        db.collection("sessions")
            .whereEqualTo("teacherId", teacherId)
            .get()
            .addOnSuccessListener { sessions ->
                val sessionIds = sessions.documents.map { it.id }
                if (sessionIds.isEmpty()) return@addOnSuccessListener

                // Get attendance records for those sessions
                db.collection("attendance")
                    .whereIn("sessionId", sessionIds)
                    .get()
                    .addOnSuccessListener { docs ->
                        records.clear()
                        for (doc in docs) {
                            doc.toObject(AttendanceRecord::class.java).let { records.add(it) }
                        }
                        records.sortByDescending { it.timestamp }
                        binding.rvAttendance.adapter?.notifyDataSetChanged()
                    }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to load records", Toast.LENGTH_SHORT).show()
            }
    }

    // Adapter defined inside the same file for simplicity
    inner class AttendanceAdapter(private val data: List<AttendanceRecord>)
        : RecyclerView.Adapter<AttendanceAdapter.VH>() {

        inner class VH(val b: ItemAttendanceBinding) : RecyclerView.ViewHolder(b.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            VH(ItemAttendanceBinding.inflate(LayoutInflater.from(parent.context), parent, false))

        override fun getItemCount() = data.size

        override fun onBindViewHolder(holder: VH, position: Int) {
            val record = data[position]
            val sdf = SimpleDateFormat("dd MMM yyyy  HH:mm", Locale.getDefault())

            holder.b.tvStudentName.text = record.studentName
            holder.b.tvClassName.text = "Session: ${record.sessionId.take(8)}..."
            holder.b.tvTime.text = sdf.format(Date(record.timestamp))

            if (record.verified) {
                holder.b.tvVerified.text = "✅ GPS Verified"
                holder.b.tvVerified.setTextColor(getColor(android.R.color.holo_green_dark))
            } else {
                holder.b.tvVerified.text = "❌ Not Verified"
                holder.b.tvVerified.setTextColor(getColor(android.R.color.holo_red_dark))
            }
        }
    }
}