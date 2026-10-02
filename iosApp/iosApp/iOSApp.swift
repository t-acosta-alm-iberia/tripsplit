import SwiftUI
import SharedLogic

@main
struct iOSApp: App {
    init() {
        KoinInitIosKt.doInitKoinIos()
    }

    var body: some Scene {
        WindowGroup {
            AppRootView()
        }
    }
}
