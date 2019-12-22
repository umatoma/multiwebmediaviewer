package io.github.umatoma.multiwebmediaviewer.view.home.viewModel

import androidx.lifecycle.Observer
import com.google.common.truth.Truth.*
import io.github.umatoma.multiwebmediaviewer.common.applyInstantTaskExecutor
import io.github.umatoma.multiwebmediaviewer.common.applyTestDispatcher
import io.github.umatoma.multiwebmediaviewer.model.feedly.entity.*
import io.github.umatoma.multiwebmediaviewer.model.feedly.repository.FeedlyRepository
import io.mockk.*
import kotlinx.coroutines.runBlocking
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

object FeedlyEntryListViewModelTest : Spek({

    applyTestDispatcher()
    applyInstantTaskExecutor()

    val feedlyRepositoryMock = mockk<FeedlyRepository>()
    val viewModel: FeedlyEntryListViewModel by memoized {
        FeedlyEntryListViewModel(feedlyRepositoryMock)
    }

    afterEachTest {
        clearAllMocks()
    }

    describe("#fetchFeedlyEntryList()") {

        val testAccessToken = createAccessToken()
        val testStream = createStream()
        val entryListObserverMock = mockk<Observer<List<FeedlyEntry>>>(relaxUnitFun = true)
        val isFetchingObserverMock = mockk<Observer<Boolean>>(relaxUnitFun = true)

        beforeEachTest {
            every { feedlyRepositoryMock.getAccessToken() } returns testAccessToken
            coEvery { feedlyRepositoryMock.getStreamContents(any(), null) } returns testStream
            viewModel.feedlyEntryListLiveData.observeForever(entryListObserverMock)
            viewModel.isFetchingLiveData.observeForever(isFetchingObserverMock)
        }

        it("should get stream with category all from repository") {
            runBlocking { viewModel.fetchFeedlyEntryList().join() }

            coVerify {
                feedlyRepositoryMock.getStreamContents(
                    category = withArg {
                        assertThat(it.label).isEqualTo(FeedlyCategory.Label.GLOBAL_ALL.value)
                    },
                    prevStream = null
                )
            }
        }

        it("should post entry list to observer") {
            runBlocking { viewModel.fetchFeedlyEntryList().join() }

            verify { entryListObserverMock.onChanged(testStream.items) }
        }

        it("should post isFetching flag to observer") {
            runBlocking { viewModel.fetchFeedlyEntryList().join() }

            verifyOrder {
                isFetchingObserverMock.onChanged(true)
                isFetchingObserverMock.onChanged(false)
            }
        }

        context("when previous stream is given") {

            val testPrevStream = createStream(id = "TEST_ID_PREV")
            val testPrevEntryList = listOf(
                createEntry(id = "TEST_ID_PREV_1"),
                createEntry(id = "TEST_ID_PREV_2")
            )

            beforeEachTest {
                viewModel.feedlyEntryListLiveData.postValue(testPrevEntryList)
                coEvery {
                    feedlyRepositoryMock.getStreamContents(
                        any(),
                        testPrevStream
                    )
                } returns testStream
            }

            it("should post entry list with previous entry list to observer") {
                runBlocking { viewModel.fetchFeedlyEntryList(testPrevStream).join() }

                verify { entryListObserverMock.onChanged(testPrevEntryList + testStream.items) }
            }
        }
    }

    describe("#setCategory()") {

        val testAccessToken = createAccessToken()
        val testCategory = createCategory()
        val testStream = createStream()
        val categoryObserverMock = mockk<Observer<FeedlyCategory>>(relaxUnitFun = true)
        val entryListObserverMock = mockk<Observer<List<FeedlyEntry>>>(relaxUnitFun = true)

        beforeEachTest {
            every { feedlyRepositoryMock.getAccessToken() } returns testAccessToken
            coEvery { feedlyRepositoryMock.getStreamContents(any(), null) } returns testStream
            viewModel.feedlyCategoryLiveData.observeForever(categoryObserverMock)
            viewModel.feedlyEntryListLiveData.observeForever(entryListObserverMock)
        }

        it("should post category to observer") {
            runBlocking { viewModel.setCategory(testCategory).join() }

            verify { categoryObserverMock.onChanged(testCategory) }
        }

        it("should post entry list after empty entry list to observer") {
            runBlocking { viewModel.setCategory(testCategory).join() }

            verifyOrder {
                entryListObserverMock.onChanged(listOf())
                entryListObserverMock.onChanged(testStream.items)
            }
        }
    }
})

private fun createCategory(): FeedlyCategory {
    return FeedlyCategory(
        id = "TEST_ID",
        label = "TEST_LABEL"
    )
}

private fun createAccessToken(): FeedlyAccessToken {
    return FeedlyAccessToken(
        id = "TEST_ID",
        accessToken = "TEST_ACCESS_TOKEN",
        refreshToken = "TEST_REFRESH_TOKEN",
        expiresIn = 99999,
        tokenType = "TEST_TOKEN_TYPE",
        plan = "TEST_PLAN"
    )
}

private fun createStream(id: String = "TEST_ID"): FeedlyStream {
    return FeedlyStream(
        id = id,
        items = listOf(
            createEntry("TEST_ID_1"),
            createEntry("TEST_ID_2")
        )
    )
}

private fun createEntry(id: String): FeedlyEntry {
    return FeedlyEntry(
        id = id,
        title = "TEST_TITLE",
        origin = FeedlyOrigin(
            streamId = "TEST_STREAM_ID",
            title = "TEST_TITLE",
            htmlUrl = "TEST_HTML_URL"
        )
    )
}