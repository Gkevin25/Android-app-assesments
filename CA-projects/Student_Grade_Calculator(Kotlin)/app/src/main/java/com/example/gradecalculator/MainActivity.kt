package com.example.gradecalculator

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gradecalculator.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var students: List<Student> = emptyList()

    private val pickFileLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let { uri -> loadExcelFile(uri) }
            }
        }

    private val saveFileLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let { uri -> saveOutputFile(uri) }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.btnPickFile.setOnClickListener { openFilePicker() }
        binding.btnExport.setOnClickListener { openSavePicker() }
    }

    private fun openFilePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "*/*"
            putExtra(
                Intent.EXTRA_MIME_TYPES,
                arrayOf(
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                    "application/vnd.ms-excel"
                )
            )
        }
        pickFileLauncher.launch(intent)
    }

    private fun openSavePicker() {
        if (students.isEmpty()) {
            Toast.makeText(this, "Please load an Excel file first.", Toast.LENGTH_SHORT).show()
            return
        }
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
            putExtra(Intent.EXTRA_TITLE, "grades_output.xlsx")
        }
        saveFileLauncher.launch(intent)
    }

    private fun loadExcelFile(uri: Uri) {
        showLoading(true)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val loaded = ExcelManager.readStudentsFromExcel(this@MainActivity, uri)
                withContext(Dispatchers.Main) {
                    students = loaded
                    showLoading(false)
                    if (students.isEmpty()) {
                        Toast.makeText(this@MainActivity,
                            "No student data found. Check that row 1 is a header and data starts from row 2.",
                            Toast.LENGTH_LONG).show()
                    } else {
                        binding.tvStatus.text = "${students.size} student(s) loaded."
                        binding.recyclerView.adapter = StudentAdapter(students)
                        binding.btnExport.visibility = View.VISIBLE
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showLoading(false)
                    Toast.makeText(this@MainActivity,
                        "Error reading file: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun saveOutputFile(uri: Uri) {
        showLoading(true)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                contentResolver.openOutputStream(uri)?.use { outputStream ->
                    ExcelManager.writeGradesToExcel(students, outputStream)
                }
                withContext(Dispatchers.Main) {
                    showLoading(false)
                    Toast.makeText(this@MainActivity,
                        "Grades exported successfully!", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showLoading(false)
                    Toast.makeText(this@MainActivity,
                        "Export failed: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun showLoading(loading: Boolean) {
        binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        binding.btnPickFile.isEnabled = !loading
        binding.btnExport.isEnabled = !loading
    }
}