import SwiftUI
import shared

struct ContentView: View {
	let event = SampleAdditionalButtonTapped(buttonId: "xxx")
	let sampleButtonTappedEvent = SampleButtonTapped(buttonId: "buttonId1", someOptional: nil)
	let greet = Greeting().greet()

	var body: some View {
		Text(greet + "\n\n " + event.description() + "\n\n" + sampleButtonTappedEvent.description())
	}
}

struct ContentView_Previews: PreviewProvider {
	static var previews: some View {
		ContentView()
	}
}
