package io.github.umatoma.multiwebmediaviewer.common.hatena.entity

import java.net.URL

class HatenaEntry(
    val title: String,
    val titleLastEditor: String? = null,
    val url: String,
    val rootUrl: String = HatenaEntry.urlToRootUrl(url),
    val isInvalidUrl: Boolean = false,
    val entryUrl: String,
    val smartphoneAppEntryUrl: String? = null,
    val imageUrl: String? = null,
    val imageHatenaUrl: String? = null,
    val imageLastEditor: String? = null,
    val faviconUrl: String? = null,
    val count: Int,
    val hasAsin: Boolean = false,
    val eid: String? = null
) {
    companion object {
        private fun urlToRootUrl(urlString: String): String {
            val url = URL(urlString)
            val path = url.file.substring(0, url.file.lastIndexOf('/'))
            return url.protocol.toString() + "://" + url.host + path
        }
    }

    enum class Category(val id: String) {
        ALL("all")
    }
}