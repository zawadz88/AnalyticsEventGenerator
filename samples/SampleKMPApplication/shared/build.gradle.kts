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
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

android {
    namespace = "dev.zawadzki.samplekmpapplication"
    compileSdk = 34
    defaultConfig {
        minSdk = 24
    }
}
