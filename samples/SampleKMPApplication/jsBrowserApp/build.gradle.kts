plugins {
    alias(libs.plugins.kotlinMultiplatform)
}

kotlin {
    js {
        browser {
            webpackTask {
                output.libraryTarget = "var"
            }
        }
        binaries.executable()
        generateTypeScriptDefinitions()
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.event.runtime)
            implementation(projects.shared)
        }
    }
}
