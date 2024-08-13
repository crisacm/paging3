package com.github.crisacm.xmlpaging3.presentation.main

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.core.view.MenuProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.PagingData
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.crisacm.xmlpaging3.R
import com.github.crisacm.xmlpaging3.data.api.service.GithubApi
import com.github.crisacm.xmlpaging3.databinding.ActivityMainBinding
import com.github.crisacm.xmlpaging3.presentation.main.adapter.ReposAdapter
import com.github.crisacm.xmlpaging3.presentation.main.adapter.ReposLoadStateAdapter
import com.github.crisacm.xmlpaging3.presentation.main.viewModel.GithubViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
  private lateinit var binding: ActivityMainBinding

  private val viewModel by viewModels<GithubViewModel>()

  @Inject
  lateinit var githubApi: GithubApi

  private val adapter by lazy { ReposAdapter() }

  private var queryJob: Job? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(binding.root)

    setSupportActionBar(binding.toolbar)
    supportActionBar?.setDisplayShowTitleEnabled(true)
    supportActionBar?.title = "Github Repositories"

    addMenuProvider(object : MenuProvider {
      override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.menu_main, menu)
      }

      override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        if (menuItem.itemId == R.id.action_reload) {
          Toast.makeText(this@MainActivity, "Reload data", Toast.LENGTH_SHORT).show()
          searchByUsername(binding.searchView.query.toString())
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
  }

  private fun clearData() {
    lifecycleScope.launch {
      binding.textEmpty.visibility = View.VISIBLE
      adapter.submitData(PagingData.empty())
    }
  }

  private fun searchByUsername(username: String) {
    queryJob?.cancel()
    queryJob = lifecycleScope.launch {
      adapter.submitData(PagingData.empty())

      if (binding.buttonRemote.isChecked) {
        viewModel.fetchRepos(username).distinctUntilChanged().collectLatest {
          adapter.submitData(it)
          binding.textEmpty.visibility = if (adapter.snapshot().isEmpty()) View.VISIBLE else View.GONE
        }
      }

      if (binding.buttonLocal.isChecked) {
        viewModel.getRepos(username).distinctUntilChanged().collectLatest {
          adapter.submitData(it)
          binding.textEmpty.visibility = if (adapter.snapshot().isEmpty()) View.VISIBLE else View.GONE
        }
      }

      if (binding.buttonRemoteLocal.isChecked) {
        viewModel.fetchGetRepos(username).distinctUntilChanged().collectLatest {
          adapter.submitData(it)
          binding.textEmpty.visibility = if (adapter.snapshot().isEmpty()) View.VISIBLE else View.GONE
        }
      }
    }
  }
}