import SwiftUI
import Shared

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
                KoinHelper.shared.eventReportingRepository.reportEvent(event: sampleEvent)
            }
            Button("Send event with duration") {
                let eventWithTimer = SampleActionWithTimer(duration: 0)
                eventWithTimer.duration = 2500
                KoinHelper.shared.eventReportingRepository.reportEvent(event: eventWithTimer)
            }
        }
                .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .top)
                .padding()
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
