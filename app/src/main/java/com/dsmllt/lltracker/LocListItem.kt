package com.dsmllt.lltracker

import com.google.gson.annotations.SerializedName

data class LocListItem(
    @SerializedName( value = "username")
    val username: String,
    @SerializedName( value = "latitude")
    val latitude: Double,
    @SerializedName( value = "longitude")
    val longitude: Double,
    @SerializedName( value = "logtime")
    val logtime: Long
)