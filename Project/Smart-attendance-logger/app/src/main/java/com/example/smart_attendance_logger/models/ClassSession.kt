package com.example.smart_attendance_logger.models

data class ClassSession(
    val sessionId: String = "",
    val teacherId: String = "",
    val className: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val radiusMeters: Double = 50.0,
    val timestamp: Long = 0L,
    val token: String = "",
    val expiryTime: Long = 0L // random token embedded in QR
)