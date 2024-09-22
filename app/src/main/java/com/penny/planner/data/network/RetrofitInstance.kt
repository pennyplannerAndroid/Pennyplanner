package com.penny.planner.data.network

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object RetrofitInstance {
    var gson: Gson = GsonBuilder()
        .setLenient()
        .create()
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://drive.google.com/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    val retrofitApi: RetrofitService by lazy {
        retrofit.create(RetrofitService::class.java)
    }
}