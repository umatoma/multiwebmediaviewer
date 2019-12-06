package io.github.umatoma.multiwebmediaviewer.model.hatena.repository

import android.content.Context
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import io.github.umatoma.multiwebmediaviewer.model.hatena.entity.HatenaAccessToken
import java.lang.Exception

class HatenaLocalRepository(context: Context) {

    private val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    companion object {
        const val KEY_ACCESS_TOKEN = "HATENA_ACCESS_TOKEN"
    }

    fun putAccessToken(accessToken: HatenaAccessToken) {
        sharedPreferences.edit {
            putString(KEY_ACCESS_TOKEN, accessToken.toJSON())
        }
    }

    fun getAccessToken(): HatenaAccessToken {
        val accessTokenString = sharedPreferences.getString(KEY_ACCESS_TOKEN, null)
        accessTokenString?.let {
            return HatenaAccessToken.fromJSON(it)
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