package io.github.umatoma.multiwebmediaviewer.common.hatena.entity

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.net.URL

class HatenaEntry(
    @SerializedName("title")
    val title: String,
    @SerializedName("url")
    val url: String,
    @SerializedName("entry_url")
    val entryUrl: String,
    @SerializedName("screenshot")
    val imageUrl: String? = null,
    @SerializedName("count")
    val count: Int,
    @SerializedName("bookmarks")
    val bookmarkList: List<HatenaBookmark>? = null
): Serializable {

    companion object {
        fun fromJSON(json: String): HatenaEntry {
            return Gson().fromJson(json, HatenaEntry::class.java)
        }
    }

    enum class Category(val id: String) {
        ALL("all")
    }

    fun getUrlHost(): String {
        return URL(getRootUrl()).host
    }

    private fun getRootUrl(): String {
        val u = URL(url)
        val path = u.file.substring(0, u.file.lastIndexOf('/'))
        return u.protocol.toString() + "://" + u.host + path
    }
}