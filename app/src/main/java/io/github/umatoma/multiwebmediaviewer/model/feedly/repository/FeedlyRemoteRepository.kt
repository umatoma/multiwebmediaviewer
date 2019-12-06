package io.github.umatoma.multiwebmediaviewer.model.feedly.repository

import com.google.gson.Gson
import io.github.umatoma.multiwebmediaviewer.model.feedly.entity.FeedlyAccessToken
import io.github.umatoma.multiwebmediaviewer.model.hatena.repository.HatenaRemoteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.MediaType
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.io.IOException

class FeedlyRemoteRepository {

    private val okHttpClient: OkHttpClient by lazy {
        val loggingInterceptor = HttpLoggingInterceptor().also {
            it.level = HttpLoggingInterceptor.Level.BODY
        }
        return@lazy OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    }

    fun getAuthenticationUrl(): String {
        return "https://cloud.feedly.com/v3/auth/auth"
            .toHttpUrl()
            .newBuilder()
            .addQueryParameter("response_type", "code")
            .addQueryParameter("client_id", "feedly")
            .addQueryParameter("redirect_uri", "http://localhost:8080")
            .addQueryParameter("scope", "https://cloud.feedly.com/subscriptions")
            .addQueryParameter("state", "state.passed.in")
            .build()
            .toString()
    }

    suspend fun getAccessToken(code: String): FeedlyAccessToken = withContext(Dispatchers.IO) {
        val jsonParams = mapOf(
            "code" to code,
            "client_id" to "feedly",
            "client_secret" to "0XP4XQ07VVMDWBKUHTJM4WUQ",
            "redirect_uri" to "http://localhost:8080",
            "state" to "state.passed.in",
            "grant_type" to "authorization_code"
        )
        val requestBody = Gson().toJson(jsonParams)
            .toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url("https://cloud.feedly.com/v3/auth/token")
            .post(requestBody)
            .build()

        val body = executeRequest(request)

        return@withContext FeedlyAccessToken.fromJSON(body)
    }

    private fun executeRequest(request: Request): String {
        val response = okHttpClient
            .newCall(request)
            .execute()

        if (!response.isSuccessful) {
            throw IOException("Unexpected code ${response.code}")
        }

        if (response.body == null) {
            throw IOException("Response body is empty")
        }

        return response.body!!.string()
    }
}