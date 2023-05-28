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
import com.example.campuswrapper.BasicExamList
import com.example.campuswrapper.R
import com.example.campuswrapper.structure.exam.Exam
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat

@SuppressLint("SimpleDateFormat")
class ExamListAdapter(val context: Activity, val exams: ArrayList<Exam>): RecyclerView.Adapter<ExamListAdapter.ViewHolder>() {
    private val dateFormatter = SimpleDateFormat("EEE, dd.MM.yyyy")
    private val timeFormatter = SimpleDateFormat("HH:mm")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.exam_list_item_basic, parent, false)

        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n", "ResourceType")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val exam = exams[position];
        holder.txtID.text = exam.lecture_ID
        holder.txtName.text = exam.lecture_Name
        holder.txtDate.text = dateFormatter.format(exam.date)
        holder.txtTime.text = "${timeFormatter.format(exam.end)} - ${timeFormatter.format(exam.end)}"
        holder.txtLocation.text = exam.location


        val listener = View.OnClickListener {
            Log.d("Campus-Layout", "Clicked on Exam for lecture ${exam.lecture_ID}")
            val basicExamListActivity = context as BasicExamList
//            basicExamListActivity.updateSelection(exam)

            val snack: Snackbar = Snackbar.make(holder.itemView, "Do you want to continue to the Exam-Details?", Snackbar.LENGTH_INDEFINITE)
            snack.setAction("Open") { view: View? ->
//                basicExamListActivity.openDetails()
                snack.dismiss()
            }
            
            snack.setBackgroundTint(Color.parseColor(context.applicationContext.getString(R.color.purple_700)))
            snack.setTextColor(Color.parseColor(context.applicationContext.getString(R.color.teal_200)))

            Thread {
                Thread.sleep(200)
                context.runOnUiThread{
                    snack.show()
                }
            }.start()
        }

        holder.container.setOnClickListener(listener)
    }

    override fun getItemCount(): Int {
        return exams.size
    }

    override fun getItemViewType(position: Int): Int {
        return 0
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var container: ConstraintLayout
        var txtID: TextView
        var txtName: TextView
        var txtDate: TextView
        var txtTime: TextView
        var txtLocation: TextView

        init {
            container = view.findViewById(R.id.exam_item_container)
            txtID = view.findViewById(R.id.txtLectureID_ExamListItem)
            txtName = view.findViewById(R.id.txtLectureName_ExamListItem)
            txtDate = view.findViewById(R.id.txtDate_ExamListItem)
            txtTime = view.findViewById(R.id.txtTime_ExamListItem)
            txtLocation = view.findViewById(R.id.txtRoom_ExamListItem)
        }
    }

}