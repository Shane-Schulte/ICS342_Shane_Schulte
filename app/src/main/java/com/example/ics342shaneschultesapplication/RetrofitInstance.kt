package com.example.ics342shaneschultesapplication

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory


object RetrofitInstance {
    private const val BASE_URL = "https://todos.simpleapi.dev/"
    private const val API_KEY = "125eb1fe-8f3f-4004-941c-1dcb818fec00"

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val client = OkHttpClient.Builder().addInterceptor { chain ->
        val original = chain.request()
        val url = original.url.newBuilder().addQueryParameter("apikey", API_KEY).build()
        val request = original.newBuilder().url(url).build()
        chain.proceed(request)
    }.build()

    var api = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()
        .create(ToDoApiService::class.java)

}
