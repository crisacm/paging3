package com.github.crisacm.xmlpaging3.presentation.main.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.github.crisacm.xmlpaging3.domain.model.Repo
import com.github.crisacm.xmlpaging3.domain.repo.GithubRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class GithubViewModel @Inject constructor(
  private val repository: GithubRepository
) : ViewModel() {

  fun fetchRepos(username: String): Flow<PagingData<Repo>> =
    repository.fetchReposByUsername(username)
      .cachedIn(viewModelScope)

  fun getRepos(username: String): Flow<PagingData<Repo>> =
    repository.getReposByUsername(username)
      .cachedIn(viewModelScope)

  fun fetchGetRepos(username: String): Flow<PagingData<Repo>> =
    repository.fetchReposByUsername(username)
      .cachedIn(viewModelScope)
}
