plugins {
    //trick: for the same plugin versions in all sub-modules
    alias(libs.plugins.androidApplication).apply(false)
    alias(libs.plugins.androidLibrary).apply(false)
    alias(libs.plugins.kotlinAndroid).apply(false)
    alias(libs.plugins.kotlinMultiplatform).apply(false)
    alias(libs.plugins.jetbrainsCompose) apply false
}
// TODO: also, add a sample how to use the one from Maven Central + how to develop locally
buildscript {
    dependencies {
        classpath(libs.event.plugin)
    }
}
