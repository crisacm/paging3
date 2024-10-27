package com.github.crisacm.xmlpaging3.presentation.main.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.github.crisacm.xmlpaging3.databinding.ItemReposBinding
import com.github.crisacm.xmlpaging3.domain.model.Repo

class ReposAdapter : PagingDataAdapter<Repo, ReposAdapter.ViewHolder>(COMPARATOR) {
  override fun onCreateViewHolder(
    parent: ViewGroup,
    viewType: Int,
  ) = ViewHolder(
    ItemReposBinding.inflate(LayoutInflater.from(parent.context), parent, false),
  )

  override fun onBindViewHolder(
    holder: ViewHolder,
    position: Int,
  ) {
    getItem(position)?.let { holder.bind(it, position) }
  }

  inner class ViewHolder(
    private val binding: ItemReposBinding,
  ) : RecyclerView.ViewHolder(binding.root) {
    fun bind(
      repo: Repo,
      position: Int,
    ) = with(binding) {
      val pos = (position + 1).toString()
      textNumber.text = pos
      textDesc.text = repo.name
    }
  }

  companion object {
    private val COMPARATOR =
      object : DiffUtil.ItemCallback<Repo>() {
        override fun areItemsTheSame(
          oldItem: Repo,
          newItem: Repo,
        ): Boolean = oldItem.name == newItem.name

        override fun areContentsTheSame(
          oldItem: Repo,
          newItem: Repo,
        ): Boolean = oldItem == newItem
      }
  }
}
