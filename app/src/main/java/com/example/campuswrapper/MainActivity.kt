package com.example.campuswrapper

import android.os.Bundle
import android.os.StrictMode
import android.telecom.Call
import android.util.ArrayMap
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.campuswrapper.structure.fetch.Handler
import com.example.campuswrapper.structure.fetch.SearchCriteria
import com.example.campuswrapper.structure.fetch.SemesterType
import com.example.campuswrapper.structure.lectures.Lecture
import com.google.gson.Gson
import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Response
import retrofit2.http.POST
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
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
        val detailed = ArrayList<Lecture>()
        val executor = ThreadPoolExecutor(10, 10, 0L, TimeUnit.MILLISECONDS, LinkedBlockingQueue())
        basicLectures?.forEach { l ->
            executor.execute {
                val result = (Handler.retrieveLectureDetails(this, l))
                detailed.add(result)
            }
        }
        executor.shutdown()
        executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS)

        Log.d("Fetch-Campus", "MainActivity finished detail retrieval!")
        Log.d("Fetch-Campus", "")

        Log.d("Fetch-Campus", "There are ${detailed.size} lectures in total")

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