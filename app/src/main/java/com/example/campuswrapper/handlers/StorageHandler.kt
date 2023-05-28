package com.example.campuswrapper.handlers

import android.app.Activity
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.campuswrapper.MainActivity
import com.example.campuswrapper.structure.lectures.Lecture
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStreamReader
import java.lang.reflect.Type

object StorageHandler {
    private const val file_detailed_lecturs = "detailed_lectures"

    var activity: MainActivity? = null
    val detailedLectures = ArrayList<Lecture>()

    fun storeDetailedLectures(){
        if(activity == null) {
            Log.w(LogHandler.appStorageTag, "Aborting store-process, because the activity property has not been set!")
            return;
        }

        val data = Gson().toJson(detailedLectures)
        storeLocal(file_detailed_lecturs, data)
    }

    fun getLocalDetailedLectures(): ArrayList<Lecture> {
        if(activity == null){
            Log.w(LogHandler.appStorageTag, "Aborting local-storage-retrieval because the activity property has not been set!")
            return detailedLectures
        }

        val typeOfHashMap: Type = object : TypeToken<ArrayList<Lecture>>() {}.type

        val data = getLocal(file_detailed_lecturs)
        val lectures = Gson().fromJson(data, typeOfHashMap) as ArrayList<Lecture>
        Log.d(LogHandler.appStorageTag, "Loaded ${lectures.size} lectures from local-storage")

        if(detailedLectures.size == 0) detailedLectures.addAll(lectures)

        return lectures
    }

    fun retrieveDetailedLectures(): ArrayList<Lecture>{
        if(detailedLectures.size == 0) return getLocalDetailedLectures()
        return detailedLectures
    }

    private fun getLocal(file: String) : String?{
        if(activity == null){
            Log.w(LogHandler.appStorageTag, "Aborting local-storage-retrieval because the activity property has not been set!")
            return null
        }

        val fileName = "$file.json"
        val stringBuffer = StringBuffer()
        val fileInputStream: FileInputStream

        try {
            fileInputStream = activity!!.openFileInput(fileName)
            val inputStreamReader = InputStreamReader(fileInputStream)
            val bufferedReader = BufferedReader(inputStreamReader)
            var line: String?
            while (bufferedReader.readLine().also { line = it } != null) {
                stringBuffer.append(line)
            }
            fileInputStream.close()
            Log.d(LogHandler.appStorageTag, "Loaded locally stored data")

            return stringBuffer.toString()
        } catch (e: Exception) {
            Log.e(LogHandler.appStorageTag, "Storing data failed: ${e.message}!")
        }
        return null
    }

    private fun storeLocal(file: String, data: Any) : Boolean{
        if(activity == null){
            Log.w(LogHandler.appStorageTag, "Aborting local-storing process because the activity property has not been set!")
            return false
        }

        val fileName = "$file.json"
        val outputStream: FileOutputStream
        try {
            outputStream = activity!!.openFileOutput(fileName, AppCompatActivity.MODE_PRIVATE)
            outputStream.write(data.toString().toByteArray())
            outputStream.close()
            Log.d(LogHandler.appStorageTag, "Successfully saved data in local-storage!")
            return true
        } catch (e: Exception) {
            Log.e(LogHandler.appStorageTag, "Storing data in local-storage failed!")
            e.printStackTrace()
        }
        return false;
    }
}