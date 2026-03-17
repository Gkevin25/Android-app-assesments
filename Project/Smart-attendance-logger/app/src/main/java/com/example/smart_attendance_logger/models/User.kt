package com.example.smart_attendance_logger.models

data class User(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val role: String = ""   // "teacher" or "student"
)