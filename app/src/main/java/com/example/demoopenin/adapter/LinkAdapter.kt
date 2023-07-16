package com.example.demoopenin.adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.recyclerview.widget.RecyclerView


class LinkAdapter<T>(
    private val dataList: List<T>,
    private val itemLayoutId: Int,
    private val bindData: (view: View, item: T) -> Unit
) : RecyclerView.Adapter<LinkAdapter<T>.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(itemLayoutId, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = dataList[position]
        bindData(holder.itemView, item)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }
}

