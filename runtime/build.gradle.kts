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

    js {
        browser()
        nodejs()
    }
}
