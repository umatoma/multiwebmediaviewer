package io.github.umatoma.multiwebmediaviewer.model.feedly.entity

import java.io.Serializable

class FeedlyCategory(
    val id: String,
    val label: String,
    val description: String? = null
): Serializable {
    companion object {
        fun fromUserIdAndLabel(userId: String, label: Label): FeedlyCategory {
            val id = "user/${userId}/category/${label.value}"
            return FeedlyCategory(id, label.value)
        }
    }

    enum class Label(val value: String) {
        GLOBAL_ALL("global.all")
    }
}