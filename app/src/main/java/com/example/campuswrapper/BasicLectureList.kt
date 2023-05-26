package com.example.campuswrapper

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class BasicLectureList : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_basic_lecture_list)

        val header_component= findViewById<TextView>(R.id.txtHeading)
        header_component.text = "Available Lectures"

    }
}