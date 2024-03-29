plugins {
    alias(libs.plugins.kotlin.multiplatform)
    `maven-publish`
}

group = "dev.zawadzki.analyticseventgenerator"
version = libs.versions.library.get()

kotlin {
    jvm()
    jvmToolchain(8)

    iosX64()
    iosArm64()
    iosSimulatorArm64()
    macosArm64()
    macosX64()
    watchosArm32()
    watchosArm64()
    tvosArm64()
    tvosSimulatorArm64()
    tvosX64()

    js(IR) {
        browser()
        nodejs()
        binaries.library()
        generateTypeScriptDefinitions()
    }

    sourceSets {
        all {
            languageSettings.apply {
                optIn("kotlin.js.ExperimentalJsExport")
            }
        }
    }
}

publishing {
    repositories {
        maven {
            name = "github"
            url = uri("https://maven.pkg.github.com/zawadz88/AnalyticsEventGenerator")
            credentials(PasswordCredentials::class)
        }
    }
}
