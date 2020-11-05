package com.dsmllt.lltracker

import com.google.gson.annotations.SerializedName

data class UserItem(
    @SerializedName( value = "username")
    val username: String,
    @SerializedName( value = "password")
    val password: String
)