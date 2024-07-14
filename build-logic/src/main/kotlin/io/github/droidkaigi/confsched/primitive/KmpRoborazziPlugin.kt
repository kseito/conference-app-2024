package io.github.droidkaigi.confsched.primitive

import io.github.takahirom.roborazzi.RoborazziExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.internal.artifacts.dependencies.DefaultExternalModuleDependency
import org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
import org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED
import org.gradle.api.tasks.testing.logging.TestLogEvent.PASSED
import org.gradle.api.tasks.testing.logging.TestLogEvent.SKIPPED
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.getByType

@Suppress("unused")
class KmpRoborazziPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        with(project) {
            with(pluginManager) {
                apply("io.github.takahirom.roborazzi")
                apply("com.google.devtools.ksp")
            }
            if (plugins.hasPlugin("com.android.library")) {
                android {
                    testOptions {
                        unitTests {
                            all { test ->
                                test.jvmArgs("-noverify")
                                test.systemProperties["robolectric.graphicsMode"] = "NATIVE"
                                test.systemProperties["robolectric.pixelCopyRenderMode"] = "hardware"

                                test.maxParallelForks = Runtime.getRuntime().availableProcessors()
                                test.testLogging {
                                    events.addAll(listOf(PASSED, SKIPPED, FAILED))
                                    showCauses = true
                                    showExceptions = true
                                    exceptionFormat = FULL
                                }
                            }
                        }
                    }
                }
            }
            project.extensions.getByType<RoborazziExtension>().apply {
                generateComposePreviewRobolectricTests {
                    enable.set(true)
                    packages.add("io.github.droidkaigi.confsched")
                }
            }
            kotlin {
                if (plugins.hasPlugin("com.android.library")) {
                    sourceSets.getByName("androidUnitTest") {
                        dependencies {
                            implementation(libs.library("androidxTestEspressoEspressoCore"))
                            implementation(libs.library("junit"))
                            implementation(libs.library("robolectric"))
                            implementation(libs.library("androidxTestExtJunit"))
                            implementation(libs.library("roborazzi"))
                            implementation(libs.library("roborazziCompose"))
                            implementation(libs.library("composablePreviewScanner"))
                            implementation(libs.library("roborazziPreviewScannerSupport"))
                        }
                    }
                }
            }
        }
    }
}
