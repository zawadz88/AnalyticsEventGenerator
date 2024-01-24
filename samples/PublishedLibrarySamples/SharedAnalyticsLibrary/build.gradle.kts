import dev.zawadzki.analyticseventgenerator.plugin.AnalyticsExtension
import dev.zawadzki.analyticseventgenerator.plugin.GenerateAnalyticsEventsTask
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.AbstractKotlinCompile
import org.jetbrains.kotlin.gradle.tasks.AbstractKotlinNativeCompile

group = "dev.zawadzki.sharedanalyticslibrary"
version = libs.versions.sharedAnalyticsLibrary.get()

// TODO: use the one from Maven Central + add info how to develop locally
buildscript {
    dependencies {
        classpath(libs.event.plugin)
    }
}
plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.eventGenerator)
    `maven-publish`
    // TODO: configure publishing for Maven multiplatform, Cocoapods and NPM
}

val analyticsExtension = the<AnalyticsExtension>().apply {
    prefix.set("Sample")
    packageName.set("dev.zawadzki.sharedanalyticslibrary.event")
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
    js {
        browser()
        binaries.library()
        generateTypeScriptDefinitions()
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
    }
}

android {
    namespace = "dev.zawadzki.sharedanalyticslibrary"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
}
