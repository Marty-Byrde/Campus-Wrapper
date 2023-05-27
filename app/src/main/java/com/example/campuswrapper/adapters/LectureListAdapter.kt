package com.example.campuswrapper.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.campuswrapper.R
import com.example.campuswrapper.structure.lectures.Lecture

class LectureListAdapter(val context: Activity, val lectures: ArrayList<Lecture>): RecyclerView.Adapter<LectureListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_lecture_basic, parent, false)

        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val lecture = lectures[position];
        holder.txtID.text = lecture.id
        holder.txtType.text = lecture.type.toString()
        holder.txtName.text = lecture.name
        holder.txtContributors.text = lecture.contributors.joinToString(",") { it -> "${it.firstName} ${it.lastName}" }
    }

    override fun getItemCount(): Int {
        return lectures.size
    }

    override fun getItemViewType(position: Int): Int {
        return 0
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var txtID: TextView
        var txtType: TextView
        var txtName: TextView
        var txtContributors: TextView

        init {
            txtID = view.findViewById(R.id.txtLectureID_ListElement)
            txtType = view.findViewById(R.id.txtLectureType_ListElement)
            txtName = view.findViewById(R.id.txtLectureName_ListElement)
            txtContributors = view.findViewById(R.id.txtLectureContributors_ListElement)
        }
    }

}