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
        fetchedSelection = null;
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

    fun openDetails(){
        val statusBar = Snackbar.make(findViewById(R.id.txtHeading), "Fetching details...", Snackbar.LENGTH_INDEFINITE)
        statusBar.show()
        Log.d("Campus-Layout", "Open details for ${selection?.name}!")
        var seconds = 0.01;
        Thread{
            Thread.sleep(10)
            Log.d("Campus-Layout", "Checking if details for ${selection?.name} are available!")
            while(fetchedSelection == null){
                Thread.sleep(10)
                runOnUiThread{statusBar.setText("Fetching details... (${String.format("%.2f", seconds)}s)");}

                if(seconds >= 15) {
                    runOnUiThread {
                        statusBar.setText("Sorry! Could not fetch details for this lecture.")
                    }
                    Log.e("Campus-Layout", "Waiting timed out after $seconds seconds!")
                    return@Thread
                }
                seconds += 0.01;
            }

            runOnUiThread {
                Log.d("Campus-Layout", "Transitioing and displaying: (${fetchedSelection?.name}s)")
                if(fetchedSelection == null) return@runOnUiThread

                statusBar.dismiss()

                val i = Intent(this, LectureFragment::class.java)
                i.putExtra("lecture", Gson().toJson(fetchedSelection))
                startActivity(i)
            }

        }.start()

    }
}