## Sample iOS App

For now using locally-built SharedAnalyticsLibrary pod is supported. You need to follow these steps:
- build the library in SharedAnalyticsLibrary with `./gradlew assemble`
- install the pod in SharedAnalyticsLibrary with `./gradlew podInstall`
- install pods in `SampleiOSApp` via `pod install`
- open in XCode via `SampleiOSApp.xcworkspace` so that Cocoapods works as expected
