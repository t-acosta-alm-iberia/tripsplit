# TripSplit

An Android and iOS expense-splitting app for a team Kotlin Multiplatform workshop.

## Project structure

- `androidApp`: Android entry point, Jetpack Compose screens, resources, and UI tests. Depends directly on `sharedLogic`.
- `iosApp`: native SwiftUI screens, consuming the `SharedLogic` framework.
- `sharedLogic`: shared Kotlin business logic and data, plus platform implementations where needed.

Each app owns its screens, navigation, and presentation state. There is no shared UI module.
The project folder and app display name are TripSplit; existing application identifiers are retained.

## Next workshop milestones

1. Add shared participant, expense, and balance models.
2. Implement equal splitting using integer cents, with deterministic remainder allocation and shared tests.
3. Display one sample trip and its balances in separate Compose and SwiftUI screens.

Start with one trip, one currency (EUR), and in-memory data.
Later milestones add expense entry, settlement suggestions, local persistence, and native sharing.

## Running the apps

- Android: `./gradlew :androidApp:assembleDebug`
- iOS: open `iosApp/iosApp.xcodeproj` in Xcode and run the `iosApp` scheme.

## Running tests

- Android app unit tests: `./gradlew :androidApp:testDebugUnitTest`
- Shared Android host tests: `./gradlew :sharedLogic:testAndroidHostTest`
- Shared iOS simulator tests: `./gradlew :sharedLogic:iosSimulatorArm64Test`

The starter cleanup is incomplete: `Platform.android.kt` still references removed platform declarations,
and the SwiftUI starter still references `Greeting`. These need resolving before both apps can build.
