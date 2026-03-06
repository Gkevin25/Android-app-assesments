package com.example.gradecalculator

data class Student(
    val name: String,
    val mark: Int?,
    val grade: String = mark?.let { calculateGrade(it) } ?: "N/A"
)

fun calculateGrade(mark: Int): String = when (mark) {
    in 90..100 -> "A"
    in 80..89  -> "B"
    in 70..79  -> "C"
    in 60..69  -> "D"
    else       -> "F"
}