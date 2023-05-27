package com.example.campuswrapper.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.campuswrapper.R

class ListViewAdapter(val values: ArrayList<String>): RecyclerView.Adapter<ListViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_lecture_key_value_value, parent, false)

        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val text = values.elementAt(position);
        holder.txtValue.text = text
    }

    override fun getItemCount(): Int {
        return values.size
    }

    override fun getItemViewType(position: Int): Int {
        return 0
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var txtValue: TextView

        init {
            txtValue = view.findViewById(R.id.txtValue_lkkv)
        }
    }

}