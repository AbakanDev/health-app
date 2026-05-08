package com.example.truyvetyte.model

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    val status: String,
    val message: String,
    val data: UserData?
)

data class UserData(
    val cccd: String,
    @SerializedName("fullName")
    val fullName: String?,
    val email: String?,
    val phone: String?
)