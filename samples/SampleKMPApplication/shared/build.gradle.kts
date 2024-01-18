import dev.zawadzki.analyticseventgenerator.plugin.AnalyticsExtension
import dev.zawadzki.analyticseventgenerator.plugin.GenerateAnalyticsEventsTask
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.AbstractKotlinCompile
import org.jetbrains.kotlin.gradle.tasks.AbstractKotlinNativeCompile

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    id(libs.plugins.eventGenerator.get().pluginId)
}

val analyticsExtension = the<AnalyticsExtension>().apply {
    prefix.set("Sample")
    packageName.set("dev.zawadzki.samplekmpapplication.event")
    inputFiles.setFrom(projectDir.resolve("src/eventDefinitions").listFiles())
    inputFiles.from(layout.projectDirectory.file("src/additionalEventDefinitions/sample.yaml"))
}

tasks.matching { it is AbstractKotlinCompile<*> || it is AbstractKotlinNativeCompile<*, *> }
    .configureEach { dependsOn(tasks.withType<GenerateAnalyticsEventsTask>()) }

kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = JvmTarget.JVM_1_8.target
            }
        }
    }

    jvm("desktop")
    @Suppress("OPT_IN_USAGE")
    wasmJs {
        moduleName = "shared"
        browser {
            commonWebpackConfig {
                outputFileName = "shared.js"
            }
        }
    }
    js {
        browser()
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "shared"
            isStatic = true
        }
    }

    sourceSets {
        commonMain.configure {
            kotlin.srcDirs(analyticsExtension.outputDirectory)
        }
        commonMain.dependencies {
            implementation(libs.event.runtime)
            implementation(libs.kotlinx.datetime)
            implementation(libs.kotlinx.coroutines)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        jsMain.configure {
            kotlin.srcDirs("src/wasmJsMain/kotlin")
        }
    }
}

android {
    namespace = "dev.zawadzki.samplekmpapplication"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
}
