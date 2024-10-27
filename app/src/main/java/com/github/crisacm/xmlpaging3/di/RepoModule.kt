package com.github.crisacm.xmlpaging3.di

import com.github.crisacm.xmlpaging3.data.repo.GithubRepositoryImpl
import com.github.crisacm.xmlpaging3.domain.repo.GithubRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface RepoModule {
  @Binds
  fun bindsGitHubRepository(impl: GithubRepositoryImpl): GithubRepository
}
