# Analytics Event Generator

This project tries to solve the issue on cross-platform misalignment when it comes to the events/metrics sent to various analytical systems.
It is not common that we e.g. send different attributes on Android than on iOS for example.

It allows to generate Kotlin model classes that can be used for analytics purposes e.g. to send
an event to Google Analytics or other system. The events are generated from YAML configuration files
and are Kotlin data classes with support for Kotlin Multiplatform.

# TODOs

- sample apps
  -- configure native app samples (JS, iOS) to take distributed shared library from private NPM artifactory and Cocoapods/SPM
  -- make the samples with more real world examples - configure a dummy analytics service to send events on button clicks in the samples
- clean up CodeGenerator
- more unit tests for plugin, especially code generation
- add info in README how to run locally
-- for native samples how to setup, update for local dependencies instead of remote, install dependencies etc. 
- GithubActions/CI support
- add an option to provide an allowlist for event/attribute names or an interface for filtering/truncating attributes/values
