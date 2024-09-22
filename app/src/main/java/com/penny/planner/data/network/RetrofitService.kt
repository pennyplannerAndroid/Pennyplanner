package com.penny.planner.data.network

import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface RetrofitService {

    @GET("uc?export=download")
    suspend fun getEmojiJsonFromDrive(@Query("id") fileId: String): Response<JsonObject>
}