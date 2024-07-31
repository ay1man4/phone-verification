package com.palmteam.verification.lib

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface PhoneVerificationApi {
    @GET("send")
    fun sendVerificationCode(@Query("phone") phone: String): Call<Void>

    @GET("verify")
    fun verifyCode(@Query("phone") phone: String, @Query("code") code: String): Call<VerificationResponse>
}

data class VerificationResponse(val customToken: String)