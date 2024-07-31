package com.palmteam.verification.lib

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PhoneVerificationManager(baseUrl: String, apiKey: String) {

    private val api: PhoneVerificationApi = RetrofitClient.getClient(baseUrl, apiKey)
        .create(PhoneVerificationApi::class.java)

    fun sendVerificationCode(mobileNumber: String, callback: SendingCallback) {
        api.sendVerificationCode(mobileNumber).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    callback.onSuccess()
                } else {
                    val errorMessage = response.errorBody()?.string() ?: "Unknown error"
                    callback.onFailure("Error sending verification code. $errorMessage")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                callback.onFailure("Error sending verification code. ${t.message ?: "Unknown error"}")
            }
        })
    }

    fun verifyCode(mobileNumber: String, code: String, callback: VerificationCallback) {
        api.verifyCode(mobileNumber, code).enqueue(object : Callback<VerificationResponse> {
            override fun onResponse(
                call: Call<VerificationResponse>,
                response: Response<VerificationResponse>
            ) {
                if (response.isSuccessful) {
                    val customToken = response.body()?.customToken ?: ""
                    callback.onSuccess(customToken)
                } else {
                    val errorMessage = response.errorBody()?.string() ?: "Unknown error"
                    callback.onFailure("Error verifying code. $errorMessage")
                }
            }

            override fun onFailure(call: Call<VerificationResponse>, t: Throwable) {
                callback.onFailure("Error verifying code. ${t.message ?: "Unknown error"}")
            }
        })
    }
}

interface SendingCallback {
    fun onSuccess()
    fun onFailure(error: String)
}

interface VerificationCallback {
    fun onSuccess(customToken: String = "")
    fun onFailure(error: String)
}