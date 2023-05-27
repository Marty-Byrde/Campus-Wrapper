package com.example.campuswrapper

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.marginTop
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.campuswrapper.structure.lectures.Lecture
import com.google.gson.Gson

class LectureFragment : AppCompatActivity() {
    private lateinit var lecture: Lecture;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_lecture)

        lecture = Gson().fromJson(intent.getStringExtra("lecture"), Lecture::class.java)
        fillLayouts()
    }

    fun fillLayouts(){
        Log.d("Campus-Layout", "Filling in lecture details ${lecture.name}")
        val txtName = findViewById<TextView>(R.id.txtLectureNameDetails)
        txtName.text = lecture.name

        val contributors = lecture.contributors
        val recycleContributors = findViewById<RecyclerView>(R.id.recContributorsBasic)
        recycleContributors.layoutManager = LinearLayoutManager(this)
//        recycleContributors.adapter = ContributorsAdapter(contributors)

        val txtType = findViewById<TextView>(R.id.txtTypeBasic)
        val txtEcts = findViewById<TextView>(R.id.txtECTSBasic)
        val txtAvgEff = findViewById<TextView>(R.id.txtEstmEffortBasic)
        val txtLanguage = findViewById<TextView>(R.id.txtLanguageBasic)
        val txtRegistrations = findViewById<TextView>(R.id.txtRegistrationsBasic)
        val txtDepartment = findViewById<TextView>(R.id.txtDepartmentBasic)

        txtType.text = lecture.type.toString()
        txtEcts.text = lecture.ects.toString()
        txtAvgEff.text = lecture.estimatedEffort.toString()
        txtLanguage.text = lecture.rawBasicValues?.get("language")?.toString() ?: "N/A"
        txtRegistrations.text = lecture.registrations.toString()
        txtDepartment.text = lecture.rawBasicValues?.get("organizedBy")?.toString() ?: "N/A"

    }
}