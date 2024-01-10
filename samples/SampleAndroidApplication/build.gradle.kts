// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.2.1" apply false
    id("org.jetbrains.kotlin.android") version "1.9.21" apply false
}
buildscript {
    dependencies {
        classpath("dev.zawadzki.analyticseventgenerator:gradle-plugin:0.1")
    }
}
