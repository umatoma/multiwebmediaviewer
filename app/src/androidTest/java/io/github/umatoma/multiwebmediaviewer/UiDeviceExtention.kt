package io.github.umatoma.multiwebmediaviewer

import androidx.test.uiautomator.*

fun UiDevice.findObjectByText(text: String): UiObject {
    return findObject(UiSelector().text(text))
}

fun UiDevice.findObjectByClassName(className: String): UiObject {
    return findObject(UiSelector().className(className))
}

fun UiDevice.findObjectByResourceIdMatches(regex: String): UiObject {
    return findObject(UiSelector().resourceIdMatches(regex))
}

fun UiDevice.findObjectsByText(text: String): MutableList<UiObject2> {
    return findObjects(By.text(text))
}

fun UiDevice.waitByText(text: String): Boolean {
    return wait(
        Until.hasObject(By.text(text).depth(0)),
        5000
    )
}

fun UiDevice.waitByPkg(applicationPackage: String): Boolean {
    return wait(
        Until.hasObject(By.pkg(applicationPackage).depth(0)),
        5000
    )
}