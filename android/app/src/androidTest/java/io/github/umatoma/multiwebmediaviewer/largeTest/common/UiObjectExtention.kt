package io.github.umatoma.multiwebmediaviewer.largeTest.common

import androidx.test.uiautomator.UiObject
import androidx.test.uiautomator.UiSelector

fun UiObject.getListItem(index: Int): UiObject {
    return getChild(UiSelector().className("android.view.ViewGroup").index(index))
}