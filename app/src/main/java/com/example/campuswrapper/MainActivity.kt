package com.example.campuswrapper

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import com.example.campuswrapper.structure.fetch.Handler
import com.example.campuswrapper.structure.fetch.SearchCriteria
import com.example.campuswrapper.structure.fetch.SemesterType
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //? Temporary workaround, Threading will be implemented later on
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        val lectures = Handler.fetchLectures(SearchCriteria(2022, SemesterType.SUMMER, 687))
        Log.d("Fetch-Campus", "MainActivity received ${lectures?.size} lectures")
        Log.d("Fetch-Campus", lectures?.get(0).toString())
    }
}