package com.simprints.simprintsidtester.fragments.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import com.simprints.simprintsidtester.BR

class RecyclerViewAdapter<T>(val viewModel: T, val layout: Int) :
    RecyclerView.Adapter<RecyclerViewAdapter.GenericViewHolder<T>>() where T : ViewModel, T : ViewModelForAdapter {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenericViewHolder<T> {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding: ViewDataBinding =
            DataBindingUtil.inflate(layoutInflater, layout, parent, false)

        return GenericViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GenericViewHolder<T>, position: Int) {
        holder.bind(viewModel, position)
    }

    override fun getItemCount(): Int = viewModel.getCount()

    class GenericViewHolder<T>(private val binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(viewModel: T, position: Int) {
            binding.setVariable(BR.viewModel, viewModel)
            binding.setVariable(BR.position, position)
            binding.executePendingBindings()
        }
    }
}

interface ViewModelForAdapter {
    fun getCount(): Int
}