package com.simprints.simprintsidtester.fragments.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView

abstract class SimpleListAdapter<T>(@LayoutRes private val rowLayout: Int) :
    RecyclerView.Adapter<SimpleListAdapter.VH>() {
    private val items = ArrayList<T>()

    fun setItems(newItems: List<T>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    abstract fun onBindData(position: Int, viewHolder: RecyclerView.ViewHolder, data: T)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        VH(LayoutInflater.from(parent.context).inflate(rowLayout, parent, false))

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        this.onBindData(position, holder, items[position])
    }

    class VH(itemView: View) : RecyclerView.ViewHolder(itemView)
}