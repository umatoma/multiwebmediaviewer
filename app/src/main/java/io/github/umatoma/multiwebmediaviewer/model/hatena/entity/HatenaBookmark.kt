package io.github.umatoma.multiwebmediaviewer.model.hatena.entity

import com.google.gson.Gson
import java.io.Serializable

class HatenaBookmark(
    val user: String,
    val comment: String,
    val timestamp: String,
    val tags: List<String>
): Serializable {

    companion object {

        fun fromJSON(json: String): HatenaBookmark {
            return Gson().fromJson(json, HatenaBookmark::class.java)
        }
    }

    fun getUserImageUrl(): String {
        return buildString {
            append("https://cdn.profile-image.st-hatena.com/users/")
            append(user)
            append("/profile.png")
        }
    }
}