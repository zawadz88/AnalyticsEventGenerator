# Analytics Event Generator

This project tries to solve the issue of cross-platform misalignment when it comes to the events/metrics sent to various analytical systems.
So that we always report user/system actions in the same fashion, regardless of the client platform.

It's a Gradle plugin that allows to generate Kotlin model classes that can be used for analytics purposes e.g. to send
an event to Google Analytics or other system. The events are generated from YAML configuration files
and are Kotlin data classes with support for Kotlin Multiplatform.

Supported Kotlin Multiplatform targets are: Android, Apple (iOS, WatchOS, etc.), JVM and JavaScript.

## Samples

You can find a sample project in `/samples` in the repository. It's a Kotlin Multiplatform project with Android, Desktop JVM, iOS and Web JavaScript support.

There's also a set of sample projects that show how to create and publish a shared KMP library using this plugin. 
And then consume in separate Android, iOS and Web JS projects:
- [Shared library](https://github.com/zawadz88/AnalyticsEventGeneratorSample-SharedLibrary) - uses plugin and publishes to Github Packages (NPM & Maven - regular for Android and KMM + KMMBridge for SPM)
- [Android app](https://github.com/zawadz88/AnalyticsEventGeneratorSample-AndroidApp) using Gradle
- [iOS app](https://github.com/zawadz88/AnalyticsEventGeneratorSample-iOSApp) using SPM
- [Web app](https://github.com/zawadz88/AnalyticsEventGeneratorSample-ReactApp) using Yarn, Webpack & react JS

# TODOs

- add Getting Started and setup
- publish action for plugin with Github Actions
- Github Actions for sample projects
- add ktlint & detekt
- make Github repo public
