subprojects {
    configurations.all {
        resolutionStrategy.eachDependency {
            // force same Kotlin version across Kotlin packages
            if (requested.group == libs.kotlin.stdlib.asProvider().get().group) {
                useVersion(libs.versions.kotlin.get())
            }
        }
    }
}
