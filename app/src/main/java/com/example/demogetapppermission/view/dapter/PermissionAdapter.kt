package com.example.demogetapppermission.view.dapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.demogetapppermission.databinding.ItemPermissionBinding

class PermissionAdapter(private val action: (Pair<String, Boolean>) -> Unit) :
    ListAdapter<Pair<String, Boolean>, PermissionAdapter.AppViewHolder>(Diff()) {

    class AppViewHolder(var binding: ItemPermissionBinding, var action: (Pair<String, Boolean>) -> Unit) : ViewHolder(binding.root) {

        fun bind(data: Pair<String, Boolean>) {

            binding.checkBox.text = data.first
            binding.checkBox.isChecked = data.second
            itemView.setOnClickListener { action(data) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
        return AppViewHolder(ItemPermissionBinding.inflate(LayoutInflater.from(parent.context), parent, false), action)
    }

    override fun onBindViewHolder(holder: AppViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it)
        }
    }

    class Diff : DiffUtil.ItemCallback<Pair<String, Boolean>>() {
        override fun areItemsTheSame(oldItem: Pair<String, Boolean>, newItem: Pair<String, Boolean>) = oldItem.first == newItem.first

        override fun areContentsTheSame(oldItem: Pair<String, Boolean>, newItem: Pair<String, Boolean>) = oldItem == newItem
    }
}