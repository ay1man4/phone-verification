package com.palmteam.verification.lib

import okhttp3.ResponseBody
import okio.Timeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FakePhoneVerificationApi : PhoneVerificationApi {
    var shouldReturnError = false

    override fun sendVerificationCode(phone: String): Call<Void> {
        return FakeCall(shouldReturnError)
    }

    override fun verifyCode(phone: String, code: String): Call<VerificationResponse> {
        return FakeVerifyCall(shouldReturnError)
    }

    inner class FakeCall(private val shouldReturnError: Boolean) : Call<Void> {
        override fun enqueue(callback: Callback<Void>) {
            if (shouldReturnError) {
                callback.onResponse(this, Response.error(400, ResponseBody.create(null, "Bad Request")))
            } else {
                callback.onResponse(this, Response.success(null))
            }
        }

        override fun clone(): Call<Void> = this
        override fun execute(): Response<Void> = throw NotImplementedError()
        override fun isExecuted(): Boolean = throw NotImplementedError()
        override fun cancel() = throw NotImplementedError()
        override fun isCanceled(): Boolean = throw NotImplementedError()
        override fun request(): okhttp3.Request = throw NotImplementedError()
        override fun timeout(): Timeout = Timeout()
    }

    inner class FakeVerifyCall(private val shouldReturnError: Boolean) : Call<VerificationResponse> {
        override fun enqueue(callback: Callback<VerificationResponse>) {
            if (shouldReturnError) {
                callback.onResponse(this, Response.error(400, ResponseBody.create(null, "Bad Request")))
            } else {
                callback.onResponse(this, Response.success(VerificationResponse("dummy_token")))
            }
        }

        override fun clone(): Call<VerificationResponse> = this
        override fun execute(): Response<VerificationResponse> = throw NotImplementedError()
        override fun isExecuted(): Boolean = throw NotImplementedError()
        override fun cancel() = throw NotImplementedError()
        override fun isCanceled(): Boolean = throw NotImplementedError()
        override fun request(): okhttp3.Request = throw NotImplementedError()
        override fun timeout(): Timeout = Timeout()
    }
}

class PhoneVerificationManagerTest {
    private lateinit var fakeApi: FakePhoneVerificationApi
    private lateinit var manager: PhoneVerificationManager

    @Before
    fun setUp() {
        fakeApi = FakePhoneVerificationApi()
        manager = PhoneVerificationManager("https://dummy-url.com", "apiKey")
        val apiField = PhoneVerificationManager::class.java.getDeclaredField("api")
        apiField.isAccessible = true
        apiField.set(manager, fakeApi)
    }

    @Test
    fun sendVerificationCodeSuccessful() {
        var successCalled = false
        var failureCalled = false
        val callback = object : SendingCallback {
            override fun onSuccess() {
                successCalled = true
            }

            override fun onFailure(error: String) {
                failureCalled = true
            }
        }

        manager.sendVerificationCode("1234567890", callback)
        assertTrue(successCalled)
        assertFalse(failureCalled)
    }

    @Test
    fun sendVerificationCodeFailure() {
        fakeApi.shouldReturnError = true
        var successCalled = false
        var failureCalled = false
        val callback = object : SendingCallback {
            override fun onSuccess() {
                successCalled = true
            }

            override fun onFailure(error: String) {
                failureCalled = true
            }
        }

        manager.sendVerificationCode("1234567890", callback)
        assertFalse(successCalled)
        assertTrue(failureCalled)
    }

    @Test
    fun verifyCodeSuccessful() {
        var successCalled = false
        var failureCalled = false
        val callback = object : VerificationCallback {
            override fun onSuccess(customToken: String) {
                successCalled = true
                assertEquals("dummy_token", customToken)
            }

            override fun onFailure(error: String) {
                failureCalled = true
            }
        }

        manager.verifyCode("1234567890", "1234", callback)
        assertTrue(successCalled)
        assertFalse(failureCalled)
    }

    @Test
    fun verifyCodeFailure() {
        fakeApi.shouldReturnError = true
        var successCalled = false
        var failureCalled = false
        val callback = object : VerificationCallback {
            override fun onSuccess(customToken: String) {
                successCalled = true
            }

            override fun onFailure(error: String) {
                failureCalled = true
            }
        }

        manager.verifyCode("1234567890", "1234", callback)
        assertFalse(successCalled)
        assertTrue(failureCalled)
    }
}
