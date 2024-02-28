//
//  ContentView.swift
//  SampleiOSApp
//
//  Created by Zawadzki, Piotr on 02/02/2024.
//

import SwiftUI
import SharedAnalyticsLibrary

struct ContentView: View {
    var body: some View {
        VStack(spacing: 16) {
            Button("Send sample event") {
                let sampleEvent = SampleSomething(
                    isEnabled: true,
                    clickCount: 1,
                    duration: 2000,
                    accuracy: 0.5,
                    myType: SampleSomething.MyType.custom
                )
                ServiceLocatorKt.eventReportingRepository.reportEvent(event: sampleEvent)
            }
            Button("Send event with duration") {
                let eventWithTimer = SampleActionWithTimer(duration: 0)
                eventWithTimer.duration = 2500
                ServiceLocatorKt.eventReportingRepository.reportEvent(event: eventWithTimer)
            }
        }
                .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .top)
                .padding()
    }
}

#Preview {
    ContentView()
}
