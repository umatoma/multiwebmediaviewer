package io.github.umatoma.multiwebmediaviewer.model.feedly.entity

import com.google.gson.Gson

class FeedlyStream(
    val id: String,
    val updated: String? = null,
    val continuation: String? = null,
    val title: String? = null,
    val direction: String? = null,
    val items: List<FeedlyEntry>
) {
    companion object {

        fun fromJSON(json: String): FeedlyStream {
            return Gson().fromJson(json, FeedlyStream::class.java)
        }
    }
}