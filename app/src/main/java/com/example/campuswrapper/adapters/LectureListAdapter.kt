package com.example.campuswrapper.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.campuswrapper.BasicLectureList
import com.example.campuswrapper.R
import com.example.campuswrapper.handlers.LogHandler
import com.example.campuswrapper.handlers.StorageHandler
import com.example.campuswrapper.structure.lectures.Lecture
import com.google.android.material.snackbar.Snackbar

class LectureListAdapter(val context: Activity, val lectures: ArrayList<Lecture>): RecyclerView.Adapter<LectureListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_lecture_basic, parent, false)

        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n", "ResourceType")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val lecture = lectures[position];
        holder.txtID.text = lecture.id
        holder.txtType.text = lecture.type.toString()
        holder.txtName.text = lecture.name
        holder.txtContributors.text = lecture.contributors.joinToString(",") { it -> "${it.firstName} ${it.lastName}" }

        val listener = View.OnClickListener {
            Log.d(LogHandler.appLayoutTag, "Clicked on Lecture ${lecture.id}")
            val basicLectureListActivity = context as BasicLectureList
            basicLectureListActivity.updateSelection(lecture)

            val snack: Snackbar = Snackbar.make(holder.itemView, "Do you want to continue to the Lecture-Details?", Snackbar.LENGTH_INDEFINITE)
            snack.setAction("Open") {
                basicLectureListActivity.openDetails()
                snack.dismiss()
            }
            
            snack.setBackgroundTint(Color.parseColor(context.applicationContext.getString(R.color.white)))
            snack.setTextColor(Color.parseColor(context.applicationContext.getString(R.color.teal_200)))

            Thread {
                if(StorageHandler.retrieveDetailedLectures().size == 0) Thread.sleep(200)
                context.runOnUiThread{
                    snack.show()
                }
            }.start()
        }

        holder.container.setOnClickListener(listener)
    }

    override fun getItemCount(): Int {
        return lectures.size
    }

    override fun getItemViewType(position: Int): Int {
        return 0
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var container: ConstraintLayout
        var txtID: TextView
        var txtType: TextView
        var txtName: TextView
        var txtContributors: TextView

        init {
            container = view.findViewById(R.id.lecture_list_item_container)
            txtID = view.findViewById(R.id.txtLectureID_ListElement)
            txtType = view.findViewById(R.id.txtLectureType_ListElement)
            txtName = view.findViewById(R.id.txtLectureName_ListElement)
            txtContributors = view.findViewById(R.id.txtLectureContributors_ListElement)
        }
    }

}