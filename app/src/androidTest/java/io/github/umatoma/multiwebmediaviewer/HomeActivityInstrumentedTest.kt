package io.github.umatoma.multiwebmediaviewer

import androidx.test.filters.LargeTest
import androidx.test.uiautomator.UiSelector
import com.google.common.truth.Truth.assertThat
import com.google.gson.Gson
import io.github.umatoma.multiwebmediaviewer.model.feedly.entity.*
import io.github.umatoma.multiwebmediaviewer.model.hatena.entity.HatenaEntry
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.junit.jupiter.api.*
import java.net.URL

@LargeTest
class HomeActivityInstrumentedTest : BaseInstrumentedTest() {

    companion object {

        private val hatenaMockWebServer = MockWebServer()
        private val feedlyMockWebServer = MockWebServer()

        @BeforeAll
        @JvmStatic
        internal fun beforeAll() {
            hatenaMockWebServer.dispatcher = hatenaMockWebServerDispatcher
            hatenaMockWebServer.start(URL(BuildConfig.HATENA_API_AUTH_BASE_URL).port)

            feedlyMockWebServer.dispatcher = feedlyMockWebServerDispatcher
            feedlyMockWebServer.start(URL(BuildConfig.FEEDLY_API_BASE_URL).port)

            clearSharedPreferences()
        }

        @AfterAll
        @JvmStatic
        internal fun afterAll() {
            hatenaMockWebServer.shutdown()
            feedlyMockWebServer.shutdown()
        }
    }

    @BeforeEach
    fun beforeAll() {
        startActivity()
    }

    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation::class)
    inner class hatenaTest {
        @Test
        @Order(1)
        fun hatenaSignInTest() {
            device.findObjectByResourceIdMatches(".+menuHatenaEntryList").click()
            device.findObjectByText("はてなID 認証").click()

            device.waitByPkg("com.android.chrome")
            device.pressBack()

            device.findObjectByClassName("android.widget.EditText").text = "TEST_TOKEN"
            device.findObjectByText("決定").clickAndWaitForNewWindow()

            assertThat(device.findObjectByText("はてなID 認証").exists()).isFalse()
        }

        @Test
        @Order(2)
        fun hatenaEntryListTest() {
            device.findObjectByResourceIdMatches(".+menuHatenaEntryList").click()

            assertThat(device.findObjectByText("新着").exists()).isTrue()
            assertThat(device.findObjectByText("人気エントリー").exists()).isTrue()

            device.findObject(UiSelector().scrollable(true)).also { listView ->
                listView.getListItem(0).also { item ->
                    assertThat(item.getChild(UiSelector().text("TITLE_1")).exists()).isTrue()
                    assertThat(item.getChild(UiSelector().text("one.com")).exists()).isTrue()
                    assertThat(item.getChild(UiSelector().text("111")).exists()).isTrue()
                }
                listView.getListItem(1).also { item ->
                    assertThat(item.getChild(UiSelector().text("TITLE_2")).exists()).isTrue()
                    assertThat(item.getChild(UiSelector().text("two.com")).exists()).isTrue()
                    assertThat(item.getChild(UiSelector().text("222")).exists()).isTrue()
                }
            }

            assertThat(device.findObjectByText("続きを読み込む").exists()).isTrue()
        }

        @Test
        @Order(3)
        fun hatenaSignOutTest() {
            device.findObjectByResourceIdMatches(".+menuSettings").click()
            device.findObjectsByText("ログアウト")[0].click()

            device.findObjectByResourceIdMatches(".+menuHatenaEntryList").click()
            assertThat(device.findObjectByText("はてなID 認証").exists()).isTrue()
        }
    }

    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation::class)
    inner class feedlyTest {
        @Test
        @Order(1)
        fun feedlySignInTest() {
            device.findObjectByResourceIdMatches(".+menuFeedlyEntryList").click()
            device.findObjectByText("FEEDLY 認証").click()

            device.waitByPkg("com.android.chrome")
            device.pressBack()

            device.findObjectByClassName("android.widget.EditText").text = "TEST_TOKEN"
            device.findObjectByText("決定").clickAndWaitForNewWindow()

            assertThat(device.findObjectByText("Feedly 認証").exists()).isFalse()
        }

        @Test
        @Order(2)
        fun feedlyEntryListTest() {
            device.findObjectByResourceIdMatches(".+menuFeedlyEntryList").click()

            assertThat(device.findObjectByText("Feeds").exists()).isTrue()
            assertThat(device.findObjectByText("Collections").exists()).isTrue()

            device.findObject(UiSelector().scrollable(true)).also { listView ->
                listView.getListItem(0).also { item ->
                    assertThat(item.getChild(UiSelector().text("TITLE_1")).exists()).isTrue()
                    assertThat(item.getChild(UiSelector().text("ORIGIN_1")).exists()).isTrue()
                }
                listView.getListItem(1).also { item ->
                    assertThat(item.getChild(UiSelector().text("TITLE_2")).exists()).isTrue()
                    assertThat(item.getChild(UiSelector().text("ORIGIN_2")).exists()).isTrue()
                }
            }

            assertThat(device.findObjectByText("続きを読み込む").exists()).isTrue()
        }

        @Test
        @Order(3)
        fun feedlySignOutTest() {
            device.findObjectByResourceIdMatches(".+menuSettings").click()
            device.findObjectsByText("ログアウト")[1].click()

            device.findObjectByResourceIdMatches(".+menuFeedlyEntryList").click()
            assertThat(device.findObjectByText("FEEDLY 認証").exists()).isTrue()
        }
    }
}

private val hatenaMockWebServerDispatcher = object : Dispatcher() {
    override fun dispatch(request: RecordedRequest): MockResponse {
        val path = URL(request.requestUrl.toString()).path
        val response = MockResponse()
        return when (path) {
            "/oauth/initiate" -> response.setBody(
                "oauth_token=TOKEN&oauth_token_secret=SECRET&oauth_callback_confirmed=true"
            )
            "/oauth/token" -> response.setBody(
                "oauth_token=TOKEN&oauth_token_secret=SECRET&url_name=U_NAME&display_name=D_NAME"
            )
            "/api/ipad.newentry.json" -> response.setBody(
                Gson().toJson(
                    listOf(
                        HatenaEntry(title = "TITLE_1", url = "http://one.com/a/", count = 111),
                        HatenaEntry(title = "TITLE_2", url = "http://two.com/a/", count = 222)
                    )
                )
            )
            "/api/ipad.hotentry.json" -> response.setBody(buildString {
                append("[]")
            })
            else -> response.setResponseCode(404)
        }
    }
}

private val feedlyMockWebServerDispatcher = object : Dispatcher() {
    override fun dispatch(request: RecordedRequest): MockResponse {
        val path = URL(request.requestUrl.toString()).path
        val response = MockResponse()
        return when (path) {
            "/v3/auth/token" -> response.setBody(
                Gson().toJson(FeedlyAccessToken(
                    id = "TEST_ID",
                    accessToken = "TEST_ACCESS_TOKEN",
                    refreshToken = "TEST_REFRESH_TOKEN",
                    expiresIn = 99999,
                    tokenType = "Bearer",
                    plan = "standard",
                    state = "state.passed.in"
                ))
            )
            "/v3/streams/contents" -> response.setBody(
                Gson().toJson(
                    FeedlyStream(
                        id = "TEST_ID",
                        items = listOf(
                            FeedlyEntry(
                                id = "ID_1",
                                title = "TITLE_1",
                                origin = FeedlyOrigin("ONE", "ORIGIN_1", "http://one.com/a/")
                            ),
                            FeedlyEntry(
                                id = "ID_2",
                                title = "TITLE_2",
                                origin = FeedlyOrigin("TWO", "ORIGIN_2", "http://two.com/a/")
                            )
                        )
                    )
                )
            )
            "/v3/collections" -> response.setBody(
                Gson().toJson(listOf(
                    FeedlyCollection(id = "ID_1", label = "LABEL_1"),
                    FeedlyCollection(id = "ID_2", label = "LABEL_2")
                ))
            )
            else -> response.setResponseCode(404)
        }
    }

}
