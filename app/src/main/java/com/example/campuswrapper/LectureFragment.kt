package com.example.campuswrapper

import android.annotation.SuppressLint
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
import com.example.campuswrapper.adapters.ContributorAdapter
import com.example.campuswrapper.adapters.CurricularAdapter
import com.example.campuswrapper.adapters.KeyValueAdapter
import com.example.campuswrapper.adapters.SessionAdapter
import com.example.campuswrapper.handlers.LayoutHandler
import com.example.campuswrapper.handlers.LogHandler
import com.example.campuswrapper.structure.lectures.Lecture
import com.google.gson.Gson

class LectureFragment : AppCompatActivity() {
    private lateinit var lecture: Lecture;
    private lateinit var sessionContainer: View;
    private lateinit var descriptionContainer: View;
    private lateinit var examInfoContainer: View;

    private val maxContainerHeight = 1200

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_lecture)
        sessionContainer = findViewById(R.id.lectureSessionContainer)
        descriptionContainer = findViewById(R.id.lectureDescriptions)
        examInfoContainer = findViewById(R.id.lectureExamInformation)

        sessionContainer.visibility = View.GONE
        descriptionContainer.visibility = View.GONE
        examInfoContainer.visibility = View.GONE

        lecture = Gson().fromJson(intent.getStringExtra("lecture"), Lecture::class.java)
        fillLayouts()
    }

    fun fillLayouts(){
        Log.d(LogHandler.appLayoutTag, "Filling in lecture details ${lecture.name}")
        fillInBasicInformations()
        fillInSessions()
        fillInKeyValues(lecture.description, descriptionContainer)
        fillInKeyValues(lecture.examInformation, examInfoContainer)
        fillInCurricular()
    }

    private fun fillInBasicInformations(){
        val txtName = findViewById<TextView>(R.id.txtLectureNameDetails)
        txtName.text = lecture.name

        val contributors = lecture.contributors
        val recycleContributors = findViewById<RecyclerView>(R.id.recContributorsBasic)
        recycleContributors.layoutManager = LinearLayoutManager(this)
        recycleContributors.adapter = ContributorAdapter(this, contributors)

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

    private fun fillInSessions(){
        val sessions = lecture.sessions
        if(sessions?.size == 0) return

        sessionContainer.visibility = View.VISIBLE;

        val recycleSessions = findViewById<RecyclerView>(R.id.recycleSessionContainer)
        recycleSessions.layoutManager = LinearLayoutManager(this)
        recycleSessions.adapter = SessionAdapter(this, sessions!!)
        LayoutHandler.calculateDimensions(sessionContainer)

        //* Set the height of the sessionContainer to 600 if it is larger than 600
        if(sessionContainer.measuredHeight > maxContainerHeight) LayoutHandler.setDimensions(sessionContainer, maxContainerHeight)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun fillInKeyValues(map: HashMap<String, ArrayList<String>>?, container: View){
        if(map?.keys == null || map.keys.size == 0) return;
        val recycleKeyValue = container.findViewById<RecyclerView>(R.id.recyclerKeyValueContainer);

        container.visibility = View.VISIBLE;
        recycleKeyValue.layoutManager = LinearLayoutManager(this)
        recycleKeyValue.adapter = KeyValueAdapter(this, map)

        LayoutHandler.calculateDimensions(container)
        if(container.measuredHeight > maxContainerHeight) LayoutHandler.setDimensions(container, maxContainerHeight)
    }

    private fun fillInCurricular(){
        val curriculars = lecture.curricularPositions;
        if(curriculars?.size == 0) return;

        val recylceCurricular = findViewById<RecyclerView>(R.id.recyclCurricularEntries)
        recylceCurricular.layoutManager = LinearLayoutManager(this)
        recylceCurricular.adapter = CurricularAdapter(this, curriculars!!)
    }
}