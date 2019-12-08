package io.github.umatoma.multiwebmediaviewer.model.feedly.repository

import android.content.Context
import io.github.umatoma.multiwebmediaviewer.model.feedly.entity.FeedlyAccessToken
import io.github.umatoma.multiwebmediaviewer.model.feedly.entity.FeedlyCategory
import io.github.umatoma.multiwebmediaviewer.model.feedly.entity.FeedlyCollection
import io.github.umatoma.multiwebmediaviewer.model.feedly.entity.FeedlyStream

class FeedlyRepository(
    private val localDataSource: FeedlyLocalDataSource,
    private val remoteDataSource: FeedlyRemoteDataSource
) {

    class Factory(val context: Context) {

        fun create(): FeedlyRepository {
            val localRepository = FeedlyLocalDataSource(context)
            val remoteRepository = FeedlyRemoteDataSource(
                getLocalAccessToken = { localRepository.getAccessToken() }
            )
            return FeedlyRepository(localRepository, remoteRepository)
        }
    }

    fun getRedirectUrl(): String {
        return remoteDataSource.getRedirectUrl()
    }

    fun getAuthenticationUrl(): String {
        return remoteDataSource.getAuthenticationUrl()
    }

    suspend fun getAccessToken(code: String): FeedlyAccessToken {
        return remoteDataSource.getAccessToken(code)
    }

    suspend fun getStreamContents(category: FeedlyCategory, prevStream: FeedlyStream?): FeedlyStream {
        return remoteDataSource.getStreamContents(category, prevStream)
    }

    suspend fun getCollections(): List<FeedlyCollection> {
        return remoteDataSource.getCollections()
    }

    fun putAccessToken(accessToken: FeedlyAccessToken) {
        return localDataSource.putAccessToken(accessToken)
    }

    fun getAccessToken(): FeedlyAccessToken {
        return localDataSource.getAccessToken()
    }

    fun isSignedIn(): Boolean {
        return localDataSource.isSignedIn()
    }

    fun signOut() {
        return localDataSource.signOut()
    }
}