package com.hw.benchmark

import androidx.benchmark.macro.FrameTimingMetric
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.StartupTimingMetric
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Direction
import androidx.test.uiautomator.Until
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * This is an example startup benchmark.
 *
 * It navigates to the device's home screen, and launches the default activity.
 *
 * Before running this benchmark:
 * 1) switch your app's active build variant in the Studio (affects Studio runs only)
 * 2) add `<profileable android:shell="true" />` to your app's manifest, within the `<application>` tag
 *
 * Run this benchmark from Studio to see startup measurements, and captured system traces
 * for investigating your app's performance.
 */
@RunWith(AndroidJUnit4::class)
class ExampleStartupBenchmark {
    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    @Test
    fun startup() = benchmarkRule.measureRepeated(
        packageName = "com.hw.fetch",
        metrics = listOf(StartupTimingMetric(), FrameTimingMetric()),
        iterations = 5,
        startupMode = StartupMode.COLD
    ) {
        pressHome()
        startActivityAndWait()
        device.findObject(By.text("Group by listId")).clickAndWait(Until.newWindow(), 1_000)
        device.findObject(By.text("Filter by name")).clickAndWait(Until.newWindow(), 1_000)
        device.findObject(By.text("Group by listId")).clickAndWait(Until.newWindow(), 1_000)
        device.findObject(By.text("Filter by name")).clickAndWait(Until.newWindow(), 1_000)
        val list = device.wait(Until.findObject(By.res( "homeScreenList")), 1_000)
        if (list != null) {
            list.fling(Direction.DOWN)
            list.fling(Direction.UP)
        } else {
            throw AssertionError("Could not find LazyColumn with testTag 'homeScreenList'")
        }
        device.findObject(By.text("Group by listId")).clickAndWait(Until.newWindow(), 1_000)
        device.findObject(By.res("homeScreenExpandableList")).also {
            it.fling(Direction.DOWN)
            it.fling(Direction.UP)
        }
    }
}