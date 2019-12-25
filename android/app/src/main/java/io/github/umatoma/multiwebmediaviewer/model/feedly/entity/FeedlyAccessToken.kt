package io.github.umatoma.multiwebmediaviewer.model.feedly.entity

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

class FeedlyAccessToken(
    @SerializedName("id")
    val id: String,
    @SerializedName("access_token")
    val accessToken: String,
    @SerializedName("refresh_token")
    val refreshToken: String,
    @SerializedName("expires_in")
    val expiresIn: Int,
    @SerializedName("token_type")
    val tokenType: String,
    @SerializedName("plan")
    val plan: String,
    @SerializedName("state")
    val state: String? = null
) {

    companion object {
        fun fromJSON(json: String): FeedlyAccessToken {
            return Gson().fromJson(json, FeedlyAccessToken::class.java)
        }
    }

    fun toJSON(): String {
        return Gson().toJson(this)
    }
}