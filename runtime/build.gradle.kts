plugins {
    alias(libs.plugins.kotlin.jvm)
    `maven-publish`
}

group = "dev.zawadzki.analyticseventgenerator.runtime"
version = libs.versions.library.get()

kotlin {
    jvmToolchain(8)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "dev.zawadzki.analyticseventgenerator"
            artifactId = "runtime"

            from(components["java"])
        }
    }
}
