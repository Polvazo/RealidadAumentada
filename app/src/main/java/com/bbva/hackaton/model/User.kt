package com.corebuild.arlocation.demo.model

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("userID")
    val username: String,
    @SerializedName("password")
    val password: String
)