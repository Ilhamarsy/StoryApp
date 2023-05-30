package com.dicoding.storyapp.ui.main

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.storyapp.databinding.ItemStoryBinding
import com.dicoding.storyapp.network.response.DetailResponse
import com.dicoding.storyapp.ui.detail.DetailActivity
import com.dicoding.storyapp.utils.withDateFormat

class ItemAdapter :
    PagingDataAdapter<DetailResponse, ItemAdapter.ListViewHolder>(DIFF_CALLBACK) {

    class ListViewHolder(private val binding: ItemStoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: DetailResponse) {
            Glide.with(binding.root)
                .load(item.photoUrl)
                .into(binding.ivItemPhoto)
            binding.tvItemName.text = item.name
            binding.tvItemDate.text = item.createdAt.withDateFormat()

            itemView.setOnClickListener {
                val optionsCompat: ActivityOptionsCompat =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        itemView.context as Activity,
                        androidx.core.util.Pair(binding.ivItemPhoto, "storyImg"),
                        androidx.core.util.Pair(binding.tvItemName, "name"),
                        androidx.core.util.Pair(binding.llDate, "date"),
                    )

                Intent(binding.root.context, DetailActivity::class.java).also { intent ->
                    intent.putExtra("DATA", item)
                    itemView.context.startActivity(intent, optionsCompat.toBundle())
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val data = getItem(position)
        if (data != null) {
            holder.bind(data)
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<DetailResponse>() {
            override fun areItemsTheSame(
                oldItem: DetailResponse,
                newItem: DetailResponse
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: DetailResponse,
                newItem: DetailResponse
            ): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}
