var rootElement = document.getElementById("root");
var greeting = jsBrowserApp.getGreeting();

var eventWithLong = new jsBrowserApp.dev.zawadzki.samplekmpapplication.event.SampleActionWithTimer(BigInt("297153970613387264"));
var eventWithDefaults = new jsBrowserApp.dev.zawadzki.samplekmpapplication.event.SampleButtonTapped("buttonId1", null);
var secondEventWithDefaults = new jsBrowserApp.dev.zawadzki.samplekmpapplication.event.SampleButtonTapped("buttonId1", null, "value for someOptional2");
var eventWithEnum = new jsBrowserApp.dev.zawadzki.samplekmpapplication.event.SampleSomething(true, 12, 12, 1.0, jsBrowserApp.dev.zawadzki.samplekmpapplication.event.SampleSomething.MyType.CUSTOM);
rootElement.innerHTML = "<div>" + jsBrowserApp.getGreeting() + "</div>"
    + "<br/>"
    + "<div> eventWithLong: " + eventWithLong.toString() + "</div>"
    + "<div> eventWithDefaults: " + eventWithDefaults.toString() + "</div>"
    + "<div> secondEventWithDefaults: " + secondEventWithDefaults.toString() + "</div>"
    + "<div> eventWithEnum: " + eventWithEnum.toString() + "</div>";

jsBrowserApp.dev.zawadzki.samplekmpapplication.executeAction();
