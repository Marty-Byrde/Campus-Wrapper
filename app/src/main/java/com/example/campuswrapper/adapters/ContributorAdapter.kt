package com.example.campuswrapper.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.campuswrapper.LectureContributor
import com.example.campuswrapper.R

class ContributorAdapter(val context: Activity, val contributors: ArrayList<LectureContributor>): RecyclerView.Adapter<ContributorAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_lecture_contributor, parent, false)

        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val contributor = contributors[position];
        holder.txtName.text = "${contributor.firstName} ${contributor.lastName}"
        if(contributor.imageRef != null && !contributor.imageRef.contains("images/card/keinbild.jpg")){
            Glide.with(context).load(contributor.imageRef).into(holder.imageView);
        }
    }

    override fun getItemCount(): Int {
        return contributors.size
    }

    override fun getItemViewType(position: Int): Int {
        return 0
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var txtName: TextView
        var imageView: ImageView

        init {
            txtName = view.findViewById(R.id.txtContributorName)
            imageView = view.findViewById(R.id.imgContributorImage)
        }
    }

}