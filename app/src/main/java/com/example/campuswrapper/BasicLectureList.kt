package com.example.campuswrapper

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.campuswrapper.adapters.LectureListAdapter
import com.example.campuswrapper.handlers.StorageHandler
import com.example.campuswrapper.structure.fetch.Handler
import com.example.campuswrapper.structure.fetch.SearchCriteria
import com.example.campuswrapper.structure.fetch.SemesterType
import com.example.campuswrapper.structure.lectures.Lecture
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson

class BasicLectureList : AppCompatActivity() {
    var selection: Lecture? = null
    private var fetchedSelection: Lecture? = null
    private var baseLectures: ArrayList<Lecture>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_basic_lecture_list)

        val headerComponent = findViewById<TextView>(R.id.txtHeading)
        headerComponent.text = "Available Lectures"

        val btnSearchMenu = findViewById<FloatingActionButton>(R.id.btnOpenSearchComponent)
        btnSearchMenu.visibility = View.INVISIBLE

        btnSearchMenu.setOnClickListener {
            createPopup()
        }

        Thread {
            if(StorageHandler.detailedLectures.size > 0){
                Log.d("Campus-Layout", "Using detailed-lectures from the Storage-Handler: ${StorageHandler.detailedLectures.size}")
                runOnUiThread {
                    showLectures(StorageHandler.detailedLectures)
                    btnSearchMenu.visibility = View.VISIBLE
                }
                return@Thread
            }

            baseLectures = Handler.fetchLectures(SearchCriteria(2022, SemesterType.SUMMER, 687))
            if (baseLectures != null) {
                Log.d("Campus-Layout", "Lectures: ${baseLectures!![0].name}")
                runOnUiThread {
                    showLectures(baseLectures!!)
                    btnSearchMenu.visibility = View.VISIBLE
                }
            } else {
                runOnUiThread {
                    Snackbar.make(findViewById(R.id.txtHeading), "Failed to fetch lectures!", Snackbar.LENGTH_INDEFINITE).show()
                }

                Thread.sleep(4000)
                runOnUiThread{
                    startActivity(Intent(this, MainActivity::class.java))
                }
            }
        }.start()
    }

    private fun showLectures(lectures: ArrayList<Lecture>) {
        val recycleList = findViewById<RecyclerView>(R.id.recyclerContainerBasicLectures)
        recycleList.layoutManager = LinearLayoutManager(this)
        recycleList.adapter = LectureListAdapter(this, lectures)
    }

    fun updateSelection(lecture: Lecture) {
        if (selection == lecture) return

        selection = lecture
        fetchedSelection = null
        Thread {
            Log.d("Campus-Layout", "Start fetching details for ${selection?.name}!")
            val result = Handler.retrieveLectureDetails(this, lecture)
            if (selection?.id == result?.id) { //? check if the selection is still the same
                fetchedSelection = result
                Log.e("Campus-Layout", "Fetched details for ${result?.name}!")
            } else {
                Log.e("Campus-Layout", "Selection changed! ${selection?.id} vs ${result?.id}")
            }
        }.start()
    }

    fun openDetails() {
        val statusBar = Snackbar.make(findViewById(R.id.txtHeading), "Fetching details...", Snackbar.LENGTH_INDEFINITE)
        statusBar.show()
        Log.d("Campus-Layout", "Open details for ${selection?.name}!")
        var seconds = 0.01
        Thread {
            Thread.sleep(10)
            Log.d("Campus-Layout", "Checking if details for ${selection?.name} are available!")
            while (fetchedSelection == null) {
                Thread.sleep(10)
                runOnUiThread { statusBar.setText("Fetching details... (${String.format("%.2f", seconds)}s)") }

                if (seconds >= 15) {
                    runOnUiThread {
                        statusBar.setText("Sorry! Could not fetch details for this lecture.")
                    }
                    Log.e("Campus-Layout", "Waiting timed out after $seconds seconds!")
                    return@Thread
                }
                seconds += 0.01
            }

            runOnUiThread {
                Log.d("Campus-Layout", "Transitioing and displaying: (${fetchedSelection?.name}s)")
                if (fetchedSelection == null) return@runOnUiThread

                statusBar.dismiss()

                val i = Intent(this, LectureFragment::class.java)
                i.putExtra("lecture", Gson().toJson(fetchedSelection))
                startActivity(i)
            }

        }.start()

    }

    private fun createPopup() {
        val popUpConstraintLayout = findViewById<ConstraintLayout>(R.id.search_popup_container)
        val view: View = LayoutInflater.from(this).inflate(R.layout.app_search_component_popup, popUpConstraintLayout)

        val btnSearch = view.findViewById<Button>(R.id.btnSearch_Search_Popup)
        val btnCancel = view.findViewById<Button>(R.id.btnCancel_Search_Popup)
        val txtInput = view.findViewById<EditText>(R.id.txtInput_Search_Popup)

        val builder = AlertDialog.Builder(this)
        builder.setView(view)
        val alertDialog = builder.create()

        btnSearch.setOnClickListener {
            Log.d("Campus-Layout", "Searching for ${txtInput.text}")
            if (txtInput.text.isBlank()) {
                showLectures(baseLectures!!)
                alertDialog.dismiss()
                return@setOnClickListener
            }

            // check if the search matches a name, contributor, id or type
            val filtered = baseLectures!!.filter {
                it.name.contains(txtInput.text, true) ||
                        it.id.contains(txtInput.text, true) ||
                        it.type.toString().contains(txtInput.text, true) ||
                        it.contributors.joinToString(",").contains(txtInput.text, true)

            }

            Log.v("Campus-Layout", "There are filtered lectures: ${filtered.size}")
            Log.d("Campus-Layout", "Filtering by: ${txtInput.text}")

            if (filtered.isNotEmpty()) {
                showLectures(filtered as ArrayList<Lecture>)
            } else {
                Snackbar.make(findViewById(R.id.txtHeading), "Sorry, but your search didnt find anything!", Snackbar.LENGTH_LONG).show()
            }



            alertDialog.dismiss()
        }

        btnCancel.setOnClickListener { alertDialog.dismiss() }

        if (alertDialog.window != null) {
            alertDialog.window!!.setBackgroundDrawable(ColorDrawable(0))
        }
        alertDialog.show()
    }
}