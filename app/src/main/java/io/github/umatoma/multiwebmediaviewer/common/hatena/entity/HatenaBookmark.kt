package io.github.umatoma.multiwebmediaviewer.common.hatena.entity

import com.google.gson.Gson

class HatenaBookmark(
    val user: String,
    val comment: String,
    val timestamp: String,
    val tags: List<String>
) {

    companion object {

        fun fromJSON(json: String): HatenaBookmark {
            return Gson().fromJson(json, HatenaBookmark::class.java)
        }
    }
}