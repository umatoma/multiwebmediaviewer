package io.github.umatoma.multiwebmediaviewer.model.feedly.entity

class FeedlyFeed(
    val id: String,
    val feedId: String,
    val title: String,
    val description: String? = null,
    val website: String? = null
)