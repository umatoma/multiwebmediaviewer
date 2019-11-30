package io.github.umatoma.multiwebmediaviewer.common.hatena

import android.util.Log
import okhttp3.FormBody
import okhttp3.Request
import java.net.URLEncoder
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

class OAuth1AuthHeaderUtil(
    private val consumerKey: String,
    private val consumerSecret: String
) {

    companion object {
        private const val AUTHORIZATION_HEADER_NAME = "Authorization"
        private const val SIGNATURE_METHOD = "HMAC-SHA1"

        private const val OAUTH_KEY_CALLBACK = "oauth_callback"
        private const val OAUTH_KEY_CONSUMER_KEY = "oauth_consumer_key"
        private const val OAUTH_KEY_NONCE = "oauth_nonce"
        private const val OAUTH_KEY_SIGNATURE = "oauth_signature"
        private const val OAUTH_KEY_SIGNATURE_METHOD = "oauth_signature_method"
        private const val OAUTH_KEY_TIMESTAMP = "oauth_timestamp"
        private const val OAUTH_KEY_TOKEN = "oauth_token"
        private const val OAUTH_KEY_VERSION = "oauth_version"
        private const val OAUTH_KEY_VERIFIER = "oauth_verifier"

        private const val OAUTH_VALUE_SIGNATURE_ALGORITHM = "HmacSHA1"
        private const val OAUTH_VALUE_CALLBACK = "oob"
        private const val OAUTH_VALUE_VERSION = "1.0"
    }

    class OAuthToken(
        val token: String,
        val tokenSecret: String
    )

    fun setAuthHeader(request: Request): Request {
        val params = mutableMapOf(
            OAUTH_KEY_CALLBACK to OAUTH_VALUE_CALLBACK,
            OAUTH_KEY_CONSUMER_KEY to consumerKey,
            OAUTH_KEY_NONCE to createNonce(),
            OAUTH_KEY_SIGNATURE_METHOD to SIGNATURE_METHOD,
            OAUTH_KEY_TIMESTAMP to createTimestamp(),
            OAUTH_KEY_VERSION to OAUTH_VALUE_VERSION
        )
        val authorizationHeaderValue = createAuthorizationHeaderValue(request, params)
        return request.newBuilder()
            .header(AUTHORIZATION_HEADER_NAME, authorizationHeaderValue)
            .build()
    }

    fun setAuthHeader(request: Request, oAuthToken: OAuthToken, verifier: String? = null): Request {
        val params = mutableMapOf(
            OAUTH_KEY_CONSUMER_KEY to consumerKey,
            OAUTH_KEY_NONCE to createNonce(),
            OAUTH_KEY_SIGNATURE_METHOD to SIGNATURE_METHOD,
            OAUTH_KEY_TIMESTAMP to createTimestamp(),
            OAUTH_KEY_TOKEN to oAuthToken.token,
            OAUTH_KEY_VERSION to OAUTH_VALUE_VERSION
        )

        verifier?.let { params[OAUTH_KEY_VERIFIER] = it }

        val authorizationHeaderValue = createAuthorizationHeaderValue(request, params, oAuthToken)
        return request.newBuilder()
            .header(AUTHORIZATION_HEADER_NAME, authorizationHeaderValue)
            .build()
    }

    private fun createAuthorizationHeaderValue(
        request: Request,
        oauthParams: Map<String, String>,
        oAuthToken: OAuthToken? = null
    ): String {
        val signKey = createSignKey(oAuthToken)
        val signBaseString = createSignBaseString(request, oauthParams)
        val signature = createSignature(signKey, signBaseString)
        val params = (oauthParams + mapOf(OAUTH_KEY_SIGNATURE to signature))

        return buildString {
            append("OAuth ")
            append(
                params
                    .toSortedMap()
                    .map { "${it.key}=\"${encode(it.value)}\"" }
                    .joinToString(separator = ", ")
            )
        }
    }

    private fun createSignKey(oAuthToken: OAuthToken?): SecretKeySpec {
        val encodedConsumerSecret = encode(consumerSecret)
        val encodedTokenSecret = encode(oAuthToken?.tokenSecret ?: "")

        return SecretKeySpec(
            "${encodedConsumerSecret}&${encodedTokenSecret}".toByteArray(),
            OAUTH_VALUE_SIGNATURE_ALGORITHM
        )
    }

    private fun createSignBaseString(request: Request, oauthParams: Map<String, String>): String {
        val requestMethod = request.method.toUpperCase()
        val requestUrl = request.url.toString().split("?")[0]

        val queryParams = extractQueryParams(request)
        Log.d("HOGE", queryParams.toString())
        val bodyParams = extractFormBodyParams(request)
        val requestParams = (oauthParams + queryParams + bodyParams)
            .toSortedMap()
            .map { "${it.key}=${encode(it.value)}" }
            .joinToString(separator = "&")

        return buildString {
            append(encode(requestMethod))
            append("&")
            append(encode(requestUrl))
            append("&")
            append(encode(requestParams))
        }
    }

    private fun createSignature(signKey: SecretKeySpec, signBaseString: String): String {
        val mac = Mac.getInstance(OAUTH_VALUE_SIGNATURE_ALGORITHM)
        mac.init(signKey)
        val bytes = mac.doFinal(signBaseString.toByteArray())
        return Base64.getEncoder()
            .encodeToString(bytes)
            .replace("\r\n", "")
    }

    private fun extractFormBodyParams(request: Request): Map<String, String> {
        val bodyParams = mutableMapOf<String, String>()
        val formBody = request.body as? FormBody
        formBody?.let {
            repeat(it.size) { i -> bodyParams[it.name(i)] = it.value(i) }
        }
        return bodyParams
    }

    private fun extractQueryParams(request: Request): Map<String, String> {
        val url = request.url
        return url.queryParameterNames.associateBy({it}, {url.queryParameter(it)!!})
    }

    private fun createTimestamp(): String {
        return (System.currentTimeMillis() / 1000).toString()
    }

    private fun createNonce(): String {
        return UUID.randomUUID().toString()
    }

    private fun encode(s: String): String {
        return URLEncoder.encode(s, "UTF-8")
    }
}