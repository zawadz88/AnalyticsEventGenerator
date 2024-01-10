import SwiftUI
import shared

struct ContentView: View {
	let event = SampleAdditionalButtonTapped(buttonId: "xxx")
	let greet = Greeting().greet()

	var body: some View {
		Text(greet + "\n\n " + event.description())
	}
}

struct ContentView_Previews: PreviewProvider {
	static var previews: some View {
		ContentView()
	}
}
