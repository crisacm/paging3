package com.github.crisacm.xmlpaging3.presentation.main.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.github.crisacm.xmlpaging3.databinding.ItemReposLoadStateFooterBinding

class ReposLoadStateAdapter(
  private val retry: () -> Unit
) : LoadStateAdapter<ReposLoadStateAdapter.ViewHolder>() {

  override fun onBindViewHolder(holder: ViewHolder, loadState: LoadState) = holder.bind(loadState)

  override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState) = ViewHolder(
    ItemReposLoadStateFooterBinding.inflate(LayoutInflater.from(parent.context), parent, false),
    retry
  )

  class ViewHolder(
    private val binding: ItemReposLoadStateFooterBinding,
    retry: () -> Unit
  ) : RecyclerView.ViewHolder(binding.root) {

    init {
      binding.retryButton.setOnClickListener { retry() }
    }

    fun bind(loadState: LoadState) = with(binding) {
      if (loadState is LoadState.Error) {
        errorMsg.text = loadState.error.localizedMessage
      }

      progressBar.isVisible = loadState is LoadState.Loading
      retryButton.isVisible = loadState is LoadState.Error
      errorMsg.isVisible = loadState is LoadState.Error
    }
  }
}
