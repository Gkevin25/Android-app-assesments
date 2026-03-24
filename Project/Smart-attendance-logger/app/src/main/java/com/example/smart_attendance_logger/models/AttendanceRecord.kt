package com.example.smart_attendance_logger.models

data class AttendanceRecord(
    val recordId: String = "",
    val sessionId: String = "",
    val studentId: String = "",
    val studentName: String = "",
    val timestamp: Long = 0L,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val verified: Boolean = false
)