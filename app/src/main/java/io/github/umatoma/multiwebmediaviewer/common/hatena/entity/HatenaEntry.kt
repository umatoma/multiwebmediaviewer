package io.github.umatoma.multiwebmediaviewer.common.hatena.entity

import com.google.gson.Gson
import com.google.gson.JsonParser
import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.net.URL

class HatenaEntry(
    @SerializedName("title")
    val title: String,
    @SerializedName("url")
    val url: String,
    @SerializedName("image")
    val image: String? = null,
    @SerializedName("screenshot")
    val screenshot: String? = null,
    @SerializedName("count")
    val count: Int,
    @SerializedName("bookmarks")
    val bookmarkList: ArrayList<HatenaBookmark>? = null
) : Serializable {

    companion object {
        fun fromJSON(json: String): HatenaEntry {
            return Gson().fromJson(json, HatenaEntry::class.java)
        }

        fun fromListJSON(json: String): List<HatenaEntry> {
            val jsonArray = JsonParser.parseString(json).asJsonArray
            return jsonArray.map { Gson().fromJson(it, HatenaEntry::class.java) }
        }
    }

    enum class Kind(val id: String) {
        NEW("new"),
        HOT("hot")
    }

    enum class Category(val id: String, val numId: String) {
        ALL("all", "315767106563433873"),
        GENERAL("general", "315756341902288872"),
        SOCIAL("social", "301816409282464093"),
        ECONOMICS("economics", "300989576564947867"),
        LIFE("life", "244148959988020477"),
        KNOWLEDGE("knowledge", "315890158150969179"),
        IT("it", "261248828312298389"),
        ENTERTAINMENT("entertainment", "302115476501939948"),
        GAME("game", "297347994088281699"),
        FUN("fun", "302115476506048236"),
        CURRENT_EVENTS("current_events", "83497569613451046"),
    }

    fun getImageUrl(): String? {
        if (!image.isNullOrEmpty()) return image
        if (!screenshot.isNullOrEmpty()) return screenshot
        return null
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