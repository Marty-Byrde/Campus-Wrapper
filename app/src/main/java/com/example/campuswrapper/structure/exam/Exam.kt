package com.example.campuswrapper.structure.exam

import java.util.*
class Exam(
    val lecture_ID: String,
    val lecture_Name: String,
    val start: Date,
    val end: Date,
    val location: String,
    val notes: ExamNotes,
    val mode: ExamMode,
    val href: String
){
    override fun toString(): String {
        return "Exam(lecture_ID='$lecture_ID', lecture_Name='$lecture_Name', start=$start, end=$end, location='$location', notes=$notes, mode=$mode, href='$href')"
    }
}
