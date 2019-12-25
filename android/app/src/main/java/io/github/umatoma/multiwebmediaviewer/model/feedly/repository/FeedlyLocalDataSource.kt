package io.github.umatoma.multiwebmediaviewer.model.feedly.repository

import android.content.Context
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import io.github.umatoma.multiwebmediaviewer.model.feedly.entity.FeedlyAccessToken
import java.lang.Exception

class FeedlyLocalDataSource(private val context: Context) {

    private val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    companion object {
        const val KEY_ACCESS_TOKEN = "KEY_FEEDLY_ACCESS_TOKEN"
    }

    fun putAccessToken(accessToken: FeedlyAccessToken) {
        sharedPreferences.edit {
            putString(KEY_ACCESS_TOKEN, accessToken.toJSON())
        }
    }

    fun getAccessToken(): FeedlyAccessToken {
        val accessTokenString = sharedPreferences.getString(KEY_ACCESS_TOKEN, null)
        accessTokenString?.let {
            return FeedlyAccessToken.fromJSON(it)
        }
        throw Exception("Failed to get access token.")
    }

    fun isSignedIn(): Boolean {
        return try {
            getAccessToken()
            true
        } catch (e: Exception) {
            false
        }
    }

    fun signOut() {
        sharedPreferences.edit {
            remove(KEY_ACCESS_TOKEN)
        }
    }
}