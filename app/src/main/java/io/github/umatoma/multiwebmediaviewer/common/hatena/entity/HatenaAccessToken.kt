package io.github.umatoma.multiwebmediaviewer.common.hatena.entity

import com.google.gson.Gson

class HatenaAccessToken(
    val token: String,
    val tokenSecret: String,
    val urlName: String,
    val displayName: String
) {

    companion object {
        fun fromJSON(json: String): HatenaAccessToken {
            return Gson().fromJson(json, HatenaAccessToken::class.java)
        }
    }

    fun toJSON(): String {
        return Gson().toJson(this)
    }
}
