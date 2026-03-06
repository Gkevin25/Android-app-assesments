package com.example.gradecalculator

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class StudentAdapter(private val students: List<Student>) :
    RecyclerView.Adapter<StudentAdapter.StudentViewHolder>() {

    inner class StudentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView  = itemView.findViewById(R.id.tvStudentName)
        val tvMark: TextView  = itemView.findViewById(R.id.tvStudentMark)
        val tvGrade: TextView = itemView.findViewById(R.id.tvStudentGrade)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_student, parent, false)
        return StudentViewHolder(view)
    }

    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        val student = students[position]
        holder.tvName.text  = student.name
        holder.tvMark.text  = student.mark?.toString() ?: "N/A"
        holder.tvGrade.text = student.grade
    }

    override fun getItemCount(): Int = students.size
}