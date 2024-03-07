import dev.zawadzki.analyticseventgenerator.plugin.AnalyticsExtension
import dev.zawadzki.analyticseventgenerator.plugin.GenerateAnalyticsEventsTask
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.AbstractKotlinCompile
import org.jetbrains.kotlin.gradle.tasks.AbstractKotlinNativeCompile

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.ksp)
    id(libs.plugins.eventGenerator.get().pluginId)
}

val analyticsExtension = the<AnalyticsExtension>().apply {
    prefix.set("Sample")
    packageName.set("dev.zawadzki.samplekmpapplication.event")
    inputFiles.setFrom(projectDir.resolve("src/eventDefinitions").listFiles())
    inputFiles.from(layout.projectDirectory.file("src/additionalEventDefinitions/sample.yaml"))
}

tasks.matching {
    it is AbstractKotlinCompile<*> || it is AbstractKotlinNativeCompile<*, *> || it.name.endsWith(
        "SourcesJar",
        ignoreCase = true
    )
}.configureEach {
    dependsOn(tasks.withType<GenerateAnalyticsEventsTask>())
    if (name != "kspCommonMainKotlinMetadata") {
        dependsOn("kspCommonMainKotlinMetadata")
    }
}

kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = JvmTarget.JVM_1_8.target
            }
        }
    }

    jvm()
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
            baseName = "Shared"
            isStatic = true
        }
    }

    applyDefaultHierarchyTemplate()
    sourceSets {
        commonMain.configure {
            kotlin.srcDirs(
                analyticsExtension.outputDirectory,
                "build/generated/ksp/metadata/commonMain/kotlin"
            )
        }
        commonMain.dependencies {
            api(libs.event.runtime)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.koin.annotations)
            implementation(libs.koin.core)
        }
        androidMain.dependencies {
            implementation(libs.ktor.client.okhttp)
        }
        jvmMain.dependencies {
            implementation(libs.ktor.client.okhttp)
        }
        jsMain.dependencies {
            implementation(libs.ktor.client.js)
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        val jsiosMain by creating {
            dependsOn(commonMain.get())
        }
        iosMain.get().dependsOn(jsiosMain)
        jsMain.get().dependsOn(jsiosMain)

        all {
            languageSettings.apply {
                optIn("kotlin.js.ExperimentalJsExport")
            }
        }
    }
}

//region KSP configuration for Koin
dependencies {
    add("kspCommonMainMetadata", libs.koin.compiler)
    // Other compilation targets should be added here, see up in article.
}
ksp {
    arg("KOIN_CONFIG_CHECK", "true")
}
//endregion

android {
    namespace = "dev.zawadzki.samplekmpapplication"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
}
