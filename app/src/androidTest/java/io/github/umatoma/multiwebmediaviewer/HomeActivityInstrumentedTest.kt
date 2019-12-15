package io.github.umatoma.multiwebmediaviewer

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import androidx.test.uiautomator.Until
import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test


class HomeActivityInstrumentedTest {

    private val instrumentation = InstrumentationRegistry.getInstrumentation()
    private val device: UiDevice = UiDevice.getInstance(instrumentation)
    private val packageName = instrumentation.targetContext.packageName

    @BeforeEach
    fun beforeAll() {
        val context: Context = ApplicationProvider.getApplicationContext()
        val intent = context.packageManager.getLaunchIntentForPackage(packageName)

        context.startActivity(intent)

        device.wait(Until.hasObject(By.pkg(packageName).depth(0)), 5000)
    }

    @Test
    fun it_should_display_sign_in_button_for_Hatena() {
        val navButton = device.findObject(UiSelector().resourceIdMatches(".+menuHatenaEntryList"))
        navButton.click()

        assertThat(device.findObject(UiSelector().text("はてなID 認証")).exists()).isTrue()
    }

    @Test
    fun it_should_display_sign_in_button_for_Feedly() {
        val navButton = device.findObject(UiSelector().resourceIdMatches(".+menuFeedlyEntryList"))
        navButton.click()

        assertThat(device.findObject(UiSelector().text("FEEDLY 認証")).exists()).isTrue()
    }

    @Test
    fun it_should_display_settings_view() {
        val navButton = device.findObject(UiSelector().resourceIdMatches(".+menuSettings"))
        navButton.click()

        // TBD
    }
}
