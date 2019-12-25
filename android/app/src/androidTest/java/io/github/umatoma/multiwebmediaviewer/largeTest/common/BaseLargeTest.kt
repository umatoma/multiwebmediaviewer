package io.github.umatoma.multiwebmediaviewer.largeTest.common

import android.content.Context
import android.content.Intent
import androidx.preference.PreferenceManager
import androidx.test.core.app.ApplicationProvider
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice


open class BaseLargeTest {

    companion object {

        private val instrumentation = InstrumentationRegistry.getInstrumentation()
        private val packageName = instrumentation.targetContext.packageName

        val device: UiDevice = UiDevice.getInstance(instrumentation)

        fun clearSharedPreferences() {
            PreferenceManager.getDefaultSharedPreferences(instrumentation.targetContext)
                .edit()
                .clear()
                .commit()
        }

        fun startActivity() {
            val context: Context = ApplicationProvider.getApplicationContext()
            val intent = context.packageManager.getLaunchIntentForPackage(packageName)?.also {
                it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            }
            context.startActivity(intent)
            device.waitByPkg(
                packageName
            )
        }
    }
}