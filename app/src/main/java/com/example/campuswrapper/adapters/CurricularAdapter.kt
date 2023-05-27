package com.example.campuswrapper.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.campuswrapper.R
import com.example.campuswrapper.structure.lectures.Curricular

class CurricularAdapter(val context: Activity, val curriclarEntries: ArrayList<Curricular>): RecyclerView.Adapter<CurricularAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_lecture_curricular, parent, false)

        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val curricular = curriclarEntries[position];

        holder.txtCurricularHeading.text = curricular.study.split("(")[0]
        holder.txtCurricularCategory.text = curricular.topic
        holder.txtCurricularSection.text = curricular.subTopic
        holder.txtCurricularLecture.text = curricular.lectureID

        if(!curricular.suggestion.isNullOrBlank()) {
            holder.txtCurricularSuggestion.visibility = View.VISIBLE
            holder.txtCurricularSuggestion.text = curricular.suggestion
        }
    }

    override fun getItemCount(): Int {
        return curriclarEntries.size
    }

    override fun getItemViewType(position: Int): Int {
        return 0
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var txtCurricularHeading: TextView
        var txtCurricularCategory: TextView
        var txtCurricularSection: TextView
        var txtCurricularLecture: TextView
        var txtCurricularSuggestion: TextView

        init {
            txtCurricularHeading = view.findViewById(R.id.txtCurricularHeading)
            txtCurricularCategory = view.findViewById(R.id.txtCurricularCategory)
            txtCurricularSection = view.findViewById(R.id.txtCurricularSection)
            txtCurricularLecture = view.findViewById(R.id.txtCurricularLecture)
            txtCurricularSuggestion = view.findViewById(R.id.txtCurricularSuggestion)

            txtCurricularSuggestion.visibility = View.GONE
        }
    }

}