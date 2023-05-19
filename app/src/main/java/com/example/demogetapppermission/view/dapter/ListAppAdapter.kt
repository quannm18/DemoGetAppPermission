package com.example.demogetapppermission.view.dapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.demogetapppermission.databinding.ItemAppBinding
import com.example.demogetapppermission.model.JunkClean

class ListAppAdapter(private val action: (JunkClean) -> Unit) : PagingDataAdapter<JunkClean, ListAppAdapter.AppViewHolder>(Diff()) {

    class AppViewHolder(var binding: ItemAppBinding, var action: (JunkClean) -> Unit) : ViewHolder(binding.root) {

        fun bind(junkClean: JunkClean) {

            binding.junkClean = junkClean
            itemView.setOnClickListener { action(junkClean) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
        return AppViewHolder(ItemAppBinding.inflate(LayoutInflater.from(parent.context), parent, false), action)
    }

    override fun onBindViewHolder(holder: AppViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it)
        }
    }

    class Diff : DiffUtil.ItemCallback<JunkClean>() {
        override fun areItemsTheSame(oldItem: JunkClean, newItem: JunkClean) = oldItem.packageName == newItem.packageName

        override fun areContentsTheSame(oldItem: JunkClean, newItem: JunkClean) = oldItem == newItem
    }
}