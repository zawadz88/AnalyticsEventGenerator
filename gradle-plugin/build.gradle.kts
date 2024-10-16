plugins {
    `java-gradle-plugin`
    `maven-publish`
    alias(libs.plugins.kotlin.dsl)
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlinx.serialization)
}

group = "dev.zawadzki.analyticseventgenerator"
version = libs.versions.library.get()

gradlePlugin {
    plugins {
        create("analyticsEventGenerator") {
            id = "dev.zawadzki.analyticseventgenerator"

            implementationClass = "dev.zawadzki.analyticseventgenerator.plugin.AnalyticsPlugin"
        }
    }
}

kotlin {
    jvmToolchain(8)
}

dependencies {
    implementation(libs.kaml)
    implementation(libs.kotlinx.serialization)
    implementation(libs.kotlinpoet)
    implementation(libs.kotlin.stdlib)
    implementation(project(":runtime"))

    testImplementation(libs.junit5)
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
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
