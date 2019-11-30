package io.github.umatoma.multiwebmediaviewer.common.hatena.repository

import io.github.umatoma.multiwebmediaviewer.common.hatena.OAuth1AuthHeaderUtil
import io.github.umatoma.multiwebmediaviewer.common.hatena.entity.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import org.simpleframework.xml.core.Persister
import java.io.IOException
import java.net.URL
import java.net.URLDecoder
import java.net.URLEncoder


class HatenaRemoteRepository(
    private val localRepository: HatenaLocalRepository
) {

    companion object {
        private const val CONSUMER_KEY = "UKPgBFtq73oNBQ=="
        private const val CONSUMER_SECRET = "v/CdhV9DxEOC3CAX7W57SsbBCr4="
        private const val URL_TEMPORARY_CREDENTIAL_REQUEST =
            "https://www.hatena.com/oauth/initiate"
        private const val URL_RESOURCE_OWNER_AUTHORIZATION =
            "https://www.hatena.ne.jp/touch/oauth/authorize"
        private const val URL_TOKEN_REQUEST =
            "https://www.hatena.com/oauth/token"
    }

    private val oauth1Interceptor =
        OAuth1AuthHeaderUtil(CONSUMER_KEY, CONSUMER_SECRET)

    private val okHttpClient: OkHttpClient by lazy {
        val loggingInterceptor = HttpLoggingInterceptor().also {
            it.level = HttpLoggingInterceptor.Level.BODY
        }
        return@lazy OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    }

    suspend fun getRequestToken(): HatenaRequestToken = withContext(Dispatchers.IO) {
        val requestBody = FormBody
            .Builder()
            .add("scope", "read_public,write_public,read_private,write_private")
            .build()

        val request = Request.Builder()
            .url(URL_TEMPORARY_CREDENTIAL_REQUEST)
            .post(requestBody)
            .build()

        val response = okHttpClient
            .newCall(oauth1Interceptor.setAuthHeader(request))
            .execute()

        if (!response.isSuccessful) {
            throw IOException("Unexpected code ${response.code}")
        }

        val bodyParams = response.body?.let { body ->
            body.string()
                .split("&")
                .map { it.split("=") }
                .associateBy({ it[0] }, { URLDecoder.decode(it[1], "UTF-8") })
        } ?: throw IOException("Response body is empty")

        return@withContext HatenaRequestToken(
            token = bodyParams.getValue("oauth_token"),
            tokenSecret = bodyParams.getValue("oauth_token_secret"),
            callbackConfirmed = bodyParams.getValue("oauth_callback_confirmed").toBoolean()
        )
    }

    fun getAuthenticationUrl(requestToken: HatenaRequestToken): String {
        return buildString {
            append(URL_RESOURCE_OWNER_AUTHORIZATION)
            append("?")
            append("oauth_token")
            append("=")
            append(URLEncoder.encode(requestToken.token, "UTF-8"))
        }
    }

    suspend fun getAccessToken(
        requestToken: HatenaRequestToken,
        verifier: String
    ): HatenaAccessToken =
        withContext(Dispatchers.IO) {
            val requestBody = FormBody
                .Builder()
                .build()

            val request = Request.Builder()
                .url(URL_TOKEN_REQUEST)
                .post(requestBody)
                .build()

            val oAuthToken = OAuth1AuthHeaderUtil.OAuthToken(
                requestToken.token,
                requestToken.tokenSecret
            )
            val response = okHttpClient
                .newCall(oauth1Interceptor.setAuthHeader(request, oAuthToken, verifier))
                .execute()

            if (!response.isSuccessful) {
                throw IOException("Unexpected code ${response.code}")
            }

            val bodyParams = response.body?.let { body ->
                body.string()
                    .split("&")
                    .map { it.split("=") }
                    .associateBy({ it[0] }, { URLDecoder.decode(it[1], "UTF-8") })
            } ?: throw IOException("Response body is empty")

            return@withContext HatenaAccessToken(
                token = bodyParams.getValue("oauth_token"),
                tokenSecret = bodyParams.getValue("oauth_token_secret"),
                urlName = bodyParams.getValue("url_name"),
                displayName = bodyParams.getValue("display_name")
            )
        }

    suspend fun getUser() = withContext(Dispatchers.IO) {
        val accessToken = localRepository.getAccessToken()

        val request = Request.Builder()
            .url("https://bookmark.hatenaapis.com/rest/1/my")
            .build()

        val oAuthToken = OAuth1AuthHeaderUtil.OAuthToken(
            accessToken.token,
            accessToken.tokenSecret
        )
        val response = okHttpClient
            .newCall(oauth1Interceptor.setAuthHeader(request, oAuthToken))
            .execute()

        if (!response.isSuccessful) {
            throw IOException("Unexpected code ${response.code}")
        }

        return@withContext HatenaUser.fromJSON(response.body!!.string())
    }

    suspend fun getEntry(url: String) = withContext(Dispatchers.IO) {
        val requestUrl = "https://bookmark.hatenaapis.com/rest/1/entry"
            .toHttpUrl()
            .newBuilder()
            .addQueryParameter("url", url)
            .build()

        val request = Request.Builder()
            .url(requestUrl)
            .build()

        val body = executeAuthRequest(request)

        // TODO: parse body and return value
    }

    suspend fun getHotEntryList(category: HatenaEntry.Category): List<HatenaEntry> =
        withContext(Dispatchers.IO) {
            val request = Request.Builder()
                .url("https://b.hatena.ne.jp/hotentry/${category.id}.rss")
                .build()

            val body = executeRequest(request)
            val root = Persister().read(HatenaRssRoot::class.java, body)

            return@withContext root.itemList!!.map {
                HatenaEntry(
                    title = it.title!!,
                    url = it.link!!,
                    entryUrl = it.bookmarkSiteEntriesListUrl!!,
                    imageUrl = it.imageurl,
                    count = it.bookmarkcount!!.toInt()
                )
            }
        }

    private fun executeAuthRequest(request: Request): String {
        val accessToken = localRepository.getAccessToken()
        val oAuthToken = OAuth1AuthHeaderUtil.OAuthToken(
            accessToken.token,
            accessToken.tokenSecret
        )
        return executeRequest(oauth1Interceptor.setAuthHeader(request, oAuthToken))
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

