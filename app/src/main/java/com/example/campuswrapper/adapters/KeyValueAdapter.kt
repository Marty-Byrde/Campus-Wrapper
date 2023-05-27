package com.example.campuswrapper.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.campuswrapper.R

class KeyValueAdapter(val context: Context, val map: HashMap<String, ArrayList<String>>): RecyclerView.Adapter<KeyValueAdapter.ViewHolder>() {

    private lateinit var parent: ViewGroup;
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_lecture_key_value, parent, false)
        this.parent = parent
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n", "ClickableViewAccessibility")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val key = map.keys.elementAt(position);
        val values = map[key]

        holder.txtKey.text = key
        holder.txtValue.text = values?.joinToString("\n\n") ?: "N / A"
    }

    override fun getItemCount(): Int {
        return map.size
    }

    override fun getItemViewType(position: Int): Int {
        return 0
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var txtKey: TextView
        var txtValue: TextView

        init {
            txtKey = view.findViewById(R.id.txtKeyValueHeading)
            txtValue = view.findViewById(R.id.txtValueField)
        }
    }

}