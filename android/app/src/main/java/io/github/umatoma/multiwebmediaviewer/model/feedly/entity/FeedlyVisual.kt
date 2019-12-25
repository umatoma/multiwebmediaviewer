package io.github.umatoma.multiwebmediaviewer.model.feedly.entity

import java.math.BigInteger

class FeedlyVisual(
    val url: String,
    val width: Int? = null,
    val height: Int?  = null,
    val contentType: String?  = null,
    val processor: String?  = null,
    val edgeCacheUrl: String?  = null,
    val expirationDate: BigInteger? = null

)