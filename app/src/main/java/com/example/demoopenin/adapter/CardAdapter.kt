package com.example.demoopenin.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.demoopenin.R
import com.google.android.material.card.MaterialCardView

class CardAdapter(val data: List<String>, val clickListener: (String) -> Unit) :
    RecyclerView.Adapter<CardAdapter.ViewHolder>() {
    private var selectedIndex = -1

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val cardView: MaterialCardView = itemView.findViewById(R.id.card_view)
        private val textView: TextView = itemView.findViewById(R.id.text_view)

        init {
            cardView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val previousSelectedIndex = selectedIndex
                    selectedIndex = position
                    notifyItemChanged(previousSelectedIndex)
                    notifyItemChanged(selectedIndex)
                    if (previousSelectedIndex != position) {
                        clickListener(data[position])
                    }
                }
            }
        }

        fun bind(item: String, position: Int) {
            textView.text = item
            if (position == selectedIndex) {
                textView.setTextColor(Color.WHITE)
                textView.setBackgroundColor(Color.argb(255, 14, 111, 255))
            } else {
                textView.setTextColor(Color.argb(255, 153, 156, 160))
                textView.setBackgroundColor(Color.WHITE)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_tag, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position], position)
    }

    override fun getItemCount(): Int = data.size

    fun setSelectedIndex(index: Int) {
        if (selectedIndex != index) {
            val previousSelectedIndex = selectedIndex
            selectedIndex = index
            notifyItemChanged(previousSelectedIndex)
            notifyItemChanged(selectedIndex)
            clickListener(data[index])
        }
    }
}






