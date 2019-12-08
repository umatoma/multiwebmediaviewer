package io.github.umatoma.multiwebmediaviewer.view.feedlyAuth.viewModel

import androidx.lifecycle.Observer
import com.google.common.truth.Truth.assertThat
import io.github.umatoma.multiwebmediaviewer.applyInstantTaskExecutor
import io.github.umatoma.multiwebmediaviewer.applyTestDispatcher
import io.github.umatoma.multiwebmediaviewer.model.common.OAuth2LocalCallbackServer
import io.github.umatoma.multiwebmediaviewer.model.feedly.entity.FeedlyAccessToken
import io.github.umatoma.multiwebmediaviewer.model.feedly.repository.FeedlyRepository
import io.mockk.*
import kotlinx.coroutines.runBlocking
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

object FeedlyAuthViewModelTest : Spek({

    applyTestDispatcher()
    applyInstantTaskExecutor()

    val feedlyRepositoryMock = mockk<FeedlyRepository>()
    val localCallbackServerMock = mockk<OAuth2LocalCallbackServer>()
    lateinit var viewModel: FeedlyAuthViewModel

    beforeEachTest {
        viewModel = FeedlyAuthViewModel(
            feedlyRepositoryMock,
            localCallbackServerMock
        )
    }

    afterEachTest {
        clearAllMocks()
    }

    describe("#startLocalCallbackServer()") {

        it("should start localCallbackServer") {
            every { localCallbackServerMock.start() } just Runs

            viewModel.startLocalCallbackServer()

            verify { localCallbackServerMock.start() }
        }
    }

    describe("#stopLocalCallbackServer()") {

        it("should stop localCallbackServer") {
            every { localCallbackServerMock.stop() } just Runs

            viewModel.stopLocalCallbackServer()

            verify { localCallbackServerMock.stop() }
        }
    }

    describe("#getAuthenticationUrl()") {

        it("should return authentication url") {
            every { feedlyRepositoryMock.getAuthenticationUrl() } returns "TEST_URL"

            val authenticationUrl = viewModel.getAuthenticationUrl()

            assertThat(authenticationUrl).isEqualTo("TEST_URL")
        }
    }

    describe("#fetchAccessToken()") {

        val testCode = "TEST_CODE"
        val testAccessToken = createTestAccessToken()
        val testException = Exception("TEST_EXCEPTION")
        val accessTokenObserverMock = mockk<Observer<FeedlyAccessToken>>(relaxUnitFun = true)
        val exceptionObserverMock = mockk<Observer<Exception>>(relaxUnitFun = true)

        beforeEachTest {
            coEvery { feedlyRepositoryMock.getAccessToken(testCode) } returns testAccessToken
            every { feedlyRepositoryMock.putAccessToken(testAccessToken) } just Runs
            viewModel.accessTokenLiveData.observeForever(accessTokenObserverMock)
            viewModel.exceptionLiveData.observeForever(exceptionObserverMock)
        }

        it("should get access token from repository") {
            runBlocking { viewModel.fetchAccessToken(testCode).join() }

            coVerify { feedlyRepositoryMock.getAccessToken(testCode) }
        }

        it("should post access token to repository") {
            runBlocking { viewModel.fetchAccessToken(testCode).join() }

            verify { feedlyRepositoryMock.putAccessToken(testAccessToken) }
        }

        it("should post access token to observer") {
            runBlocking { viewModel.fetchAccessToken(testCode).join() }

            verify { accessTokenObserverMock.onChanged(testAccessToken) }
        }

        context("when exception has happened") {

            it("should post exception to observer") {
                coEvery { feedlyRepositoryMock.getAccessToken(testCode) } throws testException

                runBlocking { viewModel.fetchAccessToken(testCode).join() }

                verify { exceptionObserverMock.onChanged(testException) }
            }
        }

    }

})

fun createTestAccessToken(): FeedlyAccessToken {
    return FeedlyAccessToken(
        id = "TEST_ID",
        accessToken = "TEST_ACCESS_TOKEN",
        refreshToken = "TEST_REFRESH_TOKEN",
        expiresIn = 99999,
        tokenType = "TEST_TOKEN_TYPE",
        plan = "TEST_PLAN"
    )
}
