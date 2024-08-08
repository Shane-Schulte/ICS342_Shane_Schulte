package com.example.ics342shaneschultesapplication

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object RetrofitInstance {
    private const val BASE_URL = "https://todos.simpleapi.dev/"
    private const val API_KEY = "bf726866-dfb5-409f-8c5b-784031d96e84"

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val client = OkHttpClient.Builder().addInterceptor { chain ->
        val original = chain.request()
        val url = original.url.newBuilder().addQueryParameter("apikey", API_KEY).build()
        val request = original.newBuilder().url(url).build()
        chain.proceed(request)
    }.build()

    val api: ToDoApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(ToDoApiService::class.java)
    }
}
