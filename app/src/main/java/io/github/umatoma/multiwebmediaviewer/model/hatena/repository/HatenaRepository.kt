package io.github.umatoma.multiwebmediaviewer.model.hatena.repository

import android.content.Context
import io.github.umatoma.multiwebmediaviewer.model.hatena.entity.HatenaAccessToken
import io.github.umatoma.multiwebmediaviewer.model.hatena.entity.HatenaEntry
import io.github.umatoma.multiwebmediaviewer.model.hatena.entity.HatenaRequestToken

class HatenaRepository(
    private val localDataSource: HatenaLocalDataSource,
    private val remoteDataSource: HatenaRemoteDataSource
) {

    class Factory(private val context: Context) {

        fun create(): HatenaRepository {
            val localRepository = HatenaLocalDataSource(context)
            val remoteRepository = HatenaRemoteDataSource(
                getLocalAccessToken = { localRepository.getAccessToken() }
            )
            return HatenaRepository(localRepository, remoteRepository)
        }
    }

    suspend fun getRequestToken(): HatenaRequestToken {
        return remoteDataSource.getRequestToken()
    }

    fun getAuthenticationUrl(requestToken: HatenaRequestToken): String {
        return remoteDataSource.getAuthenticationUrl(requestToken)
    }

    suspend fun getAccessToken(requestToken: HatenaRequestToken, verifier: String): HatenaAccessToken {
        return remoteDataSource.getAccessToken(requestToken, verifier)
    }

    suspend fun getEntry(url: String): HatenaEntry {
        return remoteDataSource.getEntry(url)
    }

    suspend fun getHotEntryList(category: HatenaEntry.Category, pageNumber: Int): List<HatenaEntry> {
        return remoteDataSource.getHotEntryList(category, pageNumber)
    }

    suspend fun getNewEntryList(category: HatenaEntry.Category, pageNumber: Int): List<HatenaEntry> {
        return remoteDataSource.getNewEntryList(category, pageNumber)
    }

    fun putAccessToken(accessToken: HatenaAccessToken) {
        return localDataSource.putAccessToken(accessToken)
    }

    fun getAccessToken(): HatenaAccessToken {
        return localDataSource.getAccessToken()
    }

    fun isSignedIn(): Boolean {
        return localDataSource.isSignedIn()
    }

    fun signOut() {
        return localDataSource.signOut()
    }
}