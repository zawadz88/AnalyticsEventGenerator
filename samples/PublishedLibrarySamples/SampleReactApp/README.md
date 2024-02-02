## Sample React App
In order to build the package you need to make sure that you authenticate to Github Packages 
in case it's a private repository: https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-npm-registry#installing-a-package

Using local version of shared-analytics-library:
- in SampleReactApp run `yarn remove shared-analytics-library` to remove the one distributed via Github Packages
- build the library in SharedAnalyticsLibrary with `./gradlew assemble`
- install the local library via `yarn add shared-analytics-library@file:./../SharedAnalyticsLibrary/build/dist/js/productionLibrary`
- in case you make some changes to SharedAnalyticsLibrary, you'd need to repeat the previous step to have the latest changes available in SampleReactApp as the packages gets copied to `node_modules`
