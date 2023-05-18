package com.example.campuswrapper.structure.exam

import java.util.*
class Exam(
    val lecture_ID: String,
    val lecture_Name: String,
    val date: Date,
    val end: Date,
    val location: String,
    val notes: ExamNotes,
    val mode: ExamMode,
    val href: String
)
