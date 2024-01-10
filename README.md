# Analytics Event Generator

This project tries to solve the issue on cross-platform misalignment when it comes to the events/metrics sent to various analytical systems.
It is not common that we e.g. send different attributes on Android than on iOS for example.

It allows to generate Kotlin model classes that can be used for analytics purposes e.g. to send
an event to Google Analytics or other system. The events are generated from YAML configuration files
and are Kotlin data classes with support for Kotlin Multiplatform.

# TODOs

- sample apps
  -- cross-platform sample app - Android, iOS, desktop, JS
  -- sample apps for when cross-platform library used 
  -- default attribute support on iOS
  -- configure a dummy analytics service to send events on button clicks
  -- define a duration extension
- more unit tests for plugin
- configure Maven Central/Github Packages to publish Plugin + Runtime for both platforms
- GithubActions/CI support
- add an option to provide an allowlist for event/attribute names

