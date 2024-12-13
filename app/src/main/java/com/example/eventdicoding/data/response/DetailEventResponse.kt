package com.example.eventdicoding.data.response

import com.google.gson.annotations.SerializedName

data class DetailEventResponse(
    @SerializedName("event")
    val event: Event? = null
)

data class Event(
    @SerializedName("id")
    val id: Int? = null,

    @SerializedName("name")
    val name: String? = null,

    @SerializedName("summary")
    val summary: String? = null,

    @SerializedName("description")
    val description: String? = null,

    @SerializedName("imageLogo")
    val imageLogo: String? = null,

    @SerializedName("mediaCover")
    val mediaCover: String? = null,

    @SerializedName("category")
    val category: String? = null,

    @SerializedName("ownerName")
    val ownerName: String? = null,

    @SerializedName("cityName")
    val cityName: String? = null,

    @SerializedName("quota")
    val quota: Int? = null,

    @SerializedName("registrants")
    val registrants: Int = 0,

    @SerializedName("beginTime")
    val beginTime: String? = null,

    @SerializedName("endTime")
    val endTime: String? = null,

    @SerializedName("link")
    val link: String? = null
)


