package com.example.campuswrapper.structure.exam

import java.util.*
class Exam(
    val lecture_ID: Int,
    val lecture_Name: Int,
    val date: Date,
    val location: String,
    val notes: ExamNotes,
    val mode: ExamMode,
    val href: String
)
