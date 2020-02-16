package io.github.umatoma.multiwebmediaviewer.model.feedly.entity

class FeedlyEntry(
    val id: String,
    val title: String,
    val canonicalUrl: String? = null,
    val visual: FeedlyVisual? = null,
    val engagement: Int? = null,
    val origin: FeedlyOrigin,
    val alternate: List<FeedlyAlternate> = listOf()
) {
    fun getEntryUrl(): String? {
        if (alternate.isNotEmpty()) {
            return alternate[0].href
        }
        return canonicalUrl
    }
}