package com.example.campuswrapper

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import com.example.campuswrapper.structure.fetch.Handler
import com.example.campuswrapper.structure.fetch.SearchCriteria
import com.example.campuswrapper.structure.fetch.SemesterType
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //? Temporary workaround, Threading will be implemented later on
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        // fetch detailed lectures:
        val basicLectures = Handler.fetchLectures(SearchCriteria(2022, SemesterType.SUMMER, 687))
        val executor = ThreadPoolExecutor(10, 10, 0L, TimeUnit.MILLISECONDS, LinkedBlockingQueue())
        basicLectures?.forEach { l ->
            executor.execute {
                Handler.retrieveLectureDetails(this, l)
            }
        }
        executor.shutdown()
        executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS)


        return;
        val exams = Handler.fetchExams(SearchCriteria(2022, SemesterType.SUMMER, 687))
        Log.d("Fetch-Campus", "MainActivity received ${exams.size} lectures")

        exams.forEach{e -> Log.d("Fetch-Campus-Exam", "${e.lecture_ID} ${e.lecture_Name}")}

        return;
        val lectures = Handler.fetchLectures(SearchCriteria(2022, SemesterType.SUMMER, 687))
        Log.d("Fetch-Campus", "MainActivity received ${lectures?.size} lectures")
        Log.d("Fetch-Campus", lectures?.get(0).toString())
    }
}