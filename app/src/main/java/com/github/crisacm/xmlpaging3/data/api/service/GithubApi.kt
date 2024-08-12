package com.github.crisacm.xmlpaging3.data.api.service

import com.github.crisacm.xmlpaging3.data.api.models.RepoResponses
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GithubApi {

  @GET("users/{username}/repos")
  suspend fun fetchRepos(
    @Path("username") username: String,
    @Query("page") page: Int,
    @Query("per_page") size: Int
  ): List<RepoResponses>
}
