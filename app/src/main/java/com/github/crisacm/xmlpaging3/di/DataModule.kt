package com.github.crisacm.xmlpaging3.di

import android.content.Context
import android.util.Log
import com.github.crisacm.xmlpaging3.data.api.service.GithubApi
import com.github.crisacm.xmlpaging3.data.local.AppDatabase
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

  @Provides
  @Singleton
  fun providesOkHttpClient(): OkHttpClient =
    OkHttpClient.Builder()
      .addInterceptor(
        HttpLoggingInterceptor { message: String? -> Log.v("Interceptor", message ?: "") }.apply {
          setLevel(HttpLoggingInterceptor.Level.BODY)
        }
      )
      .build()

  @Provides
  @Singleton
  fun provideMoshiAdapter(): Moshi {
    return Moshi.Builder()
      .add(KotlinJsonAdapterFactory())
      .build()
  }

  @Provides
  @Singleton
  fun provideRetrofit(okHttpClient: OkHttpClient, moshi: Moshi): Retrofit {
    return Retrofit.Builder()
      .client(okHttpClient)
      .baseUrl("https://api.github.com")
      .addConverterFactory(MoshiConverterFactory.create(moshi))
      .build()
  }

  @Provides
  fun providesGithubApi(retrofit: Retrofit): GithubApi = retrofit.create(GithubApi::class.java)

  @Provides
  fun providesAppDatabase(
    @ApplicationContext context: Context
  ): AppDatabase = AppDatabase.getInstance(context)
}
