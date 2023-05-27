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
import com.example.campuswrapper.structure.lectures.LectureSession
import java.text.SimpleDateFormat

@SuppressLint("SimpleDateFormat")
class SessionAdapter(val context: Activity, val sessions: ArrayList<LectureSession>): RecyclerView.Adapter<SessionAdapter.ViewHolder>() {

    private val timeFormatter = SimpleDateFormat("HH:mm")
    private val dateFormatter = SimpleDateFormat("EEE, dd.MM.yyyy")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_lecture_session, parent, false)

        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val session = sessions[position];
        holder.txtType.text = session.type.toString()
        holder.txtDate.text = dateFormatter.format(session.start)
        holder.txtTime.text = "${timeFormatter.format(session.start)} - ${timeFormatter.format(session.end)}"
        holder.txtLocation.text = if(session.onCampus) session.room else "Unknown"

        holder.txtonCampus.text = if(session.onCampus) "On Campus" else "Off Campus"
        if(!session.onCampus){
            holder.imgOnCampusImg.setImageResource(R.drawable.cross_mark)
        }
    }

    override fun getItemCount(): Int {
        return sessions.size
    }

    override fun getItemViewType(position: Int): Int {
        return 0
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var txtType: TextView
        var txtDate: TextView
        var txtTime: TextView
        var txtLocation: TextView
        var txtonCampus: TextView
        var imgOnCampusImg: ImageView

        var _imgDate: ImageView
        var _imgTime: ImageView
        var _imgLocation: ImageView

        init {
            txtType = view.findViewById(R.id.lblSessionType)
            txtDate = view.findViewById(R.id.txtSessionDate)
            txtTime = view.findViewById(R.id.txtSessionTime)
            txtLocation = view.findViewById(R.id.txtSessionLocation)
            txtonCampus = view.findViewById(R.id.txtSessionCampus)
            imgOnCampusImg = view.findViewById(R.id.imageView8)

            _imgDate = view.findViewById(R.id.imageView6)
            _imgTime = view.findViewById(R.id.imageView5)
            _imgLocation = view.findViewById(R.id.imageView7)

            _imgDate.setImageResource(R.drawable.schedule)
            _imgTime.setImageResource(R.drawable.clock)
            _imgLocation.setImageResource(R.drawable.placeholder)
        }
    }

}