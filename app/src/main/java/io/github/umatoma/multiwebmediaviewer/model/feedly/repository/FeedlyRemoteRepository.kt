package io.github.umatoma.multiwebmediaviewer.model.feedly.repository

import com.google.gson.Gson
import io.github.umatoma.multiwebmediaviewer.model.feedly.entity.FeedlyAccessToken
import io.github.umatoma.multiwebmediaviewer.model.feedly.entity.FeedlyCategory
import io.github.umatoma.multiwebmediaviewer.model.feedly.entity.FeedlyCollection
import io.github.umatoma.multiwebmediaviewer.model.feedly.entity.FeedlyStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import java.io.IOException

class FeedlyRemoteRepository(
    private val localRepository: FeedlyLocalRepository
) {

    private val okHttpClient: OkHttpClient by lazy {
        val loggingInterceptor = HttpLoggingInterceptor().also {
            it.level = HttpLoggingInterceptor.Level.BODY
        }
        return@lazy OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    }

    fun getRedirectUrl(): String {
        return "http://localhost:8080"
    }

    fun getAuthenticationUrl(): String {
        return "https://cloud.feedly.com/v3/auth/auth"
            .toHttpUrl()
            .newBuilder()
            .addQueryParameter("response_type", "code")
            .addQueryParameter("client_id", "feedly")
            .addQueryParameter("redirect_uri", getRedirectUrl())
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
            "redirect_uri" to getRedirectUrl(),
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

    suspend fun getStreamContents(category: FeedlyCategory, prevStream: FeedlyStream?): FeedlyStream = withContext(Dispatchers.IO) {
        val count = 30
        val continuation = prevStream?.continuation
        val requestUrl = "https://cloud.feedly.com/v3/streams/contents"
            .toHttpUrl()
            .newBuilder()
            .addQueryParameter("streamId", category.id)
            .addQueryParameter("count", count.toString())
            .addQueryParameter("continuation", continuation)
            .build()

        val request = Request.Builder()
            .url(requestUrl)
            .build()

        val body = executeAuthRequest(request)

        return@withContext FeedlyStream.fromJSON(body)
    }

    suspend fun getCollections(): List<FeedlyCollection> = withContext(Dispatchers.IO) {
        val requestUrl = "https://cloud.feedly.com/v3/collections"
        val request = Request.Builder()
            .url(requestUrl)
            .build()

        val body = executeAuthRequest(request)

        return@withContext FeedlyCollection.fromListJSON(body)
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

    private fun executeAuthRequest(request: Request): String {
        val accessToken = localRepository.getAccessToken()
        val newRequest = request.newBuilder()
            .addHeader("Authorization", "Bearer ${accessToken.accessToken}")
            .build()
        return executeRequest(newRequest)
    }
}