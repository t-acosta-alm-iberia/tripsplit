# TripSplit

An Android and iOS expense-splitting app

## Project structure

- `androidApp`: Android entry point, Jetpack Compose screens, resources, and UI tests. Depends directly on `sharedLogic`.
- `iosApp`: native SwiftUI screens, consuming the `SharedLogic` framework.
- `sharedLogic`: shared Kotlin business logic and data, plus platform implementations where needed.

Each app owns its screens, navigation, and presentation state.

## Running the apps

- Android: `./gradlew :androidApp:assembleDebug`
- iOS: open `iosApp/iosApp.xcodeproj` in Xcode and run the `iosApp` scheme.

## Running tests

- Android app unit tests: `./gradlew :androidApp:testDebugUnitTest`
- Shared Android host tests: `./gradlew :sharedLogic:testAndroidHostTest`
- Shared iOS simulator tests: `./gradlew :sharedLogic:iosSimulatorArm64Test`
