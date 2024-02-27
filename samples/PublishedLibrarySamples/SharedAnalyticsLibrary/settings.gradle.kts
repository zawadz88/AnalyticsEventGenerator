enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
        //mavenLocal()
        maven {
            setUrl("https://maven.pkg.github.com/zawadz88/AnalyticsEventGenerator")
            name = "github"
            credentials(PasswordCredentials::class)
        }
    }
}
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        //mavenLocal()
        maven {
            setUrl("https://maven.pkg.github.com/zawadz88/AnalyticsEventGenerator")
            name = "github"
            credentials(PasswordCredentials::class)
        }
        maven {
            setUrl("https://maven.pkg.github.com/zawadz88/AnalyticsEventGenerator")
            name = "publishing"
            credentials(PasswordCredentials::class)
        }
    }
}

rootProject.name = "shared-analytics-library"
