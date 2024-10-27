package com.github.crisacm.xmlpaging3.presentation.main

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.core.view.MenuProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.crisacm.xmlpaging3.R
import com.github.crisacm.xmlpaging3.databinding.ActivityMainBinding
import com.github.crisacm.xmlpaging3.presentation.main.adapter.ReposAdapter
import com.github.crisacm.xmlpaging3.presentation.main.adapter.ReposLoadStateAdapter
import com.github.crisacm.xmlpaging3.presentation.main.viewModel.GithubViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
  private lateinit var binding: ActivityMainBinding

  private val viewModel by viewModels<GithubViewModel>()

  private val adapter by lazy { ReposAdapter() }

  private var queryJob: Job? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(binding.root)

    setSupportActionBar(binding.toolbar)
    supportActionBar?.setDisplayShowTitleEnabled(true)

    addMenuProvider(object : MenuProvider {
      override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.menu_main, menu)
      }

      override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        if (menuItem.itemId == R.id.action_clear) {
          clearData()
        }

        return true
      }
    })

    binding.recyclerView.layoutManager = LinearLayoutManager(this)
    binding.recyclerView.addItemDecoration(
      DividerItemDecoration(
        this,
        DividerItemDecoration.VERTICAL
      )
    )
    binding.recyclerView.adapter = adapter.withLoadStateHeaderAndFooter(
      header = ReposLoadStateAdapter { adapter.retry() },
      footer = ReposLoadStateAdapter { adapter.retry() }
    )

    lifecycleScope.launch {
      adapter.loadStateFlow
        .distinctUntilChanged()
        .collect { ls ->
          val txtCount = "Count: ${adapter.itemCount}"
          binding.textCount.text = txtCount

          if (ls.refresh is LoadState.NotLoading && ls.append.endOfPaginationReached && adapter.itemCount < 1) {
            manageRecyclerStates(RecyclerStates.ERROR)
          }

          if (ls.refresh is LoadState.NotLoading && adapter.itemCount > 1) {
            manageRecyclerStates(RecyclerStates.LOADED)
          }
        }
    }

    binding.buttonToggleGroup.addOnButtonCheckedListener { _, _, isChecked ->
      if (isChecked) clearData()
    }

    binding.searchView.setOnQueryTextListener(object : OnQueryTextListener {
      override fun onQueryTextSubmit(query: String?): Boolean {
        searchByUsername(query.toString())
        return true
      }

      override fun onQueryTextChange(newText: String?): Boolean = true
    })

    manageRecyclerStates(RecyclerStates.EMPTY)
  }

  private fun clearData() {
    manageRecyclerStates(RecyclerStates.EMPTY)
    binding.searchView.setQuery("", false)
    lifecycleScope.launch {
      adapter.submitData(PagingData.empty())
    }
  }

  private fun searchByUsername(username: String) {
    queryJob?.cancel()
    queryJob = lifecycleScope.launch {
      manageRecyclerStates(RecyclerStates.LOADING)
      adapter.submitData(PagingData.empty())

      if (binding.buttonRemoteLocal.isChecked) {
        viewModel.fetchRepos(username).distinctUntilChanged().collectLatest {
          adapter.submitData(it)
        }
      }

      if (binding.buttonLocal.isChecked) {
        viewModel.getRepos(username).distinctUntilChanged().collectLatest {
          adapter.submitData(it)
        }
      }
    }
  }

  private fun manageRecyclerStates(states: RecyclerStates) {
    when (states) {
      RecyclerStates.LOADING -> {
        binding.progressBar.visibility = View.VISIBLE
        binding.textError.visibility = View.GONE
        binding.textEmpty.visibility = View.GONE
      }

      RecyclerStates.LOADED -> {
        binding.progressBar.visibility = View.GONE
        binding.textError.visibility = View.GONE
        binding.textEmpty.visibility = View.GONE
      }

      RecyclerStates.ERROR -> {
        binding.progressBar.visibility = View.GONE
        binding.textError.visibility = View.VISIBLE
        binding.textEmpty.visibility = View.GONE
      }

      RecyclerStates.EMPTY -> {
        binding.progressBar.visibility = View.GONE
        binding.textError.visibility = View.GONE
        binding.textEmpty.visibility = View.VISIBLE
      }
    }
  }

  enum class RecyclerStates {
    LOADING,
    LOADED,
    ERROR,
    EMPTY
  }

  private fun logI(msg: String) {
    Log.i("Testing:I", msg)
  }

  private fun logE(msg: String) {
    Log.e("Testing:E", msg)
  }
}