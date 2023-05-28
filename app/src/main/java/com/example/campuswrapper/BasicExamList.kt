package com.example.campuswrapper

import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.campuswrapper.adapters.ExamListAdapter
import com.example.campuswrapper.structure.exam.Exam
import com.example.campuswrapper.structure.fetch.Handler
import com.example.campuswrapper.structure.fetch.SearchCriteria
import com.example.campuswrapper.structure.fetch.SemesterType
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar

class BasicExamList : AppCompatActivity() {
    private var baseExams : ArrayList<Exam>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_basic_exam_list)

        val header = findViewById<TextView>(R.id.txtHeading)
        header.text = "List of exams"

        val btnSearchMenu = findViewById<FloatingActionButton>(R.id.btnOpenSearchComponent)
        btnSearchMenu.visibility = View.INVISIBLE

        btnSearchMenu.setOnClickListener {
            createPopup()
        }

        Thread {
            baseExams = Handler.fetchExams(SearchCriteria(2022, SemesterType.SUMMER, 687))
            if(baseExams != null){
                Log.d("Campus-Layout", "Fetched Basic Exams: ${baseExams!!.size}")
                runOnUiThread {
                    showExams(baseExams!!)
                    btnSearchMenu.visibility = View.VISIBLE
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

    private fun createPopup(){
        val popUpConstraintLayout = findViewById<ConstraintLayout>(R.id.search_popup_container)
        val view: View = LayoutInflater.from(this).inflate(R.layout.app_search_component_popup, popUpConstraintLayout)

        val btnSearch = view.findViewById<Button>(R.id.btnSearch_Search_Popup)
        val btnCancel = view.findViewById<Button>(R.id.btnCancel_Search_Popup)
        val txtInput = view.findViewById<EditText>(R.id.txtInput_Search_Popup)

        val builder = AlertDialog.Builder(this)
        builder.setView(view)
        val alertDialog = builder.create()

        btnSearch.setOnClickListener { view1: View? ->
            Log.d("Campus-Layout", "Searching for ${txtInput.text}")
            if(txtInput.text.isBlank()){
                showExams(baseExams!!)
                alertDialog.dismiss()
                return@setOnClickListener
            }

            // check if the search matches a name, id or date, location, mode or notes
            val filtered = baseExams!!.filter {
                it.lecture_Name.contains(txtInput.text, true) ||
                        it.lecture_ID.contains(txtInput.text, true) ||
                        it.date.toString().contains(txtInput.text, true) ||
                        it.mode.toString().contains(txtInput.text, true) ||
                        it.notes.toString().contains(txtInput.text, true) ||
                        it.location.contains(txtInput.text, true)
            }

            Log.v("Campus-Layout", "There are filtered exams: ${filtered.size}")
            Log.d("Campus-Layout", "Filtering by: ${txtInput.text}")

            if(filtered.isNotEmpty()){
                showExams(filtered as ArrayList<Exam>)
            }else{
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