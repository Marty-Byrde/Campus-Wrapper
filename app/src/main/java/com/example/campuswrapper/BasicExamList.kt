package com.example.campuswrapper

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.campuswrapper.adapters.ExamListAdapter
import com.example.campuswrapper.structure.exam.Exam
import com.example.campuswrapper.structure.fetch.Handler
import com.example.campuswrapper.structure.fetch.SearchCriteria
import com.example.campuswrapper.structure.fetch.SemesterType
import com.example.campuswrapper.structure.lectures.Lecture
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson

class BasicExamList : AppCompatActivity() {
    private var selection: Exam? = null;
    private var fetchedSelection: Exam? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_basic_exam_list)

        val header = findViewById<TextView>(R.id.txtHeading)
        header.text = "List of exams"

        Thread {
            val basicExams = Handler.fetchExams(SearchCriteria(2022, SemesterType.SUMMER, 687))
            if(basicExams != null){
                Log.d("Campus-Layout", "Fetched Basic Exams: ${basicExams.size}")
                runOnUiThread {
                    showExams(basicExams)
                }
            }else{
                runOnUiThread {
                    Snackbar.make(findViewById(R.id.txtHeading), "Failed to fetch exams!", Snackbar.LENGTH_LONG).show()
                }
            }
        }.start()
    }

    private fun showExams(exams: ArrayList<Exam>) {
        val recycleList = findViewById<RecyclerView>(R.id.recycleExamLIstBasic)
        recycleList.layoutManager = LinearLayoutManager(this)
        recycleList.adapter = ExamListAdapter(this, exams)
    }


}