package com.example.eventdicoding.ui.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.eventdicoding.data.database.local.entity.DetailDataEventEntity
import com.example.eventdicoding.databinding.ItemEventBinding
import com.example.eventdicoding.ui.detail.DetailEventActivity

class FavoriteEventAdapter : ListAdapter<DetailDataEventEntity, FavoriteEventAdapter.FavoriteEventViewHolder>(
    DIFF_CALLBACK
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteEventViewHolder {
        val binding = ItemEventBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FavoriteEventViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavoriteEventViewHolder, position: Int) {
        val event = getItem(position)
        if (event != null) {
            holder.bind(event)
        }
    }

    class FavoriteEventViewHolder(private val binding: ItemEventBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(event: DetailDataEventEntity) {
            binding.eventName.text = event.name ?: "No Name"

            event.mediaCover?.let {
                Glide.with(binding.eventImage.context)
                    .load(it)
                    .into(binding.eventImage)
            }

            binding.root.setOnClickListener {
                val context = binding.root.context
                val intent = Intent(context, DetailEventActivity::class.java).apply {
                    putExtra("id", event.id)
                }
                context.startActivity(intent)
            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<DetailDataEventEntity>() {
            override fun areItemsTheSame(oldItem: DetailDataEventEntity, newItem: DetailDataEventEntity): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: DetailDataEventEntity, newItem: DetailDataEventEntity): Boolean {
                return oldItem == newItem
            }
        }
    }
}
