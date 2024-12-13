package com.example.eventdicoding.data.retrofit

import com.example.eventdicoding.data.response.DetailEventResponse
import com.example.eventdicoding.data.response.EventResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("events")
    fun getEvents(@Query("active") active: Int): Call<EventResponse>

    @GET("events/{id}")
    fun getDetailEvent(@Path("id") id: Int): Call<DetailEventResponse>

    @GET("events")
    fun searchEvents(@Query("q") query: String): Call<EventResponse>
}