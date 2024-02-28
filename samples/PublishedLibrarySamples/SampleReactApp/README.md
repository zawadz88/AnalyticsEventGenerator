## Sample React App

In order to build the package you need to make sure that you authenticate to Github Packages
in case it's a private
repository: https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-npm-registry#authenticating-with-a-personal-access-token

To set up locally execute `yarn install`.

Then to run locally execute `yarn webpack serve`.

Using local version of shared-analytics-library:

- In SampleReactApp run `yarn remove shared-analytics-library` to remove the one distributed via Github Packages.
- Build the library in SharedAnalyticsLibrary with `./gradlew assemble`.
- Install the local library
  via `yarn add shared-analytics-library@file:./../SharedAnalyticsLibrary/build/dist/js/productionLibrary`.
- In case you make some changes to SharedAnalyticsLibrary, you'd need to repeat the previous step to have the latest
  changes available in SampleReactApp as the packages gets copied to `node_modules`.

Re-adding the version from Github Packages:

- execute `yarn add shared-analytics-library@npm:@zawadz88/shared-analytics-library@1.0.0`
