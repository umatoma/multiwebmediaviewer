package io.github.umatoma.multiwebmediaviewer.common.hatena.entity

import com.google.gson.Gson

class HatenaUser(
    val name: String,
    val plususer: Boolean,
    val private: Boolean,
    val is_oauth_twitter: Boolean,
    val is_oauth_evernote: Boolean,
    val is_oauth_facebook: Boolean,
    val is_oauth_mixi_check: Boolean
) {
    companion object {
        fun fromJSON(json: String): HatenaUser {
            return Gson().fromJson(json, HatenaUser::class.java)
        }
    }

    fun toJSON(): String {
        return Gson().toJson(this)
    }
}
