import SwiftUI
import shared

struct ContentView: View {
	let event = SampleAdditionalButtonTapped(buttonId: "xxx")
	let eventWithDefaultArgs = SampleButtonTapped(buttonId: "buttonId1", someOptional: nil)
	let greet = Greeting().greet()

	let action = ActionKt.executeAction()

	var body: some View {
		Text(greet + "\n\n " + event.description() + "\n\n" + eventWithDefaultArgs.description())
	}
}

struct ContentView_Previews: PreviewProvider {
	static var previews: some View {
		ContentView()
	}
}
