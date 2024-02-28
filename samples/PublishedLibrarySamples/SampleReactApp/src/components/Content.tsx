import React, {FunctionComponent} from 'react';
import SharedAnalyticsLibrary from 'shared-analytics-library';
import SAL = SharedAnalyticsLibrary.dev.zawadzki.sharedanalyticslibrary;
import Event = SAL.event;

const Content: FunctionComponent = () => {
    const handleSampleEventButtonClick = () => {
        const sampleEvent = new Event.SampleSomething(true, 12, 12, 1.0, Event.SampleSomething.MyType.CUSTOM);
        SAL.di.eventReportingRepository.reportEvent(sampleEvent);
    };
    const handleEventWithTimerButtonClick = () => {
        const eventWithTimer = new Event.SampleActionWithTimer(0);
        eventWithTimer.duration = 2500;
        SAL.di.eventReportingRepository.reportEvent(eventWithTimer);
    };

    return (
        <div>
            <button className="buttonClass" onClick={handleSampleEventButtonClick}>Send sample event</button>
            <button className="buttonClass" onClick={handleEventWithTimerButtonClick}>Send event with duration</button>
        </div>
    )
};
export default Content;
