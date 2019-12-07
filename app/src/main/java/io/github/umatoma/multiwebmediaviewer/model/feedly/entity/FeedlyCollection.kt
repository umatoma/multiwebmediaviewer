package io.github.umatoma.multiwebmediaviewer.model.feedly.entity

import com.google.gson.Gson
import com.google.gson.JsonParser

class FeedlyCollection(
    id: String,
    label: String,
    description: String?,
    val cover: String? = null,
    val feeds: List<FeedlyFeed>? = null
) : FeedlyCategory(id, label, description) {

    companion object {

        fun fromListJSON(json: String): List<FeedlyCollection> {
            val jsonArray = JsonParser.parseString(json).asJsonArray
            return jsonArray.map { Gson().fromJson(it, FeedlyCollection::class.java) }
        }
    }
}