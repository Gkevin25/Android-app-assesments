package com.example.gradecalculator

import android.content.Context
import android.net.Uri
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.OutputStream

object ExcelManager {

    fun readStudentsFromExcel(context: Context, uri: Uri): List<Student> {
        val students = mutableListOf<Student>()

        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            val workbook = WorkbookFactory.create(inputStream)
            val sheet = workbook.getSheetAt(0)

            // Row 0 is the header, so we start from row 1
            for (rowIndex in 1..sheet.lastRowNum) {
                val row = sheet.getRow(rowIndex) ?: continue

                // Column A = student name
                val nameCell = row.getCell(0)
                val name = nameCell?.stringCellValue?.trim() ?: continue
                if (name.isBlank()) continue

                // Column B = mark (could be a number or text)
                val markCell = row.getCell(1)
                val mark: Int? = when (markCell?.cellType) {
                    CellType.NUMERIC -> markCell.numericCellValue.toInt()
                    CellType.STRING  -> markCell.stringCellValue.trim().toIntOrNull()
                    else             -> null
                }

                students.add(Student(name = name, mark = mark))
            }

            workbook.close()
        }

        return students
    }

    fun writeGradesToExcel(students: List<Student>, outputStream: OutputStream) {
        val workbook = XSSFWorkbook()
        val sheet = workbook.createSheet("Grades")

        // Header row
        val headerRow = sheet.createRow(0)
        val boldStyle = workbook.createCellStyle().apply {
            val font = workbook.createFont()
            font.bold = true
            setFont(font)
        }
        headerRow.createCell(0).apply {
            setCellValue("Student Name")
            cellStyle = boldStyle
        }
        headerRow.createCell(1).apply {
            setCellValue("Grade")
            cellStyle = boldStyle
        }

        // One row per student
        students.forEachIndexed { index, student ->
            val row = sheet.createRow(index + 1)
            row.createCell(0).setCellValue(student.name)
            row.createCell(1).setCellValue(student.grade)
        }

        sheet.autoSizeColumn(0)
        sheet.autoSizeColumn(1)

        workbook.write(outputStream)
        workbook.close()
    }
}