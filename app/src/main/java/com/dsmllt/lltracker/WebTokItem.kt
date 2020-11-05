package com.dsmllt.lltracker

//data class WebTokItem(
//    val status: Int,
//    val token: String,
//    val username: String
//)

import com.google.gson.annotations.SerializedName

data class WebTokItem(
    @SerializedName( value = "username")
    val username: String,
    @SerializedName( value = "status")
    val status: Int,
    @SerializedName( value = "token")
    val token: String
)