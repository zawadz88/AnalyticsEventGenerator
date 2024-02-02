//
//  ContentView.swift
//  SampleiOSApp
//
//  Created by Zawadzki, Piotr on 02/02/2024.
//

import SwiftUI
import SharedAnalyticsLibrary

struct ContentView: View {
    let event = SampleAdditionalButtonTapped(buttonId: "xxx")
    let eventWithDefaultArgs = SampleButtonTapped(buttonId: "buttonId1", someOptional: nil)
    let greet = Greeting().greet()

    let action: Void = ActionKt.executeAction()

    var body: some View {
        VStack {
            Image(systemName: "globe")
                .imageScale(.large)
                .foregroundStyle(.tint)
            
                Text(greet + "\n\n " + event.description() + "\n\n" + eventWithDefaultArgs.description())
        }
        .padding()
    }
}

#Preview {
    ContentView()
}
