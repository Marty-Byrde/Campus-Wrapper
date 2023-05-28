package com.example.campuswrapper

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.campuswrapper.handlers.LogHandler
import com.example.campuswrapper.handlers.StorageHandler
import com.example.campuswrapper.structure.fetch.Handler
import com.example.campuswrapper.structure.fetch.SearchCriteria
import com.example.campuswrapper.structure.fetch.SemesterType
import com.example.campuswrapper.structure.lectures.Lecture
import com.google.gson.Gson
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
        StorageHandler.activity = this

        val header_component= findViewById<TextView>(R.id.txtHeading)
        header_component.text = "Campus Wrapper"

        val btnLectures = findViewById<Button>(R.id.btnOpenLectureList)
        btnLectures.setOnClickListener{
            val i = Intent(this, BasicLectureList::class.java)
            startActivity(i)
        }

        val btnExams = findViewById<Button>(R.id.btnOpenExamList)
        btnExams.setOnClickListener{
            val i = Intent(this, BasicExamList::class.java)
            startActivity(i)
        }

        Thread {
            // Stop further retrievals as they have already been retrieved before.
            if(StorageHandler.retrieveDetailedLectures().size > 0) {
                Log.v(LogHandler.appFetchTag, "Aborting lecture-detail retrieval, as they are already retrieved!")
                return@Thread
            }

            // fetch detailed lectures:
            val basicLectures = Handler.fetchLectures(SearchCriteria(2022, SemesterType.SUMMER, 687))
            val detailed = ArrayList<Lecture>()
            val executor = ThreadPoolExecutor(10, 10, 0L, TimeUnit.MILLISECONDS, LinkedBlockingQueue())
            basicLectures?.forEach { l ->
                executor.execute {
                    var result = (Handler.retrieveLectureDetails(this, l))

                    //* second attempt
                    if(result == null) result = Handler.retrieveLectureDetails(this, l)

                    result?.let { detailed.add(it) }
                }
            }
            executor.shutdown()
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS)

            if(detailed.size > 0) {
                StorageHandler.detailedLectures.clear()
                StorageHandler.detailedLectures.addAll(detailed)

                Log.d(LogHandler.appFetchTag, "MainActivity finished detail retrieval!")
                StorageHandler.storeDetailedLectures()
                Log.v(LogHandler.appFetchTag, "Detailed Lecturs have successully been saved to the local storage!")
            }
            else {
                Log.v(LogHandler.appFetchTag, "No lectures were retrieved!")
                detailed.addAll(StorageHandler.retrieveDetailedLectures())
            }

            sendAPI(Gson().toJson(detailed))

            Log.d(LogHandler.appFetchTag, "There are ${detailed.size} lectures in total")
        }.start()
    }

    private fun sendAPI(data: Any){
        Thread {
            val url = URL("http://10.0.2.2/results")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/json")
            connection.doOutput = true

            val outputStreamWriter = OutputStreamWriter(connection.outputStream)
            outputStreamWriter.write(data.toString())
            outputStreamWriter.flush()

            val responseCode = connection.responseCode
            Log.d(LogHandler.appAPISendTag, "API responded with $responseCode")
        }.start()
    }
}