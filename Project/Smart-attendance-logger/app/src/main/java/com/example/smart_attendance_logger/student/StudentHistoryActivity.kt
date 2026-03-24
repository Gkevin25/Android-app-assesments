package com.example.smart_attendance_logger.student

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.smart_attendance_logger.databinding.ActivityStudentHistoryBinding
import com.example.smart_attendance_logger.databinding.ItemStudentHistoryBinding
import com.example.smart_attendance_logger.models.AttendanceRecord
import java.text.SimpleDateFormat
import java.util.*

class StudentHistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStudentHistoryBinding
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val records = mutableListOf<Pair<AttendanceRecord, String>>() // record + className

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStudentHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rvHistory.layoutManager = LinearLayoutManager(this)
        binding.rvHistory.adapter = HistoryAdapter(records)

        loadHistory()
    }

    private fun loadHistory() {
        val uid = auth.currentUser?.uid ?: return

        db.collection("attendance")
            .whereEqualTo("studentId", uid)
            .get()
            .addOnSuccessListener { docs ->
                records.clear()
                val attendanceList = docs.map { it.toObject(AttendanceRecord::class.java) }
                    .sortedByDescending { it.timestamp }

                if (attendanceList.isEmpty()) {
                    binding.tvTotalCount.text = "No attendance records yet."
                    binding.rvHistory.adapter?.notifyDataSetChanged()
                    return@addOnSuccessListener
                }

                binding.tvTotalCount.text = "Total sessions attended: ${attendanceList.size}"

                // For each record, look up the class name from the session
                var loaded = 0
                for (record in attendanceList) {
                    db.collection("sessions").document(record.sessionId).get()
                        .addOnSuccessListener { sessionDoc ->
                            val className = sessionDoc.getString("className") ?: "Unknown Class"
                            records.add(Pair(record, className))
                            loaded++
                            if (loaded == attendanceList.size) {
                                records.sortByDescending { it.first.timestamp }
                                binding.rvHistory.adapter?.notifyDataSetChanged()
                            }
                        }
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to load history", Toast.LENGTH_SHORT).show()
            }
    }

    inner class HistoryAdapter(private val data: List<Pair<AttendanceRecord, String>>)
        : RecyclerView.Adapter<HistoryAdapter.VH>() {

        inner class VH(val b: ItemStudentHistoryBinding) : RecyclerView.ViewHolder(b.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            VH(ItemStudentHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false))

        override fun getItemCount() = data.size

        override fun onBindViewHolder(holder: VH, position: Int) {
            val (record, className) = data[position]
            val sdf = SimpleDateFormat("dd MMM yyyy  •  HH:mm:ss", Locale.getDefault())

            holder.b.tvClassName.text = className
            holder.b.tvScanTime.text = "Scanned at: ${sdf.format(Date(record.timestamp))}"

            if (record.verified) {
                holder.b.tvVerifiedBadge.text = "✅ GPS Verified"
                holder.b.tvVerifiedBadge.setTextColor(getColor(android.R.color.holo_green_dark))
            } else {
                holder.b.tvVerifiedBadge.text = "❌ Not Verified"
                holder.b.tvVerifiedBadge.setTextColor(getColor(android.R.color.holo_red_dark))
            }
        }
    }
}