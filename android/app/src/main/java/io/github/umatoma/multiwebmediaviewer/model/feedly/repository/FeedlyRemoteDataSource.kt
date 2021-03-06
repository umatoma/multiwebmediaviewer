package io.github.umatoma.multiwebmediaviewer.model.feedly.repository

import com.google.gson.Gson
import io.github.umatoma.multiwebmediaviewer.BuildConfig
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

class FeedlyRemoteDataSource(
    private val getLocalAccessToken: () -> FeedlyAccessToken,
    private val putAccessToken: (accessToken: FeedlyAccessToken) -> Unit
) {

    companion object {
        private class UnauthorizedException(message: String) : Exception(message)
        private class ForbiddenException(message: String) : Exception(message)
    }

    private val okHttpClient: OkHttpClient by lazy {
        val loggingInterceptor = HttpLoggingInterceptor().also {
            it.level = HttpLoggingInterceptor.Level.BODY
        }
        return@lazy OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    }

    fun getRedirectUrl(): String {
        return BuildConfig.FEEDLY_API_REDIRECT_URL
    }

    fun getAuthenticationUrl(): String {
        return "${BuildConfig.FEEDLY_API_BASE_URL}/v3/auth/auth"
            .toHttpUrl()
            .newBuilder()
            .addQueryParameter("response_type", "code")
            .addQueryParameter("client_id", "feedly")
            .addQueryParameter("redirect_uri", getRedirectUrl())
            .addQueryParameter("scope", "${BuildConfig.FEEDLY_API_BASE_URL}/subscriptions")
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
            .url("${BuildConfig.FEEDLY_API_BASE_URL}/v3/auth/token")
            .post(requestBody)
            .build()

        val body = executeRequest(request)

        return@withContext FeedlyAccessToken.fromJSON(body)
    }

    suspend fun getStreamContents(category: FeedlyCategory, prevStream: FeedlyStream?): FeedlyStream = withContext(Dispatchers.IO) {
        val count = 30
        val continuation = prevStream?.continuation
        val requestUrl = "${BuildConfig.FEEDLY_API_BASE_URL}/v3/streams/contents"
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
        val requestUrl = "${BuildConfig.FEEDLY_API_BASE_URL}/v3/collections"
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
            when (response.code) {
                401 -> throw UnauthorizedException(response.message)
                403 -> throw ForbiddenException(response.message)
                else -> throw IOException("Unexpected code ${response.code}")
            }
        }

        if (response.body == null) {
            throw IOException("Response body is empty")
        }

        return response.body!!.string()
    }

    private suspend fun executeAuthRequest(request: Request): String = withContext(Dispatchers.IO) {
        val accessToken = getLocalAccessToken()

        try {
            val newRequest = request.newBuilder()
                .addHeader("Authorization", "Bearer ${accessToken.accessToken}")
                .build()

            return@withContext executeRequest(newRequest)
        } catch (e: Exception) {
            if (
                e is ForbiddenException ||
                e is UnauthorizedException
            ) {
                val refreshedAccessToken = getRefreshedAccessToken(accessToken.refreshToken)
                putAccessToken(refreshedAccessToken)

                val newRequest = request.newBuilder()
                    .addHeader("Authorization", "Bearer ${refreshedAccessToken.accessToken}")
                    .build()

                return@withContext executeRequest(newRequest)
            } else {
                throw e
            }
        }
    }

    private suspend fun getRefreshedAccessToken(refreshToken: String): FeedlyAccessToken = withContext(Dispatchers.IO) {
        val jsonParams = mapOf(
            "refresh_token" to refreshToken,
            "client_id" to "feedly",
            "client_secret" to "0XP4XQ07VVMDWBKUHTJM4WUQ",
            "grant_type" to "refresh_token"
        )
        val requestBody = Gson().toJson(jsonParams)
            .toRequestBody("application/json".toMediaType())

        val requestUrl = "${BuildConfig.FEEDLY_API_BASE_URL}/v3/auth/token"
        val request = Request.Builder()
            .url(requestUrl)
            .post(requestBody)
            .build()

        val body = executeRequest(request)

        return@withContext FeedlyAccessToken.fromJSON(body)
    }
}