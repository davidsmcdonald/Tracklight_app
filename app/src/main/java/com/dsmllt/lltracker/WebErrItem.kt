package com.dsmllt.lltracker

import com.google.gson.annotations.SerializedName

data class WebErrItem(
    @SerializedName( value = "username")
    val username: String,
    @SerializedName( value = "status")
    val status: Int,
    @SerializedName( value = "message")
    val message: String
)