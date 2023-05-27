package com.example.campuswrapper

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.campuswrapper.adapters.LectureListAdapter
import com.example.campuswrapper.structure.fetch.Handler
import com.example.campuswrapper.structure.fetch.SearchCriteria
import com.example.campuswrapper.structure.fetch.SemesterType
import com.example.campuswrapper.structure.lectures.Lecture
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson

class BasicLectureList : AppCompatActivity() {
    var selection: Lecture? = null;
    private var fetchedSelection: Lecture? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_basic_lecture_list)

        val header_component = findViewById<TextView>(R.id.txtHeading)
        header_component.text = "Available Lectures"


        Thread {
            val basicLectures = Handler.fetchLectures(SearchCriteria(2022, SemesterType.SUMMER, 687))
            if(basicLectures != null){
                Log.d("Campus-Layout", "Lectures: ${basicLectures.get(0).name}")
                runOnUiThread {
                    showLectures(basicLectures)
                }
            }else{
                runOnUiThread {
                    Snackbar.make(findViewById(R.id.txtHeading), "Failed to fetch lectures!", Snackbar.LENGTH_LONG).show()
                }
            }
        }.start()
    }

    private fun showLectures(lectures: ArrayList<Lecture>) {
        val recycleList = findViewById<RecyclerView>(R.id.recyclerContainerBasicLectures)
        recycleList.layoutManager = LinearLayoutManager(this)
        recycleList.adapter = LectureListAdapter(this, lectures)
    }

    fun updateSelection(lecture: Lecture){
        if(selection == lecture) return;

        selection = lecture;
        Thread {
            Log.d("Campus-Layout", "Start fetching details for ${selection?.name}!")
            val result = Handler.retrieveLectureDetails(this, lecture)
            if(selection?.id == result?.id){ //? check if the selection is still the same
                fetchedSelection = result;
                Log.e("Campus-Layout", "Fetched details for ${result?.name}!")
            }else{
                Log.e("Campus-Layout", "Selection changed! ${selection?.id} vs ${result?.id}")
            }
        }.start()
    }
}