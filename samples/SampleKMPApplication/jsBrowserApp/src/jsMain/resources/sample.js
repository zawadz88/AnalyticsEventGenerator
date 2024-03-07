jsBrowserApp.dev.zawadzki.samplekmpapplication.di.KoinHelper.initKoin();
let repository = jsBrowserApp.dev.zawadzki.samplekmpapplication.di.KoinHelper.eventReportingRepository;
document.getElementById("sampleEventButton").onclick = function() {
    let sampleEvent = new jsBrowserApp.dev.zawadzki.samplekmpapplication.event.SampleSomething(true, 12, 12, 1.0, jsBrowserApp.dev.zawadzki.samplekmpapplication.event.SampleSomething.MyType.CUSTOM);
    repository.reportEvent(sampleEvent);
};
document.getElementById("sampleEventWithDurationButton").onclick = function() {
    let eventWithTimer = new jsBrowserApp.dev.zawadzki.samplekmpapplication.event.SampleActionWithTimer(0);
    eventWithTimer.duration = 2500;
    repository.reportEvent(eventWithTimer);
};
